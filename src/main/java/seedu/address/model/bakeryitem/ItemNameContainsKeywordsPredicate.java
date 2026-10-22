package seedu.address.model.bakeryitem;

import java.util.List;
import java.util.function.Predicate;

import seedu.address.commons.util.StringUtil;
import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests that a {@code BakeryItem}'s name matches any of the keywords given.
 */
public class ItemNameContainsKeywordsPredicate implements Predicate<BakeryItem> {
    private final List<String> keywords;

    public ItemNameContainsKeywordsPredicate(List<String> keywords) {
        this.keywords = keywords;
    }

    @Override
    public boolean test(BakeryItem item) {
        return keywords.stream()
                .anyMatch(keyword -> StringUtil.containsWordIgnoreCase(item.getItemName().toString(), keyword));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ItemNameContainsKeywordsPredicate otherItemNameContainsKeywordsPredicate)) {
            return false;
        }

        return keywords.equals(otherItemNameContainsKeywordsPredicate.keywords);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("keywords", keywords).toString();
    }
}
