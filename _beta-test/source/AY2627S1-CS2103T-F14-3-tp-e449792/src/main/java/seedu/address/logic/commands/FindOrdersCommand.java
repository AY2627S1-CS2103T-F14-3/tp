package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.Model;
import seedu.address.model.order.OrderContainsKeywordsPredicate;

/**
 * Finds and lists all orders in the address book whose name contains any of the argument keywords.
 * Keyword matching is case insensitive.
 */
public class FindOrdersCommand extends Command {

    public static final String COMMAND_WORD = "find-orders";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Finds all orders whose client or item names contain any of "
            + "the specified keywords (case-insensitive) and displays them as a list with index numbers.\n"
            + "Parameters: KEYWORD [MORE_KEYWORDS]\n"
            + "Example: " + COMMAND_WORD + " banana chocolate";

    public static final String MESSAGE_SUCCESS = "%1$d orders listed!";

    private final OrderContainsKeywordsPredicate predicate;

    public FindOrdersCommand(OrderContainsKeywordsPredicate predicate) {
        this.predicate = predicate;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredOrderList(predicate);
        return new CommandResult(
                String.format(MESSAGE_SUCCESS, model.getFilteredOrderList().size()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof FindOrdersCommand otherFindOrdersCommand)) {
            return false;
        }

        return predicate.equals(otherFindOrdersCommand.predicate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("predicate", predicate)
                .toString();
    }
}
