package seedu.address.model.order;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Objects;
import java.util.UUID;

import seedu.address.model.bakeryitem.BakeryItem;
import seedu.address.model.person.Person;

/**
 * Represents an immutable order with a stable identity and references to its client and bakery item.
 * Identical repeat orders have distinct identities so changing one never changes another.
 */
public final class ClientOrder {
    private final UUID id;
    private final Person client;
    private final BakeryItem item;
    private final Quantity quantity;
    private final Deadline deadline;
    private final boolean isComplete;

    /**
     * Creates an order with its persistent identity and validated details.
     */
    public ClientOrder(UUID id, Person client, BakeryItem item, Quantity quantity, Deadline deadline,
            boolean isComplete) {
        requireAllNonNull(id, client, item, quantity, deadline);
        this.id = id;
        this.client = client;
        this.item = item;
        this.quantity = quantity;
        this.deadline = deadline;
        this.isComplete = isComplete;
    }

    /**
     * Creates a new incomplete order, including when an identical order already exists.
     */
    public static ClientOrder create(Person client, BakeryItem item, Quantity quantity, Deadline deadline) {
        return new ClientOrder(UUID.randomUUID(), client, item, quantity, deadline, false);
    }

    public UUID getId() {
        return id;
    }

    public Person getClient() {
        return client;
    }

    public BakeryItem getItem() {
        return item;
    }

    public Quantity getQuantity() {
        return quantity;
    }

    public Deadline getDeadline() {
        return deadline;
    }

    /**
     * Returns whether this order is complete.
     */
    public boolean isComplete() {
        return isComplete;
    }

    /**
     * Returns a copy with the requested completion status and the same identity.
     */
    public ClientOrder withCompletion(boolean isComplete) {
        return new ClientOrder(id, client, item, quantity, deadline, isComplete);
    }

    /**
     * Returns a copy associated with an edited client without changing the order identity or details.
     */
    public ClientOrder withClient(Person editedClient) {
        return new ClientOrder(id, editedClient, item, quantity, deadline, isComplete);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof ClientOrder order && id.equals(order.id) && client.equals(order.client)
                && item.equals(order.item) && quantity.equals(order.quantity) && deadline.equals(order.deadline)
                && isComplete == order.isComplete;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, client, item, quantity, deadline, isComplete);
    }

    @Override
    public String toString() {
        return client.getName() + ": " + quantity + " x " + item.getItemName() + " (Due: " + deadline + "; "
                + (isComplete ? "complete" : "incomplete") + ")";
    }
}
