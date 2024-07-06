package ree.theos.bqnpcsaddon.playernpc.objectives;

import dev.sergiferry.playernpc.api.NPC;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.objectives.NPCInteractObjective;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.objectives.EntityInteractObjective;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import static org.betonquest.betonquest.objectives.EntityInteractObjective.Interaction.*;

/**
 * PlayerNPC implementation of {@link NPCInteractObjective}.
 */
public class PlayerNPCInteractObjective extends NPCInteractObjective {
    /**
     * Creates a new PlayerNPC NPC Interact Objective from the given instruction.
     *
     * @param instruction the user-provided instruction
     * @throws InstructionParseException if the instruction is invalid
     */
    public PlayerNPCInteractObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
    }

    /**
     * Handles click events.
     *
     * @param event the event provided by the NPC plugin
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteract(final NPC.Events.Interact event) {
        final boolean cancel = onNPCClick(event.getNPC().getFullID(), convert(event.getClickType()), event.getPlayer());
        if (cancel) {
            event.setCancelled(true);
        }
    }

    @SuppressWarnings("deprecation")
    private EntityInteractObjective.Interaction convert(final NPC.Interact.ClickType interactionType) {
        return switch (interactionType) {
            case LEFT_CLICK -> LEFT;
            case RIGHT_CLICK -> RIGHT;
            case EITHER -> ANY;
        };
    }
}