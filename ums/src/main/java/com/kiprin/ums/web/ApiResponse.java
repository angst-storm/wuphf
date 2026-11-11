package com.kiprin.ums.web;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.kiprin.ums.service.BadRequestException;
import com.kiprin.ums.service.ForbiddenException;
import com.kiprin.ums.service.NotFoundException;
import com.kiprin.ums.service.UnauthorizedException;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public final class ApiResponse {

    public static final String CODE = "code";
    public static final String MESSAGE = "message";
    public static final String DATA = "data";

    private ApiResponse() {
    }

    public static Map<String, Object> of(int code, String message, Object data) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put(CODE, code);
        body.put(MESSAGE, message);
        body.put(DATA, data);
        return body;
    }

    public static Mono<ResponseEntity<Map<String, Object>>> okAsync(Callable<Map<String, Object>> action) {
        return Mono.fromCallable(() -> {
                    try {
                        return action.call();
                    } catch (NotFoundException e) {
                        return of(404, e.getMessage(), null);
                    } catch (BadRequestException e) {
                        return of(400, e.getMessage(), null);
                    } catch (UnauthorizedException e) {
                        return of(401, e.getMessage(), null);
                    } catch (ForbiddenException e) {
                        return of(403, e.getMessage(), null);
                    }
                })
                .subscribeOn(Schedulers.boundedElastic())
                .map(body -> ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(body));
    }
}
