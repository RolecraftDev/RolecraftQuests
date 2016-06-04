/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.volumetricpixels.questy.event;

/**
 * A manager used to send {@link Event}s to relevant listeners.
 */
public interface EventManager {
    /**
     * Registers the given {@link Object} to this {@link EventManager}.
     *
     * It is required by any implementation of this method that all methods
     * within the listener annotated with {@link Listen} are considered for
     * registration as a listener method, although it is not guaranteed that all
     * will be registered as there may be other restrictions on the method
     * signature.
     *
     * @param listener the {@link Object} to register
     * @return whether the given {@link Object} was successfully registered
     */
    boolean register(Object listener);

    /**
     * Unregisters the given listener {@link Object}.
     *
     * @param listener the {@link Object} to unregister
     */
    void unregister(Object listener);

    /**
     * Fires the given {@link Event}, directing it to all registered {@link
     * Object}s which are subscribed to the given event type.
     *
     * @param event the {@link Event} to fire
     * @param <T> the type of {@link Event} to fire, and therefore return
     * @return the given {@link Event}
     */
    <T extends Event> T fire(T event);
}
