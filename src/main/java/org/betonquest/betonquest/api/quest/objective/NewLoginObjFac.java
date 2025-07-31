package org.betonquest.betonquest.api.quest.objective;

import org.betonquest.betonquest.instruction.Instruction;

/**
 * Factory to create {@link NewLoginObj}s from {@link Instruction}s.
 */
public class NewLoginObjFac extends NewSimpleObjFactory {

    /**
     * Create a new Factory.
     *
     * @param factoryData the data required for the objective
     */
    public NewLoginObjFac(final FactoryData factoryData) {
        super(factoryData);
    }

    @Override
    public NewSimpleObj createNewObjective(final Instruction instruction) {
        return new NewLoginObj(NewObjData.Factory.DEFAULT);
    }
}
