package seedu.address.model.util;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.bakeryitem.BakeryItem;
import seedu.address.model.bakeryitem.BakeryItemName;
import seedu.address.model.bakeryitem.Price;
import seedu.address.model.order.ClientOrder;
import seedu.address.model.order.Deadline;
import seedu.address.model.order.Quantity;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;

/**
 * Contains utility methods for populating {@code AddressBook} with sample data.
 */
public class SampleDataUtil {
    public static Person[] getSamplePersons() {
        return new Person[] {
            new Person(new Name("Alex Yeoh"), new Phone("87438807"), new Email("alexyeoh@example.com"),
                new Address("Blk 30 Geylang Street 29, #06-40"),
                getTagSet("friends")),
            new Person(new Name("Bernice Yu"), new Phone("99272758"), new Email("berniceyu@example.com"),
                new Address("Blk 30 Lorong 3 Serangoon Gardens, #07-18"),
                getTagSet("colleagues", "friends")),
            new Person(new Name("Charlotte Oliveiro"), new Phone("93210283"), new Email("charlotte@example.com"),
                new Address("Blk 11 Ang Mo Kio Street 74, #11-04"),
                getTagSet("neighbours")),
            new Person(new Name("David Li"), new Phone("91031282"), new Email("lidavid@example.com"),
                new Address("Blk 436 Serangoon Gardens Street 26, #16-43"),
                getTagSet("family")),
            new Person(new Name("Irfan Ibrahim"), new Phone("92492021"), new Email("irfan@example.com"),
                new Address("Blk 47 Tampines Street 20, #17-35"),
                getTagSet("classmates")),
            new Person(new Name("Roy Balakrishnan"), new Phone("92624417"), new Email("royb@example.com"),
                new Address("Blk 45 Aljunied Street 85, #11-31"),
                getTagSet("colleagues"))
        };
    }

    /**
     * Returns sample bakery items for the catalogue.
     */
    public static BakeryItem[] getSampleBakeryItems() {
        return new BakeryItem[] {
            new BakeryItem(new BakeryItemName("Banana Muffin"), Price.fromDollars("1.20")),
            new BakeryItem(new BakeryItemName("Chocolate Cake"), Price.fromDollars("25.00")),
            new BakeryItem(new BakeryItemName("Butter Croissant"), Price.fromDollars("2.80")),
            new BakeryItem(new BakeryItemName("Chocolate Chip Cookies"), Price.fromDollars("5.50"))
        };
    }

    /**
     * Returns a sample book with clients, bakery items, and complete and incomplete orders.
     */
    public static ReadOnlyAddressBook getSampleAddressBook() {
        AddressBook sampleAb = new AddressBook();
        Person[] persons = getSamplePersons();
        BakeryItem[] items = getSampleBakeryItems();
        for (Person samplePerson : persons) {
            sampleAb.addPerson(samplePerson);
        }
        for (BakeryItem sampleItem : items) {
            sampleAb.addBakeryItem(sampleItem);
        }
        sampleAb.addOrder(ClientOrder.create(persons[0], items[0], new Quantity(12), new Deadline("2026-10-20")));
        sampleAb.addOrder(ClientOrder.create(persons[1], items[1], new Quantity(1), new Deadline("2026-10-21")));
        sampleAb.addOrder(ClientOrder.create(persons[0], items[2], new Quantity(6), new Deadline("2026-10-22")));
        sampleAb.addOrder(ClientOrder.create(persons[2], items[3], new Quantity(3), new Deadline("2026-10-12"))
                .withCompletion(true));
        sampleAb.addOrder(ClientOrder.create(persons[3], items[0], new Quantity(8), new Deadline("2026-10-23")));
        sampleAb.addOrder(ClientOrder.create(persons[4], items[2], new Quantity(4), new Deadline("2026-10-13"))
                .withCompletion(true));
        return sampleAb;
    }

    /**
     * Returns a tag set containing the list of strings given.
     */
    public static Set<Tag> getTagSet(String... strings) {
        return Arrays.stream(strings)
                .map(Tag::new)
                .collect(Collectors.toSet());
    }

}
