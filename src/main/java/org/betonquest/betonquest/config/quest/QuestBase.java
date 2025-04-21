package org.betonquest.betonquest.config.quest;

import org.betonquest.betonquest.api.bukkit.config.custom.multi.KeyConflictException;
import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfiguration;
import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiSectionConfiguration;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.ConfigAccessorFactory;
import org.betonquest.betonquest.api.config.FileConfigAccessor;
import org.betonquest.betonquest.api.config.quest.Quest;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is a basic implementation for managing a quest's files.
 */
public class QuestBase implements Quest {
    /**
     * The merged {@link MultiConfiguration} that represents this {@link QuestBase}.
     */
    protected final MultiConfiguration config;

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * The factory that will be used to create {@link ConfigAccessor}s.
     */
    private final ConfigAccessorFactory configAccessorFactory;

    /**
     * The address of this {@link QuestBase}.
     */
    private final String questPath;

    /**
     * The root folder of this {@link QuestBase}.
     */
    private final File root;

    /**
     * The list of all {@link ConfigAccessor}s of this {@link QuestBase}.
     */
    private final List<FileConfigAccessor> configs;

    /**
     * Creates a new {@link QuestBase}. The {@code questPath} represents the address of this {@link QuestBase}.
     * <p>
     * All {@code files} are merged into one {@link MultiConfiguration} config.
     *
     * @param log                   the logger that will be used for logging
     * @param configAccessorFactory the factory that will be used to create {@link ConfigAccessor}s
     * @param questPath             the path that addresses this {@link QuestBase}
     * @param root                  the root file of this {@link QuestBase}
     * @param files                 all files contained in this {@link QuestBase}
     * @throws InvalidConfigurationException thrown if a {@link ConfigAccessor} could not be created
     *                                       or an exception occurred while creating the {@link MultiConfiguration}
     * @throws FileNotFoundException         thrown if a file could not be found during the creation
     *                                       of a {@link ConfigAccessor}
     */
    protected QuestBase(final BetonQuestLogger log, final ConfigAccessorFactory configAccessorFactory, final String questPath, final File root, final List<File> files) throws InvalidConfigurationException, FileNotFoundException {
        this.log = log;
        this.configAccessorFactory = configAccessorFactory;
        this.questPath = questPath;
        this.root = root;
        this.configs = new ArrayList<>();

        final Map<ConfigurationSection, String> configurations = new HashMap<>();
        for (final File file : files) {
            final FileConfigAccessor configAccessor = configAccessorFactory.create(file);
            configs.add(configAccessor);
            configurations.put(configAccessor.getConfig(), getRelativePath(root, file));
        }
        try {
            config = new MultiSectionConfiguration(new ArrayList<>(configurations.keySet()));
        } catch (final KeyConflictException e) {
            throw new InvalidConfigurationException(e.resolvedMessage(configurations), e);
        }
    }

    private static String getRelativePath(final File questFile, final File otherFile) {
        return questFile.toURI().relativize(otherFile.toURI()).getPath();
    }

    @Override
    public String getQuestPath() {
        return questPath;
    }

    @Override
    public boolean saveAll() throws IOException {
        boolean exceptionOccurred = false;
        unsaved:
        for (final ConfigurationSection unsavedConfig : config.getUnsavedConfigs()) {
            for (final FileConfigAccessor configAccessor : configs) {
                if (unsavedConfig.equals(configAccessor.getConfig())) {
                    try {
                        configAccessor.save();
                    } catch (final IOException e) {
                        log.warn("Could not save file '" + configAccessor.getConfigurationFile().getPath() + "'! Reason: " + e.getMessage(), e);
                        exceptionOccurred = true;
                    }
                    continue unsaved;
                }
            }
            log.warn("No related ConfigAccessor found for ConfigurationSection '" + unsavedConfig.getName() + "'!");
            exceptionOccurred = true;
        }
        if (exceptionOccurred) {
            throw new IOException("It was not possible to save everything to files in the Quest '" + questPath + "'!");
        }
        return config.needSave();
    }

    @Override
    public ConfigAccessor getOrCreateConfigAccessor(final String relativePath) throws InvalidConfigurationException, FileNotFoundException {
        for (final FileConfigAccessor configAccessor : configs) {
            if (root.toURI().relativize(configAccessor.getConfigurationFile().toURI()).getPath().equals(relativePath)) {
                return configAccessor;
            }
        }
        return createConfigAccessor(relativePath, root);
    }

    private ConfigAccessor createConfigAccessor(final String relativePath, final File root) throws InvalidConfigurationException, FileNotFoundException {
        final File newConfig = new File(root, relativePath);
        final File newConfigParent = newConfig.getParentFile();
        if (!newConfigParent.exists() && !newConfigParent.mkdirs()) {
            throw new InvalidConfigurationException("It was not possible to create the folders for the file '" + newConfig.getPath() + "'!");
        }
        try {
            if (!newConfig.createNewFile()) {
                throw new InvalidConfigurationException("It was not possible to create the file '" + newConfig.getPath() + "'!");
            }
        } catch (final IOException e) {
            throw new InvalidConfigurationException(e.getMessage(), e);
        }
        final FileConfigAccessor newAccessor = configAccessorFactory.create(newConfig);
        configs.add(newAccessor);
        return newAccessor;
    }

    @Override
    public MultiConfiguration getQuestConfig() {
        return config;
    }

    @Override
    public String toString() {
        return getQuestPath();
    }
}
