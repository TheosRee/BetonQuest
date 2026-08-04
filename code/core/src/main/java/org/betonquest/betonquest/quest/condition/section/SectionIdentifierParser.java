package org.betonquest.betonquest.quest.condition.section;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.instruction.argument.InstructionArgumentParser;
import org.betonquest.betonquest.api.service.placeholder.PlaceholderManager;

/**
 * Parses a string to an {@link SectionIdentifier}.
 */
public class SectionIdentifierParser implements InstructionArgumentParser<SectionIdentifier> {

    /**
     * Factory to create identifier from section keys.
     */
    private final SectionIdentifierFactory factory;

    /**
     * Creates a new parser for the {@link SectionIdentifier}.
     *
     * @param packManager      the quest package manager to resolve relative paths
     * @param readableTypeName the readable type name of the identifier
     * @param section          the section in the configuration which is addressed
     */
    public SectionIdentifierParser(final QuestPackageManager packManager, final String readableTypeName, final String section) {
        this.factory = new SectionIdentifierFactory(packManager, readableTypeName, section);
    }

    @Override
    public SectionIdentifier apply(final PlaceholderManager placeholders, final QuestPackageManager packManager,
                                   final QuestPackage pack, final String string) throws QuestException {
        return factory.parseIdentifier(pack, string);
    }
}
