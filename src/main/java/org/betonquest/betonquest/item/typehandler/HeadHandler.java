package org.betonquest.betonquest.item.typehandler;

import io.papermc.lib.PaperLib;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Handles metadata about player Skulls.
 */
public abstract class HeadHandler {
    /**
     * Owner metadata about the Skull.
     */
    public static final String META_OWNER = "owner";

    /**
     * PlayerId metadata about the Skull.
     */
    public static final String META_PLAYER_ID = "player-id";

    /**
     * Encoded texture metadata about the Skull.
     */
    public static final String META_TEXTURE = "texture";

    /**
     * Variable placeholder literal for player name.
     */
    private static final String VARIABLE_PLAYER_NAME = "%player%";

    /**
     * An optional player name owner of the skull.
     */
    public final AhProfileSomething owner = new AhProfileSomething();

    /**
     * An optional player ID owner of the skull, used in conjunction with the encoded texture.
     */
    public final AhTStuff<UUID> playerId = new AhTStuff<>(false) {
        @Override
        protected UUID convertStringToValue(final String value) {
            return UUID.fromString(value);
        }
    };

    /**
     * An optional encoded texture URL of the skull, used in conjunction with the player UUID.
     */
    public final AhStringStuff texture = new AhStringStuff(false);

    /**
     * Construct a new HeadHandler.
     */
    public HeadHandler() {
    }

    /**
     * Get an appropriate implementation of the HeadHandler based upon the type of server running.
     *
     * @return An appropriate HeadHandler instance.
     */
    public static HeadHandler getServerInstance() {
        if (PaperLib.isPaper()) {
            return new PaperHeadHandler();
        } else {
            return new SpigotHeadHandler(BetonQuest.getInstance().getLoggerFactory().create(SpigotHeadHandler.class));
        }
    }

    /**
     * Serialize the specified SkullMeta data into a String for item persistence.
     *
     * @param skullMeta The SkullMeta data to serialize.
     * @return A String representation of the SkullMeta data.
     */
    public static String serializeSkullMeta(final SkullMeta skullMeta) {
        final Map<String, String> props;
        if (PaperLib.isPaper()) {
            props = PaperHeadHandler.parseSkullMeta(skullMeta);
        } else {
            props = SpigotHeadHandler.parseSkullMeta(skullMeta);
        }
        return props.entrySet().stream()
                .map(it -> it.getKey() + ":" + it.getValue())
                .collect(Collectors.joining(" ", " ", ""));
    }

    /**
     * Reconstitute this head data into the specified skullMeta object.
     *
     * @param skullMeta The SkullMeta object to populate.
     * @param profile   An optional Profile.
     */
    public abstract void populate(SkullMeta skullMeta, @Nullable Profile profile);

    /**
     * Check to see if the specified SkullMeta matches this HeadHandler metadata.
     *
     * @param skullMeta The SkullMeta to check.
     * @return True if this metadata is required and matches, false otherwise.
     */
    public abstract boolean check(SkullMeta skullMeta);

    public class AhProfileSomething extends AhStringStuff {
        public AhProfileSomething() {
            super(true);
        }

        @Nullable
        @Override
        public String get() {
            throw new IllegalStateException("Use #getvalue(Profile profile)!");
        }

        /**
         * Get the profile of the skull's owner.
         * Also resolves the owner name to a player if it is a variable.
         *
         * @param profile The Profile that the item is made for
         * @return The profile of the skull's owner.
         */
        @Nullable
        public Profile getOwner(@Nullable final Profile profile) {
            if (profile != null && VARIABLE_PLAYER_NAME.equals(owner.get())) {
                return profile;
            }
            if (owner.get() != null) {
                final OfflinePlayer player = Bukkit.getOfflinePlayer(owner.get());
                return PlayerConverter.getID(player);
            }
            return null;
        }
    }
}
