package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalBakeryItems.BANANA_MUFFIN;
import static seedu.address.testutil.TypicalBakeryItems.CHOCOLATE_MUFFIN;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;

/**
 * Tests listing bakery items and its interaction with displayed lists.
 */
public class ListItemsCommandTest {
    private Model model;
    private Model expectedModel;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        model.addBakeryItem(BANANA_MUFFIN);
        model.addBakeryItem(CHOCOLATE_MUFFIN);
        expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
    }

    @Test
    public void execute_listIsNotFiltered_showsSameList() {
        assertCommandSuccess(new ListItemsCommand(), model, ListItemsCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_listIsFiltered_showsEverything() {
        model.updateFilteredBakeryItemList(item -> item.equals(BANANA_MUFFIN));
        assertCommandSuccess(new ListItemsCommand(), model, ListItemsCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_personListIsFiltered_preservesPersonFilter() {
        model.updateFilteredPersonList(person -> false);
        expectedModel.updateFilteredPersonList(person -> false);
        assertCommandSuccess(new ListItemsCommand(), model, ListItemsCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_emptyItemList_succeeds() {
        model = new ModelManager();
        expectedModel = new ModelManager();
        assertCommandSuccess(new ListItemsCommand(), model, ListItemsCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ListItemsCommand().execute(null));
    }
}
