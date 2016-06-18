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
import com.github.rolecraftdev.quests.quest.completion.OutcomeCompletionChecker;

import com.volumetricpixels.questy.objective.ObjectiveProgress;
import com.volumetricpixels.questy.objective.OutcomeProgress;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Allows a check to see whether a quest objective has been completed.
 *
 * @since 0.1.0
 */
public final class QuestObjectiveCompletionChecker {
    /**
     * The {@link RolecraftQuests} plugin instance.
     */
    private final RolecraftQuests plugin;
    /**
     * The {@link QuestingHandler} used by the {@link RolecraftQuests} instance.
     */
    private final QuestingHandler questingHandler;
    /**
     * A {@link Map} of objective outcome types to outcome completion checkers
     * of those types.
     */
    private final Map<String, OutcomeCompletionChecker> outcomeCheckers;

    /**
     * Constructor.
     *
     * @param plugin the {@link RolecraftQuests} plugin instance
     * @since 0.1.0
     */
    public QuestObjectiveCompletionChecker(
            @Nonnull final RolecraftQuests plugin) {
        this.plugin = plugin;
        this.questingHandler = plugin.getQuestingHandler();
        this.outcomeCheckers = new HashMap<>();
    }

    /**
     * Checks whether any possible outcomes of the given
     * {@link ObjectiveProgress} have been achieved by the given quester, and
     * returns the one which has been completed in an {@link Optional}.
     *
     * @param objective the quester's progress in the objective which is being
     *        checked
     * @param quester the player UUID of the quester to check progress for
     * @return the completed outcome progress, or empty if there isn't one
     * @since 0.1.0
     */
    public Optional<OutcomeProgress> checkCompletion(
            final ObjectiveProgress objective, final String quester,
            final Object newData) {
        for (final OutcomeProgress outcome : objective.getOutcomeProgresses()) {
            final String type = outcome.getInfo().getType();
            final OutcomeCompletionChecker checker = outcomeCheckers.get(type);

            if (checker != null && checker.checkCompletion(questingHandler,
                    outcome, quester, newData)) {
                return Optional.of(outcome);
            }
        }

        return Optional.empty();
    }

    /**
     * Registers the given {@link OutcomeCompletionChecker} to this quest
     * objective completion checker. Note that this overwrites any previous
     * outcome checkers of the same type, as there may only be one checker
     * registered for any given outcome type at a time.
     *
     * {@link OutcomeCompletionChecker#getType()} is used to determine the type
     * of checker being registered.
     *
     * @param checker the {@link OutcomeCompletionChecker} to register
     * @since 0.1.0
     */
    public void registerOutcomeChecker(
            @Nonnull final OutcomeCompletionChecker checker) {
        this.outcomeCheckers.put(checker.getType(), checker);
    }
}
