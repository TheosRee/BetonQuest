package org.betonquest.betonquest.quest.legacy;

import org.betonquest.betonquest.api.Variable;
import org.betonquest.betonquest.api.quest.variable.PlayerVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerlessVariable;
import org.jetbrains.annotations.Nullable;

@Deprecated
public class FromVariableClassToTrippleFactoryAdapter extends FromClassToTrippleFactoryAdapter<Variable, PlayerlessVariable, PlayerVariable> {
    public FromVariableClassToTrippleFactoryAdapter(final FromClassLegacyTypeFactory<? extends Variable, Variable> factory) {
        super(factory);
    }

    @Override
    @Nullable
    protected PlayerlessVariable playerless(final Variable legacy) {
        return legacy.isStaticness() ? () -> legacy.getValue(null) : null;
    }

    @Override
    protected PlayerVariable player(final Variable legacy) {
        return legacy::getValue;
    }
}
