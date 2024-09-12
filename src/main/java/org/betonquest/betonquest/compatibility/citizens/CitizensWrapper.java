package org.betonquest.betonquest.compatibility.citizens;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.betonquest.betonquest.api.quest.npc.NpcWrapper;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

/**
 * Citizens wrapper to get a Npc.
 */
class CitizensWrapper implements NpcWrapper<NPC> {
    /**
     * Id of the Npc.
     */
    private final int npcId;

    /**
     * Create a new Citizens Npc Wrapper.
     *
     * @param npcId the id of the Npc, greater or equals to zero
     */
    public CitizensWrapper(final int npcId) {
        this.npcId = npcId;
    }

    @Override
    public Npc<NPC> getNpc() throws QuestRuntimeException {
        final NPC npc = CitizensAPI.getNPCRegistry().getById(npcId);
        if (npc == null) {
            throw new QuestRuntimeException("NPC with ID " + npcId + " not found");
        }
        return new CitizensAdapter(npc);
    }
}
