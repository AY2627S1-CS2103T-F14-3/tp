package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class NameTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Name(null));
    }

    @Test
    public void constructor_invalidName_throwsIllegalArgumentException() {
        String invalidName = "";
        assertThrows(IllegalArgumentException.class, () -> new Name(invalidName));
    }

    @Test
    public void isValidName() {
        // null name
        assertThrows(NullPointerException.class, () -> Name.isValidName(null));

        // invalid name
        assertFalse(Name.isValidName("")); // empty string
        assertFalse(Name.isValidName(" ")); // spaces only
        assertFalse(Name.isValidName(" Peter")); // leading space
        assertFalse(Name.isValidName("Anne\tMarie")); // tab
        assertFalse(Name.isValidName("Anne\nMarie")); // newline
        assertFalse(Name.isValidName("Anne\rMarie")); // carriage return
        assertFalse(Name.isValidName("Anne\u0000Marie")); // null character
        assertFalse(Name.isValidName("Anne\u007fMarie")); // delete character
        assertFalse(Name.isValidName("O\u2019Neil")); // non-ASCII apostrophe

        // valid name
        assertTrue(Name.isValidName("peter jack")); // alphabets only
        assertTrue(Name.isValidName("Anne-Marie O'Neil"));
        assertTrue(Name.isValidName("Siti d/o Rahim"));
        assertTrue(Name.isValidName("Raj s/o Kumar"));
        assertTrue(Name.isValidName("Dr. Tan (Jr.)"));
        assertTrue(Name.isValidName("^")); // printable punctuation
        assertTrue(Name.isValidName("peter*"));
        assertTrue(Name.isValidName("12345")); // numbers only
        assertTrue(Name.isValidName("peter the 2nd")); // alphanumeric characters
        assertTrue(Name.isValidName("Capital Tan")); // with capital letters
        assertTrue(Name.isValidName("David Roger Jackson Ray Jr 2nd")); // long names
    }

    @Test
    public void equals() {
        Name name = new Name("Valid Name");

        // same values -> returns true
        assertTrue(name.equals(new Name("Valid Name")));

        // same object -> returns true
        assertTrue(name.equals(name));

        // null -> returns false
        assertFalse(name.equals(null));

        // different types -> returns false
        assertFalse(name.equals(5.0f));

        // different values -> returns false
        assertFalse(name.equals(new Name("Other Valid Name")));
    }
}
