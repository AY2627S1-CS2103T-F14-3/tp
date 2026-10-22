package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.function.DoublePredicate;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import seedu.address.logic.commands.AddBakeryItemCommand;
import seedu.address.logic.commands.AddClientOrderCommand;
import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.DeleteBakeryItemCommand;
import seedu.address.logic.commands.DeleteClientOrderCommand;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.EditBakeryItemCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.FindItemsCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.ListItemsCommand;
import seedu.address.logic.commands.ListOrdersCommand;
import seedu.address.logic.commands.MarkOrderCompleteCommand;
import seedu.address.logic.commands.PopulateCommand;
import seedu.address.logic.commands.UnmarkOrderCommand;

public class HelpWindowTest {
    private static final double INITIAL_WINDOW_WIDTH = 750;
    private static final double INITIAL_WINDOW_HEIGHT = 420;
    private static final double MIN_SUPPORTED_LOGICAL_SCREEN_WIDTH = 1280 / 1.5;
    private static final double MIN_SUPPORTED_LOGICAL_SCREEN_HEIGHT = 720 / 1.5;

    private static final List<CommandSummary> EXPECTED_COMMAND_SUMMARIES = List.of(
            new CommandSummary(ClearCommand.COMMAND_WORD, ClearCommand.MESSAGE_USAGE),
            new CommandSummary(PopulateCommand.COMMAND_WORD, PopulateCommand.MESSAGE_USAGE),
            new CommandSummary(HelpCommand.COMMAND_WORD, HelpCommand.MESSAGE_USAGE),
            new CommandSummary(ExitCommand.COMMAND_WORD, ExitCommand.MESSAGE_USAGE),
            new CommandSummary(AddCommand.COMMAND_WORD, AddCommand.MESSAGE_USAGE),
            new CommandSummary(EditCommand.COMMAND_WORD, EditCommand.MESSAGE_USAGE),
            new CommandSummary(DeleteCommand.COMMAND_WORD, DeleteCommand.MESSAGE_USAGE),
            new CommandSummary(FindCommand.COMMAND_WORD, FindCommand.MESSAGE_USAGE),
            new CommandSummary(ListCommand.COMMAND_WORD, ListCommand.MESSAGE_USAGE),
            new CommandSummary(AddBakeryItemCommand.COMMAND_WORD, AddBakeryItemCommand.MESSAGE_USAGE),
            new CommandSummary(DeleteBakeryItemCommand.COMMAND_WORD, DeleteBakeryItemCommand.MESSAGE_USAGE),
            new CommandSummary(EditBakeryItemCommand.COMMAND_WORD, EditBakeryItemCommand.MESSAGE_USAGE),
            new CommandSummary(FindItemsCommand.COMMAND_WORD, FindItemsCommand.MESSAGE_USAGE),
            new CommandSummary(ListItemsCommand.COMMAND_WORD, ListItemsCommand.MESSAGE_USAGE),
            new CommandSummary(AddClientOrderCommand.COMMAND_WORD, AddClientOrderCommand.MESSAGE_USAGE),
            new CommandSummary(DeleteClientOrderCommand.COMMAND_WORD, DeleteClientOrderCommand.MESSAGE_USAGE),
            new CommandSummary(MarkOrderCompleteCommand.COMMAND_WORD, MarkOrderCompleteCommand.MESSAGE_USAGE),
            new CommandSummary(UnmarkOrderCommand.COMMAND_WORD, UnmarkOrderCommand.MESSAGE_USAGE),
            new CommandSummary(ListOrdersCommand.COMMAND_WORD, ListOrdersCommand.MESSAGE_USAGE));

    @BeforeAll
    public static void startJavaFx() throws Exception {
        CompletableFuture<Void> started = new CompletableFuture<>();
        Runnable initialize = () -> {
            Platform.setImplicitExit(false);
            started.complete(null);
        };
        try {
            Platform.startup(initialize);
        } catch (IllegalStateException alreadyStarted) {
            Platform.runLater(initialize);
        }
        started.get(10, TimeUnit.SECONDS);
    }

    private static void onJavaFxThread(Runnable action) throws Exception {
        FutureTask<Void> task = new FutureTask<>(action, null);
        Platform.runLater(task);
        task.get(10, TimeUnit.SECONDS);
    }

