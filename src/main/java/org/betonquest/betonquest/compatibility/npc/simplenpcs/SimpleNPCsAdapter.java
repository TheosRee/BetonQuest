package org.betonquest.betonquest.compatibility.npc.simplenpcs;

import com.github.arnhav.objects.SNPC;
import org.betonquest.betonquest.api.quest.npc.Npc;

public class SimpleNPCsAdapter implements Npc<SNPC> {
    private final SNPC npc;

    public SimpleNPCsAdapter(final SNPC npc) {
        this.npc = npc;
    }
}
