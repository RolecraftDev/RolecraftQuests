/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.volumetricpixels.questy.event.quest.objective;

import com.volumetricpixels.questy.QuestInstance;
import com.volumetricpixels.questy.objective.ObjectiveProgress;
import com.volumetricpixels.questy.objective.Outcome;
import com.volumetricpixels.questy.objective.OutcomeProgress;

/**
 * Called when an {@link ObjectiveProgress} reached completed state.
 */
public class ObjectiveCompleteEvent extends QuestObjectiveEvent {
    /**
     * The {@link OutcomeProgress} relevant to this event.
     */
    private final OutcomeProgress outcome;

    public ObjectiveCompleteEvent(QuestInstance quest,
            ObjectiveProgress objective, OutcomeProgress outcome) {
        super(quest, objective);
        this.outcome = outcome;
    }

    /**
     * Gets the {@link OutcomeProgress} relevant to this event.
     *
     * @return the {@link OutcomeProgress} relevant to this event
     */
    public OutcomeProgress getOutcome() {
        return outcome;
    }

    /**
     * Gets {@link Outcome} information for the outcome this event is related
     * to.
     *
     * @return the {@link Outcome} for the outcome this event is related to
     */
    public Outcome getOutcomeInfo() {
        return getOutcome().getInfo();
    }
}
