package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import seedu.address.model.bakeryitem.BakeryItem;
import seedu.address.model.bakeryitem.BakeryItemName;
import seedu.address.model.bakeryitem.Price;

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
}
