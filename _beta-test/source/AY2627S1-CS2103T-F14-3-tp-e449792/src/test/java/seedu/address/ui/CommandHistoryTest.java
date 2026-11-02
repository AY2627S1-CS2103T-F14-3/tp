package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class CommandHistoryTest {
    @Test
    public void navigate_emptyHistory_retainsInput() {
        CommandHistory history = new CommandHistory();
        assertEquals("draft", history.previous("draft"));
        assertEquals("draft", history.next("draft"));
    }

    @Test
    public void navigate_commandsAndBoundaries_restoresDraft() {
        CommandHistory history = new CommandHistory();
        history.add("list");
        history.add("find Alice");
        assertEquals("find Alice", history.previous("unfinished draft"));
        assertEquals("list", history.previous("find Alice"));
        assertEquals("list", history.previous("list"));
        assertEquals("find Alice", history.next("list"));
        assertEquals("unfinished draft", history.next("find Alice"));
        assertEquals("unfinished draft", history.next("unfinished draft"));
    }

    @Test
    public void add_editedRecall_preservesOriginalAndResetsPosition() {
        CommandHistory history = new CommandHistory();
        history.add("find Alice");
        assertEquals("find Alice", history.previous("draft"));
        history.add("find Bob");
        assertEquals("find Bob", history.previous(""));
        assertEquals("find Alice", history.previous("find Bob"));
        assertEquals("find Bob", history.next("find Alice"));
        assertEquals("", history.next("find Bob"));
    }

    @Test
    public void add_blankInput_doesNotOverwriteDraftOrHistory() {
        CommandHistory history = new CommandHistory();
        history.add("list");
        history.previous("draft");
        history.add("");
        assertEquals("draft", history.next("list"));
    }

    @Test
    public void add_repeatedCommands_keepsSubmissionOrder() {
        CommandHistory history = new CommandHistory();
        history.add("list");
        history.add("list");
        assertEquals("list", history.previous("draft"));
        assertEquals("list", history.previous("list"));
        assertEquals("list", history.next("list"));
        assertEquals("draft", history.next("list"));
    }

    @Test
    public void navigate_nullInput_throwsNullPointerException() {
        CommandHistory history = new CommandHistory();
        assertThrows(NullPointerException.class, () -> history.add(null));
        assertThrows(NullPointerException.class, () -> history.previous(null));
        assertThrows(NullPointerException.class, () -> history.next(null));
    }
}
