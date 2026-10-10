package seedu.address.model;

import javafx.collections.ObservableList;
import seedu.address.model.bakeryitem.BakeryItem;
import seedu.address.model.order.ClientOrder;
import seedu.address.model.person.Person;

/**
 * Unmodifiable view of an address book
 */
public interface ReadOnlyAddressBook {

    /**
     * Returns an unmodifiable view of the persons list.
     * This list will not contain any duplicate persons.
     */
    ObservableList<Person> getPersonList();

    /**
     * Returns an unmodifiable observable list of bakery items.
     */
    ObservableList<BakeryItem> getBakeryItemList();

    /**
     * Returns an unmodifiable observable list of all orders, including completed orders.
     */
    ObservableList<ClientOrder> getOrderList();

}
