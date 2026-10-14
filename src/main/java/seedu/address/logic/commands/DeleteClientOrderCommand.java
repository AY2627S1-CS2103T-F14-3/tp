package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ORDER_INDEX;

import java.util.Objects;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.order.ClientOrder;

/**
 * Deletes the selected client order.
 */
public class DeleteClientOrderCommand extends Command {
    public static final String COMMAND_WORD = "delete-order";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Requests to delete a client order. "
            + "Parameters: CONTACT_INDEX (positive integer) "
            + PREFIX_ORDER_INDEX + "ORDER_INDEX (positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1 o/2";
    public static final String MESSAGE_SUCCESS = "Order deleted: %1$s";

    private final Index clientIndex;
    private final Index orderIndex;

    /**
     * Creates a request targeting an order within the specified client's orders.
     * The client index refers to the displayed client list; the order index is local to that client.
     */
    public DeleteClientOrderCommand(Index clientIndex, Index orderIndex) {
        requireAllNonNull(clientIndex, orderIndex);
        this.clientIndex = clientIndex;
        this.orderIndex = orderIndex;
    }

    /**
     * Deletes the selected client order.
     *
     * @throws CommandException If the displayed client index or client-local order index is invalid.
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        ClientOrder order = OrderCommandUtil.findOrder(model, clientIndex, orderIndex);
        model.deleteOrder(order);
        return new CommandResult(String.format(MESSAGE_SUCCESS, order));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof DeleteClientOrderCommand otherCommand)) {
            return false;
        }
        return clientIndex.equals(otherCommand.clientIndex) && orderIndex.equals(otherCommand.orderIndex);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientIndex.getZeroBased(), orderIndex.getZeroBased());
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("clientIndex", clientIndex)
                .add("orderIndex", orderIndex)
                .toString();
    }
}
