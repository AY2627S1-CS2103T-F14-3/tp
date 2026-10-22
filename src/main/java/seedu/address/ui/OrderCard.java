package seedu.address.ui;

import java.time.LocalDate;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import seedu.address.model.order.ClientOrder;

/**
 * Displays an order's list index, client, item, quantity, deadline, and status.
 */
public class OrderCard extends UiPart<Region> {
    private static final String FXML = "OrderListCard.fxml";

    @FXML
    private Label client;
    @FXML
    private Label item;
    @FXML
    private Label deadline;
    @FXML
    private Label status;

    /**
     * Creates a card with its row number, client-local order number, and displayed client index.
     * A zero client index indicates that the client is hidden from the displayed client list.
     */
    public OrderCard(ClientOrder order, int displayedIndex, int clientOrderIndex, int clientIndex) {
        this(order, displayedIndex, clientOrderIndex, clientIndex, LocalDate.now());
    }

    /** Creates a card using the supplied current date for consistent status rendering. */
    OrderCard(ClientOrder order, int displayedIndex, int clientOrderIndex, int clientIndex, LocalDate today) {
        super(FXML);
        String clientLabel = clientIndex > 0 ? "Client " + clientIndex : "Client";
        client.setText(clientLabel + ": " + order.getClient().getName() + " — Order " + clientOrderIndex);
        item.setText(displayedIndex + ". " + order.getQuantity() + " x " + order.getItem().getItemName());
        deadline.setText("Due: " + order.getDeadline());
        boolean overdue = order.getDeadline().getValue().isBefore(today);
        String style = order.isComplete() ? "order-complete" : overdue ? "order-overdue" : "order-incomplete";
        getRoot().getStyleClass().add(style);
        status.setText(order.isComplete() ? "Complete" : overdue ? "Incomplete (overdue)" : "Incomplete");
    }
}
