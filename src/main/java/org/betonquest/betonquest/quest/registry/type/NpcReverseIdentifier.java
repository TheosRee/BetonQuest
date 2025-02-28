package org.betonquest.betonquest.quest.registry.type;

import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.betonquest.betonquest.id.NpcID;

import java.util.Set;

/**
 * Allows Integrations to identify to a {@link Npc} associated {@link NpcID}s.
 */
public interface NpcReverseIdentifier {

    /**
     * Gets the instruction strings which could be used to identify the Npc.
     *
     * @param npc     the Npc to get its identifier from
     * @param profile the related profile allowing finer selection decision
     * @return all ids this thing can identify the npc
     */
    Set<NpcID> getIdsFromNpc(final Npc<?> npc, final OnlineProfile profile);

    /**
     * Adds an ID to be possible used to identify npcs.
     *
     * @param npcId the id to register
     */
    void addID(final NpcID npcId);
}
