package com.kiprin.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.OK)
public class BasicException extends RuntimeException {

    public static final int DEFAULT_CODE = 1001;

    public final int code;

    public BasicException(String message) {
        this(DEFAULT_CODE, message);
    }

    public BasicException(Throwable cause) {
        this(DEFAULT_CODE, cause);
    }

    public BasicException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BasicException(int code, Throwable cause) {
        super(rootMessage(cause));
        this.code = code;
    }

    public static String rootMessage(Throwable ex) {
        Throwable root = ex;
        while (root.getCause() != null && root.getCause() != root) {
            root = root.getCause();
        }
        String message = root.getMessage();
        return message != null ? message : root.getClass().getSimpleName();
    }
}
