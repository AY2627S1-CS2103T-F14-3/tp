package seedu.address.model.bakeryitem;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Objects;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Represents a bakery item in the address book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class BakeryItem {
    private final BakeryItemName itemName;
    private final Price price;

    /**
     * Creates a new BakeryItem. Every field must be present and not null.
     */
    public BakeryItem(BakeryItemName itemName, Price price) {
        requireAllNonNull(itemName, price);
        this.itemName = itemName;
        this.price = price;
    }

    public BakeryItemName getItemName() {
        return itemName;
    }

    public Price getPrice() {
        return price;
    }

    /**
     * Returns true if both bakery items have the same name after normalizing.
     * This defines a weaker notion of equality between two bakery items.
     */
    public boolean isSameBakeryItem(BakeryItem otherItem) {
        if (otherItem == this) {
            return true;
        }

        return otherItem != null && this.getItemName().hasSameNormalizedName(otherItem.getItemName());
    }

    /**
     * Returns true if both persons have the same identity and data fields.
     * This defines a stronger notion of equality between two persons.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof BakeryItem other)) {
            return false;
        }

        return this.getItemName().equals(other.getItemName())
                && this.getPrice().equals(other.getPrice());
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemName, price);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", itemName)
                .add("price", price)
                .toString();

    }
}
