package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.List;

import seedu.address.logic.commands.FindOrdersCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.order.OrderContainsKeywordsPredicate;

/**
 * Parses input arguments and creates a new FindOrdersCommand object
 */
public class FindOrdersCommandParser implements Parser<FindOrdersCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the FindOrdersCommand
     * and returns a FindOrdersCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public FindOrdersCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindOrdersCommand.MESSAGE_USAGE));
        }

        String[] nameKeywords = trimmedArgs.split("\\s+");

        return new FindOrdersCommand(new OrderContainsKeywordsPredicate(List.of(nameKeywords)));
    }

}
