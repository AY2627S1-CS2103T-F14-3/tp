package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_BAKERY_ITEM_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PRICE;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_BAKERY_ITEMS;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.bakeryitem.BakeryItem;
import seedu.address.model.bakeryitem.BakeryItemName;
import seedu.address.model.bakeryitem.Price;

/**
 * Edits the details of an existing bakery item.
 */
public class EditBakeryItemCommand extends Command {

    public static final String COMMAND_WORD = "edit-item";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the details of the bakery item identified "
            + "by the index number used in the displayed bakery item list. "
            + "Existing values will be overwritten by the input values.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "[" + PREFIX_BAKERY_ITEM_NAME + "ITEM_NAME] "
            + "[" + PREFIX_PRICE + "PRICE]\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_BAKERY_ITEM_NAME + "Chocolate Muffin "
            + PREFIX_PRICE + "2.50";

    public static final String MESSAGE_EDIT_BAKERY_ITEM_SUCCESS = "Edited bakery item: %1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_DUPLICATE_BAKERY_ITEM = "This bakery item already exists.";

    private final Index index;
    private final EditBakeryItemDescriptor editBakeryItemDescriptor;

    /**
     * Creates an {@code EditBakeryItemCommand} to edit the item at {@code index}.
     */
    public EditBakeryItemCommand(Index index, EditBakeryItemDescriptor editBakeryItemDescriptor) {
        requireNonNull(index);
        requireNonNull(editBakeryItemDescriptor);

        this.index = index;
        this.editBakeryItemDescriptor = new EditBakeryItemDescriptor(editBakeryItemDescriptor);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<BakeryItem> lastShownList = model.getFilteredBakeryItemList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_BAKERY_ITEM_DISPLAYED_INDEX);
        }

        BakeryItem bakeryItemToEdit = lastShownList.get(index.getZeroBased());
        BakeryItem editedBakeryItem = createEditedBakeryItem(bakeryItemToEdit, editBakeryItemDescriptor);

        if (!bakeryItemToEdit.isSameBakeryItem(editedBakeryItem) && model.hasBakeryItem(editedBakeryItem)) {
            throw new CommandException(MESSAGE_DUPLICATE_BAKERY_ITEM);
        }

        model.setBakeryItem(bakeryItemToEdit, editedBakeryItem);
        model.updateFilteredBakeryItemList(PREDICATE_SHOW_ALL_BAKERY_ITEMS);
        return new CommandResult(String.format(MESSAGE_EDIT_BAKERY_ITEM_SUCCESS,
                Messages.format(editedBakeryItem)));
    }

    /**
     * Creates a bakery item using the current details and any supplied replacement details.
     */
    private static BakeryItem createEditedBakeryItem(BakeryItem bakeryItemToEdit,
            EditBakeryItemDescriptor editBakeryItemDescriptor) {
        assert bakeryItemToEdit != null;

        BakeryItemName updatedName = editBakeryItemDescriptor.getItemName()
                .orElse(bakeryItemToEdit.getItemName());
        Price updatedPrice = editBakeryItemDescriptor.getPrice().orElse(bakeryItemToEdit.getPrice());

        return new BakeryItem(updatedName, updatedPrice);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditBakeryItemCommand otherEditBakeryItemCommand)) {
            return false;
        }

        return index.equals(otherEditBakeryItemCommand.index)
                && editBakeryItemDescriptor.equals(otherEditBakeryItemCommand.editBakeryItemDescriptor);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("editBakeryItemDescriptor", editBakeryItemDescriptor)
                .toString();
    }

    /**
     * Stores the details to edit a bakery item with.
     */
    public static class EditBakeryItemDescriptor {
        private BakeryItemName itemName;
        private Price price;

        /**
         * Creates an empty {@code EditBakeryItemDescriptor}.
         */
        public EditBakeryItemDescriptor() {}

        /**
         * Creates a defensive copy of {@code toCopy}.
         */
        public EditBakeryItemDescriptor(EditBakeryItemDescriptor toCopy) {
            requireNonNull(toCopy);
            setItemName(toCopy.itemName);
            setPrice(toCopy.price);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(itemName, price);
        }

        public void setItemName(BakeryItemName itemName) {
            this.itemName = itemName;
        }

        public Optional<BakeryItemName> getItemName() {
            return Optional.ofNullable(itemName);
        }

        public void setPrice(Price price) {
            this.price = price;
        }

        public Optional<Price> getPrice() {
            return Optional.ofNullable(price);
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditBakeryItemDescriptor otherEditBakeryItemDescriptor)) {
                return false;
            }

            return Objects.equals(itemName, otherEditBakeryItemDescriptor.itemName)
                    && Objects.equals(price, otherEditBakeryItemDescriptor.price);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .add("itemName", itemName)
                    .add("price", price)
                    .toString();
        }
    }
}
