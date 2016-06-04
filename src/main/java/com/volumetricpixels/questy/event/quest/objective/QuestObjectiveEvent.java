/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.volumetricpixels.questy.event.quest.objective;

import com.volumetricpixels.questy.QuestInstance;
import com.volumetricpixels.questy.event.quest.QuestEvent;
import com.volumetricpixels.questy.objective.Objective;
import com.volumetricpixels.questy.objective.ObjectiveProgress;

/**
 * Represents an event related to a specific {@link Objective} within a single
 * {@link QuestInstance}.
 */
public abstract class QuestObjectiveEvent extends QuestEvent {
    /**
     * The {@link ObjectiveProgress} relevant to this event.
     */
    private final ObjectiveProgress objective;

    protected QuestObjectiveEvent(QuestInstance quest,
            ObjectiveProgress objective) {
        super(quest);
        this.objective = objective;
    }

    /**
     * Gets the {@link ObjectiveProgress} which this event is related to.
     *
     * @return the {@link ObjectiveProgress} relevant to this event
     */
    public ObjectiveProgress getObjective() {
        return objective;
    }

    /**
     * Gets the {@link Objective} information for the {@link ObjectiveProgress}
     * this event is related to. Note that this method is equivalent to calling
     * {@code questObjectiveEvent.getObjective().getInfo()}.
     *
     * @return the {@link Objective} information for the objective this event is
     *         related to
     */
    public Objective getObjectiveInfo() {
        return getObjective().getInfo();
    }
}
