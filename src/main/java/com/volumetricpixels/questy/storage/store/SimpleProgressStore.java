/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.volumetricpixels.questy.storage.store;

import com.volumetricpixels.questy.storage.ProgressStore;
import com.volumetricpixels.questy.util.Serialization;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple implementation of a {@link ProgressStore} which uses the Java serialization API to write Map objects to files. Not advisable for use in a production environment, this is
 * simply a way of testing the framework and alternatives will be implemented before release.
 */
// this file doesn't really need documenting beyond the above
public class SimpleProgressStore implements ProgressStore {
    private final File currentStore;
    private final File completedStore;

    public SimpleProgressStore(File storageDirectory) {
        this(new File(storageDirectory, "current"),
                new File(storageDirectory, "completed"));
    }

    public SimpleProgressStore(File currentStore, File completedStore) {
        this.currentStore = currentStore;
        this.completedStore = completedStore;
    }

    @Override
    public void saveCurrentQuestData(Map<String, Map<String, String>> data) {
        doSaveData(currentStore, data);
    }

    @Override
    public Map<String, Map<String, String>> loadCurrentQuestData() {
        return doLoadData(currentStore);
    }

    @Override
    public void saveCompletedQuestData(Map<String, Map<String, String>> data) {
        doSaveData(completedStore, data);
    }

    @Override
    public Map<String, Map<String, String>> loadCompletedQuestData() {
        return doLoadData(completedStore);
    }

    private void doSaveData(File file, Map<String, Map<String, String>> data) {
        try {
            file.getParentFile().mkdirs();
            file.delete();
            file.createNewFile();
            Serialization.writeObject(file, data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Map<String, Map<String, String>> doLoadData(File file) {
        if (!file.exists()) {
            // account for load being called before the first save
            return new HashMap<>();
        }

        try {
            return (Map) Serialization.readObject(file);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
