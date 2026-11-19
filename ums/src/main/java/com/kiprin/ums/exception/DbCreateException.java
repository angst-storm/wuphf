package com.kiprin.ums.exception;

public class DbCreateException extends BasicException {

    public DbCreateException(Throwable cause) {
        super(1002, cause);
    }
}
