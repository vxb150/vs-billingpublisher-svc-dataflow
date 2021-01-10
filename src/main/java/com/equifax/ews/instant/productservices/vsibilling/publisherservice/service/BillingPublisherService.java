package com.equifax.ews.instant.productservices.vsibilling.publisherservice.service;

import com.equifax.core.barricade.cryptography.impl.BasicCryptographyManager;
import com.equifax.ews.instant.productservices.vsibilling.publisherservice.config.BarricadeUtilConfig;
import com.equifax.ews.instant.productservices.vsibilling.publisherservice.domain.EncryptResponse;
import com.equifax.ews.instant.productservices.vsibilling.publisherservice.domain.PubSubEncryptedData;
import com.equifax.ews.instant.productservices.vsibilling.publisherservice.domain.PubSubEvent;
import com.equifax.ews.instant.productservices.vsibilling.publisherservice.exception.BarricadeException;
import com.equifax.ews.instant.productservices.vsibilling.publisherservice.job.BillingPipelineOptions;
import com.equifax.ews.instant.productservices.vsibilling.publisherservice.util.CommonUtil;
import com.equifax.ews.vs.instant.product.service.domain.billing.BillingRequest;
import com.equifax.ews.vs.instant.product.service.domain.billing.EventPayload;
import org.apache.beam.sdk.transforms.DoFn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static com.equifax.ews.instant.productservices.vsibilling.publisherservice.constants.BillingPublisherConstants.DEMO_EMPLOYER_TRANS;
import static com.equifax.ews.instant.productservices.vsibilling.publisherservice.constants.BillingPublisherConstants.DEMO_VERIFIER_TRANS;

public class BillingPublisherService extends DoFn<String, String> {

    /** The log to output status messages to. */
    private static final Logger logger = LoggerFactory.getLogger(BarricadeUtilConfig.class);
    /*private BillingPipelineOptions options;

    public BillingPublisherService (BillingPipelineOptions options) {
        this.options = options;
    }*/

    @ProcessElement
    public void processElement(ProcessContext ctx) {
//        logger.info("BillingPublisherService.processElement.Started Process:" + options.getInputSubscription());
        String input = ctx.element();
        logger.info("BillingPublisherService.processElement.input:" + input);
        String output = null;
        try {
            if(input!=null){
                BillingRequest billingRequest = CommonUtil.stringJsonToObject(input, BillingRequest.class);
                if (billingRequest != null && billingRequest.getEventPayload() != null) {
                    EventPayload eventPayload = billingRequest.getEventPayload();
                    logger.info("BillingPublisherService.processElement.billingRequest:" + billingRequest);
                    if (!isDemoTransaction(eventPayload.getBillingMethodId())) {
                        /*
                            1. Decrypt the PII
                            2. Encrypt the Payload and set to PubSubEncrypted Data
                            3. Create the PubSubEncryptedData object
                            4. Create Billing event

                        */
                        BasicCryptographyManager basicCryptographyManager = BarricadeUtilConfig.getBasicCryptographyManager();
                        decryptData(billingRequest, basicCryptographyManager);
                        //TODO: Delete this log as it consists of PII
                        logger.info("BillingPublisherService.processElement.billingRequest:" + billingRequest);
                        String billingRequestJson = CommonUtil.objectToJson(billingRequest);
                        //Encrypt the Payload
                        PubSubEncryptionService pubSubEncryptionService = new PubSubEncryptionService();
                        PubSubEncryptedData pubSubEncryptedData = pubSubEncryptionService.encrypt(
                                billingRequestJson.getBytes(StandardCharsets.UTF_8), basicCryptographyManager);
                        logger.info("BillingPublisherService.processElement.pubSubEncryptedData:" + pubSubEncryptedData);
                        BillingEventBuilderService billingEventBuilderService = new BillingEventBuilderService();
                        PubSubEvent pubSubEvent = billingEventBuilderService.getPubSubEvent(billingRequest, pubSubEncryptedData);
                        output = CommonUtil.objectToJson(pubSubEvent);

                    }
                }
            }
        } catch (BarricadeException e) {
            logger.error("BillingPublisherService.processElement: BarricadeException Occured:" + e);
        } catch (Exception e) {
            logger.error("BillingPublisherService.processElement: Exception Occured:" + e);
        }
        ctx.output(output);
        logger.info("BillingPublisherService.processElement.End Process");
    }

    private void decryptData (BillingRequest billingRequest, BasicCryptographyManager basicCryptographyManager) {
        logger.info("BillingPublisherService.decryptData: Start");
        String dekReference = billingRequest.getDekReference();
        if (billingRequest.getEventPayload() != null) {
            billingRequest.getEventPayload()
                    .setSsn(getDecryptText(
                            billingRequest.getEventPayload().getSsn(), dekReference, basicCryptographyManager));
            if (billingRequest.getEventPayload().getBillingEmployers() != null
                    && billingRequest.getEventPayload().getBillingEmployers().size() > 0) {
                billingRequest.getEventPayload().getBillingEmployers().forEach(billingEmployer ->
                        {
                            billingEmployer.setFirstName(
                                    getDecryptText(billingEmployer.getFirstName(), dekReference, basicCryptographyManager));
                            billingEmployer.setLastName(
                                    getDecryptText(billingEmployer.getLastName(), dekReference, basicCryptographyManager));
                        }
                );
            }
        }
        logger.info("BillingPublisherService.decryptData: End");
    }

    public String getDecryptText(String text, String dekRef, BasicCryptographyManager basicCryptographyManager) {
        logger.info("BillingPublisherService.getDecryptText: Start");
        logger.info("BillingPublisherService.getDecryptText: text:" + text);
        logger.info("BillingPublisherService.getDecryptText: dekRef:" + dekRef);
        BarricadeService barricadeService = new BarricadeService();
        if (text != null) {
            EncryptResponse response = barricadeService.decrypt(text, dekRef, basicCryptographyManager);
            if (response != null) {
                text = response.getValue();
            }
        }
        logger.info("BillingPublisherService.getDecryptText: End:" + text);
        return text;
    }

    private boolean isDemoTransaction (String billingMethod) {
        if (billingMethod != null) {
            if (billingMethod.equals(DEMO_VERIFIER_TRANS)
                || billingMethod.equals(DEMO_EMPLOYER_TRANS)) {
                return true;
            }
        }
        return false;
    }
}
