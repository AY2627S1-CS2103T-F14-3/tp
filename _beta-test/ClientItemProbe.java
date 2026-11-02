import java.nio.file.*;
import java.util.logging.*;
import seedu.address.logic.*;
import seedu.address.model.*;
import seedu.address.storage.*;

/** Black-box command/persistence probe, compiled only against the published JAR. */
public class ClientItemProbe {
    static ModelManager model;
    static LogicManager logic;
    static Path data = Path.of("_beta-test/client-item-probe/addressbook.json");
    static void init() throws Exception {
        model = new ModelManager();
        logic = new LogicManager(model, new StorageManager(new JsonAddressBookStorage(data),
                new JsonUserPrefsStorage(data.resolveSibling("prefs.json"))));
    }
    static void command(String command) {
        System.out.println("\n> " + command.replace("\t", "<TAB>"));
        try { System.out.println("SUCCESS " + logic.execute(command).getFeedbackToUser()); }
        catch (Exception e) { System.out.println(e.getClass().getSimpleName() + ": " + e.getMessage()); }
        System.out.println("STATE persons=" + model.getAddressBook().getPersonList().size()
                + " items=" + model.getAddressBook().getBakeryItemList().size()
                + " orders=" + model.getAddressBook().getOrderList().size()
                + " displayedClients=" + model.getFilteredPersonList()
                + " displayedItems=" + model.getFilteredBakeryItemList());
    }
    public static void main(String[] args) throws Exception {
        Logger.getLogger("").setLevel(Level.OFF);
        init();
        command("add-client n/John Tan p/91234567 e/john@one.com a/Block 1");
        command("add-client n/John Tan p/98765432 e/john@two.com a/Block 2");
        command("add-client n/john  tan p/98765432 e/john@two.com a/Block 2");
        command("add-client n/Mary Lee p/98765432 e/mary@example.com a/Block 3");
        command("find-clients Mary");
        command("edit-client 1 a/New Address");
        command("find-clients Mary");
        command("delete-client 1");
        command("undo");
        command("undo");
        command("add-item n/Chocolate Cake p/5.99");
        command("add-item n/chocolate  cake p/6");
        command("add-item n/Banana Bread p/.5");
        command("find-items Banana");
        command("edit-item 1 p/8");
        command("find-items Banana");
        command("delete-item 1");
        command("undo");
        command("add-order 1 i/1 q/3 d/2026-11-01");
        command("edit-client 1 n/Johnny Tan");
        command("edit-item 1 n/Premium Cake");
        command("delete-client 1");
        command("delete-item 1");
        System.out.println("RELOADED " + new JsonAddressBookStorage(data).readAddressBook().get().getOrderList());
        command("edit-client 1 n/Johnny Tan");
        command("undo");
        command("edit-client 1 n/Johnny Tan x/typo");
        command("edit-client 1 a/Somewhere q/9");
        command("add-client\tn/Tab Name\tp/12345678\te/tab@example.com\ta/Block");
        command("edit-item 1 n/Premium Cake x/typo");
        command("add-item n/Free p/0");
        command("add-item n/Huge p/99999999999999999999999999999999");
        command("add-client n/O'Neil p/12345678 e/name+@example.com a/Block");
        command("add-client n/Chinese Name p/+6591234567 e/name@example.com a/Block");
        command("clear");
        command("clear confirm extra");
        command("populate");
        command("undo");
        command("populate typo");
        command("list-items");
        System.out.println("RELOAD_EQUALS " + new JsonAddressBookStorage(data).readAddressBook().get().equals(model.getAddressBook()));
    }
}
