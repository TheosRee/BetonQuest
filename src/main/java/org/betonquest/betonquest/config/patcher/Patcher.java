package org.betonquest.betonquest.config.patcher;

import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.FileConfigAccessor;
import org.betonquest.betonquest.api.config.patcher.PatchException;
import org.betonquest.betonquest.api.config.patcher.PatchTransformer;
import org.betonquest.betonquest.api.config.patcher.PatchTransformerRegistry;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.config.patcher.migration.SettableVersion;
import org.betonquest.betonquest.config.patcher.migration.VersionMissmatchException;
import org.betonquest.betonquest.versioning.UpdateStrategy;
import org.betonquest.betonquest.versioning.Version;
import org.betonquest.betonquest.versioning.VersionComparator;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Patches BetonQuest's configuration file.
 */
public class Patcher extends DoStuffWithVersions<FileConfigAccessor, Patcher.Patchy> {
    /**
     * Comparator for {@link Version} with the qualifier CONFIG.
     */
    private static final VersionComparator VERSION_COMPARATOR = new VersionComparator(UpdateStrategy.MAJOR, "CONFIG-");

    /**
     * The comment at the version entry in the config.
     */
    private static final String VERSION_CONFIG_COMMENT = "Don't change this! The plugin's automatic config updater handles it.";

    /**
     * The path to the config's version in the config.
     */
    private static final String CONFIG_VERSION_PATH = "configVersion";

    /**
     * Regex pattern of the internal config version schema.
     */
    private static final Pattern VERSION_PATTERN = Pattern.compile("(\\d*\\.\\d*\\.\\d*)\\.(\\d*)");

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * The accessor for the resource file to copy default values from.
     */
    private final ConfigAccessor resourceAccessor;

    /**
     * Registry for all {@link PatchTransformer}s.
     */
    private final PatchTransformerRegistry transformerRegistry;

    /**
     * Creates a new Patcher.
     * <br>
     * Updates can be applied using {@link Patcher#patch(FileConfigAccessor)}.
     *
     * @param log                 the logger that will be used for logging
     * @param resourceAccessor    the accessor for the resource file to copy default values from
     * @param transformerRegistry the registry for all {@link PatchTransformer}s
     * @param patchConfig         the patchConfig that contains patches
     * @throws InvalidConfigurationException if the patchConfig is malformed
     */
    public Patcher(final BetonQuestLogger log, final ConfigAccessor resourceAccessor,
                   final PatchTransformerRegistry transformerRegistry, final ConfigurationSection patchConfig)
            throws InvalidConfigurationException {
        super(log, CONFIG_VERSION_PATH, new Version("0.0.0-CONFIG-0"), VERSION_COMPARATOR,
                buildVersionIndex(new TreeMap<>(VERSION_COMPARATOR), patchConfig, ""));
        this.log = log;
        this.resourceAccessor = resourceAccessor;
        this.transformerRegistry = transformerRegistry;
    }

    @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
    private static Map<Version, Patchy> buildVersionIndex(final Map<Version, Patchy> map, final ConfigurationSection section, final String previousKeys)
            throws InvalidConfigurationException {
        for (final String key : section.getKeys(false)) {
            final String currentKey = previousKeys.isEmpty() ? key : previousKeys + "." + key;
            if (section.isConfigurationSection(key)) {
                final ConfigurationSection nestedSection = section.getConfigurationSection(key);
                if (nestedSection == null) {
                    throw new InvalidConfigurationException("The patch file at '" + currentKey + "' is not a list or a section.");
                }
                buildVersionIndex(map, nestedSection, currentKey);
            } else if (currentKey.split("\\.").length == 4) {
                collectVersion(map, currentKey, section.getMapList(key));
            } else {
                throw new InvalidConfigurationException("The patch file at '" + currentKey + "' is too long or too short.");
            }
        }
        return map;
    }

    private static void collectVersion(final Map<Version, Patchy> map, final String currentKey, final List<Map<?, ?>> mapList)
            throws InvalidConfigurationException {
        final Matcher matcher = VERSION_PATTERN.matcher(currentKey);
        if (!matcher.matches()) {
            throw new InvalidConfigurationException("The patch file at '" + currentKey + "' has an invalid version format.");
        }
        final String result = matcher.group(1) + "-CONFIG-" + matcher.group(2);
        final Version discoveredVersion = new Version(result);
        map.put(discoveredVersion, new Patchy(mapList));
    }

    @Override
    public void migrate(final FileConfigAccessor accessor) throws InvalidConfigurationException, VersionMissmatchException {
        final Configuration config = accessor.getConfig();
        config.setDefaults(resourceAccessor.getConfig());
        config.options().copyDefaults(true);
        try {
            super.migrate(accessor);
        } catch (final IOException e) {
            throw new InvalidConfigurationException("Default values were applied to the config but could not be saved! Reason: " + e.getMessage(), e);
        }
    }

