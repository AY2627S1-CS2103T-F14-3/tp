package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.DeleteBakeryItemCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new {@code DeleteBakeryItemCommand}.
 */
public class DeleteBakeryItemCommandParser implements Parser<DeleteBakeryItemCommand> {

    /**
     * Parses the given arguments in the context of {@code DeleteBakeryItemCommand}
     * and returns a DeleteBakeryItemCommand object for execution.
     *
     * @throws ParseException if the user input does not conform to the expected format.
     */
    public DeleteBakeryItemCommand parse(String args) throws ParseException {
        requireNonNull(args);
        try {
            Index index = ParserUtil.parseIndex(args);
            return new DeleteBakeryItemCommand(index);
        } catch (ParseException pe) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteBakeryItemCommand.MESSAGE_USAGE), pe);
        }
    }

}
