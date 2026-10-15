package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static seedu.address.testutil.TypicalBakeryItems.BANANA_MUFFIN;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BOB;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.StackPane;
import seedu.address.model.order.ClientOrder;
import seedu.address.model.order.Deadline;
import seedu.address.model.order.Quantity;
import seedu.address.model.person.Person;

/**
 * Exercises the real FXML cards and virtualized list cells on the JavaFX application thread.
 */
public class OrderListPanelTest {
    @BeforeAll
    public static void startJavaFx() throws Exception {
        CompletableFuture<Void> started = new CompletableFuture<>();
        Runnable initialize = () -> {
            Platform.setImplicitExit(false);
            started.complete(null);
        };
        try {
            Platform.startup(initialize);
        } catch (IllegalStateException alreadyStarted) {
            Platform.runLater(initialize);
        }
        started.get(10, TimeUnit.SECONDS);
    }

    private static void onJavaFxThread(Runnable action) throws Exception {
        FutureTask<Void> task = new FutureTask<>(action, null);
        Platform.runLater(task);
        task.get(10, TimeUnit.SECONDS);
    }

    private static ClientOrder orderFor(Person client) {
        return ClientOrder.create(client, BANANA_MUFFIN, new Quantity(2), new Deadline("2026-10-15"));
    }

    @Test
    public void render_interleavedClients_showsGlobalAndClientLocalIndices() throws Exception {
        onJavaFxThread(() -> {
            RenderedPanel rendered = new RenderedPanel(orderFor(ALICE), orderFor(BOB), orderFor(ALICE));
            List<Node> cards = rendered.cards();
            assertEquals(3, cards.size());
            assertCard(cards.get(0), 1, ALICE, 1, "Incomplete");
            assertCard(cards.get(1), 2, BOB, 1, "Incomplete");
            assertCard(cards.get(2), 3, ALICE, 2, "Incomplete");
        });
    }

    @Test
    public void render_deleteEarlierOrder_renumbersBothIndices() throws Exception {
        onJavaFxThread(() -> {
            RenderedPanel rendered = new RenderedPanel(orderFor(ALICE), orderFor(BOB), orderFor(ALICE));
            rendered.cards();
            rendered.orders.remove(0);
            List<Node> cards = rendered.cards();
            assertEquals(2, cards.size());
            assertCard(cards.get(0), 1, BOB, 1, "Incomplete");
            assertCard(cards.get(1), 2, ALICE, 1, "Incomplete");
        });
    }

    @Test
    public void render_changeCompletion_updatesStatusWithoutRenumbering() throws Exception {
        onJavaFxThread(() -> {
            ClientOrder aliceOrder = orderFor(ALICE);
            RenderedPanel rendered = new RenderedPanel(aliceOrder, orderFor(BOB));
            rendered.cards();
            rendered.orders.set(0, aliceOrder.withCompletion(true));
            List<Node> cards = rendered.cards();
            assertCard(cards.get(0), 1, ALICE, 1, "Complete");
            assertCard(cards.get(1), 2, BOB, 1, "Incomplete");
            rendered.orders.set(0, aliceOrder);
            assertCard(rendered.cards().get(0), 1, ALICE, 1, "Incomplete");
        });
    }

    @Test
    public void render_addOrderAndClear_updatesCards() throws Exception {
        onJavaFxThread(() -> {
            RenderedPanel rendered = new RenderedPanel();
            assertEquals(0, rendered.cards().size());
            rendered.orders.add(orderFor(ALICE));
            assertCard(rendered.cards().get(0), 1, ALICE, 1, "Incomplete");
            rendered.orders.clear();
            assertEquals(0, rendered.cards().size());
        });
    }

    @Test
    public void updateItem_emptyOrNull_clearsReusedCell() throws Exception {
        onJavaFxThread(() -> {
            ClientOrder order = orderFor(ALICE);
            OrderListPanel panel = new OrderListPanel(FXCollections.observableArrayList(order));
            OrderListPanel.OrderListViewCell cell = panel.new OrderListViewCell();
            cell.updateItem(order, false);
            cell.updateItem(order, true);
            assertNull(cell.getGraphic());
            assertNull(cell.getText());
            cell.updateItem(order, false);
            cell.updateItem(null, false);
            assertNull(cell.getGraphic());
            assertNull(cell.getText());
        });
    }

    private static void assertCard(Node card, int row, Person client, int clientOrder, String status) {
        assertEquals(row + ". 2 x Banana Muffin", labelText(card, "item"));
        assertEquals("Client: " + client.getName() + " — Order " + clientOrder, labelText(card, "client"));
        assertEquals("Due: 2026-10-15", labelText(card, "deadline"));
        assertEquals(status, labelText(card, "status"));
    }

    private static String labelText(Node card, String id) {
        return ((Label) card.lookup("#" + id)).getText();
    }

    /**
     * Lays out actual list cells in a scene without opening a desktop window.
     */
    private static class RenderedPanel {
        private final ObservableList<ClientOrder> orders = FXCollections.observableArrayList();
        private final StackPane root;

        RenderedPanel(ClientOrder... initialOrders) {
            orders.addAll(initialOrders);
            OrderListPanel panel = new OrderListPanel(FXCollections.unmodifiableObservableList(orders));
            root = new StackPane(panel.getRoot());
            new Scene(root, 800, 800);
            root.resize(800, 800);
        }

        List<Node> cards() {
            root.applyCss();
            root.layout();
            return root.lookupAll(".list-cell").stream()
                    .filter(node -> node instanceof ListCell<?> cell && !cell.isEmpty())
                    .map(node -> (ListCell<?>) node)
                    .sorted(Comparator.comparingInt(ListCell::getIndex))
                    .map(ListCell::getGraphic)
                    .toList();
        }
    }
}
