package org.betonquest.betonquest.api.quest.objective;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.QuestTypeAPI;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.id.NewObjID;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.kernel.registry.TypeFactory;

public abstract class NewSimpleObjFactory implements TypeFactory<NewObjective> {

    /**
     * Required data for Objective creation.
     */
    protected final FactoryData factoryData;

    /**
     * Create a new Factory.
     *
     * @param factoryData the data required for the objective
     */
    protected NewSimpleObjFactory(final FactoryData factoryData) {
        this.factoryData = factoryData;
    }

    @Override
    public NewObjective parseInstruction(final Instruction instruction) throws QuestException {
        final NewSimpleObj newObjective = createNewObjective(instruction);
        final int customNotifyInterval = instruction.getValue("notify", Argument.NUMBER, 0).getValue(null).intValue();
        final boolean notify = customNotifyInterval > 0 || instruction.hasArgument("notify");
        final int notifyInterval = Math.max(1, customNotifyInterval);
        final BetonQuestLogger log = factoryData.loggerFactory.create(newObjective.getClass());
        final NewObjectiveMetadata metadata = new NewObjectiveMetadata(log, (NewObjID) instruction.getID(),
                factoryData.playerDataStorage, factoryData.questTypeAPI,
                instruction.getValueList("events", EventID::new), instruction.getValueList("conditions", ConditionID::new),
                instruction.hasArgument("persistent"), notify, notifyInterval);
        newObjective.attach(factoryData.profileProvider, metadata);
        return newObjective;
    }

    /**
     * Creates the individual objective. The general metadata will be added in {@link #parseInstruction(Instruction)}.
     *
     * @param instruction the instruction to parse
     * @return the newly created objective
     * @throws QuestException if the instruction cannot be parsed
     */
    protected abstract NewSimpleObj createNewObjective(Instruction instruction) throws QuestException;

    /**
     * Data required for generic objective functionality.
     *
     * @param loggerFactory   the factory to create a class specific logger
     * @param profileProvider the provider used to retrieve the active profile for a player
     * @param questTypeAPI    the Quest Type API to check conditions and execute events
     */
    public record FactoryData(
            BetonQuestLoggerFactory loggerFactory,
            PlayerDataStorage playerDataStorage,
            ProfileProvider profileProvider,
            QuestTypeAPI questTypeAPI) {
    }
}
