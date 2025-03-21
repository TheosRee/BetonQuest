package org.betonquest.betonquest.kernel.registry.feature;

import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.message.MessageParserRegistry;
import org.betonquest.betonquest.kernel.registry.FactoryRegistry;

/**
 * Registries that are not based on the Instruction to create instances.
 *
 * @param conversationIO  The Registry holding registered conversation IOs.
 * @param interceptor     The Registry holding registered Interceptors.
 * @param messageParser   The Registry holding registered Message Parsers.
 * @param notifyIO        The Registry holding registered NotifyIOs.
 * @param eventScheduling The Registry holding registered Event Schedulers.
 */
public record FeatureRegistries(
        ConversationIORegistry conversationIO,
        InterceptorRegistry interceptor,
        MessageParserRegistry messageParser,
        NotifyIORegistry notifyIO,
        ScheduleRegistry eventScheduling
) {

    /**
     * Create a new other factory registry for quest elements not based on the Instruction object.
     *
     * @param loggerFactory the logger factory to create individual class logger
     * @return the newly created registries
     */
    public static FeatureRegistries create(final BetonQuestLoggerFactory loggerFactory) {
        return new FeatureRegistries(
                new ConversationIORegistry(loggerFactory.create(ConversationIORegistry.class)),
                new InterceptorRegistry(loggerFactory.create(FactoryRegistry.class)),
                new MessageParserRegistryImpl(loggerFactory.create(MessageParserRegistryImpl.class)),
                new NotifyIORegistry(loggerFactory.create(NotifyIORegistry.class)),
                new ScheduleRegistry(loggerFactory.create(ScheduleRegistry.class))
        );
    }
}
