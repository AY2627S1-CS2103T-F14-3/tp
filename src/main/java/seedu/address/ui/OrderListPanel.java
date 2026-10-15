package seedu.address.ui;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import seedu.address.model.order.ClientOrder;

/**
 * Panel containing all orders in insertion order.
 */
public class OrderListPanel extends UiPart<Region> {
    private static final String FXML = "OrderListPanel.fxml";

    @FXML
    private ListView<ClientOrder> orderListView;

    /**
     * Creates a panel with the given observable order list.
     */
    public OrderListPanel(ObservableList<ClientOrder> orders) {
        super(FXML);
        orderListView.setItems(orders);
        orderListView.setCellFactory(listView -> new OrderListViewCell());
        orders.addListener((ListChangeListener<ClientOrder>) change -> orderListView.refresh());
    }

    /**
     * Displays an order with both its list position and its position within the client's orders.
     */
    class OrderListViewCell extends ListCell<ClientOrder> {
        @Override
        protected void updateItem(ClientOrder order, boolean empty) {
            super.updateItem(order, empty);
            if (empty || order == null) {
                setGraphic(null);
                setText(null);
            } else {
                int clientOrderIndex = 0;
                for (ClientOrder candidate : orderListView.getItems()) {
                    if (candidate.getClient().equals(order.getClient())) {
                        clientOrderIndex++;
                    }
                    if (candidate.getId().equals(order.getId())) {
                        break;
                    }
                }
                setGraphic(new OrderCard(order, getIndex() + 1, clientOrderIndex).getRoot());
            }
        }
    }
}
