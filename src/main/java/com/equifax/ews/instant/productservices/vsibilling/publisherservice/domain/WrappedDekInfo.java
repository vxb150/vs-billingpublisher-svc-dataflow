package com.equifax.ews.instant.productservices.vsibilling.publisherservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WrappedDekInfo {
    private String dekReference;
    private String wrappedKeyByteString;
    private String kmsURL;
    private String kmsKeyVersion;
    private String encMetaDataByteString;
    private long counter;
    private String status;
}
