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
package com.github.rolecraftdev.quests.command;

import com.github.rolecraftdev.command.PlayerCommandHandler;
import com.github.rolecraftdev.quests.RolecraftQuests;

import javax.annotation.Nonnull;

/**
 * Command to create a dungeon.
 *
 * @since 0.1.0
 */
public final class DungeonCreateCommand extends PlayerCommandHandler {
    private final RolecraftQuests quests;

    /**
     * @since 0.1.0
     */
    public DungeonCreateCommand(@Nonnull final RolecraftQuests plugin) {
        super(plugin.getCore(), "info");
        this.quests = plugin;
    }
}
