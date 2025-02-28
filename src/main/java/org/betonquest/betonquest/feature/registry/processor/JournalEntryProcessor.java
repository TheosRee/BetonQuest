package org.betonquest.betonquest.feature.registry.processor;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.id.JournalEntryID;
import org.betonquest.betonquest.message.ParsedSectionMessage;
import org.betonquest.betonquest.quest.registry.processor.VariableProcessor;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Loads and stores Journal entries.
 */
public class JournalEntryProcessor extends SectionProcessor<JournalEntryID, ParsedSectionMessage> {

    /**
     * Processor to create new variables.
     */
    private final VariableProcessor variableProcessor;

    /**
     * Create a new QuestProcessor to store and execute journal entry logic.
     *
     * @param log               the custom logger for this class
     * @param variableProcessor the processor to create new variables
     */
    public JournalEntryProcessor(final BetonQuestLogger log, final VariableProcessor variableProcessor) {
        super(log, "Journal Entry", "journal");
        this.variableProcessor = variableProcessor;
    }

    @Override
    protected ParsedSectionMessage loadSection(final QuestPackage pack, final ConfigurationSection section) throws QuestException {
        return new ParsedSectionMessage(variableProcessor, pack, section, "");
    }

    @Override
    protected JournalEntryID getIdentifier(final QuestPackage pack, final String identifier) throws QuestException {
        return new JournalEntryID(pack, identifier);
    }

    /**
     * Renames the journal entry instance.
     *
     * @param name   the current name
     * @param rename the name it should have now
     */
    public void renameJournalEntry(final JournalEntryID name, final JournalEntryID rename) {
        final ParsedSectionMessage message = values.remove(name);
        values.put(rename, message);
    }
}
