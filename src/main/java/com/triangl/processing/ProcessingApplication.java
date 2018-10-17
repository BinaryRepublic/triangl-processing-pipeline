package com.triangl.processing;

import com.triangl.processing.controller.ConverterController;
import com.triangl.processing.controller.RepositoryController;
import com.triangl.processing.dto.InputOperationTypeDto;
import com.triangl.processing.dto.OutputOperationDto;
import com.triangl.processing.repository.RepositoryConnector;
import org.apache.beam.runners.dataflow.options.DataflowPipelineOptions;
import org.apache.beam.runners.direct.DirectRunner;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubIO;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubMessage;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;


// based on pubsub example: https://gist.github.com/maciekrb/9c73cb94a258e177e023dba9049dda13

public class ProcessingApplication {

    public static void main(String [] args) {

        DataflowPipelineOptions options = PipelineOptionsFactory.as(DataflowPipelineOptions.class);
        options.setProject("triangl-215714");
        options.setRunner(DirectRunner.class); // for Dataflow use DataflowRunner.class
        options.setRegion("europe-west1");
        options.setDiskSizeGb(10);
        options.setWorkerMachineType("n1-standard-1");

        String TOPIC_NAME = "projects/triangl-215714/topics/test";

        Pipeline p = Pipeline.create(options);
        p
                .apply(PubsubIO.readMessagesWithAttributes().fromTopic(TOPIC_NAME))
                .apply("ConstructDatabaseOutputOperations", ParDo.of(new DoFn<PubsubMessage, OutputOperationDto>() {
                    @DoFn.ProcessElement
                    public void processElement(ProcessContext c) {
                        PubsubMessage message = c.element();

                        String inputOperationTypeString = message.getAttribute("operation");
                        InputOperationTypeDto inputOperationType = InputOperationTypeDto.valueOf(inputOperationTypeString);
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

                        RepositoryController repository = new RepositoryController(result, new RepositoryConnector());
                        repository.applyOutputOperations();
                    }
                }));

        // Run the pipeline
        p.run().waitUntilFinish();
    }
}
