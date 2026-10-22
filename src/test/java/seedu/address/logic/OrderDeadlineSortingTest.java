package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.testutil.TypicalBakeryItems.BANANA_MUFFIN;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BOB;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.collections.ObservableList;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.order.ClientOrder;
import seedu.address.model.order.Deadline;
import seedu.address.model.order.Quantity;
import seedu.address.model.person.Person;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

/** Tests deadline sorting through parsed commands and the live order view. */
public class OrderDeadlineSortingTest {
    @TempDir
    public Path directory;

    private ModelManager model;
    private Logic logic;
    private ClientOrder later;
    private ClientOrder earlier;
    private ClientOrder sameDeadline;
    private ClientOrder completedLater;
    private ClientOrder completedEarlier;

    @BeforeEach
    public void setUp() {
        model = new ModelManager();
        model.addPerson(ALICE);
        model.addPerson(BOB);
        model.addBakeryItem(BANANA_MUFFIN);
        later = addOrder(ALICE, "2026-11-01", false);
        earlier = addOrder(ALICE, "2026-10-01", false);
        sameDeadline = addOrder(BOB, "2026-10-01", false);
        completedLater = addOrder(ALICE, "2026-12-01", true);
        completedEarlier = addOrder(BOB, "2026-09-30", true);
        logic = new LogicManager(model, new StorageManager(
                new JsonAddressBookStorage(directory.resolve("book.json")),
                new JsonUserPrefsStorage(directory.resolve("prefs.json"))));
    }

    private ClientOrder addOrder(Person client, String deadline, boolean complete) {
        ClientOrder order = ClientOrder.create(client, BANANA_MUFFIN, new Quantity(2), new Deadline(deadline))
                .withCompletion(complete);
        model.addOrder(order);
        return order;
    }

    @Test
    public void listByDeadline_sortsChronologicallyAndKeepsEqualDeadlinesInInsertionOrder() throws Exception {
        AddressBook before = new AddressBook(model.getAddressBook());
        model.updateFilteredPersonList(person -> person.equals(BOB));
        model.updateFilteredBakeryItemList(item -> false);
        logic.execute("find-orders missing");
        assertEquals("Listed all orders by deadline (earliest first).",
                logic.execute("list-orders by-deadline").getFeedbackToUser());
        assertEquals(List.of(completedEarlier, earlier, sameDeadline, later, completedLater), logic.getOrderList());
        assertEquals(List.of(BOB), logic.getFilteredPersonList());
        assertEquals(List.of(), logic.getFilteredBakeryItemList());
        assertEquals(before, model.getAddressBook());
        assertEquals(before.getOrderList(), logic.getAllOrderList());
    }

    @Test
    public void listByDeadline_completionCommands_filterAndSort() throws Exception {
        assertEquals("Listed completed orders by deadline (earliest first).",
                logic.execute("list-complete-orders by-deadline").getFeedbackToUser());
        assertEquals(List.of(completedEarlier, completedLater), logic.getOrderList());
        assertEquals("Listed incomplete orders by deadline (earliest first).",
                logic.execute("list-incomplete-orders by-deadline").getFeedbackToUser());
        assertEquals(List.of(earlier, sameDeadline, later), logic.getOrderList());
    }

    @Test
    public void listWithoutSort_restoresInsertionOrderForEachCommand() throws Exception {
        logic.execute("list-orders by-deadline");
        logic.execute("list-orders");
        assertEquals(List.of(later, earlier, sameDeadline, completedLater, completedEarlier), logic.getOrderList());
        logic.execute("list-complete-orders by-deadline");
        logic.execute("list-complete-orders");
        assertEquals(List.of(completedLater, completedEarlier), logic.getOrderList());
        logic.execute("list-incomplete-orders by-deadline");
        logic.execute("list-incomplete-orders");
        assertEquals(List.of(later, earlier, sameDeadline), logic.getOrderList());
    }

