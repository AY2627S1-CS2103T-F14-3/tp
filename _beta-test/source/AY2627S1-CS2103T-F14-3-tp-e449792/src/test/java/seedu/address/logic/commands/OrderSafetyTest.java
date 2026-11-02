package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalBakeryItems.BANANA_MUFFIN;
import static seedu.address.testutil.TypicalBakeryItems.CHOCOLATE_MUFFIN;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BOB;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.commons.core.index.Index;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.logic.LogicManager;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.ModelManager;
import seedu.address.model.order.ClientOrder;
import seedu.address.model.order.Deadline;
import seedu.address.model.order.Quantity;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

public class OrderSafetyTest {
    @TempDir
    public Path directory;

    private ModelManager createModel() {
        ModelManager model = new ModelManager();
        model.addPerson(ALICE);
        model.addPerson(BOB);
        model.addBakeryItem(BANANA_MUFFIN);
        model.addBakeryItem(CHOCOLATE_MUFFIN);
        model.addOrder(ClientOrder.create(ALICE, BANANA_MUFFIN, new Quantity(2), new Deadline("2026-10-15")));
        model.addOrder(ClientOrder.create(BOB, CHOCOLATE_MUFFIN, new Quantity(3), new Deadline("2026-10-16")));
        return model;
    }

    @Test
    public void findOrder_filteredClient_resolvesClientLocalIndexWithoutMutation() throws Exception {
        ModelManager model = createModel();
        model.updateFilteredPersonList(person -> person.equals(BOB));
        AddressBook before = new AddressBook(model.getAddressBook());
        ClientOrder selected = OrderCommandUtil.findOrder(model, Index.fromOneBased(1), Index.fromOneBased(1));
        assertEquals(model.getOrdersForClient(BOB).getFirst(), selected);
        assertEquals(before, model.getAddressBook());
    }

    @Test
    public void findOrder_invalidClient_reportsSpecificErrorWithoutMutation() {
        ModelManager model = createModel();
        AddressBook before = new AddressBook(model.getAddressBook());
        assertThrows(CommandException.class, OrderCommandUtil.MESSAGE_INVALID_CLIENT, () ->
                OrderCommandUtil.findOrder(model, Index.fromOneBased(3), Index.fromOneBased(1)));
        model.updateFilteredPersonList(person -> false);
        assertThrows(CommandException.class, OrderCommandUtil.MESSAGE_INVALID_CLIENT, () ->
                OrderCommandUtil.findOrder(model, Index.fromOneBased(1), Index.fromOneBased(1)));
        assertEquals(before, model.getAddressBook());
    }

    @Test
    public void findOrder_invalidOrEmptyClientOrderList_reportsSpecificError() {
        ModelManager model = createModel();
        assertThrows(CommandException.class, OrderCommandUtil.MESSAGE_INVALID_ORDER, () ->
                OrderCommandUtil.findOrder(model, Index.fromOneBased(1), Index.fromOneBased(2)));
        model.deleteOrder(model.getOrdersForClient(ALICE).getFirst());
        assertThrows(CommandException.class, OrderCommandUtil.MESSAGE_INVALID_ORDER, () ->
                OrderCommandUtil.findOrder(model, Index.fromOneBased(1), Index.fromOneBased(1)));
        assertEquals(1, model.getOrdersForClient(BOB).size());
    }

    @Test
    public void findOrder_nullArguments_rejectsBeforeAccessingModel() {
        ModelManager model = createModel();
        Index first = Index.fromOneBased(1);
        assertThrows(NullPointerException.class, () -> OrderCommandUtil.findOrder(null, first, first));
        assertThrows(NullPointerException.class, () -> OrderCommandUtil.findOrder(model, null, first));
        assertThrows(NullPointerException.class, () -> OrderCommandUtil.findOrder(model, first, null));
    }

    @Test
    public void replaceLists_danglingReferences_rejectsWithoutChangingBook() {
        AddressBook book = new AddressBook(createModel().getAddressBook());
        AddressBook before = new AddressBook(book);
        assertThrows(IllegalArgumentException.class, () -> book.setPersons(List.of(BOB)));
        assertThrows(IllegalArgumentException.class, () -> book.setBakeryItems(List.of(CHOCOLATE_MUFFIN)));
        assertEquals(before, book);
    }

    @Test
    public void replaceBook_duplicateOrderIdentity_validatesBeforeChangingExistingData() {
        ModelManager model = createModel();
        AddressBook before = new AddressBook(model.getAddressBook());
        ClientOrder duplicate = model.getOrdersForClient(ALICE).getFirst();
        AddressBook invalid = new AddressBook(before) {
            @Override
            public ObservableList<ClientOrder> getOrderList() {
                return FXCollections.observableArrayList(duplicate, duplicate);
            }
        };
        assertThrows(IllegalArgumentException.class, () -> model.setAddressBook(invalid));
        assertEquals(before, model.getAddressBook());
    }

