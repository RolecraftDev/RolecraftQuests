/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.volumetricpixels.questy.storage;

import java.util.Map;

/**
 * Stores data about the progression of players into quests and quest chains.
 */
public interface ProgressStore {
    /**
     * Stores the given quest progression {@code data} for the given {@code
     * player}. The {@code data} parameter is a {@link Map} of quest names to
     * serialized progression data for the quest.
     *
     * @param data the data to store
     */
    void saveCurrentQuestData(Map<String, Map<String, String>> data);

    /**
     * Loads all stored quest progression data from the data store.
     *
     * @return a {@link Map} of player names to stored quest progression data
     */
    Map<String, Map<String, String>> loadCurrentQuestData();

    /**
     * Stores the given quest progression {@code data} for the given {@code
     * player}. The {@code data} parameter is a {@link Map} of quest names to
     * serialized progression data for the quest.
     *
     * @param data the data to store
     */
    void saveCompletedQuestData(Map<String, Map<String, String>> data);

    /**
     * Loads all stored quest progression data from the data store.
     *
     * @return a {@link Map} of player names to stored quest progression data
     */
    Map<String, Map<String, String>> loadCompletedQuestData();
}
