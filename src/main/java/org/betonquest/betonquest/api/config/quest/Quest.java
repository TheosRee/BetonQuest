package org.betonquest.betonquest.api.config.quest;

import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfiguration;
import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiSectionConfiguration;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.FileConfigAccessor;
import org.betonquest.betonquest.config.quest.QuestBase;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * This is the basic for managing a quest's files.
 */
public interface Quest {

    /**
     * Gets the path that addresses this {@link Quest}.
     *
     * @return the address
     */
    String getQuestPath();

    /**
     * Tries to save all modifications in the {@link MultiSectionConfiguration} to files.
     *
     * @return true, and only true if there are no unsaved changes
     * @throws IOException thrown if an exception was thrown by calling {@link FileConfigAccessor#save()}
     *                     or {@link MultiSectionConfiguration#getUnsavedConfigs()} returned a {@link ConfigurationSection},
     *                     that is not represented by this {@link Quest}
     */
    boolean saveAll() throws IOException;

    /**
     * Gets the existing {@link ConfigAccessor} for the {@code relativePath}.
     * If the {@link ConfigAccessor} for the {@code relativePath} does not exist, a new one is created.
     *
     * @param relativePath the relative path from the root of the package
     * @return the already existing or newly created {@link ConfigAccessor}
     * @throws InvalidConfigurationException thrown if there was an exception creating the new {@link ConfigAccessor}
     * @throws FileNotFoundException         thrown if the file for the new {@link ConfigAccessor} could not be found
     */
    ConfigAccessor getOrCreateConfigAccessor(String relativePath) throws InvalidConfigurationException, FileNotFoundException;

    /**
     * The {@link MultiConfiguration} that represents this {@link QuestBase}.
     *
     * @return this actual config defining this
     */
    MultiConfiguration getQuestConfig();
}
