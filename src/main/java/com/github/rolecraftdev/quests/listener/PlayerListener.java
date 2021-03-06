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

import com.github.rolecraftdev.quests.RQUtil;
import com.github.rolecraftdev.quests.RolecraftQuests;
import com.github.rolecraftdev.quests.quest.QuestingHandler;

import com.volumetricpixels.questy.QuestInstance;
import com.volumetricpixels.questy.objective.ObjectiveProgress;
import com.volumetricpixels.questy.objective.Outcome;
import com.volumetricpixels.questy.objective.OutcomeProgress;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.Collection;
import java.util.Set;

import static com.github.rolecraftdev.quests.quest.ObjectiveOutcomeTypes.KILL_PLAYER;
import static com.github.rolecraftdev.quests.quest.ObjectiveOutcomeTypes.KILL_PLAYERS;

/**
 * Listens for player-related events in order to update quests for Rolecraft.
 *
 * @since 0.1.0
 */
public final class PlayerListener implements Listener {
    /**
     * The main {@link RolecraftQuests} plugin instance.
     */
    private final RolecraftQuests plugin;
    /**
     * The {@link QuestingHandler} instance associated with the plugin.
     */
    private final QuestingHandler questingHandler;

    /**
     * Constructor.
     *
     * @param plugin the main plugin instance
     * @since 0.1.0
     */
    public PlayerListener(final RolecraftQuests plugin) {
        this.plugin = plugin;
        this.questingHandler = plugin.getQuestingHandler();
    }

    /**
     * @since 0.1.0
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        // this method updates player quests relating to killing other players
        // this includes: killing a specific player, killer any single player, or killing a specific number of players
        final Player killed = event.getEntity();
        final EntityDamageEvent lastDamage = killed.getLastDamageCause();

        if (!(lastDamage instanceof EntityDamageByEntityEvent)) {
            return;
        }

        final EntityDamageByEntityEvent byEntity = (EntityDamageByEntityEvent) lastDamage;
        final Entity entityKiller = byEntity.getDamager();

        if (!(entityKiller instanceof Player)) {
            return;
        }

        final Player killer = (Player) entityKiller;
        final Collection<QuestInstance> killerQuestInstances = questingHandler
                .getQuests(killer);

        for (final QuestInstance questInstance : killerQuestInstances) {
            final ObjectiveProgress objectiveProgress = questInstance
                    .getCurrentObjective();
            final Set<OutcomeProgress> outcomeProgresses = objectiveProgress
                    .getOutcomeProgresses();

            for (final OutcomeProgress outcomeProgress : outcomeProgresses) {
                final Outcome outcome = outcomeProgress.getInfo();
                final String[] typeSegments = outcome.getType().split(
                        RQUtil.QUOTED_UNDERSCORE);
                final int length = typeSegments.length;

                if (length == 0) {
                    continue;
                }

                if (length == 1) {
                    if (typeSegments[0].equals(KILL_PLAYER) && outcomeProgress
                            .getProgress().toString().equals("0")) { // case-sensitive
                        outcomeProgress.setProgress(1);
                        questInstance.objectiveComplete(objectiveProgress,
                                outcomeProgress);
                    }

                    continue;
                }

                // at this point we know length > 1
                if (typeSegments[0].equals(KILL_PLAYER) && outcomeProgress
                        .getProgress().toString().equals("0")) {
                    final String playerID = typeSegments[1];
                    if (killed.getUniqueId().toString().equals(playerID)) {
                        outcomeProgress.setProgress(1);
                        questInstance.objectiveComplete(objectiveProgress,
                                outcomeProgress);
                    }
                } else if (typeSegments[0].equals(KILL_PLAYERS)) {
                    final int killsNeeded = Integer.valueOf(typeSegments[1]); // if this causes an exception, the quest is not valid
                    outcomeProgress.setProgress(Integer.valueOf(
                            outcomeProgress.getProgress().toString()) + 1);

                    if (Integer.valueOf(
                            outcomeProgress.getProgress().toString()) >= killsNeeded) {
                        questInstance.objectiveComplete(objectiveProgress,
                                outcomeProgress);
                    }
                }
            }
        }
    }
}
