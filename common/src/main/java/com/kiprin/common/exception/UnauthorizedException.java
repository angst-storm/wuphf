package com.kiprin.common.exception;

public class UnauthorizedException extends BasicException {

    public UnauthorizedException(String message) {
        super(401, message);
    }
}
