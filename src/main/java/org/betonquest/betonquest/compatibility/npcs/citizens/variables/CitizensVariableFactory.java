package org.betonquest.betonquest.compatibility.npcs.citizens.variables;

import net.citizensnpcs.api.npc.NPC;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.variables.npc.NPCVariableFactory;
import org.betonquest.betonquest.compatibility.npcs.citizens.CitizensNPCSupplier;

/**
 * Factory to create NPCVariables with {@link NPC Citizens NPC}s from Instructions.
 */
public class CitizensVariableFactory extends NPCVariableFactory {
    /**
     * Create a new NPCVariable factory for Citizens NPCs.
     *
     * @param loggerFactory the logger factory creating new custom logger
     */
    public CitizensVariableFactory(final BetonQuestLoggerFactory loggerFactory) {
        super(loggerFactory, () -> CitizensNPCSupplier::getSupplierByIDStatic);
    }
}
