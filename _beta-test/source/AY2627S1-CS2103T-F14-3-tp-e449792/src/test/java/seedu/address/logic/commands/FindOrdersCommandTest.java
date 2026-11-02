package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.order.OrderContainsKeywordsPredicate;

public class FindOrdersCommandTest {
    @Test
    public void equals_comparesSearchPredicates() {
        FindOrdersCommand first = new FindOrdersCommand(new OrderContainsKeywordsPredicate(List.of("banana")));
        FindOrdersCommand same = new FindOrdersCommand(new OrderContainsKeywordsPredicate(List.of("banana")));
        FindOrdersCommand different = new FindOrdersCommand(new OrderContainsKeywordsPredicate(List.of("alice")));
        assertTrue(first.equals(first));
        assertTrue(first.equals(same));
        assertFalse(first.equals(different));
        assertFalse(first.equals(null));
        assertFalse(first.equals(new ListOrdersCommand()));
    }

    @Test
    public void toString_includesPredicate() {
        OrderContainsKeywordsPredicate predicate = new OrderContainsKeywordsPredicate(List.of("banana"));
        assertEquals(FindOrdersCommand.class.getCanonicalName() + "{predicate=" + predicate + "}",
                new FindOrdersCommand(predicate).toString());
    }

    @Test
    public void execute_nullModel_rejected() {
        FindOrdersCommand command = new FindOrdersCommand(new OrderContainsKeywordsPredicate(List.of("banana")));
        assertThrows(NullPointerException.class, () -> command.execute(null));
    }
}
