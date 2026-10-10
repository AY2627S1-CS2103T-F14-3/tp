package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_BAKERY_ITEM_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PRICE;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.bakeryitem.BakeryItem;

/**
 * Adds a bakery item to the address book.
 */
public class AddBakeryItemCommand extends Command {
    public static final String COMMAND_WORD = "add-item";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a bakery item to the address book. "
            + "Parameters: "
            + PREFIX_BAKERY_ITEM_NAME + "ITEM_NAME "
            + PREFIX_PRICE + "PRICE "
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_BAKERY_ITEM_NAME + "Banana Muffin "
            + PREFIX_PRICE + "0.99 ";

    public static final String MESSAGE_SUCCESS = "New bakery item added: %1$s";
    public static final String MESSAGE_DUPLICATE_ITEM = "This bakery item already exists.";

    private final BakeryItem toAdd;

    /**
     * Creates a new {@code BakeryItemCommand}.
     *
     * @param item The bakery item to be added.
     */
    public AddBakeryItemCommand(BakeryItem item) {
        requireNonNull(item);
        this.toAdd = item;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        System.out.println(model);
        System.out.println(toAdd);
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'execute'");
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddBakeryItemCommand otherAddCommand)) {
            return false;
        }

        return toAdd.equals(otherAddCommand.toAdd);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("toAdd", toAdd)
                .toString();
    }
}
