package org.betonquest.betonquest.quest.event.journal;

import org.betonquest.betonquest.feature.journal.Journal;
import org.betonquest.betonquest.id.JournalEntryID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

/**
 * Test {@link RemoveEntryJournalChanger}.
 */
@ExtendWith(MockitoExtension.class)
class RemoveEntryJournalChangerTest {
    @Test
    void testChangeJournalRemovesPointer(@Mock final Journal journal) {
        final String entryName = "test_entry";
        final JournalEntryID entryID = mock(JournalEntryID.class);
        when(entryID.getFullID()).thenReturn(entryName);
        final RemoveEntryJournalChanger changer = new RemoveEntryJournalChanger(entryID);

        changer.changeJournal(journal);

        verify(journal).removePointer(entryName);
        verifyNoMoreInteractions(journal);
    }
}
