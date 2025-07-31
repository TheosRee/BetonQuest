package org.betonquest.betonquest.api.quest.objective;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.common.function.QuestRunnable;
import org.betonquest.betonquest.api.common.function.QuestSupplier;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.profile.ProfileKeyMap;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.database.PlayerData;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@SuppressWarnings("NullAway.Init")
public abstract class NewSimpleObj implements NewObjective {

    /**
     * Exception Handler to not spam the log.
     */
    protected final QEHandler qeHandler = new QEHandler();

    private final NewObjData.Factory dataFactory;

    /**
     * Contains all data objects of the profiles with this objective active.
     */
    private Map<Profile, NewObjData> dataMap;

    private ProfileProvider profileProvider;

    private NewObjectiveMetadata metadata;

    protected NewSimpleObj(final NewObjData.Factory dataFactory) {
        this.dataFactory = dataFactory;
    }

    /* default */
    final void attach(final ProfileProvider profileProvider, final NewObjectiveMetadata metadata) {
        this.profileProvider = profileProvider;
        this.metadata = metadata;
        this.dataMap = new ProfileKeyMap<>(profileProvider);
    }

    protected OnlineProfile getProfile(final Player player) {
        return profileProvider.getProfile(player);
    }

    protected NewObjectiveMetadata getMetadata() {
        return metadata;
    }

    protected Map<Profile, NewObjData> getDataMap() {
        return dataMap;
    }

    @Override
    public final boolean containsPlayer(final Profile profile) {
        return dataMap.containsKey(profile);
    }

    @Override
    public final boolean checkConditions(final Profile profile) {
        return metadata.checkConditions(profile);
    }

    /**
     * This method fires events for the objective and removes it from the profile's
     * list of active objectives. Use it when you detect that the objective has
     * been completed. It deletes the objective using delete() method.
     *
     * @param profile the {@link Profile} for which the objective is to be completed
     */
    @Override
    public final void completeObjective(final Profile profile) {
        completeObjectiveForPlayer(profile);
        final PlayerData playerData = BetonQuest.getInstance().getPlayerDataStorage().get(profile);
        playerData.removeRawObjective(metadata.id());
        if (metadata.persistent()) {
            playerData.addNewRawObjective(metadata.id());
            createObjectiveForPlayer(profile, getDefaultDataInstruction(profile));
        }
        metadata.completeObjective(profile);
    }

    /**
     * Adds this new objective to the profile. Also updates the database with the
     * objective.
     *
     * @param profile the {@link Profile} for which the objective is to be added
     */
    @Override
    public final void newPlayer(final Profile profile) {
        final String defaultInstruction = getDefaultDataInstruction(profile);
        createObjectiveForPlayer(profile, defaultInstruction);
        BetonQuest.getInstance().getPlayerDataStorage().get(profile).addObjToDB(metadata.id().getFullID(), defaultInstruction);
    }

    /**
     * Starts a new objective for the profile.
     *
     * @param profile           the {@link Profile} for which the objective is to be started
     * @param instructionString the objective data instruction
     * @see #resumeObjectiveForPlayer(Profile, String)
     */
    public final void createObjectiveForPlayer(final Profile profile, final String instructionString) {
        startObjective(profile, instructionString, Objective.ObjectiveState.NEW);
    }

    /**
     * Resumes a paused objective for the profile.
     *
     * @param profile           the {@link Profile} for which the objective is to be resumed
     * @param instructionString the objective data instruction
     * @see #createObjectiveForPlayer(Profile, String)
     */
    @Override
    public final void resumeObjectiveForPlayer(final Profile profile, final String instructionString) {
        startObjective(profile, instructionString, Objective.ObjectiveState.PAUSED);
    }

    /**
     * Start an objective for the profile. This lower level method allows to set the previous state directly. If possible
     * prefer {@link #createObjectiveForPlayer(Profile, String)} and {@link #resumeObjectiveForPlayer(Profile, String)}.
     *
     * @param profile           the {@link Profile} for which the objective is to be started
     * @param instructionString the objective data instruction
     * @param previousState     the objective's previous state
     */
    @SuppressWarnings("PMD.AvoidSynchronizedStatement")
    public final void startObjective(final Profile profile, final String instructionString, final Objective.ObjectiveState previousState) {
        synchronized (this) {
            try {
                final NewObjData objData = dataFactory.create(instructionString, profile, metadata.id().getFullID());
                startObjectiveWithEvent(profile, objData, previousState);
            } catch (final QuestException exception) {
                metadata.log().warn(metadata.id().getPackage(), "Error while loading " + metadata.id() + " objective data for "
                        + profile + ": " + exception.getMessage(), exception);
            }
        }
    }

