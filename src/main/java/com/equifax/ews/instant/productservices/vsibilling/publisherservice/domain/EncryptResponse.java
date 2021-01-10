package com.equifax.ews.instant.productservices.vsibilling.publisherservice.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EncryptResponse {
    private String value;
    private String dekReference;
}
