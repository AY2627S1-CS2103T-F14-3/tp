package seedu.address.model;

import java.util.function.Predicate;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.model.bakeryitem.BakeryItem;
import seedu.address.model.order.ClientOrder;
import seedu.address.model.person.Person;

/**
 * The API of the Model component.
 */
public interface Model {
    /** {@code Predicate} that always evaluates to true */
    Predicate<Person> PREDICATE_SHOW_ALL_PERSONS = unused -> true;

    /** {@code Predicate} that always evaluates to true for bakery items. */
    Predicate<BakeryItem> PREDICATE_SHOW_ALL_BAKERY_ITEMS = unused -> true;

    /**
     * Returns the user prefs.
     */
    ReadOnlyUserPrefs getUserPrefs();

    /**
     * Returns the user prefs' GUI settings.
     */
    GuiSettings getGuiSettings();

    /**
     * Sets the user prefs' GUI settings.
     */
    void setGuiSettings(GuiSettings guiSettings);

    /**
     * Replaces address book data with the data in {@code addressBook}.
     */
    void setAddressBook(ReadOnlyAddressBook addressBook);

    /** Returns the AddressBook */
    ReadOnlyAddressBook getAddressBook();

    /**
     * Returns true if a person with the same identity as {@code person} exists in the address book.
     */
    boolean hasPerson(Person person);

    /**
     * Deletes the given person.
     * The person must exist in the address book.
     */
    void deletePerson(Person target);

    /**
     * Deletes an existing bakery item that is not referenced by an order.
     */
    void deleteBakeryItem(BakeryItem target);

    /**
     * Adds the given person.
     * {@code person} must not already exist in the address book.
     */
    void addPerson(Person person);

    /**
     * Replaces the given person {@code target} with {@code editedPerson}.
     * {@code target} must exist in the address book.
     * The person identity of {@code editedPerson} must not be the same as another existing person in the address book.
     */
    void setPerson(Person target, Person editedPerson);

    /** Returns an unmodifiable view of the filtered person list */
    ObservableList<Person> getFilteredPersonList();

    /**
     * Updates the filter of the filtered person list to filter by the given {@code predicate}.
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredPersonList(Predicate<Person> predicate);

    /**
     * Returns the current client filter for restoring a failed transaction.
     */
    Predicate<Person> getPersonFilter();

    /**
     * Returns the current item filter for restoring a failed transaction.
     */
    Predicate<BakeryItem> getBakeryItemFilter();

    /**
     * Returns an unmodifiable view of the displayed bakery items.
     */
    ObservableList<BakeryItem> getFilteredBakeryItemList();

    /**
     * Updates the bakery item filter used for displayed item indices.
     */
    void updateFilteredBakeryItemList(Predicate<BakeryItem> predicate);

    /**
     * Returns whether an item with the same normalized name already exists.
     */
    boolean hasBakeryItem(BakeryItem item);

    /**
     * Adds a bakery item and shows all items.
     */
    void addBakeryItem(BakeryItem item);

    /**
     * Replaces the given bakery item with its edited version.
     */
    void setBakeryItem(BakeryItem target, BakeryItem editedBakeryItem);

    /**
     * Returns an unmodifiable observable list of the client's orders in insertion order.
     */
    ObservableList<ClientOrder> getOrdersForClient(Person client);

    /** Returns the displayed orders without changing stored order associations. */
    ObservableList<ClientOrder> getFilteredOrderList();

    /** Updates the displayed order filter. */
    void updateFilteredOrderList(Predicate<ClientOrder> predicate);

    /** Returns the order filter for restoring a failed transaction. */
    Predicate<ClientOrder> getOrderFilter();

    /**
     * Adds an order with existing client and item references.
     */
    void addOrder(ClientOrder order);

    /**
     * Deletes the specified existing order.
     */
    void deleteOrder(ClientOrder order);

    /**
     * Sets completion status for the specified existing order.
     */
    void setOrderCompletion(ClientOrder order, boolean isComplete);
}
