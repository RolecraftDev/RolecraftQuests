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

/**
 * Contains constant values for the various quest objective outcome types which
 * are implemented by RolecraftQuests. All constant values are lowercase.
 *
 * @since 0.1.0
 */
public abstract class ObjectiveOutcomeTypes {
    /**
     * An outcome which requires the killing of a single player - either a
     * specific player or just any single player.
     *
     * @since 0.1.0
     */
    public static final String KILL_PLAYER = "killplayer";
    /**
     * An outcome which requires the killing of a set number of other players.
     *
     * @since 0.1.0
     */
    public static final String KILL_PLAYERS = "killplayers";
    /**
     * An outcome which requires the player to select a Rolecraft profession.
     *
     * @since 0.1.0
     */
    public static final String SELECT_PROFESSION = "selectprofession";
    /**
     * An outcome which requires the player to join a Rolecraft guild.
     *
     * @since 0.1.0
     */
    public static final String JOIN_GUILD = "joinguild";
    /**
     * An outcome which requires the player to reach a certain Rolecraft level.
     *
     * @since 0.1.0
     */
    public static final String REACH_LEVEL = "reachlevel";
    /**
     * An outcome which requires the player to collect a certain item(s).
     *
     * @since 0.1.0
     */
    public static final String ACQUIRE_ITEMS = "acquireitems";
}
