package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalBakeryItems.BANANA_MUFFIN;
import static seedu.address.testutil.TypicalBakeryItems.CHOCOLATE_MUFFIN;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.bakeryitem.BakeryItem;

public class AddBakeryItemCommandTest {
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
