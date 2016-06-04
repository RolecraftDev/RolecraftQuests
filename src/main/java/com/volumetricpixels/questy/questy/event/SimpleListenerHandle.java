/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.volumetricpixels.questy.questy.event;

import com.volumetricpixels.questy.event.Event;
import com.volumetricpixels.questy.event.Listen;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Wraps a listener Object to direct {@link Event}s its way.
 */
public final class SimpleListenerHandle {
    /**
     * The actual listener {@link Object}.
     */
    private final Object listener;
    /**
     * A {@link Map} of {@link Event} subclasses to {@link Set}s of methods
     * which listen for the event type they are mapped from.
     */
    private final Map<Class<? extends Event>, Set<MethodHandle>> eventHandlers;

    public SimpleListenerHandle(Object listener) {
        this.listener = listener;
        this.eventHandlers = new HashMap<>();

        // check every method in the given Listener
        for (Method meth : listener.getClass().getDeclaredMethods()) {
            Listen eh = meth.getAnnotation(Listen.class);
            // if it isn't an EventHandler or it doesn't have one parameter
            // (which should be the event being listened for)
            if (eh == null || meth.getParameterCount() != 1) {
                continue;
            }

            Class<?> parameter = meth.getParameterTypes()[0];
            // if the parameter isn't Event or an extension of it
            if (!Event.class.isAssignableFrom(parameter)) {
                continue;
            }

            Class<? extends Event> evtClass = parameter.asSubclass(Event.class);
            try {
                // use MethodHandle over Method for actual calls as it is faster
                MethodHandle handle = MethodHandles.lookup().unreflect(meth);
                Set<MethodHandle> handlers = eventHandlers.get(evtClass);
                if (handlers == null) {
                    // this is the first handler for that event type - in
                    // practice this will be most of the time
                    handlers = new HashSet<>();
                    eventHandlers.put(evtClass, handlers);
                }

                // add the MethodHandle to the Set of them
                handlers.add(handle);
            } catch (IllegalAccessException e) {
                // in theory shouldn't happen unless someone is dumb enough to
                // implement Listener really badly
                e.printStackTrace();
            }
        }
    }

    /**
     * Handles the given {@link Event}, directing it to each registered {@link
     * Object} which has an {@link Listen}-annotated method which handles the
     * given {@link Event}'s type.
     *
     * The {@link List} contained by the returned {@link Optional} will contain
     * all {@link Throwable}s thrown by {@link MethodHandle#invoke(Object...)}.
     * In most cases, this will simply be {@link Optional#empty()}, unless a
     * {@link Throwable}(s) is actually thrown.
     *
     * @param event the {@link Event} to call
     * @return all {@link Throwable}s thrown while calling the {@link Event}
     */
    public Optional<List<Throwable>> handle(Event event) {
        Set<MethodHandle> handles = eventHandlers.get(event.getClass());
        if (handles == null || handles.isEmpty()) {
            return Optional.empty();
        }

        List<Throwable> problems = new ArrayList<>();
        for (MethodHandle handle : handles) {
            try {
                handle.invoke(listener, event);
            } catch (Throwable throwable) {
                problems.add(throwable);
            }
        }

        return problems.isEmpty() ? Optional.empty() : Optional.of(problems);
    }

    public Object getListener() {
        return listener;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof SimpleListenerHandle
                && listener == ((SimpleListenerHandle) other).listener;
    }
}
