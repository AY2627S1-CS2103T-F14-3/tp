package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;

/**
 * Stores submitted commands and navigates them without overwriting an unsent draft.
 */
public class CommandHistory {
    private final Logger logger = LogsCenter.getLogger(CommandHistory.class);
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
        logger.fine(() -> "Recorded command history entry; size=" + commands.size() + "; navigation reset.");
    }

    /**
     * Returns the previous command, remembering the current draft when navigation starts.
     */
    public String previous(String currentText) {
        requireNonNull(currentText);
        if (commands.isEmpty()) {
            logger.fine("History navigation ignored: no submitted commands.");
            return currentText;
        }
        if (position == commands.size()) {
            draft = currentText;
            logger.fine("Preserved unsent draft before navigating command history.");
        }
        if (position > 0) {
            position--;
        } else {
            logger.fine("History navigation reached the oldest command.");
        }
        logger.fine(() -> "Recalled older command at history position " + position);
        return commands.get(position);
    }

    /**
     * Returns the next command, or restores the unsent draft after the newest command.
     */
    public String next(String currentText) {
        requireNonNull(currentText);
        if (position == commands.size()) {
            logger.fine("History navigation ignored: already at the current draft.");
            return currentText;
        }
        position++;
        if (position == commands.size()) {
            logger.fine("Restored unsent draft after the newest command.");
        } else {
            logger.fine(() -> "Recalled newer command at history position " + position);
        }
        return position == commands.size() ? draft : commands.get(position);
    }
}
