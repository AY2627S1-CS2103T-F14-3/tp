package seedu.address.model.bakeryitem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalBakeryItems.BANANA_MUFFIN;

import java.util.List;

import org.junit.jupiter.api.Test;

public class ItemNameContainsKeywordsPredicateTest {
    @Test
    public void test_matchesAnyWholeWordIgnoringCase() {
        assertTrue(new ItemNameContainsKeywordsPredicate(List.of("bAnAnA")).test(BANANA_MUFFIN));
        assertTrue(new ItemNameContainsKeywordsPredicate(List.of("missing", "MUFFIN")).test(BANANA_MUFFIN));
        assertFalse(new ItemNameContainsKeywordsPredicate(List.of()).test(BANANA_MUFFIN));
        assertFalse(new ItemNameContainsKeywordsPredicate(List.of("muff")).test(BANANA_MUFFIN));
        assertFalse(new ItemNameContainsKeywordsPredicate(List.of("1.20")).test(BANANA_MUFFIN));
    }

    @Test
    public void equals_comparesKeywords() {
        ItemNameContainsKeywordsPredicate first = new ItemNameContainsKeywordsPredicate(List.of("banana"));
        assertTrue(first.equals(first));
        assertTrue(first.equals(new ItemNameContainsKeywordsPredicate(List.of("banana"))));
        assertFalse(first.equals(new ItemNameContainsKeywordsPredicate(List.of("muffin"))));
        assertFalse(first.equals(null));
        assertFalse(first.equals(List.of("banana")));
    }

    @Test
    public void toString_includesKeywords() {
        List<String> keywords = List.of("banana", "muffin");
        assertEquals(ItemNameContainsKeywordsPredicate.class.getCanonicalName() + "{keywords=" + keywords + "}",
                new ItemNameContainsKeywordsPredicate(keywords).toString());
    }
}
