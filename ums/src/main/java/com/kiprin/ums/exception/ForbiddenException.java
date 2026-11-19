package com.kiprin.ums.exception;

public class ForbiddenException extends BasicException {

    public ForbiddenException(String message) {
        super(403, message);
    }
}
