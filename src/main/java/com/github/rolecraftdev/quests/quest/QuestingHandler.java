/*
 * This file is part of RolecraftQuests.
 *
 * Copyright (c) 2016 RolecraftDev <http://rolecraftdev.github.com>
 * RolecraftQuests is licensed under the Creative Commons
 * Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this
 * license, visit http://creativecommons.org/licenses/by-nc-nd/3.0
 *
 * As long as you follow the following terms, you are free to copy and redistribute
 * the material in any medium or format.
 *
 * You must give appropriate credit, provide a link to the license, and indicate
 * whether any changes were made to the material. You may do so in any reasonable
 * manner, but not in any way which suggests the licensor endorses you or your use.
 *
 * You may not use the material for commercial purposes.
 *
 * If you remix, transform, or build upon the material, you may not distribute the
 * modified material.
 *
 * You may not apply legal terms or technological measures that legally restrict
 * others from doing anything the license permits.
 *
 * DISCLAIMER: This is a human-readable summary of (and not a substitute for) the
 * license.
 */
package com.github.rolecraftdev.quests.quest;

import com.github.rolecraftdev.RolecraftCore;
import com.github.rolecraftdev.quests.RolecraftQuests;

import com.volumetricpixels.questy.Quest;
import com.volumetricpixels.questy.QuestInstance;
import com.volumetricpixels.questy.QuestManager;

import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Handles the linking of RolecraftQuests with the Questy framework.
 *
 * @since 0.1.0
 */
public final class QuestingHandler {
    private final RolecraftQuests plugin;
    private final RolecraftCore core;
    private final QuestManager questManager;

    /**
     * Constructor for the RolecraftQuests quest handler, which links the plugin
     * to the Questy framework.
     *
     * @param plugin the {@link RolecraftQuests} plugin instance
     * @since 0.1.0
     */
    public QuestingHandler(@Nonnull final RolecraftQuests plugin) {
        this.plugin = plugin;
        this.core = plugin.getCore();
        this.questManager = plugin.getQuestManager();
    }

    // TODO: doc

    @Nullable
    public QuestInstance beginQuest(@Nonnull final String questName,
            @Nonnull final Player quester) {
        final Quest quest = questManager.getQuest(questName);
        if (!quest.satisfiesPrerequisites(quester.getUniqueId().toString())
                || questManager
                .hasCompleted(quest, quester.getUniqueId().toString())) {
            return null;
        }

        return quest.start(quester.getUniqueId().toString());
    }

    @Nullable
    public QuestInstance beginQuest(@Nonnull final String questName,
            @Nonnull final UUID questerId) {
        final Quest quest = questManager.getQuest(questName);
        if (!quest.satisfiesPrerequisites(questerId.toString())
                || questManager.hasCompleted(quest, questerId.toString())) {
            return null;
        }

        return quest.start(questerId.toString());
    }

    @Nullable
    public QuestInstance getProgress(@Nonnull final String questName,
            @Nonnull final Player quester) {
        final Quest quest = questManager.getQuest(questName);
        if (quest == null) {
            return null;
        }

        return questManager.getQuestInstance(quest,
                quester.getUniqueId().toString());
    }

    @Nullable
    public QuestInstance getProgress(@Nonnull final String questName,
            @Nonnull final UUID questerId) {
        final Quest quest = questManager.getQuest(questName);
        if (quest == null) {
            return null;
        }

        return questManager.getQuestInstance(quest, questerId.toString());
    }
}
