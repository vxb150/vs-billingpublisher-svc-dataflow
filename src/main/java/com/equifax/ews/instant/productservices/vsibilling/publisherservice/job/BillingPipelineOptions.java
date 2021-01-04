package com.equifax.ews.instant.productservices.vsibilling.publisherservice.job;

import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.StreamingOptions;
import org.apache.beam.sdk.options.Validation;

/**
 * Options supported by {@link BillingRecordProcessor}.
 *
 * <p>Inherits standard configuration options.
 */
public interface BillingPipelineOptions extends PipelineOptions, StreamingOptions {
  @Description(
      "The Cloud Pub/Sub subscription to consume from. "
          + "The name should be in the format of "
          + "projects/<project-id>/subscriptions/<subscription-name>.")
  @Validation.Required
  String getInputSubscription();

  void setInputSubscription(String inputSubscription);

  @Description(
      "The Cloud Pub/Sub topic to publish to. "
          + "The name should be in the format of "
          + "projects/<project-id>/topics/<topic-name>.")
  @Validation.Required
  String getOutputTopic();

  void setOutputTopic(String outputTopic);
}