package org.betonquest.betonquest.compatibility.npcs.abstractnpc.conditions.region;

import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.BQNPCAdapter;
import org.betonquest.betonquest.compatibility.worldguard.WorldGuardIntegrator;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

import java.util.function.Supplier;

/**
 * Checks if a npc is inside a region.
 */
@SuppressWarnings("PMD.CommentRequired")
public class NPCRegionCondition implements PlayerlessCondition {
    private final Supplier<BQNPCAdapter> npcSupplier;

    private final String region;

    public NPCRegionCondition(final Supplier<BQNPCAdapter> npcSupplier, final String region) {
        this.npcSupplier = npcSupplier;
        this.region = region;
    }

    @Override
    public boolean check() throws QuestRuntimeException {
        final BQNPCAdapter npc = npcSupplier.get();
        return npc != null && WorldGuardIntegrator.isInsideRegion(npc.getLocation(), region);
    }
}
