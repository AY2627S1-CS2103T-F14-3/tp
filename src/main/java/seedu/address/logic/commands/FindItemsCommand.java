package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.Model;
import seedu.address.model.bakeryitem.ItemNameContainsKeywordsPredicate;

/**
 * Finds and lists all bakery items in the address book whose name contains any of the argument keywords.
 * Keyword matching is case insensitive.
 */
public class FindItemsCommand extends Command {

    public static final String COMMAND_WORD = "find-items";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Finds all bakery items whose names contain any of "
            + "the specified keywords (case-insensitive) and displays them as a list with index numbers.\n"
            + "Parameters: KEYWORD [MORE_KEYWORDS]\n"
            + "Example: " + COMMAND_WORD + " banana chocolate";

    public static final String MESSAGE_SUCCESS = "%1$d bakery items listed!";

    private final ItemNameContainsKeywordsPredicate predicate;

    public FindItemsCommand(ItemNameContainsKeywordsPredicate predicate) {
        this.predicate = predicate;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredBakeryItemList(predicate);
        return new CommandResult(
                String.format(MESSAGE_SUCCESS, model.getFilteredBakeryItemList().size()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof FindItemsCommand otherFindItemsCommand)) {
            return false;
        }

        return predicate.equals(otherFindItemsCommand.predicate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("predicate", predicate)
                .toString();
    }
}
