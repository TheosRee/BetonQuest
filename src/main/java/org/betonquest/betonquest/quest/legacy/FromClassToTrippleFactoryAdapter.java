package org.betonquest.betonquest.quest.legacy;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.quest.registry.processor.TrippleFactory;
import org.jetbrains.annotations.Nullable;

@Deprecated
public abstract class FromClassToTrippleFactoryAdapter<L, S, P> implements TrippleFactory<S, P> {
    private final FromClassLegacyTypeFactory<?, L> factory;

    public FromClassToTrippleFactoryAdapter(final FromClassLegacyTypeFactory<? extends L, L> factory) {
        this.factory = factory;
    }

    @Override
    public Wrapper<S, P> parseInstruction(final Instruction instruction) throws InstructionParseException {
        final L legacy = factory.parseInstruction(instruction);
        return new Wrapper<>(instruction, playerless(legacy), player(legacy));
    }

    @Nullable
    protected abstract S playerless(L legacy);

    protected abstract P player(L legacy);
}
