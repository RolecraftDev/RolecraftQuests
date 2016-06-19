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
package com.github.rolecraftdev.quests.listener;

import com.github.rolecraftdev.quests.RolecraftQuests;
import com.github.rolecraftdev.quests.quest.QuestingHandler;

import com.volumetricpixels.questy.QuestInstance;
import com.volumetricpixels.questy.objective.ObjectiveProgress;
import com.volumetricpixels.questy.objective.OutcomeProgress;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;

import java.util.Collection;
import java.util.Optional;

/**
 * Listens for inventory-related events in order to update quests for Rolecraft.
 *
 * @since 0.1.0
 */
public final class InventoryListener implements Listener {
    /**
     * The main {@link RolecraftQuests} plugin instance.
     */
    private final RolecraftQuests plugin;
    /**
     * The {@link RolecraftQuests} plugin's {@link QuestingHandler}.
     */
    private final QuestingHandler questingHandler;

    /**
     * Constructor.
     *
     * @param plugin the main plugin instance
     * @since 0.1.0
     */
    public InventoryListener(final RolecraftQuests plugin) {
        this.plugin = plugin;
        this.questingHandler = plugin.getQuestingHandler();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPickupItem(final InventoryPickupItemEvent event) {
        final Inventory inventory = event.getInventory();
        if (!(inventory instanceof PlayerInventory)) {
            return;
        }

        final PlayerInventory playerInventory = (PlayerInventory) inventory;
        final HumanEntity humanEntity = playerInventory.getHolder();
        if (!(humanEntity instanceof Player)) {
            return;
        }

        final Player player = (Player) humanEntity;
        final Collection<QuestInstance> quests = this.questingHandler
                .getQuests(player);

        for (final QuestInstance quest : quests) {
            final ObjectiveProgress objective = quest.getCurrentObjective();
            final Optional<OutcomeProgress> completedOutcome = questingHandler
                    .getObjectiveCompletionChecker().checkCompletion(
                            objective, player.getUniqueId().toString(),
                            playerInventory);

            if (completedOutcome.isPresent()) {
                completedOutcome.get().setProgress(1);
                quest.objectiveComplete(objective, completedOutcome.get());
            }
        }
    }
}
