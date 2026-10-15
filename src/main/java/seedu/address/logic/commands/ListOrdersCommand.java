package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.model.Model;

/**
 * Lists all orders without changing the displayed client or item lists.
 */
public class ListOrdersCommand extends Command {
    public static final String COMMAND_WORD = "list-orders";
    public static final String MESSAGE_SUCCESS = "Listed all orders.";

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
