package org.betonquest.betonquest.quest.registry.type;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.betonquest.betonquest.api.quest.npc.NpcWrapper;
import org.betonquest.betonquest.id.NpcID;
import org.betonquest.betonquest.quest.registry.FactoryRegistry;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Stores the npc types that can be used in BetonQuest.
 */
public class NpcTypeRegistry extends FactoryRegistry<TypeFactory<NpcWrapper<?>>> {
    /**
     * Identifier to get {@link NpcID}s from a specific Npc.
     */
    private final List<NpcReverseIdentifier> backFires;

    /**
     * Create a new npc type registry.
     *
     * @param log the logger that will be used for logging
     */
    public NpcTypeRegistry(final BetonQuestLogger log) {
        super(log, "npc");
        this.backFires = new ArrayList<>();
    }

    /**
     * Registers a reverse-identifier to allow matching npcs to their in BQ used IDs.
     *
     * @param backFire the object to register reverse used npc ids
     */
    public void registerIdentifier(final NpcReverseIdentifier backFire) {
        backFires.add(backFire);
    }

    /**
     * Adds the id to the "instruction -> ID" mapping to identify external npc interaction.
     *
     * @param npcId the id to add store in the mapping
     */
    public void addIdentifier(final NpcID npcId) {
        for (final NpcReverseIdentifier backFire : backFires) {
            backFire.addID(npcId);
        }
    }

    /**
     * Gets the IDs used to get a Npc.
     *
     * @param npc     the npc to get the npc ids
     * @param profile the related profile potentially resolving influencing
     * @return the ids used in BetonQuest to identify the Npc
     */
    public Set<NpcID> getIdentifier(final Npc<?> npc, final OnlineProfile profile) {
        final Set<NpcID> npcIDS = new HashSet<>();
        for (final NpcReverseIdentifier backFire : backFires) {
            npcIDS.addAll(backFire.getIdsFromNpc(npc, profile));
        }
        return npcIDS;
    }
}
