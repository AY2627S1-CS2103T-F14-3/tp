package seedu.address.model.bakeryitem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class BakeryItemNameTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new BakeryItemName(null));
    }

    @Test
    public void constructor_invalidName_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new BakeryItemName(""));
    }

    @Test
    public void isValidItemName() {
        assertThrows(NullPointerException.class, () -> BakeryItemName.isValidItemName(null));

        assertFalse(BakeryItemName.isValidItemName(""));
        assertFalse(BakeryItemName.isValidItemName(" "));
        assertFalse(BakeryItemName.isValidItemName(" Muffin"));
        assertFalse(BakeryItemName.isValidItemName("Banana\tMuffin"));
        assertFalse(BakeryItemName.isValidItemName("Banana\nMuffin"));
        assertFalse(BakeryItemName.isValidItemName("Cr\u00e8me Brulee"));
        assertFalse(BakeryItemName.isValidItemName("Banana\u007fMuffin"));

        assertTrue(BakeryItemName.isValidItemName("Muffin"));
        assertTrue(BakeryItemName.isValidItemName("123"));
        assertTrue(BakeryItemName.isValidItemName("Banana Muffin 2"));
        assertTrue(BakeryItemName.isValidItemName("Banana-Muffin (Large)!"));
        assertTrue(BakeryItemName.isValidItemName("Cake & Coffee @ $5.00"));
        assertTrue(BakeryItemName.isValidItemName("Banana Muffin "));
    }

    @Test
    public void getNormalizedItemName_differentFormatting_returnsSameName() {
        BakeryItemName itemName = new BakeryItemName("Banana   Muffin ");
        assertEquals("BANANA MUFFIN", itemName.getNormalizedItemName());
    }

    @Test
    public void hasSameNormalizedName() {
        BakeryItemName itemName = new BakeryItemName("Banana Muffin");

        assertTrue(itemName.hasSameNormalizedName(new BakeryItemName("BANANA   MUFFIN ")));
        assertFalse(itemName.hasSameNormalizedName(new BakeryItemName("Chocolate Muffin")));
    }

    @Test
    public void equals() {
        BakeryItemName itemName = new BakeryItemName("Banana Muffin");
        BakeryItemName sameItemName = new BakeryItemName("Banana Muffin");

        assertTrue(itemName.equals(itemName));
        assertTrue(itemName.equals(sameItemName));
        assertFalse(itemName.equals(null));
        assertFalse(itemName.equals("Banana Muffin"));
        assertFalse(itemName.equals(new BakeryItemName("Chocolate Muffin")));
        assertEquals(itemName.hashCode(), sameItemName.hashCode());
    }

    @Test
    public void toStringMethod() {
        assertEquals("Banana Muffin", new BakeryItemName("Banana Muffin").toString());
    }
}
