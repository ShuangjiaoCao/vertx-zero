package io.vertx.zero.exception;

import java.text.MessageFormat;

public class ErrorMissingException extends ZeroRunException {

    public ErrorMissingException(final Integer code, final String clazz) {
        super(MessageFormat.format(Info.ECODE_MSG, code, clazz));
    }
}
