/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.volumetricpixels.questy;

import com.volumetricpixels.questy.event.quest.objective.ObjectiveCompleteEvent;
import com.volumetricpixels.questy.event.quest.objective.ObjectiveFailEvent;
import com.volumetricpixels.questy.event.quest.objective.ObjectiveStartEvent;
import com.volumetricpixels.questy.objective.Objective;
import com.volumetricpixels.questy.objective.ObjectiveProgress;
import com.volumetricpixels.questy.objective.OutcomeProgress;

import static com.volumetricpixels.questy.util.General.appendIf;

/**
 * Represents an 'instance' of a {@link Quest}. A {@link QuestInstance} holds
 * the {@link Quest} object it is an instance of, but also holds the player
 * doing the quest and data about said player's progression through the quest.
 */
public final class QuestInstance {
    /**
     * The {@link Quest} which this QuestInstance is an instance of.
     */
    private final Quest quest;
    /**
     * The quester embarking on the quest.
     */
    private final String quester;
    /**
     * The progress for all of the {@link Quest}'s {@link Objective}s.
     */
    private final ObjectiveProgress[] objectiveProgresses;

    /**
     * The array index of the current {@link ObjectiveProgress}.
     */
    private int current;

    public QuestInstance(Quest quest, String quester) {
        this.quest = quest;
        this.quester = quester;

        objectiveProgresses = new ObjectiveProgress[quest.getAmtObjectives()];
        quest.populateObjectiveProgresses(this, objectiveProgresses);
        current = 0;
    }

    public QuestInstance(QuestManager questManager, String quester,
            String serialized) {
        this.quester = quester;

        String[] split = serialized.split("_");
        this.quest = questManager.getQuest(split[0]);
        String[] progressions = split[1].split("%");
        objectiveProgresses = new ObjectiveProgress[progressions.length];
        for (int i = 0; i < progressions.length; i++) {
            String serial = progressions[i];
            objectiveProgresses[i] = new ObjectiveProgress(this, serial);
            if (serial.endsWith("<c>")) {
                current = i;
            }
        }
    }

    /**
     * Should be called whenever it is detected that an {@link Objective} has
     * been completed.
     *
     * This method handles advancing the {@link Objective} of this {@link
     * QuestInstance} to the next one (or completing it if there are no more
     * {@link Objective}s) and fires {@link ObjectiveCompleteEvent}, {@link
     * ObjectiveStartEvent} as necessary. This method also invokes {@link
     * QuestManager#completeQuest(QuestInstance, OutcomeProgress)} if necessary,
     * and thus that method doesn't need to be called manually.
     *
     * @param objective the {@link ObjectiveProgress} of the completed Objective
     * @param outcome the {@link OutcomeProgress} which was the Objective result
     */
    public void objectiveComplete(ObjectiveProgress objective,
            OutcomeProgress outcome) {
        Objective next = outcome.getInfo().getNext();
        if (next == null) {
            quest.getQuestManager().completeQuest(this, outcome);
            return;
        }

        ObjectiveProgress temp = getProgress(next);
        if (temp == null) {
            return;
        }

        ObjectiveCompleteEvent e = quest.getQuestManager().getEventManager()
                .fire(new ObjectiveCompleteEvent(this, objective, outcome));
        current = getIndex(temp);
        quest.getQuestManager().getEventManager()
                .fire(new ObjectiveStartEvent(this, temp, e));
    }

    /**
     * Should be called when it is detected that an {@link Objective} has been
     * failed.
     *
     * This method automatically resets the {@link Objective} and abandons the
     * {@link Quest} if {@code endQuest == true}. Events for abandoning the
     * objective and failing the quest if applicable are fired.
     *
     * @param objective the {@link ObjectiveProgress} for the failed objective
     * @param endQuest whether the failure ends the Quest
     */
    public void objectiveFailed(ObjectiveProgress objective, boolean endQuest) {
        if (endQuest) {
            quest.getQuestManager().getEventManager().fire(
                    new ObjectiveFailEvent(this, objective));
            quest.getQuestManager().abandonQuest(this);
            return;
        }

        for (int i = 0; i < objectiveProgresses.length; i++) {
            if (objectiveProgresses[i] == objective) {
                objectiveProgresses[i] = new ObjectiveProgress(this,
                        objective.getInfo());
                quest.getQuestManager().getEventManager().fire(
                        new ObjectiveFailEvent(this, objective));
                break; // there are no duplicates
            }
        }
    }

    /**
     * Gets the {@link Quest} for this {@link QuestInstance}, which contains the
     * basic information about the Quest.
     *
     * @return the {@link Quest} this object represents an instance of
     */
    public Quest getInfo() {
        return quest;
    }

    public String getQuester() {
        return quester;
    }

    public ObjectiveProgress[] getObjectiveProgresses() {
        return objectiveProgresses.clone();
    }

    public ObjectiveProgress getProgress(Objective objective) {
        for (ObjectiveProgress cur : objectiveProgresses) {
            if (cur.getInfo() == objective) {
                return cur;
            }
        }
        return null;
    }

    public ObjectiveProgress getCurrentObjective() {
        return objectiveProgresses[current];
    }

    /**
     * Serializes the progression into the {@link Quest} which is contained by
     * this QuestInstance.
     *
     * @return the serialized progression into the Quest for this QuestInstance
     */
    public String serializeProgression() {
        StringBuilder result = new StringBuilder(quest.getName()).append("_");
        for (ObjectiveProgress progress : objectiveProgresses) {
            // append <c> if it is the current objective
            appendIf(getIndex(progress) == current, result.append(
                    progress.serialize()), "<c>").append("%");
        }
        result.setLength(result.length() - 1);
        return result.toString();
    }

    private int getIndex(ObjectiveProgress progress) {
        for (int i = 0; i < objectiveProgresses.length; i++) {
            if (objectiveProgresses[i] == progress) {
                return i;
            }
        }
        return -1;
    }
}
