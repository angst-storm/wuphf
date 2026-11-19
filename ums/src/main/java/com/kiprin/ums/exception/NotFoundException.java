package com.kiprin.ums.exception;

public class NotFoundException extends BasicException {

    public NotFoundException(String message) {
        super(404, message);
    }
}
