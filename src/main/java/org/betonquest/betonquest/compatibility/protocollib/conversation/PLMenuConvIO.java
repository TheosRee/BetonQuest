package org.betonquest.betonquest.compatibility.protocollib.conversation;

import com.comphenix.packetwrapper.WrapperPlayClientSteerVehicle;
import com.comphenix.packetwrapper.WrapperPlayServerAnimation;
import com.comphenix.packetwrapper.WrapperPlayServerMount;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import io.papermc.lib.PaperLib;
import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.common.component.FixedComponentLineWrapper;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.compatibility.protocollib.conversation.display.Scroll;
import org.betonquest.betonquest.compatibility.protocollib.wrappers.WrapperPlayClientSteerVehicleUpdated;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.conversation.ConversationColors;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * A {@link MenuConvIO} that uses ProtocolLib packets for its functionality.
 */
@SuppressWarnings("PMD.CouplingBetweenObjects")
public class PLMenuConvIO extends MenuConvIO {

    /**
     * The packet adapter used to intercept packets.
     */
    @Nullable
    protected PacketAdapter packetAdapter;

    /**
     * The armor stand used to steer the conversation.
     */
    @Nullable
    private ArmorStand stand;

    /**
     * Creates a new MenuConvIO instance working with PL packet interceptor.
     *
     * @param conv                 the conversation this IO is part of
     * @param onlineProfile        the online profile of the player participating in the conversation
     * @param colors               the colors used in the conversation
     * @param settings             the settings for the conversation IO
     * @param componentLineWrapper the component line wrapper to use for the conversation
     * @param plugin               the plugin instance to run tasks
     * @param controls             the used controls
     */
    public PLMenuConvIO(final Conversation conv, final OnlineProfile onlineProfile, final ConversationColors colors, final MenuConvIOSettings settings, final FixedComponentLineWrapper componentLineWrapper, final Plugin plugin, final Map<CONTROL, ACTION> controls) {
        super(conv, onlineProfile, colors, settings, componentLineWrapper, plugin, controls);
    }

    @Override
    protected void initPlayer() {
        final Player player = onlineProfile.getPlayer();
        final Location target = getBlockBelowPlayer(player).add(0, -1, 0);
        // TODO version switch:
        //  Remove this code when only 1.20.2+ is supported
        stand = player.getWorld().spawn(target.add(0, PaperLib.isVersion(20, 2) ? -0.375 : -0.131_25, 0), ArmorStand.class);

        stand.setGravity(false);
        stand.setVisible(false);
        final AttributeInstance attribute = stand.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (attribute != null) {
            attribute.setBaseValue(0);
        }

        // Mount the player to it using packets
        final WrapperPlayServerMount mount = new WrapperPlayServerMount();
        mount.setEntityID(stand.getEntityId());
        mount.setPassengerIds(new int[]{player.getEntityId()});

        // Send Packets
        mount.sendPacket(player);

        // Display Actionbar to hide the dismount message
        player.sendActionBar(Component.empty());

        // Intercept Packets
        packetAdapter = getPacketAdapter();
        ProtocolLibrary.getProtocolManager().addPacketListener(packetAdapter);
    }

    /**
     * Gets the location on the top of the block below the player.
     * This prevents the conversation's steering armor stand from spawning in the air.
     * <p>
     * This is done by getting the bounding box of the player.
     * Then all bounding boxes of the blocks in the bounding box of the player are checked for collision.
     * The highest collision is then returned.
     * If no collision is found the process is repeated with the player bounding box shifted down by 1.
     *
     * @param player the player to get the location for
     * @return the location on the top of the block below the player
     */
    private Location getBlockBelowPlayer(final Player player) {
        if (player.isFlying()) {
            return player.getLocation();
        }

        final BoundingBox playerBoundingBox = player.getBoundingBox();
        playerBoundingBox.shift(0, -(playerBoundingBox.getMinY() % 1), 0);
        while (playerBoundingBox.getMinY() >= player.getWorld().getMinHeight()) {
            final Set<Block> blocks = getBlocksInBoundingBox(player.getWorld(), playerBoundingBox);

            final List<BoundingBox> boundingBoxes = blocks.stream()
                    .map(block -> block.getCollisionShape().getBoundingBoxes().stream()
                            .map(box -> box.shift(block.getLocation())).toList())
                    .flatMap(Collection::stream)
                    .filter(box -> box.overlaps(playerBoundingBox))
                    .toList();

            if (!boundingBoxes.isEmpty()) {
                final Optional<Double> maxY = boundingBoxes.stream()
                        .map(BoundingBox::getMaxY)
                        .max(Double::compareTo);
                final Location location = player.getLocation();
                location.setY(maxY.get());
                return location;
            }
            playerBoundingBox.shift(0, -1, 0);
        }
        return player.getLocation();
    }

