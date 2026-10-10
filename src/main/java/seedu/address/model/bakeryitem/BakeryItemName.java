package seedu.address.model.bakeryitem;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a bakery item's name in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidItemName(String)}
 */
public class BakeryItemName {
    public static final String MESSAGE_CONSTRAINTS =
            "Names should only contain printable ASCII characters, and should not be blank";

    /**
     * The first character of the name must not be a whitespace,
     * otherwise " " (a blank string) becomes a valid input.
     */
    public static final String VALIDATION_REGEX = "[\\x21-\\x7E][\\x20-\\x7E]*";

    public final String itemName;

    /**
     * Constructs a {@code BakeryItemName}.
     *
     * @param itemName A valid bakery item name.
     */
    public BakeryItemName(String itemName) {
        requireNonNull(itemName);
        checkArgument(isValidItemName(itemName), MESSAGE_CONSTRAINTS);
        this.itemName = itemName;
    }

    /**
     * Returns true if a given string is a valid bakery item name.
     */
    public static boolean isValidItemName(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    public String getNormalizedItemName() {
        return this.itemName.toUpperCase().trim().replaceAll("\\s+", " ");
    }

    /**
     * Returns true if the {@code other} bakery item has the same normalized item name.
     */
    public boolean hasSameNormalizedName(BakeryItemName other) {
        return getNormalizedItemName().equals(other.getNormalizedItemName());
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BakeryItemName other)) {
            return false;
        }

        return this.itemName.equals(other.itemName);
    }

    @Override
    public int hashCode() {
        return itemName.hashCode();
    }

    @Override
    public String toString() {
        return this.itemName;
    }
}
