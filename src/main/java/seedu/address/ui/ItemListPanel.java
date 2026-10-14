package seedu.address.ui;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;

/**
 * Panel containing the item list, ready for model integration.
 */
public class ItemListPanel extends UiPart<Region> {
    private static final String FXML = "ItemListPanel.fxml";

    @FXML
    private ListView<?> itemListView;

    /**
     * Creates an empty {@code ItemListPanel}.
     */
    public ItemListPanel() {
        super(FXML);
    }
}
