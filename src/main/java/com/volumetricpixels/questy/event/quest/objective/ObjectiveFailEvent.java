/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.volumetricpixels.questy.event.quest.objective;

import com.volumetricpixels.questy.QuestInstance;
import com.volumetricpixels.questy.objective.ObjectiveProgress;

public class ObjectiveFailEvent extends QuestObjectiveEvent {
    public ObjectiveFailEvent(QuestInstance quest,
            ObjectiveProgress objective) {
        super(quest, objective);
    }
}
