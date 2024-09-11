package org.betonquest.betonquest.quest.variable.npc;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableInstruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.variable.PlayerlessVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerlessVariableFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.id.NoID;
import org.betonquest.betonquest.id.NpcID;
import org.betonquest.betonquest.quest.registry.processor.NpcProcessor;
import org.betonquest.betonquest.variables.LocationVariable;

/**
 * Factory to create {@link NpcVariable}s from {@link Instruction}s.
 * <p>
 * Format:
 * {@code %<variableName>.<id>.<argument>.<mode>.<precision>%}
 * <p>
 * Arguments:<br>
 * * name - Return npc name<br>
 * * full_name - Full npc name<br>
 * * location - Return npc location, defaults to ulfLong<br>
 * Modes: refer to LocationVariable documentation for details.<br>
 *
 * @see LocationVariable
 */
public class NpcVariableFactory implements PlayerlessVariableFactory {
    /**
     * Logger Factory for creating new Instruction logger.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Providing a new NPC Adapter from an id.
     */
    private final NpcProcessor npcProcessor;

    /**
     * Create a new factory to create NPC Variables.
     *
     * @param npcProcessor  the supplier providing the npc adapter
     * @param loggerFactory the logger factory creating new custom logger
     */
    public NpcVariableFactory(final NpcProcessor npcProcessor, final BetonQuestLoggerFactory loggerFactory) {
        this.npcProcessor = npcProcessor;
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerlessVariable parsePlayerless(final Instruction instruction) throws InstructionParseException {
        final NpcID npcID = instruction.getNpc();
        final Argument key = instruction.getEnum(Argument.class);
        final LocationVariable location = key == Argument.LOCATION ? parseLocation(instruction) : null;
        return new NpcVariable(npcProcessor, npcID, key, location, loggerFactory.create(NpcVariable.class));
    }

    private LocationVariable parseLocation(final Instruction instruction) throws InstructionParseException {
        try {
            final Instruction locationInstruction = new VariableInstruction(
                    loggerFactory.create(Instruction.class),
                    instruction.getPackage(),
                    new NoID(instruction.getPackage()),
                    "%location." + String.join(".", instruction.getRemainingParts()) + "%"
            );
            locationInstruction.current();
            return new LocationVariable(locationInstruction);
        } catch (final ObjectNotFoundException e) {
            throw new InstructionParseException("Could not generate dynamic location variable", e);
        }
    }
}
