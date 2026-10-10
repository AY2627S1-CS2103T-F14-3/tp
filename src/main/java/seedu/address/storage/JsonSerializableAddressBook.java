package seedu.address.storage;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Person;

/**
 * Stores clients, bakery items, and orders in a human-editable JSON document.
 */
@JsonRootName(value = "addressbook")
class JsonSerializableAddressBook {
    public static final String MESSAGE_DUPLICATE_PERSON = "Persons list contains duplicate person(s).";

    private final List<JsonAdaptedPerson> persons = new ArrayList<>();
    private final List<JsonAdaptedBakeryItem> bakeryItems = new ArrayList<>();
    private final List<JsonAdaptedClientOrder> orders = new ArrayList<>();

    /**
     * Creates a serialized book, accepting legacy AB3 files that omit items and orders.
     */
    @JsonCreator
    public JsonSerializableAddressBook(@JsonProperty("persons") List<JsonAdaptedPerson> persons,
            @JsonProperty("bakeryItems") List<JsonAdaptedBakeryItem> bakeryItems,
            @JsonProperty("orders") List<JsonAdaptedClientOrder> orders) {
        if (persons == null) {
            throw new IllegalArgumentException("The persons collection is missing.");
        }
        this.persons.addAll(persons);
        if (bakeryItems != null) {
            this.bakeryItems.addAll(bakeryItems);
        }
        if (orders != null) {
            this.orders.addAll(orders);
        }
    }

    /**
     * Creates an immutable serialization snapshot of the current book.
     */
    public JsonSerializableAddressBook(ReadOnlyAddressBook source) {
        persons.addAll(source.getPersonList().stream().map(JsonAdaptedPerson::new).toList());
        bakeryItems.addAll(source.getBakeryItemList().stream().map(JsonAdaptedBakeryItem::new).toList());
        orders.addAll(source.getOrderList().stream().map(JsonAdaptedClientOrder::new).toList());
    }

    /**
     * Returns a completely validated model, rejecting missing references and duplicate identities.
     */
    public AddressBook toModelType() throws IllegalValueException {
        AddressBook book = new AddressBook();
        for (JsonAdaptedPerson entry : persons) {
            if (entry == null) {
                throw new IllegalValueException("The persons collection contains a null entry.");
            }
            Person person = entry.toModelType();
            if (book.hasPerson(person)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_PERSON);
            }
            book.addPerson(person);
        }
        try {
            for (JsonAdaptedBakeryItem entry : bakeryItems) {
                if (entry == null) {
                    throw new IllegalValueException("The bakery items collection contains a null entry.");
                }
                book.addBakeryItem(entry.toModelType());
            }
            for (JsonAdaptedClientOrder entry : orders) {
                if (entry == null) {
                    throw new IllegalValueException("The orders collection contains a null entry.");
                }
                book.addOrder(entry.toModelType(book));
            }
        } catch (IllegalArgumentException exception) {
            throw new IllegalValueException(exception.getMessage());
        }
        return book;
    }
}
