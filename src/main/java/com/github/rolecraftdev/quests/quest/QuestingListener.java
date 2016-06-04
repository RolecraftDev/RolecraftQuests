package com.github.rolecraftdev.quests.quest;

import com.github.rolecraftdev.quests.RolecraftQuests;

import org.bukkit.event.Listener;

/**
 * Listens for events which may start or hand in a quest etc, e.g. interactions
 * with signs may start a quest.
 *
 * @since 0.1.0
 */
public final class QuestingListener implements Listener {
    /**
     * The main {@link RolecraftQuests} plugin instance.
     */
    private final RolecraftQuests plugin;

    /**
     * Constructor.
     *
     * @param plugin the main plugin instance
     * @since 0.1.0
     */
    public QuestingListener(final RolecraftQuests plugin) {
        this.plugin = plugin;
    }
}
