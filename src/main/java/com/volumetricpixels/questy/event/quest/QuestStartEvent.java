/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.volumetricpixels.questy.event.quest;

import com.volumetricpixels.questy.QuestInstance;

public class QuestStartEvent extends QuestEvent {
    // TODO: add causes for quest starting
    public QuestStartEvent(QuestInstance quest) {
        super(quest);
    }
}
