package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.bakeryitem.BakeryItem;

/**
 * Deletes a bakery item identified using its displayed index from the address book.
 */
public class DeleteBakeryItemCommand extends Command {

    public static final String COMMAND_WORD = "delete-item";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the bakery item identified by the index number used in the displayed bakery items list.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";
    public static final String MESSAGE_DELETE_BAKERY_ITEM_SUCCESS = "Deleted bakery item: %1$s";

    private final Index targetIndex;

    public DeleteBakeryItemCommand(Index targetIndex) {
        this.targetIndex = targetIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<BakeryItem> lastShownList = model.getFilteredBakeryItemList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_BAKERY_ITEM_DISPLAYED_INDEX);
        }

        BakeryItem itemToDelete = lastShownList.get(targetIndex.getZeroBased());
        if (model.getAddressBook().getOrderList().stream().anyMatch(order -> order.getItem().equals(itemToDelete))) {
            throw new CommandException(AddressBook.MESSAGE_ITEM_HAS_ORDERS);
        }
        model.deleteBakeryItem(itemToDelete);
        return new CommandResult(String.format(MESSAGE_DELETE_BAKERY_ITEM_SUCCESS, Messages.format(itemToDelete)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof DeleteBakeryItemCommand otherDeleteCommand)) {
            return false;
        }

        return targetIndex.equals(otherDeleteCommand.targetIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .toString();
    }
}
