package com.equifax.ews.instant.productservices.vsibilling.publisherservice.job;

import com.equifax.ews.instant.productservices.vsibilling.publisherservice.service.BillingPublisherService;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.PipelineResult;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubIO;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.ParDo;

public class BillingRecordProcessor {

    /**
     * Main entry point for executing the pipeline.
     *
     * @param args The command-line arguments to the pipeline.
     */
    public static void main(String[] args) {

        // Parse the user options passed from the command-line
        BillingPipelineOptions options = PipelineOptionsFactory.fromArgs(args).withValidation().as(BillingPipelineOptions.class);

        options.setStreaming(true);

        run(options);
    }

    /**
     * Runs the pipeline with the supplied options.
     *
     * @param options The execution parameters to the pipeline.
     * @return The result of the pipeline execution.
     */
    public static PipelineResult run(BillingPipelineOptions options) {
        // Create the pipeline
        Pipeline pipeline = Pipeline.create(options);

        /**
         * Steps:
         *      1) Read PubSubMessage with attributes from input PubSub subscription.
         *      2) Apply any filters if an attribute=value pair is provided.
         *      3) Write each PubSubMessage to output PubSub topic.
         */
        // pipeline
        //     .apply(
        //         "Read PubSub Events",
        //         PubsubIO.readMessagesWithAttributes().fromSubscription(options.getInputSubscription()))
        //     .apply("Write PubSub Events", PubsubIO.writeMessages().to(options.getOutputTopic()));

        /**
         * Steps:
         *      1) Read PubSubMessage as string from input PubSub subscription.
         *      2) Reverse the input string
         *      3) Write reversed string to output PubSub topic.
         */
        pipeline
                .apply(
                        "Read PubSub Events as Strings",
                        PubsubIO.readStrings().fromSubscription(options.getInputSubscription()))
                .apply("Reverse Message", ParDo.of(new BillingPublisherService()))
                .apply("Write PubSub Events as Strings", PubsubIO.writeStrings().to(options.getOutputTopic()));

        // Execute the pipeline and return the result.
        return pipeline.run();
    }
}
