package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import seedu.address.logic.LogicManager;
import seedu.address.logic.commands.UndoCommand;
import seedu.address.model.ModelManager;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

public class UndoCommandUiTest {
    @TempDir
    public Path directory;

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

    @Test
    public void command_dataChangeAndUndo_updatesDataAndFeedback() throws Exception {
        FutureTask<Void> task = new FutureTask<>(() -> {
            ModelManager model = new ModelManager();
            Path book = directory.resolve("book.json");
            JsonAddressBookStorage storage = new JsonAddressBookStorage(book);
            LogicManager logic = new LogicManager(model, new StorageManager(storage,
                    new JsonUserPrefsStorage(directory.resolve("prefs.json"))));
            Stage stage = new Stage();
            try {
                MainWindow window = new MainWindow(stage, logic, book);
                window.fillInnerParts();
                TextField commandInput = (TextField) stage.getScene().lookup("#commandTextField");
                logic.execute("add-item n/Cake p/10");
                assertTrue(logic.undoAvailableProperty().get());
                logic.execute("list-items");
                assertTrue(logic.undoAvailableProperty().get());
                commandInput.setText("undo");
                commandInput.fireEvent(new ActionEvent());
                assertTrue(model.getAddressBook().getBakeryItemList().isEmpty());
                assertTrue(storage.readAddressBook().orElseThrow().getBakeryItemList().isEmpty());
                assertFalse(logic.undoAvailableProperty().get());
                assertEquals("", commandInput.getText());
                TextArea feedback = (TextArea) stage.getScene().lookup("#resultDisplay");
                assertEquals(UndoCommand.MESSAGE_SUCCESS, feedback.getText());

                commandInput.setText("undo");
                commandInput.fireEvent(new ActionEvent());
                assertEquals(UndoCommand.MESSAGE_UNAVAILABLE, feedback.getText());
                assertTrue(model.getAddressBook().getBakeryItemList().isEmpty());
            } finally {
                stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
                stage.close();
            }
            return null;
        });
        Platform.runLater(task);
        task.get(10, TimeUnit.SECONDS);
    }
}
