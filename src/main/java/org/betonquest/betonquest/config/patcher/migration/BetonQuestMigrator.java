package org.betonquest.betonquest.config.patcher.migration;

import org.betonquest.betonquest.api.config.patcher.migration.QuestMigration;
import org.betonquest.betonquest.api.config.patcher.migration.QuestMigrator;
import org.betonquest.betonquest.api.config.patcher.migration.SettableVersion;
import org.betonquest.betonquest.api.config.patcher.migration.VersionMissmatchException;
import org.betonquest.betonquest.api.config.quest.Quest;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.versioning.UpdateStrategy;
import org.betonquest.betonquest.api.versioning.Version;
import org.betonquest.betonquest.api.versioning.VersionComparator;
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
import org.betonquest.betonquest.config.patcher.migration.migrator.from2to3.NpcRename;
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
public class BetonQuestMigrator extends QuestMigrator {
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
     * The '3.0.0' semantic version.
     */
    private static final String THREE_ZERO_ZERO = "3.0.0";

    /**
     * The newest package version.
     */
    private static final SettableVersion FIRST_NON_LEGACY_VERSION = questVersion(THREE_ZERO_ZERO, 0);

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
    protected BetonQuestMigrator(final BetonQuestLogger log, final List<QuestMigration> legacyMigrations,
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
    public BetonQuestMigrator(final BetonQuestLogger log, final PluginDescriptionFile pluginDescription) {
        this(log, getLegacy(), getMigrations(), questVersion(pluginDescription.getVersion(), 0));
    }

    private static SettableVersion questVersion(final String semanticVersion, final int number) {
        return new SettableVersion(semanticVersion + "-QUEST-" + number);
    }

    private static NavigableMap<Version, QuestMigration> getMigrations() {
        final NavigableMap<Version, QuestMigration> migrations = new TreeMap<>(VERSION_COMPARATOR);
        migrations.put(questVersion(THREE_ZERO_ZERO, 1), new LanguageRename());
        migrations.put(questVersion(THREE_ZERO_ZERO, 2), new NpcRename());
        migrations.put(questVersion(THREE_ZERO_ZERO, 3), new AddSimpleTypeToQuestItem());
        return migrations;
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
}
