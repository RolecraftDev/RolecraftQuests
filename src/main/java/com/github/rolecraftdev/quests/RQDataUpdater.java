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
