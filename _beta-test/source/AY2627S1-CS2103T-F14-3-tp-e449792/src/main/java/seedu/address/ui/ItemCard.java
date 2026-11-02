package seedu.address.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import seedu.address.model.bakeryitem.BakeryItem;

/**
 * A UI component that displays a bakery item's index, name, and price.
 */
public class ItemCard extends UiPart<Region> {
    private static final String FXML = "ItemListCard.fxml";

    public final BakeryItem item;

    @FXML
    private Label id;
    @FXML
    private Label name;
    @FXML
    private Label price;

    /**
     * Creates an {@code ItemCard} with the given item and index to display.
     */
    public ItemCard(BakeryItem item, int displayedIndex) {
        super(FXML);
        this.item = item;
        id.setText(displayedIndex + ". ");
        name.setText(item.getItemName().getItemName());
        price.setText("$" + item.getPrice());
    }
}
