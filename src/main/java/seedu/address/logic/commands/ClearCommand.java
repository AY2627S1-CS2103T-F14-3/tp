package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;

/**
 * Clears all data only when explicitly confirmed.
 */
public class ClearCommand extends Command {

    public static final String COMMAND_WORD = "clear";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Deletes all data only with confirmation.\n"
            + "Usage: clear [confirm]\nExample: clear confirm";
    public static final String MESSAGE_CONFIRMATION = "No data was deleted. Clearing removes ALL clients, "
            + "bakery items, and orders. Type clear confirm to proceed, or continue with another command to cancel.";
    public static final String MESSAGE_SUCCESS = "Address book has been cleared!";

    private final boolean confirmed;

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
            throw new CommandException(MESSAGE_CONFIRMATION);
        }
        model.setAddressBook(new AddressBook());
        model.updateFilteredPersonList(Model.PREDICATE_SHOW_ALL_PERSONS);
        model.updateFilteredBakeryItemList(Model.PREDICATE_SHOW_ALL_BAKERY_ITEMS);
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
