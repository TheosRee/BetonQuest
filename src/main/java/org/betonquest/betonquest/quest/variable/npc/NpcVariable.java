package org.betonquest.betonquest.quest.variable.npc;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.betonquest.betonquest.api.quest.variable.PlayerlessVariable;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.id.NpcID;
import org.betonquest.betonquest.quest.registry.processor.NpcProcessor;
import org.betonquest.betonquest.variables.LocationVariable;
import org.jetbrains.annotations.Nullable;

/**
 * Provides information about a npc.
 */
public class NpcVariable implements PlayerlessVariable {
    /**
     * The custom {@link BetonQuestLogger} for this class.
     */
    private final BetonQuestLogger log;

    /**
     * The Supplier for the NPC.
     */
    private final NpcProcessor npcProcessor;

    /**
     * Id of the npc.
     */
    private final NpcID npcID;

    /**
     * The type of information to retrieve for the NPC: name, full_name, or location.
     */
    private final Argument key;

    /**
     * A wrapper for the location property of the NPC.
     */
    @Nullable
    private final LocationVariable location;

    /**
     * Construct a new NPCVariable that allows for resolution of information about a NPC.
     *
     * @param npcProcessor the processor to get npc
     * @param npcID        the npc id
     * @param key          the argument defining the value
     * @param location     the location to provide when
     * @param log          the custom logger to use when the variable cannot be resolved
     * @throws IllegalArgumentException when location argument is given without location variable
     */
    public NpcVariable(final NpcProcessor npcProcessor, final NpcID npcID, final Argument key,
                       @Nullable final LocationVariable location, final BetonQuestLogger log) {
        this.npcProcessor = npcProcessor;
        this.npcID = npcID;
        this.key = key;
        this.location = location;
        if (key == Argument.LOCATION && location == null) {
            throw new IllegalArgumentException("The location argument requires a location variable!");
        }
        this.log = log;
    }

    @Override
    public String getValue() {
        try {
            final Npc<?> npc = npcProcessor.getNpc(npcID);
            return key.resolve(npc, location);
        } catch (final QuestRuntimeException exception) {
            log.warn("Can't get Npc for variable!", exception);
            return "";
        }
    }
}
