package org.betonquest.betonquest.api.quest.objective;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.QuestTypeAPI;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.id.NewObjID;
import org.betonquest.betonquest.instruction.variable.Variable;

import java.util.List;

public record NewObjectiveMetadata(
        BetonQuestLogger log,
        NewObjID id,
        PlayerDataStorage playerDataStorage,
        QuestTypeAPI questTypeAPI,
        Variable<List<EventID>> events,
        Variable<List<ConditionID>> conditions,
        boolean persistent,
        boolean shouldNotify,
        int notifyInterval
) {

    /* default */ boolean checkConditions(final Profile profile) {
        log.debug(id.getPackage(), "Condition check in \"" + id + "\" objective for " + profile);
        try {
            return questTypeAPI.conditions(profile, conditions.getValue(profile));
        } catch (final QuestException e) {
            log.warn(id.getPackage(),
                    "Error while checking conditions in objective '" + id
                            + "' for " + profile + ": " + e.getMessage(), e);
            return false;
        }
    }

    public void completeObjective(final Profile profile) {
        log.debug(id.getPackage(), "Objective '" + id + "' has been completed for " + profile + ", firing events.");
        try {
            for (final EventID event : events.getValue(profile)) {
                questTypeAPI.event(profile, event);
            }
        } catch (final QuestException e) {
            log.warn(id.getPackage(), "Error while firing events in objective '" + id
                    + "' for " + profile + ": " + e.getMessage(), e);
        }
        log.debug(id.getPackage(), "Firing events in objective '" + id + "' for " + profile + " finished");
    }
}
