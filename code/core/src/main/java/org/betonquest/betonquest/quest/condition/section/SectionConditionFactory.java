package org.betonquest.betonquest.quest.condition.section;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.ConditionIdentifier;
import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.condition.NullableConditionAdapter;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.api.service.condition.ConditionManager;
import org.betonquest.betonquest.quest.condition.logik.ConjunctionCondition;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Factory to create section grouped conditions from {@link Instruction}s.
 */
public class SectionConditionFactory implements PlayerConditionFactory, PlayerlessConditionFactory {

    /**
     * The condition manager.
     */
    private final ConditionManager conditionManager;

    /**
     * Factory to create identifier from section keys.
     */
    private final IdentifierFactory<ConditionIdentifier> identifierFactory;

    /**
     * Create the section condition factory.
     *
     * @param conditionManager  the condition manager
     * @param identifierFactory the factory to create identifier from section keys
     */
    public SectionConditionFactory(final ConditionManager conditionManager, final IdentifierFactory<ConditionIdentifier> identifierFactory) {
        this.conditionManager = conditionManager;
        this.identifierFactory = identifierFactory;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        return parseAlternative(instruction);
    }

    @Override
    public PlayerlessCondition parsePlayerless(final Instruction instruction) throws QuestException {
        return parseAlternative(instruction);
    }

    private NullableConditionAdapter parseAlternative(final Instruction instruction) throws QuestException {
        final Argument<List<ConditionIdentifier>> conditionIDs = instruction.identifier(ConditionIdentifier.class).map(this::subsectionIdentifiers).get();
        return new NullableConditionAdapter(new ConjunctionCondition(conditionIDs, conditionManager));
    }

    private List<ConditionIdentifier> subsectionIdentifiers(final ConditionIdentifier identifier) throws QuestException {
        final QuestPackage pack = identifier.getPackage();
        final String identifierSection = identifier.getSection();
        final ConfigurationSection section = pack.getConfig().getConfigurationSection(identifierSection);
        if (section == null) {
            throw new QuestException("There is no section '%s' in pack '%s'".formatted(identifierSection, identifier.getPackage()));
        }
        final ConfigurationSection targetSection = section.getConfigurationSection(identifier.get());
        if (targetSection == null) {
            throw new QuestException("Target section '%s' does not exist (in section '%s' in pack '%s')"
                    .formatted(identifier.get(), identifierSection, identifier.getPackage()));
        }
        final List<ConditionIdentifier> identifiers = new ArrayList<>();
        for (final Map.Entry<String, Object> entry : targetSection.getValues(true).entrySet()) {
            if (entry.getValue() instanceof ConfigurationSection) {
                continue;
            }
            identifiers.add(identifierFactory.parseIdentifier(pack, identifier.get() + "." + entry.getKey()));
        }
        return identifiers;
    }
}
