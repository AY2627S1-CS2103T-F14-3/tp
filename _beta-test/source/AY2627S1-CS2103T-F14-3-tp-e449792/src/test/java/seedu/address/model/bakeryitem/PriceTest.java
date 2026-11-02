package seedu.address.model.bakeryitem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

public class PriceTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Price(null));
    }

    @Test
    public void constructor_negativePrice_throwsArithmeticException() {
        assertThrows(ArithmeticException.class, () -> new Price(new BigDecimal("-0.01")));
    }

    @Test
    public void constructor_priceRequiringRounding_throwsArithmeticException() {
        assertThrows(ArithmeticException.class, () -> new Price(new BigDecimal("1.234")));
    }

    @Test
    public void isValidPriceDollars() {
        assertThrows(NullPointerException.class, () -> Price.isValidPriceDollars(null));

        assertFalse(Price.isValidPriceDollars(""));
        assertFalse(Price.isValidPriceDollars("."));
        assertFalse(Price.isValidPriceDollars("-1"));
        assertFalse(Price.isValidPriceDollars("1.234"));
        assertFalse(Price.isValidPriceDollars("$1.20"));

        assertTrue(Price.isValidPriceDollars("0"));
        assertTrue(Price.isValidPriceDollars("1"));
        assertTrue(Price.isValidPriceDollars("1."));
        assertTrue(Price.isValidPriceDollars("1.2"));
        assertTrue(Price.isValidPriceDollars("1.20"));
        assertTrue(Price.isValidPriceDollars(".2"));
        assertTrue(Price.isValidPriceDollars(".20"));
    }

    @Test
    public void fromDollars_invalidPrice_throwsNumberFormatException() {
        assertThrows(NumberFormatException.class, () -> Price.fromDollars("1.234"));
    }

    @Test
    public void fromDollars_validPrice_returnsPrice() {
        assertEquals(new Price(new BigDecimal("1.20")), Price.fromDollars(" 1.2 "));
    }

    @Test
    public void equals() {
        Price price = new Price(new BigDecimal("1.20"));
        Price samePrice = new Price(new BigDecimal("1.2"));

        assertTrue(price.equals(price));
        assertTrue(price.equals(samePrice));
        assertFalse(price.equals(null));
        assertFalse(price.equals(new BigDecimal("1.20")));
        assertFalse(price.equals(new Price(new BigDecimal("2.00"))));
        assertEquals(price.hashCode(), samePrice.hashCode());
    }

    @Test
    public void toStringMethod() {
        assertEquals("1.20", new Price(new BigDecimal("1.2")).toString());
    }
}
