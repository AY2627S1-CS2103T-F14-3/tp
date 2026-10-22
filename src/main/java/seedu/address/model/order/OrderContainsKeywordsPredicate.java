package seedu.address.model.order;

import java.util.List;
import java.util.function.Predicate;

import seedu.address.commons.util.StringUtil;
import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests that an order's client or item name matches any of the keywords given.
 */
public class OrderContainsKeywordsPredicate implements Predicate<ClientOrder> {
    private final List<String> keywords;

    public OrderContainsKeywordsPredicate(List<String> keywords) {
        this.keywords = keywords;
    }

    @Override
    public boolean test(ClientOrder order) {
        return keywords.stream()
                .anyMatch(keyword -> StringUtil.containsWordIgnoreCase(order.getClient().getName().fullName, keyword)
                        || StringUtil.containsWordIgnoreCase(order.getItem().getItemName().toString(), keyword));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof OrderContainsKeywordsPredicate otherOrderContainsKeywordsPredicate)) {
            return false;
        }

        return keywords.equals(otherOrderContainsKeywordsPredicate.keywords);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("keywords", keywords).toString();
    }
}
