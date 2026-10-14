package seedu.address.testutil;

import seedu.address.model.bakeryitem.BakeryItem;
import seedu.address.model.bakeryitem.BakeryItemName;
import seedu.address.model.bakeryitem.Price;

/**
 * A utility class containing {@code BakeryItem} objects to be used in tests.
 */
public class TypicalBakeryItems {

    public static final BakeryItem BANANA_MUFFIN = new BakeryItem(
            new BakeryItemName("Banana Muffin"), Price.fromDollars("1.20"));
    public static final BakeryItem BANANA_MUFFIN_WITH_DIFFERENT_PRICE = new BakeryItem(
            new BakeryItemName("BANANA   MUFFIN"), Price.fromDollars("1.50"));
    public static final BakeryItem CHOCOLATE_MUFFIN = new BakeryItem(
            new BakeryItemName("Chocolate Muffin"), Price.fromDollars("1.50"));

    private TypicalBakeryItems() {}
}
