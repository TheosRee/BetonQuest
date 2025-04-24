package org.betonquest.betonquest.config.patcher.migration;

import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfiguration;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.config.patcher.DoStuffWithVersions;
import org.betonquest.betonquest.config.patcher.migration.migrator.from1to2.AuraSkillsRename;
import org.betonquest.betonquest.config.patcher.migration.migrator.from1to2.EffectLib;
import org.betonquest.betonquest.config.patcher.migration.migrator.from1to2.EventScheduling;
import org.betonquest.betonquest.config.patcher.migration.migrator.from1to2.FabledRename;
import org.betonquest.betonquest.config.patcher.migration.migrator.from1to2.MmoUpdates;
import org.betonquest.betonquest.config.patcher.migration.migrator.from1to2.NpcHolograms;
import org.betonquest.betonquest.config.patcher.migration.migrator.from1to2.PackageSection;
import org.betonquest.betonquest.config.patcher.migration.migrator.from1to2.RemoveEntity;
import org.betonquest.betonquest.config.patcher.migration.migrator.from1to2.RideUpdates;
import org.betonquest.betonquest.config.patcher.migration.migrator.from2to3.AddSimpleTypeToQuestItem;
import org.betonquest.betonquest.config.patcher.migration.migrator.from2to3.LanguageRename;
import org.betonquest.betonquest.config.patcher.migration.migrator.from2to3.ListNamesRenameToPlural;
import org.betonquest.betonquest.config.patcher.migration.migrator.from2to3.NpcRename;
import org.betonquest.betonquest.config.patcher.migration.migrator.from2to3.PickRandomPercentage;
import org.betonquest.betonquest.config.quest.Quest;
import org.betonquest.betonquest.versioning.UpdateStrategy;
import org.betonquest.betonquest.versioning.Version;
import org.betonquest.betonquest.versioning.VersionComparator;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.PluginDescriptionFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Migrates {@link Quest}s by Versions.
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class QuestMigrator extends DoStuffWithVersions<Quest, QuestMigration> {
    /**
     * Comparator for {@link Version} with the qualifier QUEST.
     */
    private static final VersionComparator VERSION_COMPARATOR = new VersionComparator(UpdateStrategy.MAJOR, "QUEST-");

    /**
     * The path to the quest's version in the package.
     */
    private static final String QUEST_VERSION_PATH = "package.version";

    /**
     * "Version" String indicating that all migrations should be applied.
     */
    private static final String LEGACY = "legacy";

    /**
     * The newest package version.
     */
    private static final SettableVersion<Quest> FIRST_NON_LEGACY_VERSION = questVersion("3.0.0", 0);

    /**
     * The legacy migrations.
     */
    private final List<QuestMigration> legacyMigrations;

    /**
     * Create a new Quest Migrator with custom migrations.
     *
     * @param log              the custom logger for this class
     * @param legacyMigrations the legacy migrations to apply if no version is set
     * @param migrations       the migrations by their version
     * @param currentVersion   the current version to set
     */
    protected QuestMigrator(final BetonQuestLogger log, final List<QuestMigration> legacyMigrations,
                            final Map<Version, QuestMigration> migrations, final Version currentVersion) {
        super(log, QUEST_VERSION_PATH, currentVersion, VERSION_COMPARATOR, migrations);
        this.legacyMigrations = legacyMigrations;
    }

    /**
     * Create a new Quest Migrator with BQ Migrations.
     *
     * @param log               the custom logger for the class
     * @param pluginDescription the PluginDescriptionFile containing a semantic version,
     *                          used as fallback when no migrator is applied
     */
    public QuestMigrator(final BetonQuestLogger log, final PluginDescriptionFile pluginDescription) {
        this(log, getLegacy(), getMigrations(), questVersion(pluginDescription.getVersion(), 0));
    }

    private static NavigableMap<Version, QuestMigration> getMigrations() {
        final NavigableMap<Version, QuestMigration> migrations = new TreeMap<>(VERSION_COMPARATOR);
        migrations.put(questVersion("3.0.0", 1), new LanguageRename());
        migrations.put(questVersion("3.0.0", 2), new NpcRename());
        migrations.put(questVersion("3.0.0", 3), new AddSimpleTypeToQuestItem());
        migrations.put(questVersion("3.0.0", 4), new ListNamesRenameToPlural());
        migrations.put(questVersion("3.0.0", 5), new PickRandomPercentage());
        return migrations;
    }

    private static SettableVersion<Quest> questVersion(final String semanticVersion, final int number) {
        return new QuestVersion(semanticVersion + "-QUEST-" + number);
    }

    private static List<QuestMigration> getLegacy() {
        return List.of(
                new EventScheduling(),
                new PackageSection(),
                new NpcHolograms(),
                new EffectLib(),
                new MmoUpdates(),
                new RemoveEntity(),
                new RideUpdates(),
                new AuraSkillsRename(),
                new FabledRename()
        );
    }

    @Override
    protected SettableVersion<Quest> version(final String versionString) {
        return new QuestVersion(versionString);
    }

    @Override
    protected ConfigurationSection getConfig(final Quest quest) {
        return quest.getQuestConfig();
    }

    @Override
    protected void save(final Quest quest) throws IOException {
        quest.saveAll();
    }

    @Override
    public void migrate(final Quest quest) throws IOException, InvalidConfigurationException, VersionMissmatchException {
        final String versionString = quest.getQuestConfig().getString(QUEST_VERSION_PATH);
        if (LEGACY.equalsIgnoreCase(versionString)) {
            log.debug(quest.getQuestPath() + ":  Legacy identifier set, applying legacy migrations");
            for (final QuestMigration legacyMigration : legacyMigrations) {
                legacyMigration.migrate(quest);
                quest.saveAll();
            }
        }
        FIRST_NON_LEGACY_VERSION.setVersion(quest, QUEST_VERSION_PATH);
        quest.saveAll();
        super.migrate(quest);
    }

    /**
     * Sets a version to a Quest.
     */
    public static class QuestVersion extends SettableVersion<Quest> {

        /**
         * Creates a new Version.
         *
         * @param versionString The raw version string
         */
        public QuestVersion(final String versionString) {
            super(versionString);
        }

        @Override
        public void setVersion(final Quest quest, final String path) throws IOException {
            final MultiConfiguration config = quest.getQuestConfig();
            final boolean isSet = config.isSet(path);
            config.set(path, getVersion());
            config.setInlineComments(path, List.of("Don't change this! The plugin's automatic quest updater handles it."));
            if (!isSet) {
                try {
                    final ConfigAccessor packageFile = quest.getOrCreateConfigAccessor("package.yml");
                    config.associateWith(path, packageFile.getConfig());
                } catch (final InvalidConfigurationException e) {
                    throw new IllegalStateException("Could not load package file: " + e.getMessage(), e);
                }
            }
        }
    }
}
