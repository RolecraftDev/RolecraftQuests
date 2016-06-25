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
import com.github.rolecraftdev.data.PlayerData;
import com.github.rolecraftdev.quests.RolecraftQuests;
import com.github.rolecraftdev.quests.quest.completion.EconomyOutcomeCompletionChecker;
import com.github.rolecraftdev.quests.quest.completion.ExperienceOutcomeCompletionChecker;
import com.github.rolecraftdev.quests.quest.completion.GuildOutcomeCompletionChecker;
import com.github.rolecraftdev.quests.quest.completion.InventoryOutcomeCompletionChecker;
import com.github.rolecraftdev.quests.quest.completion.ProfessionOutcomeCompletionChecker;
import com.github.rolecraftdev.quests.task.CompletionCheckTask;

import com.volumetricpixels.questy.Quest;
import com.volumetricpixels.questy.QuestInstance;
import com.volumetricpixels.questy.QuestManager;
import com.volumetricpixels.questy.objective.ObjectiveProgress;
import com.volumetricpixels.questy.objective.Outcome;
import com.volumetricpixels.questy.objective.OutcomeProgress;

import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import static com.github.rolecraftdev.quests.quest.ObjectiveOutcomeTypes.*;

/**
 * Handles the linking of RolecraftQuests with the Questy framework.
 *
 * @since 0.1.0
 */
public final class QuestingHandler {
    /**
     * Five seconds.
     */
    private static final long CHECK_PERIOD = 100L;

    private final RolecraftQuests plugin;
    private final RolecraftCore core;
    private final QuestManager questManager;
    private final QuestObjectiveCompletionChecker objectiveCompletionChecker;

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
        this.objectiveCompletionChecker = new QuestObjectiveCompletionChecker(
                plugin);

        this.objectiveCompletionChecker.registerOutcomeChecker(
                new ExperienceOutcomeCompletionChecker(this));
        this.objectiveCompletionChecker.registerOutcomeChecker(
                new GuildOutcomeCompletionChecker(this));
        this.objectiveCompletionChecker.registerOutcomeChecker(
                new InventoryOutcomeCompletionChecker(this));
        this.objectiveCompletionChecker.registerOutcomeChecker(
                new ProfessionOutcomeCompletionChecker(this));
        this.objectiveCompletionChecker.registerOutcomeChecker(
                new EconomyOutcomeCompletionChecker(this));

        new CompletionCheckTask(plugin)
                .runTaskTimer(plugin, CHECK_PERIOD, CHECK_PERIOD);
    }

    // TODO: doc

    public void checkCompletion(@Nonnull final Player player,
            @Nonnull final ObjectiveProgress objective) {
        final QuestInstance quest = objective.getQuest();
        final String quester = player.getUniqueId().toString();

        for (final OutcomeProgress outcome : objective.getOutcomeProgresses()) {
            final Outcome info = outcome.getInfo();
            final String type = info.getType().toLowerCase();

            if (type.startsWith(REACH_LEVEL)) {
                final Optional<OutcomeProgress> completedOutcome = objectiveCompletionChecker
                        .checkCompletion(objective, quester,
                                getPlayerData(quester).getLevel());

                if (completedOutcome.isPresent()) {
                    quest.objectiveComplete(objective, completedOutcome.get());
                    break;
                }
            } else if (type.equals(JOIN_GUILD)) {
                final Optional<OutcomeProgress> completedOutcome = objectiveCompletionChecker
                        .checkCompletion(objective, quester,
                                getPlayerData(quester).getGuild());

                if (completedOutcome.isPresent()) { // outcome completed
                    quest.objectiveComplete(objective, completedOutcome.get());
                    break;
                }
            } else if (type.equals(SELECT_PROFESSION)) {
                final Optional<OutcomeProgress> completedOutcome = objectiveCompletionChecker
                        .checkCompletion(objective, quester,
                                getPlayerData(quester).getProfession());

                if (completedOutcome.isPresent()) {
                    quest.objectiveComplete(objective, completedOutcome.get());
                    break;
                }
            } else if (type.equals(ACQUIRE_ITEMS)) {
                final Optional<OutcomeProgress> completedOutcome = objectiveCompletionChecker
                        .checkCompletion(objective, quester,
                                player.getInventory());

                if (completedOutcome.isPresent()) {
                    quest.objectiveComplete(objective, completedOutcome.get());
                    break;
                }
            } else if (type.equals(ACQUIRE_MONEY)) {
                final Optional<OutcomeProgress> completedOutcome = objectiveCompletionChecker
                        .checkCompletion(objective, quester, plugin.getCore()
                                .getVaultEcon().getBalance(player));

                if (completedOutcome.isPresent()) {
                    quest.objectiveComplete(objective, completedOutcome.get());
                    break;
                }
            }
        }
    }

    @Nonnull
    public RolecraftQuests getPlugin() {
        return plugin;
    }

    @Nonnull
    public QuestObjectiveCompletionChecker getObjectiveCompletionChecker() {
        return objectiveCompletionChecker;
    }

    @Nullable
    public Player getPlayer(@Nonnull final String quester) {
        return plugin.getServer().getPlayer(UUID.fromString(quester));
    }

    @Nullable
    public PlayerData getPlayerData(@Nonnull final String quester) {
        return core.getDataManager().getPlayerData(UUID.fromString(quester));
    }

    @Nullable
    public PlayerData getPlayerData(@Nonnull final Player player) {
        return getPlayerData(player.getUniqueId().toString());
    }

    @Nonnull
    public Collection<QuestInstance> getQuests(@Nonnull final Player quester) {
        return this.questManager
                .getQuestInstances(quester.getUniqueId().toString());
    }

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

    @Nullable
    public Quest getQuest(@Nonnull final String questName) {
        return questManager.getQuest(questName);
    }

    public boolean questExists(@Nonnull final String questName) {
        return this.getQuest(questName) != null;
    }
}
