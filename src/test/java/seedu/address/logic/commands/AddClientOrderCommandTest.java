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
import seedu.address.model.order.Deadline;
import seedu.address.model.order.Quantity;

public class AddClientOrderCommandTest {
    private final Index clientIndex = Index.fromOneBased(1);
    private final Index itemIndex = Index.fromOneBased(2);
    private final Quantity quantity = new Quantity(5);
    private final Deadline deadline = new Deadline("2026-10-15");

    @Test
    public void constructor_nullField_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                new AddClientOrderCommand(null, itemIndex, quantity, deadline));
        assertThrows(NullPointerException.class, () ->
                new AddClientOrderCommand(clientIndex, null, quantity, deadline));
        assertThrows(NullPointerException.class, () ->
                new AddClientOrderCommand(clientIndex, itemIndex, null, deadline));
        assertThrows(NullPointerException.class, () ->
                new AddClientOrderCommand(clientIndex, itemIndex, quantity, null));
    }

    @Test
    public void execute_missingItem_rejectsWithoutChangingModel() throws Exception {
        ModelManager model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        ModelManager expected = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Command command = new AddressBookParser().parseCommand("add-order 1 i/2 q/5 d/2026-10-15");
        assertThrows(CommandException.class, AddClientOrderCommand.MESSAGE_INVALID_ITEM, () ->
                command.execute(model));
        assertEquals(expected, model);
    }

    @Test
    public void equals_differentField_returnsFalse() {
        AddClientOrderCommand command = new AddClientOrderCommand(clientIndex, itemIndex, quantity, deadline);
        AddClientOrderCommand copy = new AddClientOrderCommand(clientIndex, itemIndex, quantity, deadline);
        assertEquals(command, command);
        assertEquals(command, copy);
        assertEquals(command.hashCode(), copy.hashCode());
        assertFalse(command.equals(null));
        assertFalse(command.equals(1));
        assertFalse(command.equals(new AddClientOrderCommand(itemIndex, itemIndex, quantity, deadline)));
        assertFalse(command.equals(new AddClientOrderCommand(clientIndex, clientIndex, quantity, deadline)));
        assertFalse(command.equals(new AddClientOrderCommand(clientIndex, itemIndex, new Quantity(6), deadline)));
        assertFalse(command.equals(new AddClientOrderCommand(clientIndex, itemIndex, quantity,
                new Deadline("2026-10-16"))));
    }
}
