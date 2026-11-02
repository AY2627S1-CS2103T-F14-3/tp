package seedu.address.model.order;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents the number of bakery items requested in an order.
 * Guarantees: immutable; the value is between 1 and 9,999 inclusive.
 */
public final class Quantity {
    public static final int MAX_QUANTITY = 9999;
    public static final String MESSAGE_CONSTRAINTS = "Order quantity must be a whole number between 1 and 9,999.";

    private final int value;

    /**
     * Creates a quantity with the given number of items.
     *
     * @param value A quantity between 1 and 9,999 inclusive.
     */
    public Quantity(int value) {
        checkArgument(value >= 1 && value <= MAX_QUANTITY, MESSAGE_CONSTRAINTS);
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    /**
     * Returns true if the given text represents a whole-number quantity between 1 and 9,999.
     * Leading zeros are accepted; signs, decimal points, and surrounding whitespace are not.
     */
    public static boolean isValidQuantity(String text) {
        requireNonNull(text);
        if (!text.matches("[0-9]+")) {
            return false;
        }
        try {
            int quantity = Integer.parseInt(text);
            return quantity >= 1 && quantity <= MAX_QUANTITY;
        } catch (NumberFormatException exception) {
            return false;
        }
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Quantity otherQuantity && value == otherQuantity.value;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }
}
