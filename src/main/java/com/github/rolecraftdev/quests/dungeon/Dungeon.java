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
package com.github.rolecraftdev.quests.dungeon;

import com.github.rolecraftdev.data.Region2D;

import com.volumetricpixels.questy.Quest;

import javax.annotation.Nonnull;

/**
 * Contains basic information about a dungeon in {@link RolecraftQuests}.
 *
 * @since 0.1.0
 */
public final class Dungeon {
    private final String name;
    private final Region2D region;
    private final Quest dungeonQuest;
    private final boolean allowGroups;

    /**
     * Constructs a new dungeon object. This should generally be called from
     * within the plugin though add-on plugins are welcome to create their own
     * dungeons using this constructor, as long as they use the appropriate
     * methods to add the dungeon to the {@link DungeonManager}.
     *
     * @param name the name of this dungeon. Note that dungeon names are used as
     *        unique identifiers and thus should be unique
     * @param dungeon the physical area of the dungeon
     * @param quest the {@link Quest} associated with the dungeon
     * @param allowGroups whether to allow players to complete this dungeon in
     *        groups or only permit solo players to enter
     * @since 0.1.0
     */
    public Dungeon(@Nonnull final String name, @Nonnull final Region2D dungeon,
            @Nonnull final Quest quest, final boolean allowGroups) {
        this.name = name;
        this.region = dungeon;
        this.dungeonQuest = quest;
        this.allowGroups = allowGroups;
    }

    /**
     * Get the (unique) name of this dungeon. This is used to identify dungeons
     * in the plugin.
     *
     * @return this dungeon's unique name
     * @since 0.1.0
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the 2D physical area of the dungeon.
     *
     * @return this dungeon's physical area
     * @since 0.1.0
     */
    public Region2D getRegion() {
        return region;
    }

    /**
     * Gets the {@link Quest} associated with completing this dungeon.
     *
     * @return this dungeon's {@link Quest}
     * @since 0.1.0
     */
    public Quest getDungeonQuest() {
        return dungeonQuest;
    }

    /**
     * Checks whether this dungeon allows players to enter in groups.
     *
     * @return {@code true} if groups are permitted in this dungeon, or
     *         {@code false} if only solo players are permitted
     * @since 0.1.0
     */
    public boolean allowGroups() {
        return allowGroups;
    }
}
