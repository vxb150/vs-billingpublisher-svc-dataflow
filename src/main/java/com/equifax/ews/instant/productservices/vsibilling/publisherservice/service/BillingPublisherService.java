package com.equifax.ews.instant.productservices.vsibilling.publisherservice.service;

import com.equifax.core.barricade.cryptography.bouncycastle.BouncyCastleFipsSecurityProvider;
import com.equifax.core.barricade.cryptography.crypto.encryption.Encryptor;
import com.equifax.core.barricade.cryptography.crypto.util.internal.SecureRandomCSPRNG;
import com.equifax.core.barricade.cryptography.google.GoogleKeyManager;
import com.equifax.core.barricade.cryptography.impl.BasicCryptographyManager;
import com.equifax.core.barricade.cryptography.jce.JceCryptographyServices;
import com.equifax.core.barricade.cryptography.jce.encryption.JceDecryptionResponse;
import com.equifax.core.barricade.cryptography.key.WrappedKey;
import com.equifax.core.barricade.cryptography.util.impl.BasicEncoding;
import org.apache.beam.sdk.transforms.DoFn;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Base64;


public class BillingPublisherService extends DoFn<String, String> {

    @ProcessElement
    public void processElement(ProcessContext ctx) {
        System.out.println("Started Process:");
        String input = ctx.element();
        String output = input;
        try {
            String ssn = "f06VpDUNUl0pjoR043Cn86T0TK0ZQnJSVy6wueopzOg0PNKn3g==";
            System.out.println("SSN:" + ssn);
            output = decryptData(ssn);
            System.out.println("Decrypted SSN:" + output);
            /*ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));
            if(input!=null){
                BillingRequest br = mapper.readValue(input, BillingRequest.class);
                br.setStatusBillingService("Output to Topic");
                br.setDekReference("Dek--1846128555964109028");
            }*/
        } catch (Exception e) {
            System.out.println("Error Occured:" + e.getMessage());
        }
        output = output + "-1";
        ctx.output(output);
    }

    private String decryptData (String barricadeCipherText) throws Exception {
        String dekKeyString = "CAIQARoFMS4wLjAilAIKgQEKf3Byb2plY3RzL3NlYy1jcnlwdG8taWFtLW5wZS1jOGVkL2xvY2F0aW9ucy91cy9rZXlSaW5ncy9ld3MtdnMtcHJkc3ZzLWRldi1ucGUtNDc2ZF9iYXAwMDA2NjQxX2RhdGEvY3J5cHRvS2V5cy92cy1iaWxsaW5nLXN5bS1rZXkSiwEKJAD6Gcx21DG/gnMrEJU0DguBiGI2Ip3UxkNNzy9G8onISDyx+hJjKmEKFAoMnZyNyqkOxjFiIerhEKLL+vALEi8KJ24kC6xSZrOr8HgCBLwMTn1AaettYOE9fPocqBq9pwkEiWgKujq3ShDxt9rSBBoYChCm+knEu7IvN8adVbhLp4pWELSy/ZcNIAE=";
        byte[] barricadeKeyByte = Base64.getDecoder().decode(dekKeyString.getBytes(StandardCharsets.UTF_8));
        JceCryptographyServices jceCryptographyServices = getJceCryptographyServices();
        BasicCryptographyManager basicCryptographyManager = getBasicCryptographyManager(jceCryptographyServices);
        WrappedKey wrappedDekForDecryption = basicCryptographyManager.decode(new BasicEncoding(barricadeKeyByte), WrappedKey.class);
        if(wrappedDekForDecryption == null){
//                LOGGER.info(Map.of("Decryption Block ","No WrappedKey to decrypt"));
            throw new Exception("No WrappedKey to decrypt");
        }
        String decryptedText = null;
        if (barricadeCipherText != null ) {
            ByteArrayInputStream cipherIS = new ByteArrayInputStream(Base64.getDecoder().decode(barricadeCipherText.getBytes(StandardCharsets.UTF_8)));
            ByteArrayOutputStream plainOS = new ByteArrayOutputStream();
            Encryptor encryptor = basicCryptographyManager.getCryptographyServices().getEncryptor();
            encryptor.decrypt(wrappedDekForDecryption, cipherIS, plainOS, null);
            decryptedText = new String(plainOS.toByteArray(), StandardCharsets.UTF_8);

        }
        return decryptedText;

    }

    public JceCryptographyServices getJceCryptographyServices(){
        JceCryptographyServices jce = JceCryptographyServices.newBuilder()
                .securityProvider(
                        BouncyCastleFipsSecurityProvider.newBuilder()
                                .approvedMode(true).build())
                .build();
        return jce;
    }


    public BasicCryptographyManager getBasicCryptographyManager(JceCryptographyServices jceCryptographyServices) {
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

}
