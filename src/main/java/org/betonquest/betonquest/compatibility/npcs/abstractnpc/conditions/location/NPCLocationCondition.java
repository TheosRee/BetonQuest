package org.betonquest.betonquest.compatibility.npcs.abstractnpc.conditions.location;

import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.condition.Condition;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.BQNPCAdapter;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.location.CompoundLocation;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * Checks if a npc is at a specific location.
 */
@SuppressWarnings("PMD.CommentRequired")
public class NPCLocationCondition implements Condition {
    private final String npcId;

    private final Supplier<BQNPCAdapter> npcSupplier;

    private final CompoundLocation location;

    private final VariableNumber radius;

    public NPCLocationCondition(final String npcId, final Supplier<BQNPCAdapter> npcSupplier,
                                final CompoundLocation location, final VariableNumber radius) {
        this.npcId = npcId;
        this.npcSupplier = npcSupplier;
        this.location = location;
        this.radius = radius;
    }

    @Override
    public boolean check(@Nullable final Profile profile) throws QuestRuntimeException {
        final BQNPCAdapter npc = npcSupplier.get();
        if (npc == null) {
            throw new QuestRuntimeException("NPC with ID " + npcId + " does not exist");
        }
        final Location location = this.location.getLocation(profile);
        final Location npcLocation = npc.getLocation();
        if (!location.getWorld().equals(npcLocation.getWorld())) {
            return false;
        }
        final double radius = this.radius.getDouble(profile);
        return npcLocation.distanceSquared(location) <= radius * radius;
    }
}
