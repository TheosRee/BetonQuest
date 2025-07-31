package org.betonquest.betonquest.id;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.quest.QuestException;
import org.jetbrains.annotations.Nullable;

public class NewObjID extends ID {

    /**
     * Create a new Objective ID.
     *
     * @param pack       the package of the objective
     * @param identifier the complete identifier of the objective
     * @throws QuestException if there is no such objective
     */
    public NewObjID(@Nullable final QuestPackage pack, final String identifier) throws QuestException {
        super(pack, identifier, "new_objectives", "New Objective");
    }
}
