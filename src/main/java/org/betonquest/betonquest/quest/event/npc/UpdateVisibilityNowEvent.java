package org.betonquest.betonquest.quest.event.npc;

import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.quest.event.online.OnlineEvent;
import org.betonquest.betonquest.api.quest.npc.feature.NpcHider;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

/**
 * Event to update the visibility of all Npcs to one player now.
 */
public class UpdateVisibilityNowEvent implements OnlineEvent {
    /**
     * Npc Hider to update the visibility.
     */
    private final NpcHider npcHider;

    /**
     * Create a new update visibility event.
     *
     * @param npcHider the hider to update the visibility
     */
    public UpdateVisibilityNowEvent(final NpcHider npcHider) {
        this.npcHider = npcHider;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestRuntimeException {
        npcHider.applyVisibility(profile);
    }
}
