package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.FindOrdersCommand;
import seedu.address.model.order.OrderContainsKeywordsPredicate;

public class FindOrdersCommandParserTest {

    private FindOrdersCommandParser parser = new FindOrdersCommandParser();

    @Test
    public void parse_emptyArg_throwsParseException() {
        assertParseFailure(parser, "     ",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindOrdersCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_validArgs_returnsFindOrdersCommand() {
        // no leading and trailing whitespaces
        FindOrdersCommand expectedFindOrdersCommand =
                new FindOrdersCommand(new OrderContainsKeywordsPredicate(List.of("Banana", "Muffin")));
        assertParseSuccess(parser, "Banana Muffin", expectedFindOrdersCommand);

        // multiple whitespaces between keywords
        assertParseSuccess(parser, " \n Banana \n \t Muffin  \t", expectedFindOrdersCommand);
    }

}