    @Test
    public void constructor_populatesAllCommandSummaries() throws Exception {
        onJavaFxThread(() -> {
            HelpWindow helpWindow = new HelpWindow();
            VBox container = commandSummaryContainer(helpWindow);

            assertEquals(EXPECTED_COMMAND_SUMMARIES.size() * 2, container.getChildren().size());
            for (int i = 0; i < EXPECTED_COMMAND_SUMMARIES.size(); i++) {
                CommandSummary expected = EXPECTED_COMMAND_SUMMARIES.get(i);
                Label commandWord = (Label) container.getChildren().get(i * 2);
                TextArea commandUsage = (TextArea) container.getChildren().get(i * 2 + 1);

                assertEquals(expected.commandWord(), commandWord.getText());
                assertTrue(commandWord.getStyleClass().contains("command-word"));
                assertEquals(expected.commandUsage(), commandUsage.getText());
                assertFalse(commandUsage.isEditable());
                assertFalse(commandUsage.isFocusTraversable());
                assertTrue(commandUsage.isWrapText());
                assertTrue(commandUsage.getStyleClass().contains("result-display"));
            }
            helpWindow.getRoot().close();
        });
    }

    @Test
    public void mousePress_textSelectedInMultipleAreas_clearsSelections() throws Exception {
        onJavaFxThread(() -> {
            HelpWindow helpWindow = new HelpWindow();
            List<TextArea> textAreas = commandUsageTextAreas(helpWindow);
            textAreas.get(0).selectRange(0, 3);
            textAreas.get(1).selectRange(0, 3);

            Node sceneRoot = helpWindow.getRoot().getScene().getRoot();
            sceneRoot.fireEvent(new MouseEvent(MouseEvent.MOUSE_PRESSED, 0, 0, 0, 0,
                    MouseButton.PRIMARY, 1, false, false, false, false,
                    true, false, false, true, false, false, null));

            assertEquals(0, textAreas.get(0).getSelectedText().length());
            assertEquals(0, textAreas.get(1).getSelectedText().length());
            helpWindow.getRoot().close();
        });
    }

    @Test
    public void secondaryMousePress_textSelected_preservesSelection() throws Exception {
        onJavaFxThread(() -> {
            HelpWindow helpWindow = new HelpWindow();
            helpWindow.show();
            TextArea textArea = commandUsageTextAreas(helpWindow).get(0);
            textArea.selectRange(0, 3);
            String selectedText = textArea.getSelectedText();

            textArea.fireEvent(new MouseEvent(MouseEvent.MOUSE_PRESSED, 0, 0, 0, 0,
                    MouseButton.SECONDARY, 1, false, false, false, false,
                    false, false, true, false, true, false, null));

            assertEquals(selectedText, textArea.getSelectedText());
            helpWindow.getRoot().close();
        });
    }

    @Test
    public void secondaryMousePress_anotherTextAreaSelected_clearsOtherSelection() throws Exception {
        onJavaFxThread(() -> {
            HelpWindow helpWindow = new HelpWindow();
            helpWindow.show();
            List<TextArea> textAreas = commandUsageTextAreas(helpWindow);
            TextArea initiallySelectedTextArea = textAreas.get(0);
            TextArea rightClickedTextArea = textAreas.get(1);
            initiallySelectedTextArea.selectRange(0, 3);

            rightClickedTextArea.fireEvent(new MouseEvent(MouseEvent.MOUSE_PRESSED, 0, 0, 0, 0,
                    MouseButton.SECONDARY, 1, false, false, false, false,
                    false, false, true, false, true, false, null));
            rightClickedTextArea.selectAll();

            assertEquals(0, initiallySelectedTextArea.getSelectedText().length());
            assertEquals(rightClickedTextArea.getText(), rightClickedTextArea.getSelectedText());
            helpWindow.getRoot().close();
        });
    }

    @Test
    public void show_resizesTextAreasAndDisablesInternalScrollbars() throws Exception {
        onJavaFxThread(() -> {
            HelpWindow helpWindow = new HelpWindow();
            helpWindow.show();

            assertTrue(helpWindow.isShowing());
            for (TextArea textArea : commandUsageTextAreas(helpWindow)) {
                ScrollPane scrollPane = (ScrollPane) textArea.lookup(".scroll-pane");
                assertNotNull(scrollPane);
                assertEquals(ScrollPane.ScrollBarPolicy.NEVER, scrollPane.getHbarPolicy());
                assertEquals(ScrollPane.ScrollBarPolicy.NEVER, scrollPane.getVbarPolicy());
                assertTrue(textArea.getPrefHeight() > 0);
            }

            helpWindow.focus();
            helpWindow.hide();
            assertFalse(helpWindow.isShowing());
            helpWindow.getRoot().close();
        });
    }

