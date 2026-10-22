package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.testutil.TypicalBakeryItems.BANANA_MUFFIN;
import static seedu.address.testutil.TypicalBakeryItems.CHOCOLATE_MUFFIN;
import static seedu.address.testutil.TypicalPersons.ALICE;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.AddressBookParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.ModelManager;

public class FindItemsCommandTest {
    private final AddressBookParser parser = new AddressBookParser();

    @Test
    public void execute_keywords_matchAnyWholeWordIgnoringCaseAndRestore() throws Exception {
        ModelManager model = new ModelManager();
        model.addBakeryItem(BANANA_MUFFIN);
        model.addBakeryItem(CHOCOLATE_MUFFIN);
        AddressBook before = new AddressBook(model.getAddressBook());
        assertEquals("2 bakery items listed!", parser.parseCommand("find-items BANANA chocolate")
                .execute(model).getFeedbackToUser());
        assertEquals(List.of(BANANA_MUFFIN, CHOCOLATE_MUFFIN), model.getFilteredBakeryItemList());
        parser.parseCommand("find-items muff").execute(model);
        assertEquals(List.of(), model.getFilteredBakeryItemList());
        parser.parseCommand("find-items \t CHOCOLATE \n").execute(model);
        assertEquals(List.of(CHOCOLATE_MUFFIN), model.getFilteredBakeryItemList());
        assertThrows(ParseException.class, () -> parser.parseCommand("find-items \t"));
        assertEquals(List.of(CHOCOLATE_MUFFIN), model.getFilteredBakeryItemList());
        assertEquals(before, model.getAddressBook());
        parser.parseCommand("list-items").execute(model);
        assertEquals(List.of(BANANA_MUFFIN, CHOCOLATE_MUFFIN), model.getFilteredBakeryItemList());
    }

    @Test
    public void execute_filteredIndices_addEditAndDeleteUseDisplayedItem() throws Exception {
        ModelManager model = new ModelManager();
        model.addPerson(ALICE);
        model.addBakeryItem(BANANA_MUFFIN);
        model.addBakeryItem(CHOCOLATE_MUFFIN);
        parser.parseCommand("find-items chocolate").execute(model);
        parser.parseCommand("add-order 1 i/1 q/2 d/2026-10-15").execute(model);
        assertEquals(CHOCOLATE_MUFFIN, model.getOrdersForClient(ALICE).get(0).getItem());
        parser.parseCommand("edit-item 1 n/Chocolate Cake").execute(model);
        assertEquals("Chocolate Cake", model.getOrdersForClient(ALICE).get(0).getItem().getItemName().toString());
        model.deleteOrder(model.getOrdersForClient(ALICE).get(0));
        parser.parseCommand("find-items Chocolate").execute(model);
        parser.parseCommand("delete-item 1").execute(model);
        assertEquals(List.of(BANANA_MUFFIN), model.getAddressBook().getBakeryItemList());
    }
}
