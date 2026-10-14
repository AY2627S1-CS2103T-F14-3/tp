package seedu.address.ui;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;

/**
 * Panel containing the order list, ready for model integration.
 */
public class OrderListPanel extends UiPart<Region> {
    private static final String FXML = "OrderListPanel.fxml";

    @FXML
    private ListView<?> orderListView;

    /**
     * Creates an empty {@code OrderListPanel}.
     */
    public OrderListPanel() {
        super(FXML);
    }
}
