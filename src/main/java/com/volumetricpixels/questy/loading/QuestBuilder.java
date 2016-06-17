/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.volumetricpixels.questy.loading;

import com.volumetricpixels.questy.Quest;
import com.volumetricpixels.questy.QuestManager;
import com.volumetricpixels.questy.QuestManager.QuestLoadHelper;
import com.volumetricpixels.questy.objective.Objective;
import com.volumetricpixels.questy.objective.Outcome;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Used for building {@link Quest}s. Objectives are cached to make it easy to
 * manage the same {@link Objective} instance being required in multiple parts
 * of the loading process.
 */
public final class QuestBuilder {
    /**
     * Gets a {@link QuestBuilder}, the {@link Quest} built by which will be
     * assigned to the given {@link QuestManager}, with the given name. If this
     * method has already been invoked with the same parameters, the same object
     * will be returned.
     *
     * @param manager the {@link QuestManager} for the {@link QuestBuilder} to
     *        use
     * @param name the name of the {@link QuestBuilder}, which will also be the
     *        name assigned to the {@link Quest} built if it is not later
     *        changed
     * @return a {@link QuestBuilder} for the given {@link QuestManager} and the
     *         given {@code name}
     */
    public static QuestBuilder begin(QuestManager manager, String name) {
        QuestLoadHelper helper = manager.getQuestLoadHelper();
        QuestBuilder result = helper.getBuilder(name);
        if (result == null) {
            // create a new builder as there isn't one already
            result = new QuestBuilder(manager).name(name);
            // add the new builder to the cache for later
            helper.addBuilder(name, result);
        }
        return result;
    }

    /**
     * A cache of already created {@link ObjectiveBuilder}s for {@link
     * Objective}s for this quest, where the keys are the names of the
     * {@link Objective}s.
     */
    private final Map<String, ObjectiveBuilder> objectiveBuilders = new HashMap<>();
    /**
     * The Questy {@link QuestManager} which the {@link Quest} being built is to
     * be assigned to.
     */
    private final QuestManager questManager;
    /**
     * A {@link List} of {@link ObjectiveBuilder}s for the {@link Objective}s
     * for this quest.
     */
    private final List<ObjectiveBuilder> objectives = new ArrayList<>();
    /**
     * A {@link Set} of the names of {@link Quest}s which are required to start
     * this {@link Quest}.
     */
    private final Set<String> prerequisites = new HashSet<>();
    /**
     * A {@link List} of rewards given for completing this {@link Quest}.
     */
    private final List<String> rewards = new ArrayList<>();

    /**
     * The name of the {@link Quest} to be built.
     */
    private String name;
    /**
     * The description of the {@link Quest} to be built.
     */
    private String description;
    /**
     * The message to be sent to a quester beginning the quest.
     */
    private String beginMessage;
    /**
     * The message to be sent to a quester finishing the quest.
     */
    private String finishMessage;
    /**
     * Only holds a non-null value if {@link #build()} has been invoked. Used to
     * allow the same {@link Quest} to be used in multiple places without
     * creating a new {@link Quest} instance.
     */
    private Quest built;

    /**
     * Do not call. Obtain instances from {@link
     * QuestBuilder#begin(QuestManager, String)}.
     *
     * @param questManager the {@link QuestManager} to use in building
     */
    private QuestBuilder(QuestManager questManager) {
        this.questManager = questManager;
    }

    /**
     * Sets the name of the {@link Quest} being built to the given {@code name}
     * parameter.
     *
     * @param name the name for the {@link Quest}
     * @return this {@link QuestBuilder} object
     */
    public QuestBuilder name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Sets the description of the {@link Quest} being built to the given
     * {@code description} parameter.
     *
     * @param description the description for the {@link Quest}
     * @return this {@link QuestBuilder} object
     */
    public QuestBuilder description(String description) {
        this.description = description;
        return this;
    }

    /**
     * Sets the message to be sent to a quester beginning the quest.
     *
     * @param beginMessage the message to be sent to a quester beginning the quest
     * @return this {@link QuestBuilder} object
     */
    public QuestBuilder beginMessage(String beginMessage) {
        this.beginMessage = beginMessage;
        return this;
    }

    /**
     * Sets the message to be sent to a quester finishing the quest.
     *
     * @param finishMessage the message to be sent to a quester finishing the quest
     * @return this {@link QuestBuilder} object
     */
    public QuestBuilder finishMessage(String finishMessage) {
        this.finishMessage = finishMessage;
        return this;
    }

    /**
     * Adds the given reward to this builder's {@link List} of quest rewards.
     *
     * @param reward the reward to add
     * @return this {@link QuestBuilder} object
     */
    public QuestBuilder reward(String reward) {
        rewards.add(reward);
        return this;
    }

