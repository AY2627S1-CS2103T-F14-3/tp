package seedu.address.logic.commands;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.order.ClientOrder;
import seedu.address.model.person.Person;

/**
 * Resolves displayed client indices and client-local order indices consistently across order commands.
 */
public final class OrderCommandUtil {
    public static final String MESSAGE_INVALID_CLIENT = "Client not found in the displayed client list.";
    public static final String MESSAGE_INVALID_ORDER = "The order index provided is invalid for this contact.";

    private OrderCommandUtil() {}

    /**
     * Returns the selected client's order, rejecting out-of-range indices before any mutation.
     */
    public static ClientOrder findOrder(Model model, Index clientIndex, Index orderIndex) throws CommandException {
        requireAllNonNull(model, clientIndex, orderIndex);
        if (clientIndex.getZeroBased() >= model.getFilteredPersonList().size()) {
            throw new CommandException(MESSAGE_INVALID_CLIENT);
        }
        Person client = model.getFilteredPersonList().get(clientIndex.getZeroBased());
        List<ClientOrder> orders = model.getOrdersForClient(client);
        if (orderIndex.getZeroBased() >= orders.size()) {
            throw new CommandException(MESSAGE_INVALID_ORDER);
        }
        return orders.get(orderIndex.getZeroBased());
    }
}
