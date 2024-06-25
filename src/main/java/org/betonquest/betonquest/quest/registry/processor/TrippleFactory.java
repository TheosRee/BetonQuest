package org.betonquest.betonquest.quest.registry.processor;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.id.ConditionID;
import org.jetbrains.annotations.Nullable;

public interface TrippleFactory<S, P> {
    TrippleWrapper<S, P> parseInstruction(Instruction instruction) throws InstructionParseException;

    record ActualEntry<S, P>(Instruction instruction, @Nullable S playerlessType,
                             @Nullable P playerType, ConditionID... conditions) implements TrippleWrapper<S, P> {
        public ActualEntry {
            if (playerlessType == null && playerType == null) {
                throw new IllegalArgumentException("Either the playerless or player type must be present!");
            }
        }
    }
}
