package org.betonquest.betonquest.atlas.conversation;

import io.papermc.paper.connection.PlayerGameConnection;
import io.papermc.paper.event.player.PlayerCustomClickEvent;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.api.BinaryTagHolder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.conversation.ConversationColors;
import org.betonquest.betonquest.conversation.io.TellrawConvIO;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CustomClickTellrawConvIO extends TellrawConvIO {

    public CustomClickTellrawConvIO(final Conversation conv, final OnlineProfile onlineProfile, final ConversationColors colors) {
        super(conv, onlineProfile, colors);
    }

    // Override this event from our parent
    @Override
    @EventHandler(ignoreCancelled = true)
    public void onCommandAnswer(final PlayerCommandPreprocessEvent event) {
        // Empty
    }

    @EventHandler(ignoreCancelled = true)
    public void onClickAnswer(final PlayerCustomClickEvent event) {
        if (!(event.getCommonConnection() instanceof final PlayerGameConnection conn)) return;
        final Player player = conn.getPlayer();
        if (!player.equals(onlineProfile.getPlayer())) return;
        final Key key = event.getIdentifier();
        final String hash = key.value();
        for (int j = 1; j <= hashes.size(); j++) {
            if (hash.equals(hashes.get(j - 1))) {
                conv.sendMessage(colors.getAnswer().append(colors.getPlayer().append(Component.text(onlineProfile.getPlayer().getName())))
                        .append(Component.text(": ")).append(options.get(j)));
                conv.passPlayerAnswer(j);
                return;
            }
        }
    }

    @Override
    protected void displayText() {
        for (int j = 1; j <= options.size(); j++) {
            final Component message = Component.empty()
                    .clickEvent(ClickEvent.custom(Key.key("bq" + ":" + hashes.get(j - 1)), BinaryTagHolder.binaryTagHolder("bqanswer")))
                    .append(colors.getOption().append(colors.getNumber().append(Component.text(j)).append(Component.text(". ")))
                            .append(options.get(j)));

            conv.sendMessage(message);
        }
    }
}
