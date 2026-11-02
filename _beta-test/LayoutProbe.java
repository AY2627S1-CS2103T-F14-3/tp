import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import seedu.address.MainApp;
import seedu.address.commons.core.GuiSettings;

/** Captures the release UI at an initial window size without depending on native resize events. */
public class LayoutProbe {
    /** Renders a realistic client, item, and order using the requested initial window width. */
    public static void main(String[] arguments) throws Exception {
        CountDownLatch completed = new CountDownLatch(1);
        Platform.startup(() -> {
            try {
                ProbeApplication application = new ProbeApplication();
                application.prepare(Double.parseDouble(arguments[0]));
                Stage window = new Stage();
                application.start(window);
                Platform.runLater(() -> {
                    try {
                        window.getScene().getRoot().applyCss();
                        window.getScene().getRoot().layout();
                        WritableImage snapshot = window.getScene().snapshot(null);
                        int width = (int) snapshot.getWidth();
                        int height = (int) snapshot.getHeight();
                        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                        for (int y = 0; y < height; y++) {
                            for (int x = 0; x < width; x++) {
                                image.setRGB(x, y, snapshot.getPixelReader().getArgb(x, y));
                            }
                        }
                        ImageIO.write(image, "png", Path.of(arguments[1]).toFile());
                        System.out.println("SCENE=" + width + "x" + height);
                        for (Node node : window.getScene().getRoot().lookupAll(".label")) {
                            if (node instanceof Label label && label.getId() != null) {
                                System.out.println("LABEL=" + label.getId() + " width=" + label.getWidth()
                                        + " preferred=" + label.prefWidth(-1) + " tooltip=" + label.getTooltip()
                                        + " wrap=" + label.isWrapText() + " text=" + label.getText());
                            }
                        }
                    } catch (Throwable exception) {
                        exception.printStackTrace();
                    } finally {
                        completed.countDown();
                        Platform.exit();
                    }
                });
            } catch (Throwable exception) {
                exception.printStackTrace();
                completed.countDown();
                Platform.exit();
            }
        });
        if (!completed.await(60, TimeUnit.SECONDS)) {
            throw new IllegalStateException("Layout probe timed out");
        }
    }

    private static class ProbeApplication extends MainApp {
        void prepare(double width) throws Exception {
            init();
            model.setGuiSettings(new GuiSettings(width, 700, 0, 0));
            logic.execute("clear confirm");
            logic.execute("add-client n/Alexandra Catherine Tan p/91234567"
                    + " e/alexandra.tan@example.com a/123 Ang Mo Kio Avenue 3 #12-456 Singapore 560123");
            logic.execute("add-item n/Chocolate Hazelnut Birthday Cake p/55.90");
            logic.execute("add-order 1 i/1 q/2 d/2026-11-03");
        }
    }
}
