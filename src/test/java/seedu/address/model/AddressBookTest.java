package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalBakeryItems.BANANA_MUFFIN;
import static seedu.address.testutil.TypicalBakeryItems.BANANA_MUFFIN_WITH_DIFFERENT_PRICE;
import static seedu.address.testutil.TypicalBakeryItems.CHOCOLATE_MUFFIN;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.bakeryitem.BakeryItem;
import seedu.address.model.bakeryitem.exceptions.DuplicateBakeryItemException;
import seedu.address.model.person.Person;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.testutil.PersonBuilder;

public class AddressBookTest {

    private final AddressBook addressBook = new AddressBook();

    @Test
    public void constructor() {
        assertEquals(List.of(), addressBook.getPersonList());
        assertEquals(List.of(), addressBook.getBakeryItemList());
    }

    @Test
    public void resetData_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> addressBook.resetData(null));
    }

    @Test
    public void resetData_withValidReadOnlyAddressBook_replacesData() {
        AddressBook newData = getTypicalAddressBook();
        newData.setBakeryItems(List.of(BANANA_MUFFIN));
        addressBook.resetData(newData);
        assertEquals(newData, addressBook);
    }

    @Test
    public void resetData_withDuplicatePersons_throwsDuplicatePersonException() {
        // Two persons with the same identity fields
        Person editedAlice = new PersonBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).withTags(VALID_TAG_HUSBAND)
                .build();
        List<Person> newPersons = List.of(ALICE, editedAlice);
        AddressBookStub newData = new AddressBookStub(newPersons);

        assertThrows(DuplicatePersonException.class, () -> addressBook.resetData(newData));
    }

    @Test
    public void resetData_withDuplicateBakeryItems_throwsDuplicateBakeryItemException() {
        List<BakeryItem> duplicateBakeryItems = List.of(BANANA_MUFFIN, BANANA_MUFFIN_WITH_DIFFERENT_PRICE);
        AddressBookStub newData = new AddressBookStub(List.of(), duplicateBakeryItems);

        assertThrows(DuplicateBakeryItemException.class, () -> addressBook.resetData(newData));
    }

    @Test
    public void hasPerson_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> addressBook.hasPerson(null));
    }

    @Test
    public void hasPerson_personNotInAddressBook_returnsFalse() {
        assertFalse(addressBook.hasPerson(ALICE));
    }

    @Test
    public void hasPerson_personInAddressBook_returnsTrue() {
        addressBook.addPerson(ALICE);
        assertTrue(addressBook.hasPerson(ALICE));
    }

    @Test
    public void hasPerson_personWithSameIdentityFieldsInAddressBook_returnsTrue() {
        addressBook.addPerson(ALICE);
        Person editedAlice = new PersonBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).withTags(VALID_TAG_HUSBAND)
                .build();
        assertTrue(addressBook.hasPerson(editedAlice));
    }

    @Test
    public void hasBakeryItem_nullBakeryItem_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> addressBook.hasBakeryItem(null));
    }

    @Test
    public void hasBakeryItem_bakeryItemNotInAddressBook_returnsFalse() {
        assertFalse(addressBook.hasBakeryItem(BANANA_MUFFIN));
    }

    @Test
    public void hasBakeryItem_bakeryItemInAddressBook_returnsTrue() {
        addressBook.addBakeryItem(BANANA_MUFFIN);
        assertTrue(addressBook.hasBakeryItem(BANANA_MUFFIN));
    }

    @Test
    public void hasBakeryItem_bakeryItemWithSameIdentityInAddressBook_returnsTrue() {
        addressBook.addBakeryItem(BANANA_MUFFIN);
        assertTrue(addressBook.hasBakeryItem(BANANA_MUFFIN_WITH_DIFFERENT_PRICE));
    }

    @Test
    public void addBakeryItem_nullBakeryItem_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> addressBook.addBakeryItem(null));
    }

    @Test
    public void addBakeryItem_newBakeryItem_addsBakeryItem() {
        addressBook.addBakeryItem(BANANA_MUFFIN);
        assertEquals(List.of(BANANA_MUFFIN), addressBook.getBakeryItemList());
    }

    @Test
    public void addBakeryItem_duplicateBakeryItem_throwsDuplicateBakeryItemException() {
        addressBook.addBakeryItem(BANANA_MUFFIN);
        assertThrows(DuplicateBakeryItemException.class, ()
                -> addressBook.addBakeryItem(BANANA_MUFFIN_WITH_DIFFERENT_PRICE));
    }

    @Test
    public void getPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> addressBook.getPersonList().remove(0));
    }

    @Test
    public void getBakeryItemList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> addressBook.getBakeryItemList().add(BANANA_MUFFIN));
    }

    @Test
    public void equals() {
        addressBook.setBakeryItems(List.of(BANANA_MUFFIN));
        AddressBook sameAddressBook = new AddressBook(addressBook);
        AddressBook differentAddressBook = new AddressBook();
        differentAddressBook.setBakeryItems(List.of(CHOCOLATE_MUFFIN));

        assertTrue(addressBook.equals(addressBook));
        assertTrue(addressBook.equals(sameAddressBook));
        assertFalse(addressBook.equals(null));
        assertFalse(addressBook.equals(5));
        assertFalse(addressBook.equals(differentAddressBook));
        assertEquals(addressBook.hashCode(), sameAddressBook.hashCode());
    }

    @Test
    public void toStringMethod() {
        String expected = AddressBook.class.getCanonicalName() + "{persons=" + addressBook.getPersonList()
                + ", bakeryItems=" + addressBook.getBakeryItemList() + "}";
        assertEquals(expected, addressBook.toString());
    }

    /**
     * A stub ReadOnlyAddressBook whose persons list can violate interface constraints.
     */
    private static class AddressBookStub implements ReadOnlyAddressBook {
        private final ObservableList<Person> persons = FXCollections.observableArrayList();
        private final ObservableList<BakeryItem> bakeryItems = FXCollections.observableArrayList();

        AddressBookStub(Collection<Person> persons) {
            this.persons.setAll(persons);
        }

        AddressBookStub(Collection<Person> persons, Collection<BakeryItem> bakeryItems) {
            this.persons.setAll(persons);
            this.bakeryItems.setAll(bakeryItems);
        }

        @Override
        public ObservableList<Person> getPersonList() {
            return persons;
        }

        @Override
        public ObservableList<BakeryItem> getBakeryItemList() {
            return bakeryItems;
        }
    }

}
