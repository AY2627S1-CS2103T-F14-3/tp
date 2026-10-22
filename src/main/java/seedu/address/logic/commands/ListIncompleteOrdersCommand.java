package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.model.Model;

/**
 * Lists incomplete orders without changing the displayed client or item lists.
 */
public class ListIncompleteOrdersCommand extends Command {
    public static final String COMMAND_WORD = "list-incomplete-orders";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Lists incomplete orders.\n"
            + "Example: " + COMMAND_WORD;
    public static final String MESSAGE_SUCCESS = "Listed incomplete orders.";

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredOrderList(order -> !order.isComplete());
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
