package com.equifax.ews.instant.productservices.vsibilling.publisherservice.service;

import com.equifax.ews.vs.instant.product.service.domain.billing.BillingRequest;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.beam.sdk.transforms.DoFn;

import java.text.SimpleDateFormat;


public class BillingPublisherService extends DoFn<String, String> {


    @ProcessElement
    public void processElement(ProcessContext ctx) {
        String input = ctx.element();
        String output = input;
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));
            if(input!=null){
                BillingRequest br = mapper.readValue(input, BillingRequest.class);
                br.setStatusBillingService("Output to Topic");
                br.setDekReference("Dek--1846128555964109028");
            }
        } catch (Exception e) {
            System.out.println("Error Occured:" + e.getMessage());
        }
        ctx.output(output);
    }


}
