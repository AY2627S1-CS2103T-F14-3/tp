import java.util.*;
import java.nio.file.*;
import java.util.logging.*;
import seedu.address.model.*;
import seedu.address.model.order.*;
import seedu.address.logic.LogicManager;
import seedu.address.storage.*;

public class OrderLogicProbe {
    static final ModelManager model = new ModelManager();
    static final JsonAddressBookStorage json = new JsonAddressBookStorage(Path.of("_beta-test/order-probe-data/addressbook.json"));
    static final LogicManager logic = new LogicManager(model, new StorageManager(json,
        new JsonUserPrefsStorage(Path.of("_beta-test/order-probe-data/preferences.json"))));
    static int checks;
    static void ok(boolean truth, String detail) {if (!truth) throw new AssertionError(detail);checks++;}
    static void run(String command) throws Exception {
        var result = logic.execute(command);
        System.out.println("> " + command.replace("\t", "\\t") + "\n" + result.getFeedbackToUser());
        ok(model.getAddressBook().equals(json.readAddressBook().get()), "round-trip: " + command);
    }
    static void invalid(String command) throws Exception {
        var before = new AddressBook(model.getAddressBook());
        try {run(command);throw new AssertionError("accepted: "+command);} catch (seedu.address.logic.parser.exceptions.ParseException | seedu.address.logic.commands.exceptions.CommandException expected) {
            System.out.println("> " + command + "\nREJECTED: " + expected.getMessage());
        }
        ok(before.equals(model.getAddressBook()),"invalid changed data: " + command);
    }
    public static void main(String[] args) throws Exception {
        LogManager.getLogManager().reset();
        run("add-client n/Alice p/1234567 e/alice@example.com a/Alpha Road");
        run("add-client n/Bob p/1234567 e/bob@example.com a/Beta Road");
        run("add-item n/Cake p/2.00");run("add-item n/Muffin p/.50");
        for (String date : new String[]{"0000-01-01","0000-02-29","0001-01-01","2000-02-29","9999-12-31"}) {
            run("add-order 1 i/1 q/9999 d/" + date);
        }
        run("add-order\t2\tq/0001\ti/2\td/2026-10-30");
        for (String command : new String[]{"add-order 1i/1 q/1 d/2026-10-30","add-order 1 i/1q/1 d/2026-10-30",
            "add-order 1 i/1 q/1d/2026-10-30", "add-order 1 x/1 q/1 d/2026-10-30", "add-order 1 i/1 q/1 x/a d/2026-10-30",
            "add-order 1 i/1 q/1 d/2026-10-30 x/a", "add-order 1 i/1 q/1 d/9999-12-32", "add-order 1 i/1 q/1 d/1900-02-29",
            "add-order 1 i/1 q/1 d/10000-01-01", "add-order 1 i/1 q/1 d/-001-01-01", "edit-order 1 o/1 q/1 q/2",
            "edit-order 1 o/1", "delete-order 1 o/1 o/2", "mark 1 o/1 x/foo"}) invalid(command);
        run("list-orders by-deadline");
        var last = model.getFilteredOrderList().getLast();
        run("edit-order 1 o/5 d/0000-01-01");
        ok(model.getFilteredOrderList().get(1).getId().equals(last.getId()), "tie preserves insertion");
        run("undo");ok(model.getAddressBook().getOrderList().get(4).getDeadline().toString().equals("9999-12-31"),"undo date");
        ok(model.getOrderComparator()==null,"undo clears sorting");
        run("find-clients Bob");run("find-items Muffin");
        run("add-order 1 i/1 q/2 d/2026-11-01");
        ok(model.getAddressBook().getOrderList().getLast().getClient().getName().fullName.equals("Bob"),"filtered client");
        ok(model.getAddressBook().getOrderList().getLast().getItem().getItemName().toString().equals("Muffin"),"filtered item");
        run("list-incomplete-orders by-deadline");run("mark 1 o/2");
        ok(model.getFilteredOrderList().size()==6,"completion filter auto removes");
        run("list-complete-orders");ok(model.getFilteredOrderList().size()==1,"completed filter");
        run("unmark 1 o/2");ok(model.getFilteredOrderList().isEmpty(),"unmark removes from complete filter");
        run("undo");ok(model.getFilteredOrderList().size()==7,"undo shows all orders");
        run("find-orders Cake");run("edit-order 1 o/2 i/2");
        ok(model.getFilteredOrderList().size()==4,"editing search membership");
        run("edit-order 1 o/2 i/2");run("undo");
        ok(model.getAddressBook().getOrderList().get(1).getItem().getItemName().toString().equals("Cake"),"noop retains undo");
        run("find-orders Alice");run("edit-client 1 n/Alicia");
        ok(model.getFilteredOrderList().isEmpty(),"client rename search membership");
        run("find-orders Cake");run("edit-item 1 n/Pie");
        ok(model.getFilteredOrderList().isEmpty(),"item rename search membership");
        run("list-orders");invalid("delete-client 1");invalid("delete-item 1");
        System.out.println("PASS " + checks + " real-persistence order logic checks against released JAR.");
    }
}
