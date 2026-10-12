package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalBakeryItems.BANANA_MUFFIN;
import static seedu.address.testutil.TypicalBakeryItems.CHOCOLATE_MUFFIN;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.bakeryitem.BakeryItem;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code DeleteBakeryItemCommand}.
 */
public class DeleteBakeryItemCommandTest {

    private final AddressBook addressBook = new AddressBook();
    private final Model model;

    public DeleteBakeryItemCommandTest() {
        addressBook.setBakeryItems(List.of(BANANA_MUFFIN, CHOCOLATE_MUFFIN));
        model = new ModelManager(addressBook, new UserPrefs());
    }

    @Test
    public void execute_validIndex_success() {
        BakeryItem bakeryItemToDelete = model.getFilteredBakeryItemList().get(INDEX_FIRST_PERSON.getZeroBased());
        DeleteBakeryItemCommand deleteCommand = new DeleteBakeryItemCommand(INDEX_FIRST_PERSON);

        String expectedMessage = String.format(DeleteBakeryItemCommand.MESSAGE_DELETE_BAKERY_ITEM_SUCCESS,
                Messages.format(bakeryItemToDelete));
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deleteBakeryItem(bakeryItemToDelete);

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndex_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredBakeryItemList().size() + 1);
        DeleteBakeryItemCommand deleteCommand = new DeleteBakeryItemCommand(outOfBoundIndex);

        assertCommandFailure(deleteCommand, model, Messages.MESSAGE_INVALID_BAKERY_ITEM_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        DeleteBakeryItemCommand deleteFirstCommand = new DeleteBakeryItemCommand(INDEX_FIRST_PERSON);
        DeleteBakeryItemCommand deleteSecondCommand = new DeleteBakeryItemCommand(INDEX_SECOND_PERSON);

        // same object -> returns true
        assertTrue(deleteFirstCommand.equals(deleteFirstCommand));

        // same values -> returns true
        DeleteBakeryItemCommand deleteFirstCommandCopy = new DeleteBakeryItemCommand(INDEX_FIRST_PERSON);
        assertTrue(deleteFirstCommand.equals(deleteFirstCommandCopy));

        // different types -> returns false
        assertFalse(deleteFirstCommand.equals(1));

        // null -> returns false
        assertFalse(deleteFirstCommand.equals(null));

        // different bakery item -> returns false
        assertFalse(deleteFirstCommand.equals(deleteSecondCommand));
    }

    @Test
    public void toStringMethod() {
        Index targetIndex = Index.fromOneBased(1);
        DeleteBakeryItemCommand deleteBakeryItemCommand = new DeleteBakeryItemCommand(targetIndex);
        String expected = DeleteBakeryItemCommand.class.getCanonicalName() + "{targetIndex=" + targetIndex + "}";
        assertEquals(expected, deleteBakeryItemCommand.toString());
    }
}
