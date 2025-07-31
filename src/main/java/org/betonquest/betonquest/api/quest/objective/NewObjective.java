package org.betonquest.betonquest.api.quest.objective;

import org.betonquest.betonquest.api.profile.Profile;

public interface NewObjective {

    /**
     * This method is called by the plugin when the objective starts for a specific profile.
     *
     * @param profile the {@link Profile} of the player
     */
    default void start(final Profile profile) {
        //Empty
    }

    /**
     * This method is called by the plugin when the objective stop for a specific profile.
     *
     * @param profile the {@link Profile} of the player
     */
    default void stop(final Profile profile) {
        //Empty
    }

    /**
     * This method should return the default data instruction for the objective,
     * ready to be parsed by the ObjectiveData class.
     *
     * @return the default data instruction string
     */
    String getDefaultDataInstruction();

    /**
     * This method should return the default data instruction for the objective,
     * ready to be parsed by the ObjectiveData class.
     * Reimplement this method if you need profile context (e.g. for variable parsing) when creating the data instruction.
     *
     * @param profile the {@link Profile} to parse the instruction for
     * @return the default data instruction string
     */
    default String getDefaultDataInstruction(final Profile profile) {
        return getDefaultDataInstruction();
    }

    String getProperty(String name, Profile profile);

    boolean containsPlayer(final Profile profile);

    boolean checkConditions(final Profile profile);

    void completeObjective(final Profile profile);

    void newPlayer(Profile profile);

    void resumeObjectiveForPlayer(final Profile profile, final String instructionString);

    void close();

    void pauseObjectiveForPlayer(Profile profile);
}
