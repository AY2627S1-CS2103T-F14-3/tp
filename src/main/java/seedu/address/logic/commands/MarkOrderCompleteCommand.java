package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ORDER_INDEX;

import java.util.Objects;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;

/**
 * Represents a parsed request to mark a client order as complete.
 * Execution is deferred until order model and storage integration are available.
 */
public class MarkOrderCompleteCommand extends Command {
    public static final String COMMAND_WORD = "mark-order-complete";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Requests to mark a client order as complete. "
            + "Parameters: CONTACT_INDEX (positive integer) "
            + PREFIX_ORDER_INDEX + "ORDER_INDEX (positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1 o/2";
    public static final String MESSAGE_NOT_IMPLEMENTED =
            "Marking orders as complete is not available yet. No order status has been changed.";

    private final Index clientIndex;
    private final Index orderIndex;

    /**
     * Creates a request targeting an order within the specified client's orders.
     * The client index refers to the displayed client list; existence checks are deferred to execution integration.
     */
    public MarkOrderCompleteCommand(Index clientIndex, Index orderIndex) {
        requireAllNonNull(clientIndex, orderIndex);
        this.clientIndex = clientIndex;
        this.orderIndex = orderIndex;
    }

    /**
     * Reports that execution is unavailable in this parsing increment without modifying the model.
     *
     * @throws CommandException Always, until model and storage integration are implemented.
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        throw new CommandException(MESSAGE_NOT_IMPLEMENTED);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof MarkOrderCompleteCommand otherCommand)) {
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
