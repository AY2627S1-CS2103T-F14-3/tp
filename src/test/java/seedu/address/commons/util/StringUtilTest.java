package seedu.address.commons.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.io.FileNotFoundException;
import java.util.Locale;

import org.junit.jupiter.api.Test;

public class StringUtilTest {

    //---------------- Tests for isNonZeroUnsignedInteger --------------------------------------

    @Test
    public void isNonZeroUnsignedInteger() {

        // EP: empty strings
        assertFalse(StringUtil.isNonZeroUnsignedInteger("")); // Boundary value
        assertFalse(StringUtil.isNonZeroUnsignedInteger("  "));

        // EP: not a number
        assertFalse(StringUtil.isNonZeroUnsignedInteger("a"));
        assertFalse(StringUtil.isNonZeroUnsignedInteger("aaa"));

        // EP: zero
        assertFalse(StringUtil.isNonZeroUnsignedInteger("0"));

        // EP: zero as prefix
        assertTrue(StringUtil.isNonZeroUnsignedInteger("01"));

        // EP: signed numbers
        assertFalse(StringUtil.isNonZeroUnsignedInteger("-1"));
        assertFalse(StringUtil.isNonZeroUnsignedInteger("+1"));

        // EP: numbers with white space
        assertFalse(StringUtil.isNonZeroUnsignedInteger(" 10 ")); // Leading/trailing spaces
        assertFalse(StringUtil.isNonZeroUnsignedInteger("1 0")); // Spaces in the middle

        // EP: number larger than Integer.MAX_VALUE
        assertFalse(StringUtil.isNonZeroUnsignedInteger(Long.toString(Integer.MAX_VALUE + 1)));

        // EP: valid numbers, should return true
        assertTrue(StringUtil.isNonZeroUnsignedInteger("1")); // Boundary value
        assertTrue(StringUtil.isNonZeroUnsignedInteger("10"));
    }


    //---------------- Tests for containsKeywordIgnoreCase --------------------------------------

    /*
     * Invalid equivalence partitions for word: null, empty, multiple words
     * Invalid equivalence partitions for sentence: null
     * The four test cases below test one invalid input at a time.
     */

    @Test
    public void containsKeywordIgnoreCase_nullWord_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> StringUtil.containsKeywordIgnoreCase("typical sentence", null));
    }

    @Test
    public void containsKeywordIgnoreCase_emptyWord_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, "Word parameter cannot be empty", ()
            -> StringUtil.containsKeywordIgnoreCase("typical sentence", "  "));
    }

    @Test
    public void containsKeywordIgnoreCase_multipleWords_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, "Word parameter should be a single word", ()
            -> StringUtil.containsKeywordIgnoreCase("typical sentence", "aaa BBB"));
    }

    @Test
    public void containsKeywordIgnoreCase_nullSentence_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> StringUtil.containsKeywordIgnoreCase(null, "abc"));
    }

    /*
     * Valid equivalence partitions for word:
     *   - any word
     *   - word containing symbols/numbers
     *   - word with leading/trailing spaces
     *
     * Valid equivalence partitions for sentence:
     *   - empty string
     *   - one word
     *   - multiple words
     *   - sentence with extra spaces
     *
     * Possible scenarios returning true:
     *   - matches first word in sentence
     *   - last word in sentence
     *   - middle word in sentence
     *   - matches multiple words
     *
     * Possible scenarios returning false:
     *   - sentence word matches part of the query word
     *
     * The test method below tries to verify all above with a reasonably low number of test cases.
     */

    @Test
    public void containsKeywordIgnoreCase_validInputs_correctResult() {

        // Empty sentence
        assertFalse(StringUtil.containsKeywordIgnoreCase("", "abc")); // Boundary case
        assertFalse(StringUtil.containsKeywordIgnoreCase("    ", "123"));

        // Matches a partial word only
        assertTrue(StringUtil.containsKeywordIgnoreCase("aaa bbb ccc", "bb")); // Sentence word bigger than query word
        assertFalse(StringUtil.containsKeywordIgnoreCase("aaa bbb ccc", "bbbb")); // Query longer than name word

        // Matches word in the sentence, different upper/lower case letters
        assertTrue(StringUtil.containsKeywordIgnoreCase("aaa bBb ccc", "Bbb")); // First word (boundary case)
        assertTrue(StringUtil.containsKeywordIgnoreCase("aaa bBb ccc@1", "CCc@1")); // Last word (boundary case)
        assertTrue(StringUtil.containsKeywordIgnoreCase("  AAA   bBb   ccc  ", "aaa")); // Sentence has extra spaces
        assertTrue(StringUtil.containsKeywordIgnoreCase("Aaa", "aaa")); // Only one word in sentence (boundary case)
        assertTrue(StringUtil.containsKeywordIgnoreCase("aaa bbb ccc", "  ccc  ")); // Leading/trailing spaces

        // Matches multiple words in sentence
        assertTrue(StringUtil.containsKeywordIgnoreCase("AAA bBb ccc  bbb", "bbB"));
    }

    @Test
    public void containsKeywordIgnoreCase_fragmentsAndPunctuation_matchLiterally() {
        assertTrue(StringUtil.containsKeywordIgnoreCase("Cupcake", "cup"));
        assertTrue(StringUtil.containsKeywordIgnoreCase("Cupcake", "pca"));
        assertTrue(StringUtil.containsKeywordIgnoreCase("Cupcake", "cake"));
        assertTrue(StringUtil.containsKeywordIgnoreCase("Chocolate (Gluten Free)", "fReE"));
        assertTrue(StringUtil.containsKeywordIgnoreCase("Chocolate (Gluten Free)", "Free)"));
        assertTrue(StringUtil.containsKeywordIgnoreCase("Anne-Marie O'Neil", "neil"));
        assertTrue(StringUtil.containsKeywordIgnoreCase("Chocolate (Gluten Free)", "("));
        assertFalse(StringUtil.containsKeywordIgnoreCase("Chocolate (Gluten Free)", "."));
        assertFalse(StringUtil.containsKeywordIgnoreCase("Chocolate (Gluten Free)", "Free.*"));
    }

    @Test
    public void containsKeywordIgnoreCase_turkishDefaultLocale_matchesAsciiIgnoringCase() {
        Locale previousLocale = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            assertTrue(StringUtil.containsKeywordIgnoreCase("VANILLA", "illa"));
            assertTrue(StringUtil.containsKeywordIgnoreCase("vanilla", "ILLA"));
        } finally {
            Locale.setDefault(previousLocale);
        }
    }

    //---------------- Tests for getDetails --------------------------------------

    /*
     * Equivalence Partitions: null, valid throwable object
     */

    @Test
    public void getDetails_exceptionGiven() {
        assertTrue(StringUtil.getDetails(new FileNotFoundException("file not found"))
            .contains("java.io.FileNotFoundException: file not found"));
    }

    @Test
    public void getDetails_nullGiven_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> StringUtil.getDetails(null));
    }

}
