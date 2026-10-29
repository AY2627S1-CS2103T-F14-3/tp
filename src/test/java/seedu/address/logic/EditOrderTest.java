package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalBakeryItems.BANANA_MUFFIN;
import static seedu.address.testutil.TypicalBakeryItems.CHOCOLATE_MUFFIN;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BOB;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.EditClientOrderCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.order.ClientOrder;
import seedu.address.model.order.Deadline;
import seedu.address.model.order.Quantity;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

public class EditOrderTest {
    @TempDir
    public Path directory;

    private ModelManager model;
    private Logic logic;
    private JsonAddressBookStorage storage;
    private ClientOrder first;
    private ClientOrder repeat;
    private ClientOrder bobOrder;

    @BeforeEach
    public void setUp() {
        model = new ModelManager();
        model.addPerson(ALICE);
        model.addPerson(BOB);
        model.addBakeryItem(BANANA_MUFFIN);
        model.addBakeryItem(CHOCOLATE_MUFFIN);
        first = ClientOrder.create(ALICE, BANANA_MUFFIN, new Quantity(2), new Deadline("2026-10-15"))
                .withCompletion(true);
        repeat = ClientOrder.create(ALICE, first.getItem(), first.getQuantity(), first.getDeadline())
                .withCompletion(true);
        bobOrder = ClientOrder.create(BOB, BANANA_MUFFIN, new Quantity(1), new Deadline("2026-10-01"));
        model.addOrder(first);
        model.addOrder(bobOrder);
        model.addOrder(repeat);
        storage = new JsonAddressBookStorage(directory.resolve("book.json"));
        logic = createLogic(storage);
    }

    private Logic createLogic(JsonAddressBookStorage bookStorage) {
        return new LogicManager(model, new StorageManager(bookStorage,
                new JsonUserPrefsStorage(directory.resolve("prefs.json"))));
    }

    @Test
    public void execute_allFields_preservesIdentityOwnerStatusAndPositionsAcrossReloadAndUndo() throws Exception {
        AddressBook before = new AddressBook(model.getAddressBook());
        logic.execute("edit-order 1 o/2 i/2 q/13 d/2020-02-29");
        ClientOrder edited = new ClientOrder(repeat.getId(), ALICE, CHOCOLATE_MUFFIN,
                new Quantity(13), new Deadline("2020-02-29"), true);
        assertEquals(List.of(first, bobOrder, edited), logic.getAllOrderList());
        assertEquals(List.of(first, edited), model.getOrdersForClient(ALICE));
        Model reloaded = new ModelManager(storage.readAddressBook().orElseThrow(), model.getUserPrefs());
        assertEquals(model.getAddressBook(), reloaded.getAddressBook());
        logic.execute("undo");
        assertEquals(before, model.getAddressBook());
        assertEquals(before, storage.readAddressBook().orElseThrow());
        assertFalse(logic.undoAvailableProperty().get());
    }

    @Test
    public void execute_individualFields_retainsOmittedValues() throws Exception {
        logic.execute("edit-order 1 o/1 q/9999");
        ClientOrder quantityEdit = new ClientOrder(first.getId(), ALICE, first.getItem(), new Quantity(9999),
                first.getDeadline(), true);
        assertEquals(quantityEdit, logic.getAllOrderList().getFirst());
        logic.execute("edit-order 1 o/1 d/2024-02-29");
        ClientOrder deadlineEdit = new ClientOrder(first.getId(), ALICE, first.getItem(), new Quantity(9999),
                new Deadline("2024-02-29"), true);
        assertEquals(deadlineEdit, logic.getAllOrderList().getFirst());
        logic.execute("edit-order 1 o/1 i/2");
        assertEquals(deadlineEdit.withItem(CHOCOLATE_MUFFIN), logic.getAllOrderList().getFirst());
    }

