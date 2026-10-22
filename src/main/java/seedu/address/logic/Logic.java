package seedu.address.logic;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.bakeryitem.BakeryItem;
import seedu.address.model.order.ClientOrder;
import seedu.address.model.person.Person;

/**
 * API of the Logic component
 */
public interface Logic {
    /**
     * Returns whether the last saved data change can be undone.
     */
    ReadOnlyBooleanProperty undoAvailableProperty();

    /**
     * Executes the command and returns the result.
     * @param commandText The command as entered by the user.
     * @return the result of the command execution.
     * @throws CommandException If an error occurs during command execution.
     * @throws ParseException If an error occurs during parsing.
     */
    CommandResult execute(String commandText) throws CommandException, ParseException;

    /** Returns an unmodifiable view of the filtered list of persons */
    ObservableList<Person> getFilteredPersonList();

    /**
     * Returns the user prefs' GUI settings.
     */
    GuiSettings getGuiSettings();

    /**
     * Returns an unmodifiable observable list of displayed bakery items for the UI.
     */
    ObservableList<BakeryItem> getFilteredBakeryItemList();

    /**
     * Returns an unmodifiable observable list of all orders in insertion order for the UI.
     */
    ObservableList<ClientOrder> getOrderList();

    /**
     * Set the user prefs' GUI settings.
     */
    void setGuiSettings(GuiSettings guiSettings);
}
