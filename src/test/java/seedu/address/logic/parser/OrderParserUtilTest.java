package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.order.Deadline;
import seedu.address.model.order.Quantity;

public class OrderParserUtilTest {
    @Test
    public void parseQuantity_validInput_returnsQuantity() throws Exception {
        assertEquals(new Quantity(5), ParserUtil.parseQuantity(" 0005 "));
        assertEquals(new Quantity(1), ParserUtil.parseQuantity("1"));
        assertEquals(new Quantity(9999), ParserUtil.parseQuantity("9999"));
    }

    @Test
    public void parseQuantity_invalidInput_throwsParseException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseQuantity(null));
        for (String invalid : new String[] {"", " ", "0", "-1", "1.1", "10000", "2147483648"}) {
            assertThrows(ParseException.class, Quantity.MESSAGE_CONSTRAINTS, () -> ParserUtil.parseQuantity(invalid));
        }
    }

    @Test
    public void parseDeadline_validInput_returnsDeadline() throws Exception {
        assertEquals(new Deadline("1990-01-01"), ParserUtil.parseDeadline(" 1990-01-01 "));
        assertEquals(new Deadline("2024-02-29"), ParserUtil.parseDeadline("2024-02-29"));
    }

    @Test
    public void parseDeadline_invalidInput_throwsParseException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseDeadline(null));
        for (String invalid : new String[] {"", " ", "2026-02-29", "2026-13-01", "2026-1-01"}) {
            assertThrows(ParseException.class, Deadline.MESSAGE_CONSTRAINTS, () -> ParserUtil.parseDeadline(invalid));
        }
    }
}
