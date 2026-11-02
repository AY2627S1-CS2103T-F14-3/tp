package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.model.Model;
import seedu.address.model.order.ClientOrder;

/**
 * Lists completed orders without changing the displayed client or item lists.
 */
public class ListCompleteOrdersCommand extends Command {
    public static final String COMMAND_WORD = "list-complete-orders";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Lists completed orders.\n"
            + "Parameters: [by-deadline]\nExample: " + COMMAND_WORD + " by-deadline";
    public static final String MESSAGE_SUCCESS = "Listed completed orders.";

    private final boolean byDeadline;

    public ListCompleteOrdersCommand() {
        this(false);
    }

    /** Creates a completed-order listing that optionally sorts by earliest deadline. */
    public ListCompleteOrdersCommand(boolean byDeadline) {
        this.byDeadline = byDeadline;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredOrderList(ClientOrder::isComplete, byDeadline ? Model.ORDER_BY_DEADLINE : null);
        return new CommandResult(byDeadline
                ? "Listed completed orders by deadline (earliest first)." : MESSAGE_SUCCESS);
    }
}
