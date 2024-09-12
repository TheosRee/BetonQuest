package org.betonquest.betonquest.compatibility.citizens;

import net.citizensnpcs.api.npc.NPC;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.betonquest.betonquest.api.quest.npc.NpcFactory;
import org.betonquest.betonquest.api.quest.npc.NpcWrapper;
import org.betonquest.betonquest.exceptions.InstructionParseException;

/**
 * Creates validated Npc Wrapper for Citizens Npcs.
 */
public class CitizensNpcFactory implements NpcFactory<NPC> {
    /**
     * The default Constructor.
     */
    public CitizensNpcFactory() {
    }

    @Override
    public NpcWrapper<NPC> parseInstruction(final Instruction instruction) throws InstructionParseException {
        final int npcId = instruction.getInt();
        if (npcId < 0) {
            throw new InstructionParseException("NPC ID cannot be less than 0");
        }
        return new CitizensWrapper(npcId);
    }

    @Override
    public String npcToInstructionString(final Npc<NPC> npc) {
        return String.valueOf(npc.getOriginal().getId());
    }
}
