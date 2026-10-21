package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_BAKERY_ITEMS;

import seedu.address.model.Model;

/**
 * Lists all bakery items to the user.
 */
public class ListItemsCommand extends Command {
    public static final String COMMAND_WORD = "list-items";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Lists all bakery items.\n"
            + "Example: " + COMMAND_WORD;
    public static final String MESSAGE_SUCCESS = "Listed all bakery items.";

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredBakeryItemList(PREDICATE_SHOW_ALL_BAKERY_ITEMS);
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
