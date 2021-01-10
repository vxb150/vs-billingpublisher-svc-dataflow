package com.equifax.ews.instant.productservices.vsibilling.publisherservice.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@JsonInclude
@Getter
@Setter
public class PubSubEvent {

    @JsonProperty("specVersion")
    private String specVersion;
    @JsonProperty("businessUnit")
    private String businessUnit;
    @JsonProperty("tribeName")
    private String tribeName;
    @JsonProperty("domain")
    private String domain;
    @JsonProperty("subDomain")
    private String subDomain;
    @JsonProperty("appId")
    private String appId;
    @JsonProperty("appName")
    private String appName;
    @JsonProperty("publisherId")
    private String publisherId;
    @JsonProperty("appTraceId")
    private String appTraceId;
    @JsonProperty("clientTransactionId")
    private String clientTransactionId;
    @JsonProperty("correlationId")
    private String correlationId;
    @JsonProperty("eventType")
    private String eventType;
    @JsonProperty("eventSource")
    private String eventSource;
    @JsonProperty("eventGenerator")
    private String eventGenerator;
    @JsonProperty("eventTrigger")
    private String eventTrigger;
    @JsonProperty("eventName")
    private String eventName;
    @JsonProperty("dekKeyPath")
    private String dekKeyPath;
    @JsonProperty("eventTime")
    private String eventTime;
    @JsonProperty("encryptionMetadata")
    private String encryptionMetadata;
    @JsonProperty("encryptionIndicator")
    private Boolean encryptionIndicator;
    @JsonProperty("eventPayload")
    private String eventPayload;

    public PubSubEvent() {
    }
}
