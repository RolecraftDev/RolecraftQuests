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
package com.github.rolecraftdev.quests.quest.completion;

import com.github.rolecraftdev.quests.RQUtil;
import com.github.rolecraftdev.quests.quest.QuestingHandler;

import com.volumetricpixels.questy.objective.OutcomeProgress;

import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.github.rolecraftdev.quests.quest.ObjectiveOutcomeTypes.REACH_LEVEL;

/**
 * Checks whether a player has reached the required level for an experience
 * related quest outcome.
 *
 * @since 0.1.0
 */
public class ExperienceOutcomeCompletionChecker
        implements OutcomeCompletionChecker {
    /**
     * The name of this {@link OutcomeCompletionChecker}'s type.
     */
    public static final String TYPE = "experience";

    /**
     * The {@link QuestingHandler} instance to be used by this checker.
     */
    private final QuestingHandler questingHandler;

    /**
     * Constructor.
     *
     * @param questingHandler the {@link QuestingHandler} to be used
     */
    public ExperienceOutcomeCompletionChecker(
            @Nonnull final QuestingHandler questingHandler) {
        this.questingHandler = questingHandler;
    }

    /**
     * {@inheritDoc}
     * @since 0.1.0
     */
    @Nonnull @Override
    public String getType() {
        return TYPE;
    }

    /**
     * {@inheritDoc}
     * @since 0.1.0
     */
    @Override
    public boolean checkCompletion(@Nonnull QuestingHandler questingHandler,
            @Nonnull OutcomeProgress outcome, @Nonnull String quester,
            @Nullable Object data) {
        if (data == null || !(data instanceof Integer)) {
            return false; // this checker isn't relevant
        }

        final Player player = questingHandler.getPlayer(quester);
        if (player == null) {
            return false; // TODO: implement for offline players??
        }

        final String type = outcome.getInfo().getType().toLowerCase();
        if (type.startsWith(REACH_LEVEL)) {
            final String[] split = type.split(RQUtil.QUOTED_UNDERSCORE);
            if (split.length < 2) {
                return false; // invalid outcome type
            }

            final int level = Integer.parseInt(split[1]);
            if ((Integer) data >= level) {
                outcome.setProgress(1);
                return true;
            }
        }

        return false;
    }
}
