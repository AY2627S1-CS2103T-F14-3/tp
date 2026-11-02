import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.logging.*;
import seedu.address.logic.*;
import seedu.address.model.*;
import seedu.address.storage.*;

/** Further ordinary-input and failed-save checks against the released v1.5 JAR. */
public class ClientItemExtendedProbe {
    static ModelManager model;
    static LogicManager logic;
    static Path data;
    static int checks;
    static void init(String fixture) throws Exception {
        Path base = Path.of("_beta-test/client-item-extended").toAbsolutePath().normalize();
        data = base.resolve(fixture + "-" + UUID.randomUUID()).resolve("addressbook.json");
        if (!data.startsWith(base)) throw new IllegalStateException("Scratch path escapes expected base");
        Files.createDirectories(data.getParent());
        model = new ModelManager();
        logic = new LogicManager(model, new StorageManager(new JsonAddressBookStorage(data),
                new JsonUserPrefsStorage(data.resolveSibling("prefs.json"))));
        for (Handler handler : Logger.getLogger("").getHandlers()) handler.setLevel(Level.OFF);
        System.out.println("\nFIXTURE " + fixture + " " + data);
    }
    static boolean command(String text) {
        System.out.println("\n> " + text);
        try {
            System.out.println("SUCCESS " + logic.execute(text).getFeedbackToUser());
            return true;
        } catch (Exception e) {
            System.out.println(e.getClass().getSimpleName() + ": " + e.getMessage());
            return false;
        }
    }
    static void check(String title, boolean result) {
        checks++;
        System.out.println((result ? "PASS " : "FAIL ") + title);
        if (!result) throw new AssertionError(title);
    }
    static AddressBook snapshot() { return new AddressBook(model.getAddressBook()); }
    static String views() {
        return model.getFilteredPersonList().toString() + "|" + model.getFilteredBakeryItemList()
                + "|" + model.getFilteredOrderList() + "|sorted=" + (model.getOrderComparator() != null);
    }
    public static void main(String[] args) throws Exception {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));
        init("realistic-inputs");
        check("first John accepted", command("add-client n/John Tan p/91234567 e/john@one.com a/Block 1"));
        check("distinct second John rejected", !command("add-client n/John Tan p/98765432 e/john@two.com a/Block 2"));
        check("only one client stored", model.getAddressBook().getPersonList().size() == 1);
        check("accented name rejected", !command("add-client n/Jos\u00e9 Tan p/91234568 e/jose@example.com a/Block 3"));
        check("Chinese name rejected", !command("add-client n/\u738b\u5c0f\u660e p/91234569 e/wang@example.com a/Block 4"));
        check("curly apostrophe name rejected", !command("add-client n/Anne O\u2019Neil p/91234560 e/anne@example.com a/Block 5"));
        check("straight apostrophe control accepted", command("add-client n/Anne O'Neil p/91234560 e/anne@example.com a/Block 5"));
        check("plus phone rejected", !command("add-client n/Mary Lee p/+6591234567 e/mary@example.com a/Block 6"));
        check("spaced phone rejected", !command("add-client n/Mary Lee p/9123 4567 e/mary@example.com a/Block 6"));
        check("digits-only phone control accepted", command("add-client n/Mary Lee p/6591234567 e/mary@example.com a/Block 6"));

        init("populate-recovery");
        check("real business record added", command("add-client n/Real Customer p/91234567 e/real@example.com a/Real Address"));
        check("populate succeeds without confirmation", command("populate"));
        check("business record absent after populate", model.getAddressBook().getPersonList().stream()
                .noneMatch(p -> p.getName().fullName.equals("Real Customer")));
        check("ordinary subsequent add succeeds", command("add-client n/New Customer p/98765432 e/new@example.com a/New Address"));
        check("undo after subsequent add succeeds", command("undo"));
        check("undo only restores sample book", model.getAddressBook().getPersonList().size() == 12
                && model.getAddressBook().getPersonList().stream().noneMatch(p -> p.getName().fullName.equals("Real Customer")));
        ReadOnlyAddressBook reloaded = new JsonAddressBookStorage(data).readAddressBook().orElseThrow();
        check("reload still contains only sample book", reloaded.getPersonList().size() == 12
                && reloaded.getPersonList().stream().noneMatch(p -> p.getName().fullName.equals("Real Customer")));
        check("no earlier undo remains", !command("undo"));

        init("save-rollback");
        command("add-client n/Alice p/91234567 e/alice@example.com a/Alice Original Address");
        command("add-client n/Bob p/98765432 e/bob@example.com a/Bob Original Address");
        command("add-item n/Cake p/5");
        command("add-item n/Bread p/2");
        command("add-order 1 i/1 q/2 d/2026-11-01");
        command("find-clients Bob");
        command("find-items Bread");
        command("list-incomplete-orders by-deadline");
        AddressBook before = snapshot();
        String beforeViews = views();
        check("undo available before save failure", logic.undoAvailableProperty().get());
        Path backup = data.resolveSibling("before-blocked-save.json");
        Files.move(data, backup);
        Files.createDirectory(data);
        System.out.println("BLOCKED SAVE: empty directory now occupies exact data-file path");
        check("edit fails when destination is directory", !command("edit-client 1 a/Bob New Address"));
        check("failed edit restores entire book", before.equals(model.getAddressBook()));
        check("failed edit restores client/item/order filters and sorting", beforeViews.equals(views()));
        check("failed edit retains earlier undo", logic.undoAvailableProperty().get());
        check("failed save preserves original disk backup", new JsonAddressBookStorage(backup).readAddressBook().orElseThrow().equals(before));
        check("undo itself also fails under blocked save", !command("undo"));
        check("failed undo restores current book", before.equals(model.getAddressBook()));
        check("failed undo restores views", beforeViews.equals(views()));
        check("failed undo retains snapshot for retry", logic.undoAvailableProperty().get());
        Files.delete(data); // deletes only the empty directory created above; never recursive
        Files.move(backup, data);
        check("undo retries after disk repair", command("undo"));
        check("earlier successful order addition was undone", model.getAddressBook().getOrderList().isEmpty()
                && model.getAddressBook().getPersonList().size() == 2 && model.getAddressBook().getBakeryItemList().size() == 2);
        check("successful retry shows all lists and resets sorting", model.getFilteredPersonList().size() == 2
                && model.getFilteredBakeryItemList().size() == 2 && model.getOrderComparator() == null);
        check("successful retry consumes undo", !logic.undoAvailableProperty().get());
        check("retry result persisted", new JsonAddressBookStorage(data).readAddressBook().orElseThrow().equals(model.getAddressBook()));
        System.out.println("\nCOMPLETED " + checks + " checks");
    }
}
