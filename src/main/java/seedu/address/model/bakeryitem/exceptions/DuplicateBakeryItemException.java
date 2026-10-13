package seedu.address.model.bakeryitem.exceptions;

/**
 * Signals that the operation will result in duplicate bakery items.
 */
public class DuplicateBakeryItemException extends RuntimeException {
    public DuplicateBakeryItemException() {
        super("Operation would result in duplicate bakery items");
    }
}
