package seedu.address.ui;

import javafx.scene.layout.Region;

/**
 * A UI component for an order card, ready for model integration.
 */
public class OrderCard extends UiPart<Region> {
    private static final String FXML = "OrderListCard.fxml";

    /**
     * Creates an empty {@code OrderCard}.
     */
    public OrderCard() {
        super(FXML);
    }
}
