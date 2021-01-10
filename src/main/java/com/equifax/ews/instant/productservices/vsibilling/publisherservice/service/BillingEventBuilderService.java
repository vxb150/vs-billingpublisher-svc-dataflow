package com.equifax.ews.instant.productservices.vsibilling.publisherservice.service;

import com.equifax.ews.de.event.VsBillingEvent;
import com.equifax.ews.de.event.VsLegacyBillingEvent;
import com.equifax.ews.de.event.domain.vsbillingevent.BillingEvent;
import com.equifax.ews.de.event.domain.vsbillingevent.Employee;
import com.equifax.ews.de.event.domain.vsbillingevent.Employer;
import com.equifax.ews.de.event.domain.vsbillingevent.Lender;
import com.equifax.ews.de.event.domain.vsbillingevent.Name;
import com.equifax.ews.de.event.domain.vsbillingevent.Product;
import com.equifax.ews.de.event.domain.vsbillingevent.Reseller;
import com.equifax.ews.de.event.domain.vsbillingevent.SupplierPartner;
import com.equifax.ews.de.event.domain.vsbillingevent.Transaction;
import com.equifax.ews.de.event.domain.vsbillingevent.Verifier;
import com.equifax.ews.de.event.domain.vslegacybillingevent.Record;
import com.equifax.ews.instant.productservices.vsibilling.publisherservice.domain.PubSubEncryptedData;
import com.equifax.ews.instant.productservices.vsibilling.publisherservice.domain.PubSubEvent;
import com.equifax.ews.instant.productservices.vsibilling.publisherservice.util.CommonUtil;
import com.equifax.ews.vs.instant.product.service.domain.billing.BillingEmployer;
import com.equifax.ews.vs.instant.product.service.domain.billing.BillingRequest;
import com.equifax.ews.vs.instant.product.service.domain.billing.EventPayload;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * This class is used to build the PubSubMessage Object to be published to PubSub
 * Builds the Billing Event Payload Object
 * Builds the Envelope Object (PubSubEvent) that is wrapped around the Payload
 */
public class BillingEventBuilderService {

    private static final Logger logger = LoggerFactory.getLogger(BillingEventBuilderService.class);

    /*public BillingEventBuilderService(BillingPublisherProperties billingPublisherProperties) {
        this.billingPublisherProperties = billingPublisherProperties;
    }*/


    /**
     * Builds the Pubsub Event Object
     * Contains the Payload Object
     * Contains the Product that is sending the Billing event
     * All the Event Envelop information
     * The DekKeypath stored in the GCP Bucket that is used to encrypt the Payload
     * @param billingRequest
     * @param pubSubEncryptedData
     * @return PubSubEvent
     */
   public PubSubEvent getPubSubEvent (BillingRequest billingRequest, PubSubEncryptedData pubSubEncryptedData) {
        logger.info("BillingEventBuilderService.getPubSubEvent.start");
        String uniqueId = UUID.randomUUID().toString();
        PubSubEvent pubSubEvent = new PubSubEvent();
        pubSubEvent.setSpecVersion("v1.0");
        pubSubEvent.setBusinessUnit(billingRequest.getAppName());
        pubSubEvent.setTribeName("Instant & Government");
        pubSubEvent.setDomain("VERIFICATION_SERVICES");
        pubSubEvent.setSubDomain(billingRequest.getProduct());
        pubSubEvent.setAppId(billingRequest.getAppId());
        pubSubEvent.setAppName(billingRequest.getAppName());
        pubSubEvent.setPublisherId(uniqueId);
        pubSubEvent.setAppTraceId(uniqueId);
        pubSubEvent.setClientTransactionId("N/A");
        pubSubEvent.setCorrelationId(billingRequest.getCorrelationId());
        pubSubEvent.setEventType("BUSINESS_EVENT");
        pubSubEvent.setEventSource("API");
        pubSubEvent.setEventGenerator("N/A");
        pubSubEvent.setEventTrigger("N/A");
        pubSubEvent.setEventName("VS_BILLING_EVENT");
        pubSubEvent.setEventTime(CommonUtil.getCurrentUTCDateString());
        pubSubEvent.setDekKeyPath(pubSubEncryptedData.getGcsBucketPath());
        pubSubEvent.setEncryptionMetadata(pubSubEncryptedData.getEncryptionMetadata());
        pubSubEvent.setEncryptionIndicator(true);
        pubSubEvent.setEventPayload(pubSubEncryptedData.getEncryptedEventPayload());
        logger.info("BillingEventBuilderService.getPubSubEvent.End");
        return pubSubEvent;
    }

