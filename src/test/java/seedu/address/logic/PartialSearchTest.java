package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.bakeryitem.BakeryItem;
import seedu.address.model.order.ClientOrder;
import seedu.address.model.person.Person;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

public class PartialSearchTest {
    @TempDir
    public Path directory;

    private Model model;
    private Logic logic;
    private JsonAddressBookStorage storage;
    private AddressBook beforeSearch;
    private List<Person> clients;
    private List<BakeryItem> items;
    private List<ClientOrder> orders;

    @BeforeEach
    public void setUp() throws Exception {
        model = new ModelManager();
        storage = new JsonAddressBookStorage(directory.resolve("book.json"));
        logic = new LogicManager(model, new StorageManager(storage,
                new JsonUserPrefsStorage(directory.resolve("prefs.json"))));
        logic.execute("add-client n/Anne-Marie O'Neil p/93456789 e/anne@example.com a/One Road");
        logic.execute("add-client n/Joanne Tan p/94567890 e/joanne@example.com a/Two Road");
        logic.execute("add-item n/Chocolate (Gluten Free) p/5");
        logic.execute("add-item n/Vanilla Cupcake p/4");
        logic.execute("add-item n/Coffee p/3");
        logic.execute("add-order 1 i/1 q/2 d/2026-12-01");
        logic.execute("add-order 2 i/2 q/1 d/2026-12-02");
        logic.execute("add-order 2 i/3 q/1 d/2026-12-03");
        beforeSearch = new AddressBook(model.getAddressBook());
        clients = List.copyOf(model.getAddressBook().getPersonList());
        items = List.copyOf(model.getAddressBook().getBakeryItemList());
        orders = List.copyOf(model.getAddressBook().getOrderList());
    }

    @Test
    public void findItems_fragmentsAndParentheses_matchLiteralNames() throws Exception {
        for (String keyword : List.of("Free", "FrEe)", "(GlUtEn", "olate")) {
            logic.execute("find-items " + keyword);
            assertEquals(List.of(items.get(0)), model.getFilteredBakeryItemList());
        }
        logic.execute("find-items missing CUP");
        assertEquals(List.of(items.get(1)), model.getFilteredBakeryItemList());
        logic.execute("find-items .");
        assertEquals(List.of(), model.getFilteredBakeryItemList());
        assertEquals(clients, model.getFilteredPersonList());
        assertEquals(orders, model.getFilteredOrderList());
        assertDataUnchanged();
    }

    @Test
    public void findClients_fragmentsAndPunctuation_matchAnyKeyword() throws Exception {
        logic.execute("find-clients aNn");
        assertEquals(clients, model.getFilteredPersonList());
        logic.execute("find-clients neil");
        assertEquals(List.of(clients.get(0)), model.getFilteredPersonList());
        logic.execute("find-clients missing tAn");
        assertEquals(List.of(clients.get(1)), model.getFilteredPersonList());
        logic.execute("find-clients @");
        assertEquals(List.of(), model.getFilteredPersonList());
        assertEquals(items, model.getFilteredBakeryItemList());
        assertEquals(orders, model.getFilteredOrderList());
        assertDataUnchanged();
    }

    @Test
    public void findOrders_fragmentsMatchClientOrItemNames() throws Exception {
        logic.execute("find-orders free");
        assertEquals(List.of(orders.get(0)), model.getFilteredOrderList());
        logic.execute("find-orders cup");
        assertEquals(List.of(orders.get(1)), model.getFilteredOrderList());
        logic.execute("find-orders ann");
        assertEquals(orders, model.getFilteredOrderList());
        logic.execute("find-orders missing fReE");
        assertEquals(List.of(orders.get(0)), model.getFilteredOrderList());
        logic.execute("find-orders 2026");
        assertEquals(List.of(), model.getFilteredOrderList());
        assertEquals(clients, model.getFilteredPersonList());
        assertEquals(items, model.getFilteredBakeryItemList());
        assertDataUnchanged();
    }

    private void assertDataUnchanged() throws Exception {
        assertEquals(beforeSearch, model.getAddressBook());
        assertEquals(beforeSearch, storage.readAddressBook().orElseThrow());
    }
}
