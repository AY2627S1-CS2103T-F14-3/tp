package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.testutil.TypicalBakeryItems.BANANA_MUFFIN;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BOB;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.collections.ObservableList;
import seedu.address.logic.commands.ListOrdersCommand;
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
import seedu.address.testutil.PersonBuilder;

/**
 * Tests the live order listing independently of the displayed clients and items.
 */
public class OrderListingTest {
    @TempDir
    public Path directory;

    private ModelManager model;
    private Logic logic;
    private ClientOrder aliceFirst;
    private ClientOrder aliceSecond;
    private ClientOrder bobFirst;

    @BeforeEach
    public void setUp() {
        model = new ModelManager();
        model.addPerson(ALICE);
        model.addPerson(BOB);
        model.addBakeryItem(BANANA_MUFFIN);
        bobFirst = createOrder(BOB);
        aliceFirst = createOrder(ALICE);
        aliceSecond = createOrder(ALICE);
        model.addOrder(bobFirst);
        model.addOrder(aliceFirst);
        model.addOrder(aliceSecond);
        logic = new LogicManager(model, new StorageManager(
                new JsonAddressBookStorage(directory.resolve("book.json")),
                new JsonUserPrefsStorage(directory.resolve("prefs.json"))));
    }

    private ClientOrder createOrder(Person client) {
        return ClientOrder.create(client, BANANA_MUFFIN, new Quantity(2), new Deadline("2026-10-15"));
    }

    @Test
    public void findOrders_keywords_matchWholeWordsAcrossBothNamesAndRestore() throws Exception {
        AddressBook before = new AddressBook(model.getAddressBook());
        assertEquals("2 orders listed!", logic.execute("find-orders ALICE").getFeedbackToUser());
        assertEquals(List.of(aliceFirst, aliceSecond), logic.getOrderList());
        logic.execute("find-orders banana Bob");
        assertEquals(List.of(bobFirst, aliceFirst, aliceSecond), logic.getOrderList());
        logic.execute("find-orders muff");
        assertEquals(List.of(), logic.getOrderList());
        assertThrows(ParseException.class, () -> logic.execute("find-orders \t"));
        assertEquals(before, model.getAddressBook());
        logic.execute("list-orders");
        assertEquals(List.of(bobFirst, aliceFirst, aliceSecond), logic.getOrderList());
    }

    @Test
    public void findOrders_hiddenEarlierOrder_preservesCommandIndicesAndIntegrity() throws Exception {
        model.addBakeryItem(seedu.address.testutil.TypicalBakeryItems.CHOCOLATE_MUFFIN);
        ClientOrder chocolateOrder = ClientOrder.create(ALICE,
                seedu.address.testutil.TypicalBakeryItems.CHOCOLATE_MUFFIN,
                new Quantity(1), new Deadline("2026-10-16"));
        model.addOrder(chocolateOrder);
        logic.execute("find-orders chocolate");
        assertEquals(List.of(chocolateOrder), logic.getOrderList());
        assertThrows(CommandException.class, () -> logic.execute("delete-client 2"));
        assertThrows(CommandException.class, () -> logic.execute("delete-item 1"));
        logic.execute("mark-order-complete 1 o/3");
        assertEquals(List.of(chocolateOrder.withCompletion(true)), logic.getOrderList());
        logic.execute("unmark 1 o/3");
        assertEquals(List.of(chocolateOrder), logic.getOrderList());
        logic.execute("delete-order 1 o/3");
        assertEquals(List.of(), logic.getOrderList());
        assertEquals(List.of(bobFirst, aliceFirst, aliceSecond), logic.getAllOrderList());
    }