    /**
     * Get the blocks that are at the bottom corners of the player's bounding box.
     * This could be 1, 2 or 4 blocks depending on the player's position.
     *
     * @param world             the world the player is in
     * @param playerBoundingBox the bounding box of the player
     * @return the blocks in the bounding box
     */
    private Set<Block> getBlocksInBoundingBox(final World world, final BoundingBox playerBoundingBox) {
        final Set<Block> blocks = new HashSet<>();
        blocks.add(new Location(world, playerBoundingBox.getMinX(), playerBoundingBox.getMinY(), playerBoundingBox.getMinZ()).getBlock());
        blocks.add(new Location(world, playerBoundingBox.getMinX(), playerBoundingBox.getMinY(), playerBoundingBox.getMaxZ()).getBlock());
        blocks.add(new Location(world, playerBoundingBox.getMaxX(), playerBoundingBox.getMinY(), playerBoundingBox.getMinZ()).getBlock());
        blocks.add(new Location(world, playerBoundingBox.getMaxX(), playerBoundingBox.getMinY(), playerBoundingBox.getMaxZ()).getBlock());
        return blocks;
    }

    @SuppressWarnings("PMD.CognitiveComplexity")
    private PacketAdapter getPacketAdapter() {
        return new PacketAdapter(plugin, ListenerPriority.HIGHEST,
                PacketType.Play.Client.STEER_VEHICLE,
                PacketType.Play.Server.ANIMATION
        ) {

            @Override
            public void onPacketSending(final PacketEvent event) {
                if (!event.getPacketType().equals(PacketType.Play.Server.ANIMATION)) {
                    return;
                }
                final WrapperPlayServerAnimation animation = new WrapperPlayServerAnimation(event.getPacket());
                if (animation.getEntityID() == onlineProfile.getPlayer().getEntityId()) {
                    event.setCancelled(true);
                }
            }

            @SuppressWarnings("PMD.CyclomaticComplexity")
            @Override
            public void onPacketReceiving(final PacketEvent event) {
                if (!event.getPlayer().equals(onlineProfile.getPlayer()) || options.isEmpty()) {
                    return;
                }
                if (!event.getPacketType().equals(PacketType.Play.Client.STEER_VEHICLE)) {
                    return;
                }

                final WrapperPlayClientSteerVehicle steerEvent;

                if (PaperLib.isVersion(21, 3)) {
                    steerEvent = new WrapperPlayClientSteerVehicleUpdated(event.getPacket());
                } else {
                    steerEvent = new WrapperPlayClientSteerVehicle(event.getPacket());
                }

                if (steerEvent.isJump() && controls.containsKey(CONTROL.JUMP)) {
                    // Player Jumped
                    switch (controls.get(CONTROL.JUMP)) {
                        case CANCEL:
                            if (!conv.isMovementBlock()) {
                                conv.endConversation();
                            }
                            break;
                        case SELECT:
                            lock.lock();
                            try {
                                passPlayerAnswer();
                            } finally {
                                lock.unlock();
                            }
                            break;
                        case MOVE:
                            break;
                    }
                } else if (steerEvent.getForward() < 0 && controls.containsKey(CONTROL.MOVE)) {
                    Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> PLMenuConvIO.this.updateDisplay(Scroll.DOWN));
                } else if (steerEvent.getForward() > 0 && controls.containsKey(CONTROL.MOVE)) {
                    Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> PLMenuConvIO.this.updateDisplay(Scroll.UP));
                } else if (steerEvent.isUnmount() && controls.containsKey(CONTROL.SNEAK)) {
                    switch (controls.get(CONTROL.SNEAK)) {
                        case CANCEL:
                            if (!conv.isMovementBlock()) {
                                conv.endConversation();
                            }
                            break;
                        case SELECT:
                            lock.lock();
                            try {
                                if (!isOnCooldown()) {
                                    passPlayerAnswer();
                                }
                            } finally {
                                lock.unlock();
                            }
                            break;
                        case MOVE:
                            break;
                    }
                }
                event.setCancelled(true);
            }
        };
    }

    @Override
    protected void clean() {
        if (packetAdapter != null) {
            ProtocolLibrary.getProtocolManager().removePacketListener(packetAdapter);
        }
        if (stand != null) {
            Bukkit.getScheduler().runTask(plugin, () -> {
                if (stand != null) {
                    stand.remove();
                    stand = null;
                }
            });
        }
    }
}
