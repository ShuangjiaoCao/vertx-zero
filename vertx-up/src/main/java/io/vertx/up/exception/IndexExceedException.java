package io.vertx.up.exception;

import io.vertx.zero.exception.UpException;

public class IndexExceedException extends UpException {

    public IndexExceedException(final Class<?> clazz,
                                final Integer index) {
        super(clazz, String.valueOf(index));
    }

    @Override
    public int getCode() {
        return -40032;
    }
}
