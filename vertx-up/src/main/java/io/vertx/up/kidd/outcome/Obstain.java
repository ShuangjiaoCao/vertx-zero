package io.vertx.up.kidd.outcome;

import io.vertx.core.AsyncResult;
import io.vertx.up.atom.Envelop;
import io.vertx.up.exception.WebException;
import io.vertx.up.exception.web._404RecordNotFoundException;
import io.vertx.up.func.Fn;
import io.vertx.up.kidd.Spy;
import io.vertx.up.log.Annal;
import io.vertx.up.tool.mirror.Instance;

/**
 * Response building to JsonObject
 */
public class Obstain<T> {

    private final Annal LOGGER = Annal.get(Obstain.class);

    protected final transient Class<?> clazz;
    protected final transient Annal logger;
    protected transient AsyncResult<T> handler;
    protected transient Spy<T> spy;
    protected transient Envelop envelop;

    public static <T> Obstain<T> start(final Class<?> clazz) {
        return new Obstain<>(clazz);
    }

    protected Obstain(final Class<?> clazz) {
        Fn.safeSemi(null == clazz, this.LOGGER,
                () -> this.LOGGER.error(Info.ERROR_NULL, clazz));
        this.clazz = clazz;
        this.logger = Annal.get(clazz);
    }

    /**
     * Connect to message handler
     *
     * @param handler
     * @return
     */
    public Obstain<T> connect(final AsyncResult<T> handler) {
        Fn.safeSemi(null == handler, this.LOGGER,
                () -> this.LOGGER.error(Info.ERROR_HANDLER, handler, this.clazz));
        this.handler = handler;
        return this;
    }

    /**
     * Connect to spy to process id
     *
     * @param spy
     * @return
     */
    public Obstain<T> connect(final Spy<T> spy) {
        this.spy = spy;
        return this;
    }

    public Obstain<T> unique() {
        final WebException error404 = Instance.instance(
                _404RecordNotFoundException.class, this.clazz);
        return unique(error404);
    }

    /**
     * @param internal404
     * @return
     */
    public Obstain<T> unique(final WebException internal404) {
        if (this.isReady()) {
            this.envelop = Fn.getSemi(this.handler.succeeded(), this.logger,
                    // 200. Handler executed successfully
                    () -> Fn.getSemi(null == this.handler.result(), this.logger,

                            // 404 -> Response
                            () -> Fn.getSemi(null == internal404, this.logger,
                                    // 200 with empty data
                                    Envelop::ok,
                                    // 404 Error returned.
                                    Failure.build(internal404)),

                            // 200 -> Response
                            () -> Fn.getSemi(null == this.spy, this.logger,
                                    // 200 -> No spy provided
                                    () -> Envelop.success(this.handler.result()),
                                    // 200 -> Spy provided
                                    () -> Envelop.success(this.spy.out(this.handler.result())))),

                    // 500. Internal Error
                    Failure.build500Flow(this.clazz, this.handler.cause()));
        }
        return this;
    }

    /**
     * Build response Envelop object.
     *
     * @return
     */
    public Envelop to() {
        Fn.safeSemi(null == this.envelop, this.LOGGER,
                () -> this.LOGGER.error(Info.ERROR_ENVELOP, this.clazz));
        return this.envelop;
    }

    protected boolean isReady() {
        return null != this.handler;
    }
}
