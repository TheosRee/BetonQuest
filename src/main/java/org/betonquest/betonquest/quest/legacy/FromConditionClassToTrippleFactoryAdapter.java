package org.betonquest.betonquest.quest.legacy;

import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.jetbrains.annotations.Nullable;

@Deprecated
public class FromConditionClassToTrippleFactoryAdapter extends FromClassToTrippleFactoryAdapter<Condition, PlayerlessCondition, PlayerCondition> {
    public FromConditionClassToTrippleFactoryAdapter(final FromClassLegacyTypeFactory<? extends Condition, Condition> factory) {
        super(factory);
    }

    @Override
    @Nullable
    protected PlayerlessCondition playerless(final Condition legacy) {
        return legacy.isStatic() ? () -> legacy.handle(null) : null;
    }

    @Override
    protected PlayerCondition player(final Condition legacy) {
        return legacy::handle;
    }
}
