/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.volumetricpixels.questy.questy.event;

import com.volumetricpixels.questy.event.Event;
import com.volumetricpixels.questy.event.EventManager;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A simple {@link EventManager}, which stores {@link SimpleListenerHandle} objects
 * and sends {@link Event}s their way when a relevant {@link Event} is fired via
 * {@link #fire(Event)}.
 */
public class SimpleEventManager implements EventManager {
    /**
     * All registered {@link Object}s, each contained within a {@link
     * SimpleListenerHandle} object.
     */
    private final Set<SimpleListenerHandle> listeners;

    /**
     * Constructs a new EventManager with no registered {@link Object}s.
     */
    public SimpleEventManager() {
        listeners = new HashSet<>();
    }

    /**
     * {@inheritDoc}
     */
    public boolean register(Object listener) {
        return listeners.add(new SimpleListenerHandle(listener));
    }

    /**
     * {@inheritDoc}
     */
    public void unregister(Object listener) {
        Iterator<SimpleListenerHandle> it = listeners.iterator();
        while (it.hasNext()) {
            SimpleListenerHandle handle = it.next();
            if (handle.getListener() == listener) {
                it.remove();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public <T extends Event> T fire(T event) {
        listeners.forEach((listener) -> listener.handle(event));
        return event;
    }
}
