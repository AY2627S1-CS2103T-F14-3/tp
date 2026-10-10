package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_BAKERY_ITEM_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PRICE;

import java.util.stream.Stream;

import seedu.address.logic.commands.AddBakeryItemCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.bakeryitem.BakeryItem;
import seedu.address.model.bakeryitem.BakeryItemName;
import seedu.address.model.bakeryitem.Price;

/**
 * Parses input arguments and creates a new AddBakeryItemCommand object
 */
public class AddBakeryItemCommandParser implements Parser<AddBakeryItemCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the AddCommand
     * and returns an AddCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public AddBakeryItemCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_BAKERY_ITEM_NAME, PREFIX_PRICE);

        if (!arePrefixesPresent(argMultimap, PREFIX_BAKERY_ITEM_NAME, PREFIX_PRICE)
                || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddBakeryItemCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_BAKERY_ITEM_NAME, PREFIX_PRICE);
        BakeryItemName itemName = ParserUtil.parseBakeryItemName(
                argMultimap.getValue(PREFIX_BAKERY_ITEM_NAME).get());
        Price price = ParserUtil.parsePrice(argMultimap.getValue(PREFIX_PRICE).get());

        BakeryItem bakeryItem = new BakeryItem(itemName, price);
        return new AddBakeryItemCommand(bakeryItem);
    }

    /**
     * Returns true if none of the prefixes contains empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }

}
