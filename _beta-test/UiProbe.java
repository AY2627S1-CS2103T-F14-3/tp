import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import javafx.application.Platform;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;
import seedu.address.MainApp;

/** Runs UI command sequences against classes loaded exclusively from the released JAR. */
public class UiProbe {
    private static MainApp application;
    private static Stage stage;
    private static Path output;

    /** Creates the real release UI, submits commands, and captures rendered scenes. */
    public static void main(String[] arguments) throws Exception {
        output = Path.of(arguments[0]);
        Files.createDirectories(output);
        CountDownLatch completed = new CountDownLatch(1);
        Platform.startup(() -> {
            try {
                Platform.setImplicitExit(false);
                application = new MainApp();
                application.init();
                stage = new Stage();
                application.start(stage);
                Platform.runLater(() -> {
                    try {
                        runScenarios(completed);
                    } catch (Throwable exception) {
                        exception.printStackTrace();
                        finish(completed);
                    }
                });
            } catch (Throwable exception) {
                exception.printStackTrace();
                completed.countDown();
                Platform.exit();
            }
        });
        if (!completed.await(90, TimeUnit.SECONDS)) {
            throw new IllegalStateException("UI probe timed out");
        }
    }

    private static void runScenarios(CountDownLatch completed) {
        List<ProbeAction> steps = new ArrayList<>();
        steps.add(() -> capture("startup-default"));
        steps.add(() -> submit("clear confirm"));
        steps.add(() -> submit("add-client n/Alexandra Catherine Tan p/91234567 e/alexandra.tan@example.com"
                + " a/123 Ang Mo Kio Avenue 3 #12-456 Singapore 560123"));
        steps.add(() -> submit("add-item n/Chocolate Hazelnut Birthday Cake p/55.90"));
        steps.add(() -> submit("add-order 1 i/1 q/2 d/2026-11-03"));
        steps.add(() -> capture("normal-data-default"));
        steps.add(() -> { stage.setWidth(1100); stage.setHeight(700); });
        steps.add(() -> capture("normal-data-1100"));
        steps.add(() -> stage.setWidth(450));
        steps.add(() -> capture("normal-data-minimum"));
        steps.add(() -> stage.setWidth(1100));
        steps.add(() -> submit("list-incomplete-orders"));
        steps.add(() -> submit("mark 1 o/1"));
        steps.add(() -> capture("marked-in-incomplete-view"));
        steps.add(() -> submit("list-orders"));
        steps.add(() -> submit("help"));
        steps.add(() -> {
            for (Window window : Window.getWindows().toArray(Window[]::new)) {
                if (window instanceof Stage help && help != stage && help.isShowing()) {
                    captureStage(help, "help-current");
                }
            }
        });
        steps.add(() -> submit("clear"));
        steps.add(() -> submit("undo"));
        steps.add(() -> capture("undo-after-clear-warning"));
        steps.add(() -> submit("populate"));
        steps.add(() -> capture("populated"));
        steps.add(() -> submit("exit"));
        steps.add(() -> System.out.println("VISIBLE_WINDOWS_AFTER_EXIT="
                + Window.getWindows().stream().filter(Window::isShowing).count()));
        int[] nextStep = {0};
        Timeline sequence = new Timeline(new KeyFrame(Duration.millis(350), event -> {
            try {
                steps.get(nextStep[0]++).run();
            } catch (Throwable exception) {
                exception.printStackTrace();
            }
        }));
        sequence.setCycleCount(steps.size());
        sequence.setOnFinished(event -> finish(completed));
        sequence.play();
    }

    private static void finish(CountDownLatch completed) {
        try {
            application.stop();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        completed.countDown();
        Platform.exit();
    }

    private static void submit(String command) {
        TextField commandField = (TextField) stage.getScene().lookup("#commandTextField");
        commandField.setText(command);
        commandField.fireEvent(new ActionEvent());
        TextArea result = (TextArea) stage.getScene().lookup("#resultDisplay");
        System.out.println("COMMAND=" + command + "\nRESULT=" + result.getText());
    }

    private static void capture(String name) throws Exception {
        captureStage(stage, name);
        for (Node node : stage.getScene().getRoot().lookupAll(".label")) {
            if (node instanceof Label label && !label.getText().isBlank()) {
                System.out.println("LABEL " + name + " " + label.getId() + " width=" + label.getWidth()
                        + " preferred=" + label.prefWidth(-1) + " text=" + label.getText());
            }
        }
    }

    private static void captureStage(Stage target, String name) throws Exception {
        target.getScene().getRoot().applyCss();
        target.getScene().getRoot().layout();
        WritableImage snapshot = target.getScene().snapshot(null);
        int width = (int) snapshot.getWidth();
        int height = (int) snapshot.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                image.setRGB(x, y, snapshot.getPixelReader().getArgb(x, y));
            }
        }
        ImageIO.write(image, "png", output.resolve(name + ".png").toFile());
        System.out.println("CAPTURE=" + name + " " + width + "x" + height
                + " stage=" + target.getWidth() + "x" + target.getHeight());
    }

    private interface ProbeAction {
        void run() throws Exception;
    }
}
