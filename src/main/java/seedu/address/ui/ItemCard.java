package seedu.address.ui;

import javafx.scene.layout.Region;

/**
 * A UI component for an item card, ready for model integration.
 */
public class ItemCard extends UiPart<Region> {
    private static final String FXML = "ItemListCard.fxml";

    /**
     * Creates an empty {@code ItemCard}.
     */
    public ItemCard() {
        super(FXML);
    }
}
