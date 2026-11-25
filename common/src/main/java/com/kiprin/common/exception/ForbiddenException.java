package com.kiprin.common.exception;

public class ForbiddenException extends BasicException {

    public ForbiddenException(String message) {
        super(403, message);
    }
}
