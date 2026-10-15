package seedu.address.ui;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import seedu.address.model.order.ClientOrder;
import seedu.address.model.person.Person;

/**
 * Panel containing all orders in insertion order.
 */
public class OrderListPanel extends UiPart<Region> {
    private static final String FXML = "OrderListPanel.fxml";

    @FXML
    private ListView<ClientOrder> orderListView;

    private final ObservableList<Person> clients;

    /**
     * Creates a panel with the observable orders and displayed clients used by order commands.
     */
    public OrderListPanel(ObservableList<ClientOrder> orders, ObservableList<Person> clients) {
        super(FXML);
        this.clients = clients;
        orderListView.setItems(orders);
        orderListView.setCellFactory(listView -> new OrderListViewCell());
        orders.addListener((ListChangeListener<ClientOrder>) change -> orderListView.refresh());
        clients.addListener((ListChangeListener<Person>) change -> orderListView.refresh());
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
                int clientIndex = clients.indexOf(order.getClient()) + 1;
                setGraphic(new OrderCard(order, getIndex() + 1, clientOrderIndex, clientIndex).getRoot());
            }
        }
    }
}
