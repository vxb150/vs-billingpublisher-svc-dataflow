package com.equifax.ews.instant.productservices.vsibilling.publisherservice.service;

import com.equifax.core.barricade.cryptography.crypto.encryption.EncryptionResponse;
import com.equifax.core.barricade.cryptography.crypto.encryption.Encryptor;
import com.equifax.core.barricade.cryptography.impl.BasicCryptographyManager;
import com.equifax.core.barricade.cryptography.key.WrappedKey;
import com.equifax.ews.instant.productservices.vsibilling.publisherservice.config.BarricadeUtilConfig;
import com.equifax.ews.instant.productservices.vsibilling.publisherservice.config.BillingPublisherProperties;
import com.equifax.ews.instant.productservices.vsibilling.publisherservice.domain.PubSubEncryptedData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Objects;


public class PubSubEncryptionService {

    private static final Logger logger = LoggerFactory.getLogger(PubSubEncryptionService.class);


    public PubSubEncryptedData encrypt(byte[] eventPayload,
                                       BasicCryptographyManager basicCryptographyManager,
                                       BillingPublisherProperties billingPublisherProperties)
                                        throws Exception {
        if (Objects.isNull(eventPayload)) {
            logger.info("No data to encrypt. Returning empty object");
            return new PubSubEncryptedData();
        }
        WrappedKey wrappedKey = BarricadeUtilConfig.getWrappedKey(basicCryptographyManager, billingPublisherProperties.getBillingGcpKeyRing());
        String gcsBucketPath = BarricadeUtilConfig.getBucketPath(basicCryptographyManager, wrappedKey, billingPublisherProperties);
        Encryptor encryptor = BarricadeUtilConfig.getEncryptor(basicCryptographyManager);
        logger.info("PubSubEncryptionService.encrypt -- wrappedKey:" + wrappedKey);
        logger.info("PubSubEncryptionService.encrypt -- gcsBucketPath:" + gcsBucketPath);
        logger.info("PubSubEncryptionService.encrypt -- encryptor:" + encryptor);
        if (Objects.isNull(wrappedKey) || gcsBucketPath == null || Objects.isNull(encryptor)) {
            logger.error("PubSub - wrappedKey or encryptor or gcsBucketPath is empty. Can not encrypt eventPayload");
            throw new IllegalStateException("PubSub - wrappedKey or encryptor or gcsBucketPath is empty. Can not encrypt eventPayload");
        }
        logger.debug("Encrypting Payload");
        ByteArrayInputStream plainInputStream = new ByteArrayInputStream(eventPayload);
        ByteArrayOutputStream encryptedOutputStream = new ByteArrayOutputStream();
        EncryptionResponse response = encryptor.encrypt(wrappedKey, plainInputStream, encryptedOutputStream, null);
        byte[] encryptedPayload = encryptedOutputStream.toByteArray();
        byte[] encryptionMetadata = response.getEncryptionMetaData().getEncoding().getEncodedValue();
        logger.debug("Encrypting Payload Complete");
        PubSubEncryptedData pubSubEncryptedData = new PubSubEncryptedData();
        pubSubEncryptedData.setEncryptedEventPayload(Base64.getEncoder().encodeToString(encryptedPayload));
        pubSubEncryptedData.setEncryptionMetadata(Base64.getEncoder().encodeToString(encryptionMetadata));
        pubSubEncryptedData.setGcsBucketPath(gcsBucketPath);
        logger.debug("Returning encrypted Data");
        return pubSubEncryptedData;
    }
}
