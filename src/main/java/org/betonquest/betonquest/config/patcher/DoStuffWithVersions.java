package org.betonquest.betonquest.config.patcher;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.config.patcher.migration.SettableVersion;
import org.betonquest.betonquest.config.patcher.migration.VersionMissmatchException;
import org.betonquest.betonquest.versioning.Version;
import org.betonquest.betonquest.versioning.VersionComparator;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

import java.io.IOException;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Applies updates to stuff.
 *
 * @param <T> the object to update
 * @param <U> the stuff to apply to the object
 */
public abstract class DoStuffWithVersions<T, U extends DoStuffWithVersions.Stuffy<T>> {
    /**
     * Custom logger for this class.
     */
    protected final BetonQuestLogger log;

    /**
     * The path to the version in the config.
     */
    private final String versionPath;

    /**
     * Comparator for {@link Version}.
     */
    private final VersionComparator versionComparator;

    /**
     * The migrations by their version.
     */
    private final NavigableMap<SettableVersion<T>, U> migrations;

    /**
     * The version to set if no migrations are present.
     */
    private final SettableVersion<T> fallbackVersion;

    /**
     * Create a new {@link DoStuffWithVersions} with stuff.
     *
     * @param log             the custom logger for this class
     * @param comparator      the version comparator to use
     * @param stuff           the stuff by their version
     * @param versionPath     the version path
     * @param fallbackVersion the fallback version to set
     */
    @SuppressWarnings("PMD.ConstructorCallsOverridableMethod")
    public DoStuffWithVersions(final BetonQuestLogger log, final String versionPath, final Version fallbackVersion,
                               final VersionComparator comparator, final Map<Version, U> stuff) {
        this.log = log;
        this.versionPath = versionPath;
        this.versionComparator = comparator;
        this.migrations = new TreeMap<>(versionComparator);
        for (final Map.Entry<Version, U> entry : stuff.entrySet()) {
            this.migrations.put(version(entry.getKey().getVersion()), entry.getValue());
        }
        this.fallbackVersion = version(fallbackVersion.getVersion());
    }

    /**
     * Gets a settable version for {@link T}.
     *
     * @param versionString the version in string format
     * @return the version object
     */
    protected abstract SettableVersion<T> version(String versionString);

    /**
     * Updates the {@link T} to the newest version.
     *
     * @param stuff the thing to do the stuff on
     * @throws InvalidConfigurationException when an error occurs
     * @throws IOException                   when an error occurs
     * @throws VersionMissmatchException     when the version is newer than the max settable version
     */
    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.AvoidCatchingGenericException"})
    public void migrate(final T stuff) throws IOException, InvalidConfigurationException, VersionMissmatchException {
        final String versionString = getConfig(stuff).getString(versionPath);
        final SettableVersion<T> lastVersionToSet = migrations.isEmpty() ? fallbackVersion : migrations.lastKey();
        if (versionString == null || versionString.isEmpty()) {
            log.debug("  No version present, just setting to '" + lastVersionToSet + "'");
            lastVersionToSet.setVersion(stuff, versionPath);
            save(stuff);
            return;
        }

        final SettableVersion<T> otherVersion = version(versionString);
        if (lastVersionToSet.equals(otherVersion)) {
            log.debug("  Version '" + otherVersion + "' is up to date");
            return;
        }
        if (versionComparator.isOtherNewerThanCurrent(lastVersionToSet, otherVersion)) {
            throw new VersionMissmatchException("The version '" + otherVersion
                    + "' is newer than the latest known version '" + lastVersionToSet + "'!\n"
                    + "Newer versions will probably cause issues. If you know that won't be the case"
                    + " you can change the version to the latest known.");
        }
        log.debug("  Migrating from version '" + otherVersion + "' to '" + lastVersionToSet + "'");
        final Map<SettableVersion<T>, U> actualMigrations = migrations.tailMap(otherVersion, false);

        if (actualMigrations.isEmpty()) {
            log.debug("  No newer migrations found, just setting version to '" + lastVersionToSet + "'");
            lastVersionToSet.setVersion(stuff, versionPath);
            save(stuff);
            return;
        }

        for (final Map.Entry<SettableVersion<T>, U> entry : actualMigrations.entrySet()) {
            try {
                entry.getValue().migrate(stuff);
            } catch (final Exception e) {
                throw new InvalidConfigurationException("Unexpected error while applying migration '" + entry.getKey() + "': " + e.getMessage(), e);
            }
            entry.getKey().setVersion(stuff, versionPath);
            save(stuff);
        }
    }

    /**
     * Get the config the stuff represents.
     *
     * @param stuff to get the config from
     * @return the config
     */
    protected abstract ConfigurationSection getConfig(T stuff);

    /**
     * Saves the stuff's changes.
     *
     * @param stuff to save
     * @throws IOException when the saving fails
     */
    protected abstract void save(T stuff) throws IOException;

    /**
     * Stuffy stuff, stuff.
     *
     * @param <T> the stuff to do on
     */
    @FunctionalInterface
    public interface Stuffy<T> {
        /**
         * Migrates the Stuff.
         *
         * @param stuff the stuff to update
         * @throws InvalidConfigurationException if an error occurs
         */
        void migrate(T stuff) throws InvalidConfigurationException;
    }
}
