package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses an optional explicit confirmation for clearing all data.
 */
public class ClearCommandParser implements Parser<ClearCommand> {
    @Override
    public ClearCommand parse(String args) throws ParseException {
        String confirmation = args.trim();
        if (confirmation.isEmpty()) {
            return new ClearCommand();
        }
        if (confirmation.equals("confirm")) {
            return new ClearCommand(true);
        }
        throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ClearCommand.MESSAGE_USAGE));
    }
}
