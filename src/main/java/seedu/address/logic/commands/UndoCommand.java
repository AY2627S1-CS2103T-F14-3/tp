package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyAddressBook;

/**
 * Restores the previous saved bakery book supplied by the logic layer.
 */
public class UndoCommand extends Command {
    public static final String COMMAND_WORD = "undo";
    public static final String MESSAGE_USAGE = "undo: Reverses the last saved data change. Usage: undo";
    public static final String MESSAGE_SUCCESS = "Last data change undone. All lists are shown.";
    public static final String MESSAGE_UNAVAILABLE = "There is no data change to undo.";

    private final ReadOnlyAddressBook previousData;

    /**
     * Creates a parsed undo request without a snapshot.
     */
    public UndoCommand() {
        this(null);
    }

    /**
     * Creates an undo command with the previous data, or null when undo is unavailable.
     */
    public UndoCommand(ReadOnlyAddressBook previousData) {
        this.previousData = previousData;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        if (previousData == null) {
            throw new CommandException(MESSAGE_UNAVAILABLE);
        }
        model.setAddressBook(previousData);
        model.updateFilteredPersonList(Model.PREDICATE_SHOW_ALL_PERSONS);
        model.updateFilteredBakeryItemList(Model.PREDICATE_SHOW_ALL_BAKERY_ITEMS);
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