    @Test
    public void show_supportedDisplayConfiguration_windowFitsAndIsResizable() throws Exception {
        onJavaFxThread(() -> {
            HelpWindow helpWindow = new HelpWindow();
            helpWindow.show();

            assertTrue(helpWindow.getRoot().isResizable());
            assertEquals(INITIAL_WINDOW_WIDTH, helpWindow.getRoot().getWidth(), 0.01);
            assertEquals(INITIAL_WINDOW_HEIGHT, helpWindow.getRoot().getHeight(), 0.01);
            assertTrue(helpWindow.getRoot().getWidth() <= MIN_SUPPORTED_LOGICAL_SCREEN_WIDTH);
            assertTrue(helpWindow.getRoot().getHeight() <= MIN_SUPPORTED_LOGICAL_SCREEN_HEIGHT);
            helpWindow.getRoot().close();
        });
    }

    @Test
    public void resizeWindow_widthChanges_textAreaGrowsAndShrinksToFitContent() throws Exception {
        HelpWindow[] helpWindow = new HelpWindow[1];
        TextArea[] longestTextArea = new TextArea[1];
        double[] initialNarrowHeight = new double[1];

        onJavaFxThread(() -> {
            HelpWindow window = new HelpWindow();
            window.show();
            longestTextArea[0] = commandUsageTextAreas(window).stream()
                    .max((first, second) -> Integer.compare(first.getLength(), second.getLength()))
                    .orElseThrow();
            helpWindow[0] = window;
            initialNarrowHeight[0] = longestTextArea[0].getPrefHeight();
        });

        double wideHeight = resizeWindowAndWaitForTextAreaHeight(
                helpWindow[0], longestTextArea[0], 1000,
                height -> height >= 0 && height < initialNarrowHeight[0]);
        onJavaFxThread(() -> assertTextAreaContentFits(longestTextArea[0]));

        double resizedNarrowHeight = resizeWindowAndWaitForTextAreaHeight(
                helpWindow[0], longestTextArea[0], INITIAL_WINDOW_WIDTH, height -> height > wideHeight);
        onJavaFxThread(() -> {
            assertTrue(resizedNarrowHeight > wideHeight);
            assertTextAreaContentFits(longestTextArea[0]);
            helpWindow[0].getRoot().close();
        });
    }

    private static double resizeWindowAndWaitForTextAreaHeight(
            HelpWindow helpWindow, TextArea textArea, double width, DoublePredicate expectedHeight) throws Exception {
        CompletableFuture<Double> resizedHeight = new CompletableFuture<>();
        onJavaFxThread(() -> {
            ChangeListener<Number> heightListener = new ChangeListener<>() {
                @Override
                public void changed(
                        ObservableValue<? extends Number> observable, Number oldHeight, Number newHeight) {
                    if (expectedHeight.test(newHeight.doubleValue())) {
                        textArea.prefHeightProperty().removeListener(this);
                        resizedHeight.complete(newHeight.doubleValue());
                    }
                }
            };
            textArea.prefHeightProperty().addListener(heightListener);
            helpWindow.getRoot().setWidth(width);
        });
        return resizedHeight.get(10, TimeUnit.SECONDS);
    }

    private static VBox commandSummaryContainer(HelpWindow helpWindow) {
        VBox helpWindowContainer = (VBox) helpWindow.getRoot().getScene().getRoot();
        ScrollPane commandSummaryScrollPane = helpWindowContainer.getChildren().stream()
                .filter(ScrollPane.class::isInstance)
                .map(ScrollPane.class::cast)
                .findFirst()
                .orElseThrow();
        return (VBox) commandSummaryScrollPane.getContent();
    }

    private static List<TextArea> commandUsageTextAreas(HelpWindow helpWindow) {
        return commandSummaryContainer(helpWindow).getChildren().stream()
                .filter(TextArea.class::isInstance)
                .map(TextArea.class::cast)
                .toList();
    }

    private static void assertTextAreaContentFits(TextArea textArea) {
        ScrollPane scrollPane = (ScrollPane) textArea.lookup(".scroll-pane");
        double contentHeight = ((Region) scrollPane.getContent())
                .prefHeight(scrollPane.getViewportBounds().getWidth());
        assertTrue(contentHeight <= scrollPane.getViewportBounds().getHeight() + 1);
    }

    private record CommandSummary(String commandWord, String commandUsage) {
    }
}
