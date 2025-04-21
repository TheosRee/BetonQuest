package org.betonquest.betonquest.api.config.patcher.migration;

import org.betonquest.betonquest.api.config.quest.Quest;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.versioning.Version;
import org.betonquest.betonquest.api.versioning.VersionComparator;
import org.bukkit.configuration.InvalidConfigurationException;

import java.io.IOException;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Migrates {@link Quest}s by Versions.
 */
public class QuestMigrator {
    /**
     * Custom logger for this class.
     */
    protected final BetonQuestLogger log;

    /**
     * The path to the quest's version in the package.
     */
    private final String versionPath;

    /**
     * Comparator for {@link Version}.
     */
    private final VersionComparator versionComparator;

    /**
     * The migrations by their version.
     */
    private final NavigableMap<SettableVersion, QuestMigration> migrations;

    /**
     * The version to set if no migrations are present.
     */
    private final SettableVersion fallbackVersion;

    /**
     * Create a new Quest Migrator with custom migrations.
     *
     * @param log             the custom logger for this class
     * @param comparator      the version comparator to use
     * @param migrations      the migrations by their version
     * @param versionPath     the version path
     * @param fallbackVersion the current version to set
     */
    public QuestMigrator(final BetonQuestLogger log, final String versionPath, final Version fallbackVersion,
                         final VersionComparator comparator, final Map<Version, QuestMigration> migrations) {
        this.log = log;
        this.versionPath = versionPath;
        this.versionComparator = comparator;
        this.migrations = new TreeMap<>(versionComparator);
        for (final Map.Entry<Version, QuestMigration> entry : migrations.entrySet()) {
            this.migrations.put(new SettableVersion(entry.getKey().getVersion()), entry.getValue());
        }
        this.fallbackVersion = new SettableVersion(fallbackVersion.getVersion());
    }

    /**
     * Updates the Quest to the newest version.
     *
     * @param quest the Quest to update
     * @throws InvalidConfigurationException when an error occurs
     * @throws IOException                   when an error occurs
     * @throws VersionMissmatchException     when the Quest version is newer than the max settable version
     */
    public void migrate(final Quest quest) throws IOException, InvalidConfigurationException, VersionMissmatchException {
        log.debug("Attempting to migrate package '" + quest.getQuestPath() + "'");
        final String versionString = quest.getQuestConfig().getString(versionPath);
        final SettableVersion lastVersionToSet = migrations.isEmpty() ? fallbackVersion : migrations.lastKey();
        if (versionString == null) {
            log.debug("  No version present, just setting to '" + lastVersionToSet + "'");
            lastVersionToSet.setVersion(quest, versionPath);
            quest.saveAll();
            return;
        }
        final SettableVersion otherVersion = new SettableVersion(versionString);
        if (lastVersionToSet.equals(otherVersion)) {
            log.debug("  Version '" + otherVersion + "' is up to date");
            return;
        }

        if (versionComparator.isOtherNewerThanCurrent(lastVersionToSet, otherVersion)) {
            throw new VersionMissmatchException("The version '" + otherVersion
                    + "' is newer than the latest known version '" + lastVersionToSet + "'!\n"
                    + "Quests with newer versions will probably cause issues. If you know that won't be the case"
                    + " you can change the quest version to the latest known.");
        }
        log.debug("  Migrating from version '" + otherVersion + "' to '" + lastVersionToSet + "'");
        final Map<SettableVersion, QuestMigration> actualMigrations = migrations.tailMap(otherVersion, false);

        if (actualMigrations.isEmpty()) {
            log.debug("  No newer migrations found, just setting version to '" + lastVersionToSet + "'");
            lastVersionToSet.setVersion(quest, versionPath);
            quest.saveAll();
            return;
        }

        for (final Map.Entry<SettableVersion, QuestMigration> entry : actualMigrations.entrySet()) {
            entry.getValue().migrate(quest);
            entry.getKey().setVersion(quest, versionPath);
            quest.saveAll();
        }
    }
}
