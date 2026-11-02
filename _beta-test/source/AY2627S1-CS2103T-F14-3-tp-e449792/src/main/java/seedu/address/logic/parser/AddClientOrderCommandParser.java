package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DEADLINE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ITEM_INDEX;
import static seedu.address.logic.parser.CliSyntax.PREFIX_QUANTITY;

import java.util.stream.Stream;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.AddClientOrderCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.order.Deadline;
import seedu.address.model.order.Quantity;

/**
 * Parses client order arguments into a validated add-order command.
 */
public class AddClientOrderCommandParser implements Parser<AddClientOrderCommand> {
    public static final String MESSAGE_INVALID_CLIENT_INDEX = "The contact index must be a positive integer.";
    public static final String MESSAGE_INVALID_ITEM_INDEX = "The item index must be a positive integer.";

    /**
     * Parses the client index and required item, quantity, and deadline fields.
     *
     * @throws ParseException If a required field is missing, repeated, or invalid.
     */
    @Override
    public AddClientOrderCommand parse(String args) throws ParseException {
        requireNonNull(args);
        // These fields contain no free text; normalize whitespace without changing other command parsers.
        String normalizedArgs = args.replaceAll("\\s+", " ");
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(normalizedArgs,
                PREFIX_ITEM_INDEX, PREFIX_QUANTITY, PREFIX_DEADLINE);
        if (arguments.getPreamble().isEmpty()
                || !Stream.of(PREFIX_ITEM_INDEX, PREFIX_QUANTITY, PREFIX_DEADLINE)
                        .allMatch(prefix -> arguments.getValue(prefix).isPresent())) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    AddClientOrderCommand.MESSAGE_USAGE));
        }
        arguments.verifyNoDuplicatePrefixesFor(PREFIX_ITEM_INDEX, PREFIX_QUANTITY, PREFIX_DEADLINE);

        Index clientIndex = parseOrderIndex(arguments.getPreamble(), MESSAGE_INVALID_CLIENT_INDEX);
        Index itemIndex = parseOrderIndex(arguments.getValue(PREFIX_ITEM_INDEX).get(), MESSAGE_INVALID_ITEM_INDEX);
        Quantity quantity = ParserUtil.parseQuantity(arguments.getValue(PREFIX_QUANTITY).get());
        Deadline deadline = ParserUtil.parseDeadline(arguments.getValue(PREFIX_DEADLINE).get());
        return new AddClientOrderCommand(clientIndex, itemIndex, quantity, deadline);
    }

    /**
     * Parses an index with an error identifying the relevant order field.
     */
    private Index parseOrderIndex(String text, String errorMessage) throws ParseException {
        try {
            return ParserUtil.parseIndex(text);
        } catch (ParseException exception) {
            throw new ParseException(errorMessage, exception);
        }
    }
}
