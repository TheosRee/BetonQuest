package org.betonquest.betonquest.kernel.registry.feature;

import org.betonquest.betonquest.api.kernel.FactoryRegistry;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.conversation.interceptor.InterceptorFactory;

/**
 * Stores the Interceptors that can be used in BetonQuest.
 */
public class InterceptorRegistry extends FactoryRegistry<InterceptorFactory> {

    /**
     * Create a new Interceptor registry.
     *
     * @param log the logger that will be used for logging
     */
    public InterceptorRegistry(final BetonQuestLogger log) {
        super(log, "Interceptor");
    }
}
