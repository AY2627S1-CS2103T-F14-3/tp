package seedu.address.model.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalBakeryItems.BANANA_MUFFIN;
import static seedu.address.testutil.TypicalPersons.ALICE;

import java.util.List;

import org.junit.jupiter.api.Test;

public class OrderContainsKeywordsPredicateTest {
    private final ClientOrder order = ClientOrder.create(ALICE, BANANA_MUFFIN, new Quantity(2),
            new Deadline("2026-10-22"));

    @Test
    public void test_matchesEitherNameAndAnyWholeWordIgnoringCase() {
        assertTrue(new OrderContainsKeywordsPredicate(List.of("aLiCe")).test(order));
        assertTrue(new OrderContainsKeywordsPredicate(List.of("MUFFIN")).test(order));
        assertTrue(new OrderContainsKeywordsPredicate(List.of("missing", "banana")).test(order));
        assertFalse(new OrderContainsKeywordsPredicate(List.of()).test(order));
        assertFalse(new OrderContainsKeywordsPredicate(List.of("muff", "Ali")).test(order));
        assertFalse(new OrderContainsKeywordsPredicate(List.of("2026-10-22", "2", "1.20")).test(order));
        assertFalse(new OrderContainsKeywordsPredicate(List.of(ALICE.getPhone().value, ALICE.getEmail().value))
                .test(order));
    }

    @Test
    public void equals_comparesKeywords() {
        OrderContainsKeywordsPredicate first = new OrderContainsKeywordsPredicate(List.of("banana"));
        assertTrue(first.equals(first));
        assertTrue(first.equals(new OrderContainsKeywordsPredicate(List.of("banana"))));
        assertFalse(first.equals(new OrderContainsKeywordsPredicate(List.of("alice"))));
        assertFalse(first.equals(null));
        assertFalse(first.equals(List.of("banana")));
    }

    @Test
    public void toString_includesKeywords() {
        List<String> keywords = List.of("banana", "alice");
        assertEquals(OrderContainsKeywordsPredicate.class.getCanonicalName() + "{keywords=" + keywords + "}",
                new OrderContainsKeywordsPredicate(keywords).toString());
    }
}
