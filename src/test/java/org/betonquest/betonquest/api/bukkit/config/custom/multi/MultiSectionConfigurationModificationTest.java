package org.betonquest.betonquest.api.bukkit.config.custom.multi;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is a test for modifications of the {@link MultiSectionConfiguration}.
 */
@Tag("ConfigurationSection")
@SuppressWarnings({"PMD.JUnitAssertionsShouldIncludeMessage", "PMD.JUnitTestContainsTooManyAsserts", "PMD.JUnit5TestShouldBePackagePrivate"})
public class MultiSectionConfigurationModificationTest {
    /**
     * {@link MultiConfiguration} part 1.
     */
    protected Configuration config1;

    /**
     * {@link MultiConfiguration} part 2.
     */
    protected Configuration config2;

    /**
     * {@link MultiConfiguration} part 3.
     */
    protected Configuration config3;

    /**
     * The {@link MultiConfiguration} instance for testing.
     */
    private MultiConfiguration config;

    /**
     * A {@link FileConfiguration} containing the result of the merged
     * {@link MultiSectionConfigurationModificationTest#config}.
     */
    private FileConfiguration configAll;

    /**
     * Creates the {@link MultiSectionConfigurationModificationTest#configAll},
     * {@link MultiSectionConfigurationModificationTest#config1}, {@link MultiSectionConfigurationModificationTest#config2},
     * {@link MultiSectionConfigurationModificationTest#config3} and {@link MultiSectionConfigurationModificationTest#config}
     * by calling the {@link MultiSectionConfigurationModificationTest#getConfig()} method.
     */
    @BeforeEach
    public void setupConfigs() {
        configAll = new YamlConfiguration();
        setupConfig1(configAll);
        setupConfig2(configAll);
        setupConfig3(configAll);
        config1 = setupConfig1(new YamlConfiguration());
        config2 = setupConfig2(new YamlConfiguration());
        config3 = setupConfig3(new YamlConfiguration());
        config = getConfig();
    }

    private Configuration setupConfig1(final Configuration config) {
        final ConfigurationSection pathSection = config.createSection("events.custom.path");
        pathSection.set("give.type", "give");
        pathSection.set("give.item", "emerald");
        pathSection.set("give.amount", 5);
        pathSection.set("take.type", "give");
        pathSection.set("take.item", "emerald");
        pathSection.set("take.amount", 5);

        final ConfigurationSection journalSection = config.createSection("events.custom.journal");
        journalSection.set("type", "journal");
        journalSection.set("action", "add");
        journalSection.set("target", "quest_started");

        return config;
    }

    private Configuration setupConfig2(final Configuration config) {
        final ConfigurationSection pathSection = config.createSection("events.custom.path");
        pathSection.set("notify.type", "notify");
        pathSection.set("notify.message", "Example Message");
        pathSection.set("notify.io", "chat");
        pathSection.set("cancel.type", "cancel");
        pathSection.set("cancel.target", "myQuest");

        final ConfigurationSection emeraldSection = config.createSection("conditions.items.emerald");
        emeraldSection.set("type", "item");
        emeraldSection.set("item", "emerald");
        emeraldSection.set("amount", 5);

        return config;
    }

    private Configuration setupConfig3(final Configuration config) {
        final ConfigurationSection finishedSection = config.createSection("events.custom.finished");
        finishedSection.set("type", "notify");
        finishedSection.set("message", "Quest Finished");
        finishedSection.set("io", "advancement");

        final ConfigurationSection startSection = config.createSection("events.start");
        startSection.set("type", "notify");
        startSection.set("message", "Quest Started");
        startSection.set("io", "advancement");

        final ConfigurationSection stoneSection = config.createSection("conditions.items.stone");
        stoneSection.set("type", "item");
        stoneSection.set("item", "stone");
        stoneSection.set("amount", 3);

        final ConfigurationSection itemIronSection = config.createSection("conditions.item_iron");
        itemIronSection.set("type", "item");
        itemIronSection.set("item", "iron");
        itemIronSection.set("amount", 2);

        return config;
    }

    /**
     * This method creates the {@link MultiConfiguration} instance for testing.
     *
     * @return the {@link MultiConfiguration} instance for testing.
     */
    protected MultiConfiguration getConfig() {
        final Map<ConfigurationSection, String> configs = new HashMap<>();
        configs.put(config1, "config1.yml");
        configs.put(config2, "config2.yml");
        configs.put(config3, "config3.yml");
        try {
            return new MultiSectionConfiguration(new ArrayList<>(configs.keySet()));
        } catch (final KeyConflictException e) {
            fail(e.resolvedMessage(configs), e);
        } catch (final InvalidConfigurationException e) {
            fail(e);
        }
        return null;
    }

    @Test
    void testMergedResult() {
        final YamlConfiguration yamlConfig = new YamlConfiguration();
        for (final Map.Entry<String, Object> entry : config.getValues(false).entrySet()) {
            yamlConfig.set(entry.getKey(), entry.getValue());
        }

        assertContainsAll(configAll, yamlConfig);
        assertContainsAll(yamlConfig, configAll);
    }

