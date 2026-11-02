package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalBakeryItems.BANANA_MUFFIN;
import static seedu.address.testutil.TypicalBakeryItems.CHOCOLATE_MUFFIN;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_BAKERY_ITEM;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_BAKERY_ITEM;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.EditBakeryItemCommand.EditBakeryItemDescriptor;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.bakeryitem.BakeryItem;
import seedu.address.model.bakeryitem.BakeryItemName;
import seedu.address.model.bakeryitem.Price;

/**
 * Contains integration tests and unit tests for {@code EditBakeryItemCommand}.
 */
public class EditBakeryItemCommandTest {

    private static final BakeryItemName BLUEBERRY_MUFFIN_NAME = new BakeryItemName("Blueberry Muffin");
    private static final Price UPDATED_PRICE = Price.fromDollars("2.50");

    private final AddressBook addressBook = new AddressBook();
    private final Model model;

    public EditBakeryItemCommandTest() {
        addressBook.setBakeryItems(List.of(BANANA_MUFFIN, CHOCOLATE_MUFFIN));
        model = new ModelManager(addressBook, new UserPrefs());
    }

    @Test
    public void execute_allFieldsSpecified_success() {
        EditBakeryItemDescriptor descriptor = new EditBakeryItemDescriptor();
        descriptor.setItemName(BLUEBERRY_MUFFIN_NAME);
        descriptor.setPrice(UPDATED_PRICE);
        EditBakeryItemCommand command = new EditBakeryItemCommand(INDEX_FIRST_BAKERY_ITEM, descriptor);
        BakeryItem editedBakeryItem = new BakeryItem(BLUEBERRY_MUFFIN_NAME, UPDATED_PRICE);

        String expectedMessage = String.format(EditBakeryItemCommand.MESSAGE_EDIT_BAKERY_ITEM_SUCCESS,
                Messages.format(editedBakeryItem));
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setBakeryItem(BANANA_MUFFIN, editedBakeryItem);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_priceOnly_nameUnchanged() {
        EditBakeryItemDescriptor descriptor = new EditBakeryItemDescriptor();
        descriptor.setPrice(UPDATED_PRICE);
        EditBakeryItemCommand command = new EditBakeryItemCommand(INDEX_FIRST_BAKERY_ITEM, descriptor);
        BakeryItem editedBakeryItem = new BakeryItem(BANANA_MUFFIN.getItemName(), UPDATED_PRICE);

        String expectedMessage = String.format(EditBakeryItemCommand.MESSAGE_EDIT_BAKERY_ITEM_SUCCESS,
                Messages.format(editedBakeryItem));
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setBakeryItem(BANANA_MUFFIN, editedBakeryItem);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_filteredList_usesDisplayedIndex() {
        model.updateFilteredBakeryItemList(item -> item.equals(CHOCOLATE_MUFFIN));
        EditBakeryItemDescriptor descriptor = new EditBakeryItemDescriptor();
        descriptor.setPrice(UPDATED_PRICE);
        EditBakeryItemCommand command = new EditBakeryItemCommand(INDEX_FIRST_BAKERY_ITEM, descriptor);
        BakeryItem editedBakeryItem = new BakeryItem(CHOCOLATE_MUFFIN.getItemName(), UPDATED_PRICE);

        String expectedMessage = String.format(EditBakeryItemCommand.MESSAGE_EDIT_BAKERY_ITEM_SUCCESS,
                Messages.format(editedBakeryItem));
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setBakeryItem(CHOCOLATE_MUFFIN, editedBakeryItem);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_duplicateName_throwsCommandException() {
        EditBakeryItemDescriptor descriptor = new EditBakeryItemDescriptor();
        descriptor.setItemName(new BakeryItemName("CHOCOLATE   MUFFIN"));
        EditBakeryItemCommand command = new EditBakeryItemCommand(INDEX_FIRST_BAKERY_ITEM, descriptor);

        assertCommandFailure(command, model, EditBakeryItemCommand.MESSAGE_DUPLICATE_BAKERY_ITEM);
    }

    @Test
    public void execute_invalidIndex_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredBakeryItemList().size() + 1);
        EditBakeryItemDescriptor descriptor = new EditBakeryItemDescriptor();
        descriptor.setPrice(UPDATED_PRICE);
        EditBakeryItemCommand command = new EditBakeryItemCommand(outOfBoundIndex, descriptor);

        assertCommandFailure(command, model, Messages.MESSAGE_INVALID_BAKERY_ITEM_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        EditBakeryItemDescriptor descriptor = new EditBakeryItemDescriptor();
        descriptor.setPrice(UPDATED_PRICE);
        EditBakeryItemCommand standardCommand = new EditBakeryItemCommand(INDEX_FIRST_BAKERY_ITEM, descriptor);

        EditBakeryItemDescriptor descriptorCopy = new EditBakeryItemDescriptor(descriptor);
        assertTrue(standardCommand.equals(new EditBakeryItemCommand(INDEX_FIRST_BAKERY_ITEM, descriptorCopy)));
        assertTrue(standardCommand.equals(standardCommand));
        assertFalse(standardCommand.equals(null));
        assertFalse(standardCommand.equals(new ClearCommand()));
        assertFalse(standardCommand.equals(new EditBakeryItemCommand(INDEX_SECOND_BAKERY_ITEM, descriptor)));

        EditBakeryItemDescriptor differentDescriptor = new EditBakeryItemDescriptor();
        differentDescriptor.setItemName(BLUEBERRY_MUFFIN_NAME);
        assertFalse(standardCommand.equals(new EditBakeryItemCommand(INDEX_FIRST_BAKERY_ITEM, differentDescriptor)));
    }

    @Test
    public void toStringMethod() {
        EditBakeryItemDescriptor descriptor = new EditBakeryItemDescriptor();
        EditBakeryItemCommand command = new EditBakeryItemCommand(INDEX_FIRST_BAKERY_ITEM, descriptor);
        String expected = EditBakeryItemCommand.class.getCanonicalName() + "{index=" + INDEX_FIRST_BAKERY_ITEM
                + ", editBakeryItemDescriptor=" + descriptor + "}";

        assertEquals(expected, command.toString());
    }
}
