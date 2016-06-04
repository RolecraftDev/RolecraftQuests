/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.volumetricpixels.questy.questy.loader;

import org.yaml.snakeyaml.Yaml;

import com.volumetricpixels.questy.Quest;
import com.volumetricpixels.questy.QuestManager;
import com.volumetricpixels.questy.loading.QuestBuilder;
import com.volumetricpixels.questy.loading.QuestBuilder.ObjectiveBuilder;
import com.volumetricpixels.questy.loading.QuestBuilder.ObjectiveBuilder.OutcomeBuilder;
import com.volumetricpixels.questy.loading.QuestLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Loads YML quests.
 */
public class YMLQuestLoader implements QuestLoader {
    private final QuestManager questManager;

    public YMLQuestLoader(QuestManager questManager) {
        this.questManager = questManager;
    }

    @Override
    public Map<String, Quest> loadQuests(File directory) {
        if (directory == null || !directory.isDirectory()) {
            return null;
        }

        Yaml yaml = new Yaml();
        Map<String, Quest> result = new HashMap<>();
        // iterate through all files in the directory which end with .yml
        for (File file : directory.listFiles(fl -> checkExtension(fl, "yml"))) {
            try {
                Quest loaded = loadQuest(yaml, new FileInputStream(file));
                if (loaded != null) {
                    result.put(loaded.getName(), loaded);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    public Quest loadQuest(Yaml yaml, InputStream stream) {
        Map<?, ?> map = (Map<?, ?>) yaml.load(stream);

        QuestBuilder builder = QuestBuilder.begin(questManager, map.get("name")
                .toString());
        builder.description(map.get("description").toString());

        Map<?, ?> objectives = (Map) map.get("objectives");
        for (Entry<?, ?> objEntry : objectives.entrySet()) {
            ObjectiveBuilder obj = builder
                    .objective(objEntry.getKey().toString());
            Map<?, ?> objMap = (Map) objEntry.getValue();
            obj.description(objMap.get("description").toString());

            Map<?, ?> outcomes = (Map) objMap.get("outcomes");
            for (Entry<?, ?> ocEntry : outcomes.entrySet()) {
                OutcomeBuilder oc = obj
                        .outcome(ocEntry.getKey().toString());
                Map<?, ?> ocMap = (Map) ocEntry.getValue();
                oc.description(ocMap.get("description").toString());
                oc.type(ocMap.get("type").toString());

                Object next = ocMap.get("next");
                if (next != null) {
                    oc.next(builder.objective(next.toString()));
                }
            }
        }

        List<?> prerequisites = (List) map.get("prerequisites");
        if (prerequisites != null) {
            for (Object obj : prerequisites) {
                builder.requireCompletion(obj.toString());
            }
        }

        List<?> rewards = (List) map.get("rewards");
        if (rewards != null) {
            for (Object obj : rewards) {
                builder.reward(obj.toString());
            }
        }

        return builder.build();
    }
}
