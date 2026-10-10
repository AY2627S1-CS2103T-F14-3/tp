package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BOB;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.logic.LogicManager;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.model.AddressBook;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.bakeryitem.BakeryItem;
import seedu.address.model.bakeryitem.BakeryItemName;
import seedu.address.model.bakeryitem.Price;
import seedu.address.model.order.ClientOrder;
import seedu.address.model.order.Deadline;
import seedu.address.model.order.Quantity;
import seedu.address.model.person.Person;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;

public class OrderBackendTest {
    private static final BakeryItem MUFFIN = new BakeryItem(new BakeryItemName("Muffin"), Price.fromDollars("1.20"));
    private static final BakeryItem CAKE = new BakeryItem(new BakeryItemName("Cake"), Price.fromDollars("3.50"));

    @TempDir
    public Path directory;

    private ModelManager createModel() {
        ModelManager model = new ModelManager();
        model.addPerson(ALICE);
        model.addPerson(BOB);
        model.addBakeryItem(MUFFIN);
        model.addBakeryItem(CAKE);
        return model;
    }

    private void addOrder(ModelManager model, String arguments) throws Exception {
        new AddressBookParser().parseCommand("add-order " + arguments).execute(model);
    }

    private LogicManager createLogic(ModelManager model, JsonAddressBookStorage storage) {
        return new LogicManager(model, new StorageManager(storage,
                new JsonUserPrefsStorage(directory.resolve("prefs.json"))));
    }

    @Test
    public void execute_filteredIndices_addsOrderForDisplayedClientAndItem() throws Exception {
        ModelManager model = createModel();
        model.updateFilteredPersonList(person -> person.equals(BOB));
        model.updateFilteredBakeryItemList(item -> item.equals(CAKE));
        addOrder(model, "1 i/1 q/9999 d/1990-01-01");
        ClientOrder order = model.getAddressBook().getOrderList().getFirst();
        assertEquals(BOB, order.getClient());
        assertEquals(CAKE, order.getItem());
        assertEquals(new Quantity(9999), order.getQuantity());
        assertEquals(new Deadline("1990-01-01"), order.getDeadline());
        assertFalse(order.isComplete());
        assertTrue(model.getOrdersForClient(ALICE).isEmpty());
    }

    @Test
    public void execute_identicalOrders_keepsSeparateIdentities() throws Exception {
        ModelManager model = createModel();
        addOrder(model, "1 i/1 q/5 d/2026-10-15");
        addOrder(model, "1 i/1 q/5 d/2026-10-15");
        List<ClientOrder> orders = model.getAddressBook().getOrderList();
        assertEquals(2, orders.size());
        assertNotEquals(orders.get(0).getId(), orders.get(1).getId());
        model.deleteOrder(orders.getFirst());
        assertEquals(1, orders.size());
    }

    @Test
    public void execute_missingClientOrItem_doesNotModifyData() {
        ModelManager model = createModel();
        AddressBook before = new AddressBook(model.getAddressBook());
        assertThrows(CommandException.class, () -> addOrder(model, "3 i/1 q/5 d/2026-10-15"));
        assertThrows(CommandException.class, () -> addOrder(model, "1 i/3 q/5 d/2026-10-15"));
        assertEquals(before, model.getAddressBook());
    }

    @Test
    public void saveAndReload_preservesAllFieldsAndCompletion() throws Exception {
        ModelManager model = createModel();
        addOrder(model, "1 i/1 q/5 d/2026-10-15");
        ClientOrder order = model.getAddressBook().getOrderList().getFirst();
        model.setOrderCompletion(order, true);
        JsonAddressBookStorage storage = new JsonAddressBookStorage(directory.resolve("book.json"));
        storage.saveAddressBook(model.getAddressBook());
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
        model.setOrderCompletion(model.getAddressBook().getOrderList().getFirst(), false);
        storage.saveAddressBook(model.getAddressBook());
        assertFalse(storage.readAddressBook().orElseThrow().getOrderList().getFirst().isComplete());
    }

    @Test
    public void saveAndReload_reorderedCollections_preservesAssociations() throws Exception {
        ModelManager model = createModel();
        addOrder(model, "1 i/1 q/5 d/2026-10-15");
        AddressBook reordered = new AddressBook();
        reordered.addPerson(BOB);
        reordered.addPerson(ALICE);
        reordered.addBakeryItem(CAKE);
        reordered.addBakeryItem(MUFFIN);
        reordered.addOrder(model.getAddressBook().getOrderList().getFirst());
        JsonAddressBookStorage storage = new JsonAddressBookStorage(directory.resolve("book.json"));
        storage.saveAddressBook(reordered);
        ClientOrder loaded = storage.readAddressBook().orElseThrow().getOrderList().getFirst();
        assertEquals(ALICE, loaded.getClient());
        assertEquals(MUFFIN, loaded.getItem());
    }

    @Test
    public void editClient_preservesOrderIdentityAndReloadedAssociation() throws Exception {
        ModelManager model = createModel();
        addOrder(model, "1 i/1 q/5 d/2026-10-15");
        ClientOrder original = model.getAddressBook().getOrderList().getFirst();
        Person edited = new PersonBuilder(ALICE).withName("Alice Renamed").build();
        model.setPerson(ALICE, edited);
        assertEquals(original.getId(), model.getAddressBook().getOrderList().getFirst().getId());
        assertEquals(edited, model.getAddressBook().getOrderList().getFirst().getClient());
        JsonAddressBookStorage storage = new JsonAddressBookStorage(directory.resolve("book.json"));
        storage.saveAddressBook(model.getAddressBook());
        assertEquals(edited, storage.readAddressBook().orElseThrow().getOrderList().getFirst().getClient());
    }

