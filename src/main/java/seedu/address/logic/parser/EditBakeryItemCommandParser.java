package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_BAKERY_ITEM_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PRICE;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.EditBakeryItemCommand;
import seedu.address.logic.commands.EditBakeryItemCommand.EditBakeryItemDescriptor;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new {@code EditBakeryItemCommand}.
 */
public class EditBakeryItemCommandParser implements Parser<EditBakeryItemCommand> {

    /**
     * Parses the given arguments in the context of {@code EditBakeryItemCommand}.
     *
     * @throws ParseException if the user input does not conform to the expected format.
     */
    public EditBakeryItemCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args,
                PREFIX_BAKERY_ITEM_NAME, PREFIX_PRICE);

        Index index;
        try {
            index = ParserUtil.parseIndex(argMultimap.getPreamble());
        } catch (ParseException exception) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    EditBakeryItemCommand.MESSAGE_USAGE), exception);
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_BAKERY_ITEM_NAME, PREFIX_PRICE);

        EditBakeryItemDescriptor editBakeryItemDescriptor = new EditBakeryItemDescriptor();
        if (argMultimap.getValue(PREFIX_BAKERY_ITEM_NAME).isPresent()) {
            editBakeryItemDescriptor.setItemName(ParserUtil.parseBakeryItemName(
                    argMultimap.getValue(PREFIX_BAKERY_ITEM_NAME).get()));
        }
        if (argMultimap.getValue(PREFIX_PRICE).isPresent()) {
            editBakeryItemDescriptor.setPrice(ParserUtil.parsePrice(
                    argMultimap.getValue(PREFIX_PRICE).get()));
        }

        if (!editBakeryItemDescriptor.isAnyFieldEdited()) {
            throw new ParseException(EditBakeryItemCommand.MESSAGE_NOT_EDITED);
        }

        return new EditBakeryItemCommand(index, editBakeryItemDescriptor);
    }
}
