package org.betonquest.betonquest.compatibility.npcs.fancynpcs;

import de.oliver.fancynpcs.api.Npc;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.BQNPCAdapter;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.NPCConversation;
import org.betonquest.betonquest.id.ConversationID;
import org.bukkit.Location;

/**
 * Represents a conversation with FancyNpcs NPC.
 */
public class FancyNpcsConversation extends NPCConversation {
    /**
     * NPC for this conversation.
     */
    private final Npc npc;

    /**
     * {@inheritDoc}
     *
     * @param npcAdapter the npc adapter used for this conversation
     */
    public FancyNpcsConversation(final BetonQuestLogger log, final OnlineProfile onlineProfile, final ConversationID conversationID,
                                 final Location center, final Npc npc, final BQNPCAdapter npcAdapter) {
        super(log, onlineProfile, conversationID, center, npcAdapter);
        this.npc = npc;
    }

    /**
     * This will return the NPC associated with this conversation only after the
     * conversation is created (all player options are listed and ready to
     * receive player input)
     * TODO is this javadoc valid?
     *
     * @return the NPC or null if it's too early
     */
    public Npc getFancyNpcsNPC() {
        return npc;
    }
}
