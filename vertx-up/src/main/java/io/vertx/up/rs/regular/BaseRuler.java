package io.vertx.up.rs.regular;

import io.vertx.up.atom.Rule;
import io.vertx.up.exception.WebException;
import io.vertx.up.exception.web._400ValidationRuleException;

public abstract class BaseRuler implements Ruler {

    protected WebException failure(
            final String field,
            final Object value,
            final Rule rule) {
        final String message = rule.getMessage();
        final WebException error = new _400ValidationRuleException(
                getClass(), field, value, message);
        error.setReadible(message);
        return error;
    }
}
