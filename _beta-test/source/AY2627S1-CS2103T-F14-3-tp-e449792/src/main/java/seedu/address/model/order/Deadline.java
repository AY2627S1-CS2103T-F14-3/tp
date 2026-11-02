package seedu.address.model.order;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Represents an order deadline as an immutable calendar date.
 * Past dates are accepted for recording earlier orders.
 */
public final class Deadline {
    public static final String MESSAGE_CONSTRAINTS = "Deadline must be a valid calendar date in YYYY-MM-DD format.";

    private final LocalDate value;

    /**
     * Creates a deadline from a valid date in YYYY-MM-DD format.
     *
     * @param date A valid calendar date with a four-digit year, two-digit month, and two-digit day.
     */
    public Deadline(String date) {
        requireNonNull(date);
        checkArgument(isValidDeadline(date), MESSAGE_CONSTRAINTS);
        value = LocalDate.parse(date);
    }

    public LocalDate getValue() {
        return value;
    }

    /**
     * Returns true if the given text is a valid calendar date in YYYY-MM-DD format.
     * Dates are parsed strictly so impossible dates are never silently adjusted.
     */
    public static boolean isValidDeadline(String text) {
        requireNonNull(text);
        if (!text.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}")) {
            return false;
        }
        try {
            LocalDate.parse(text);
            return true;
        } catch (DateTimeParseException exception) {
            return false;
        }
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Deadline otherDeadline && value.equals(otherDeadline.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
