package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.AddClientOrderCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.order.Deadline;
import seedu.address.model.order.Quantity;

public class AddClientOrderCommandParserTest {
    private final AddClientOrderCommandParser parser = new AddClientOrderCommandParser();

    @Test
    public void parse_validInput_returnsCommand() {
        AddClientOrderCommand expected = new AddClientOrderCommand(Index.fromOneBased(1), Index.fromOneBased(2),
                new Quantity(5), new Deadline("2026-10-15"));
        assertParseSuccess(parser, " 1 i/2 q/5 d/2026-10-15", expected);
        assertParseSuccess(parser, " 1 d/2026-10-15 q/5 i/2", expected);
        assertParseSuccess(parser, "\t1\ti/ 2\t q/ 0005 \t d/ 2026-10-15 \t", expected);
    }

    @Test
    public void parse_boundariesAndPastDate_returnsCommand() {
        for (int quantity : new int[] {1, 9999}) {
            AddClientOrderCommand expected = new AddClientOrderCommand(Index.fromOneBased(Integer.MAX_VALUE),
                    Index.fromOneBased(Integer.MAX_VALUE), new Quantity(quantity), new Deadline("1990-01-01"));
            assertParseSuccess(parser, " 2147483647 i/2147483647 q/" + quantity + " d/1990-01-01", expected);
        }
    }

    @Test
    public void parse_missingField_throwsParseException() {
        String message = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddClientOrderCommand.MESSAGE_USAGE);
        for (String input : new String[] {"", " 1", " i/2 q/5 d/2026-10-15", " 1 q/5 d/2026-10-15",
            " 1 i/2 d/2026-10-15", " 1 i/2 q/5", " 1 i/2q/5 d/2026-10-15"}) {
            assertParseFailure(parser, input, message);
        }
    }

    @Test
    public void parse_duplicateField_throwsParseException() {
        String valid = " 1 i/2 q/5 d/2026-10-15";
        assertParseFailure(parser, valid + " i/3",
                Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_ITEM_INDEX));
        assertParseFailure(parser, valid + " q/6",
                Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_QUANTITY));
        assertParseFailure(parser, valid + " d/2026-10-16",
                Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_DEADLINE));
    }

    @Test
    public void parse_invalidIndex_throwsParseException() {
        for (String invalid : new String[] {"0", "-1", "+1", "1.5", "1 2", "2147483648",
            "999999999999999999999999999999"}) {
            assertParseFailure(parser, " " + invalid + " i/2 q/5 d/2026-10-15",
                    AddClientOrderCommandParser.MESSAGE_INVALID_CLIENT_INDEX);
            assertParseFailure(parser, " 1 i/" + invalid + " q/5 d/2026-10-15",
                    AddClientOrderCommandParser.MESSAGE_INVALID_ITEM_INDEX);
        }
        assertParseFailure(parser, " 1 i/ q/5 d/2026-10-15",
                AddClientOrderCommandParser.MESSAGE_INVALID_ITEM_INDEX);
    }

    @Test
    public void parse_invalidQuantity_throwsParseException() {
        for (String invalid : new String[] {"", "0", "-1", "1.5", "10000", "2147483648", "5 extra", "5 x/1"}) {
            assertParseFailure(parser, " 1 i/2 q/" + invalid + " d/2026-10-15", Quantity.MESSAGE_CONSTRAINTS);
        }
    }

    @Test
    public void parse_invalidDeadline_throwsParseException() {
        for (String invalid : new String[] {"", "2026-02-29", "2026-13-01", "2026-1-01", "2026-10-15 extra"}) {
            assertParseFailure(parser, " 1 i/2 q/5 d/" + invalid, Deadline.MESSAGE_CONSTRAINTS);
        }
    }

    @Test
    public void parseCommand_addOrder_returnsCommand() throws Exception {
        String arguments = " 1 i/2 q/5 d/2026-10-15";
        assertEquals(parser.parse(arguments), new AddressBookParser().parseCommand("add-order" + arguments));
        assertThrows(ParseException.class, () ->
                new AddressBookParser().parseCommand("add-order 1 i/2 q/0 d/2026-10-15"));
    }
}
