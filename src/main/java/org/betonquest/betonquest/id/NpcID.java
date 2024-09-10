package org.betonquest.betonquest.id;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.jetbrains.annotations.Nullable;

/**
 * Identifies a {@link org.betonquest.betonquest.api.quest.npc.Npc Npc} via the path syntax.
 * Handles relative and absolute paths.
 */
public class NpcID extends ID {
    /**
     * Creates a new Npc id.
     *
     * @param pack       the package the ID is in
     * @param identifier the id instruction string
     * @throws ObjectNotFoundException if the ID could not be parsed
     */
    public NpcID(@Nullable final QuestPackage pack, final String identifier) throws ObjectNotFoundException {
        super(pack, identifier, "npc_definitions", "Npc");
    }
}
