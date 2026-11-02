package seedu.address.logic;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.address.logic.parser.Prefix;
import seedu.address.model.bakeryitem.BakeryItem;
import seedu.address.model.person.Person;

/**
 * Container for user visible messages.
 */
public class Messages {

    public static final String MESSAGE_UNKNOWN_COMMAND = "Unknown command.";
    public static final String MESSAGE_INVALID_COMMAND_FORMAT = "Invalid command format!\n%1$s";
    public static final String MESSAGE_INVALID_PERSON_DISPLAYED_INDEX = "The client index provided is invalid.";
    public static final String MESSAGE_INVALID_BAKERY_ITEM_DISPLAYED_INDEX =
            "The bakery item index provided is invalid.";
    public static final String MESSAGE_PERSONS_LISTED_OVERVIEW = "%1$d client(s) listed!";
    public static final String MESSAGE_DUPLICATE_FIELDS =
            "Multiple values specified for the following single-valued field(s): ";
    public static final String MESSAGE_SIMILAR_CLIENT_WARNING =
            "\nWarning: Another client has a similar name (ignoring case and extra spaces):\n%1$s\n"
            + "The change succeeded. If this was accidental, use undo before making another data change.";

    /**
     * Returns an advisory warning for similar names, excluding the changed client itself.
     */
    public static String getSimilarClientWarning(Person changedPerson, List<Person> clients) {
        String matches = clients.stream()
                .filter(client -> !client.equals(changedPerson))
                .filter(client -> client.getName().isSimilarTo(changedPerson.getName()))
                .map(Messages::format)
                .collect(Collectors.joining("\n"));
        return matches.isEmpty() ? "" : String.format(MESSAGE_SIMILAR_CLIENT_WARNING, matches);
    }

    /**
     * Returns an error message indicating the duplicate prefixes.
     */
    public static String getErrorMessageForDuplicatePrefixes(Prefix... duplicatePrefixes) {
        assert duplicatePrefixes.length > 0;

        Set<String> duplicateFields =
                Stream.of(duplicatePrefixes).map(Prefix::toString).collect(Collectors.toSet());

        return MESSAGE_DUPLICATE_FIELDS + String.join(" ", duplicateFields);
    }

    /**
     * Formats the {@code person} for display to the user.
     */
    public static String format(Person person) {
        final StringBuilder builder = new StringBuilder();
        builder.append(person.getName())
                .append("; Phone: ")
                .append(person.getPhone())
                .append("; Email: ")
                .append(person.getEmail())
                .append("; Address: ")
                .append(person.getAddress())
                .append("; Tags: ");
        person.getTags().forEach(builder::append);
        return builder.toString();
    }

    /**
     * Formats the {@code bakeryItem} for display to the user.
     */
    public static String format(BakeryItem bakeryItem) {
        return bakeryItem.getItemName() + "; Price: $" + bakeryItem.getPrice();
    }

}
