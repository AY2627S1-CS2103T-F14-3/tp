package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.UndoCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

public class UndoTest {
    @TempDir
    public Path directory;

    private final ModelManager model = new ModelManager();
    private LogicManager logic;
    private FailingStorage storage;

    @BeforeEach
    public void setUp() {
        storage = new FailingStorage(directory.resolve("book.json"));
        logic = new LogicManager(model, new StorageManager(storage,
                new JsonUserPrefsStorage(directory.resolve("prefs.json"))));
    }

    @Test
    public void undo_emptySessionAndExtraArguments_rejected() {
        assertFalse(logic.undoAvailableProperty().get());
        assertThrows(CommandException.class, () -> logic.execute("undo"));
        assertThrows(ParseException.class, () -> logic.execute("undo extra"));
    }

    @Test
    public void undo_latestChangeOnly_persistsAndConsumesSnapshot() throws Exception {
        logic.execute("add-item n/Cake p/10");
        AddressBook first = snapshot();
        logic.execute("add-item n/Tart p/5");
        assertTrue(logic.undoAvailableProperty().get());
        assertEquals(UndoCommand.MESSAGE_SUCCESS, logic.execute("undo").getFeedbackToUser());
        assertEquals(first, model.getAddressBook());
        assertEquals(first, storage.readAddressBook().orElseThrow());
        assertFalse(logic.undoAvailableProperty().get());
        assertThrows(CommandException.class, () -> logic.execute("undo"));
        LogicManager restarted = new LogicManager(model, new StorageManager(storage,
                new JsonUserPrefsStorage(directory.resolve("prefs.json"))));
        assertFalse(restarted.undoAvailableProperty().get());
        logic.execute("add-item n/Bread p/3");
        logic.execute("undo");
        assertEquals(first, model.getAddressBook());
    }

    @Test
    public void undo_readOnlyNoOpAndFailedCommands_preserveSnapshot() throws Exception {
        logic.execute("add-item n/Cake p/10");
        logic.execute("edit-item 1 p/10");
        logic.execute("list-items");
        logic.execute("help");
        assertThrows(ParseException.class, () -> logic.execute("unknown"));
        assertThrows(CommandException.class, () -> logic.execute("delete-item 9"));
        storage.failure = new IOException("unavailable");
        assertThrows(CommandException.class, () -> logic.execute("add-item n/Tart p/5"));
        storage.failure = null;
        logic.execute("undo");
        assertEquals(new AddressBook(), model.getAddressBook());
    }

    @Test
    public void undo_linkedDataAndIdenticalOrders_restoresAllDetails() throws Exception {
        logic.execute("populate");
        verifyUndo("edit-client 1 n/New Name");
        verifyUndo("edit-item 1 n/New Item p/99");
        verifyUndo("add-order 1 i/1 q/2 d/2026-10-22");
        logic.execute("add-order 1 i/1 q/2 d/2026-10-22");
        logic.execute("add-order 1 i/1 q/2 d/2026-10-22");
        int lastOrder = model.getOrdersForClient(model.getFilteredPersonList().get(0)).size();
        verifyUndo("delete-order 1 o/" + lastOrder);
        verifyUndo("delete-order 1 o/1");
        logic.execute("add-client n/Test p/12345678 e/test@example.com a/Test Road");
        logic.execute("add-order 7 i/1 q/2 d/2026-10-22");
        verifyUndo("mark-order-complete 7 o/1");
        logic.execute("mark-order-complete 7 o/1");
        verifyUndo("unmark 7 o/1");
        verifyUndo("clear");
        verifyUndo("populate");
        logic.execute("add-item n/Spare p/1");
        verifyUndo("delete-item 5");
        logic.execute("add-client n/Spare p/12345678 e/spare@example.com a/Test Road");
        verifyUndo("delete-client 8");
    }

    @Test
    public void undo_saveFailure_restoresViewAndAllowsRetry() throws Exception {
        logic.execute("populate");
        logic.execute("add-item n/Spare p/1");
        AddressBook current = snapshot();
        model.updateFilteredPersonList(person -> false);
        model.updateFilteredBakeryItemList(item -> false);
        model.updateFilteredOrderList(order -> false);
        for (IOException failure : new IOException[] {new IOException("disk"), new AccessDeniedException("denied")}) {
            storage.failure = failure;
            assertThrows(CommandException.class, () -> logic.execute("undo"));
            assertEquals(current, model.getAddressBook());
            assertTrue(model.getFilteredPersonList().isEmpty());
            assertTrue(model.getFilteredBakeryItemList().isEmpty());
            assertTrue(model.getFilteredOrderList().isEmpty());
            assertTrue(logic.undoAvailableProperty().get());
        }
        storage.failure = null;
        logic.execute("undo");
        assertEquals(6, model.getFilteredPersonList().size());
        assertEquals(4, model.getFilteredBakeryItemList().size());
        assertEquals(model.getAddressBook().getOrderList(), model.getFilteredOrderList());
        assertFalse(logic.undoAvailableProperty().get());
    }

    private AddressBook snapshot() {
        return new AddressBook(model.getAddressBook());
    }

    private void verifyUndo(String command) throws Exception {
        AddressBook before = snapshot();
        logic.execute(command);
        logic.execute("undo");
        assertEquals(before, model.getAddressBook());
        assertEquals(before, storage.readAddressBook().orElseThrow());
    }

    private static class FailingStorage extends JsonAddressBookStorage {
        private IOException failure;

        FailingStorage(Path path) {
            super(path);
        }

        @Override
        public void saveAddressBook(ReadOnlyAddressBook data) throws IOException {
            if (failure != null) {
                throw failure;
            }
            super.saveAddressBook(data);
        }
    }
}
