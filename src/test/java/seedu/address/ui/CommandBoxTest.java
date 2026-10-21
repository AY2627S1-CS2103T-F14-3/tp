package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;

public class CommandBoxTest {
    @BeforeAll
    public static void initializeToolkit() throws Exception {
        boolean linuxWithoutDisplay = System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("linux")
                && System.getenv("DISPLAY") == null;
        assumeTrue(!linuxWithoutDisplay, "JavaFX UI tests require a display; history tests run on every platform.");
        CountDownLatch started = new CountDownLatch(1);
        try {
            Platform.startup(() -> {
                Platform.setImplicitExit(false);
                started.countDown();
            });
        } catch (IllegalStateException alreadyStarted) {
            started.countDown();
        }
        assertTrue(started.await(10, TimeUnit.SECONDS));
    }

    private void onFxThread(Runnable action) throws Exception {
        FutureTask<Void> task = new FutureTask<>(action, null);
        Platform.runLater(task);
        task.get(10, TimeUnit.SECONDS);
    }

    private TextField input(CommandBox box) {
        return (TextField) box.getRoot().lookup("#commandTextField");
    }

    private void press(TextField field, KeyCode code) {
        field.fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", code, false, false, false, false));
    }

    @Test
    public void keyboard_historyNavigation_restoresDraftWithoutExecuting() throws Exception {
        onFxThread(() -> {
            List<String> executed = new ArrayList<>();
            CommandBox box = new CommandBox(command -> {
                executed.add(command);
                return new CommandResult("Success");
            });
            TextField field = input(box);
            field.setText("list-clients");
            field.fireEvent(new ActionEvent());
            field.setText("find-clients Alice");
            field.fireEvent(new ActionEvent());
            field.setText("draft");
            press(field, KeyCode.UP);
            assertEquals("find-clients Alice", field.getText());
            assertEquals(field.getLength(), field.getCaretPosition());
            press(field, KeyCode.UP);
            assertEquals("list-clients", field.getText());
            press(field, KeyCode.UP);
            assertEquals("list-clients", field.getText());
            press(field, KeyCode.DOWN);
            assertEquals("find-clients Alice", field.getText());
            press(field, KeyCode.DOWN);
            assertEquals("draft", field.getText());
            assertEquals(List.of("list-clients", "find-clients Alice"), executed);
        });
    }

    @Test
    public void keyboard_emptyHistoryAndBlankSubmission_doNotExecute() throws Exception {
        onFxThread(() -> {
            List<String> executed = new ArrayList<>();
            CommandBox box = new CommandBox(command -> {
                executed.add(command);
                return new CommandResult("Success");
            });
            TextField field = input(box);
            field.setText("draft");
            press(field, KeyCode.UP);
            press(field, KeyCode.DOWN);
            assertEquals("draft", field.getText());
            field.setText("");
            field.fireEvent(new ActionEvent());
            assertTrue(executed.isEmpty());
        });
    }

    @Test
    public void keyboard_modifiedArrows_preserveInputAndHistory() throws Exception {
        onFxThread(() -> {
            CommandBox box = new CommandBox(command -> new CommandResult("Success"));
            TextField field = input(box);
            Scene scene = new Scene(box.getRoot());
            field.requestFocus();
            field.setText("list-clients");
            field.fireEvent(new ActionEvent());
            field.setText("draft");
            for (int modifier = 0; modifier < 4; modifier++) {
                for (KeyCode code : List.of(KeyCode.UP, KeyCode.DOWN)) {
                    field.fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", code,
                            modifier == 0, modifier == 1, modifier == 2, modifier == 3));
                    assertEquals("draft", field.getText());
                }
            }
            press(field, KeyCode.UP);
            assertEquals("list-clients", field.getText());
            assertEquals(field, scene.getFocusOwner());
            press(field, KeyCode.DOWN);
            assertEquals("draft", field.getText());
            assertEquals(field.getLength(), field.getCaretPosition());
            assertEquals(field, scene.getFocusOwner());
        });
    }

    @Test
    public void keyboard_failedCommands_canBeRecalledAndCorrected() throws Exception {
        onFxThread(() -> {
            List<String> executed = new ArrayList<>();
            CommandBox box = new CommandBox(command -> {
                executed.add(command);
                if (command.equals("bad parse")) {
                    throw new ParseException("Invalid command format");
                }
                if (command.equals("bad execute")) {
                    throw new CommandException("Invalid order index");
                }
                return new CommandResult("Success");
            });
            TextField field = input(box);
            field.setText("bad parse");
            field.fireEvent(new ActionEvent());
            field.setText("bad execute");
            field.fireEvent(new ActionEvent());
            field.setText("");
            press(field, KeyCode.UP);
            assertEquals("bad execute", field.getText());
            field.setText("list-clients");
            field.fireEvent(new ActionEvent());
            assertEquals("", field.getText());
            press(field, KeyCode.UP);
            assertEquals("list-clients", field.getText());
            press(field, KeyCode.UP);
            assertEquals("bad execute", field.getText());
            press(field, KeyCode.UP);
            assertEquals("bad parse", field.getText());
            assertEquals(List.of("bad parse", "bad execute", "list-clients"), executed);
        });
    }
}
