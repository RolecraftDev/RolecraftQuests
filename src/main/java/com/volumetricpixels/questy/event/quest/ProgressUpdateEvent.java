/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.volumetricpixels.questy.event.quest;

import com.volumetricpixels.questy.QuestInstance;
import com.volumetricpixels.questy.objective.Outcome;
import com.volumetricpixels.questy.objective.OutcomeProgress;

/**
 * Called whenever the progress for a {@link OutcomeProgress} is updated to a
 * new value. This allows tracking completion / progress of a {@link Outcome}.
 * Note that changes to the outcome should not be made while listening to this
 * event.
 */
public class ProgressUpdateEvent extends QuestEvent {
    private final OutcomeProgress outcome;
    private final Object newProgress;

    public ProgressUpdateEvent(QuestInstance quest, OutcomeProgress outcome) {
        super(quest);
        this.outcome = outcome;
        this.newProgress = outcome.getProgress();
    }

    public OutcomeProgress getOutcome() {
        return outcome;
    }

    public String getOutcomeType() {
        return getOutcomeInfo().getType();
    }

    public Outcome getOutcomeInfo() {
        return getOutcome().getInfo();
    }

    public Object getNewProgress() {
        return newProgress;
    }
}
