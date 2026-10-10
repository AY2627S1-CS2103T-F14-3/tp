package seedu.address.model.bakeryitem;

import static java.util.Objects.requireNonNull;

import java.math.BigDecimal;

/**
 * Represents a bakery item's price in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidPriceDollars(String)}
 */
public class Price {
    public static final String MESSAGE_CONSTRAINTS =
            "Prices should be in one of the formats: D | D. | D.c | D.cc | .cc | .c";

    /**
     * Price (dollars) in the formats D, D., D.c, D.cc, .cc, .c are all be accepted
     */
    public static final String VALIDATION_REGEX = "\\d+(?:\\.\\d{0,2})?|\\.\\d{1,2}";

    public final BigDecimal priceDollars;

    /**
     * Constructs a {@code Price}.
     *
     * @param priceDollars A valid price in dollars.
     * @throws ArithmeticException If price is invalid, i.e. the price cannot be represented
     *             with 2dp without rounding, or is negative.
     */
    public Price(BigDecimal priceDollars) {
        requireNonNull(priceDollars);

        if (priceDollars.signum() < 0) {
            throw new ArithmeticException("Expected non-negative priceDollars, got " + priceDollars + " instead");
        }

        // throws ArithmeticException if needs rounding (.123 does, 0.12000 doesn't)
        this.priceDollars = priceDollars.setScale(2);
    }

    /**
     * Returns true if a given string is a valid price.
     */
    public static boolean isValidPriceDollars(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    /**
     * Converts the dollar amount given as text into a {@link Price}.
     *
     * @param priceDollarsText The dollar amount. See {@link #VALIDATION_REGEX} for valid formats.
     * @throws NumberFormatException If the {@code priceDollarsText} is not in a valid format.
     */
    public static Price fromDollars(String priceDollarsText) {
        String trimmed = priceDollarsText.trim();

        if (!isValidPriceDollars(trimmed)) {
            throw new NumberFormatException(String.format("priceDollarsText %s does not match the format %s",
                    priceDollarsText, VALIDATION_REGEX));
        }

        // this should not throw since our validation + normalization guarantees it is correct format
        BigDecimal priceDollars = new BigDecimal(trimmed);
        // guaranteed not to throw since we already validated > 0 and 2dp
        return new Price(priceDollars);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Price other)) {
            return false;
        }

        // using compareTo here to compare the values and not the scale (decimal places)
        return this.priceDollars.compareTo(other.priceDollars) == 0;
    }

    @Override
    public int hashCode() {
        return priceDollars.stripTrailingZeros().hashCode();
    }

    @Override
    public String toString() {
        return this.priceDollars.toPlainString();
    }
}
