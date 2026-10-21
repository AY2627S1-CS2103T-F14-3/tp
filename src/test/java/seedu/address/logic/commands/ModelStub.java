package seedu.address.logic.commands;

import java.util.function.Predicate;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.bakeryitem.BakeryItem;
import seedu.address.model.order.ClientOrder;
import seedu.address.model.person.Person;

/**
 * A default model stub that has all of the methods failing.
 */
class ModelStub implements Model {

    @Override
    public Predicate<Person> getPersonFilter() {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public Predicate<BakeryItem> getBakeryItemFilter() {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public void updateFilteredBakeryItemList(Predicate<BakeryItem> predicate) {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public ObservableList<ClientOrder> getOrdersForClient(Person client) {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public void addOrder(ClientOrder order) {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public void deleteOrder(ClientOrder order) {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public void setOrderCompletion(ClientOrder order, boolean isComplete) {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public GuiSettings getGuiSettings() {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public void setAddressBook(ReadOnlyAddressBook addressBook) {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public ReadOnlyAddressBook getAddressBook() {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public boolean hasPerson(Person person) {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public void deletePerson(Person target) {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public void addPerson(Person person) {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public boolean hasBakeryItem(BakeryItem bakeryItem) {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public void addBakeryItem(BakeryItem bakeryItem) {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public void setBakeryItem(BakeryItem target, BakeryItem editedBakeryItem) {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public void deleteBakeryItem(BakeryItem target) {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public ObservableList<BakeryItem> getFilteredBakeryItemList() {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public void setPerson(Person target, Person editedPerson) {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public ObservableList<Person> getFilteredPersonList() {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public void updateFilteredPersonList(Predicate<Person> predicate) {
        throw new AssertionError("This method should not be called.");
    }
}
