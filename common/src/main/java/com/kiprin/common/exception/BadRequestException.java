package com.kiprin.common.exception;

public class BadRequestException extends BasicException {

    public BadRequestException(String message) {
        super(400, message);
    }
}
