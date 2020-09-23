package org.alien4cloud.inventory.nexus.rest;

public class RestException extends Exception {

    private static final long serialVersionUID = -468015641215338045L;

    public RestException(String message) {
        super(message);
    }

    public RestException(String message, Throwable cause) {
        super(message, cause);
    }
}
