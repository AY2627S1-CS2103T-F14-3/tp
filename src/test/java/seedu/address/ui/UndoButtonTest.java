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
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import seedu.address.logic.LogicManager;
import seedu.address.logic.commands.UndoCommand;
import seedu.address.model.ModelManager;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

public class UndoButtonTest {
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
    public void button_dataChangeAndUndo_updatesAvailabilityAndFeedback() throws Exception {
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
                Button button = (Button) stage.getScene().lookup("#undoButton");
                assertTrue(button.isDisabled());
                logic.execute("add-item n/Cake p/10");
                assertFalse(button.isDisabled());
                logic.execute("list-items");
                assertFalse(button.isDisabled());
                button.fire();
                assertTrue(model.getAddressBook().getBakeryItemList().isEmpty());
                assertTrue(storage.readAddressBook().orElseThrow().getBakeryItemList().isEmpty());
                assertTrue(button.isDisabled());
                TextArea feedback = (TextArea) stage.getScene().lookup("#resultDisplay");
                assertEquals(UndoCommand.MESSAGE_SUCCESS, feedback.getText());
            } finally {
                stage.close();
            }
            return null;
        });
        Platform.runLater(task);
        task.get(10, TimeUnit.SECONDS);
    }
}
