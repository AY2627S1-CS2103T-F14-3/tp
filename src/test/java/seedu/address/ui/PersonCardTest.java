package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.scene.control.Label;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class PersonCardTest {
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
    public void constructor_absentEmail_showsNotProvided() throws Exception {
        assertEquals("Not provided", displayedEmail(new PersonBuilder().withEmail("").build()));
    }

    @Test
    public void constructor_existingEmail_showsEmailUnchanged() throws Exception {
        assertEquals("carol@example.com", displayedEmail(new PersonBuilder().withEmail("carol@example.com").build()));
    }

    private String displayedEmail(Person person) throws Exception {
        FutureTask<String> task = new FutureTask<>(() -> {
            PersonCard card = new PersonCard(person, 1);
            return ((Label) card.getRoot().lookup("#email")).getText();
        });
        Platform.runLater(task);
        return task.get(10, TimeUnit.SECONDS);
    }
}
