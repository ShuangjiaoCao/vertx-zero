package io.vertx.zero.exception;

public class ClusterConflictException extends DemonException {

    public ClusterConflictException(final Class<?> clazz,
                                    final String name,
                                    final String options) {
        super(clazz, name, options);
    }

    @Override
    public int getCode() {
        return -10004;
    }
}