    /**
     * Builds the Event Payload that needs to be Published to Pubsub
     * This information is needed to create the billing for the client
     * @param billingRequest
     * @return
     */
    public VsBillingEvent buildBillingEvent(BillingRequest billingRequest) {
        logger.info("BillingEventBuilderService.buildBillingEvent.start");
        VsBillingEvent vsBillingEvent = new VsBillingEvent();
        BillingEvent billingEvent = new BillingEvent();
        logger.info("EventPayload():" + billingRequest.getEventPayload());

        billingEvent.setMasterReferenceNumber(UUID.randomUUID());
        if (billingRequest.getEventPayload() != null) {
            EventPayload eventPayload = billingRequest.getEventPayload();
            if (eventPayload.getTransactionMasterReference()!= null) {
                billingEvent.setMasterReferenceTraceNumber(eventPayload.getTransactionMasterReference());
            }
            if (eventPayload.getProductCodes() != null && eventPayload.getProductCodes().size() > 0) {
                eventPayload.getProductCodes().forEach(productCode ->
                {
                    if (isValidCDMProduct(productCode)) {
                        billingEvent.addTransactionItem(
                                getTransaction(billingRequest,null, productCode));
                    }
                });
            }
            if (eventPayload.getBillingEmployers() != null && eventPayload.getBillingEmployers().size() > 0) {
                eventPayload.getBillingEmployers().forEach(billingEmployer ->
                {
                    if (isValidCDMProduct(billingEmployer.getProductCode())) {
                        billingEvent.addTransactionItem(getTransaction(billingRequest, billingEmployer,null));
                    }
                });
            }
        }

        vsBillingEvent.setBillingEvent(billingEvent);
        logger.info("BillingEventBuilderService.buildBillingEvent.End");
        return vsBillingEvent;
    }

    private boolean isValidCDMProduct (String productCode) {
        if (productCode == null)
            return false;
        if (productCode.endsWith("SSN-F"))
            return true;
        if (productCode.endsWith("-F"))
            return false;
        return true;
    }

    private Transaction getTransaction (BillingRequest billingRequest,
                                        BillingEmployer billingEmployer,
                                        String productCode) {
        logger.info("BillingEventBuilderService.getTransaction.start");
        EventPayload eventPayload = billingRequest.getEventPayload();
        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID());
        transaction.setStartDateTime(new Date());
        transaction.setEndDateTime(new Date());
        transaction.setDataSource(billingRequest.getDatasource());
        transaction.setDeliveryChannel(billingRequest.getDeliveryChannel());
        if (billingEmployer != null && billingEmployer.getTransactionReference() != null) {
            transaction.setReferenceNumber(billingEmployer.getTransactionReference());
        }
        if (productCode != null)
        {
            transaction.setProduct(getProduct(productCode, billingRequest.getProduct()));
        }
        if (billingEmployer != null) {
            transaction.setProduct(getProduct(billingEmployer.getProductCode(), billingRequest.getProduct()));
            transaction.setEmployer(getEmployer(billingEmployer));
        }
        transaction.setEmployee(getEmployee(eventPayload, billingEmployer));
        transaction.setSupplierPartner(getSupplierPartner(eventPayload));
        transaction.setLender(getLender(eventPayload));
        transaction.setVerifier(getVerifier(eventPayload));
        transaction.setReseller(getReseller(eventPayload));
        transaction.setUserId(eventPayload.getUserId());
        transaction.setLoanNumber(eventPayload.getTransactionTrackingNumber());
//        LOGGER.debug(Map.of(
//                LOG4J2_LOGMESSAGE, "getTransaction Ended"));
        return transaction;
    }

    private Product getProduct(String code, String fulfilName) {
        Product product = new Product();
        product.setCode(code);
        product.setQuantity(1);
        product.setFulfillmentSystemName(fulfilName);
        return product;
    }

    private SupplierPartner getSupplierPartner(EventPayload eventPayload) {
        SupplierPartner partner = new SupplierPartner();
        partner.setId(eventPayload.getPartnerId());
        return partner;
    }

    private Employer getEmployer (BillingEmployer billingEmployer) {
        Employer employer = new Employer();
        employer.setId(billingEmployer.getCompanyCode());
        employer.setName(billingEmployer.getCompanyName());
        employer.setDivisionId(billingEmployer.getDivisionCode());
        employer.setDivisionName(billingEmployer.getDivisionName());
        return employer;
    }

    private Lender getLender (EventPayload eventPayload) {
        Lender lender = new Lender();
        lender.setId(eventPayload.getLenderId());
        lender.setName(eventPayload.getLenderName());
        return lender;
    }

    private Verifier getVerifier (EventPayload eventPayload) {
        Verifier verifier = new Verifier();
        verifier.setId(eventPayload.getVerifierId());
        verifier.setName(eventPayload.getVerifierName());
        return verifier;
    }
    private Employee getEmployee (EventPayload eventPayload, BillingEmployer billingEmployer) {
        Employee employee = new Employee();
        employee.setSsn(eventPayload.getSsn());
        if (billingEmployer != null) {
            employee.setStatus(billingEmployer.getEmployeeStatus());
            Name name = new Name();
            name.setFirstName(billingEmployer.getFirstName());
            name.setLastName(billingEmployer.getLastName());
            employee.setName(name);
        }
        return employee;
    }
    private Reseller getReseller(EventPayload eventPayload) {
        Reseller reseller = new Reseller();
        reseller.setCustomerId(eventPayload.getResellerId());
        reseller.setCustomerName(eventPayload.getResellerName());
        return reseller;
    }
}
