package pw.stamina.munition.event.annotated;

import pw.stamina.munition.event.annotated.listeners.KeyedListener;
import pw.stamina.munition.event.annotated.listeners.Listener;
import pw.stamina.munition.event.core.Event;
import pw.stamina.munition.event.core.ScanFailedException;
import pw.stamina.munition.event.core.registration.Registration;
import pw.stamina.munition.event.core.registration.registry.Registry;
import pw.stamina.munition.event.core.routing.Router;
import pw.stamina.munition.event.core.scanning.ScanResult;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author Mark Johnson
 */
public class CachingAnnotatedEventBus<K, E, R> extends AbstractAnnotatedEventBus<K, Event<E>, R> {

    private final ConcurrentMap<R, List<Object>> fieldContentsCache = new ConcurrentHashMap<>();

    public CachingAnnotatedEventBus(final Registry<K, BiConsumer<K, Event<E>>> registrations, final Router<K, Event<E>> router) {
        super(registrations, router);
    }

    @Override
    public ScanResult<K, Event<E>> scan(final R registrant) throws ScanFailedException {
        return fieldContentsCache.computeIfAbsent(registrant, reg ->
                Arrays.stream(reg.getClass().getDeclaredFields())
                    .filter(CachingAnnotatedEventBus::checkIfValidFieldAndEnsureConsistency)
                    .map(field -> getFieldContents(field, reg))
                    .collect(Collectors.toList()))
                .stream()
                .map(this::tryCreatingRegistrationFromFieldContent)
                .collect(new ScanResultCollector<>());
    }

    private Object getFieldContents(final Field field, final R registrant) {
        AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
            field.setAccessible(true);
            return null;
        });
        try {
            return field.get(registrant);
        } catch (final IllegalAccessException e) {
            throw new ScanFailedException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private Registration<? extends K, BiConsumer<K, Event<E>>> tryCreatingRegistrationFromFieldContent(final Object fieldContents) {
        if (fieldContents instanceof Listener) {
            return on(((Listener<K, Event<E>>) fieldContents).getSelector(), ((Consumer<Event<E>>) fieldContents));
        } else {
            return on(((KeyedListener<K, E>) fieldContents).getSelector(), ((BiConsumer<K, Event<E>>) fieldContents));
        }
    }

    private static boolean checkIfValidFieldAndEnsureConsistency(final Field field) {
        if (!field.isAnnotationPresent(Reactive.class)) {
            return false;
        } else {
            if (!Listener.class.isAssignableFrom(field.getType()) && !KeyedListener.class.isAssignableFrom(field.getType())) {
                throw new ScanFailedException(String.format("@Reactive-annotated field must have a type / supertype of one of {%s}",
                        String.join(", ", Listener.class.getCanonicalName(), KeyedListener.class.getCanonicalName())));
            }
            if (!Modifier.isFinal(field.getModifiers())) {
                throw new ScanFailedException("@Reactive-annotated fields must be marked as final to be registered");
            }
        }
        return true;
    }
}
