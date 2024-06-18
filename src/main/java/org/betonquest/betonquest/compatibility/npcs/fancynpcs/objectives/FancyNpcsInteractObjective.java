package org.betonquest.betonquest.compatibility.npcs.fancynpcs.objectives;

import de.oliver.fancynpcs.api.events.NpcInteractEvent;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.objectives.NPCInteractObjective;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.objectives.EntityInteractObjective;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import static org.betonquest.betonquest.objectives.EntityInteractObjective.Interaction.ANY;
import static org.betonquest.betonquest.objectives.EntityInteractObjective.Interaction.LEFT;
import static org.betonquest.betonquest.objectives.EntityInteractObjective.Interaction.RIGHT;

/**
 * FancyNpcs implementation of {@link NPCInteractObjective}.
 */
public class FancyNpcsInteractObjective extends NPCInteractObjective {
    /**
     * Creates a new FancyNpcsInteractObjective from the given instruction.
     *
     * @param instruction the user-provided instruction
     * @throws InstructionParseException if the instruction is invalid
     */
    public FancyNpcsInteractObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
    }

    /**
     * Handles click events.
     *
     * @param event the event provided by the NPC plugin
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteract(final NpcInteractEvent event) {
        final boolean cancel = onNPCClick(event.getNpc().getData().getId(), convert(event.getInteractionType()), event.getPlayer());
        if (cancel) {
            event.setCancelled(true);
        }
    }

    private EntityInteractObjective.Interaction convert(final NpcInteractEvent.InteractionType interactionType) {
        return switch (interactionType) {
            case LEFT_CLICK -> LEFT;
            case RIGHT_CLICK -> RIGHT;
            case CUSTOM -> ANY;
        };
    }
}
