package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.MarkOrderCompleteCommand;
import seedu.address.logic.parser.exceptions.ParseException;

public class MarkOrderCompleteCommandParserTest {
    private final MarkOrderCompleteCommandParser parser = new MarkOrderCompleteCommandParser();

    @Test
    public void parse_validIndices_returnsCommand() {
        MarkOrderCompleteCommand expected = new MarkOrderCompleteCommand(Index.fromOneBased(1), Index.fromOneBased(2));
        assertParseSuccess(parser, " 1 o/2", expected);
        assertParseSuccess(parser, "  001  o/ 002  ", expected);
        assertParseSuccess(parser, "\t1\to/\t2\t", expected);
        assertParseSuccess(parser, " 2147483647 o/2147483647",
                new MarkOrderCompleteCommand(Index.fromOneBased(Integer.MAX_VALUE),
                        Index.fromOneBased(Integer.MAX_VALUE)));
    }

    @Test
    public void parse_missingField_throwsParseException() {
        String message = String.format(MESSAGE_INVALID_COMMAND_FORMAT, MarkOrderCompleteCommand.MESSAGE_USAGE);
        for (String input : new String[] {"", " ", " 1", " o/2", " 1o/2", " 1 order/2"}) {
            assertParseFailure(parser, input, message);
        }
    }

    @Test
    public void parse_duplicateOrderPrefix_throwsParseException() {
        String message = Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_ORDER_INDEX);
        assertParseFailure(parser, " 1 o/2 o/3", message);
        assertParseFailure(parser, " 1 o/2 o/2", message);
        assertParseFailure(parser, " 1 o/ o/2", message);
    }

    @Test
    public void parse_invalidClientIndex_throwsParseException() {
        for (String invalid : new String[] {"0", "-1", "+1", "1.0", "one", "1 2", "1 x/2", "2147483648",
            "999999999999999999999999999999"}) {
            assertParseFailure(parser, " " + invalid + " o/2",
                    MarkOrderCompleteCommandParser.MESSAGE_INVALID_CLIENT_INDEX);
        }
    }

    @Test
    public void parse_invalidOrderIndex_throwsParseException() {
        for (String invalid : new String[] {"", " ", "0", "-1", "+1", "1.0", "two", "1 2", "2 extra", "2 x/3",
            "2147483648", "999999999999999999999999999999"}) {
            assertParseFailure(parser, " 1 o/" + invalid,
                    MarkOrderCompleteCommandParser.MESSAGE_INVALID_ORDER_INDEX);
        }
    }

    @Test
    public void parseCommand_validRequest_returnsCommand() throws Exception {
        assertEquals(parser.parse(" 1 o/2"), new AddressBookParser().parseCommand("mark-order-complete 1 o/2"));
        assertThrows(ParseException.class, () -> new AddressBookParser().parseCommand("mark-order-complete 1 o/0"));
    }
}
