package com.equifax.ews.instant.productservices.vsibilling.publisherservice.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BillingPublisherProperties {


    /** GCP related Properties */
    private String billingGcpKeyRing;
    private String pubsubGcpKeyRing;
    private String gcpBucket;
    private String gcpBucketFileName;
    private String pubsubProjectId;

    /** Datastore related Properties */
    private String datasourceNamespace;
    private String datasourceProjectId;

    /** VSI Billing Event related Properties */
    private String specVersion;
    private String tribeName;
    private String eventDomain;
    private String clientTransactionId;
    private String eventType;
    private String eventSource;
    private String eventGenerator;
    private String eventTrigger;
    private String eventName;
}
