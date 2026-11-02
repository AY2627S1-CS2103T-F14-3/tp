package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Person;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

public class OptionalEmailTest {
    @TempDir
    public Path directory;

    private Model model;
    private Logic logic;
    private JsonAddressBookStorage storage;

    @BeforeEach
    public void setUp() {
        model = new ModelManager();
        storage = new JsonAddressBookStorage(directory.resolve("book.json"));
        logic = new LogicManager(model, new StorageManager(storage,
                new JsonUserPrefsStorage(directory.resolve("prefs.json"))));
    }

    @Test
    public void add_withoutEmail_supportsOrdersAndReload() throws Exception {
        String feedback = logic.execute("add-client n/Carol p/93456789 a/30 Test Road").getFeedbackToUser();
        assertTrue(feedback.contains("Email: Not provided"));
        logic.execute("add-item n/Cake p/5");
        logic.execute("add-order 1 i/1 q/2 d/2026-12-01");

        ReadOnlyAddressBook reloaded = storage.readAddressBook().orElseThrow();
        assertEquals(model.getAddressBook(), reloaded);
        assertEquals("", reloaded.getPersonList().getFirst().getEmail().value);
        assertEquals(1, reloaded.getOrderList().size());
        assertEquals(reloaded.getPersonList().getFirst(), reloaded.getOrderList().getFirst().getClient());
        assertFalse(Files.readString(directory.resolve("book.json")).contains("Not provided"));
    }

    @Test
    public void add_emptyEmail_isAbsentAndPersists() throws Exception {
        logic.execute("add-client n/Carol p/93456789 e/   a/30 Test Road");

        assertEquals("", model.getAddressBook().getPersonList().getFirst().getEmail().value);
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
    }

    @Test
    public void edit_omittedEmailRetainsValueAndEmptyEmailClearsIt() throws Exception {
        logic.execute("add-client n/Carol p/93456789 e/carol@example.com a/30 Test Road");
        logic.execute("edit-client 1 p/94567890");
        assertEquals("carol@example.com", model.getAddressBook().getPersonList().getFirst().getEmail().value);

        String feedback = logic.execute("edit-client 1 e/").getFeedbackToUser();
        assertTrue(feedback.contains("Email: Not provided"));
        assertEquals("", model.getAddressBook().getPersonList().getFirst().getEmail().value);
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());

        logic.execute("undo");
        assertEquals("carol@example.com", model.getAddressBook().getPersonList().getFirst().getEmail().value);
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
    }

    @Test
    public void edit_emailChanges_preserveOrdersAndUndo() throws Exception {
        logic.execute("add-client n/Carol p/93456789 a/30 Test Road");
        logic.execute("add-item n/Cake p/5");
        logic.execute("add-order 1 i/1 q/2 d/2026-12-01");
        AddressBook before = new AddressBook(model.getAddressBook());

        logic.execute("edit-client 1 e/carol@example.com");
        Person client = model.getAddressBook().getPersonList().getFirst();
        assertEquals("carol@example.com", client.getEmail().value);
        assertEquals(client, model.getAddressBook().getOrderList().getFirst().getClient());
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());

        logic.execute("undo");
        assertEquals(before, model.getAddressBook());
        assertEquals(before, storage.readAddressBook().orElseThrow());
    }

    @Test
    public void addAndEdit_invalidOrRepeatedEmails_doNotChangeData() throws Exception {
        logic.execute("add-client n/Carol p/93456789 a/30 Test Road");
        AddressBook before = new AddressBook(model.getAddressBook());
        String[] invalidCommands = {
            "add-client n/Other p/94567890 e/invalid a/Other Road",
            "add-client n/Other p/94567890 e/ e/other@example.com a/Other Road",
            "add-client n/Other p/94567890 e/ e/ a/Other Road",
            "edit-client 1 e/invalid",
            "edit-client 1 e/ e/carol@example.com",
            "edit-client 1 e/ e/"
        };
        for (String command : invalidCommands) {
            assertThrows(ParseException.class, () -> logic.execute(command));
            assertEquals(before, model.getAddressBook());
            assertEquals(before, storage.readAddressBook().orElseThrow());
        }
    }
}
