package org.betonquest.betonquest.compatibility.heroes;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.CharacterManager;
import com.herocraftonline.heroes.characters.classes.HeroClassManager;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.api.quest.QuestTypeRegistries;
import org.betonquest.betonquest.api.quest.condition.ConditionRegistry;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.heroes.condition.HeroesAttributeConditionFactory;
import org.betonquest.betonquest.compatibility.heroes.condition.HeroesClassConditionFactory;
import org.betonquest.betonquest.compatibility.heroes.condition.HeroesSkillConditionFactory;
import org.betonquest.betonquest.compatibility.heroes.event.HeroesExperienceEventFactory;

/**
 * Integrator for Heroes.
 */
public class HeroesIntegrator implements Integrator {
    /**
     * The Heroes plugin instance.
     */
    private final Heroes heroes;

    /**
     * Creates a new Heroes Integrator.
     *
     * @param heroes the hero plugins instance
     */
    public HeroesIntegrator(final Heroes heroes) {
        this.heroes = heroes;
    }

    @Override
    public void hook(final BetonQuestApi api) {
        final PrimaryServerThreadData data = api.getPrimaryServerThreadData();
        final BetonQuestLoggerFactory loggerFactory = api.getLoggerFactory();
        final CharacterManager characterManager = heroes.getCharacterManager();
        final HeroClassManager classManager = heroes.getClassManager();

        final QuestTypeRegistries questRegistries = api.getQuestRegistries();
        final ConditionRegistry conditionRegistry = questRegistries.condition();
        conditionRegistry.register("heroesattribute", new HeroesAttributeConditionFactory(loggerFactory, data, characterManager));
        conditionRegistry.register("heroesclass", new HeroesClassConditionFactory(loggerFactory, data, characterManager, classManager));
        conditionRegistry.register("heroesskill", new HeroesSkillConditionFactory(loggerFactory, data, characterManager));

        questRegistries.event().register("heroesexp", new HeroesExperienceEventFactory(loggerFactory, data, characterManager));

        heroes.getServer().getPluginManager().registerEvents(new HeroesMobKillListener(api.getProfileProvider()), heroes);
    }

    @Override
    public void reload() {
        // Empty
    }

    @Override
    public void close() {
        // Empty
    }
}
