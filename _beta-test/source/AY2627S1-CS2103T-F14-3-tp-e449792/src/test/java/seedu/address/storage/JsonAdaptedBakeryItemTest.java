package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.storage.JsonAdaptedBakeryItem.MISSING_FIELD_MESSAGE_FORMAT;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalBakeryItems.BANANA_MUFFIN;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.bakeryitem.BakeryItemName;
import seedu.address.model.bakeryitem.Price;

public class JsonAdaptedBakeryItemTest {
    private static final String INVALID_ITEM_NAME = "Crème Brulee";
    private static final String INVALID_PRICE = "1.234";

    private static final String VALID_ITEM_NAME = BANANA_MUFFIN.getItemName().toString();
    private static final String VALID_PRICE = BANANA_MUFFIN.getPrice().toString();

    @Test
    public void toModelType_validBakeryItemDetails_returnsBakeryItem() throws Exception {
        JsonAdaptedBakeryItem bakeryItem = new JsonAdaptedBakeryItem(BANANA_MUFFIN);
        assertEquals(BANANA_MUFFIN, bakeryItem.toModelType());
    }

    @Test
    public void toModelType_invalidItemName_throwsIllegalValueException() {
        JsonAdaptedBakeryItem bakeryItem = new JsonAdaptedBakeryItem(INVALID_ITEM_NAME, VALID_PRICE);
        String expectedMessage = BakeryItemName.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, bakeryItem::toModelType);
    }

    @Test
    public void toModelType_nullItemName_throwsIllegalValueException() {
        JsonAdaptedBakeryItem bakeryItem = new JsonAdaptedBakeryItem(null, VALID_PRICE);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, BakeryItemName.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, bakeryItem::toModelType);
    }

    @Test
    public void toModelType_invalidPrice_throwsIllegalValueException() {
        JsonAdaptedBakeryItem bakeryItem = new JsonAdaptedBakeryItem(VALID_ITEM_NAME, INVALID_PRICE);
        String expectedMessage = Price.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, bakeryItem::toModelType);
    }

    @Test
    public void toModelType_nullPrice_throwsIllegalValueException() {
        JsonAdaptedBakeryItem bakeryItem = new JsonAdaptedBakeryItem(VALID_ITEM_NAME, null);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Price.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, bakeryItem::toModelType);
    }
}
