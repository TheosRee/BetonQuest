package org.betonquest.betonquest.command.quest;

/**
 * Accessor Type for ID completion.
 * The enum in lower case is the used section.
 */
public enum AccessorType {
    /**
     * ActionID.
     */
    ACTIONS(true),
    /**
     * ConditionID.
     */
    CONDITIONS(true),
    /**
     * ObjectiveID.
     */
    OBJECTIVES(true),
    /**
     * ItemID.
     */
    ITEMS(true),
    /**
     * JournalID.
     */
    JOURNAL(false);

    /**
     * If the accessor allows nested ids.
     */
    public final boolean allowNested;

    AccessorType(final boolean allowNested) {
        this.allowNested = allowNested;
    }
}
