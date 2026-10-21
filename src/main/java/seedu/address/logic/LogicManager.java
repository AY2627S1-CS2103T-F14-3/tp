package seedu.address.logic;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.UndoCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.bakeryitem.BakeryItem;
import seedu.address.model.order.ClientOrder;
import seedu.address.model.person.Person;
import seedu.address.storage.Storage;

/**
 * The main LogicManager of the app.
 */
public class LogicManager implements Logic {
    public static final String FILE_OPS_ERROR_FORMAT = "Could not save data due to the following error: %s";

    public static final String FILE_OPS_PERMISSION_ERROR_FORMAT =
            "Could not save data to file %s due to insufficient permissions to write to the file or the folder.";

    private final Logger logger = LogsCenter.getLogger(LogicManager.class);

    private final Model model;
    private final Storage storage;
    private final AddressBookParser addressBookParser;
    private final ReadOnlyBooleanWrapper undoAvailable = new ReadOnlyBooleanWrapper(false);
    private AddressBook undoSnapshot;

    /**
     * Constructs a {@code LogicManager} with the given {@code Model} and {@code Storage}.
     */
    public LogicManager(Model model, Storage storage) {
        this.model = model;
        this.storage = storage;
        addressBookParser = new AddressBookParser();
    }

    /**
     * Restores data and displayed indices when persistence fails.
     */
    private void restoreAfterSaveFailure(AddressBook beforeCommand, Predicate<Person> clients,
            Predicate<BakeryItem> items) {
        model.setAddressBook(beforeCommand);
        model.updateFilteredPersonList(clients);
        model.updateFilteredBakeryItemList(items);
    }

    @Override
    public CommandResult execute(String commandText) throws CommandException, ParseException {
        logger.info("----------------[USER COMMAND][" + commandText + "]");

        CommandResult commandResult;
        Command command = addressBookParser.parseCommand(commandText);
        boolean isUndo = command instanceof UndoCommand;
        if (isUndo) {
            command = new UndoCommand(undoSnapshot);
        }
        AddressBook beforeCommand = new AddressBook(model.getAddressBook());
        Predicate<Person> displayedClients = model.getPersonFilter();
        Predicate<BakeryItem> displayedItems = model.getBakeryItemFilter();
        commandResult = command.execute(model);

        try {
            storage.saveAddressBook(model.getAddressBook());
        } catch (AccessDeniedException e) {
            restoreAfterSaveFailure(beforeCommand, displayedClients, displayedItems);
            if (command instanceof ClearCommand) {
                logger.warning("Confirmed clear could not be saved: access denied; data and filters restored.");
            }
            if (isUndo) {
                logger.warning("Undo save denied: current data and filters restored; snapshot retained for retry.");
            }
            throw new CommandException(String.format(FILE_OPS_PERMISSION_ERROR_FORMAT, e.getMessage()), e);
        } catch (IOException ioe) {
            restoreAfterSaveFailure(beforeCommand, displayedClients, displayedItems);
            if (command instanceof ClearCommand) {
                logger.warning("Confirmed clear could not be saved: I/O failure; data and filters restored.");
            }
            if (isUndo) {
                logger.warning("Undo save failed: current data and filters restored; snapshot retained for retry.");
            }
            throw new CommandException(String.format(FILE_OPS_ERROR_FORMAT, ioe.getMessage()), ioe);
        }

        if (command instanceof ClearCommand) {
            logger.info("Confirmed clear saved successfully.");
        }
        if (isUndo) {
            undoSnapshot = null;
            logger.info("Undo saved successfully; snapshot consumed.");
        } else if (!beforeCommand.equals(model.getAddressBook())) {
            undoSnapshot = beforeCommand;
            logger.fine("Saved data change recorded as the new single-step undo snapshot.");
        }
        undoAvailable.set(undoSnapshot != null);
        return commandResult;
    }

    @Override
    public ReadOnlyBooleanProperty undoAvailableProperty() {
        return undoAvailable.getReadOnlyProperty();
    }

    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return model.getFilteredPersonList();
    }

    @Override
    public ObservableList<BakeryItem> getFilteredBakeryItemList() {
        return model.getFilteredBakeryItemList();
    }

    @Override
    public ObservableList<ClientOrder> getOrderList() {
        return model.getAddressBook().getOrderList();
    }

    @Override
    public GuiSettings getGuiSettings() {
        return model.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        model.setGuiSettings(guiSettings);
    }
}
