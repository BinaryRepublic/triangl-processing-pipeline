package com.triangl.processing;

import com.triangl.processing.converter.MainConverter;
import com.triangl.processing.dto.InputOperationTypeDto;
import com.triangl.processing.dto.OutputOperationDto;
import org.apache.beam.runners.dataflow.options.DataflowPipelineOptions;
import org.apache.beam.runners.direct.DirectRunner;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubIO;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubMessage;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;


// source https://gist.github.com/maciekrb/9c73cb94a258e177e023dba9049dda13

public class Processing {

    public static void main(String[] args) {
        DataflowPipelineOptions options = PipelineOptionsFactory.as(DataflowPipelineOptions.class);
        options.setProject("triangl-215714");
        options.setRunner(DirectRunner.class);
        options.setStreaming(true);

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

                        MainConverter converter = new MainConverter();
                        OutputOperationDto<?> outputOperation = converter.constructOutputOperations(inputOperationType, jsonPayload);

                        c.output(outputOperation);
                    }
                }))
                .apply("ApplyOutputOperationsToDatabase", ParDo.of(new DoFn<OutputOperationDto, String>() {
                    @ProcessElement
                    public void processElement(ProcessContext c) {

                        ObjectMapper mapper = new ObjectMapper();

                        OutputOperationDto result = c.element();
                        try {
                            System.out.printf(mapper.writeValueAsString(result));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }));

        // Run the pipeline
        p.run().waitUntilFinish();
    }
}
