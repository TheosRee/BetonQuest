package org.betonquest.betonquest.compatibility.npcs.citizens;

import net.citizensnpcs.api.npc.NPC;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.BQNPCAdapter;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.NPCConversation;
import org.betonquest.betonquest.id.ConversationID;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a conversation with NPC
 */
@SuppressWarnings("PMD.CommentRequired")
public class CitizensConversation extends NPCConversation {
    /**
     * Citizens NPC used in this conversation.
     */
    private final NPC npc;

    /**
     * Lazy NPC adapter for this Conversation.
     */
    @Nullable
    private BQNPCAdapter npcAdapter;

    /**
     * {@inheritDoc}
     *
     * @param npc the Citizens NPC used for this conversation
     */
    public CitizensConversation(final BetonQuestLogger log, final OnlineProfile onlineProfile, final ConversationID conversationID,
                                final Location location, final NPC npc, final BQNPCAdapter adapter) {
        super(log, onlineProfile, conversationID, location, adapter);
        this.npc = npc;
    }

    /**
     * This will return the NPC associated with this conversation only after the
     * conversation is created (all player options are listed and ready to
     * receive player input)
     * TODO check for doc correctness
     *
     * @return the NPC or null if it's too early
     */
    public NPC getCitizensNPC() {
        return npc;
    }

    @Override
    public BQNPCAdapter getNPC() {
        if (npcAdapter == null) {
            npcAdapter = new CitizensBQAdapter(npc);
        }
        return npcAdapter;
    }
}
