package org.betonquest.betonquest.compatibility;

import java.util.List;

/**
 * Holds all possible integrations for a single plugin.
 */
public interface IntegrationSource {

    /**
     * Gets a list of registered integrations.
     *
     * @return immutable list of data
     */
    List<IntegrationData> getDataList();

    /**
     * Gets the name of the source.
     *
     * @return source name to display
     */
    String getName();
}
