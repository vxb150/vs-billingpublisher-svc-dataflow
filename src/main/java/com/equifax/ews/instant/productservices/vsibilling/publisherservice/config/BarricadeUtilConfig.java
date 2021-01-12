package com.equifax.ews.instant.productservices.vsibilling.publisherservice.config;

import com.equifax.core.barricade.cryptography.bouncycastle.BouncyCastleFipsSecurityProvider;
import com.equifax.core.barricade.cryptography.crypto.encryption.Encryptor;
import com.equifax.core.barricade.cryptography.crypto.util.internal.SecureRandomCSPRNG;
import com.equifax.core.barricade.cryptography.domain.SemanticVersion;
import com.equifax.core.barricade.cryptography.google.GoogleKeyManager;
import com.equifax.core.barricade.cryptography.impl.BasicCryptographyManager;
import com.equifax.core.barricade.cryptography.jce.JceCryptographyServices;
import com.equifax.core.barricade.cryptography.key.HybridWrappedKey;
import com.equifax.core.barricade.cryptography.key.KeyManager;
import com.equifax.core.barricade.cryptography.key.KeyReference;
import com.equifax.core.barricade.cryptography.key.PublicKey;
import com.equifax.core.barricade.cryptography.key.WrappedKey;
import com.equifax.core.barricade.cryptography.util.Encoding;
import com.equifax.ews.instant.productservices.vsibilling.publisherservice.domain.WrappedDekInfo;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery;
import com.google.cloud.datastore.Value;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

import static com.equifax.ews.instant.productservices.vsibilling.publisherservice.constants.BillingPublisherConstants.DEK_RECORD_FIELD_COUNTER;
import static com.equifax.ews.instant.productservices.vsibilling.publisherservice.constants.BillingPublisherConstants.DEK_RECORD_FIELD_DEKREF;
import static com.equifax.ews.instant.productservices.vsibilling.publisherservice.constants.BillingPublisherConstants.DEK_RECORD_FIELD_ENCMETADATA;
import static com.equifax.ews.instant.productservices.vsibilling.publisherservice.constants.BillingPublisherConstants.DEK_RECORD_FIELD_KMSURL;
import static com.equifax.ews.instant.productservices.vsibilling.publisherservice.constants.BillingPublisherConstants.DEK_RECORD_FIELD_KMSVERSION;
import static com.equifax.ews.instant.productservices.vsibilling.publisherservice.constants.BillingPublisherConstants.DEK_RECORD_FIELD_STATUS;
import static com.equifax.ews.instant.productservices.vsibilling.publisherservice.constants.BillingPublisherConstants.DEK_RECORD_FIELD_WRAPPEDKEYBYTESTRING;
import static com.equifax.ews.instant.productservices.vsibilling.publisherservice.constants.BillingPublisherConstants.DEK_RECORD_KIND;

public class BarricadeUtilConfig {

    /** The log to output status messages to. */
    private static final Logger logger = LoggerFactory.getLogger(BarricadeUtilConfig.class);

    public static JceCryptographyServices getJceCryptographyServices(){
        logger.debug("BarricadeUtilConfig.getJceCryptographyServices");
        return  JceCryptographyServices.newBuilder()
                .securityProvider(
                        BouncyCastleFipsSecurityProvider.newBuilder()
                                .approvedMode(true)
                                .build()).build();
    }


    public static BasicCryptographyManager getBasicCryptographyManager() {
        logger.debug("BarricadeUtilConfig.getBasicCryptographyManager");
        JceCryptographyServices jceCryptographyServices = getJceCryptographyServices();
        return BasicCryptographyManager.newBuilder().
                registerCryptogrpahyServices(jceCryptographyServices).
                registerKeyManager(GoogleKeyManager.newBuilder()
                        .randomNumberGenerator(new SecureRandomCSPRNG())
                        .lease(10000, Duration.ofMinutes(5))
                        .applicationRetry(0, 500, 2000)
                        .disableGrpcRetry(false)
                        .grpcTimeout(1)
                        .build())
                .build();
    }

    public static Encryptor getEncryptor (BasicCryptographyManager basicCryptographyManager) {
        return basicCryptographyManager.getCryptographyServices().getEncryptor();
    }

    public static String getBucketPath(
                BasicCryptographyManager basicCryptographyManager,
                WrappedKey wrappedKey,
                BillingPublisherProperties billingPublisherProperties)
            throws Exception {
        logger.info("BarricadeUtilConfig.getBucketPath -- wrappedKey:" + wrappedKey);
        logger.info("BarricadeUtilConfig.getBucketPath -- billingPublisherProperties:" + billingPublisherProperties);
        PublicKey publicKey = getPublicKey(basicCryptographyManager, billingPublisherProperties.getPubsubGcpKeyRing());
        byte[] bytes = hybridWrappedKey(basicCryptographyManager, wrappedKey, publicKey);
        String uUID= UUID.randomUUID().toString();
        String bucketKeyPath = String.format(billingPublisherProperties.getGcpBucketFileName(), uUID);

        logger.info("BarricadeUtilConfig.getBucketPath -- bucketKeyPath:" + bucketKeyPath);
        Storage storage = StorageOptions.newBuilder().setProjectId(billingPublisherProperties.getPubsubProjectId()).build().getService();
        BlobId blobId = BlobId.of(billingPublisherProperties.getGcpBucket(), bucketKeyPath);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        Blob storageDetails = storage.create(blobInfo, bytes);
        String gcpBucketPath = "gs://" + storageDetails.getBlobId().getBucket() + "/" + storageDetails.getBlobId().getName();
        logger.info("BarricadeUtilConfig.getBucketPath -- gcpBucketPath:" + gcpBucketPath);
        return gcpBucketPath;
    }

