package io.vertx.up.exception;

import io.vertx.zero.exception.UpException;

public class WorkerMissingException extends UpException {

    public WorkerMissingException(final Class<?> clazz,
                                  final String address) {
        super(clazz, address);
    }

    @Override
    public int getCode() {
        return -40014;
    }
}
