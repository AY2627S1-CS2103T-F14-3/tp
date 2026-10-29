package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Comparator;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.bakeryitem.BakeryItem;
import seedu.address.model.order.ClientOrder;
import seedu.address.model.person.Person;

/**
 * Represents the in-memory model of the address book data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final AddressBook addressBook;
    private final UserPrefs userPrefs;
    private final FilteredList<Person> filteredPersons;
    private final FilteredList<BakeryItem> filteredBakeryItems;
    private final FilteredList<ClientOrder> filteredOrders;
    private final SortedList<ClientOrder> sortedOrders;
    private Comparator<? super ClientOrder> orderComparator;

    /**
     * Initializes a ModelManager with the given addressBook and userPrefs.
     */
    public ModelManager(ReadOnlyAddressBook addressBook, ReadOnlyUserPrefs userPrefs) {
        requireAllNonNull(addressBook, userPrefs);

        logger.fine("Initializing with address book: " + addressBook + " and user prefs " + userPrefs);

        this.addressBook = new AddressBook(addressBook);
        this.userPrefs = new UserPrefs(userPrefs);
        filteredPersons = new FilteredList<>(this.addressBook.getPersonList(), PREDICATE_SHOW_ALL_PERSONS);
        filteredBakeryItems = new FilteredList<>(this.addressBook.getBakeryItemList(), PREDICATE_SHOW_ALL_BAKERY_ITEMS);
        filteredOrders = new FilteredList<>(this.addressBook.getOrderList(), order -> true);
        sortedOrders = new SortedList<>(filteredOrders);
    }

    public ModelManager() {
        this(new AddressBook(), new UserPrefs());
    }

    //=========== UserPrefs ==================================================================================

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return userPrefs;
    }

    @Override
    public GuiSettings getGuiSettings() {
        return userPrefs.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        userPrefs.setGuiSettings(guiSettings);
    }

    //=========== AddressBook ================================================================================

    @Override
    public void setAddressBook(ReadOnlyAddressBook addressBook) {
        this.addressBook.resetData(addressBook);
    }

    @Override
    public ReadOnlyAddressBook getAddressBook() {
        return addressBook;
    }

    @Override
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return addressBook.hasPerson(person);
    }

    @Override
    public void deletePerson(Person target) {
        addressBook.removePerson(target);
    }

    @Override
    public void addPerson(Person person) {
        addressBook.addPerson(person);
        updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
    }

    @Override
    public void setPerson(Person target, Person editedPerson) {
        requireAllNonNull(target, editedPerson);

        addressBook.setPerson(target, editedPerson);
    }

    // @@author Yik-Wee
    @Override
    public void deleteBakeryItem(BakeryItem target) {
        addressBook.removeBakeryItem(target);
        logger.fine("Deleted bakery item: " + target);
    }
    // @@author

    //=========== Filtered Person List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Person} backed by the internal list of
     * {@code addressBook}
     */
    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return filteredPersons;
    }

    @Override
    public void updateFilteredPersonList(Predicate<Person> predicate) {
        requireNonNull(predicate);
        filteredPersons.setPredicate(predicate);
    }

    @Override
    public Predicate<Person> getPersonFilter() {
        return filteredPersons.getPredicate()::test;
    }

    @Override
    public Predicate<BakeryItem> getBakeryItemFilter() {
        return filteredBakeryItems.getPredicate()::test;
    }

    @Override
    public ObservableList<BakeryItem> getFilteredBakeryItemList() {
        return FXCollections.unmodifiableObservableList(filteredBakeryItems);
    }

    @Override
    public void updateFilteredBakeryItemList(Predicate<BakeryItem> predicate) {
        filteredBakeryItems.setPredicate(requireNonNull(predicate));
    }

    @Override
    public boolean hasBakeryItem(BakeryItem item) {
        return addressBook.hasBakeryItem(item);
    }

    @Override
    public void addBakeryItem(BakeryItem item) {
        addressBook.addBakeryItem(item);
        updateFilteredBakeryItemList(PREDICATE_SHOW_ALL_BAKERY_ITEMS);
        logger.fine("Added bakery item: " + item);
    }

    @Override
    public void setBakeryItem(BakeryItem target, BakeryItem editedBakeryItem) {
        requireAllNonNull(target, editedBakeryItem);

        addressBook.setBakeryItem(target, editedBakeryItem);
        logger.fine("Edited bakery item from " + target + " to " + editedBakeryItem);
    }

    @Override
    public ObservableList<ClientOrder> getOrdersForClient(Person client) {
        requireNonNull(client);
        return FXCollections.unmodifiableObservableList(addressBook.getOrderList()
                .filtered(order -> order.getClient().equals(client)));
    }

    @Override
    public void addOrder(ClientOrder order) {
        addressBook.addOrder(order);
    }

    @Override
    public ObservableList<ClientOrder> getFilteredOrderList() {
        return FXCollections.unmodifiableObservableList(sortedOrders);
    }

    @Override
    public void updateFilteredOrderList(Predicate<ClientOrder> predicate) {
        updateFilteredOrderList(predicate, null);
    }

    @Override
    public void updateFilteredOrderList(Predicate<ClientOrder> predicate, Comparator<? super ClientOrder> comparator) {
        filteredOrders.setPredicate(requireNonNull(predicate));
        orderComparator = comparator;
        if (comparator == null) {
            sortedOrders.setComparator(null);
        } else {
            Comparator<ClientOrder> primaryComparator = (first, second) -> comparator.compare(first, second);
            sortedOrders.setComparator(primaryComparator.thenComparingInt(addressBook.getOrderList()::indexOf));
        }
    }

    @Override
    public Comparator<? super ClientOrder> getOrderComparator() {
        return orderComparator;
    }

    @Override
    public Predicate<ClientOrder> getOrderFilter() {
        return filteredOrders.getPredicate()::test;
    }

    @Override
    public void deleteOrder(ClientOrder order) {
        addressBook.removeOrder(order);
    }

    @Override
    public void setOrderCompletion(ClientOrder order, boolean isComplete) {
        addressBook.setOrderCompletion(order, isComplete);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ModelManager otherModelManager)) {
            return false;
        }

        return addressBook.equals(otherModelManager.addressBook)
                && userPrefs.equals(otherModelManager.userPrefs)
                && filteredPersons.equals(otherModelManager.filteredPersons)
                && filteredBakeryItems.equals(otherModelManager.filteredBakeryItems);
    }

}
