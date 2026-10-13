package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.bakeryitem.BakeryItem;
import seedu.address.model.bakeryitem.BakeryItemName;
import seedu.address.model.bakeryitem.Price;
import seedu.address.model.person.Person;

public class AddBakeryItemCommandTest {
    private static final BakeryItem BANANA_MUFFIN = new BakeryItem(new BakeryItemName("Banana Muffin"),
            new Price(new BigDecimal("1.20")));
    private static final BakeryItem CHOCOLATE_MUFFIN = new BakeryItem(new BakeryItemName("Chocolate Muffin"),
            new Price(new BigDecimal("1.50")));

    @Test
    public void constructor_nullBakeryItem_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AddBakeryItemCommand(null));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        AddBakeryItemCommand addBakeryItemCommand = new AddBakeryItemCommand(BANANA_MUFFIN);
        assertThrows(NullPointerException.class, () -> addBakeryItemCommand.execute(null));
    }

    @Test
    public void execute_bakeryItemAcceptedByModel_addSuccessful() throws Exception {
        ModelStubAcceptingBakeryItemAdded modelStub = new ModelStubAcceptingBakeryItemAdded();

        CommandResult commandResult = new AddBakeryItemCommand(BANANA_MUFFIN).execute(modelStub);

        assertEquals(String.format(AddBakeryItemCommand.MESSAGE_SUCCESS, Messages.format(BANANA_MUFFIN)),
                commandResult.getFeedbackToUser());
        assertEquals(List.of(BANANA_MUFFIN), modelStub.bakeryItemsAdded);
    }

    @Test
    public void execute_duplicateBakeryItem_throwsCommandException() {
        AddBakeryItemCommand addBakeryItemCommand = new AddBakeryItemCommand(BANANA_MUFFIN);
        ModelStub modelStub = new ModelStubWithBakeryItem(BANANA_MUFFIN);

        assertThrows(CommandException.class,
                AddBakeryItemCommand.MESSAGE_DUPLICATE_ITEM, () -> addBakeryItemCommand.execute(modelStub));
    }

    @Test
    public void equals() {
        AddBakeryItemCommand addBananaMuffinCommand = new AddBakeryItemCommand(BANANA_MUFFIN);
        AddBakeryItemCommand addBananaMuffinCommandCopy = new AddBakeryItemCommand(BANANA_MUFFIN);
        AddBakeryItemCommand addChocolateMuffinCommand = new AddBakeryItemCommand(CHOCOLATE_MUFFIN);

        assertTrue(addBananaMuffinCommand.equals(addBananaMuffinCommand));
        assertTrue(addBananaMuffinCommand.equals(addBananaMuffinCommandCopy));
        assertFalse(addBananaMuffinCommand.equals(null));
        assertFalse(addBananaMuffinCommand.equals(1));
        assertFalse(addBananaMuffinCommand.equals(addChocolateMuffinCommand));
    }

    @Test
    public void toStringMethod() {
        AddBakeryItemCommand command = new AddBakeryItemCommand(BANANA_MUFFIN);
        String expected = AddBakeryItemCommand.class.getCanonicalName() + "{toAdd=" + BANANA_MUFFIN + "}";
        assertEquals(expected, command.toString());
    }

    /**
     * A default model stub that has all of the methods failing.
     */
    private class ModelStub implements Model {
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

    /**
     * A model stub that contains a single bakery item.
     */
    private class ModelStubWithBakeryItem extends ModelStub {
        private final BakeryItem bakeryItem;

        ModelStubWithBakeryItem(BakeryItem bakeryItem) {
            requireNonNull(bakeryItem);
            this.bakeryItem = bakeryItem;
        }

        @Override
        public boolean hasBakeryItem(BakeryItem bakeryItem) {
            requireNonNull(bakeryItem);
            return this.bakeryItem.isSameBakeryItem(bakeryItem);
        }
    }

    /**
     * A model stub that accepts bakery items being added.
     */
    private class ModelStubAcceptingBakeryItemAdded extends ModelStub {
        private final ArrayList<BakeryItem> bakeryItemsAdded = new ArrayList<>();

        @Override
        public boolean hasBakeryItem(BakeryItem bakeryItem) {
            requireNonNull(bakeryItem);
            return bakeryItemsAdded.stream().anyMatch(bakeryItem::isSameBakeryItem);
        }

        @Override
        public void addBakeryItem(BakeryItem bakeryItem) {
            requireNonNull(bakeryItem);
            bakeryItemsAdded.add(bakeryItem);
        }
    }
}
