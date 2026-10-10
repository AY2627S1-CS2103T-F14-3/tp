package seedu.address.model.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class DeadlineTest {
    @Test
    public void constructor_invalidDate_throwsIllegalArgumentException() {
        assertThrows(NullPointerException.class, () -> new Deadline(null));
        assertThrows(IllegalArgumentException.class, () -> new Deadline("2026-02-29"));
        assertThrows(IllegalArgumentException.class, () -> new Deadline("2026-2-01"));
    }

    @Test
    public void isValidDeadline_invalidDate_returnsFalse() {
        assertThrows(NullPointerException.class, () -> Deadline.isValidDeadline(null));
        for (String invalid : new String[] {"", " ", "2026-02-29", "1900-02-29", "2026-04-31",
            "2026-00-01", "2026-13-01", "2026-01-00", "2026-01-32", "2026-1-01", "2026-01-1",
            "26-01-01", "10000-01-01", "2026/01/01", "2026-01-01T12:00", " 2026-01-01 "}) {
            assertFalse(Deadline.isValidDeadline(invalid), invalid);
        }
    }

    @Test
    public void isValidDeadline_validCalendarDate_returnsTrue() {
        for (String valid : new String[] {"2024-02-29", "2000-02-29", "2026-04-30", "1990-01-01",
            "0001-01-01", "9999-12-31"}) {
            assertTrue(Deadline.isValidDeadline(valid), valid);
        }
        assertEquals(LocalDate.of(1990, 1, 1), new Deadline("1990-01-01").getValue());
    }

    @Test
    public void equals_sameDate_returnsTrue() {
        Deadline deadline = new Deadline("2026-10-15");
        assertEquals(deadline, new Deadline("2026-10-15"));
        assertEquals(deadline.hashCode(), new Deadline("2026-10-15").hashCode());
        assertFalse(deadline.equals(new Deadline("2026-10-16")));
        assertFalse(deadline.equals(null));
        assertFalse(deadline.equals("2026-10-15"));
        assertEquals("2026-10-15", deadline.toString());
    }
}