    @Test
    public void changeMissingOrder_rejectsWithoutModifyingOtherOrders() {
        ModelManager model = createModel();
        ClientOrder missing = ClientOrder.create(ALICE, BANANA_MUFFIN, new Quantity(2), new Deadline("2026-10-15"));
        AddressBook before = new AddressBook(model.getAddressBook());
        assertThrows(IllegalArgumentException.class, () -> model.deleteOrder(missing));
        assertThrows(IllegalArgumentException.class, () -> model.setOrderCompletion(missing, true));
        assertEquals(before, model.getAddressBook());
    }

    @Test
    public void addOrder_missingItem_rejectsWithoutMutation() {
        ModelManager model = createModel();
        ModelManager empty = new ModelManager();
        empty.addPerson(ALICE);
        AddressBook before = new AddressBook(empty.getAddressBook());
        assertThrows(IllegalArgumentException.class, () -> empty.addOrder(model.getOrdersForClient(ALICE).getFirst()));
        assertEquals(before, empty.getAddressBook());
    }

    @Test
    public void logicViews_liveAndUnmodifiable_followModelUpdates() {
        ModelManager model = createModel();
        LogicManager logic = new LogicManager(model, new StorageManager(
                new JsonAddressBookStorage(directory.resolve("book.json")),
                new JsonUserPrefsStorage(directory.resolve("prefs.json"))));
        var items = logic.getFilteredBakeryItemList();
        var orders = logic.getOrderList();
        assertThrows(UnsupportedOperationException.class, items::clear);
        assertThrows(UnsupportedOperationException.class, orders::clear);
        model.deleteOrder(model.getOrdersForClient(ALICE).getFirst());
        assertEquals(1, orders.size());
        model.deleteBakeryItem(BANANA_MUFFIN);
        assertEquals(List.of(CHOCOLATE_MUFFIN), items);
        assertEquals(model.getFilteredPersonList(), logic.getFilteredPersonList());
    }

    @Test
    public void immutableOrder_copiesRetainIdentityAndValueEquality() {
        ClientOrder order = createModel().getOrdersForClient(ALICE).getFirst();
        ClientOrder copy = new ClientOrder(order.getId(), order.getClient(), order.getItem(),
                order.getQuantity(), order.getDeadline(), false);
        assertEquals(order, copy);
        assertEquals(order.hashCode(), copy.hashCode());
        ClientOrder completed = order.withCompletion(true);
        assertEquals(order.getId(), completed.getId());
        assertTrue(completed.isComplete());
        assertFalse(order.isComplete());
        assertFalse(order.equals(completed));
        assertFalse(order.equals(null));
        assertFalse(order.equals("order"));
        assertEquals(order.getId(), order.withClient(BOB).getId());
        assertFalse(order.equals(order.withClient(BOB)));
        assertFalse(order.equals(ClientOrder.create(ALICE, BANANA_MUFFIN, order.getQuantity(), order.getDeadline())));
    }

    @Test
    public void loadJson_missingCollectionsAndNullEntries_reportsDataLoadingError() throws Exception {
        Path path = directory.resolve("invalid.json");
        JsonAddressBookStorage storage = new JsonAddressBookStorage(path);
        for (String json : List.of("{}", "{\"persons\":[null]}",
                "{\"persons\":[],\"bakeryItems\":[null]}", "{\"persons\":[],\"orders\":[null]}")) {
            Files.writeString(path, json);
            assertThrows(DataLoadingException.class, storage::readAddressBook);
        }
    }

    @Test
    public void loadJson_missingOrderFieldsAndNoncanonicalUuid_reportsDataLoadingError() throws Exception {
        Path path = directory.resolve("invalid.json");
        JsonAddressBookStorage storage = new JsonAddressBookStorage(path);
        storage.saveAddressBook(createModel().getAddressBook());
        ObjectNode valid = JsonUtil.fromJsonString(Files.readString(path), ObjectNode.class);
        for (String field : List.of("id", "clientName", "itemName", "quantity", "deadline", "complete")) {
            ObjectNode invalid = valid.deepCopy();
            ObjectNode firstOrder = (ObjectNode) invalid.get("orders").get(0);
            firstOrder.remove(field);
            Files.writeString(path, invalid.toString());
            assertThrows(DataLoadingException.class, storage::readAddressBook);
        }
        ObjectNode invalid = valid.deepCopy();
        ObjectNode firstOrder = (ObjectNode) invalid.get("orders").get(0);
        firstOrder.put("id", "1-1-1-1-1");
        Files.writeString(path, invalid.toString());
        assertThrows(DataLoadingException.class, storage::readAddressBook);
    }

    @Test
    public void loadJson_duplicateOrderIdentity_reportsDataLoadingError() throws Exception {
        Path path = directory.resolve("invalid.json");
        JsonAddressBookStorage storage = new JsonAddressBookStorage(path);
        ModelManager model = createModel();
        storage.saveAddressBook(model.getAddressBook());
        ObjectNode invalid = JsonUtil.fromJsonString(Files.readString(path), ObjectNode.class);
        ArrayNode orders = (ArrayNode) invalid.get("orders");
        orders.add(orders.get(0).deepCopy());
        Files.writeString(path, invalid.toString());
        assertThrows(DataLoadingException.class, storage::readAddressBook);
        assertEquals(2, model.getAddressBook().getOrderList().size());
    }
}