    /**
     * Adds the {@link Quest} with the given name as a prerequisite in order to
     * begin the {@link Quest} being built.
     *
     * @param prerequisite the name of the quest to pre-require completion of
     * @return this {@link QuestBuilder} object
     */
    public QuestBuilder requireCompletion(String prerequisite) {
        prerequisites.add(prerequisite);
        return this;
    }

    /**
     * Obtains an {@link ObjectiveBuilder} for an {@link Objective} with the
     * given {@code name}. A new {@link ObjectiveBuilder} is created if there
     * isn't already one with the given name, otherwise the old one is reused.
     *
     * The {@link ObjectiveBuilder} returned is added to this {@link
     * QuestBuilder}'s {@link #objectives} {@link List} if a new one is created.
     *
     * @param name the name of the objective to get a builder for
     * @return a builder for an objective with the given name
     */
    public ObjectiveBuilder objective(String name) {
        ObjectiveBuilder temp = objectiveBuilders.get(name);
        if (temp != null) {
            return temp;
        }
        temp = new ObjectiveBuilder().name(name);
        objectiveBuilders.put(name, temp);
        objectives.add(temp);
        return temp;
    }

    /**
     * Builds this {@link Quest}. All of the associated {@link
     * ObjectiveBuilder}s are also built into {@link Objective}s, and therefore
     * all of the associated {@link Outcome}s are built as well.
     *
     * If this method has already been invoked, the same object is returned as
     * the last time it was invoked. This method automatically registers the
     * {@link Quest} which is built to the {@link QuestManager} which was
     * provided in {@link QuestBuilder#begin(QuestManager, String)}.
     *
     * @return a newly built {@link Quest} object created from the details
     *         submitted to this {@link QuestBuilder}
     */
    public Quest build() {
        if (built != null) {
            // we already built the Quest
            return built;
        }
        // build all of the associated ObjectiveBuilders
        Objective[] objs = new Objective[objectives.size()];
        for (int i = 0; i < objs.length; i++) {
            objs[i] = objectives.get(i).build();
        }
        return built = new Quest(questManager, name, description, beginMessage,
                finishMessage, objs,
                prerequisites.toArray(new String[prerequisites.size()]),
                rewards.toArray(new String[rewards.size()]));
    }

    /**
     * Used to build {@link Objective} objects and their {@link Outcome}s
     * (which are built using {@link OutcomeBuilder}).
     */
    public final class ObjectiveBuilder {
        /**
         * A cache of already created {@link OutcomeBuilder}s for {@link
         * Outcome}s for this quest, where the keys are the names of the
         * {@link Outcome}s.
         */
        private final Map<String, OutcomeBuilder> outcomeBuilders = new HashMap<>();
        /**
         * A {@link List} of the {@link OutcomeBuilder}s for the possible
         * {@link Outcome}s for the {@link Objective} being built from this
         * {@link ObjectiveBuilder}.
         */
        private final List<OutcomeBuilder> outcomes = new ArrayList<>();

        /**
         * The name of the {@link Objective} being built.
         */
        private String name;
        /**
         * The description of the {@link Objective} being built.
         */
        private String description;
        /**
         * The message to be sent to a player beginning this objective.
         */
        private String beginMessage;
        /**
         * Only holds a non-null value if {@link #build()} has been invoked.
         * Used to allow the same {@link Objective} to be used in multiple
         * places without creating a new {@link Objective} instance.
         */
        private Objective built;

        /**
         * Do not call. Obtain instances from {@link
         * QuestBuilder#objective(String)}.
         */
        private ObjectiveBuilder() {
        }

        /**
         * Gets the {@link QuestBuilder} which this {@link ObjectiveBuilder}
         * builds an objective for.
         *
         * @return this ObjectiveBuilder's QuestBuilder
         */
        public QuestBuilder quest() {
            return QuestBuilder.this;
        }

        /**
         * Sets the name of the {@link Objective} being built to the given
         * {@code name} parameter.
         *
         * @param name the name for the {@link Objective}
         * @return this {@link ObjectiveBuilder} object
         */
        public ObjectiveBuilder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * Sets the description of the {@link Objective} being built to the
         * given {@code description} parameter.
         *
         * @param description the description for the {@link Objective}
         * @return this {@link ObjectiveBuilder} object
         */
        public ObjectiveBuilder description(String description) {
            this.description = description;
            return this;
        }

        /**
         * Sets the message to be sent to a quester beginning the objective.
         *
         * @param beginMessage the message to be sent to a quester beginning the objective
         * @return this {@link ObjectiveBuilder} object
         */
        public ObjectiveBuilder beginMessage(String beginMessage) {
            this.beginMessage = beginMessage;
            return this;
        }

