package org.betonquest.betonquest.quest.registry.type;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.npc.NpcFactory;
import org.betonquest.betonquest.api.quest.npc.NpcWrapper;
import org.betonquest.betonquest.api.quest.npc.conversation.NpcConversationStarter;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Stores the npc types that can be used in BetonQuest.
 */
public class NpcTypeRegistry extends TypeRegistry<NpcWrapper<?>> {
    private final Map<Class<?>, String> factoryIdentifier;

    /**
     * Create a new npc type registry.
     *
     * @param log           the logger that will be used for logging
     * @param loggerFactory the logger factory to create a new logger for the legacy quest type factory created
     */
    public NpcTypeRegistry(final BetonQuestLogger log, final BetonQuestLoggerFactory loggerFactory) {
        super(log, loggerFactory, "npc");
        this.factoryIdentifier = new HashMap<>();
    }

    /**
     * Registers a type that does not support playerless execution with its name
     * and a player factory to create new player instances.
     *
     * @param name    the name of the type
     * @param factory the player factory to create the type
     */
    public <T> void register(final String name, final NpcFactory<T> factory, @Nullable final NpcConversationStarter<T> conversationStarter) {
        register(name, factory::parseInstruction); // TODO irgendwas mit generics…
        factoryIdentifier.put(factory.getClass(), name);
    }

    @Nullable
    public String getFactoryIdentifier(final NpcFactory<?> npcFactory) {
        return factoryIdentifier.get(npcFactory.getClass());
    }
}
