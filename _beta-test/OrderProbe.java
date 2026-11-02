import java.util.*;
import seedu.address.model.*;
import seedu.address.model.order.*;
import seedu.address.model.util.SampleDataUtil;
import seedu.address.logic.parser.AddressBookParser;

public class OrderProbe {
    static final AddressBookParser parser = new AddressBookParser();
    static final ModelManager model = new ModelManager(SampleDataUtil.getSampleAddressBook(), new UserPrefs());
    static int checks;
    static void run(String command) throws Exception { parser.parseCommand(command).execute(model); }
    static void sorted() {
        var expected = new ArrayList<>(model.getAddressBook().getOrderList());
        expected.sort(Comparator.comparing(o -> o.getDeadline().getValue()));
        if (!expected.equals(model.getFilteredOrderList())) throw new AssertionError("Bad sort at " + checks);
        checks++;
    }
    public static void main(String[] args) throws Exception {
        run("list-orders by-deadline"); sorted();
        Random rng = new Random(42);
        for (int i=0;i<1500;i++) {
            int client = 1+rng.nextInt(12);
            var clientOrders = model.getOrdersForClient(model.getFilteredPersonList().get(client-1));
            if (clientOrders.isEmpty() || rng.nextBoolean()) {
                run("add-order " + client + " i/" + (1+rng.nextInt(12)) + " q/" + (1+rng.nextInt(9999))
                    + " d/2026-10-" + (10+rng.nextInt(20)));
            } else {
                int order = 1+rng.nextInt(clientOrders.size());
                if (rng.nextBoolean()) run("edit-order " + client + " o/" + order + " d/2026-10-" + (10+rng.nextInt(20)));
                else run("delete-order " + client + " o/" + order);
            }
            sorted();
        }
        for (String invalid : new String[]{"add-order 1 i/1 q/0 d/2026-10-30", "add-order 1 i/1 q/10000 d/2026-10-30",
            "add-order 1 i/1 q/-1 d/2026-10-30", "add-order 1 i/1 q/1 d/2026-02-29", "add-order 1 i/1 q/1 d/2026-04-31",
            "add-order 1 i/1 q/1 q/2 d/2026-10-30", "edit-order 1 o/1 q/0", "edit-order 1 o/1 d/2026-02-29",
            "delete-order 1 o/2147483648", "mark 1 o/2147483648"}) {
            var before = new AddressBook(model.getAddressBook());
            try {run(invalid);throw new AssertionError("accepted invalid: "+invalid);} catch (seedu.address.logic.parser.exceptions.ParseException | seedu.address.logic.commands.exceptions.CommandException expected) {}
            if (!before.equals(model.getAddressBook())) throw new AssertionError("Mutated after rejected " + invalid);
            checks++;
        }
        System.out.println("PASS " + checks + " order checks against released JAR; final order count " + model.getAddressBook().getOrderList().size());
    }
}