    private void assertContainsAll(final ConfigurationSection config1, final ConfigurationSection config2) {
        if (config1 == null) {
            assertNull(config2);
            return;
        }
        assertNotNull(config2);
        for (final String key : config.getKeys(false)) {
            if (config1.isConfigurationSection(key)) {
                assertContainsAll(config1.getConfigurationSection(key), config2.getConfigurationSection(key));
            } else {
                assertEquals(config1.get(key), config2.get(key));
            }
        }
    }

    @Test
    void testModificationToConfigs() {
        config.set("events.custom.path.give.item", "stone");
        assertTrue(config.needSave());
        final Set<ConfigurationSection> unsavedConfigs1 = config.getUnsavedConfigs();
        assertEquals(1, unsavedConfigs1.size());
        final ConfigurationSection unsavedConfig1 = unsavedConfigs1.iterator().next();
        assertEquals(config1, unsavedConfig1);
        assertEquals("stone", unsavedConfig1.getString("events.custom.path.give.item"));
        assertTrue(config.markAsSaved(unsavedConfig1));
        assertFalse(config.needSave());

        config.set("events.custom.path.cancel.target", "stone");
        config.set("conditions.items.stone.amount", "stone");
        assertTrue(config.needSave());
        final Set<ConfigurationSection> unsavedConfigs2 = config.getUnsavedConfigs();
        assertEquals(2, unsavedConfigs2.size());
        final Iterator<ConfigurationSection> iterator = unsavedConfigs2.iterator();
        final ConfigurationSection unsavedConfig2 = iterator.next();
        final ConfigurationSection unsavedConfig3 = iterator.next();

        if (unsavedConfig2.equals(config2) && unsavedConfig3.equals(config3)) {
            assertEquals("stone", unsavedConfig2.getString("events.custom.path.cancel.target"));
            assertEquals("stone", unsavedConfig3.getString("conditions.items.stone.amount"));
        } else if (unsavedConfig2.equals(config3) && unsavedConfig3.equals(config2)) {
            assertEquals("stone", unsavedConfig3.getString("events.custom.path.cancel.target"));
            assertEquals("stone", unsavedConfig2.getString("conditions.items.stone.amount"));
        } else {
            fail();
        }

        assertTrue(config.markAsSaved(unsavedConfig2));
        assertTrue(config.markAsSaved(unsavedConfig3));
        assertFalse(config.needSave());
    }

    @Test
    void testUnassociatedKeys() throws InvalidConfigurationException {
        assertFalse(config.needSave());
        config.set("objectives.block.type", "block");
        config.set("objectives.block.material", "bedrock");
        assertTrue(config.needSave());

        final List<String> keys1 = new ArrayList<>();
        keys1.add("objectives.block.type");
        keys1.add("objectives.block.material");
        assertEquals(keys1, config.getUnassociatedKeys());

        final MemorySection newConfig = new MemoryConfiguration();
        config.associateWith(newConfig);
        assertTrue(config.getUnassociatedKeys().isEmpty());
        assertTrue(config.needSave());

        final Set<ConfigurationSection> unsavedConfigs1 = config.getUnsavedConfigs();
        assertEquals(1, unsavedConfigs1.size());
        final ConfigurationSection unsavedConfig1 = unsavedConfigs1.iterator().next();
        assertEquals(newConfig, unsavedConfig1);
        assertEquals("block", unsavedConfig1.getString("objectives.block.type"));
        assertEquals("bedrock", unsavedConfig1.getString("objectives.block.material"));
        assertTrue(config.markAsSaved(unsavedConfig1));

        assertFalse(config.needSave());
        config.set("objectives.block.amount", "50");
        config.set("objectives.block.notify", "5");
        assertTrue(config.needSave());

        final List<String> keys2 = new ArrayList<>();
        keys2.add("objectives.block.amount");
        keys2.add("objectives.block.notify");
        assertEquals(keys2, config.getUnassociatedKeys());

        config.associateWith("objectives.block", newConfig);
        assertTrue(config.getUnassociatedKeys().isEmpty());
        assertTrue(config.needSave());

        final Set<ConfigurationSection> unsavedConfigs2 = config.getUnsavedConfigs();
        assertEquals(1, unsavedConfigs2.size());
        final ConfigurationSection unsavedConfig2 = unsavedConfigs2.iterator().next();
        assertEquals(newConfig, unsavedConfig2);
        assertEquals("50", unsavedConfig2.getString("objectives.block.amount"));
        assertEquals("5", unsavedConfig2.getString("objectives.block.notify"));
        assertTrue(config.markAsSaved(unsavedConfig2));

        assertFalse(config.needSave());
        assertEquals(newConfig, config.getSourceConfigurationSection("objectives.block"));
    }

    @Test
    void testGetSourceConfigurationSection() throws InvalidConfigurationException {
        assertNull(config.getSourceConfigurationSection("events.custom.path.give.slot"));
        config.set("events.custom.path.give.slot", "hand");
        assertThrows(InvalidConfigurationException.class,
                () -> config.getSourceConfigurationSection("events.custom.path.give"),
                "Not all entries are from the same source config");
        config.associateWith("events.custom.path.give.slot", config1);
        assertDoesNotThrow(() -> {
                    final ConfigurationSection config = this.config.getSourceConfigurationSection("events.custom.path.give");
                    assertEquals(config1, config);
                },
                "Not all entries are from the same source config");
    }
}
