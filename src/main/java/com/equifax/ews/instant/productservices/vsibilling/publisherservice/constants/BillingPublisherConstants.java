package com.equifax.ews.instant.productservices.vsibilling.publisherservice.constants;

import java.util.Set;

public class BillingPublisherConstants {

    public static final String DEK_RECORD_KIND = "DEKRecords";
    public static final String DEK_RECORD_FIELD_STATUS = "status";
    public static final String DEK_RECORD_FIELD_DEKREF = "dekReference";
    public static final String DEK_RECORD_FIELD_COUNTER = "counter";
    public static final String DEK_RECORD_FIELD_ENCMETADATA = "encMetaDataByteString";
    public static final String DEK_RECORD_FIELD_KMSVERSION = "kmsKeyVersion";
    public static final String DEK_RECORD_FIELD_KMSURL = "kmsURL";
    public static final String DEK_RECORD_FIELD_WRAPPEDKEYBYTESTRING = "wrappedKeyByteString";

    public static final String UTC_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    public static final String DEMO_VERIFIER_TRANS = "10";
    public static final String DEMO_EMPLOYER_TRANS = "12";
    public static final String BILL_STATUS_VAL_DEMO_VERIFIER = "DEMO_VERIFIER";
    public static final String BILL_STATUS_VAL_DEMO_EMPLOYEE = "DEMO_EMPLOYEE";

    /*public static final String LOG4J2_CORRELATION_ID = "correlationId";
    public static final String LOG4J2_TRACKING_ID = "trackingId";
    public static final String LOG4J2_APP_ID = "APP_ID";
    public static final String BPS_APP_ID = "VS-BillingPublisher-Service";
    public static final String ABRN_APP_ID = "ABRN-Product-Service";
    public static final String REVERIFY_APP_ID = "REVERIFY-Product-Service";
    public static final String LOG4J2_LOGMESSAGE = "logMessage";
    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    public static final String BILL_STATUS_VAL_NEW = "NEW";
    public static final String RECON_STATUS_VAL_NEW = "NEW";
    public static final String BILL_STATUS_VAL_ERR = "ERROR";
    public static final String BILL_STATUS_VAL_PUBLISHED = "PUBLISHED";
    public static final String BILL_STATUS_VAL_DEMO_VERIFIER = "DEMO_VERIFIER";
    public static final String BILL_STATUS_VAL_DEMO_EMPLOYEE = "DEMO_EMPLOYEE";

    public static final String BILL_RECORD_KIND = "BillingRecords";
    public static final String BILLING_REC_FIELD_PROD = "product";
    public static final String BILLING_REC_FIELD_APPID = "appId";
    public static final String BILLING_REC_FIELD_APPNAME = "appName";
    public static final String BILLING_REC_FIELD_CRID = "correlationId";
    public static final String BILLING_REC_FIELD_REQTRACKID = "requestTrackingId";
    public static final String BILLING_REC_FIELD_PERMPURP = "permissiblePurpose";
    public static final String BILLING_REC_FIELD_BILLEVENT = "billingEvent";
    public static final String BILLING_REC_FIELD_DEKREF = "dekReference";
    public static final String BILLING_REC_FIELD_BILLSTATUS = "billingStatus";
    public static final String BILLING_REC_FIELD_RECONCILIATIONTATUS = "reconciliationStatus";
    public static final String BILLING_REC_FIELD_PUBLISHTIME = "publishTime";











    public static final String ABRN_TRANSACTION_TYPE = "122";

    public static final String REVERIFY_TRANSACTION_TYPE = "214";
    public static final int ABRN_NO_HIT_TRANSACTION_STATUS_CODE = 47990;

    public static final Set<String> NA_SUBCODE_PRODUCT_CODES = Set.of(
            "VOE-B2BSELB-SSN-F",
            "VOE-B2B-SSN-F",
            "VOE-B2BSELC-SSN-F",
            "VOI-B2BSELG-SSN-F",
            "VOI-B2BSELS-SSN-F",
            "VOI-B2BSELC-SSN-F",
            "VOI-B2B-SSN-F",
            "ABRN-B2B-SSN",
            "ABRN-B2B-A",
            "ABRN-B2B-SSN-F"
    );*/

    private BillingPublisherConstants() {
    }
}
