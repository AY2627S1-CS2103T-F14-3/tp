package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddBakeryItemCommand;
import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.EditBakeryItemCommand;
import seedu.address.logic.commands.EditBakeryItemCommand.EditBakeryItemDescriptor;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.ListItemsCommand;
import seedu.address.logic.commands.ListOrdersCommand;
import seedu.address.logic.commands.PopulateCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.bakeryitem.BakeryItem;
import seedu.address.model.bakeryitem.BakeryItemName;
import seedu.address.model.bakeryitem.Price;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Person;
import seedu.address.testutil.EditPersonDescriptorBuilder;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.PersonUtil;

public class AddressBookParserTest {

    private final AddressBookParser parser = new AddressBookParser();

    @Test
    public void parseCommand_add() throws Exception {
        Person person = new PersonBuilder().build();
        AddCommand command = (AddCommand) parser.parseCommand(PersonUtil.getAddCommand(person));
        assertEquals(new AddCommand(person), command);
    }

    @Test
    public void parseCommand_addBakeryItem() throws Exception {
        BakeryItem bakeryItem = new BakeryItem(new BakeryItemName("Banana Muffin"), Price.fromDollars("1.20"));
        AddBakeryItemCommand command = (AddBakeryItemCommand) parser.parseCommand("add-item n/Banana Muffin p/1.20");
        assertEquals(new AddBakeryItemCommand(bakeryItem), command);
    }

    @Test
    public void parseCommand_populate() throws Exception {
        assertTrue(parser.parseCommand(PopulateCommand.COMMAND_WORD) instanceof PopulateCommand);
        assertTrue(parser.parseCommand(PopulateCommand.COMMAND_WORD + " extra") instanceof PopulateCommand);
    }

    @Test
    public void parseCommand_clear() throws Exception {
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD) instanceof ClearCommand);
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD + " confirm") instanceof ClearCommand);
        assertThrows(ParseException.class, () -> parser.parseCommand(ClearCommand.COMMAND_WORD + " 3"));
    }

    @Test
    public void parseCommand_delete() throws Exception {
        DeleteCommand command = (DeleteCommand) parser.parseCommand(
                DeleteCommand.COMMAND_WORD + " " + INDEX_FIRST_PERSON.getOneBased());
        assertEquals(new DeleteCommand(INDEX_FIRST_PERSON), command);
    }

    @Test
    public void parseCommand_edit() throws Exception {
        Person person = new PersonBuilder().build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(person).build();
        EditCommand command = (EditCommand) parser.parseCommand(EditCommand.COMMAND_WORD + " "
                + INDEX_FIRST_PERSON.getOneBased() + " " + PersonUtil.getEditPersonDescriptorDetails(descriptor));
        assertEquals(new EditCommand(INDEX_FIRST_PERSON, descriptor), command);
    }

    @Test
    public void parseCommand_editBakeryItem() throws Exception {
        EditBakeryItemDescriptor descriptor = new EditBakeryItemDescriptor();
        descriptor.setItemName(new BakeryItemName("Blueberry Muffin"));
        descriptor.setPrice(Price.fromDollars("2.50"));
        EditBakeryItemCommand command = (EditBakeryItemCommand) parser.parseCommand(
                "edit-item 1 n/Blueberry Muffin p/2.50");

        assertEquals(new EditBakeryItemCommand(INDEX_FIRST_PERSON, descriptor), command);
    }

    @Test
    public void parseCommand_exit() throws Exception {
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD) instanceof ExitCommand);
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD + " 3") instanceof ExitCommand);
    }

    @Test
    public void parseCommand_find() throws Exception {
        List<String> keywords = List.of("foo", "bar", "baz");
        FindCommand command = (FindCommand) parser.parseCommand(
                FindCommand.COMMAND_WORD + " " + keywords.stream().collect(Collectors.joining(" ")));
        assertEquals(new FindCommand(new NameContainsKeywordsPredicate(keywords)), command);
    }

    @Test
    public void parseCommand_help() throws Exception {
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD) instanceof HelpCommand);
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD + " 3") instanceof HelpCommand);
    }

    @Test
    public void parseCommand_listOrders() throws Exception {
        assertTrue(parser.parseCommand(ListOrdersCommand.COMMAND_WORD) instanceof ListOrdersCommand);
        assertTrue(parser.parseCommand(ListOrdersCommand.COMMAND_WORD + " 3") instanceof ListOrdersCommand);
    }

    @Test
    public void parseCommand_listItems() throws Exception {
        assertTrue(parser.parseCommand(ListItemsCommand.COMMAND_WORD) instanceof ListItemsCommand);
        assertTrue(parser.parseCommand(ListItemsCommand.COMMAND_WORD + " 3") instanceof ListItemsCommand);
    }

    @Test
    public void parseCommand_list() throws Exception {
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD) instanceof ListCommand);
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD + " 3") instanceof ListCommand);
    }

    @Test
    public void parseCommand_unrecognisedInput_throwsParseException() {
        assertThrows(ParseException.class, String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE), ()
            -> parser.parseCommand(""));
    }

    @Test
    public void parseCommand_clientCommands_acceptExplicitNames() throws Exception {
        assertTrue(parser.parseCommand("add-client n/Alice p/12345678 e/alice@example.com a/Somewhere")
                instanceof AddCommand);
        assertTrue(parser.parseCommand("delete-client 1") instanceof DeleteCommand);
        assertTrue(parser.parseCommand("edit-client 1 n/Alice") instanceof EditCommand);
        assertTrue(parser.parseCommand("find-clients Alice") instanceof FindCommand);
        assertTrue(parser.parseCommand("list-clients") instanceof ListCommand);
    }

    @Test
    public void parseCommand_genericClientCommands_rejected() {
        for (String command : List.of("add", "delete", "edit", "find", "list")) {
            assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand(command));
        }
    }

    @Test
    public void parseCommand_unknownCommand_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand("unknownCommand"));
    }
}
