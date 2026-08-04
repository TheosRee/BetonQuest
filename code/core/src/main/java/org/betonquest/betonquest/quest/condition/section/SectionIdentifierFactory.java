package org.betonquest.betonquest.quest.condition.section;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.factory.DefaultIdentifierFactory;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * A {@link DefaultIdentifierFactory} for {@link SectionIdentifier}s.
 */
public class SectionIdentifierFactory extends DefaultIdentifierFactory<SectionIdentifier> {

    /**
     * The section in the configuration which is addressed.
     */
    private final String section;

    /**
     * Create a new identifier factory.
     *
     * @param packManager      the quest package manager to resolve relative paths
     * @param readableTypeName the readable type name of the identifier
     * @param section          the section in the configuration which is addressed
     */
    public SectionIdentifierFactory(final QuestPackageManager packManager, final String readableTypeName, final String section) {
        super(packManager, readableTypeName);
        this.section = section;
    }

    @Override
    public SectionIdentifier parseIdentifier(@Nullable final QuestPackage source, final String input) throws QuestException {
        final Map.Entry<QuestPackage, String> parsed = parse(source, input);
        final SectionIdentifier identifier = new SectionIdentifier(parsed.getKey(), parsed.getValue(), section);
        return requireSection(identifier, section);
    }
}
