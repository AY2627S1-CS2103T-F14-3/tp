package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ORDER_INDEX;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.DeleteClientOrderCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses arguments for requests to delete a client order.
 */
public class DeleteClientOrderCommandParser implements Parser<DeleteClientOrderCommand> {
    public static final String MESSAGE_INVALID_CLIENT_INDEX = "The contact index must be a positive integer.";
    public static final String MESSAGE_INVALID_ORDER_INDEX = "The order index must be a positive integer.";

    /**
     * Parses a client index followed by exactly one order index prefix.
     *
     * @throws ParseException If a required field is missing, repeated, or invalid.
     */
    @Override
    public DeleteClientOrderCommand parse(String args) throws ParseException {
        requireNonNull(args);
        // Indices contain no free text, so normalize whitespace locally without changing other parsers.
        String normalizedArgs = args.replaceAll("\\s+", " ");
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(normalizedArgs, PREFIX_ORDER_INDEX);
        if (arguments.getPreamble().isEmpty() || arguments.getValue(PREFIX_ORDER_INDEX).isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    DeleteClientOrderCommand.MESSAGE_USAGE));
        }
        arguments.verifyNoDuplicatePrefixesFor(PREFIX_ORDER_INDEX);

        Index clientIndex = parseOrderIndex(arguments.getPreamble(), MESSAGE_INVALID_CLIENT_INDEX);
        Index orderIndex = parseOrderIndex(arguments.getValue(PREFIX_ORDER_INDEX).get(), MESSAGE_INVALID_ORDER_INDEX);
        return new DeleteClientOrderCommand(clientIndex, orderIndex);
    }

    /**
     * Parses an index with a field-specific error, including when the input overflows an integer.
     */
    private Index parseOrderIndex(String text, String errorMessage) throws ParseException {
        try {
            return ParserUtil.parseIndex(text);
        } catch (ParseException exception) {
            throw new ParseException(errorMessage, exception);
        }
    }
}
