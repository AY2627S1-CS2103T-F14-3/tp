import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import seedu.address.logic.LogicManager;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.util.SampleDataUtil;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

public class TriageStorageProbe {
    public static void main(String[] args) throws Exception {
        Path dir = Files.createTempDirectory(Path.of(args[0]), "storage-triage-");
        Path book = dir.resolve("book.json");
        JsonAddressBookStorage disk = new JsonAddressBookStorage(book);
        disk.saveAddressBook(SampleDataUtil.getSampleAddressBook());
        ModelManager first = new ModelManager(disk.readAddressBook().orElseThrow(), new UserPrefs());
        ModelManager stale = new ModelManager(disk.readAddressBook().orElseThrow(), new UserPrefs());
        LogicManager a = new LogicManager(first, new StorageManager(disk,
                new JsonUserPrefsStorage(dir.resolve("prefs-a.json"))));
        LogicManager b = new LogicManager(stale, new StorageManager(new JsonAddressBookStorage(book),
                new JsonUserPrefsStorage(dir.resolve("prefs-b.json"))));
        a.execute("add-order 1 i/1 q/123 d/2026-12-01");
        System.out.println("AFTER_NEW_ORDER=" + disk.readAddressBook().orElseThrow().getOrderList().size());
        b.execute("list-orders");
        System.out.println("AFTER_STALE_LIST=" + disk.readAddressBook().orElseThrow().getOrderList().size());
        JsonAddressBookStorage unavailable = new JsonAddressBookStorage(dir.resolve("fail.json")) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook value) throws IOException {
                throw new IOException("Replacement blocked");
            }
        };
        LogicManager locked = new LogicManager(new ModelManager(SampleDataUtil.getSampleAddressBook(), new UserPrefs()),
                new StorageManager(unavailable, new JsonUserPrefsStorage(dir.resolve("prefs-c.json"))));
        for (String command : new String[] {"help", "exit", "find-items Muffin"}) {
            try {
                locked.execute(command);
                throw new AssertionError("Expected current save failure");
            } catch (CommandException expected) {
                System.out.println("BLOCKED=" + command + " : " + expected.getMessage());
            }
        }
    }
}
