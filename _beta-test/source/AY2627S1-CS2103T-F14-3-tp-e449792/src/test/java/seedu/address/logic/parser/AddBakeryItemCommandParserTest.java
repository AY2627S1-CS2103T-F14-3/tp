package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_BAKERY_ITEM_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PRICE;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.AddBakeryItemCommand;
import seedu.address.model.bakeryitem.BakeryItem;
import seedu.address.model.bakeryitem.BakeryItemName;
import seedu.address.model.bakeryitem.Price;

public class AddBakeryItemCommandParserTest {
    private static final String ITEM_NAME_DESC = " n/Banana Muffin";
    private static final String PRICE_DESC = " p/1.20";
    private static final String INVALID_ITEM_NAME_DESC = " n/Cr\u00e8me Brulee";
    private static final String INVALID_PRICE_DESC = " p/1.234";

    private final AddBakeryItemCommandParser parser = new AddBakeryItemCommandParser();

    @Test
    public void parse_allFieldsPresent_success() {
        BakeryItem expectedItem = new BakeryItem(new BakeryItemName("Banana Muffin"),
                new Price(new BigDecimal("1.20")));

        assertParseSuccess(parser, ITEM_NAME_DESC + PRICE_DESC, new AddBakeryItemCommand(expectedItem));
        assertParseSuccess(parser, " \t" + ITEM_NAME_DESC + PRICE_DESC,
                new AddBakeryItemCommand(expectedItem));
    }

    @Test
    public void parse_repeatedValue_failure() {
        assertParseFailure(parser, ITEM_NAME_DESC + ITEM_NAME_DESC + PRICE_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_BAKERY_ITEM_NAME));
        assertParseFailure(parser, ITEM_NAME_DESC + PRICE_DESC + PRICE_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PRICE));
    }

    @Test
    public void parse_compulsoryFieldMissing_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddBakeryItemCommand.MESSAGE_USAGE);

        assertParseFailure(parser, " Banana Muffin" + PRICE_DESC, expectedMessage);
        assertParseFailure(parser, ITEM_NAME_DESC + " 1.20", expectedMessage);
        assertParseFailure(parser, " Banana Muffin 1.20", expectedMessage);
    }

    @Test
    public void parse_invalidValue_failure() {
        assertParseFailure(parser, INVALID_ITEM_NAME_DESC + PRICE_DESC, BakeryItemName.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, ITEM_NAME_DESC + INVALID_PRICE_DESC, Price.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "preamble" + ITEM_NAME_DESC + PRICE_DESC,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddBakeryItemCommand.MESSAGE_USAGE));
    }
}
