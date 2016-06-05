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
import com.github.rolecraftdev.quests.dungeon.DungeonManager;
import com.github.rolecraftdev.quests.listener.BlockListener;
import com.github.rolecraftdev.quests.listener.EnchantListener;
import com.github.rolecraftdev.quests.listener.EntityListener;
import com.github.rolecraftdev.quests.listener.ExperienceListener;
import com.github.rolecraftdev.quests.listener.GuildListener;
import com.github.rolecraftdev.quests.listener.InventoryListener;
import com.github.rolecraftdev.quests.listener.PlayerListener;
import com.github.rolecraftdev.quests.listener.ProfessionListener;
import com.github.rolecraftdev.quests.quest.QuestingHandler;
import com.github.rolecraftdev.quests.quest.QuestingListener;

import com.volumetricpixels.questy.QuestManager;
import com.volumetricpixels.questy.questy.SimpleQuestManager;
import com.volumetricpixels.questy.questy.loader.JSQuestLoader;
import com.volumetricpixels.questy.questy.loader.YMLQuestLoader;
import com.volumetricpixels.questy.storage.ProgressStore;
import com.volumetricpixels.questy.storage.store.SimpleProgressStore;

import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/**
 * Main plugin class for RolecraftQuests, the quest add-on for the Rolecraft RPG
 * Bukkit plugin.
 *
 * @since 0.1.0
 */
public final class RolecraftQuests extends JavaPlugin {
    /**
     * @since 0.1.0
     */
    public static final String CORE_PLUGIN_NAME = "RolecraftCore";

    private RolecraftCore core;
    private QuestManager questManager;
    private ProgressStore progressStore;
    private DungeonManager dungeonManager;
    private QuestingHandler questingHandler;

    /**
     * @since 0.1.0
     */
    @Override
    public void onEnable() {
        final Server server = getServer();
        final PluginManager pluginManager = server.getPluginManager();

        // plugin.yml has dependency for RolecraftCore so this should always work
        this.core = (RolecraftCore) pluginManager.getPlugin(CORE_PLUGIN_NAME);

        final File dataFolder = this.getDataFolder();
        final File storageFolder = new File(dataFolder, "data");
        final File questsFolder = new File(dataFolder, "quests");
        final File dungeonsFolder = new File(dataFolder, "dungeons");

        if (!storageFolder.exists() && !storageFolder.mkdirs()) {
            getLogger().severe("Could not create data storage folder");
            getLogger().severe("RolecraftQuests will not function correctly");
            return;
        }

        if (!questsFolder.exists() && !questsFolder.mkdirs()) {
            getLogger().severe("Could not create quest script folder");
            getLogger().severe("RolecraftQuests will not function correctly");
            return;
        }

        if (!dungeonsFolder.exists() && !dungeonsFolder.mkdirs()) {
            getLogger().severe("Could not create dungeons folder");
            getLogger().severe("RolecraftQuests will not function correctly");
            return;
        }

        questManager.addLoader(new JSQuestLoader(questManager));
        questManager.addLoader(new YMLQuestLoader(questManager));

        questManager.loadQuests(questsFolder);

        // create & load dungeons AFTER quests as dungeons depend on quests
        // (progression doesn't matter at this point)
        this.dungeonManager = new DungeonManager(this, dungeonsFolder);

        // we'll use a different implementation of progress store (SQLite / MySQL) when it is added to Questy
        this.progressStore = new SimpleProgressStore(storageFolder);
        this.questManager = new SimpleQuestManager(progressStore);

        this.questManager.loadProgression();

        this.questingHandler = new QuestingHandler(this);

        // deals with events regarding starting, finishing quests, giving rewards etc
        pluginManager.registerEvents(new QuestingListener(this), this);

        // listeners dealing with updating quest objectives
        pluginManager.registerEvents(new BlockListener(this), this);
        pluginManager.registerEvents(new EnchantListener(this), this);
        pluginManager.registerEvents(new EntityListener(this), this);
        pluginManager.registerEvents(new ExperienceListener(this), this);
        pluginManager.registerEvents(new GuildListener(this), this);
        pluginManager.registerEvents(new InventoryListener(this), this);
        pluginManager.registerEvents(new PlayerListener(this), this);
        pluginManager.registerEvents(new ProfessionListener(this), this);
    }

    /**
     * @since 0.1.0
     */
    @Override
    public void onDisable() {
        this.questManager.saveProgression();
    }

    /**
     * Get the {@link RolecraftCore} core Rolecraft plugin object.
     *
     * @return this server's {@link RolecraftCore} plugin instance
     * @since 0.1.0
     */
    public RolecraftCore getCore() {
        return this.core;
    }

    /**
     * Get the {@link QuestManager} from Questy used for RolecraftQuests.
     *
     * @return RolecraftQuests' {@link QuestManager} instance
     * @since 0.1.0
     */
    public QuestManager getQuestManager() {
        return this.questManager;
    }

    /**
     * Get the {@link ProgressStore} from Questy used for RolecraftQuests.
     *
     * @return RolecraftQuests' {@link ProgressStore} instance
     * @since 0.1.0
     */
    public ProgressStore getProgressStore() {
        return this.progressStore;
    }
}
