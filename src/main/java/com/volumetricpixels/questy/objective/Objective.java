/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.volumetricpixels.questy.objective;

import com.volumetricpixels.questy.Quest;
import com.volumetricpixels.questy.QuestInstance;

/**
 * Represents a single objective in a {@link Quest}.
 */
public final class Objective {
    /**
     * The name of this {@link Objective}. This should be both human-readable
     * and unique within this Objective's {@link Quest}, i.e there should be no
     * other Objective with the same name in the same {@link Quest}.
     */
    private final String name;
    /**
     * The human-readable description of this {@link Objective}.
     */
    private final String description;
    /**
     * All of the potential {@link Outcome}s of this {@link Objective}.
     */
    private final Outcome[] outcomes;

    public Objective(String name, String description, Outcome[] outcomes) {
        this.name = name;
        this.description = description;
        this.outcomes = outcomes;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Outcome[] getOutcomes() {
        return outcomes.clone();
    }

    /**
     * Gets the amount of outcomes possible for the completion of this {@link
     * Objective}. Note that this should <em>always</em> be preferred to using
     * {@link #getOutcomes()} followed by a read of the length field, as the
     * aforementioned method clones the array.
     *
     * @return the amount of Outcomes possible for this Objective
     */
    public int getAmtOutcomes() {
        return outcomes.length;
    }

    public Outcome getOutcome(String outcome) {
        for (Outcome curOutcome : outcomes) {
            if (curOutcome.getName().equals(outcome)) {
                return curOutcome;
            }
        }
        return null;
    }

    /**
     * Don't call outside of {@link
     * ObjectiveProgress#ObjectiveProgress(QuestInstance, Objective)}
     */
    void populateOutcomeProgresses(QuestInstance qst, OutcomeProgress[] array) {
        // is always the case without Questy bugs, hence assert
        assert array.length == outcomes.length;

        for (int i = 0; i < array.length; i++) {
            array[i] = new OutcomeProgress(qst, outcomes[i]);
        }
    }
}
