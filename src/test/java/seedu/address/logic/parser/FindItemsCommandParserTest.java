package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.FindItemsCommand;
import seedu.address.model.bakeryitem.ItemNameContainsKeywordsPredicate;

public class FindItemsCommandParserTest {

    private FindItemsCommandParser parser = new FindItemsCommandParser();

    @Test
    public void parse_emptyArg_throwsParseException() {
        assertParseFailure(parser, "     ",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindItemsCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_validArgs_returnsFindItemsCommand() {
        // no leading and trailing whitespaces
        FindItemsCommand expectedFindItemsCommand =
                new FindItemsCommand(new ItemNameContainsKeywordsPredicate(List.of("Banana", "Muffin")));
        assertParseSuccess(parser, "Banana Muffin", expectedFindItemsCommand);

        // multiple whitespaces between keywords
        assertParseSuccess(parser, " \n Banana \n \t Muffin  \t", expectedFindItemsCommand);
    }

}
