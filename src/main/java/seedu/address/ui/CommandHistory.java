package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores submitted commands and navigates them without overwriting an unsent draft.
 */
public class CommandHistory {
    private final List<String> commands = new ArrayList<>();
    private int position;
    private String draft = "";

    /**
     * Records a non-empty submission and resets navigation to the newest position.
     */
    public void add(String command) {
        requireNonNull(command);
        if (command.isEmpty()) {
            return;
        }
        commands.add(command);
        position = commands.size();
        draft = "";
    }

    /**
     * Returns the previous command, remembering the current draft when navigation starts.
     */
    public String previous(String currentText) {
        requireNonNull(currentText);
        if (commands.isEmpty()) {
            return currentText;
        }
        if (position == commands.size()) {
            draft = currentText;
        }
        if (position > 0) {
            position--;
        }
        return commands.get(position);
    }

    /**
     * Returns the next command, or restores the unsent draft after the newest command.
     */
    public String next(String currentText) {
        requireNonNull(currentText);
        if (position == commands.size()) {
            return currentText;
        }
        position++;
        return position == commands.size() ? draft : commands.get(position);
    }
}
