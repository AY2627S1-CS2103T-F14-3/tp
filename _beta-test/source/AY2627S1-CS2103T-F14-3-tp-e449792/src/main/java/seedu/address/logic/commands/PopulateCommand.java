package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_BAKERY_ITEMS;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import seedu.address.model.Model;
import seedu.address.model.util.SampleDataUtil;

/**
 * Replaces all current data with the default sample data.
 */
public class PopulateCommand extends Command {
    public static final String COMMAND_WORD = "populate";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Replaces all current data with sample data.\n"
            + "Example: " + COMMAND_WORD;
    public static final String MESSAGE_SUCCESS = "Replaced all clients, bakery items, and orders with sample data.";

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.setAddressBook(SampleDataUtil.getSampleAddressBook());
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        model.updateFilteredBakeryItemList(PREDICATE_SHOW_ALL_BAKERY_ITEMS);
        model.updateFilteredOrderList(order -> true);
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
