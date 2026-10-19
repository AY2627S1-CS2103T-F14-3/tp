package seedu.address.ui;

import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.AddBakeryItemCommand;
import seedu.address.logic.commands.AddClientOrderCommand;
import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.DeleteBakeryItemCommand;
import seedu.address.logic.commands.DeleteClientOrderCommand;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.ListItemsCommand;
import seedu.address.logic.commands.ListOrdersCommand;
import seedu.address.logic.commands.MarkOrderCompleteCommand;
import seedu.address.logic.commands.PopulateCommand;
import seedu.address.logic.commands.UnmarkOrderCommand;

/**
 * Controller for a help page
 */
public class HelpWindow extends UiPart<Stage> {

    public static final String USERGUIDE_URL = "https://ay2627s1-cs2103t-f14-3.github.io/tp/UserGuide.html";
    public static final String HELP_MESSAGE = "Refer to the user guide: " + USERGUIDE_URL;

    private static final Logger logger = LogsCenter.getLogger(HelpWindow.class);
    private static final String FXML = "HelpWindow.fxml";

    @FXML
    private Button copyButton;

    @FXML
    private Label helpMessage;

    @FXML
    private VBox commandSummaryContainer;

    /**
     * Creates a new HelpWindow.
     *
     * @param root Stage to use as the root of the HelpWindow.
     */
    public HelpWindow(Stage root) {
        super(FXML, root);
        helpMessage.setText(HELP_MESSAGE);
        populateCommandSummary();
        // Clear existing selections before the clicked text area starts a new selection.
        getRoot().getScene().addEventFilter(MouseEvent.MOUSE_PRESSED, event -> clearTextAreaSelections());
    }

    /**
     * Creates a new HelpWindow.
     */
    public HelpWindow() {
        this(new Stage());
    }

    /**
     * Shows the help window.
     * @throws IllegalStateException
     *     <ul>
     *         <li>
     *             if this method is called on a thread other than the JavaFX Application Thread.
     *         </li>
     *         <li>
     *             if this method is called during animation or layout processing.
     *         </li>
     *         <li>
     *             if this method is called on the primary stage.
     *         </li>
     *         <li>
     *             if {@code dialogStage} is already showing.
     *         </li>
     *     </ul>
     */
    public void show() {
        logger.fine("Showing help page about the application.");
        getRoot().show();
        resizeCommandUsageTextAreas();
        getRoot().centerOnScreen();
    }

    /**
     * Returns true if the help window is currently being shown.
     */
    public boolean isShowing() {
        return getRoot().isShowing();
    }

    /**
     * Hides the help window.
     */
    public void hide() {
        getRoot().hide();
    }

    /**
     * Focuses on the help window.
     */
    public void focus() {
        getRoot().requestFocus();
    }

    /**
     * Copies the URL to the user guide to the clipboard.
     */
    @FXML
    private void copyUrl() {
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent url = new ClipboardContent();
        url.putString(USERGUIDE_URL);
        clipboard.setContent(url);
    }

    private void populateCommandSummary() {
        addCommandSummary(ClearCommand.COMMAND_WORD, ClearCommand.MESSAGE_USAGE);
        addCommandSummary(PopulateCommand.COMMAND_WORD, PopulateCommand.MESSAGE_USAGE);
        addCommandSummary(HelpCommand.COMMAND_WORD, HelpCommand.MESSAGE_USAGE);
        addCommandSummary(ExitCommand.COMMAND_WORD, ExitCommand.MESSAGE_USAGE);

        addCommandSummary(AddCommand.COMMAND_WORD, AddCommand.MESSAGE_USAGE);
        addCommandSummary(EditCommand.COMMAND_WORD, EditCommand.MESSAGE_USAGE);
        addCommandSummary(DeleteCommand.COMMAND_WORD, DeleteCommand.MESSAGE_USAGE);
        addCommandSummary(FindCommand.COMMAND_WORD, FindCommand.MESSAGE_USAGE);
        addCommandSummary(ListCommand.COMMAND_WORD, ListCommand.MESSAGE_USAGE);

        addCommandSummary(AddBakeryItemCommand.COMMAND_WORD, AddBakeryItemCommand.MESSAGE_USAGE);
        addCommandSummary(DeleteBakeryItemCommand.COMMAND_WORD, DeleteBakeryItemCommand.MESSAGE_USAGE);
        addCommandSummary(ListItemsCommand.COMMAND_WORD, ListItemsCommand.MESSAGE_USAGE);

        addCommandSummary(AddClientOrderCommand.COMMAND_WORD, AddClientOrderCommand.MESSAGE_USAGE);
        addCommandSummary(DeleteClientOrderCommand.COMMAND_WORD, DeleteClientOrderCommand.MESSAGE_USAGE);
        addCommandSummary(MarkOrderCompleteCommand.COMMAND_WORD, MarkOrderCompleteCommand.MESSAGE_USAGE);
        addCommandSummary(UnmarkOrderCommand.COMMAND_WORD, UnmarkOrderCommand.MESSAGE_USAGE);
        addCommandSummary(ListOrdersCommand.COMMAND_WORD, ListOrdersCommand.MESSAGE_USAGE);
    }

    private void addCommandSummary(String commandWord, String commandUsage) {
        Label commandWordLabel = new Label(commandWord);
        commandWordLabel.getStyleClass().add("command-word");

        TextArea commandUsageTextArea = new TextArea(commandUsage);
        commandUsageTextArea.setEditable(false);
        commandUsageTextArea.setFocusTraversable(false);
        commandUsageTextArea.setPrefRowCount(1);
        commandUsageTextArea.setMinHeight(Region.USE_PREF_SIZE);
        commandUsageTextArea.setMaxHeight(Region.USE_PREF_SIZE);
        commandUsageTextArea.setWrapText(true);
        commandUsageTextArea.getStyleClass().add("result-display");

        commandSummaryContainer.getChildren().addAll(commandWordLabel, commandUsageTextArea);
    }

    private void resizeCommandUsageTextAreas() {
        // Applying CSS creates each text area's skin/visual structure and its internal scroll pane.
        commandSummaryContainer.getScene().getRoot().applyCss();
        resizeEachCommandUsageTextArea();
    }

    private void resizeEachCommandUsageTextArea() {
        commandSummaryContainer.getChildren().stream()
                .filter(TextArea.class::isInstance)
                .map(TextArea.class::cast)
                .forEach(this::resizeToFitContent);
    }

    private void clearTextAreaSelections() {
        commandSummaryContainer.getChildren().stream()
                .filter(TextArea.class::isInstance)
                .map(TextArea.class::cast)
                .forEach(TextArea::deselect);
    }

    private void resizeToFitContent(TextArea textArea) {
        // TextArea does not expose its scrollbar policies or rendered content dimensions directly.
        ScrollPane scrollPane = (ScrollPane) textArea.lookup(".scroll-pane");
        if (scrollPane == null) {
            return;
        }

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.applyCss();
        scrollPane.layout();

        // Use the viewport width so wrapped lines are included in the preferred content height.
        Region content = (Region) scrollPane.getContent();
        double contentHeight = content.prefHeight(scrollPane.getViewportBounds().getWidth());
        double verticalInsets = textArea.getInsets().getTop() + textArea.getInsets().getBottom()
                + scrollPane.getInsets().getTop() + scrollPane.getInsets().getBottom();
        // The extra pixel prevents subpixel rounding from leaving a tiny vertical scroll range.
        textArea.setPrefHeight(Math.ceil(contentHeight + verticalInsets) + 1);
    }
}
