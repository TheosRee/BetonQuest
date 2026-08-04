package org.betonquest.betonquest.quest.condition.section;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.DefaultIdentifier;
import org.bukkit.configuration.ConfigurationSection;

/**
 * An identifier pointing to any {@link ConfigurationSection} defined in a quest package.
 */
public class SectionIdentifier extends DefaultIdentifier {

    /**
     * The section in the configuration where the identifier is defined.
     */
    private final String section;

    /**
     * Creates a new identifier without resolving the package.
     *
     * @param pack       the package the object is in
     * @param identifier the identifier of the object without the package name
     * @param section    the section in the configuration which is addressed
     */
    public SectionIdentifier(final QuestPackage pack, final String identifier, final String section) {
        super(pack, identifier);
        this.section = section;
    }

    /**
     * The section the identifier is defined in.
     *
     * @return the name of the section
     */
    public String getSection() {
        return section;
    }
}
