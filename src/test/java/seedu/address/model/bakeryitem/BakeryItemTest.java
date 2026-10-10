package seedu.address.model.bakeryitem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

public class BakeryItemTest {
    private static final BakeryItemName ITEM_NAME = new BakeryItemName("Banana Muffin");
    private static final BakeryItemName OTHER_ITEM_NAME = new BakeryItemName("Chocolate Muffin");
    private static final Price PRICE = new Price(new BigDecimal("1.20"));
    private static final Price OTHER_PRICE = new Price(new BigDecimal("2.00"));

    @Test
    public void constructor_nullField_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new BakeryItem(null, PRICE));
        assertThrows(NullPointerException.class, () -> new BakeryItem(ITEM_NAME, null));
    }

    @Test
    public void isSameBakeryItem() {
        BakeryItem bakeryItem = new BakeryItem(ITEM_NAME, PRICE);

        assertTrue(bakeryItem.isSameBakeryItem(bakeryItem));
        assertTrue(bakeryItem.isSameBakeryItem(
                new BakeryItem(new BakeryItemName("BANANA   MUFFIN "), OTHER_PRICE)));
        assertFalse(bakeryItem.isSameBakeryItem(null));
        assertFalse(bakeryItem.isSameBakeryItem(new BakeryItem(OTHER_ITEM_NAME, PRICE)));
    }

    @Test
    public void equals() {
        BakeryItem bakeryItem = new BakeryItem(ITEM_NAME, PRICE);
        BakeryItem sameBakeryItem = new BakeryItem(new BakeryItemName("Banana Muffin"),
                new Price(new BigDecimal("1.2")));

        assertTrue(bakeryItem.equals(bakeryItem));
        assertTrue(bakeryItem.equals(sameBakeryItem));
        assertFalse(bakeryItem.equals(null));
        assertFalse(bakeryItem.equals("Banana Muffin"));
        assertFalse(bakeryItem.equals(new BakeryItem(OTHER_ITEM_NAME, PRICE)));
        assertFalse(bakeryItem.equals(new BakeryItem(ITEM_NAME, OTHER_PRICE)));
        assertEquals(bakeryItem.hashCode(), sameBakeryItem.hashCode());
    }

    @Test
    public void toStringMethod() {
        BakeryItem bakeryItem = new BakeryItem(ITEM_NAME, PRICE);
        String expected = BakeryItem.class.getCanonicalName() + "{name=Banana Muffin, price=1.20}";
        assertEquals(expected, bakeryItem.toString());
    }
}
