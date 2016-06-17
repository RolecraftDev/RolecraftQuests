/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.volumetricpixels.questy.objective;

/**
 * Represents a single possible outcome for an {@link Objective}.
 */
public final class Outcome {
    private final String name;
    private final String description;
    private final String finishMessage;
    private final String type;
    private final Objective next;

    public Outcome(String name, String description, String finishMessage,
            String type, Objective next) {
        this.name = name;
        this.description = description;
        this.finishMessage = finishMessage;
        this.type = type;
        this.next = next;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getFinishMessage() {
        return finishMessage;
    }

    public String getType() {
        return type;
    }

    public Objective getNext() {
        return next;
    }
}
