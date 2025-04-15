package org.betonquest.betonquest.quest.event.teleport;

import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.online.OnlineEvent;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Teleports the player to specified location.
 */
public class TeleportEvent implements OnlineEvent {
    /**
     * Location to teleport to.
     */
    private final VariableLocation location;

    /**
     * Create a new teleport event that teleports the player to the given location.
     *
     * @param location location to teleport to
     */
    public TeleportEvent(final VariableLocation location) {
        this.location = location;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        final Conversation conv = Conversation.getConversation(profile);
        if (conv != null) {
            conv.endConversation();
        }
        final Location playerLocation = location.getValue(profile);
        // Atlas start - "allow" teleportation with passengers
        final Player player = profile.getPlayer();
        final List<Entity> passengers = player.getPassengers();
        player.eject();
        player.teleportAsync(playerLocation).thenAccept(b -> {
            if (passengers.isEmpty()) return;
            player.addPassenger(passengers.get(0));
        });
        // Atlas end
    }
}
