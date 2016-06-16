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
package com.github.rolecraftdev.quests;

import com.github.rolecraftdev.RolecraftCore;

import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;

/**
 * Periodically stores data used in the {@link RolecraftQuests} plugin.
 *
 * @since 0.1.0
 */
public final class RQDataUpdater extends BukkitRunnable {
    /**
     * The associated {@link RolecraftQuests} plugin instance.
     */
    private final RolecraftQuests plugin;
    /**
     * The {@link RolecraftCore} instance which the associated
     * {@link RolecraftQuests} instance is for.
     */
    private final RolecraftCore core;

    /**
     * Constructs a new data updater object for {@link RolecraftQuests}.
     *
     * @param plugin the associated {@link RolecraftQuests} instance
     * @since 0.1.0
     */
    public RQDataUpdater(@Nonnull final RolecraftQuests plugin) {
        this.plugin = plugin;
        this.core = plugin.getCore();
    }

    /**
     * @since 0.1.0
     */
    @Override
    public void run() {
        this.plugin.getDungeonManager().save();
        this.plugin.getQuestManager().saveProgression();
    }
}
