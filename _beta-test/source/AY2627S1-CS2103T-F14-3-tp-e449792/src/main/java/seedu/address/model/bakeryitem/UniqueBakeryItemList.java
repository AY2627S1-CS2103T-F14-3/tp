package seedu.address.model.bakeryitem;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Iterator;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.bakeryitem.exceptions.BakeryItemNotFoundException;
import seedu.address.model.bakeryitem.exceptions.DuplicateBakeryItemException;

/**
 * A list of bakery items that enforces uniqueness between its elements and does not allow nulls.
 * A bakery item is considered unique by comparing it using
 * {@link BakeryItem#isSameBakeryItem(BakeryItem)}.
 */
public class UniqueBakeryItemList implements Iterable<BakeryItem> {

    private final ObservableList<BakeryItem> internalList = FXCollections.observableArrayList();
    private final ObservableList<BakeryItem> internalUnmodifiableList =
            FXCollections.unmodifiableObservableList(internalList);

    /**
     * Returns true if the list contains an equivalent bakery item as the given argument.
     */
    public boolean contains(BakeryItem toCheck) {
        requireNonNull(toCheck);
        return internalList.stream().anyMatch(toCheck::isSameBakeryItem);
    }

    /**
     * Adds a bakery item to the list.
     * The bakery item must not already exist in the list.
     */
    public void add(BakeryItem toAdd) {
        requireNonNull(toAdd);
        if (contains(toAdd)) {
            throw new DuplicateBakeryItemException();
        }
        internalList.add(toAdd);
    }

    /**
     * Replaces the bakery item {@code target} in the list with {@code editedBakeryItem}.
     * {@code target} must exist in the list.
     * The identity of {@code editedBakeryItem} must not match another existing bakery item.
     */
    public void setBakeryItem(BakeryItem target, BakeryItem editedBakeryItem) {
        requireAllNonNull(target, editedBakeryItem);

        int index = internalList.indexOf(target);
        if (index == -1) {
            throw new BakeryItemNotFoundException();
        }

        if (!target.isSameBakeryItem(editedBakeryItem) && contains(editedBakeryItem)) {
            throw new DuplicateBakeryItemException();
        }

        internalList.set(index, editedBakeryItem);
    }

    /**
     * Removes the equivalent bakery item from the list.
     * The bakery item must exist in the list.
     */
    public void remove(BakeryItem toRemove) {
        requireNonNull(toRemove);
        if (!internalList.remove(toRemove)) {
            throw new BakeryItemNotFoundException();
        }
    }

    /**
     * Replaces the contents of this list with {@code replacement}.
     */
    public void setBakeryItems(UniqueBakeryItemList replacement) {
        requireNonNull(replacement);
        internalList.setAll(replacement.internalList);
    }

    /**
     * Replaces the contents of this list with {@code bakeryItems}.
     * {@code bakeryItems} must not contain duplicate bakery items.
     */
    public void setBakeryItems(List<BakeryItem> bakeryItems) {
        requireAllNonNull(bakeryItems);
        if (!bakeryItemsAreUnique(bakeryItems)) {
            throw new DuplicateBakeryItemException();
        }

        internalList.setAll(bakeryItems);
    }

    /**
     * Returns the backing list as an unmodifiable {@code ObservableList}.
     */
    public ObservableList<BakeryItem> asUnmodifiableObservableList() {
        return internalUnmodifiableList;
    }

    @Override
    public Iterator<BakeryItem> iterator() {
        return internalUnmodifiableList.iterator();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof UniqueBakeryItemList otherUniqueBakeryItemList)) {
            return false;
        }

        return internalList.equals(otherUniqueBakeryItemList.internalList);
    }

    @Override
    public int hashCode() {
        return internalList.hashCode();
    }

    @Override
    public String toString() {
        return internalList.toString();
    }

    /**
     * Returns true if {@code bakeryItems} contains only unique bakery items.
     */
    private boolean bakeryItemsAreUnique(List<BakeryItem> bakeryItems) {
        for (int i = 0; i < bakeryItems.size() - 1; i++) {
            for (int j = i + 1; j < bakeryItems.size(); j++) {
                if (bakeryItems.get(i).isSameBakeryItem(bakeryItems.get(j))) {
                    return false;
                }
            }
        }
        return true;
    }
}
