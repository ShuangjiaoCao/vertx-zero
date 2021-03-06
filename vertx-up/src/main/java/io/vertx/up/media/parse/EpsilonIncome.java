package io.vertx.up.media.parse;

import io.vertx.ext.web.RoutingContext;
import io.vertx.up.atom.Epsilon;
import io.vertx.up.atom.Event;
import io.vertx.up.eon.ID;
import io.vertx.up.exception.WebException;
import io.vertx.up.func.Fn;
import io.vertx.up.log.Annal;
import io.vertx.up.rs.Filler;
import io.vertx.up.tool.mirror.Instance;
import io.vertx.up.web.ZeroSerializer;
import io.vertx.zero.eon.Values;

import javax.ws.rs.DefaultValue;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Help to extract epsilon
 */
public class EpsilonIncome implements Income<List<Epsilon<Object>>> {

    private static final Annal LOGGER = Annal.get(EpsilonIncome.class);

    private transient final Atomic<Object> atomic
            = Instance.singleton(MimeAtomic.class);

    @Override
    public List<Epsilon<Object>> in(final RoutingContext context,
                                    final Event event)
            throws WebException {
        final Method method = event.getAction();
        final Class<?>[] paramTypes = method.getParameterTypes();
        final Annotation[][] annoTypes = method.getParameterAnnotations();
        final List<Epsilon<Object>> args = new ArrayList<>();
        for (int idx = 0; idx < paramTypes.length; idx++) {

            /** For each field specification **/
            final Epsilon<Object> epsilon = new Epsilon<>();
            epsilon.setArgType(paramTypes[idx]);
            epsilon.setAnnotation(getAnnotation(annoTypes[idx]));
            epsilon.setName(getName(epsilon.getAnnotation()));

            /** Default Value **/
            epsilon.setDefaultValue(getDefault(annoTypes[idx], epsilon.getArgType()));

            /** Epsilon income -> outcome **/
            final Epsilon<Object> outcome =
                    this.atomic.ingest(context, epsilon);
            args.add(Fn.get(() -> outcome, outcome));
        }
        return args;
    }

    private String getName(final Annotation annotation) {
        return Fn.getSemi(null == annotation, LOGGER,
                () -> ID.IGNORE,
                () -> Fn.getSemi(!Filler.NO_VALUE.contains(annotation.annotationType()),
                        LOGGER,
                        () -> Instance.invoke(annotation, "value"),
                        () -> ID.DIRECT));
    }

    private Annotation getAnnotation(final Annotation[] annotations) {
        final List<Annotation> annotationList = Arrays.stream(annotations)
                .filter(item -> Filler.PARAMS.containsKey(item.annotationType()))
                .collect(Collectors.toList());
        return annotationList.isEmpty() ? null : annotationList.get(Values.IDX);
    }

    private Object getDefault(final Annotation[] annotations,
                              final Class<?> paramType) {
        final List<Annotation> annotationList = Arrays.stream(annotations)
                .filter(item -> item.annotationType() == DefaultValue.class)
                .collect(Collectors.toList());
        return Fn.getSemi(annotationList.isEmpty(), LOGGER,
                Fn::nil,
                () -> {
                    final Annotation annotation = annotationList.get(Values.IDX);
                    return ZeroSerializer.getValue(paramType,
                            Instance.invoke(annotation, "value"));
                });
    }
}
