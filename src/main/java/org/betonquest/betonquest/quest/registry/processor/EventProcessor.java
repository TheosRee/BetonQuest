package org.betonquest.betonquest.quest.registry.processor;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.quest.registry.type.EventTypeRegistry;
import org.jetbrains.annotations.Nullable;

/**
 * Stores Events and execute them.
 */
public class EventProcessor extends TypedQuestProcessor<EventID, StaticEvent, Event> {
    /**
     * Create a new Event Processor to store events and execute them.
     *
     * @param log        the custom logger for this class
     * @param eventTypes the available event types
     */
    public EventProcessor(final BetonQuestLogger log, final EventTypeRegistry eventTypes) {
        super(log, eventTypes, "Event", "events");
    }

    @Override
    protected EventID getIdentifier(final QuestPackage pack, final String identifier) throws ObjectNotFoundException {
        return new EventID(pack, identifier);
    }

    /**
     * Fires an event for the {@link Profile} if it meets the event's conditions.
     * If the profile is null, the event will be fired as a static event.
     *
     * @param profile the {@link Profile} for which the event must be executed or null
     * @param eventID ID of the event to fire
     * @return true if the event was run even if there was an exception during execution
     */
    public boolean execute(@Nullable final Profile profile, final EventID eventID) {
        final TrippleFactory.Wrapper<StaticEvent, Event> event = values.get(eventID);
        if (event == null) {
            log.warn(eventID.getPackage(), "Event " + eventID + " is not defined");
            return false;
        }
        try {
            if (profile != null && event.playerType() != null) {
                log.debug(eventID.getPackage(), "Firing event " + eventID + " for " + profile);
                return handleProfile(profile, event.playerType(), event.instruction().getPackage(), event.conditions());
            }
            if (event.playerlessType() != null) {
                log.debug(eventID.getPackage(), "Firing event " + eventID + " player independent");
                return handleNullProfile(event.playerlessType(), eventID.getPackage(), event.conditions());
            }
            log.warn(eventID.getPackage(), "Cannot fire non-static event '" + eventID + "' without a player!");
            return false;
        } catch (final QuestRuntimeException e) {
            log.warn(eventID.getPackage(), "Error while firing '" + eventID + "' event: " + e.getMessage(), e);
            return true;
        }
    }

    /**
     * Fires an event for the profile if it meets the event's conditions.
     * If the profile is null, the event will be fired as a static event.
     *
     * @param profile the {@link Profile} of the player for whom the event will fire
     * @return whether the event was successfully handled or not.
     * @throws QuestRuntimeException passes the exception from the event up the stack
     */
    private boolean handleProfile(final Profile profile, final Event event, final QuestPackage pack, final ConditionID... conditions) throws QuestRuntimeException {
        if (profile.getOnlineProfile().isEmpty()) {
            return handleOfflineProfile(profile, event, pack, conditions);
        } else {
            return handleOnlineProfile(profile, event, pack, conditions);
        }
    }

    private boolean handleNullProfile(final StaticEvent staticEvent, final QuestPackage pack, final ConditionID... conditions) throws QuestRuntimeException {
        log.debug(pack, "Static event will be fired without a profile.");
        if (!BetonQuest.conditions(null, conditions)) {
            log.debug(pack, "Event conditions were not met");
            return false;
        }
        staticEvent.execute();
        return true;
    }

    private boolean handleOfflineProfile(final Profile profile, final Event event, final QuestPackage pack, final ConditionID... conditions) throws QuestRuntimeException {
        if (true) { // TODO third state required (Online-/Profile
            log.debug(pack, "Persistent event will be fired for offline profile.");
            event.execute(profile);
            return true;
        } else {
            log.debug(pack, profile + " is offline, cannot fire event because it's not persistent.");
            return false;
        }
    }

    private boolean handleOnlineProfile(final Profile profile, final Event event, final QuestPackage pack, final ConditionID... conditions) throws QuestRuntimeException {
        if (!BetonQuest.conditions(profile, conditions)) {
            log.debug(pack, "Event conditions were not met for " + profile);
            return false;
        }
        event.execute(profile);
        return true;
    }
}
