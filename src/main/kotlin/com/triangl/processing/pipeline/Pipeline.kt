package com.triangl.processing.pipeline

import com.triangl.processing.controller.ConverterController
import com.triangl.processing.controller.RepositoryController
import com.triangl.processing.dto.InputOperationTypeDto
import com.triangl.processing.dto.OutputOperationDto
import com.triangl.processing.repository.RepositoryEntity
import com.triangl.processing.repository.RepositoryExecutor
import org.apache.beam.runners.dataflow.options.DataflowPipelineOptions
import org.apache.beam.sdk.Pipeline
import org.apache.beam.sdk.io.gcp.pubsub.PubsubIO
import org.apache.beam.sdk.io.gcp.pubsub.PubsubMessage
import org.apache.beam.sdk.options.PipelineOptionsFactory
import org.apache.beam.sdk.transforms.DoFn
import org.apache.beam.sdk.transforms.ParDo
import org.apache.beam.sdk.values.PCollection

object Pipeline {

    var pipelineOptions = PipelineOptionsFactory.`as`(DataflowPipelineOptions::class.java)!!

    var repositoryExecutor: RepositoryExecutor? = null

    var pubsubSubscriptionName: String? = null

    @JvmStatic
    fun run() {

        val p = Pipeline.create(pipelineOptions)
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