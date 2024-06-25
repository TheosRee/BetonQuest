package org.betonquest.betonquest.quest.legacy;

import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.jetbrains.annotations.Nullable;

@Deprecated
public class FromEventClassToTrippleFactoryAdapter extends FromClassToTrippleFactoryAdapter<QuestEvent, StaticEvent, Event> {
    public FromEventClassToTrippleFactoryAdapter(final FromClassLegacyTypeFactory<? extends QuestEvent, QuestEvent> factory) {
        super(factory);
    }

    @Override
    @Nullable
    protected StaticEvent playerless(final QuestEvent legacy) {
        return legacy.isStaticness() ? () -> legacy.handle(null) : null;
    }

    @Override
    protected Event player(final QuestEvent legacy) {
        return legacy::fire;
    }
}
