package io.vertx.zero.marshal.micro;

import io.vertx.zero.marshal.Visitor;

import java.util.concurrent.ConcurrentMap;

/**
 * @param <T>
 * @author Lang
 */
public interface ServerVisitor<T>
        extends Visitor<ConcurrentMap<Integer, T>> {

    String YKEY_TYPE = "type";

    String YKEY_CONFIG = "config";
}
