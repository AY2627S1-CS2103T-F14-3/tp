package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyAddressBook;

/**
 * Clears all data only when explicitly confirmed.
 */
public class ClearCommand extends Command {

    public static final String COMMAND_WORD = "clear";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Shows the deletion warning and record counts.\n"
            + "clear confirm deletes all clients, items, and orders immediately. No additional prompt is shown.\n"
            + "Usage: clear [confirm]\nExample: clear confirm";
    public static final String MESSAGE_CONFIRMATION_FORMAT = "No data was deleted. "
            + "clear confirm deletes all clients, items, and orders immediately.\n"
            + "This includes %d clients, %d items, and %d orders (including hidden records).\n"
            + "No additional prompt will be shown. Type clear confirm to proceed, "
            + "or continue with another command to cancel.";
    public static final String MESSAGE_SUCCESS = "Address book has been cleared!";

    private final boolean confirmed;
    private final Logger logger = LogsCenter.getLogger(ClearCommand.class);

    /**
     * Creates a clear request that requires explicit confirmation.
     */
    public ClearCommand() {
        this(false);
    }

    /**
     * Creates a clear request with the supplied confirmation state.
     */
    public ClearCommand(boolean confirmed) {
        this.confirmed = confirmed;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        if (!confirmed) {
            logger.info("Clear blocked: explicit confirmation is required; data remains unchanged.");
            ReadOnlyAddressBook book = model.getAddressBook();
            throw new CommandException(String.format(MESSAGE_CONFIRMATION_FORMAT,
                    book.getPersonList().size(), book.getBakeryItemList().size(), book.getOrderList().size()));
        }
        model.setAddressBook(new AddressBook());
        model.updateFilteredPersonList(Model.PREDICATE_SHOW_ALL_PERSONS);
        model.updateFilteredBakeryItemList(Model.PREDICATE_SHOW_ALL_BAKERY_ITEMS);
        logger.fine("Confirmed clear applied in memory; filters reset; awaiting persistence.");
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