    private void startObjectiveWithEvent(final Profile profile, final NewObjData data, final Objective.ObjectiveState previousState) {
        runObjectiveChangeEvent(profile, previousState, Objective.ObjectiveState.ACTIVE);
        dataMap.put(profile, data);
        start(profile);
    }

    /**
     * Complete an active objective for the profile. It will only remove it from the profile and not run any completion
     * events, run {@link #completeObjective(Profile)} instead! It does also not remove it from the database.
     *
     * @param profile the {@link Profile} for which the objective is to be completed
     * @see #cancelObjectiveForPlayer(Profile)
     * @see #pauseObjectiveForPlayer(Profile)
     */
    public final void completeObjectiveForPlayer(final Profile profile) {
        stopObjective(profile, Objective.ObjectiveState.COMPLETED);
    }

    /**
     * Cancel an active objective for the profile. It will only remove it from the profile and not remove it from the
     * database.
     *
     * @param profile the {@link Profile} for which the objective is to be cancelled
     * @see #completeObjectiveForPlayer(Profile)
     * @see #pauseObjectiveForPlayer(Profile)
     */
    public final void cancelObjectiveForPlayer(final Profile profile) {
        stopObjective(profile, Objective.ObjectiveState.CANCELED);
    }

    /**
     * Pause an active objective for the profile.
     *
     * @param profile the {@link Profile} for which the objective is to be paused
     * @see #completeObjectiveForPlayer(Profile)
     * @see #cancelObjectiveForPlayer(Profile)
     */
    @Override
    public final void pauseObjectiveForPlayer(final Profile profile) {
        stopObjective(profile, Objective.ObjectiveState.PAUSED);
    }

    /**
     * Stops an objective for the profile. This lower level method allows to set the previous state directly. If possible
     * prefer {@link #completeObjectiveForPlayer(Profile)}, {@link #cancelObjectiveForPlayer(Profile)} and
     * {@link #pauseObjectiveForPlayer(Profile)}.
     *
     * @param profile  the {@link Profile} for which the objective is to be stopped
     * @param newState the objective's new state
     */
    @SuppressWarnings("PMD.AvoidSynchronizedStatement")
    public final void stopObjective(final Profile profile, final Objective.ObjectiveState newState) {
        synchronized (this) {
            runObjectiveChangeEvent(profile, Objective.ObjectiveState.ACTIVE, newState);
            stop(profile);
            dataMap.remove(profile);
        }
    }

    private void runObjectiveChangeEvent(final Profile profile, final Objective.ObjectiveState previousState,
                                         final Objective.ObjectiveState newState) {
//        BetonQuest.getInstance().callSyncBukkitEvent(
//                new PlayerObjectiveChangeEvent(profile, this, metadata.id(), newState, previousState)
//        );
    }

    /**
     * Returns the data of the specified profile.
     *
     * @param profile the {@link Profile} to get the data for
     * @return the data string for this objective
     */
    @Nullable
    public final String getData(final Profile profile) {
        final NewObjData data = dataMap.get(profile);
        if (data == null) {
            return null;
        }
        return data.toString();
    }

    @Override
    public void close() {
        for (final Map.Entry<Profile, NewObjData> entry : dataMap.entrySet()) {
            final Profile profile = entry.getKey();
            stop(profile);
            metadata.playerDataStorage().get(profile).addRawObjective(metadata.id().getFullID(),
                    entry.getValue().toString());
        }
    }

    protected class QEHandler {

        /**
         * Interval in which errors are logged.
         */
        public static final int ERROR_RATE_LIMIT_MILLIS = 5000;

        /**
         * The last time when an error message was logged, in milliseconds.
         */
        public long last;

        /**
         * Runs a task and logs occurring quest exceptions with a rate limit.
         *
         * @param qeThrowing   a task that may throw a quest exception
         * @param defaultValue the default value to return in case of an exception
         * @param <T>          the type of the result
         * @return the result of the task or the default value if an exception occurs
         */
        public <T> T handle(final QuestSupplier<T> qeThrowing, final T defaultValue) {
            try {
                return qeThrowing.get();
            } catch (final QuestException e) {
                if (System.currentTimeMillis() - last >= ERROR_RATE_LIMIT_MILLIS) {
                    last = System.currentTimeMillis();
                    metadata.log().warn(metadata.id().getPackage(),
                            "Error while handling '" + metadata.id() + "' objective: " + e.getMessage(), e);
                }
                return defaultValue;
            }
        }

        /**
         * Runs a task and logs occurring quest exceptions with a rate limit.
         *
         * @param qeThrowing a task that may throw a quest exception
         */
        @SuppressWarnings("NullAway")
        public void handle(final QuestRunnable qeThrowing) {
            handle(() -> {
                qeThrowing.run();
                return null;
            }, null);
        }
    }
}
