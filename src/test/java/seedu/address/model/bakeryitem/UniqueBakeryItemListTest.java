package seedu.address.model.bakeryitem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalBakeryItems.BANANA_MUFFIN;
import static seedu.address.testutil.TypicalBakeryItems.BANANA_MUFFIN_WITH_DIFFERENT_PRICE;
import static seedu.address.testutil.TypicalBakeryItems.CHOCOLATE_MUFFIN;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.bakeryitem.exceptions.BakeryItemNotFoundException;
import seedu.address.model.bakeryitem.exceptions.DuplicateBakeryItemException;

public class UniqueBakeryItemListTest {

    private final UniqueBakeryItemList uniqueBakeryItemList = new UniqueBakeryItemList();

    @Test
    public void contains_nullBakeryItem_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueBakeryItemList.contains(null));
    }

    @Test
    public void contains_bakeryItemNotInList_returnsFalse() {
        assertFalse(uniqueBakeryItemList.contains(BANANA_MUFFIN));
    }

    @Test
    public void contains_bakeryItemInList_returnsTrue() {
        uniqueBakeryItemList.add(BANANA_MUFFIN);
        assertTrue(uniqueBakeryItemList.contains(BANANA_MUFFIN));
    }

    @Test
    public void contains_bakeryItemWithSameIdentityInList_returnsTrue() {
        uniqueBakeryItemList.add(BANANA_MUFFIN);
        assertTrue(uniqueBakeryItemList.contains(BANANA_MUFFIN_WITH_DIFFERENT_PRICE));
    }

    @Test
    public void add_nullBakeryItem_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueBakeryItemList.add(null));
    }

    @Test
    public void add_duplicateBakeryItem_throwsDuplicateBakeryItemException() {
        uniqueBakeryItemList.add(BANANA_MUFFIN);

        assertThrows(DuplicateBakeryItemException.class, ()
                -> uniqueBakeryItemList.add(BANANA_MUFFIN_WITH_DIFFERENT_PRICE));
    }

    @Test
    public void setBakeryItem_nullTargetBakeryItem_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueBakeryItemList.setBakeryItem(null, BANANA_MUFFIN));
    }

    @Test
    public void setBakeryItem_nullEditedBakeryItem_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueBakeryItemList.setBakeryItem(BANANA_MUFFIN, null));
    }

    @Test
    public void setBakeryItem_targetBakeryItemNotInList_throwsBakeryItemNotFoundException() {
        assertThrows(BakeryItemNotFoundException.class, ()
                -> uniqueBakeryItemList.setBakeryItem(BANANA_MUFFIN, BANANA_MUFFIN));
    }

    @Test
    public void setBakeryItem_editedBakeryItemIsSameBakeryItem_success() {
        uniqueBakeryItemList.add(BANANA_MUFFIN);
        uniqueBakeryItemList.setBakeryItem(BANANA_MUFFIN, BANANA_MUFFIN);

        UniqueBakeryItemList expectedList = new UniqueBakeryItemList();
        expectedList.add(BANANA_MUFFIN);
        assertEquals(expectedList, uniqueBakeryItemList);
    }

    @Test
    public void setBakeryItem_editedBakeryItemHasSameIdentity_success() {
        uniqueBakeryItemList.add(BANANA_MUFFIN);
        uniqueBakeryItemList.setBakeryItem(BANANA_MUFFIN, BANANA_MUFFIN_WITH_DIFFERENT_PRICE);

        UniqueBakeryItemList expectedList = new UniqueBakeryItemList();
        expectedList.add(BANANA_MUFFIN_WITH_DIFFERENT_PRICE);
        assertEquals(expectedList, uniqueBakeryItemList);
    }

    @Test
    public void setBakeryItem_editedBakeryItemHasDifferentIdentity_success() {
        uniqueBakeryItemList.add(BANANA_MUFFIN);
        uniqueBakeryItemList.setBakeryItem(BANANA_MUFFIN, CHOCOLATE_MUFFIN);

        UniqueBakeryItemList expectedList = new UniqueBakeryItemList();
        expectedList.add(CHOCOLATE_MUFFIN);
        assertEquals(expectedList, uniqueBakeryItemList);
    }

    @Test
    public void setBakeryItem_editedBakeryItemHasNonUniqueIdentity_throwsDuplicateBakeryItemException() {
        uniqueBakeryItemList.add(BANANA_MUFFIN);
        uniqueBakeryItemList.add(CHOCOLATE_MUFFIN);

        assertThrows(DuplicateBakeryItemException.class, ()
                -> uniqueBakeryItemList.setBakeryItem(BANANA_MUFFIN, CHOCOLATE_MUFFIN));
    }

    @Test
    public void remove_nullBakeryItem_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueBakeryItemList.remove(null));
    }

    @Test
    public void remove_bakeryItemDoesNotExist_throwsBakeryItemNotFoundException() {
        assertThrows(BakeryItemNotFoundException.class, () -> uniqueBakeryItemList.remove(BANANA_MUFFIN));
    }

    @Test
    public void remove_existingBakeryItem_removesBakeryItem() {
        uniqueBakeryItemList.add(BANANA_MUFFIN);
        uniqueBakeryItemList.remove(BANANA_MUFFIN);

        assertEquals(new UniqueBakeryItemList(), uniqueBakeryItemList);
    }

    @Test
    public void setBakeryItems_nullUniqueBakeryItemList_throwsNullPointerException() {
        assertThrows(NullPointerException.class, ()
                -> uniqueBakeryItemList.setBakeryItems((UniqueBakeryItemList) null));
    }

    @Test
    public void setBakeryItems_uniqueBakeryItemList_replacesOwnList() {
        uniqueBakeryItemList.add(BANANA_MUFFIN);
        UniqueBakeryItemList replacement = new UniqueBakeryItemList();
        replacement.add(CHOCOLATE_MUFFIN);

        uniqueBakeryItemList.setBakeryItems(replacement);

        assertEquals(replacement, uniqueBakeryItemList);
    }

    @Test
    public void setBakeryItems_nullList_throwsNullPointerException() {
        assertThrows(NullPointerException.class, ()
                -> uniqueBakeryItemList.setBakeryItems((List<BakeryItem>) null));
    }

    @Test
    public void setBakeryItems_list_replacesOwnList() {
        uniqueBakeryItemList.add(BANANA_MUFFIN);

        uniqueBakeryItemList.setBakeryItems(List.of(CHOCOLATE_MUFFIN));

        UniqueBakeryItemList expectedList = new UniqueBakeryItemList();
        expectedList.add(CHOCOLATE_MUFFIN);
        assertEquals(expectedList, uniqueBakeryItemList);
    }

    @Test
    public void setBakeryItems_listWithDuplicateBakeryItems_throwsDuplicateBakeryItemException() {
        List<BakeryItem> duplicateBakeryItems = List.of(BANANA_MUFFIN, BANANA_MUFFIN_WITH_DIFFERENT_PRICE);

        assertThrows(DuplicateBakeryItemException.class, ()
                -> uniqueBakeryItemList.setBakeryItems(duplicateBakeryItems));
    }

    @Test
    public void asUnmodifiableObservableList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, ()
                -> uniqueBakeryItemList.asUnmodifiableObservableList().add(BANANA_MUFFIN));
    }

    @Test
    public void iterator_iteratesInInsertionOrder() {
        uniqueBakeryItemList.add(BANANA_MUFFIN);
        uniqueBakeryItemList.add(CHOCOLATE_MUFFIN);

        assertEquals(List.of(BANANA_MUFFIN, CHOCOLATE_MUFFIN),
                uniqueBakeryItemList.asUnmodifiableObservableList());
    }

    @Test
    public void toStringMethod() {
        uniqueBakeryItemList.add(BANANA_MUFFIN);

        assertEquals(uniqueBakeryItemList.asUnmodifiableObservableList().toString(),
                uniqueBakeryItemList.toString());
    }
}
