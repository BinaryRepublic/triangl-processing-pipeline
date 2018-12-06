package com.triangl.processing

import com.google.api.gax.rpc.ApiException
import com.google.api.gax.rpc.StatusCode
import com.google.cloud.pubsub.v1.SubscriptionAdminClient
import com.google.pubsub.v1.ProjectSubscriptionName
import com.google.pubsub.v1.ProjectTopicName
import com.google.pubsub.v1.PushConfig
import com.triangl.processing.controller.ConverterController
import com.triangl.processing.controller.RepositoryController
import com.triangl.processing.dto.InputOperationTypeDto
import com.triangl.processing.dto.OutputOperationDto
import com.triangl.processing.helper.SQLQueryBuilder
import com.triangl.processing.repository.RepositoryConnector
import com.triangl.processing.repository.RepositoryEntity
import com.triangl.processing.repository.RepositoryExecutor
import org.apache.beam.runners.dataflow.options.DataflowPipelineOptions
import org.apache.beam.runners.direct.DirectRunner
import org.apache.beam.sdk.Pipeline
import org.apache.beam.sdk.io.gcp.pubsub.PubsubIO
import org.apache.beam.sdk.io.gcp.pubsub.PubsubMessage
import org.apache.beam.sdk.options.PipelineOptionsFactory
import org.apache.beam.sdk.transforms.DoFn
import org.apache.beam.sdk.transforms.ParDo
import org.apache.beam.sdk.values.PCollection
import java.io.IOException
import java.sql.DriverManager
import java.sql.SQLException

object ProcessingApplication {

    private var repositoryExecutor: RepositoryExecutor? = null

    private fun setupDatabaseConnection() {
        val env = System.getenv()
        try {
            val dbConnection = DriverManager.getConnection(env["JDBC_URL"], env["DB_USER"], env["DB_PASSWORD"])
            val repositoryConnector = RepositoryConnector(dbConnection)
            repositoryExecutor = RepositoryExecutor(repositoryConnector, SQLQueryBuilder())
        } catch (e: SQLException) {
            e.printStackTrace()
        }

    }

    private fun setupPubSubSubscription(projectId: String, pubsubTopic: String, subscriptionName: String): String {
        val topic = ProjectTopicName.of(projectId, pubsubTopic)
        val subscription = ProjectSubscriptionName.of(projectId, subscriptionName)
        try {
            SubscriptionAdminClient.create().use { subscriptionAdminClient -> subscriptionAdminClient.createSubscription(subscription, topic, PushConfig.getDefaultInstance(), 0) }
        } catch (e: ApiException) {
            if (e.statusCode.code == StatusCode.Code.ALREADY_EXISTS) {
                System.out.printf("Subscription %s already exists.\n", subscription.toString())
            } else {
                e.printStackTrace()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return subscription.toString()
    }

    @JvmStatic
    fun main(args: Array<String>) {

        setupDatabaseConnection()

        val env = System.getenv()

        val projectId = env["PROJECT_ID"]
        val pubsubTopic = env["PUBSUB_TOPIC"]
        val subscription = env["PUBSUB_SUBSCRIPTION"]

        if (projectId == null || pubsubTopic == null || subscription == null) {
            throw Error("environment variables must be set: PROJECT_ID, PUBSUB_TOPIC, PUBSUB_SUBSCRIPTION")
        }

        val options = PipelineOptionsFactory.`as`(DataflowPipelineOptions::class.java)
        options.runner = DirectRunner::class.java
        options.project = projectId
        val pubsubSubscriptionName = setupPubSubSubscription(projectId, pubsubTopic, subscription)

        val p = Pipeline.create(options)
        p
                .apply<PCollection<PubsubMessage>>(PubsubIO.readMessagesWithAttributes().fromSubscription(pubsubSubscriptionName))
                .apply("ConstructDatabaseOutputOperations", ParDo.of<PubsubMessage, OutputOperationDto<RepositoryEntity>>(object : DoFn<PubsubMessage, OutputOperationDto<RepositoryEntity>>() {
                    @DoFn.ProcessElement
                    fun processElement(c: ProcessContext) {
                        val message = c.element()

                        val inputOperationType = InputOperationTypeDto.valueOf(message.getAttribute("operation")!!)
                        val jsonPayload = String(message.payload).replace("\n", "")
                        val jsonAdditional = message.getAttribute("additional")

                        val converter = ConverterController()
                        val outputOperation = converter.constructOutputOperations(inputOperationType, jsonPayload, jsonAdditional)

                        @Suppress("UNCHECKED_CAST")
                        c.output(outputOperation as OutputOperationDto<RepositoryEntity>)
                    }
                }))
                .apply("ApplyOutputOperationsToDatabase", ParDo.of<OutputOperationDto<RepositoryEntity>, String>(object : DoFn<OutputOperationDto<RepositoryEntity>, String>() {
                    @ProcessElement
                    fun processElement(c: ProcessContext) {

                        val result = c.element()
                        val repositoryController = RepositoryController(result, repositoryExecutor!!)
                        repositoryController.applyOutputOperations()
                    }
                }))

        // Run the pipeline
        p.run().waitUntilFinish()
    }
}