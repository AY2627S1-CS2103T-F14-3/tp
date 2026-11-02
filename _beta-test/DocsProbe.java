import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import seedu.address.logic.LogicManager;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.model.ModelManager;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

/** Focused reproduction of documentation discrepancies against the release JAR. */
public class DocsProbe {
    static final ModelManager model = new ModelManager();
    static final Path data = Path.of("_beta-test/docs-probe/addressbook.json");
    static final LogicManager logic = new LogicManager(model,
            new StorageManager(new JsonAddressBookStorage(data),
                    new JsonUserPrefsStorage(data.resolveSibling("prefs.json"))));

    static void run(String command) {
        System.out.println("\n> " + command);
        try {
            System.out.println(logic.execute(command).getFeedbackToUser());
        } catch (Exception e) {
            System.out.println(e.getClass().getSimpleName() + ": " + e.getMessage());
        }
        System.out.println("Stored orders: " + model.getAddressBook().getOrderList().size()
                + "; displayed orders: " + model.getFilteredOrderList().size());
    }

    public static void main(String[] args) {
        Logger.getLogger("").setLevel(Level.OFF);
        run("add-client n/Alpha Baker p/12345678 e/alpha@example.com a/Home");
        run("add-item n/Seasonal Tart p/10");
        run("add-order 1 i/1 q/1 d/2026-10-30");
        run("list-incomplete-orders");
        run("mark 1 o/1");
        run("list-orders");
        run("delete-item 1");
        run("delete 1");
        run("clear");
        System.out.println("\nCURRENT HELP CLEAR USAGE:\n" + ClearCommand.MESSAGE_USAGE);
    }
}
