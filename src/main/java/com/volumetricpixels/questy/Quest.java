/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.volumetricpixels.questy;

import com.volumetricpixels.questy.objective.Objective;
import com.volumetricpixels.questy.objective.ObjectiveProgress;

import java.util.function.Predicate;

/**
 * Represents the 'outline' of a quest. There is a single {@link Quest} object
 * created for each scripted or configured quest which is loaded.
 */
public final class Quest {
    /**
     * The {@link QuestManager} this Quest is attached to.
     */
    private final QuestManager questManager;
    /**
     * The name of this Quest, used for identification as well as a name which
     * can be read by humans, so it should always be unique.
     */
    private final String name;
    /**
     * The description of this Quest, human-readable.
     */
    private final String description;
    /**
     * All of the {@link Objective}s which are a part of this Quest.
     */
    private final Objective[] objectives;
    /**
     * A {@link Predicate} specifying the test for whether a quester can embark
     * on this Quest by checking the requirements.
     */
    private final Predicate<String> predicate;

    /**
     * Main Quest constructor. Should primarily be accessed by {@link
     * com.volumetricpixels.questy.loading.QuestBuilder#build()}
     * but can be used elsewhere.
     *
     * This constructor automatically attaches the Quest to the given {@link
     * QuestManager}.
     *
     * @param questManager the {@link QuestManager} to attach the Quest to
     * @param name the unique and human-readable name of the Quest
     * @param description the human-readable description of the Quest
     * @param objectives the {@link Objective}s present in the Quest
     * @param prerequisites quests which must be completed to start the Quest
     */
    public Quest(QuestManager questManager, String name, String description,
            Objective[] objectives, String[] prerequisites) {
        this.questManager = questManager;
        this.name = name;
        this.description = description;
        this.objectives = objectives;

        predicate = quester -> {
            if (prerequisites != null && prerequisites.length > 0) {
                for (String qst : prerequisites) {
                    if (!questManager.hasCompleted(questManager.getQuest(qst),
                            quester)) {
                        return false;
                    }
                }
            }
            return true;
        };

        questManager.addQuest(this);
    }

    /**
     * Gets the {@link QuestManager} this Quest is attached to.
     *
     * @return this Quest's {@link QuestManager}
     */
    public QuestManager getQuestManager() {
        return questManager;
    }

    /**
     * Gets the name of this Quest. Note that this should always be both unique
     * AND human-readable.
     *
     * @return the name of this Quest
     */
    public String getName() {
        return name;
    }

    /**
     * Gets a human-readable description of the Quest.
     *
     * @return a human-readable description of this Quest
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets an array of all {@link Objective}s which are part of this Quest.
     * Note that modifying the returned array will have NO effect on the actual
     * objectives of this Quest, as a clone of the actual array is used.
     *
     * @return an array of {@link Objective}s for this quest
     */
    public Objective[] getObjectives() {
        return objectives.clone();
    }

    /**
     * Checks whether the given {@code quester} satisfies the prerequisites for
     * starting this {@link Quest}.
     *
     * @param quester the person to check
     * @return whether the given quester satisfies this Quest's prerequisites
     * @see {@link #predicate}
     * @see {@link Predicate#test(Object)}
     */
    public boolean satisfiesPrerequisites(String quester) {
        return predicate.test(quester);
    }

    /**
     * Gets the amount of objectives in this Quest. A call to this method should
     * always be used over {@code {@link #getObjectives()}.length} as the getter
     * for the objective calls {@link Cloneable#clone()} on the objectives array
     * where this method simply returns the length of the actual array.
     *
     * @return the amount of objectives this Quest has
     */
    public int getAmtObjectives() {
        return objectives.length;
    }

    /**
     * Gets the {@link Objective} with the given name within this Quest.
     *
     * @param name the name of the {@link Objective} to get
     * @return the {@link Objective} with the given name within this Quest
     */
    public Objective getObjective(String name) {
        for (Objective objective : objectives) {
            if (objective.getName().equals(name)) {
                return objective;
            }
        }
        return null;
    }

    /**
     * Creates a new {@link QuestInstance} for this Quest, for the given {@code
     * quester}. This automatically invokes {@link
     * QuestManager#startQuest(QuestInstance)}.
     *
     * @param quester the person embarking on the Quest
     * @return a new {@link QuestInstance} for this Quest and the given quester
     */
    public QuestInstance start(String quester) {
        QuestInstance result = new QuestInstance(this, quester);
        questManager.startQuest(result);
        return result;
    }

    /**
     * Don't call outside of {@link QuestInstance#QuestInstance(Quest, String)}
     */
    void populateObjectiveProgresses(QuestInstance instance,
            ObjectiveProgress[] progresses) {
        assert progresses.length == objectives.length;

        for (int i = 0; i < objectives.length; i++) {
            progresses[i] = new ObjectiveProgress(instance, objectives[i]);
        }
    }
}
