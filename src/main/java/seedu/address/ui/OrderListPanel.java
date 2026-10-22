package seedu.address.ui;

import java.time.Clock;
import java.time.LocalDate;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import javafx.util.Duration;
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
    private final ObservableList<ClientOrder> allOrders;
    private final Clock clock;
    private final Timeline dateRefresh;
    private LocalDate today;

    /**
     * Creates a panel with the observable orders and displayed clients used by order commands.
     */
    public OrderListPanel(ObservableList<ClientOrder> orders, ObservableList<Person> clients) {
        this(orders, clients, orders, Clock.systemDefaultZone());
    }

    /** Creates a filtered panel whose client-local order indices refer to all stored orders. */
    public OrderListPanel(ObservableList<ClientOrder> orders, ObservableList<Person> clients,
            ObservableList<ClientOrder> allOrders) {
        this(orders, clients, allOrders, Clock.systemDefaultZone());
    }

    /** Creates a panel using the supplied clock for overdue status. */
    OrderListPanel(ObservableList<ClientOrder> orders, ObservableList<Person> clients, Clock clock) {
        this(orders, clients, orders, clock);
    }

    /** Creates a filtered panel with stable order indices and the supplied status clock. */
    OrderListPanel(ObservableList<ClientOrder> orders, ObservableList<Person> clients,
            ObservableList<ClientOrder> allOrders, Clock clock) {
        super(FXML);
        this.clients = clients;
        this.allOrders = allOrders;
        this.clock = clock;
        today = LocalDate.now(clock);
        orderListView.setItems(orders);
        orderListView.setCellFactory(listView -> new OrderListViewCell());
        orders.addListener((ListChangeListener<ClientOrder>) change -> orderListView.refresh());
        clients.addListener((ListChangeListener<Person>) change -> orderListView.refresh());
        if (allOrders != orders) {
            allOrders.addListener((ListChangeListener<ClientOrder>) change -> orderListView.refresh());
        }
        dateRefresh = new Timeline(new KeyFrame(Duration.seconds(1), event -> refreshDate()));
        dateRefresh.setCycleCount(Timeline.INDEFINITE);
        getRoot().sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene == null) {
                dateRefresh.stop();
            } else {
                refreshDate();
                dateRefresh.play();
            }
        });
    }

    /** Refreshes visible cards when the local date changes, including after system sleep. */
    void refreshDate() {
        LocalDate currentDate = LocalDate.now(clock);
        if (!currentDate.equals(today)) {
            today = currentDate;
            orderListView.refresh();
        }
    }

    /** Stops the date timer when the application closes. */
    public void dispose() {
        dateRefresh.stop();
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
                for (ClientOrder candidate : allOrders) {
                    if (candidate.getClient().equals(order.getClient())) {
                        clientOrderIndex++;
                    }
                    if (candidate.getId().equals(order.getId())) {
                        break;
                    }
                }
                int clientIndex = clients.indexOf(order.getClient()) + 1;
                setGraphic(new OrderCard(order, getIndex() + 1, clientOrderIndex, clientIndex, today).getRoot());
            }
        }
    }
}
