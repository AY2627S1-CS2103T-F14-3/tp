package seedu.address.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.bakeryitem.BakeryItem;
import seedu.address.model.bakeryitem.BakeryItemName;
import seedu.address.model.bakeryitem.Price;

/**
 * Jackson-friendly version of {@link BakeryItem}.
 */
class JsonAdaptedBakeryItem {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Bakery item's %s field is missing!";

    private final String itemName;
    private final String price;

    /**
     * Constructs a {@code JsonAdaptedBakeryItem} with the given bakery item details.
     */
    @JsonCreator
    public JsonAdaptedBakeryItem(@JsonProperty("itemName") String itemName,
            @JsonProperty("price") String price) {
        this.itemName = itemName;
        this.price = price;
    }

    /**
     * Converts a given {@code BakeryItem} into this class for Jackson use.
     */
    public JsonAdaptedBakeryItem(BakeryItem source) {
        itemName = source.getItemName().toString();
        price = source.getPrice().toString();
    }

    /**
     * Converts this Jackson-friendly adapted bakery item object into the model's {@code BakeryItem} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted bakery item.
     */
    public BakeryItem toModelType() throws IllegalValueException {
        if (itemName == null) {
            throw new IllegalValueException(
                    String.format(MISSING_FIELD_MESSAGE_FORMAT, BakeryItemName.class.getSimpleName()));
        }
        if (!BakeryItemName.isValidItemName(itemName)) {
            throw new IllegalValueException(BakeryItemName.MESSAGE_CONSTRAINTS);
        }
        final BakeryItemName modelItemName = new BakeryItemName(itemName);

        if (price == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Price.class.getSimpleName()));
        }
        if (!Price.isValidPriceDollars(price)) {
            throw new IllegalValueException(Price.MESSAGE_CONSTRAINTS);
        }
        final Price modelPrice = Price.fromDollars(price);

        return new BakeryItem(modelItemName, modelPrice);
    }
}
