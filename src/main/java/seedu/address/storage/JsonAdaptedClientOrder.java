package seedu.address.storage;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.bakeryitem.BakeryItem;
import seedu.address.model.order.ClientOrder;
import seedu.address.model.order.Deadline;
import seedu.address.model.order.Quantity;
import seedu.address.model.person.Person;

/**
 * Stores an order's identity and associations by unique names rather than transient displayed indices.
 */
class JsonAdaptedClientOrder {
    private final String id;
    private final String clientName;
    private final String itemName;
    private final String quantity;
    private final String deadline;
    private final Boolean complete;

    /**
     * Creates an adapted order from editable JSON fields.
     */
    @JsonCreator
    public JsonAdaptedClientOrder(@JsonProperty("id") String id, @JsonProperty("clientName") String clientName,
            @JsonProperty("itemName") String itemName, @JsonProperty("quantity") String quantity,
            @JsonProperty("deadline") String deadline, @JsonProperty("complete") Boolean complete) {
        this.id = id;
        this.clientName = clientName;
        this.itemName = itemName;
        this.quantity = quantity;
        this.deadline = deadline;
        this.complete = complete;
    }

    /**
     * Creates an adapted order from a validated model order.
     */
    public JsonAdaptedClientOrder(ClientOrder order) {
        id = order.getId().toString();
        clientName = order.getClient().getName().fullName;
        itemName = order.getItem().getItemName().getItemName();
        quantity = order.getQuantity().toString();
        deadline = order.getDeadline().toString();
        complete = order.isComplete();
    }

    /**
     * Returns a validated order linked to the loaded client and item collections.
     */
    public ClientOrder toModelType(ReadOnlyAddressBook book) throws IllegalValueException {
        if (id == null || clientName == null || itemName == null || complete == null) {
            throw new IllegalValueException("Order identity, client, item, or completion status is missing.");
        }
        UUID orderId;
        try {
            orderId = UUID.fromString(id);
            if (!orderId.toString().equalsIgnoreCase(id)) {
                throw new IllegalArgumentException("Invalid UUID format.");
            }
        } catch (IllegalArgumentException exception) {
            throw new IllegalValueException("Order identity must be a UUID.");
        }
        if (quantity == null || !Quantity.isValidQuantity(quantity)) {
            throw new IllegalValueException(Quantity.MESSAGE_CONSTRAINTS);
        }
        if (deadline == null || !Deadline.isValidDeadline(deadline)) {
            throw new IllegalValueException(Deadline.MESSAGE_CONSTRAINTS);
        }
        Person client = book.getPersonList().stream().filter(person -> person.getName().fullName.equals(clientName))
                .findFirst().orElseThrow(() -> new IllegalValueException("Order references a missing client."));
        BakeryItem item = book.getBakeryItemList().stream()
                .filter(bakeryItem -> bakeryItem.getItemName().getItemName().equals(itemName)).findFirst()
                .orElseThrow(() -> new IllegalValueException("Order references a missing bakery item."));
        return new ClientOrder(orderId, client, item, new Quantity(Integer.parseInt(quantity)),
                new Deadline(deadline), complete);
    }
}
