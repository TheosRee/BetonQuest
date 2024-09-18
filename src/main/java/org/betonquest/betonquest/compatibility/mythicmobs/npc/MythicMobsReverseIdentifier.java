package org.betonquest.betonquest.compatibility.mythicmobs.npc;

import io.lumine.mythic.core.mobs.ActiveMob;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.betonquest.betonquest.api.quest.npc.NpcReverseIdentifier;
import org.betonquest.betonquest.id.NpcID;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Allows to get {@link NpcID}s for an {@link ActiveMob}.
 */
public class MythicMobsReverseIdentifier implements NpcReverseIdentifier {
    /**
     * The MythicMobs {@link NpcID} prefix.
     */
    private static final String PREFIX = "mythicmobs ";

    /**
     * Maps the contents of ids to the ids having that content.
     */
    private final Map<String, Set<NpcID>> idsByInstruction;

    /**
     * The default constructor.
     */
    public MythicMobsReverseIdentifier() {
        idsByInstruction = new HashMap<>();
    }

    @Override
    public Set<NpcID> getIdsFromNpc(final Npc<?> npc, @Nullable final OnlineProfile profile) {
        if (!(npc.getOriginal() instanceof final ActiveMob original)) {
            return Set.of();
        }
        final Set<NpcID> valid = new HashSet<>();
        for (final Type value : Type.values()) {
            final Set<NpcID> byID = idsByInstruction.get(PREFIX + value.toInstructionString(original));
            if (byID != null) {
                valid.addAll(byID);
            }
        }
        return valid;
    }

    @Override
    public void addID(final NpcID npcId) {
        final String instruction = npcId.getInstruction().toString();
        if (instruction.startsWith(PREFIX)) {
            idsByInstruction.computeIfAbsent(instruction, string -> new HashSet<>()).add(npcId);
        }
    }

    @Override
    public void reset() {
        idsByInstruction.clear();
    }
}
