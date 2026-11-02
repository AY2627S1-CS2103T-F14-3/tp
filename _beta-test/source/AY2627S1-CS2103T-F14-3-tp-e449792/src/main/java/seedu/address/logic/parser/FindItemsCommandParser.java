package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.List;

import seedu.address.logic.commands.FindItemsCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.bakeryitem.ItemNameContainsKeywordsPredicate;

/**
 * Parses input arguments and creates a new FindItemsCommand object
 */
public class FindItemsCommandParser implements Parser<FindItemsCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the FindItemsCommand
     * and returns a FindItemsCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public FindItemsCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindItemsCommand.MESSAGE_USAGE));
        }

        String[] nameKeywords = trimmedArgs.split("\\s+");

        return new FindItemsCommand(new ItemNameContainsKeywordsPredicate(List.of(nameKeywords)));
    }

}