    @Test
    public void execute_filteredClientsAndItems_resolvesDisplayedIndicesAndHiddenClientLocalOrder() throws Exception {
        model.updateFilteredPersonList(person -> person.equals(BOB));
        model.updateFilteredBakeryItemList(item -> item.equals(CHOCOLATE_MUFFIN));
        logic.execute("list-complete-orders by-deadline");
        logic.execute("edit-order 1 o/1 i/1 q/7");
        ClientOrder edited = new ClientOrder(bobOrder.getId(), BOB, CHOCOLATE_MUFFIN,
                new Quantity(7), bobOrder.getDeadline(), false);
        assertEquals(List.of(first, edited, repeat), logic.getAllOrderList());
        assertEquals(List.of(first, repeat), logic.getOrderList());
        assertEquals(List.of(BOB), logic.getFilteredPersonList());
        assertEquals(List.of(CHOCOLATE_MUFFIN), logic.getFilteredBakeryItemList());
        model.updateFilteredBakeryItemList(item -> false);
        logic.execute("edit-order 1 o/1 q/8");
        assertEquals(CHOCOLATE_MUFFIN, model.getOrdersForClient(BOB).getFirst().getItem());
        assertEquals(new Quantity(8), model.getOrdersForClient(BOB).getFirst().getQuantity());
    }

    @Test
    public void execute_sortedAndSearchedOrders_updatesLiveViewWithoutChangingLocalIndices() throws Exception {
        logic.execute("list-complete-orders by-deadline");
        var view = logic.getOrderList();
        logic.execute("edit-order 1 o/2 d/2020-01-01");
        assertEquals(repeat.getId(), view.getFirst().getId());
        assertEquals(first.getId(), view.get(1).getId());
        assertEquals(repeat.getId(), model.getOrdersForClient(ALICE).get(1).getId());
        logic.execute("find-orders Banana");
        logic.execute("edit-order 1 o/2 i/2");
        assertEquals(List.of(first, bobOrder), view);
    }

    @Test
    public void execute_noOp_preservesPreviousUndoAndDoesNotCreateUndo() throws Exception {
        logic.execute("edit-order 1 o/1 q/2");
        assertFalse(logic.undoAvailableProperty().get());
        AddressBook before = new AddressBook(model.getAddressBook());
        logic.execute("edit-order 1 o/1 q/5");
        assertTrue(logic.execute("edit-order 1 o/1 q/5").getFeedbackToUser().contains("unchanged"));
        logic.execute("undo");
        assertEquals(before, model.getAddressBook());
    }

    @Test
    public void execute_invalidIndices_rejectsWithoutPartialMutationOrReplacingUndo() throws Exception {
        logic.execute("edit-order 1 o/1 q/5");
        AddressBook before = new AddressBook(model.getAddressBook());
        for (String args : List.of("3 o/1 q/2", "1 o/3 q/2", "1 o/1 i/3 q/2")) {
            assertThrows(CommandException.class, () -> logic.execute("edit-order " + args));
            assertEquals(before, model.getAddressBook());
        }
        model.updateFilteredBakeryItemList(item -> false);
        assertThrows(CommandException.class, () -> logic.execute("edit-order 1 o/1 i/1 q/2"));
        model.updateFilteredPersonList(person -> false);
        assertThrows(CommandException.class, () -> logic.execute("edit-order 1 o/1 q/2"));
        assertEquals(before, model.getAddressBook());
        logic.execute("undo");
        assertEquals(first, logic.getAllOrderList().getFirst());
    }

    @Test
    public void execute_saveFailures_restoreDataFiltersSortAndPreviousUndo() throws Exception {
        for (IOException failure : List.of(new IOException("Cannot save"), new AccessDeniedException("denied"))) {
            AtomicBoolean shouldFail = new AtomicBoolean();
            Logic transactional = createLogic(new JsonAddressBookStorage(directory.resolve("rollback.json")) {
                @Override
                public void saveAddressBook(ReadOnlyAddressBook book) throws IOException {
                    if (shouldFail.get()) {
                        throw failure;
                    }
                    super.saveAddressBook(book);
                }
            });
            AddressBook initial = new AddressBook(model.getAddressBook());
            transactional.execute("edit-order 1 o/1 q/5");
            model.updateFilteredPersonList(person -> person.equals(ALICE));
            model.updateFilteredBakeryItemList(item -> item.equals(CHOCOLATE_MUFFIN));
            transactional.execute("list-complete-orders by-deadline");
            AddressBook beforeFailure = new AddressBook(model.getAddressBook());
            List<ClientOrder> view = List.copyOf(transactional.getOrderList());
            shouldFail.set(true);
            assertThrows(CommandException.class, () ->
                    transactional.execute("edit-order 1 o/2 i/1 q/13 d/2020-01-01"));
            assertEquals(beforeFailure, model.getAddressBook());
            assertEquals(beforeFailure, new JsonAddressBookStorage(directory.resolve("rollback.json"))
                    .readAddressBook().orElseThrow());
            assertEquals(view, transactional.getOrderList());
            assertEquals(List.of(ALICE), transactional.getFilteredPersonList());
            assertEquals(List.of(CHOCOLATE_MUFFIN), transactional.getFilteredBakeryItemList());
            assertEquals(Model.ORDER_BY_DEADLINE, model.getOrderComparator());
            assertTrue(transactional.undoAvailableProperty().get());
            shouldFail.set(false);
            transactional.execute("undo");
            assertEquals(initial, model.getAddressBook());
        }
    }

