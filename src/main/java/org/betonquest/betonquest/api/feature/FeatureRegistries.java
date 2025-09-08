package org.betonquest.betonquest.api.feature;

import org.betonquest.betonquest.api.kernel.FactoryRegistry;
import org.betonquest.betonquest.api.kernel.TypeFactory;
import org.betonquest.betonquest.api.quest.npc.NpcWrapper;
import org.betonquest.betonquest.api.text.TextParserRegistry;
import org.betonquest.betonquest.conversation.ConversationIOFactory;
import org.betonquest.betonquest.conversation.interceptor.InterceptorFactory;
import org.betonquest.betonquest.item.QuestItemWrapper;
import org.betonquest.betonquest.notify.NotifyIOFactory;
import org.betonquest.betonquest.schedule.EventScheduling;

public interface FeatureRegistries {
    FactoryRegistry<ConversationIOFactory> conversationIO();

    FactoryRegistry<TypeFactory<QuestItemWrapper>> item();

    FactoryRegistry<InterceptorFactory> interceptor();

    TextParserRegistry textParser();

    FactoryRegistry<TypeFactory<NpcWrapper<?>>> npc();

    FactoryRegistry<NotifyIOFactory> notifyIO();

    FactoryRegistry<EventScheduling.ScheduleType<?, ?>> eventScheduling();
}
