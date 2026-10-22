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
            + "Example: " + COMMAND_WORD;
    public static final String MESSAGE_SUCCESS = "Listed completed orders.";

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredOrderList(ClientOrder::isComplete);
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
