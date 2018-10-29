package com.triangl.processing;

import com.triangl.processing.controller.ConverterController;
import com.triangl.processing.controller.RepositoryController;
import com.triangl.processing.dto.InputOperationTypeDto;
import com.triangl.processing.dto.OutputOperationDto;
import com.triangl.processing.helper.SQLQueryBuilder;
import com.triangl.processing.repository.RepositoryConnector;
import com.triangl.processing.repository.RepositoryExecutor;
import org.apache.beam.runners.dataflow.options.DataflowPipelineOptions;
import org.apache.beam.runners.direct.DirectRunner;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubIO;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubMessage;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Map;


// based on pubsub example: https://gist.github.com/maciekrb/9c73cb94a258e177e023dba9049dda13

public class ProcessingApplication {

    private static RepositoryExecutor repositoryExecutor;

    private static void setupDatabaseConnection() {
        Map<String, String> env = System.getenv();
        try {
            Connection dbConnection = DriverManager.getConnection(env.get("JDBC_URL"), env.get("DB_USER"), env.get("DB_PASSWORD"));
            RepositoryConnector repositoryConnector = new RepositoryConnector(dbConnection);
            setRepositoryExecutor(new RepositoryExecutor(repositoryConnector, new SQLQueryBuilder()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String [] args) {

        setupDatabaseConnection();

        Map<String, String> env = System.getenv();

        String projectId = env.get("PROJECT_ID");
        String pubsubTopic = env.get("PUBSUB_TOPIC");

        DataflowPipelineOptions options = PipelineOptionsFactory.as(DataflowPipelineOptions.class);
        options.setRunner(DirectRunner.class);
        options.setProject(projectId);

        String TOPIC_NAME = MessageFormat.format("projects/{0}/topics/{1}", projectId, pubsubTopic);

        Pipeline p = Pipeline.create(options);
        p
            .apply(PubsubIO.readMessagesWithAttributes().fromTopic(TOPIC_NAME))
            .apply("ConstructDatabaseOutputOperations", ParDo.of(new DoFn<PubsubMessage, OutputOperationDto>() {
                @DoFn.ProcessElement
                public void processElement(ProcessContext c) {
                PubsubMessage message = c.element();

                InputOperationTypeDto inputOperationType = InputOperationTypeDto.valueOf(message.getAttribute("operation"));
                String jsonPayload = new String(message.getPayload()).replace("\n", "");
                String jsonAdditional = message.getAttribute("additional");

                ConverterController converter = new ConverterController();
                OutputOperationDto<?> outputOperation = converter.constructOutputOperations(inputOperationType, jsonPayload, jsonAdditional);

                c.output(outputOperation);
                }
            }))
            .apply("ApplyOutputOperationsToDatabase", ParDo.of(new DoFn<OutputOperationDto, String>() {
                @ProcessElement
                public void processElement(ProcessContext c) {

                OutputOperationDto result = c.element();
                RepositoryController repositoryController = new RepositoryController(result, getRepositoryExecutor());
                repositoryController.applyOutputOperations();
                }
            }));

        // Run the pipeline
        p.run().waitUntilFinish();
    }

    private static RepositoryExecutor getRepositoryExecutor() {
        return repositoryExecutor;
    }

    private static void setRepositoryExecutor(RepositoryExecutor value) {
        repositoryExecutor = value;
    }
}
