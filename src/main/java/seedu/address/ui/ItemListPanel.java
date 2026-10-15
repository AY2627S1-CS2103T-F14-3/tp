package seedu.address.ui;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import seedu.address.model.bakeryitem.BakeryItem;

/**
 * Panel containing the list of bakery items.
 */
public class ItemListPanel extends UiPart<Region> {
    private static final String FXML = "ItemListPanel.fxml";

    @FXML
    private ListView<BakeryItem> itemListView;

    /**
     * Creates an {@code ItemListPanel} with the given observable list.
     */
    public ItemListPanel(ObservableList<BakeryItem> itemList) {
        super(FXML);
        itemListView.setItems(itemList);
        itemListView.setCellFactory(listView -> new ItemListViewCell());
    }

    /**
     * Displays a bakery item using an indexed {@code ItemCard}.
     */
    class ItemListViewCell extends ListCell<BakeryItem> {
        @Override
        protected void updateItem(BakeryItem item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(new ItemCard(item, getIndex() + 1).getRoot());
            }
        }
    }
}
