/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.volumetricpixels.questy.objective;

import com.volumetricpixels.questy.QuestInstance;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Stores progresses for each available {@link Outcome} of one particular
 * {@link Objective}, in the form of {@link OutcomeProgress} objects.
 */
public final class ObjectiveProgress {
    /**
     * The {@link QuestInstance} which this progress relates to.
     */
    private final QuestInstance quest;
    /**
     * The {@link Objective} which this progress is for.
     */
    private final Objective objective;
    /**
     * All of the {@link OutcomeProgress} objects for the {@link Objective} this
     * {@link ObjectiveProgress} stores progress for.
     */
    private OutcomeProgress[] outcomeProgresses;

    public ObjectiveProgress(QuestInstance quest, Objective objective) {
        this.quest = quest;
        this.objective = objective;

        outcomeProgresses = new OutcomeProgress[objective.getAmtOutcomes()];
        objective.populateOutcomeProgresses(quest, outcomeProgresses);
    }

    public ObjectiveProgress(QuestInstance quest, String serialized) {
        this.quest = quest;

        String[] split = serialized.split("//");
        Objective obj = quest.getInfo().getObjective(split[0]);
        this.objective = obj;

        String[] progresses = split[1].split("&&");
        outcomeProgresses = new OutcomeProgress[progresses.length];
        for (int idx = 0; idx < progresses.length; idx++) {
            outcomeProgresses[idx] = new OutcomeProgress(quest, obj,
                    progresses[idx]);
        }
    }

    /**
     * Gets the {@link QuestInstance} which this progress relates to.
     *
     * @return the related {@link QuestInstance}
     */
    public QuestInstance getQuest() {
        return quest;
    }

    /**
     * Gets the {@link Objective} which this object stores progress for.
     *
     * @return the {@link Objective} for this progress
     */
    public Objective getInfo() {
        return objective;
    }

    public Set<OutcomeProgress> getOutcomeProgresses() {
        return new HashSet<>(Arrays.asList(this.outcomeProgresses));
    }

    /**
     * Serializes this {@link ObjectiveProgress} into a single String.
     *
     * @return a serialized form of this {@link ObjectiveProgress}
     */
    public String serialize() {
        StringBuilder res = new StringBuilder(objective.getName() + "//");
        for (OutcomeProgress progress : outcomeProgresses) {
            res.append(progress.serialize()).append("&&");
        }
        // remove last &&
        res.setLength(res.length() - 2);
        return res.toString();
    }
}
