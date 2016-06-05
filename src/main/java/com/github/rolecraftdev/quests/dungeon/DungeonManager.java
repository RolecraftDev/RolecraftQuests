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

import com.github.rolecraftdev.RolecraftCore;
import com.github.rolecraftdev.data.Region2D;
import com.github.rolecraftdev.data.storage.YamlFile;
import com.github.rolecraftdev.quests.RolecraftQuests;

import com.volumetricpixels.questy.Quest;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import static com.volumetricpixels.questy.util.Serialization.checkExtension;

/**
 * Manages dungeons for RolecraftQuests.
 *
 * Dungeons are built in a Minecraft world then specified to the plugin, with
 * the user giving a list of the types of enemies that should be generated in
 * the dungeon and the end boss of the dungeon, as well as specifying the reward
 * for completing the dungeon. We then generate a {@link Quest} for the dungeon,
 * and when a player begins the dungeon {@link Quest} they are transported to a
 * specific instance of the dungeon (in their group once groups are added to
 * Rolecraft). When the end boss dies, they are transported away and receive
 * rewards, or they can transport away at any time without rewards.
 *
 * There should be a specific Minecraft world designated for dungeon instances
 * to keep them separate from the main world.
 *
 * @since 0.1.0
 */
public final class DungeonManager {
    private final RolecraftQuests plugin;
    private final RolecraftCore core;
    private final Map<String, Dungeon> dungeons;
    private final File dungeonsDirectory;

    /**
     * Constructs a new {@link RolecraftQuests} dungeon manager. Only one of
     * these objects needs to exist at a time.
     *
     * @param plugin the {@link RolecraftQuests} plugin instance
     * @since 0.1.0
     */
    public DungeonManager(@Nonnull final RolecraftQuests plugin,
            @Nonnull final File dungeonsDirectory) {
        this.plugin = plugin;
        this.core = plugin.getCore();
        this.dungeons = new HashMap<>();
        this.dungeonsDirectory = dungeonsDirectory;

        // dungeons directory should exist as this is checked in plugin main
        this.loadDungeons(dungeonsDirectory);
    }

    /**
     * @since 0.1.0
     */
    public void cleanup() {
        this.save();
    }

    /**
     * @since 0.1.0
     */
    public void save() {
        this.saveDungeons(this.dungeonsDirectory);
    }

    private void loadDungeons(@Nonnull final File directory) {
        // loop through files with the ".yml" extension
        for (final File file : directory
                .listFiles(fl -> checkExtension(fl, "yml"))) {
            final YamlFile yamlFile = new YamlFile(file);

            // all of these will have a value barring users modifying files
            // (and users who manually modify these files don't deserve us to add checks)
            final String name = yamlFile.getString("name");
            final Region2D region = Region2D
                    .fromString(yamlFile.getString("region"));
            final Quest quest = plugin.getQuestManager()
                    .getQuest(yamlFile.getString("quest"));
            final boolean allowGroups = yamlFile.getBoolean("allow-groups");

            final Dungeon dungeon = new Dungeon(name, region, quest,
                    allowGroups);

            this.dungeons.put(name, dungeon);
        }
    }

    private void saveDungeons(@Nonnull final File directory) {
        for (final Entry<String, Dungeon> entry : this.dungeons.entrySet()) {
            final String name = entry.getKey();
            final Dungeon dungeon = entry.getValue();

            try {
                final File file = new File(directory, name + ".yml");
                if (!file.exists() && !file.createNewFile()) {
                    plugin.getLogger()
                            .severe("Couldn't save " + name + " dungeon!");
                }

                final YamlFile yamlFile = new YamlFile(file);

                yamlFile.set("name", dungeon.getName());
                yamlFile.set("region", dungeon.getRegion().toString());
                yamlFile.set("quest", dungeon.getDungeonQuest().getName());
                yamlFile.set("allow-groups", dungeon.allowGroups());

                yamlFile.save();
            } catch (IOException e) {
                plugin.getLogger()
                        .severe("Couldn't save " + name + " dungeon!");
                e.printStackTrace();
            }
        }
    }
}