        /**
         * Obtains an {@link OutcomeBuilder} for an {@link Outcome} with the
         * given {@code name}. A new {@link OutcomeBuilder} is created if there
         * isn't already one with the given name, otherwise the old one is
         * reused.
         *
         * The {@link OutcomeBuilder} returned is added to this {@link
         * ObjectiveBuilder}'s {@link #outcomes} {@link List} if a new one is
         * created.
         *
         * @param name the name of the outcome to get a builder for
         * @return an {@link OutcomeBuilder} for an outcome with the given name
         */
        public OutcomeBuilder outcome(String name) {
            OutcomeBuilder temp = outcomeBuilders.get(name);
            if (temp != null) {
                return temp;
            }
            temp = new OutcomeBuilder().name(name);
            outcomeBuilders.put(name, temp);
            outcomes.add(temp);
            return temp;
        }

        /**
         * Builds an {@link Objective} object from the details submitted to this
         * {@link ObjectiveBuilder}. If this method has already been invoked,
         * the same object will be returned.
         *
         * @return an {@link Objective} built from the details submitted to this
         *         builder
         */
        private Objective build() {
            if (built != null) {
                // we already built the Objective
                return built;
            }
            Outcome[] array = new Outcome[outcomes.size()];
            // build all of the associated OutcomeBuilders
            for (int i = 0; i < array.length; i++) {
                array[i] = outcomes.get(i).build();
            }
            return built = new Objective(name, description, beginMessage,
                    array);
        }

        /**
         * Used to build {@link Outcome} objects.
         */
        public final class OutcomeBuilder {
            /**
             * The name of the {@link Outcome} being built.
             */
            private String name;
            /**
             * The description of the {@link Outcome} being built.
             */
            private String description;
            /**
             * The message to be sent to a player who achieves this outcome to
             * an objective they are performing.
             */
            private String finishMessage;
            /**
             * The type of the {@link Outcome} being built.
             */
            private String type;
            /**
             * The {@link ObjectiveBuilder} for the {@link Objective} which the
             * {@link Outcome} built from this {@link OutcomeBuilder} leads to.
             */
            private ObjectiveBuilder next;
            /**
             * Only holds a non-null value if {@link #build()} has been invoked.
             * Used to allow the same {@link Outcome} to be used in multiple
             * places without creating a new {@link Outcome} instance.
             */
            private Outcome built;

            /**
             * Do not call. Obtain instances from {@link
             * ObjectiveBuilder#outcome(String)}.
             */
            private OutcomeBuilder() {
            }

            /**
             * Gets the {@link QuestBuilder} which this {@link OutcomeBuilder}'s
             * {@link ObjectiveBuilder} builds an objective for.
             *
             * @return this OutcomeBuilder's ObjectiveBuilder's QuestBuilder
             */
            public QuestBuilder quest() {
                return objective().quest();
            }

            /**
             * Gets the {@link ObjectiveBuilder} which this {@link
             * OutcomeBuilder} builds an outcome for.
             *
             * @return this OutcomeBuilder's ObjectiveBuilder
             */
            public ObjectiveBuilder objective() {
                return ObjectiveBuilder.this;
            }

            /**
             * Sets the name of the {@link Outcome} being built to the given
             * {@code name} parameter.
             *
             * @param name the name for the {@link Outcome}
             * @return this {@link Outcome} object
             */
            public OutcomeBuilder name(String name) {
                this.name = name;
                return this;
            }

            /**
             * Sets the description of the {@link Outcome} being built to the
             * given {@code description} parameter.
             *
             * @param description the description for the {@link Outcome}
             * @return this {@link OutcomeBuilder} object
             */
            public OutcomeBuilder description(String description) {
                this.description = description;
                return this;
            }

            /**
             * Sets the message to be sent to a player who achieves this outcome
             * to an objective they are performing.
             *
             * @param finishMessage the new message to be sent to a player who achieves this outcome
             * @return this {@link OutcomeBuilder} object
             */
            public OutcomeBuilder finishMessage(String finishMessage) {
                this.finishMessage = finishMessage;
                return this;
            }

            /**
             * Sets the type of the {@link Outcome} being built to the given
             * {@code type} parameter.
             *
             * @param type the type for the {@link Outcome}
             * @return this {@link OutcomeBuilder} object
             */
            public OutcomeBuilder type(String type) {
                this.type = type;
                return this;
            }

            /**
             * Sets the {@link Objective} which the {@link Outcome} built by
             * this builder will lead to.
             *
             * @param next the next {@link Objective} when this Outcome is
             *        completed
             * @return this {@link OutcomeBuilder} object
             */
            public OutcomeBuilder next(ObjectiveBuilder next) {
                this.next = next;
                return this;
            }

            /**
             * Builds an {@link Outcome} object from the details submitted to
             * this {@link OutcomeBuilder}. If this method has already been
             * invoked, the same object will be returned.
             *
             * @return an {@link Outcome} built from the details submitted to
             *         this builder
             */
            private Outcome build() {
                if (built != null) {
                    // we already built the Outcome
                    return built;
                }
                return built = new Outcome(name, description, finishMessage,
                        type, next == null ? null : next.build());
            }
        }
    }
}
