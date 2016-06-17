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
package com.github.rolecraftdev.quests.quest;

import com.github.rolecraftdev.quests.RolecraftQuests;
import com.github.rolecraftdev.sign.RolecraftSign;
import com.github.rolecraftdev.sign.SignInteractionHandler;

import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

/**
 * An implementation of {@link SignInteractionHandler} designed to handle
 * {@link RolecraftSign}s of type 'quest'.
 */
public final class QuestSignInteractionHandler
        implements SignInteractionHandler {
    /**
     * The name of the sign function which begins a quest.
     *
     * @since 0.1.0
     */
    public static final String BEGIN_FUNCTION = "begin";

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
    public QuestSignInteractionHandler(final RolecraftQuests plugin) {
        this.plugin = plugin;
        this.questingHandler = plugin.getQuestingHandler();
    }

    /**
     * {@inheritDoc}
     * @since 0.1.0
     */
    @Nonnull @Override
    public String getType() {
        return RolecraftQuests.QUEST_SIGN_TYPE;
    }

    /**
     * {@inheritDoc}
     * @since 0.1.0
     */
    @Override
    public void handleSignInteraction(@Nonnull final Player player,
            @Nonnull final RolecraftSign sign) {
        final String function = sign.getFunction();

        if (function.toLowerCase().equals("begin")) {
            final String questName = sign.getData();

            if (questingHandler.questExists(questName)) {
                questingHandler.beginQuest(questName, player);
            }
        }
        // TODO: other functions (abandon? check progress?)
    }
}
