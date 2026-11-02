package seedu.address.model.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class QuantityTest {
    @Test
    public void constructor_outsideBounds_throwsIllegalArgumentException() {
        for (int invalid : new int[] {Integer.MIN_VALUE, -1, 0, 10000, Integer.MAX_VALUE}) {
            assertThrows(IllegalArgumentException.class, () -> new Quantity(invalid));
        }
    }

    @Test
    public void isValidQuantity_invalidInput_returnsFalse() {
        assertThrows(NullPointerException.class, () -> Quantity.isValidQuantity(null));
        for (String invalid : new String[] {"", " ", "0", "000", "-1", "+1", "1.0", "1 2", "1,000",
            "10000", "2147483648", "999999999999999999999999999999", " 5 ", "five"}) {
            assertFalse(Quantity.isValidQuantity(invalid), invalid);
        }
    }

    @Test
    public void isValidQuantity_validInput_returnsTrue() {
        for (String valid : new String[] {"1", "5", "9999", "0005"}) {
            assertTrue(Quantity.isValidQuantity(valid), valid);
        }
        assertEquals(1, new Quantity(1).getValue());
        assertEquals(9999, new Quantity(9999).getValue());
    }

    @Test
    public void equals_sameValue_returnsTrue() {
        Quantity quantity = new Quantity(5);
        assertEquals(quantity, new Quantity(5));
        assertEquals(quantity.hashCode(), new Quantity(5).hashCode());
        assertFalse(quantity.equals(new Quantity(6)));
        assertFalse(quantity.equals(null));
        assertFalse(quantity.equals(5));
        assertEquals("5", quantity.toString());
    }
}
