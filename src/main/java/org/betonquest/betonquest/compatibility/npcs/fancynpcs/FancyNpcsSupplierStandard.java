package org.betonquest.betonquest.compatibility.npcs.fancynpcs;

import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.Npc;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.BQNPCAdapter;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.NPCSupplierStandard;

import java.util.function.Supplier;

/**
 * FancyNPC supplier for the {@link BQNPCAdapter}.
 */
public interface FancyNpcsSupplierStandard extends NPCSupplierStandard {
    @Override
    default Supplier<BQNPCAdapter> getSupplierByID(final String npcId) {
        return () -> {
            final Npc npc = FancyNpcsPlugin.get().getNpcManager().getNpc(npcId);
            return npc == null ? null : new FanyNpcsBQAdapter(npc);
        };
    }
}
