package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DEADLINE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ITEM_INDEX;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ORDER_INDEX;
import static seedu.address.logic.parser.CliSyntax.PREFIX_QUANTITY;

import java.util.Optional;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.EditClientOrderCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.order.Deadline;
import seedu.address.model.order.Quantity;

/**
 * Parses an order selection and optional replacement fields, rejecting invalid input before mutation.
 */
public class EditClientOrderCommandParser implements Parser<EditClientOrderCommand> {
    @Override
    public EditClientOrderCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(args.replaceAll("\\s+", " "),
                PREFIX_ORDER_INDEX, PREFIX_ITEM_INDEX, PREFIX_QUANTITY, PREFIX_DEADLINE);
        if (arguments.getPreamble().isEmpty() || arguments.getValue(PREFIX_ORDER_INDEX).isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    EditClientOrderCommand.MESSAGE_USAGE));
        }
        arguments.verifyNoDuplicatePrefixesFor(PREFIX_ORDER_INDEX, PREFIX_ITEM_INDEX, PREFIX_QUANTITY, PREFIX_DEADLINE);
        Index client = parseIndex(arguments.getPreamble(), MarkOrderCompleteCommandParser.MESSAGE_INVALID_CLIENT_INDEX);
        Index order = parseIndex(arguments.getValue(PREFIX_ORDER_INDEX).get(),
                MarkOrderCompleteCommandParser.MESSAGE_INVALID_ORDER_INDEX);
        Optional<Index> item = Optional.empty();
        Optional<Quantity> quantity = Optional.empty();
        Optional<Deadline> deadline = Optional.empty();
        if (arguments.getValue(PREFIX_ITEM_INDEX).isPresent()) {
            item = Optional.of(parseIndex(arguments.getValue(PREFIX_ITEM_INDEX).get(),
                    AddClientOrderCommandParser.MESSAGE_INVALID_ITEM_INDEX));
        }
        if (arguments.getValue(PREFIX_QUANTITY).isPresent()) {
            quantity = Optional.of(ParserUtil.parseQuantity(arguments.getValue(PREFIX_QUANTITY).get()));
        }
        if (arguments.getValue(PREFIX_DEADLINE).isPresent()) {
            deadline = Optional.of(ParserUtil.parseDeadline(arguments.getValue(PREFIX_DEADLINE).get()));
        }
        if (item.isEmpty() && quantity.isEmpty() && deadline.isEmpty()) {
            throw new ParseException(EditClientOrderCommand.MESSAGE_NOT_EDITED);
        }
        return new EditClientOrderCommand(client, order, item, quantity, deadline);
    }

    private Index parseIndex(String value, String message) throws ParseException {
        try {
            return ParserUtil.parseIndex(value);
        } catch (ParseException exception) {
            throw new ParseException(message, exception);
        }
    }
}
