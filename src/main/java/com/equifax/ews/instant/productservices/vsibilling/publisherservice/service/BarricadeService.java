package com.equifax.ews.instant.productservices.vsibilling.publisherservice.service;

import com.equifax.core.barricade.cryptography.crypto.encryption.Encryptor;
import com.equifax.core.barricade.cryptography.impl.BasicCryptographyManager;
import com.equifax.core.barricade.cryptography.key.WrappedKey;
import com.equifax.core.barricade.cryptography.util.impl.BasicEncoding;
import com.equifax.ews.instant.productservices.vsibilling.publisherservice.config.BarricadeUtilConfig;
import com.equifax.ews.instant.productservices.vsibilling.publisherservice.domain.EncryptResponse;
import com.equifax.ews.instant.productservices.vsibilling.publisherservice.domain.WrappedDekInfo;
import com.equifax.ews.instant.productservices.vsibilling.publisherservice.exception.BarricadeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class BarricadeService {
    /** The log to output status messages to. */
    private static final Logger logger = LoggerFactory.getLogger(BarricadeUtilConfig.class);

    public EncryptResponse decrypt(String barricadeCipherText, String dekReference, BasicCryptographyManager basicCryptographyManager) throws BarricadeException {
        logger.info("BarricadeService.decrypt -- Start");
        logger.info("BarricadeService.decrypt -- barricadeCipherText:" + barricadeCipherText);
        logger.info("BarricadeService.decrypt -- dekReference:" + dekReference);
        WrappedKey wrappedDekForDecryption = getWrappedKeyForDecryption(dekReference);
        logger.info("BarricadeService.decrypt -- wrappedDekForDecryption:" + wrappedDekForDecryption);
        if(wrappedDekForDecryption == null){
            logger.error("BarricadeService.decrypt:No WrappedKey to decrypt");
            throw new BarricadeException("No WrappedKey to decrypt");
        }
        EncryptResponse encryptResponse = new EncryptResponse();
        logger.info("BarricadeService.decrypt -- encryptResponse:" + encryptResponse);
        String decryptedText;
        if (barricadeCipherText != null ) {
            ByteArrayInputStream cipherIS = new ByteArrayInputStream(Base64.getDecoder().decode(barricadeCipherText.getBytes(StandardCharsets.UTF_8)));
            ByteArrayOutputStream plainOS = new ByteArrayOutputStream();
            Encryptor encryptor = basicCryptographyManager.getCryptographyServices().getEncryptor();
            encryptor.decrypt(wrappedDekForDecryption, cipherIS, plainOS, null);
            decryptedText = new String(plainOS.toByteArray(), StandardCharsets.UTF_8);
            encryptResponse.setValue(decryptedText);
        }
        logger.info("BarricadeService.decrypt -- End");
        return encryptResponse;
    }

    private WrappedKey getWrappedKeyForDecryption(String dekReference) {
        logger.info("BarricadeService.decrypt -- Start");
        BasicCryptographyManager basicCryptographyManager = BarricadeUtilConfig.getBasicCryptographyManager();
        if(dekReference == null || dekReference.trim().equals("")){
            return null;
        }
        WrappedKey wrappedDekForDecryption = null;
        WrappedDekInfo wrappedDekInfo = BarricadeUtilConfig.getWrappedDekInfo(dekReference);
        String dekKeyString = wrappedDekInfo.getWrappedKeyByteString();
        byte[] barricadeKeyByte = Base64.getDecoder().decode(dekKeyString.getBytes(StandardCharsets.UTF_8));
        wrappedDekForDecryption = basicCryptographyManager.decode(new BasicEncoding(barricadeKeyByte), WrappedKey.class);
        logger.info("BarricadeService.decrypt -- End");
        return wrappedDekForDecryption;
    }
}
