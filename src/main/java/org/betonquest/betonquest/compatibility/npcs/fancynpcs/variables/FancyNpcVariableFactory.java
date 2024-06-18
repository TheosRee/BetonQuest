package org.betonquest.betonquest.compatibility.npcs.fancynpcs.variables;

import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.variables.npc.NPCVariableFactory;
import org.betonquest.betonquest.compatibility.npcs.fancynpcs.FancyNpcsSupplierStandard;

/**
 * Factory to create NPC Variables with {@link de.oliver.fancynpcs.api.Npc FancyNPC}s from Instructions.
 */
public class FancyNpcVariableFactory extends NPCVariableFactory implements FancyNpcsSupplierStandard {
    /**
     * Create a new FancyNPC Variable factory.
     *
     * @param loggerFactory the logger factory creating new custom logger
     */
    public FancyNpcVariableFactory(final BetonQuestLoggerFactory loggerFactory) {
        super(loggerFactory);
    }
}