    @Test
    public void parse_invalidArguments_rejectsWithoutMutation() {
        AddressBook before = new AddressBook(model.getAddressBook());
        for (String args : List.of("", "1", "o/1 q/2", "1 o/1", "0 o/1 q/2", "1 o/0 q/2",
                "2147483648 o/1 q/2", "1 o/2147483648 q/2", "1 o/1 i/2147483648", "1 o/1 i/0",
                "1 o/1 i/", "1 o/1 q/", "1 o/1 d/", "1 o/1 q/0", "1 o/1 q/10000", "1 o/1 q/1.5",
                "1 o/1 q/-1", "1 o/1 q/2147483648", "1 o/1 d/2026-02-29", "1 o/1 d/2026-2-01",
                "1 o/1 o/2 q/2", "1 o/1 i/1 i/2", "1 o/1 q/2 q/3", "1 o/1 d/2020-01-01 d/2020-01-01",
                "1 o/1 q/2 extra", "1 o/1 q/2 x/3", "1 2 o/1 q/2")) {
            assertThrows(ParseException.class, () -> logic.execute("edit-order " + args), args);
            assertEquals(before, model.getAddressBook());
        }
    }

    @Test
    public void parse_validBoundariesAndFieldCombinations_acceptsWhitespaceAndLeadingZeros() throws Exception {
        AddressBookParser parser = new AddressBookParser();
        for (String fields : List.of("i/1", "q/1", "q/9999", "d/2020-02-29", "i/1 q/2", "i/1 d/2020-01-01",
                "q/2 d/2020-01-01", "d/2020-01-01 q/2 i/1")) {
            assertTrue(parser.parseCommand("edit-order 2147483647 o/2147483647 " + fields)
                    instanceof EditClientOrderCommand);
        }
        assertEquals(parser.parseCommand("edit-order 1 o/2 q/1"),
                parser.parseCommand(" \tedit-order\t01\to/02\tq/0001 \t"));
    }

    @Test
    public void parsedCommands_equalValues_haveEqualHashCodes() throws Exception {
        AddressBookParser parser = new AddressBookParser();
        var command = parser.parseCommand("edit-order 1 o/2 i/1 q/2 d/2020-01-01");
        var same = parser.parseCommand("edit-order 01 o/02 d/2020-01-01 q/02 i/01");
        assertEquals(command, same);
        assertEquals(command.hashCode(), same.hashCode());
        assertFalse(command.equals(parser.parseCommand("edit-order 1 o/2 i/2 q/2 d/2020-01-01")));
        assertFalse(command.equals(null));
    }

    @Test
    public void setOrder_invalidReplacement_rejectsBeforeMutation() {
        AddressBook before = new AddressBook(model.getAddressBook());
        assertThrows(IllegalArgumentException.class, () -> model.setOrder(first, repeat));
        assertThrows(IllegalArgumentException.class, () -> model.setOrder(first, first.withClient(BOB)));
        assertThrows(IllegalArgumentException.class, () -> model.setOrder(first, first.withCompletion(false)));
        assertThrows(IllegalArgumentException.class, () -> model.setOrder(first.withCompletion(false), first));
        model.deleteBakeryItem(CHOCOLATE_MUFFIN);
        AddressBook withoutChocolate = new AddressBook(model.getAddressBook());
        assertThrows(IllegalArgumentException.class, () -> model.setOrder(first, first.withItem(CHOCOLATE_MUFFIN)));
        assertEquals(withoutChocolate, model.getAddressBook());
        model.addBakeryItem(CHOCOLATE_MUFFIN);
        assertEquals(before, model.getAddressBook());
    }
}
