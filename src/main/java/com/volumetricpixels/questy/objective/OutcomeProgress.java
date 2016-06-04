/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.volumetricpixels.questy.objective;

import com.volumetricpixels.questy.QuestInstance;
import com.volumetricpixels.questy.event.quest.ProgressUpdateEvent;
import com.volumetricpixels.questy.util.Serialization;

public final class OutcomeProgress {
    /**
     * The {@link QuestInstance} this outcome is a part of.
     */
    private final QuestInstance quest;
    /**
     * The {@link Outcome} which this object stores progress for.
     */
    private final Outcome outcome;

    /**
     * An {@link Object} representing the actual progress towards the Outcome
     * for it's {@link ObjectiveProgress}. Only immutable types should be used
     * for this object, as the {@link ProgressUpdateEvent} system depends on
     * {@link #setProgress(Object)} being called whenever the progress is
     * updated.
     */
    private Object progress = "NULL";

    public OutcomeProgress(QuestInstance quest, Outcome outcome) {
        this.quest = quest;
        this.outcome = outcome;
    }

    public OutcomeProgress(QuestInstance quest, Objective objective,
            String serialized) {
        this.quest = quest;
        String[] split = serialized.split("==");
        this.outcome = objective.getOutcome(split[0]);

        setProgressSilent(Serialization.handleCommonTypes(split[1]));
    }

    public Outcome getInfo() {
        return outcome;
    }

    public Object getProgress() {
        return progress;
    }

    public void setProgress(Object progress) {
        setProgressSilent(progress);

        quest.getInfo().getQuestManager().getEventManager().fire(
                new ProgressUpdateEvent(quest, this));
    }

    private void setProgressSilent(Object progress) {
        this.progress = progress;
    }

    public String serialize() {
        return outcome.getName() + "==" + progress.toString();
    }
}
