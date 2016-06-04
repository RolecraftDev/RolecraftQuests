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
 * Called when a {@link QuestInstance} is completed.
 */
public class QuestCompleteEvent extends QuestEvent {
    private final OutcomeProgress outcome;

    public QuestCompleteEvent(QuestInstance quest, OutcomeProgress outcome) {
        super(quest);
        this.outcome = outcome;
    }

    public OutcomeProgress getOutcome() {
        return outcome;
    }

    public Outcome getOutcomeInfo() {
        return getOutcome().getInfo();
    }
}
