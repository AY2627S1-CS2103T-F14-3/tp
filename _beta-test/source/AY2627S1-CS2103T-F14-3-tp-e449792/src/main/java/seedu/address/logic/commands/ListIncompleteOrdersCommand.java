package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.model.Model;

/**
 * Lists incomplete orders without changing the displayed client or item lists.
 */
public class ListIncompleteOrdersCommand extends Command {
    public static final String COMMAND_WORD = "list-incomplete-orders";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Lists incomplete orders.\n"
            + "Parameters: [by-deadline]\nExample: " + COMMAND_WORD + " by-deadline";
    public static final String MESSAGE_SUCCESS = "Listed incomplete orders.";

    private final boolean byDeadline;

    public ListIncompleteOrdersCommand() {
        this(false);
    }

    /** Creates an incomplete-order listing that optionally sorts by earliest deadline. */
    public ListIncompleteOrdersCommand(boolean byDeadline) {
        this.byDeadline = byDeadline;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredOrderList(order -> !order.isComplete(), byDeadline ? Model.ORDER_BY_DEADLINE : null);
        return new CommandResult(byDeadline
                ? "Listed incomplete orders by deadline (earliest first)." : MESSAGE_SUCCESS);
    }
}
