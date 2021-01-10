package com.equifax.ews.instant.productservices.vsibilling.publisherservice.exception;

public class BarricadeException extends RuntimeException {

    public BarricadeException(String message, Throwable cause) {
        super(message, cause);
    }

    public BarricadeException(String message) {
        super(message);
    }
}

