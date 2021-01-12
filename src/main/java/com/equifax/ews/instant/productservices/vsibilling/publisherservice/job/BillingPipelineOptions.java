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

  /** GCP related */
  @Description("The Billing Google Resource ID Key ring")
  @Validation.Required
  String getBillingGcpKeyRing();

  void setBillingGcpKeyRing(String billingGcpKeyRing);

  @Description("The PubSub Key Ring for Public Key")
  @Validation.Required
  String getPubsubGcpKeyRing();

  void setPubsubGcpKeyRing(String pubsubGcpKeyRing);

  @Description("The Storage Bucket Name")
  @Validation.Required
  String getGcpBucket();

  void setGcpBucket(String gcpBucket);

  @Description("The Storage Bucket file path Name")
  @Validation.Required
  String getGcpBucketFileName();

  void setGcpBucketFileName(String gcpBucketFileName);

  @Description("The DE Pubsub Project ID")
  @Validation.Required
  String getPubsubProjectId();

  void setPubsubProjectId(String pubsubProjectId);

  /** Datastore related */

  @Description("The Datasource Namespace")
  @Validation.Required
  String getDatasourceNamespace();

  void setDatasourceNamespace(String datasourceNamespace);

  @Description("The Datasource Project ID")
  @Validation.Required
  String getDatasourceProjectId();

  void setDatasourceProjectId(String datasourceProjectId);

  /** Vsi Billing Event related */

  @Description("The PubSub Billing Event SpecVersion")
  @Validation.Required
  String getSpecVersion();

  void setSpecVersion(String specVersion);

  @Description("The PubSub Billing Event tribeName")
  @Validation.Required
  String getTribeName();

  void setTribeName(String tribeName);

  @Description("The PubSub Billing Event Domain")
  @Validation.Required
  String getEventDomain();

  void setEventDomain(String eventDomain);

  @Description("The PubSub Billing Event clientTransactionId")
  @Validation.Required
  String getClientTransactionId();

  void setClientTransactionId(String clientTransactionId);

  @Description("The PubSub Billing Event eventType")
  @Validation.Required
  String getEventType();

  void setEventType(String eventType);

  @Description("The PubSub Billing Event eventSource")
  @Validation.Required
  String getEventSource();

  void setEventSource(String eventSource);

  @Description("The PubSub Billing Event eventGenerator")
  @Validation.Required
  String getEventGenerator();

  void setEventGenerator(String eventGenerator);

  @Description("The PubSub Billing Event eventTrigger")
  @Validation.Required
  String getEventTrigger();

  void setEventTrigger(String eventTrigger);

  @Description("The PubSub Billing Event eventName")
  @Validation.Required
  String getEventName();

  void setEventName(String eventName);


}