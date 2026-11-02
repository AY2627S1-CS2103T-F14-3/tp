package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses an optional explicit confirmation for clearing all data.
 */
public class ClearCommandParser implements Parser<ClearCommand> {
    private final Logger logger = LogsCenter.getLogger(ClearCommandParser.class);

    @Override
    public ClearCommand parse(String args) throws ParseException {
        String confirmation = args.trim();
        if (confirmation.isEmpty()) {
            return new ClearCommand();
        }
        if (confirmation.equals("confirm")) {
            return new ClearCommand(true);
        }
        logger.fine("Clear rejected: expected no arguments or the exact confirmation token.");
        throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ClearCommand.MESSAGE_USAGE));
    }
}
