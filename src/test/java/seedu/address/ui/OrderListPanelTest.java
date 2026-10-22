package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalBakeryItems.BANANA_MUFFIN;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BOB;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
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
import javafx.collections.transformation.FilteredList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
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
    public void render_deadlineAndCompletion_applyReadableStatusColors() throws Exception {
        onJavaFxThread(() -> {
            LocalDate today = LocalDate.of(2026, 10, 22);
            for (int days : new int[] {-1, 0, 1}) {
                ClientOrder order = ClientOrder.create(ALICE, BANANA_MUFFIN, new Quantity(2),
                        new Deadline(today.plusDays(days).toString()));
                for (boolean complete : new boolean[] {false, true}) {
                    Region card = new OrderCard(order.withCompletion(complete), 1, 1, 1, today).getRoot();
                    StackPane root = new StackPane(card);
                    Scene scene = new Scene(root);
                    scene.getStylesheets().add(OrderCard.class.getResource("/view/DarkTheme.css").toExternalForm());
                    root.applyCss();
                    Color expected = Color.web(complete ? "#24543a" : days < 0 ? "#7a2929" : "#3c3e3f");
                    assertEquals(expected, card.getBackground().getFills().get(0).getFill());
                    assertEquals(complete ? "Complete" : days < 0 ? "Incomplete (overdue)" : "Incomplete",
                            labelText(card, "status"));
                    assertEquals("Due: " + today.plusDays(days), labelText(card, "deadline"));
                    assertEquals(Color.WHITE, ((Label) card.lookup("#status")).getTextFill());
                }
            }
        });
    }

    @Test
    public void render_statusColors_alternateWithDisplayedRowsAndRenumberAfterDeletion() throws Exception {
        onJavaFxThread(() -> {
            for (int days : new int[] {-1, 0}) {
                for (boolean complete : new boolean[] {false, true}) {
                    Clock clock = Clock.fixed(Instant.parse("2026-10-22T12:00:00Z"), ZoneOffset.UTC);
                    ClientOrder first = ClientOrder.create(ALICE, BANANA_MUFFIN, new Quantity(2),
                            new Deadline(LocalDate.now(clock).plusDays(days).toString())).withCompletion(complete);
                    ClientOrder second = ClientOrder.create(BOB, BANANA_MUFFIN, new Quantity(2),
                            first.getDeadline()).withCompletion(complete);
                    RenderedPanel rendered = new RenderedPanel(clock, first, second);
                    rendered.root.getScene().getStylesheets()
                            .add(OrderCard.class.getResource("/view/DarkTheme.css").toExternalForm());
                    List<Node> cards = rendered.cards();
                    Color darker = Color.web(complete ? "#24543a" : days < 0 ? "#7a2929" : "#3c3e3f");
                    Color lighter = Color.web(complete ? "#306a4b" : days < 0 ? "#913a3a" : "#515658");
                    assertEquals(darker, ((Region) cards.get(0)).getBackground().getFills().get(0).getFill());
                    assertEquals(lighter, ((Region) cards.get(1)).getBackground().getFills().get(0).getFill());
                    assertEquals(Color.WHITE, ((Label) cards.get(1).lookup("#status")).getTextFill());
                    rendered.orders.remove(first);
                    Node remaining = rendered.cards().get(0);
                    assertEquals(darker, ((Region) remaining).getBackground().getFills().get(0).getFill());
                    rendered.panel.dispose();
                }
            }
        });
    }

    @Test
    public void render_dateChanges_refreshesExistingCards() throws Exception {
        onJavaFxThread(() -> {
            MutableClock clock = new MutableClock();
            ClientOrder incomplete = orderFor(ALICE);
            RenderedPanel rendered = new RenderedPanel(clock, incomplete, orderFor(BOB).withCompletion(true));
            assertTrue(rendered.cards().get(0).getStyleClass().contains("order-incomplete"));
            clock.instant = clock.instant.plusSeconds(24 * 60 * 60);
            rendered.panel.refreshDate();
            List<Node> cards = rendered.cards();
            assertTrue(cards.get(0).getStyleClass().contains("order-overdue"));
            assertEquals("Incomplete (overdue)", labelText(cards.get(0), "status"));
            assertTrue(cards.get(1).getStyleClass().contains("order-complete"));
            rendered.panel.dispose();
        });
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
    public void render_filterClients_updatesClientNumbersAndOmitsHiddenNumbers() throws Exception {
        onJavaFxThread(() -> {
            RenderedPanel rendered = new RenderedPanel(orderFor(ALICE), orderFor(BOB), orderFor(ALICE));
            rendered.cards();
            rendered.filteredClients.setPredicate(person -> person.equals(BOB));
            List<Node> cards = rendered.cards();
            assertEquals(3, cards.size());
            assertCard(cards.get(0), 1, ALICE, 1, "Incomplete", 0);
            assertCard(cards.get(1), 2, BOB, 1, "Incomplete", 1);
            assertCard(cards.get(2), 3, ALICE, 2, "Incomplete", 0);
            rendered.filteredClients.setPredicate(person -> true);
            cards = rendered.cards();
            assertCard(cards.get(0), 1, ALICE, 1, "Incomplete");
            assertCard(cards.get(1), 2, BOB, 1, "Incomplete");
            assertCard(cards.get(2), 3, ALICE, 2, "Incomplete");
        });
    }

    @Test
    public void render_reorderClients_updatesNumbersWithoutReorderingOrders() throws Exception {
        onJavaFxThread(() -> {
            RenderedPanel rendered = new RenderedPanel(orderFor(ALICE), orderFor(BOB));
            rendered.cards();
            rendered.clients.setAll(BOB, ALICE);
            List<Node> cards = rendered.cards();
            assertCard(cards.get(0), 1, ALICE, 1, "Incomplete", 2);
            assertCard(cards.get(1), 2, BOB, 1, "Incomplete", 1);
        });
    }

    @Test
    public void updateItem_emptyOrNull_clearsReusedCell() throws Exception {
        onJavaFxThread(() -> {
            ClientOrder order = orderFor(ALICE);
            OrderListPanel panel = new OrderListPanel(FXCollections.observableArrayList(order),
                    FXCollections.observableArrayList(ALICE));
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
        assertCard(card, row, client, clientOrder, status, client.equals(ALICE) ? 1 : 2);
    }

    private static void assertCard(Node card, int row, Person client, int clientOrder, String status, int clientIndex) {
        assertEquals(row + ". 2 x Banana Muffin", labelText(card, "item"));
        String clientLabel = clientIndex > 0 ? "Client " + clientIndex : "Client";
        assertEquals(clientLabel + ": " + client.getName() + " — Order " + clientOrder, labelText(card, "client"));
        assertEquals("Due: 2026-10-15", labelText(card, "deadline"));
        boolean overdue = LocalDate.parse("2026-10-15").isBefore(LocalDate.now());
        String expectedStatus = status.equals("Incomplete") && overdue ? "Incomplete (overdue)" : status;
        assertEquals(expectedStatus, labelText(card, "status"));
        String style = status.equals("Complete") ? "order-complete" : overdue ? "order-overdue" : "order-incomplete";
        assertTrue(card.getStyleClass().contains(style));
    }

    private static String labelText(Node card, String id) {
        return ((Label) card.lookup("#" + id)).getText();
    }

    /**
     * Lays out actual list cells in a scene without opening a desktop window.
     */
    private static class RenderedPanel {
        private final ObservableList<ClientOrder> orders = FXCollections.observableArrayList();
        private final ObservableList<Person> clients = FXCollections.observableArrayList(ALICE, BOB);
        private final FilteredList<Person> filteredClients = new FilteredList<>(clients);
        private final StackPane root;
        private final OrderListPanel panel;

        RenderedPanel(ClientOrder... initialOrders) {
            this(Clock.systemDefaultZone(), initialOrders);
        }

        RenderedPanel(Clock clock, ClientOrder... initialOrders) {
            orders.addAll(initialOrders);
            panel = new OrderListPanel(FXCollections.unmodifiableObservableList(orders), filteredClients, clock);
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

    private static class MutableClock extends Clock {
        private Instant instant = Instant.parse("2026-10-15T12:00:00Z");

        @Override
        public ZoneId getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return Clock.fixed(instant, zone);
        }

        @Override
        public Instant instant() {
            return instant;
        }
    }
}
