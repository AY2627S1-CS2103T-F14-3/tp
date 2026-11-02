package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Objects;
import java.util.Optional;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.order.ClientOrder;
import seedu.address.model.order.Deadline;
import seedu.address.model.order.Quantity;

/**
 * Edits selected order details while retaining its identity, owner, completion status, and insertion position.
 */
public class EditClientOrderCommand extends Command {
    public static final String COMMAND_WORD = "edit-order";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits an existing client order. "
            + "Parameters: CONTACT_INDEX (positive integer) o/ORDER_INDEX (positive integer) "
            + "[i/ITEM_INDEX (positive integer)] [q/QUANTITY (1 to 9,999)] [d/DEADLINE (YYYY-MM-DD)]\n"
            + "Provide at least one of i/, q/, or d/. Omitted fields retain their values.\n"
            + "Example: " + COMMAND_WORD + " 1 o/2 q/13";
    public static final String MESSAGE_SUCCESS = "Order edited: %1$s";
    public static final String MESSAGE_NOT_EDITED = "Provide at least one of i/, q/, or d/ to edit an order.";
    public static final String MESSAGE_NO_CHANGE = "Order details are unchanged: %1$s";

    private final Index clientIndex;
    private final Index orderIndex;
    private final Optional<Index> itemIndex;
    private final Optional<Quantity> quantity;
    private final Optional<Deadline> deadline;

    /**
     * Creates an edit request with displayed client/item indices and a client-local order index.
     *
     * @throws IllegalArgumentException If no editable field is supplied.
     */
    public EditClientOrderCommand(Index clientIndex, Index orderIndex, Optional<Index> itemIndex,
            Optional<Quantity> quantity, Optional<Deadline> deadline) {
        requireAllNonNull(clientIndex, orderIndex, itemIndex, quantity, deadline);
        if (itemIndex.isEmpty() && quantity.isEmpty() && deadline.isEmpty()) {
            throw new IllegalArgumentException(MESSAGE_NOT_EDITED);
        }
        this.clientIndex = clientIndex;
        this.orderIndex = orderIndex;
        this.itemIndex = itemIndex;
        this.quantity = quantity;
        this.deadline = deadline;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        ClientOrder original = OrderCommandUtil.findOrder(model, clientIndex, orderIndex);
        if (itemIndex.isPresent()
                && itemIndex.get().getZeroBased() >= model.getFilteredBakeryItemList().size()) {
            throw new CommandException(AddClientOrderCommand.MESSAGE_INVALID_ITEM);
        }
        ClientOrder edited = new ClientOrder(original.getId(), original.getClient(),
                itemIndex.map(index -> model.getFilteredBakeryItemList().get(index.getZeroBased()))
                        .orElse(original.getItem()),
                quantity.orElse(original.getQuantity()), deadline.orElse(original.getDeadline()),
                original.isComplete());
        if (original.equals(edited)) {
            return new CommandResult(String.format(MESSAGE_NO_CHANGE, original));
        }
        model.setOrder(original, edited);
        return new CommandResult(String.format(MESSAGE_SUCCESS, edited));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof EditClientOrderCommand command)) {
            return false;
        }
        return clientIndex.equals(command.clientIndex) && orderIndex.equals(command.orderIndex)
                && itemIndex.equals(command.itemIndex) && quantity.equals(command.quantity)
                && deadline.equals(command.deadline);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientIndex.getZeroBased(), orderIndex.getZeroBased(),
                itemIndex.map(Index::getZeroBased), quantity, deadline);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("clientIndex", clientIndex).add("orderIndex", orderIndex)
                .add("itemIndex", itemIndex).add("quantity", quantity).add("deadline", deadline).toString();
    }
}
