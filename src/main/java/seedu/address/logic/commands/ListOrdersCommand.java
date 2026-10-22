package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.model.Model;

/**
 * Lists all orders without changing the displayed client or item lists.
 */
public class ListOrdersCommand extends Command {
    public static final String COMMAND_WORD = "list-orders";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Lists all orders.\n"
            + "Parameters: [by-deadline]\nExample: " + COMMAND_WORD + " by-deadline";
    public static final String MESSAGE_SUCCESS = "Listed all orders.";

    private final boolean byDeadline;

    public ListOrdersCommand() {
        this(false);
    }

    /** Creates a listing that optionally sorts by earliest deadline. */
    public ListOrdersCommand(boolean byDeadline) {
        this.byDeadline = byDeadline;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredOrderList(order -> true, byDeadline ? Model.ORDER_BY_DEADLINE : null);
        return new CommandResult(byDeadline ? "Listed all orders by deadline (earliest first)." : MESSAGE_SUCCESS);
    }
}