    @Test
    public void deleteReferencedClientOrItem_rejectsUntilOrdersRemoved() throws Exception {
        ModelManager model = createModel();
        addOrder(model, "1 i/1 q/5 d/2026-10-15");
        AddressBook before = new AddressBook(model.getAddressBook());
        assertThrows(CommandException.class, AddressBook.MESSAGE_CLIENT_HAS_ORDERS, () ->
                new AddressBookParser().parseCommand("delete 1").execute(model));
        assertThrows(IllegalArgumentException.class, () -> model.deletePerson(ALICE));
        AddressBook book = new AddressBook(model.getAddressBook());
        assertThrows(IllegalArgumentException.class, () -> book.removeBakeryItem(MUFFIN));
        assertEquals(before, model.getAddressBook());
        model.deleteOrder(model.getAddressBook().getOrderList().getFirst());
        model.deletePerson(ALICE);
        book.removeOrder(book.getOrderList().getFirst());
        book.removeBakeryItem(MUFFIN);
    }

    @Test
    public void addOrder_invalidAssociationOrDuplicateIdentity_rejectsWithoutChanges() throws Exception {
        ModelManager model = createModel();
        addOrder(model, "1 i/1 q/5 d/2026-10-15");
        ClientOrder order = model.getAddressBook().getOrderList().getFirst();
        assertThrows(IllegalArgumentException.class, () -> model.addOrder(order));
        Person missing = new PersonBuilder(ALICE).withName("Missing").build();
        assertThrows(IllegalArgumentException.class, () -> model.addOrder(
                ClientOrder.create(missing, MUFFIN, new Quantity(1), new Deadline("2026-10-15"))));
        assertEquals(1, model.getAddressBook().getOrderList().size());
    }

    @Test
    public void execute_storageFailure_restoresDataAndDisplayedLists() throws Exception {
        ModelManager model = createModel();
        model.updateFilteredPersonList(person -> person.equals(BOB));
        model.updateFilteredBakeryItemList(item -> item.equals(CAKE));
        AddressBook before = new AddressBook(model.getAddressBook());
        JsonAddressBookStorage failing = new JsonAddressBookStorage(directory.resolve("book.json")) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook book) throws IOException {
                throw new IOException("Simulated failure");
            }
        };
        LogicManager logic = createLogic(model, failing);
        assertThrows(CommandException.class, () -> logic.execute("add-order 1 i/1 q/5 d/2026-10-15"));
        assertEquals(before, model.getAddressBook());
        assertEquals(List.of(BOB), model.getFilteredPersonList());
        assertEquals(List.of(CAKE), model.getFilteredBakeryItemList());
        assertTrue(model.getPersonFilter().test(BOB));
        assertTrue(model.getBakeryItemFilter().test(CAKE));
    }

    @Test
    public void execute_addItemAndOrder_savesThroughLogicManager() throws Exception {
        ModelManager model = new ModelManager();
        model.addPerson(ALICE);
        JsonAddressBookStorage storage = new JsonAddressBookStorage(directory.resolve("book.json"));
        LogicManager logic = createLogic(model, storage);
        logic.execute("add-item n/Muffin p/1.20");
        logic.execute("add-order 1 i/1 q/5 d/2026-10-15");
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
        assertThrows(CommandException.class, () -> logic.execute("add-item n/MUFFIN p/2"));
        assertEquals(1, model.getAddressBook().getBakeryItemList().size());
    }

    @Test
    public void readLegacyJson_loadsWithoutItemsOrOrders() throws Exception {
        Path path = directory.resolve("book.json");
        Files.writeString(path, "{\"persons\":[]}");
        assertEquals(new AddressBook(), new JsonAddressBookStorage(path).readAddressBook().orElseThrow());
    }

    @Test
    public void readInvalidOrderJson_reportsDataLoadingError() throws Exception {
        ModelManager model = createModel();
        addOrder(model, "1 i/1 q/5 d/2026-10-15");
        Path path = directory.resolve("book.json");
        JsonAddressBookStorage storage = new JsonAddressBookStorage(path);
        storage.saveAddressBook(model.getAddressBook());
        String json = Files.readString(path);
        for (String invalid : new String[] {
            json.replace("\"quantity\" : \"5\"", "\"quantity\" : \"0\""),
            json.replace("\"quantity\" : \"5\"", "\"quantity\" : 1.5"),
            json.replace("\"deadline\" : \"2026-10-15\"", "\"deadline\" : \"2026-02-30\""),
            json.replace("\"clientName\" : \"" + ALICE.getName() + "\"", "\"clientName\" : \"Missing\""),
            json.replace("\"itemName\" : \"Muffin\"", "\"itemName\" : \"Missing\""),
            json.replace(model.getAddressBook().getOrderList().getFirst().getId().toString(), "bad-id"),
            json.replace("\"complete\" : false", "\"complete\" : null")}) {
            assertNotEquals(json, invalid);
            Files.writeString(path, invalid);
            assertThrows(DataLoadingException.class, () -> storage.readAddressBook());
        }
    }

    @Test
    public void orderViews_areObservableAndUnmodifiable() throws Exception {
        ModelManager model = createModel();
        List<ClientOrder> view = model.getOrdersForClient(ALICE);
        addOrder(model, "1 i/1 q/5 d/2026-10-15");
        assertEquals(1, view.size());
        assertThrows(UnsupportedOperationException.class, () -> view.clear());
        assertThrows(UnsupportedOperationException.class, () -> model.getFilteredBakeryItemList().clear());
        model.setAddressBook(new AddressBook());
        assertTrue(view.isEmpty());
        assertTrue(model.getAddressBook().getBakeryItemList().isEmpty());
    }
}
