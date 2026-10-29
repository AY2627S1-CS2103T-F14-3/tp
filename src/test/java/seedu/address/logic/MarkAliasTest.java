package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalBakeryItems.BANANA_MUFFIN;
import static seedu.address.testutil.TypicalPersons.ALICE;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.ModelManager;
import seedu.address.model.order.ClientOrder;
import seedu.address.model.order.Deadline;
import seedu.address.model.order.Quantity;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

public class MarkAliasTest {
    @TempDir
    public Path directory;

    @Test
    public void parseCommand_bothSpellings_produceEqualCommandsAndErrors() throws Exception {
        AddressBookParser parser = new AddressBookParser();
        for (String args : List.of(" 1 o/2", "\t01\to/02 ", " 2147483647 o/2147483647")) {
            assertEquals(parser.parseCommand("mark-order-complete" + args), parser.parseCommand("mark" + args));
        }
        for (String args : List.of("", " 1", " o/1", " 0 o/1", " 1 o/0", " 1 o/1 o/2",
                " 1 o/", " 2147483648 o/1", " 1 o/2147483648", " 1 o/1 extra", " 1 o/1 q/2")) {
            ParseException canonical = assertThrows(ParseException.class, () ->
                    parser.parseCommand("mark-order-complete" + args));
            ParseException alias = assertThrows(ParseException.class, () -> parser.parseCommand("mark" + args));
            assertEquals(canonical.getMessage(), alias.getMessage());
        }
    }

    @Test
    public void execute_alias_selectsOnlyOneRepeatOrderPersistsAndUndoes() throws Exception {
        ModelManager model = new ModelManager();
        model.addPerson(ALICE);
        model.addBakeryItem(BANANA_MUFFIN);
        ClientOrder first = ClientOrder.create(ALICE, BANANA_MUFFIN, new Quantity(2), new Deadline("2026-10-15"));
        ClientOrder second = ClientOrder.create(ALICE, BANANA_MUFFIN, first.getQuantity(), first.getDeadline());
        model.addOrder(first);
        model.addOrder(second);
        AddressBook before = new AddressBook(model.getAddressBook());
        JsonAddressBookStorage storage = new JsonAddressBookStorage(directory.resolve("book.json"));
        Logic logic = new LogicManager(model, new StorageManager(storage,
                new JsonUserPrefsStorage(directory.resolve("prefs.json"))));
        logic.execute("mark 1 o/2");
        assertEquals(List.of(first, second.withCompletion(true)), model.getAddressBook().getOrderList());
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
        assertTrue(logic.undoAvailableProperty().get());
        for (String word : List.of("mark", "mark-order-complete")) {
            assertEquals("This order is already complete.",
                    assertThrows(CommandException.class, () -> logic.execute(word + " 1 o/2")).getMessage());
            assertThrows(CommandException.class, () -> logic.execute(word + " 2 o/1"));
            assertThrows(CommandException.class, () -> logic.execute(word + " 1 o/3"));
        }
        logic.execute("undo");
        assertEquals(before, model.getAddressBook());
        assertEquals(before, storage.readAddressBook().orElseThrow());
        assertFalse(logic.undoAvailableProperty().get());
    }
}
