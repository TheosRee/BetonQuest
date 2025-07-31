package org.betonquest.betonquest.api.quest.objective;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Requires the player to join the server.
 */
public class NewLoginObj extends NewSimpleObj implements Listener {
    /**
     * Constructor for the LoginObjective.
     */
    public NewLoginObj(final NewObjData.Factory dataFactory) {
        super(dataFactory);
    }

    /**
     * Check if the player has joined the server.
     *
     * @param event the event that triggers when the player joins
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onJoin(final PlayerJoinEvent event) {
        final OnlineProfile onlineProfile = getProfile(event.getPlayer());
        if (containsPlayer(onlineProfile) && checkConditions(onlineProfile)) {
            completeObjective(onlineProfile);
            event.getPlayer().sendMessage(Component.text(
                    "You completed an objective with the new API!", NamedTextColor.LIGHT_PURPLE));
        }
    }

    @Override
    public String getDefaultDataInstruction() {
        return "";
    }

    @Override
    public String getProperty(final String name, final Profile profile) {
        return "";
    }
}
