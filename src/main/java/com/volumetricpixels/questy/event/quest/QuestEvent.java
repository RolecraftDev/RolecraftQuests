/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.volumetricpixels.questy.event.quest;

import com.volumetricpixels.questy.Quest;
import com.volumetricpixels.questy.QuestInstance;
import com.volumetricpixels.questy.event.Event;

/**
 * Represents a {@link Quest}-related event. Used for listening for many things
 * which happen during a {@link Quest}, which can be hooked into to perform any
 * action.
 */
public abstract class QuestEvent extends Event {
    /**
     * The {@link QuestInstance} this {@link QuestEvent} relates to.
     */
    private final QuestInstance quest;

    protected QuestEvent(QuestInstance quest) {
        this.quest = quest;
    }

    /**
     * Gets the {@link QuestInstance} this event is related to.
     *
     * @return this event's relevant {@link QuestInstance}
     */
    public QuestInstance getQuest() {
        return quest;
    }

    /**
     * Gets the {@link Quest} information which is relevant to the {@link
     * QuestInstance} this event is related to. This is equivalent to calling
     * {@code questEvent.getQuest().getInfo()}.
     *
     * @return the {@link Quest} information for the relevant {@link
     *         QuestInstance}
     * @see {@link QuestInstance#getInfo()}
     */
    public Quest getQuestInfo() {
        return getQuest().getInfo();
    }

    /**
     * Gets the player doing the quest. This method is equivalent to calling
     * {@code questEvent.getQuest().getQuester()}.
     *
     * @return the player doing the {@link QuestInstance} relevant to this event
     */
    public String getQuester() {
        return getQuest().getQuester();
    }
}
