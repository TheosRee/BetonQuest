package org.betonquest.betonquest.quest.condition.section;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.ConditionIdentifier;
import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.InstructionArgumentParser;
import org.betonquest.betonquest.api.quest.condition.NullableConditionAdapter;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.api.service.condition.ConditionManager;
import org.betonquest.betonquest.id.condition.DefaultConditionIdentifier;
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
     * Parses the given string into an identifier to an existing section.
     */
    private final InstructionArgumentParser<SectionIdentifier> parser;

    /**
     * Create the section condition factory.
     *
     * @param conditionManager  the condition manager
     * @param identifierFactory the factory to create identifier from section keys
     * @param packageManager    the package manager used to resolve relative paths
     */
    public SectionConditionFactory(final ConditionManager conditionManager, final IdentifierFactory<ConditionIdentifier> identifierFactory,
                                   final QuestPackageManager packageManager) {
        this.conditionManager = conditionManager;
        this.identifierFactory = identifierFactory;
        this.parser = new SectionIdentifierParser(packageManager, "Condition", DefaultConditionIdentifier.CONDITION_SECTION);
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
        final Argument<List<ConditionIdentifier>> conditionIDs = instruction.parse(parser).map(this::identifiersFromSectionName).get();
        return new NullableConditionAdapter(new ConjunctionCondition(conditionIDs, conditionManager));
    }

    private List<ConditionIdentifier> identifiersFromSectionName(final SectionIdentifier identifier) throws QuestException {
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