    /**
     * Patches the given config with the given patch file.
     *
     * @param accessor the config to patch
     * @throws InvalidConfigurationException if the config could not be saved
     */
    public void patch(final FileConfigAccessor accessor) throws InvalidConfigurationException {
        final String logPrefix = String.format("The config file '%s' ", accessor.getConfigurationFile().getName());
        if (patches.isEmpty()) {
            log.debug(logPrefix + "has no patches to apply.");
        } else if (configVersionString != null && configVersionString.isEmpty()) {
            log.debug(logPrefix + "gets the latest version '" + patches.lastKey() + "' set.");
            setConfigVersion(config, patches.lastKey());
        } else {
            final Version version = getConfigVersion(configVersionString);
            if (version != null && !VERSION_COMPARATOR.isOtherNewerThanCurrent(version, patches.lastEntry().getKey())) {
                log.debug(logPrefix + "is already up to date.");
            } else {
                final String displayVersion = version == null ? "'legacy' version" : "version '" + version + "'";
                log.info(logPrefix + "gets updated from " + displayVersion + "...");
                patch(version, config);
            }
        }
    }

    private void patch(@Nullable final Version version, final Configuration config) {
        boolean noErrors = true;
        final NavigableMap<Version, List<Map<?, ?>>> actualPatches = version == null ? patches : patches.tailMap(version, false);
        for (final Map.Entry<Version, List<Map<?, ?>>> patch : actualPatches.entrySet()) {
            log.info("Applying patches to update to '" + patch.getKey() + "'...");
            setConfigVersion(config, patch.getKey());
            if (!applyPatch(config, patch.getValue())) {
                noErrors = false;
            }
        }
        if (noErrors) {
            log.info("Patching complete!");
        } else {
            log.warn("The patching progress did not go flawlessly. However, this does not mean your configs "
                    + "are now corrupted. Please check the errors above to see what the patcher did. "
                    + "You might want to adjust your config manually depending on that information.");
        }
    }

    @Nullable
    private Version getConfigVersion(@Nullable final String configVersion) {
        if (configVersion == null || configVersion.isEmpty()) {
            return null;
        }
        return new Version(configVersion);
    }

    private boolean applyPatch(final ConfigurationSection config, final List<Map<?, ?>> patchData) {
        boolean noErrors = true;
        for (final Map<?, ?> transformationData : patchData) {
            final Map<String, String> typeSafeTransformationData = transformationData.entrySet().stream()
                    .map(entry -> Map.entry(String.valueOf(entry.getKey()), String.valueOf(entry.getValue())))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            try {
                getPatchTransformer(typeSafeTransformationData.get("type")).transform(typeSafeTransformationData, config);
            } catch (final PatchException e) {
                noErrors = false;
                log.warn("There has been an issue while applying the patches: " + e.getMessage());
            }
        }
        return noErrors;
    }

    private PatchTransformer getPatchTransformer(@Nullable final String transformationType) throws PatchException {
        if (transformationType == null) {
            throw new PatchException("Missing transformation type for patcher!");
        }

        final String transformationTypeUpperCase = transformationType.toUpperCase(Locale.ROOT);
        final PatchTransformer patchTransformer = transformerRegistry.getTransformers().get(transformationTypeUpperCase);
        if (patchTransformer == null) {
            throw new PatchException("Unknown transformation type '" + transformationTypeUpperCase + "' used!");
        }
        return patchTransformer;
    }

    private void setConfigVersion(final ConfigurationSection config, final Version newVersion) {
        config.set(CONFIG_VERSION_PATH, newVersion.getVersion());
        config.setInlineComments(CONFIG_VERSION_PATH, List.of(VERSION_CONFIG_COMMENT));
    }

    @Override
    protected SettableVersion<FileConfigAccessor> version(final String versionString) {
        return new SettableVersion<>(versionString) {
            @Override
            public void setVersion(final FileConfigAccessor accessor, final String path) {
                final Configuration config = accessor.getConfig();
                config.set(path, getVersion());
                config.setInlineComments(CONFIG_VERSION_PATH, List.of(VERSION_CONFIG_COMMENT));
            }
        };
    }

    @Override
    protected ConfigurationSection getConfig(final FileConfigAccessor accessor) {
        return accessor.getConfig();
    }

    @Override
    protected void save(final FileConfigAccessor accessor) throws IOException {
        accessor.save();
    }

    public static class Patchy implements Stuffy<FileConfigAccessor> {

        private final List<Map<?, ?>> mapList;

        public Patchy(final List<Map<?, ?>> mapList) {
            this.mapList = mapList;
        }

        @Override
        public void migrate(final FileConfigAccessor stuff) throws InvalidConfigurationException {

        }
    }
}
