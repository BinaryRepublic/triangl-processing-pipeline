package com.triangl.processing.pipeline

import com.google.api.gax.rpc.ApiException
import com.google.api.gax.rpc.StatusCode
import com.google.cloud.pubsub.v1.SubscriptionAdminClient
import com.google.pubsub.v1.ProjectSubscriptionName
import com.google.pubsub.v1.ProjectTopicName
import com.google.pubsub.v1.PushConfig
import com.triangl.processing.helper.SQLQueryBuilder
import com.triangl.processing.repository.RepositoryConnector
import com.triangl.processing.repository.RepositoryExecutor
import org.apache.beam.sdk.PipelineRunner
import java.io.IOException
import java.sql.DriverManager
import java.sql.SQLException

class PipelineBuilder(init: PipelineBuilder.() -> Unit) {

    private var googleCloudConfig: GoogleCloudConfig? = null

    private var databaseConfig: DatabaseConfig? = null

    private var apacheBeamConfig: ApacheBeamConfig? = null

    fun googleCloud(init: GoogleCloudConfig.() -> Unit) {
        googleCloudConfig = GoogleCloudConfig().apply { init() }
    }

    fun database(init: DatabaseConfig.() -> Unit) {
        databaseConfig = DatabaseConfig().apply { init() }
    }

    fun apacheBeam(init: ApacheBeamConfig.() -> Unit) {
        apacheBeamConfig = ApacheBeamConfig().apply { init() }
    }

    fun build(): Pipeline {
        val pipeline = Pipeline

        googleCloudConfig!!.apply {
            pipeline.pubsubSubscriptionName = setupPubSubSubscription(projectId, pubsubTopic, pubsubSubscription)
        }

        databaseConfig!!.apply {
            pipeline.repositoryExecutor = setupDatabaseConnection(jdbcUrl, dbUser, dbPassword)!!
        }

        apacheBeamConfig!!.apply {
            pipeline.pipelineOptions.runner = runner
            pipeline.pipelineOptions.project = googleCloudConfig!!.projectId
        }

        return pipeline
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

    private fun setupDatabaseConnection(jdbcUrl: String, dbUser: String, dbPassword: String): RepositoryExecutor? {
        return try {
            val dbConnection = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword)
            val repositoryConnector = RepositoryConnector(dbConnection)
            RepositoryExecutor(repositoryConnector, SQLQueryBuilder())
        } catch (e: SQLException) {
            e.printStackTrace()
            null
        }
    }

    init {
        init()
    }
}

class GoogleCloudConfig {

    lateinit var projectId: String

    lateinit var pubsubTopic: String

    lateinit var pubsubSubscription: String
}

class DatabaseConfig {

    lateinit var jdbcUrl: String

    lateinit var dbUser: String

    lateinit var dbPassword: String
}

class ApacheBeamConfig {

    lateinit var runner: Class<out PipelineRunner<*>>
}
