package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalBakeryItems.BANANA_MUFFIN;
import static seedu.address.testutil.TypicalPersons.ALICE;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.LogicManager;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.order.ClientOrder;
import seedu.address.model.order.Deadline;
import seedu.address.model.order.Quantity;
import seedu.address.model.util.SampleDataUtil;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

/**
 * Tests replacing existing data, resetting filters, and persisting the sample book safely.
 */
public class PopulateCommandTest {
    @TempDir
    public Path directory;

    @Test
    public void execute_emptyBook_loadsSampleData() {
        ModelManager model = new ModelManager();
        assertEquals(new CommandResult(PopulateCommand.MESSAGE_SUCCESS), new PopulateCommand().execute(model));
        assertSampleData(model.getAddressBook());
    }

    @Test
    public void execute_existingData_replacesAllCollectionsAndUpdatesViews() {
        ModelManager model = existingModel();
        var clients = model.getFilteredPersonList();
        var items = model.getFilteredBakeryItemList();
        var orders = model.getAddressBook().getOrderList();
        new PopulateCommand().execute(model);
        assertSampleData(model.getAddressBook());
        assertEquals(model.getAddressBook().getPersonList(), clients);
        assertEquals(model.getAddressBook().getBakeryItemList(), items);
        assertEquals(model.getAddressBook().getOrderList(), orders);
    }

    @Test
    public void execute_filteredLists_showsAllSampleEntries() {
        ModelManager model = existingModel();
        model.updateFilteredPersonList(person -> false);
        model.updateFilteredBakeryItemList(item -> false);
        new PopulateCommand().execute(model);
        assertEquals(model.getAddressBook().getPersonList(), model.getFilteredPersonList());
        assertEquals(model.getAddressBook().getBakeryItemList(), model.getFilteredBakeryItemList());
    }

    @Test
    public void execute_repeatedPopulation_replacesInsteadOfAppending() {
        ModelManager model = existingModel();
        new PopulateCommand().execute(model);
        List<ClientOrder> previousOrders = List.copyOf(model.getAddressBook().getOrderList());
        new PopulateCommand().execute(model);
        assertSampleData(model.getAddressBook());
        assertTrue(model.getAddressBook().getOrderList().stream()
                .noneMatch(order -> previousOrders.stream().anyMatch(old -> old.getId().equals(order.getId()))));
    }

    @Test
    public void execute_throughLogic_savesReloadableSampleData() throws Exception {
        ModelManager model = existingModel();
        JsonAddressBookStorage storage = new JsonAddressBookStorage(directory.resolve("book.json"));
        assertEquals(new CommandResult(PopulateCommand.MESSAGE_SUCCESS), logic(model, storage).execute("populate"));
        assertSampleData(model.getAddressBook());
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
    }

    @Test
    public void execute_failedSave_restoresPreviousDataAndFilters() {
        ModelManager model = existingModel();
        model.updateFilteredPersonList(person -> false);
        model.updateFilteredBakeryItemList(item -> false);
        AddressBook before = new AddressBook(model.getAddressBook());
        JsonAddressBookStorage storage = new JsonAddressBookStorage(directory.resolve("book.json")) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook book) throws IOException {
                throw new IOException("Simulated save failure");
            }
        };
        assertThrows(CommandException.class, () -> logic(model, storage).execute("populate"));
        assertEquals(before, model.getAddressBook());
        assertTrue(model.getFilteredPersonList().isEmpty());
        assertTrue(model.getFilteredBakeryItemList().isEmpty());
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new PopulateCommand().execute(null));
    }

    private ModelManager existingModel() {
        ModelManager model = new ModelManager();
        model.addPerson(ALICE);
        model.addBakeryItem(BANANA_MUFFIN);
        model.addOrder(ClientOrder.create(ALICE, BANANA_MUFFIN, new Quantity(1), new Deadline("2026-10-15")));
        return model;
    }

    private LogicManager logic(ModelManager model, JsonAddressBookStorage storage) {
        return new LogicManager(model, new StorageManager(storage,
                new JsonUserPrefsStorage(directory.resolve("prefs.json"))));
    }

    private void assertSampleData(ReadOnlyAddressBook actual) {
        ReadOnlyAddressBook expected = SampleDataUtil.getSampleAddressBook();
        assertEquals(expected.getPersonList(), actual.getPersonList());
        assertEquals(expected.getBakeryItemList(), actual.getBakeryItemList());
        assertEquals(expected.getOrderList().stream().map(ClientOrder::toString).toList(),
                actual.getOrderList().stream().map(ClientOrder::toString).toList());
        assertEquals(actual.getOrderList().size(), actual.getOrderList().stream().map(ClientOrder::getId)
                .distinct().count());
    }
}