    public static byte[] hybridWrappedKey(BasicCryptographyManager basicCryptographyManager, WrappedKey wrappedKey, PublicKey publicKey){
        KeyManager keyManager = basicCryptographyManager.getKeyManager();
        HybridWrappedKey hybridWrappedKey = keyManager.rewrap(wrappedKey, publicKey);
        Encoding enc = hybridWrappedKey.getEncoding();
        byte[] bytes = enc.getEncodedValue();
        logger.info("BarricadeUtilConfig.hybridWrappedKey -- bytes:" + bytes);
        return bytes;
    }
    public static WrappedKey getWrappedKey (BasicCryptographyManager basicCryptographyManager, String googleResourceId){
        logger.info("PubSubEncryptionService.encrypt -- Key Ring:" + googleResourceId);
        KeyManager keyManager = basicCryptographyManager.getKeyManager(1L, new SemanticVersion(1, 0, "0"));
        KeyReference symKeyReference = keyManager.getKeyReference(googleResourceId, null);
        WrappedKey wrappedKey = keyManager.generateKey(symKeyReference, 32);
        logger.info("PubSubEncryptionService.encrypt -- wrappedKey:" + wrappedKey);
        return wrappedKey;
    }

    public static PublicKey getPublicKey(BasicCryptographyManager basicCryptographyManager, String pubsubResourceId) {
        logger.info("BarricadeUtilConfig.getPublicKey -- pubsubResourceId:" + pubsubResourceId);
        KeyManager keyManager = basicCryptographyManager.getKeyManager();
        KeyReference pubKeyReference = keyManager.getKeyReference(pubsubResourceId, null);
        PublicKey publicKey = keyManager.loadPublicKey(pubKeyReference);
        logger.info("BarricadeUtilConfig.getPublicKey -- publicKey:" + publicKey);
        return publicKey;
    }

    public static WrappedDekInfo getWrappedDekInfo(String dekKey){
        logger.info("BarricadeUtilConfig.getWrappedDekInfo.dekKey" + dekKey);
        Datastore datastore = getDatastore();
        logger.info("BarricadeUtilConfig.getWrappedDekInfo.datastore" + datastore);
        if(null == datastore) {
            return null;
        }
        Query<Entity> query = Query.newEntityQueryBuilder().setKind(DEK_RECORD_KIND)
                .setFilter(StructuredQuery.PropertyFilter.eq(DEK_RECORD_FIELD_STATUS, "active"))
                .setFilter(StructuredQuery.PropertyFilter.eq(DEK_RECORD_FIELD_DEKREF, dekKey))
                .build();
        logger.info("BarricadeUtilConfig.getWrappedDekInfo: query:" + query);
        QueryResults<Entity> results = datastore.run(query);
        logger.info("BarricadeUtilConfig.getWrappedDekInfo: results:" + results.hasNext());
        while(results.hasNext()){
            Entity entity = results.next();
            return getQueryData(entity.getProperties());
        }
        return null;
    }

    public static WrappedDekInfo getQueryData(Map<String, Value<?>> entValues) {
        logger.info("BarricadeUtilConfig.getQueryData: entValues:" + entValues.values());
        Value<String> dekReference = (Value<String>) entValues.get(DEK_RECORD_FIELD_DEKREF);
        Value<Long> counter = (Value<Long>) entValues.get(DEK_RECORD_FIELD_COUNTER);
        Value<String> encMetaDataByteString = (Value<String>) entValues.get(DEK_RECORD_FIELD_ENCMETADATA);
        Value<String> kmsKeyVersion = (Value<String>) entValues.get(DEK_RECORD_FIELD_KMSVERSION);
        Value<String> kmsURL = (Value<String>) entValues.get(DEK_RECORD_FIELD_KMSURL);
        Value<String> status = (Value<String>) entValues.get(DEK_RECORD_FIELD_STATUS);
        Value<String> wrappedKeyByteString = (Value<String>) entValues.get(DEK_RECORD_FIELD_WRAPPEDKEYBYTESTRING);

        return WrappedDekInfo.builder()
                .dekReference(dekReference.get())
                .counter(counter.get())
                .encMetaDataByteString(encMetaDataByteString != null ? encMetaDataByteString.get() : null)
                .kmsKeyVersion(kmsKeyVersion != null ? kmsKeyVersion.get() : null)
                .kmsURL(kmsURL != null ? kmsURL.get() : null)
                .status(status.get())
                .wrappedKeyByteString(wrappedKeyByteString.get())
                .build();
    }

    public static Datastore getDatastore(){
        logger.info("BarricadeUtilConfig.getDatastore");
        return DatastoreOptions.getDefaultInstance()
                .toBuilder()
                .setNamespace("vsiBillingService-dev")
                .setProjectId("ews-vs-prdsvs-dev-npe-476d")
                .build()
                .getService();
    }

}