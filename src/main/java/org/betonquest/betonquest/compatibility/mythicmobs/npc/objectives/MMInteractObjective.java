package org.betonquest.betonquest.compatibility.mythicmobs.npc.objectives;

import io.lumine.mythic.bukkit.events.MythicMobInteractEvent;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.objectives.NPCInteractObjective;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.objectives.EntityInteractObjective;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

/**
 * Mythic Mobs implementation of {@link NPCInteractObjective}.
 */
public class MMInteractObjective extends NPCInteractObjective {
    /**
     * Creates a new NPCInteractObjective from the given instruction.
     *
     * @param instruction the user-provided instruction
     * @throws InstructionParseException if the instruction is invalid
     */
    public MMInteractObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
    }

    /**
     * Handles click events.
     *
     * @param event the event provided by the NPC plugin
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteract(final MythicMobInteractEvent event) {
        final boolean cancel = onNPCClick(event.getActiveMob().getUniqueId().toString(), EntityInteractObjective.Interaction.RIGHT, event.getPlayer());
        if (cancel) {
            event.setCancelled();
        }
    }
}
