package com.triangl.processing

import com.triangl.processing.pipeline.PipelineBuilder
import org.apache.beam.runners.direct.DirectRunner

object ProcessingApplication {

    @JvmStatic
    fun main(args: Array<String>) {

        val env = System.getenv()

        val pipelineBuilder = PipelineBuilder{
            googleCloud {
                projectId = env["PROJECT_ID"]!!
                pubsubTopic = env["PUBSUB_TOPIC"]!!
                pubsubSubscription = env["PUBSUB_SUBSCRIPTION"]!!
            }
            database {
                jdbcUrl = env["JDBC_URL"]!!
                dbUser = env["DB_USER"]!!
                dbPassword = env["DB_PASSWORD"]!!
            }
            apacheBeam {
                runner = DirectRunner::class.java
            }
        }

        val pipeline = pipelineBuilder.build()

        pipeline.run()
    }
}