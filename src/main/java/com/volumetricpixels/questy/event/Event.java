/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.volumetricpixels.questy.event;

/**
 * A simple event which can be fired from {@link EventManager}.
 */
public abstract class Event {
    /**
     * Whether this event has been cancelled.
     */
    private boolean cancelled;

    /**
     * Sets the cancelled state of this {@link Event} to {@code cancelled}.
     *
     * Events wishing to be cancellable should override this method as below, to
     * ensure that there are no conflicts.
     *
     * <code>
     *     public void setCancelled(boolean cancelled) {
     *         super.setCancelled(cancelled);
     *     }
     * </code>
     *
     * @param cancelled the new cancelled state of the event
     */
    protected void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public boolean isCancelled() {
        return cancelled;
    }
}
