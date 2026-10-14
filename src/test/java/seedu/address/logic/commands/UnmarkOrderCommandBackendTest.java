package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BOB;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

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
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

public class UnmarkOrderCommandBackendTest {
    @TempDir
    public Path directory;

    private ModelManager createModel() {
        ModelManager model = new ModelManager();
        model.addPerson(ALICE);
        model.addPerson(BOB);
        BakeryItem item = new BakeryItem(new BakeryItemName("Cake"), Price.fromDollars("3.50"));
        model.addBakeryItem(item);
        model.addOrder(ClientOrder.create(ALICE, item, new Quantity(2), new Deadline("2026-10-15")));
        ClientOrder target = ClientOrder.create(BOB, item, new Quantity(2), new Deadline("2026-10-15"));
        model.addOrder(target);
        model.setOrderCompletion(target, true);
        model.updateFilteredPersonList(person -> person.equals(BOB));
        return model;
    }

    private void execute(ModelManager model) throws Exception {
        new AddressBookParser().parseCommand("unmark 1 o/1").execute(model);
    }

    private LogicManager createLogic(ModelManager model, JsonAddressBookStorage storage) {
        return new LogicManager(model, new StorageManager(storage,
                new JsonUserPrefsStorage(directory.resolve("prefs.json"))));
    }

    @Test
    public void execute_filteredClient_changesOnlySelectedClientsOrder() throws Exception {
        ModelManager model = createModel();
        ClientOrder untouched = model.getOrdersForClient(ALICE).getFirst();
        execute(model);
        assertFalse(model.getOrdersForClient(BOB).getFirst().isComplete());
        assertEquals(untouched, model.getOrdersForClient(ALICE).getFirst());
        assertFalse(untouched.isComplete());
    }

    @Test
    public void execute_identicalOrders_preservesUnselectedIdentity() throws Exception {
        ModelManager model = createModel();
        ClientOrder target = model.getOrdersForClient(BOB).getFirst();
        ClientOrder repeat = ClientOrder.create(BOB, target.getItem(), target.getQuantity(), target.getDeadline());
        model.addOrder(repeat);
        execute(model);
        assertEquals(target.getId(), model.getOrdersForClient(BOB).getFirst().getId());
        assertEquals(repeat, model.getOrdersForClient(BOB).getLast());
    }
    @Test
    public void execute_invalidIndices_doesNotChangeData() throws Exception {
        ModelManager model = createModel();
        AddressBook before = new AddressBook(model.getAddressBook());
        assertThrows(CommandException.class, OrderCommandUtil.MESSAGE_INVALID_CLIENT, () ->
                new AddressBookParser().parseCommand("unmark 2 o/1").execute(model));
        assertThrows(CommandException.class, OrderCommandUtil.MESSAGE_INVALID_ORDER, () ->
                new AddressBookParser().parseCommand("unmark 1 o/2").execute(model));
        assertEquals(before, model.getAddressBook());
    }

    @Test
    public void execute_success_persistsChangesAcrossReload() throws Exception {
        ModelManager model = createModel();
        JsonAddressBookStorage storage = new JsonAddressBookStorage(directory.resolve("orders.json"));
        createLogic(model, storage).execute("unmark 1 o/1");
        assertFalse(model.getOrdersForClient(BOB).getFirst().isComplete());
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
    }

    @Test
    public void execute_failedSave_restoresDataAndFilter() throws Exception {
        ModelManager model = createModel();
        AddressBook before = new AddressBook(model.getAddressBook());
        JsonAddressBookStorage failing = new JsonAddressBookStorage(directory.resolve("orders.json")) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
                throw new IOException("Simulated save failure");
            }
        };
        assertThrows(CommandException.class, () -> createLogic(model, failing).execute("unmark 1 o/1"));
        assertEquals(before, model.getAddressBook());
        assertEquals(BOB, model.getFilteredPersonList().getFirst());
        assertEquals(1, model.getFilteredPersonList().size());
    }

    @Test
    public void execute_repeatedStatus_reportsErrorWithoutChangingData() throws Exception {
        ModelManager model = createModel();
        execute(model);
        AddressBook before = new AddressBook(model.getAddressBook());
        assertThrows(CommandException.class, () -> execute(model));
        assertEquals(before, model.getAddressBook());
    }

}
