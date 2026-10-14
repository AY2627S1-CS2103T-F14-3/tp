package seedu.address.model;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Objects;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.bakeryitem.BakeryItem;
import seedu.address.model.bakeryitem.UniqueBakeryItemList;
import seedu.address.model.order.ClientOrder;
import seedu.address.model.person.Person;
import seedu.address.model.person.UniquePersonList;

/**
 * Wraps all data at the address-book level.
 * Duplicates are not allowed (by .isSamePerson comparison).
 */
public class AddressBook implements ReadOnlyAddressBook {
    public static final String MESSAGE_CLIENT_HAS_ORDERS =
            "Cannot delete this client: existing orders reference this client. Delete those orders first.";
    public static final String MESSAGE_ITEM_HAS_ORDERS =
            "Cannot delete this item: existing orders reference this item. Delete those orders first.";

    private final UniquePersonList persons = new UniquePersonList();
    private final UniqueBakeryItemList bakeryItems = new UniqueBakeryItemList();
    private final ObservableList<ClientOrder> orders = FXCollections.observableArrayList();

    public AddressBook() {}

    /**
     * Creates an AddressBook using the Persons in the {@code toBeCopied}
     */
    public AddressBook(ReadOnlyAddressBook toBeCopied) {
        this();
        resetData(toBeCopied);
    }

    //// list overwrite operations

    /**
     * Replaces the contents of the person list with {@code persons}.
     * {@code persons} must not contain duplicate persons.
     */
    public void setPersons(List<Person> persons) {
        if (orders.stream().anyMatch(order -> !persons.contains(order.getClient()))) {
            throw new IllegalArgumentException("The replacement client list would leave an order without its client.");
        }
        this.persons.setPersons(persons);
    }

    /**
     * Resets the existing data of this {@code AddressBook} with {@code newData}.
     */
    public void resetData(ReadOnlyAddressBook newData) {
        requireNonNull(newData);

        // Validate a complete replacement before touching any existing observable list.
        AddressBook replacement = new AddressBook();
        replacement.setPersons(newData.getPersonList());
        newData.getBakeryItemList().forEach(replacement::addBakeryItem);
        newData.getOrderList().forEach(replacement::addOrder);
        persons.setPersons(replacement.getPersonList());
        bakeryItems.setBakeryItems(replacement.getBakeryItemList());
        orders.setAll(replacement.getOrderList());
    }

    /**
     * Replaces bakery items without leaving existing orders without their items.
     */
    public void setBakeryItems(List<BakeryItem> items) {
        requireNonNull(items);
        if (orders.stream().anyMatch(order -> !items.contains(order.getItem()))) {
            throw new IllegalArgumentException("The replacement item list would leave an order without its item.");
        }
        bakeryItems.setBakeryItems(items);
    }

    //// person-level operations

    /**
     * Returns true if a person with the same identity as {@code person} exists in the address book.
     */
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return persons.contains(person);
    }

    /**
     * Adds a person to the address book.
     * The person must not already exist in the address book.
     */
    public void addPerson(Person p) {
        persons.add(p);
    }

    /**
     * Replaces the given person {@code target} in the list with {@code editedPerson}.
     * {@code target} must exist in the address book.
     * The person identity of {@code editedPerson} must not be the same as another existing person in the address book.
     */
    public void setPerson(Person target, Person editedPerson) {
        requireNonNull(editedPerson);

        persons.setPerson(target, editedPerson);
        orders.replaceAll(order -> order.getClient().equals(target) ? order.withClient(editedPerson) : order);
    }

    /**
     * Removes {@code key} from this {@code AddressBook}.
     * {@code key} must exist in the address book.
     */
    public void removePerson(Person key) {
        if (orders.stream().anyMatch(order -> order.getClient().equals(key))) {
            throw new IllegalArgumentException(MESSAGE_CLIENT_HAS_ORDERS);
        }
        persons.remove(key);
    }

    /**
     * Returns whether an item with the same normalized name exists.
     */
    public boolean hasBakeryItem(BakeryItem item) {
        requireNonNull(item);
        return bakeryItems.contains(item);
    }

    /**
     * Adds a bakery item, rejecting duplicate normalized names.
     */
    public void addBakeryItem(BakeryItem item) {
        bakeryItems.add(item);
    }

    /**
     * Removes an existing bakery item only when no orders reference it.
     */
    public void removeBakeryItem(BakeryItem item) {
        requireNonNull(item);
        if (orders.stream().anyMatch(order -> order.getItem().equals(item))) {
            throw new IllegalArgumentException(MESSAGE_ITEM_HAS_ORDERS);
        }
        bakeryItems.remove(item);
    }

    /**
     * Adds an order with existing client and item references and a unique persistent identity.
     */
    public void addOrder(ClientOrder order) {
        requireNonNull(order);
        if (!getPersonList().contains(order.getClient()) || !getBakeryItemList().contains(order.getItem())) {
            throw new IllegalArgumentException("An order must reference an existing client and bakery item.");
        }
        if (orders.stream().anyMatch(existing -> existing.getId().equals(order.getId()))) {
            throw new IllegalArgumentException("Order identity already exists.");
        }
        orders.add(order);
    }

    /**
     * Removes only the specified order, preserving identical repeat orders.
     */
    public void removeOrder(ClientOrder order) {
        requireNonNull(order);
        if (!orders.remove(order)) {
            throw new IllegalArgumentException("Order not found.");
        }
    }

    /**
     * Replaces an existing order's completion status without changing its identity or associations.
     */
    public void setOrderCompletion(ClientOrder order, boolean isComplete) {
        requireNonNull(order);
        int position = orders.indexOf(order);
        if (position < 0) {
            throw new IllegalArgumentException("Order not found.");
        }
        orders.set(position, order.withCompletion(isComplete));
    }

    @Override
    public ObservableList<BakeryItem> getBakeryItemList() {
        return bakeryItems.asUnmodifiableObservableList();
    }

    @Override
    public ObservableList<ClientOrder> getOrderList() {
        return FXCollections.unmodifiableObservableList(orders);
    }

    //// util methods

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("persons", persons)
                .add("bakeryItems", bakeryItems)
                .toString();
    }

    @Override
    public ObservableList<Person> getPersonList() {
        return persons.asUnmodifiableObservableList();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddressBook otherAddressBook)) {
            return false;
        }

        return persons.equals(otherAddressBook.persons) && bakeryItems.equals(otherAddressBook.bakeryItems)
                && orders.equals(otherAddressBook.orders);
    }

    @Override
    public int hashCode() {
        return Objects.hash(persons, bakeryItems, orders);
    }
}