    @Test
    public void sortedView_addDeleteAndStatusChanges_updateExistingViewAndPreserveCommandIndices() throws Exception {
        logic.execute("list-incomplete-orders by-deadline");
        ObservableList<ClientOrder> view = logic.getOrderList();
        ClientOrder newest = addOrder(BOB, "2026-10-01", false);
        assertEquals(List.of(earlier, sameDeadline, newest, later), view);
        // Alice's first order is still the later one, even though it appears last in the view.
        logic.execute("mark-order-complete 1 o/1");
        assertEquals(List.of(earlier, sameDeadline, newest), view);
        logic.execute("unmark 1 o/3");
        assertEquals(List.of(earlier, sameDeadline, newest, completedLater.withCompletion(false)), view);
        logic.execute("delete-order 1 o/2");
        assertEquals(List.of(sameDeadline, newest, completedLater.withCompletion(false)), view);
        logic.execute("list-complete-orders by-deadline");
        assertEquals(List.of(completedEarlier, later.withCompletion(true)), view);
        logic.execute("unmark 2 o/2");
        assertEquals(List.of(later.withCompletion(true)), view);
    }

    @Test
    public void byDeadline_emptyResultsAndWhitespace_succeed() throws Exception {
        model.setAddressBook(new AddressBook());
        for (String command : List.of("list-orders", "list-complete-orders", "list-incomplete-orders")) {
            logic.execute(" \t" + command + " \tby-deadline \t");
            assertEquals(List.of(), logic.getOrderList());
        }
    }

    @Test
    public void byDeadline_invalidArguments_reportUsageAndPreserveCurrentView() throws Exception {
        logic.execute("list-complete-orders by-deadline");
        AddressBook before = new AddressBook(model.getAddressBook());
        for (String command : List.of("list-orders", "list-complete-orders", "list-incomplete-orders")) {
            for (String argument : List.of("extra", "by-deadlin", "BY-DEADLINE", "by-deadline extra",
                    "by-deadline by-deadline")) {
                ParseException exception = assertThrows(ParseException.class, () ->
                        logic.execute(command + " " + argument));
                assertFalse(exception.getMessage().isBlank());
                assertEquals(List.of(completedEarlier, completedLater), logic.getOrderList());
                assertEquals(before, model.getAddressBook());
            }
        }
    }

    @Test
    public void sortedView_findUndoAndPopulate_restoreInsertionOrder() throws Exception {
        logic.execute("list-orders by-deadline");
        logic.execute("find-orders Alice");
        assertEquals(List.of(later, earlier, completedLater), logic.getOrderList());
        logic.execute("mark-order-complete 1 o/1");
        logic.execute("list-complete-orders by-deadline");
        logic.execute("undo");
        assertEquals(List.of(later, earlier, sameDeadline, completedLater, completedEarlier), logic.getOrderList());
        logic.execute("list-orders by-deadline");
        logic.execute("populate");
        assertEquals(logic.getAllOrderList(), logic.getOrderList());
    }

    @Test
    public void saveFailure_restoresPreviousSortAndCompletionFilter() throws Exception {
        for (IOException failure : List.of(new IOException("Cannot save"), new AccessDeniedException("denied"))) {
            logic.execute("list-complete-orders by-deadline");
            Logic failing = new LogicManager(model, new StorageManager(
                    new JsonAddressBookStorage(directory.resolve("failing.json")) {
                        @Override
                        public void saveAddressBook(ReadOnlyAddressBook book) throws IOException {
                            throw failure;
                        }
                    }, new JsonUserPrefsStorage(directory.resolve("prefs.json"))));
            assertThrows(CommandException.class, () -> failing.execute("list-incomplete-orders"));
            assertEquals(List.of(completedEarlier, completedLater), logic.getOrderList());
            assertThrows(CommandException.class, () -> failing.execute("unmark 2 o/2"));
            assertEquals(List.of(completedEarlier, completedLater), logic.getOrderList());
            logic.execute("list-orders");
            assertThrows(CommandException.class, () -> failing.execute("list-orders by-deadline"));
            assertEquals(logic.getAllOrderList(), logic.getOrderList());
        }
    }
}
