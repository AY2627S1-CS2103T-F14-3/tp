package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_BAKERY_ITEM_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PRICE;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.EditBakeryItemCommand;
import seedu.address.logic.commands.EditBakeryItemCommand.EditBakeryItemDescriptor;
import seedu.address.model.bakeryitem.BakeryItemName;
import seedu.address.model.bakeryitem.Price;

/**
 * Contains tests for {@code EditBakeryItemCommandParser}.
 */
public class EditBakeryItemCommandParserTest {

    private static final String ITEM_NAME_DESC = " n/Blueberry Muffin";
    private static final String PRICE_DESC = " p/2.50";
    private static final String INVALID_ITEM_NAME_DESC = " n/Cr\u00e8me Brulee";
    private static final String INVALID_PRICE_DESC = " p/1.234";
    private static final String MESSAGE_INVALID_FORMAT = String.format(MESSAGE_INVALID_COMMAND_FORMAT,
            EditBakeryItemCommand.MESSAGE_USAGE);

    private final EditBakeryItemCommandParser parser = new EditBakeryItemCommandParser();

    @Test
    public void parse_missingParts_failure() {
        assertParseFailure(parser, ITEM_NAME_DESC, MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "1", EditBakeryItemCommand.MESSAGE_NOT_EDITED);
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidPreamble_failure() {
        assertParseFailure(parser, "-1" + ITEM_NAME_DESC, MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "0" + ITEM_NAME_DESC, MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "1 random text" + ITEM_NAME_DESC, MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "1 i/2" + ITEM_NAME_DESC, MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidValue_failure() {
        assertParseFailure(parser, "1" + INVALID_ITEM_NAME_DESC, BakeryItemName.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "1" + INVALID_PRICE_DESC, Price.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_allFieldsSpecified_success() {
        EditBakeryItemDescriptor descriptor = new EditBakeryItemDescriptor();
        descriptor.setItemName(new BakeryItemName("Blueberry Muffin"));
        descriptor.setPrice(Price.fromDollars("2.50"));

        assertParseSuccess(parser, INDEX_SECOND_PERSON.getOneBased() + ITEM_NAME_DESC + PRICE_DESC,
                new EditBakeryItemCommand(INDEX_SECOND_PERSON, descriptor));
    }

    @Test
    public void parse_oneFieldSpecified_success() {
        EditBakeryItemDescriptor nameDescriptor = new EditBakeryItemDescriptor();
        nameDescriptor.setItemName(new BakeryItemName("Blueberry Muffin"));
        assertParseSuccess(parser, INDEX_FIRST_PERSON.getOneBased() + ITEM_NAME_DESC,
                new EditBakeryItemCommand(INDEX_FIRST_PERSON, nameDescriptor));

        EditBakeryItemDescriptor priceDescriptor = new EditBakeryItemDescriptor();
        priceDescriptor.setPrice(Price.fromDollars("2.50"));
        assertParseSuccess(parser, INDEX_FIRST_PERSON.getOneBased() + PRICE_DESC,
                new EditBakeryItemCommand(INDEX_FIRST_PERSON, priceDescriptor));
    }

    @Test
    public void parse_repeatedFields_failure() {
        assertParseFailure(parser, "1" + ITEM_NAME_DESC + ITEM_NAME_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_BAKERY_ITEM_NAME));
        assertParseFailure(parser, "1" + PRICE_DESC + PRICE_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PRICE));
        assertParseFailure(parser, "1" + ITEM_NAME_DESC + PRICE_DESC + ITEM_NAME_DESC + PRICE_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_BAKERY_ITEM_NAME, PREFIX_PRICE));
    }
}
