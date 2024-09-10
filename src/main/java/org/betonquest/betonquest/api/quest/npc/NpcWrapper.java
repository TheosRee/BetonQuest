package org.betonquest.betonquest.api.quest.npc;

import org.betonquest.betonquest.exceptions.QuestRuntimeException;

/**
 * A validated wrapper for a {@link Npc}.
 *
 * @param <T> the original npc type
 */
public interface NpcWrapper<T> {
    /**
     * Gets the Npc represented by this Wrapper.
     *
     * @return the npc ready to use
     * @throws QuestRuntimeException when the Npc cannot be found
     */
    Npc<T> getNpc() throws QuestRuntimeException;
}
