package io.vertx.up.plugin.mongo;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.up.atom.Envelop;
import io.vertx.up.concurrent.Runner;
import io.vertx.up.func.Fn;
import io.vertx.up.kidd.Heart;
import io.vertx.up.log.Annal;
import io.vertx.zero.eon.Values;

import java.util.concurrent.CountDownLatch;

/**
 * Tool for mongodb, simply the query with MongoClient
 * Read thread executor -> Rtor
 */
public class MongoRtor {

    private static final Annal LOGGER = Annal.get(MongoRtor.class);

    private transient final MongoClient client;

    private transient Class<?> hitted;
    private transient String collection;
    private transient FindOptions options;

    public static MongoRtor init(final MongoClient client) {
        return new MongoRtor(client);
    }

    private MongoRtor(final MongoClient client) {
        this.client = client;
    }

    public MongoRtor connect(final Class<?> clazz) {
        this.hitted = clazz;
        return this;
    }

    public MongoRtor connect(final String collection) {
        this.collection = collection;
        return this;
    }

    public MongoRtor connect(final FindOptions options) {
        this.options = options;
        return this;
    }

    /**
     * 单个子查询处理
     *
     * @param dataObject
     * @param refKey
     * @param verticalKey
     * @param mountField
     * @return
     */
    public JsonObject minorBy(final JsonObject dataObject,
                              final String refKey,
                              final String verticalKey,
                              final String mountField) {
        final JsonArray dataArray = new JsonArray().add(dataObject);
        final JsonArray result = this.minorBy(dataArray, refKey, verticalKey, mountField);
        return result.getJsonObject(Values.IDX);
    }

    /**
     * Secondary query method to enable concurrent query
     * For example
     * Topic = [{...},{...},{...}]
     * Then query Videos belong to Topic for each element.
     * 1. Extract filter condition with refKey = item.get(verticalKey);
     * 2. Put options, filter to query result to JsonArray
     * 3. Mount the jsonArray to item.put(mountField,jsonArray)
     *
     * @param dataArray
     * @param refKey
     * @param verticalKey
     * @param mountField
     * @return
     */
    @SuppressWarnings("unchecked")
    public JsonArray minorBy(
            final JsonArray dataArray,
            final String refKey,
            final String verticalKey,
            final String mountField
    ) {
        return Fn.getJvm(new JsonArray(), () -> {
                    // Build counter.
                    final CountDownLatch counter = new CountDownLatch(dataArray.size());
                    Fn.itJArray(dataArray, JsonObject.class, (item, index) -> {
                        // Get item value by verticalKey
                        final Object value = item.getValue(verticalKey);
                        Fn.safeNull(() -> Runner.run(() -> {
                            // Direct set filter
                            final JsonObject filter = new JsonObject().put(refKey, value);
                            this.client.findWithOptions(this.collection, filter, this.options, res -> {
                                // Build response model
                                final Envelop envelop = Heart.getReacts(this.hitted)
                                        .connect(res).result().to();
                                final JsonArray data = envelop.data();
                                if (null != data) {
                                    item.put(mountField, data);
                                }
                                counter.countDown();
                            });
                        }, "concurrent-secondary-" + value), value);
                    });
                    // Await
                    try {
                        counter.await();
                    } catch (final InterruptedException ex) {
                        LOGGER.jvm(ex);
                    }
                    // Convert to target mountKey
                    return dataArray;
                }, dataArray, verticalKey, refKey,
                this.hitted, this.collection);
    }
}