    @Test
    public void findOrders_saveFailure_restoresPreviousFilterAndStoredOrders() throws Exception {
        logic.execute("find-orders Alice");
        Logic failingLogic = new LogicManager(model, new StorageManager(
                new JsonAddressBookStorage(directory.resolve("failing.json")) {
                    @Override
                    public void saveAddressBook(ReadOnlyAddressBook book) throws IOException {
                        throw new IOException("Cannot save");
                    }
                }, new JsonUserPrefsStorage(directory.resolve("prefs.json"))));
        assertThrows(CommandException.class, () -> failingLogic.execute("find-orders Bob"));
        assertEquals(List.of(aliceFirst, aliceSecond), logic.getOrderList());
        assertThrows(CommandException.class, () -> failingLogic.execute("delete-order 1 o/1"));
        assertEquals(List.of(aliceFirst, aliceSecond), logic.getOrderList());
        assertEquals(List.of(bobFirst, aliceFirst, aliceSecond), logic.getAllOrderList());
    }

    @Test
    public void execute_preservesOrderInsertionOrder() throws Exception {
        AddressBook before = new AddressBook(model.getAddressBook());
        assertEquals(ListOrdersCommand.MESSAGE_SUCCESS, logic.execute("list-orders").getFeedbackToUser());
        assertEquals(List.of(bobFirst, aliceFirst, aliceSecond), logic.getOrderList());
        assertEquals(before, model.getAddressBook());
    }

    @Test
    public void execute_filteredClientsAndItems_preservesBothFilters() throws Exception {
        model.updateFilteredPersonList(person -> person.equals(BOB));
        model.updateFilteredBakeryItemList(item -> false);
        logic.execute("list-orders");
        assertEquals(List.of(BOB), logic.getFilteredPersonList());
        assertEquals(List.of(bobFirst, aliceFirst, aliceSecond), logic.getOrderList());
        assertEquals(List.of(), logic.getFilteredBakeryItemList());
    }

    @Test
    public void execute_emptyBookAndExtraArguments_succeeds() throws Exception {
        model.setAddressBook(new AddressBook());
        assertEquals(ListOrdersCommand.MESSAGE_SUCCESS, logic.execute("list-orders extra").getFeedbackToUser());
        assertEquals(List.of(), logic.getOrderList());
    }

    @Test
    public void getOrderList_addDeleteAndCompletion_updateExistingView() {
        ObservableList<ClientOrder> view = logic.getOrderList();
        ClientOrder bobSecond = createOrder(BOB);
        model.addOrder(bobSecond);
        assertEquals(List.of(bobFirst, aliceFirst, aliceSecond, bobSecond), view);
        model.deleteOrder(aliceFirst);
        assertEquals(List.of(bobFirst, aliceSecond, bobSecond), view);
        model.setOrderCompletion(bobFirst, true);
        assertEquals(List.of(bobFirst.withCompletion(true), aliceSecond, bobSecond), view);
    }

    @Test
    public void getOrderList_editClient_updatesExistingView() {
        ObservableList<ClientOrder> view = logic.getOrderList();
        Person edited = new PersonBuilder(ALICE).withName("Alice Renamed").build();
        model.setPerson(ALICE, edited);
        assertEquals(List.of(bobFirst, aliceFirst.withClient(edited), aliceSecond.withClient(edited)), view);
    }

    @Test
    public void getOrderList_reorderClients_doesNotReorderOrders() {
        ObservableList<ClientOrder> view = logic.getOrderList();
        AddressBook reordered = new AddressBook(model.getAddressBook());
        reordered.setPersons(List.of(BOB, ALICE));
        model.setAddressBook(reordered);
        assertEquals(List.of(bobFirst, aliceFirst, aliceSecond), view);
        model.setAddressBook(new AddressBook());
        assertEquals(List.of(), view);
    }

    @Test
    public void getOrderList_filteredClients_doesNotHideOrders() {
        model.updateFilteredPersonList(person -> false);
        assertFalse(logic.getOrderList().isEmpty());
        assertEquals(List.of(bobFirst, aliceFirst, aliceSecond), logic.getOrderList());
    }

    @Test
    public void getOrderList_modification_rejected() {
        assertThrows(UnsupportedOperationException.class, () -> logic.getOrderList().clear());
    }

    @Test
    public void execute_nullModel_rejected() {
        assertThrows(NullPointerException.class, () -> new ListOrdersCommand().execute(null));
    }
}
