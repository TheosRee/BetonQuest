package org.betonquest.betonquest.quest.registry.type;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.npc.NpcFactory;
import org.betonquest.betonquest.api.quest.npc.NpcWrapper;
import org.betonquest.betonquest.api.quest.npc.conversation.NpcInteractCatcher;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Stores the npc types that can be used in BetonQuest.
 */
public class NpcTypeRegistry extends TypeRegistry<NpcWrapper<?>> {
    /**
     * Factories mapped to their catcher to update their registered prefix.
     */
    private final Map<Class<?>, List<NpcInteractCatcher<?>>> starter;

    /**
     * Create a new npc type registry.
     *
     * @param log           the logger that will be used for logging
     * @param loggerFactory the logger factory to create a new logger for the legacy quest type factory created
     */
    public NpcTypeRegistry(final BetonQuestLogger log, final BetonQuestLoggerFactory loggerFactory) {
        super(log, loggerFactory, "npc");
        this.starter = new HashMap<>();
    }

    /**
     * Registers a npc factory with a {@link NpcInteractCatcher} to convert the third party interactions.
     *
     * @param name            the name of the type
     * @param factory         the player factory to create the type
     * @param interactCatcher the catcher to convert interactions
     * @param <T>             the original npc type
     */
    public <T> void register(final String name, final NpcFactory<T> factory, @Nullable final NpcInteractCatcher<T> interactCatcher) {
        register(name, factory::parseInstruction); // TODO irgendwas mit generics…
        final List<NpcInteractCatcher<?>> starterList = starter.computeIfAbsent(factory.getClass(), clazz -> new ArrayList<>());
        starterList.forEach(starter -> starter.setPrefix(name)); // TODO now the bad decision is here, yay!
        if (interactCatcher != null) {
            interactCatcher.setPrefix(name);
            starterList.add(interactCatcher);
        }
    }
}
