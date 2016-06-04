/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.volumetricpixels.questy.questy;

import com.volumetricpixels.questy.Quest;
import com.volumetricpixels.questy.QuestInstance;
import com.volumetricpixels.questy.QuestManager;
import com.volumetricpixels.questy.event.EventManager;
import com.volumetricpixels.questy.event.quest.QuestAbandonEvent;
import com.volumetricpixels.questy.event.quest.QuestCompleteEvent;
import com.volumetricpixels.questy.event.quest.QuestStartEvent;
import com.volumetricpixels.questy.event.quest.objective.ObjectiveStartEvent;
import com.volumetricpixels.questy.loading.QuestBuilder;
import com.volumetricpixels.questy.loading.QuestLoader;
import com.volumetricpixels.questy.objective.OutcomeProgress;
import com.volumetricpixels.questy.questy.event.SimpleEventManager;
import com.volumetricpixels.questy.storage.ProgressStore;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A simple {@link QuestManager} implementation which is not threadsafe.
 */
public class SimpleQuestManager implements QuestManager {
    /**
     * The {@link ProgressStore} used for progression data storage.
     */
    private final ProgressStore store;
    /**
     * Questy's {@link EventManager}.
     */
    private final EventManager eventManager;
    /**
     * All current {@link QuestLoader}s in use.
     */
    private final Set<QuestLoader> loaders;
    /**
     * All currently loaded {@link Quest}s.
     */
    private final Map<String, Quest> loaded;
    /**
     * All current {@link QuestInstance}s.
     */
    private final Set<QuestInstance> current;
    /**
     * All completed {@link QuestInstance}s.
     */
    private final Set<QuestInstance> completed;
    /**
     * The {@link QuestLoadHelper} helper used for {@link QuestBuilder} caching.
     */
    private final QuestLoadHelper questLoadHelper = new QuestLoadHelper();

    /**
     * Constructs a blank {@link SimpleQuestManager} with no registered {@link
     * QuestLoader}s or loaded {@link Quest}s, with the given {@link
     * ProgressStore} being used for saving / loading progression.
     *
     * A {@link SimpleEventManager} is used for events.
     *
     * @param store the {@link ProgressStore} for saving and loading progression
     *        data - can be null but {@link NullPointerException} will be thrown
     *        from {@link #loadProgression()} / {@link #saveProgression()} if it
     *        is
     */
    public SimpleQuestManager(ProgressStore store) {
        this(store, new SimpleEventManager());
    }

    /**
     * Constructs a blank {@link SimpleQuestManager} with no registered {@link
     * QuestLoader}s or loaded {@link Quest}s, with the given {@link
     * ProgressStore} being used for saving / loading progression.
     *
     * @param store the {@link ProgressStore} for saving and loading progression
     *        data - can be null but {@link NullPointerException} will be thrown
     *        from {@link #loadProgression()} / {@link #saveProgression()} if it
     *        is
     * @param eventManager the {@link EventManager} implementation to use for
     *        events
     */
    public SimpleQuestManager(ProgressStore store, EventManager eventManager) {
        this.store = store;
        this.eventManager = eventManager;
        this.loaders = new HashSet<>();
        this.loaded = new HashMap<>();
        this.current = new HashSet<>();
        this.completed = new HashSet<>();
    }

    @Override
    public ProgressStore getProgressStore() {
        return store;
    }

    @Override
    public EventManager getEventManager() {
        return eventManager;
    }

    @Override
    public Map<String, Quest> getLoadedQuests() {
        return new HashMap<>(loaded);
    }

    @Override
    public QuestLoadHelper getQuestLoadHelper() {
        return questLoadHelper;
    }

    @Override
    public Quest getQuest(String name) {
        return loaded.get(name);
    }

    @Override
    public QuestInstance getQuestInstance(Quest quest, String quester) {
        for (QuestInstance inst : current) {
            if (inst.getInfo().equals(quest) && inst.getQuester()
                    .equals(quester)) {
                return inst;
            }
        }
        return null;
    }

    @Override
    public QuestInstance getCompletedQuest(Quest quest, String quester) {
        for (QuestInstance inst : completed) {
            if (inst.getInfo().equals(quest) && inst.getQuester().equals(
                    quester)) {
                return inst;
            }
        }
        return null;
    }

    @Override
    public boolean hasCompleted(Quest quest, String quester) {
        return getCompletedQuest(quest, quester) != null;
    }

    @Override
    public Collection<QuestInstance> getQuestInstances(String quester) {
        Set<QuestInstance> result = new HashSet<>();
        for (QuestInstance instance : this.current) {
            if (instance.getQuester().equals(quester)) {
                result.add(instance);
            }
        }
        return result;
    }

    @Override
    public Collection<QuestInstance> getInstances(Quest quest) {
        return null;
    }

    @Override
    public void loadQuests(File directory) {
        if (loaders.isEmpty()) {
            return;
        }

        loaders.forEach((ql) -> loaded.putAll(ql.loadQuests(directory)));
    }

    @Override
    public void loadProgression() {
        if (store == null) {
            throw new NullPointerException("store mustn't be null");
        }

        deserialize(store.loadCurrentQuestData(), current);
        deserialize(store.loadCompletedQuestData(), completed);
    }

    @Override
    public void saveProgression() {
        if (store == null) {
            throw new NullPointerException("store mustn't be null");
        }

        store.saveCurrentQuestData(serialize(current));
        store.saveCompletedQuestData(serialize(completed));
    }

    @Override
    public boolean startQuest(QuestInstance instance) {
        boolean val = current.add(instance);
        if (val) {
            QuestStartEvent event = eventManager.fire(
                    new QuestStartEvent(instance));
            eventManager.fire(new ObjectiveStartEvent(instance,
                    instance.getCurrentObjective(), event));
        }
        return val;
    }

    @Override
    public boolean abandonQuest(QuestInstance instance) {
        return !eventManager.fire(new QuestAbandonEvent(instance)).isCancelled()
                && current.remove(instance);
    }

    @Override
    public boolean completeQuest(QuestInstance instance,
            OutcomeProgress outcome) {
        boolean val = current.remove(instance) && completed.add(instance);
        if (val) {
            eventManager.fire(new QuestCompleteEvent(instance, outcome));
        }
        return val;
    }

    @Override
    public boolean addQuest(Quest quest) {
        loaded.put(quest.getName(), quest);
        return true; // this implementation simply overwrites quests which are already present so always true
    }

    @Override
    public boolean removeQuest(Quest quest) {
        return loaded.remove(quest.getName()) != null;
    }

    @Override
    public boolean addLoader(QuestLoader loader) {
        return loaders.add(loader);
    }

    @Override
    public boolean removeLoader(QuestLoader loader) {
        return loaders.remove(loader);
    }

    // internal
    // these methods should probably be cleaned up at some point

    private void deserialize(Map<String, Map<String, String>> map,
            Set<QuestInstance> set) {
        for (String key : map.keySet()) { // loop through map values
            Collection<String> serialized = map.get(key).values();
            set.addAll(serialized.stream().map(serial -> new QuestInstance(
                    this, key, serial)).collect(Collectors.toList()));
        }
    }

    private Map<String, Map<String, String>> serialize(Set<QuestInstance> set) {
        Map<String, Map<String, String>> result = new HashMap<>();
        for (QuestInstance questInstance : set) { // loop through current quests
            String quester = questInstance.getQuester();
            Map<String, String> map = result.get(quester);
            if (map == null) {
                map = new HashMap<>();
                result.put(quester, map);
            }

            map.put(questInstance.getInfo().getName(),
                    questInstance.serializeProgression());
        }

        return result;
    }
}
