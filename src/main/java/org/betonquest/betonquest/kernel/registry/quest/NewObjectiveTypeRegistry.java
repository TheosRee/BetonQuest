package org.betonquest.betonquest.kernel.registry.quest;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.quest.objective.NewObjective;
import org.betonquest.betonquest.kernel.registry.FactoryRegistry;
import org.betonquest.betonquest.kernel.registry.TypeFactory;

/**
 * Stores the Objectives that can be used in BetonQuest.
 */
public class NewObjectiveTypeRegistry extends FactoryRegistry<TypeFactory<NewObjective>> {

    /**
     * Create a new Objective registry.
     *
     * @param log the logger that will be used for logging
     */
    public NewObjectiveTypeRegistry(final BetonQuestLogger log) {
        super(log, "objective");
    }
}
