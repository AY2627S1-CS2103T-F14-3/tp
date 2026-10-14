package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;

public class UnmarkOrderCommandTest {
    private final Index clientIndex = Index.fromOneBased(1);
    private final Index orderIndex = Index.fromOneBased(2);

    @Test
    public void constructor_nullIndex_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new UnmarkOrderCommand(null, orderIndex));
        assertThrows(NullPointerException.class, () -> new UnmarkOrderCommand(clientIndex, null));
    }

    @Test
    public void execute_parsedRequest_reportsUnavailableWithoutChangingModel() throws Exception {
        ModelManager model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        ModelManager expected = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Command command = new AddressBookParser().parseCommand("unmark 1 o/2");
        assertThrows(CommandException.class, UnmarkOrderCommand.MESSAGE_NOT_IMPLEMENTED, () ->
                command.execute(model));
        assertEquals(expected, model);
    }

    @Test
    public void equals_differentIndex_returnsFalse() {
        UnmarkOrderCommand command = new UnmarkOrderCommand(clientIndex, orderIndex);
        UnmarkOrderCommand copy = new UnmarkOrderCommand(clientIndex, orderIndex);
        assertEquals(command, command);
        assertEquals(command, copy);
        assertEquals(command.hashCode(), copy.hashCode());
        assertFalse(command.equals(null));
        assertFalse(command.equals(1));
        assertFalse(command.equals(new UnmarkOrderCommand(orderIndex, orderIndex)));
        assertFalse(command.equals(new UnmarkOrderCommand(clientIndex, clientIndex)));
    }
}
