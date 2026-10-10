package seedu.address.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.bakeryitem.BakeryItem;
import seedu.address.model.bakeryitem.BakeryItemName;
import seedu.address.model.bakeryitem.Price;

/**
 * Stores a bakery item's name and exact decimal price in editable JSON.
 */
class JsonAdaptedBakeryItem {
    private final String name;
    private final String price;

    /**
     * Creates an adapted item from JSON fields.
     */
    @JsonCreator
    public JsonAdaptedBakeryItem(@JsonProperty("name") String name, @JsonProperty("price") String price) {
        this.name = name;
        this.price = price;
    }

    /**
     * Creates an adapted item from a validated model item.
     */
    public JsonAdaptedBakeryItem(BakeryItem item) {
        name = item.getItemName().itemName;
        price = item.getPrice().toString();
    }

    /**
     * Returns a validated item or reports invalid or missing JSON fields.
     */
    public BakeryItem toModelType() throws IllegalValueException {
        if (name == null || !BakeryItemName.isValidItemName(name)) {
            throw new IllegalValueException(BakeryItemName.MESSAGE_CONSTRAINTS);
        }
        if (price == null || !Price.isValidPriceDollars(price)) {
            throw new IllegalValueException(Price.MESSAGE_CONSTRAINTS);
        }
        return new BakeryItem(new BakeryItemName(name), Price.fromDollars(price));
    }
}
