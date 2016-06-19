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

import com.github.rolecraftdev.quests.quest.QuestingHandler;

import com.volumetricpixels.questy.objective.OutcomeProgress;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Checks whether a specific outcome has been achieved based on type. Outcome
 * completion checkers for things such as reaching a required level, joining a
 * Rolecraft guild or picking a Rolecraft profession can be used to determine
 * whether a player has already completed the objective when they begin it. For
 * things such as killing entities, which must be achieved after the objective
 * has already been started, checkers are not required and thus all checks are
 * conducted in listeners.
 *
 * @since 0.1.0
 */
public interface OutcomeCompletionChecker {
    /**
     * Gets the type of outcome this checker checks for.
     *
     * @return the type of outcome this checker checks for
     * @since 0.1.0
     */
    @Nonnull String getType();

    /**
     * Checks whether the given quester has achieved the given outcome.
     *
     * @param questingHandler the questing handler to use in checks
     * @param outcome the outcome to check completion of
     * @param quester the quester to check completion for
     * @param data the newly updated data, if applicable
     * @return whether the outcome has been achieved by the quester
     * @since 0.1.0
     */
    boolean checkCompletion(@Nonnull QuestingHandler questingHandler,
            @Nonnull OutcomeProgress outcome, @Nonnull String quester,
            @Nullable Object data);
}
