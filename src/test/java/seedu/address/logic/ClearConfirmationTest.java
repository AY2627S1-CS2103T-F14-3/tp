package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

public class ClearConfirmationTest {
    @TempDir
    public Path directory;

    private final ModelManager model = new ModelManager();
    private LogicManager logic;
    private JsonAddressBookStorage storage;

    @BeforeEach
    public void setUp() throws Exception {
        storage = new JsonAddressBookStorage(directory.resolve("book.json"));
        logic = logicWith(storage);
        logic.execute("populate");
    }

    @Test
    public void clear_unconfirmedOrInvalid_preservesDataFiltersAndFile() throws Exception {
        AddressBook before = new AddressBook(model.getAddressBook());
        String saved = Files.readString(storage.getAddressBookFilePath());
        model.updateFilteredPersonList(person -> false);
        model.updateFilteredBakeryItemList(item -> false);
        for (int attempt = 0; attempt < 2; attempt++) {
            CommandException exception = assertThrows(CommandException.class, () -> logic.execute("clear"));
            assertEquals(String.format(ClearCommand.MESSAGE_CONFIRMATION_FORMAT, 6, 4, 6), exception.getMessage());
        }
        for (String args : new String[] {"3", "yes", "CONFIRM", "confirm extra", "confirm confirm"}) {
            assertThrows(ParseException.class, () -> logic.execute("clear " + args));
        }
        assertEquals(before, model.getAddressBook());
        assertEquals(saved, Files.readString(storage.getAddressBookFilePath()));
        assertTrue(model.getFilteredPersonList().isEmpty());
        assertTrue(model.getFilteredBakeryItemList().isEmpty());
        logic.execute("list-clients");
        assertEquals(before, model.getAddressBook());
    }

    @Test
    public void clear_confirmed_deletesAllDataAndPersists() throws Exception {
        assertEquals(ClearCommand.MESSAGE_SUCCESS, logic.execute(" clear  confirm ").getFeedbackToUser());
        assertEquals(new AddressBook(), model.getAddressBook());
        assertEquals(new AddressBook(), storage.readAddressBook().orElseThrow());
        logic.execute("clear confirm");
        assertEquals(new AddressBook(), model.getAddressBook());
    }

    @Test
    public void clear_warningAndInvalidConfirmation_preservePreviousUndo() throws Exception {
        AddressBook original = new AddressBook(model.getAddressBook());
        logic.execute("edit-item 1 p/123.45");
        assertThrows(CommandException.class, () -> logic.execute("clear"));
        assertThrows(ParseException.class, () -> logic.execute("clear yes"));
        logic.execute("undo");
        assertEquals(original, model.getAddressBook());
        assertEquals(original, storage.readAddressBook().orElseThrow());
    }

    @Test
    public void clear_confirmedAndThenEmptyClear_retainsSnapshotUntilUndo() throws Exception {
        AddressBook original = new AddressBook(model.getAddressBook());
        logic.execute("clear confirm");
        logic.execute("clear confirm");
        assertTrue(logic.undoAvailableProperty().get());
        logic.execute("undo");
        assertEquals(original, model.getAddressBook());
        assertEquals(original, storage.readAddressBook().orElseThrow());
        assertFalse(logic.undoAvailableProperty().get());
    }

    @Test
    public void clear_accessDenied_restoresDataFiltersAndPreviousUndo() throws Exception {
        IOException[] failure = {null};
        logic = logicWith(new JsonAddressBookStorage(storage.getAddressBookFilePath()) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook data) throws IOException {
                if (failure[0] != null) {
                    throw failure[0];
                }
                super.saveAddressBook(data);
            }
        });
        AddressBook original = new AddressBook(model.getAddressBook());
        logic.execute("edit-item 1 p/123.45");
        AddressBook edited = new AddressBook(model.getAddressBook());
        model.updateFilteredPersonList(person -> false);
        model.updateFilteredBakeryItemList(item -> false);
        failure[0] = new AccessDeniedException("book.json");
        CommandException exception = assertThrows(CommandException.class, () -> logic.execute("clear confirm"));
        assertEquals(String.format(LogicManager.FILE_OPS_PERMISSION_ERROR_FORMAT, "book.json"), exception.getMessage());
        assertEquals(edited, model.getAddressBook());
        assertEquals(edited, storage.readAddressBook().orElseThrow());
        assertTrue(model.getFilteredPersonList().isEmpty());
        assertTrue(model.getFilteredBakeryItemList().isEmpty());
        assertTrue(logic.undoAvailableProperty().get());
        failure[0] = null;
        logic.execute("undo");
        assertEquals(original, model.getAddressBook());
    }

    @Test
    public void undo_clearedBookSaveFails_keepsEmptyBookAndAllowsRetry() throws Exception {
        IOException[] failure = {null};
        logic = logicWith(new JsonAddressBookStorage(storage.getAddressBookFilePath()) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook data) throws IOException {
                if (failure[0] != null) {
                    throw failure[0];
                }
                super.saveAddressBook(data);
            }
        });
        AddressBook original = new AddressBook(model.getAddressBook());
        logic.execute("clear confirm");
        failure[0] = new IOException("disk unavailable");
        assertThrows(CommandException.class, () -> logic.execute("undo"));
        assertEquals(new AddressBook(), model.getAddressBook());
        assertEquals(new AddressBook(), storage.readAddressBook().orElseThrow());
        assertTrue(logic.undoAvailableProperty().get());
        failure[0] = null;
        logic.execute("undo");
        assertEquals(original, model.getAddressBook());
        assertEquals(original, storage.readAddressBook().orElseThrow());
    }

    @Test
    public void clear_confirmedSaveFails_restoresDataAndFilters() throws Exception {
        AddressBook before = new AddressBook(model.getAddressBook());
        model.updateFilteredPersonList(person -> false);
        model.updateFilteredBakeryItemList(item -> false);
        logic = logicWith(new JsonAddressBookStorage(storage.getAddressBookFilePath()) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook data) throws IOException {
                throw new IOException("disk unavailable");
            }
        });
        assertThrows(CommandException.class, () -> logic.execute("clear confirm"));
        assertEquals(before, model.getAddressBook());
        assertEquals(before, storage.readAddressBook().orElseThrow());
        assertTrue(model.getFilteredPersonList().isEmpty());
        assertTrue(model.getFilteredBakeryItemList().isEmpty());
    }

    private LogicManager logicWith(JsonAddressBookStorage bookStorage) {
        return new LogicManager(model, new StorageManager(bookStorage,
                new JsonUserPrefsStorage(directory.resolve("prefs.json"))));
    }
}
