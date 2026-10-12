package seedu.address.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.bakeryitem.BakeryItem;
import seedu.address.model.person.Person;

/**
 * An Immutable AddressBook that is serializable to JSON format.
 */
@JsonRootName(value = "addressbook")
class JsonSerializableAddressBook {

    public static final String MESSAGE_DUPLICATE_PERSON = "Persons list contains duplicate person(s).";
    public static final String MESSAGE_DUPLICATE_BAKERY_ITEM = "Bakery items list contains duplicate bakery item(s).";

    private final List<JsonAdaptedPerson> persons = new ArrayList<>();
    private final List<JsonAdaptedBakeryItem> bakeryItems = new ArrayList<>();

    /**
     * Constructs a {@code JsonSerializableAddressBook} with the given persons and bakery items.
     */
    @JsonCreator
    public JsonSerializableAddressBook(@JsonProperty("persons") List<JsonAdaptedPerson> persons,
            @JsonProperty("bakeryItems") List<JsonAdaptedBakeryItem> bakeryItems) {
        this.persons.addAll(persons);
        if (bakeryItems != null) {
            this.bakeryItems.addAll(bakeryItems);
        }
    }

    /**
     * Converts a given {@code ReadOnlyAddressBook} into this class for Jackson use.
     *
     * @param source future changes to this will not affect the created {@code JsonSerializableAddressBook}.
     */
    public JsonSerializableAddressBook(ReadOnlyAddressBook source) {
        persons.addAll(source.getPersonList().stream().map(JsonAdaptedPerson::new).collect(Collectors.toList()));
        bakeryItems.addAll(source.getBakeryItemList().stream()
                .map(JsonAdaptedBakeryItem::new)
                .collect(Collectors.toList()));
    }

    /**
     * Converts this address book into the model's {@code AddressBook} object.
     *
     * @throws IllegalValueException if there were any data constraints violated.
     */
    public AddressBook toModelType() throws IllegalValueException {
        AddressBook addressBook = new AddressBook();
        for (JsonAdaptedPerson jsonAdaptedPerson : persons) {
            Person person = jsonAdaptedPerson.toModelType();
            if (addressBook.hasPerson(person)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_PERSON);
            }
            addressBook.addPerson(person);
        }

        for (JsonAdaptedBakeryItem jsonAdaptedBakeryItem : bakeryItems) {
            BakeryItem bakeryItem = jsonAdaptedBakeryItem.toModelType();
            if (addressBook.hasBakeryItem(bakeryItem)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_BAKERY_ITEM);
            }
            addressBook.addBakeryItem(bakeryItem);
        }
        return addressBook;
    }

}
