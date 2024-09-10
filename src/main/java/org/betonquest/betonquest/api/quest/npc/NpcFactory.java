package org.betonquest.betonquest.api.quest.npc;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.quest.registry.type.TypeFactory;

/**
 * Factory to create specific {@link Npc}s from {@link Instruction}s.
 *
 * @param <T> the original Npc type
 */
public interface NpcFactory<T> extends TypeFactory<NpcWrapper<T>> {
    /**
     * Parses an instruction to create a {@link NpcWrapper}.
     *
     * @param instruction instruction to parse
     * @return npc referenced by the instruction
     * @throws InstructionParseException when the instruction cannot be parsed
     */
    @Override
    NpcWrapper<T> parseInstruction(Instruction instruction) throws InstructionParseException;

    /**
     * Gets the instruction string which would be used to identify this Npc.
     *
     * @param npc the Npc to get its identifier from
     * @return the identifying string as used inside {@link org.betonquest.betonquest.id.NpcID NpcId}s.
     */
    String npcToInstructionString(Npc<T> npc);

    /**
     * The class of the Npc to check if this factory can create the instruction.
     *
     * @return the class of the wrapper
     * @deprecated this is just bad design
     */
    @Deprecated
    Class<T> getNpcClass();
}
