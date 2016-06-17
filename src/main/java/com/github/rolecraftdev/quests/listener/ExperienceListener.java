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

import com.github.rolecraftdev.event.experience.RCLevelChangeEvent;
import com.github.rolecraftdev.quests.RolecraftQuests;
import com.github.rolecraftdev.quests.quest.QuestingHandler;

import com.volumetricpixels.questy.QuestInstance;
import com.volumetricpixels.questy.objective.ObjectiveProgress;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Collection;

/**
 * Listens for experience-related events in order to update quests for Rolecraft.
 *
 * @since 0.1.0
 */
public final class ExperienceListener implements Listener {
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
    public ExperienceListener(final RolecraftQuests plugin) {
        this.plugin = plugin;
        this.questingHandler = plugin.getQuestingHandler();
    }

    /**
     * @since 0.1.0
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onLevelChange(final RCLevelChangeEvent event) {
        if (event.isLevelDecrease()) {
            return;
        }

        final Player player = event.getPlayer();
        final Collection<QuestInstance> quests = this.questingHandler
                .getQuests(player);

        for (final QuestInstance quest : quests) {
            final ObjectiveProgress objective = quest.getCurrentObjective();
            objective.getOutcomeProgresses().stream()
                    .filter(outcome -> outcome.getInfo().getType().toLowerCase()
                            .startsWith("reachlevel"))
                    .forEach(outcome -> {
                        final String type = outcome.getInfo().getType()
                                .toLowerCase();
                        final String[] split = type.split("_");
                        if (split.length < 2) {
                            return;
                        }

                        // always works if quest is correctly configured
                        final int level = Integer.parseInt(split[1]);
                        if (event.getNewLevel() >= level) {
                            outcome.setProgress(1);
                            quest.objectiveComplete(objective, outcome);
                        }
                    });
        }
    }
}
