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

import com.github.rolecraftdev.quests.RolecraftQuests;

import com.volumetricpixels.questy.Quest;
import com.volumetricpixels.questy.QuestInstance;
import com.volumetricpixels.questy.event.Listen;
import com.volumetricpixels.questy.event.quest.QuestCompleteEvent;
import com.volumetricpixels.questy.event.quest.QuestStartEvent;
import com.volumetricpixels.questy.event.quest.objective.ObjectiveCompleteEvent;
import com.volumetricpixels.questy.event.quest.objective.ObjectiveStartEvent;
import com.volumetricpixels.questy.objective.Objective;
import com.volumetricpixels.questy.objective.ObjectiveProgress;
import com.volumetricpixels.questy.objective.Outcome;
import com.volumetricpixels.questy.objective.OutcomeProgress;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

import static com.github.rolecraftdev.quests.quest.ObjectiveOutcomeTypes.*;

/**
 * Listens for events which may start or hand in a quest etc, e.g. interactions
 * with signs. Also deals with quest rewards.
 *
 * @since 0.1.0
 */
public final class QuestingListener implements Listener {
    /**
     * The integer returned by {@link PlayerInventory#firstEmpty()} if the
     * player has a full inventory.
     */
    private static final int INVENTORY_EMPTY = -1;

    /**
     * The main {@link RolecraftQuests} plugin instance.
     */
    private final RolecraftQuests plugin;
    /**
     * The {@link QuestingHandler} instance for the plugin.
     */
    private final QuestingHandler questingHandler;

    /**
     * Constructor.
     *
     * @param plugin the main plugin instance
     * @since 0.1.0
     */
    public QuestingListener(final RolecraftQuests plugin) {
        this.plugin = plugin;
        this.questingHandler = plugin.getQuestingHandler();
    }

    /**
     * @since 0.1.0
     */
    @Listen(monitor = true)
    public void onQuestStart(final QuestStartEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final Quest questInfo = event.getQuestInfo();
        final String message = questInfo.getBeginMessage();
        if (message != null) {
            final Player player = plugin.getServer()
                    .getPlayer(UUID.fromString(event.getQuester()));
            player.sendMessage(questInfo.getBeginMessage());
        }
    }

    /**
     * @since 0.1.0
     */
    @Listen(monitor = true)
    public void onQuestComplete(final QuestCompleteEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final Quest questInfo = event.getQuestInfo();
        final String[] rewards = questInfo.getRewards();
        final String quester = event.getQuester();
        final UUID questerId = UUID.fromString(quester);
        final Player player = this.plugin.getServer().getPlayer(questerId);

        if (questInfo.getFinishMessage() != null) {
            player.sendMessage(questInfo.getFinishMessage());
        }

        if (player == null) {
            // the player has completed a quest without being online. w0t.
            // todo: implement a waiting list so if this does happen the player can be rewarded when they next log on
            return;
        }

        if (rewards == null || rewards.length == 0) {
            return;
        }

        for (final String reward : rewards) {
            final String[] split = reward.split(Pattern.quote("-"));

            try {
                final String rewardName = split[0];
                final int quantity = Integer.parseInt(split[1]);
                final Material material = Material.getMaterial(rewardName);
                final ItemStack stack = new ItemStack(material, quantity);

                final PlayerInventory inventory = player.getInventory();
                if (inventory.firstEmpty() == INVENTORY_EMPTY) {
                    // TODO: implement a waiting list so they can be rewarded later
                    continue; // continue looping so we can later add other rewards to waiting list
                }

                player.getInventory().addItem(stack);
            } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
                // the person who wrote the quest messed up, smh
                this.plugin.getLogger()
                        .severe("The quest '" + questInfo.getName()
                                + "' is incorrectly configured. Rewards for this quest will not work");
            }
        }
    }

    /**
     * @since 0.1.0
     */
    @Listen(monitor = true)
    public void onObjectiveStart(final ObjectiveStartEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final Objective objectiveInfo = event.getObjectiveInfo();
        final String message = objectiveInfo.getBeginMessage();
        if (message != null) {
            final Player player = plugin.getServer()
                    .getPlayer(UUID.fromString(event.getQuester()));
            player.sendMessage(message);
        }

        final QuestInstance quest = event.getQuest();
        final ObjectiveProgress objective = quest.getCurrentObjective();

        for (final OutcomeProgress outcome : objective.getOutcomeProgresses()) {
            final Outcome info = outcome.getInfo();
            final String type = info.getType().toLowerCase();

            // if there is an outcome which can be completed prior to the
            // objective being started, check whether it has already been
            // completed and update the quest instance object if it has been
            if (type.startsWith(REACH_LEVEL)) {
                final Optional<OutcomeProgress> completedOutcome = questingHandler
                        .getObjectiveCompletionChecker()
                        .checkCompletion(objective, event.getQuester(),
                                questingHandler.getPlayerData(
                                        event.getQuester()).getLevel()); // playerdata should be present as a player must be online to start a quest

                if (completedOutcome.isPresent()) { // outcome completed
                    quest.objectiveComplete(objective, completedOutcome.get());
                    break;
                }
            } else if (type.equals(JOIN_GUILD)) {
                final Optional<OutcomeProgress> completedOutcome = questingHandler
                        .getObjectiveCompletionChecker()
                        .checkCompletion(objective, event.getQuester(),
                                questingHandler.getPlayerData(
                                        event.getQuester()).getGuild()); // playerdata should be present as a player must be online to start a quest

                if (completedOutcome.isPresent()) { // outcome completed
                    quest.objectiveComplete(objective, completedOutcome.get());
                    break;
                }
            } else if (type.equals(SELECT_PROFESSION)) {
                final Optional<OutcomeProgress> completedOutcome = questingHandler
                        .getObjectiveCompletionChecker()
                        .checkCompletion(objective, event.getQuester(),
                                questingHandler.getPlayerData(
                                        event.getQuester()).getProfession()); // playerdata should be present as a player must be online to start a quest

                if (completedOutcome.isPresent()) { // outcome completed
                    quest.objectiveComplete(objective, completedOutcome.get());
                    break;
                }
            } else if (type.equals(ACQUIRE_ITEMS)) {
                final Optional<OutcomeProgress> completedOutcome = questingHandler
                        .getObjectiveCompletionChecker()
                        .checkCompletion(objective, event.getQuester(),
                                Bukkit.getPlayer(UUID.fromString(
                                        event.getQuester())).getInventory()); // playerdata should be present as a player must be online to start a quest

                if (completedOutcome.isPresent()) { // outcome completed
                    quest.objectiveComplete(objective, completedOutcome.get());
                    break;
                }
            }
        }
    }

    /**
     * @since 0.1.0
     */
    @Listen(monitor = true)
    public void onObjectiveComplete(final ObjectiveCompleteEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final Outcome outcomeInfo = event.getOutcomeInfo();
        final String message = outcomeInfo.getFinishMessage();
        if (message != null) {
            final Player player = plugin.getServer()
                    .getPlayer(UUID.fromString(event.getQuester()));
            player.sendMessage(message);
        }
    }
}
