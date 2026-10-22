package seedu.address.model.util;

import java.time.LocalDate;
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
                getTagSet("colleagues")),
            new Person(new Name("Sarah Tan"), new Phone("81234567"), new Email("sarahtan@example.com"),
                new Address("Blk 15 Bishan Street 12, #05-22"),
                getTagSet("regular", "birthdays")),
            new Person(new Name("Marcus Lim"), new Phone("82345678"), new Email("marcuslim@example.com"),
                new Address("Blk 82 Toa Payoh Lorong 4, #09-15"),
                getTagSet("corporate")),
            new Person(new Name("Priya Nair"), new Phone("83456789"), new Email("priyanair@example.com"),
                new Address("Blk 208 Clementi Avenue 6, #03-18"),
                getTagSet("regular", "events")),
            new Person(new Name("Daniel Wong"), new Phone("84567890"), new Email("danielwong@example.com"),
                new Address("Blk 118 Bukit Merah View, #08-06"),
                getTagSet("neighbours")),
            new Person(new Name("Nur Aisyah"), new Phone("85678901"), new Email("nuraisyah@example.com"),
                new Address("Blk 320 Woodlands Street 32, #12-24"),
                getTagSet("family", "events")),
            new Person(new Name("Grace Lee"), new Phone("86789012"), new Email("gracelee@example.com"),
                new Address("Blk 55 Punggol Field, #04-11"),
                getTagSet("corporate", "regular"))
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
            new BakeryItem(new BakeryItemName("Chocolate Chip Cookies"), Price.fromDollars("5.50")),
            new BakeryItem(new BakeryItemName("Blueberry Muffin"), Price.fromDollars("1.80")),
            new BakeryItem(new BakeryItemName("Chocolate Muffin"), Price.fromDollars("1.60")),
            new BakeryItem(new BakeryItemName("Almond Croissant"), Price.fromDollars("3.50")),
            new BakeryItem(new BakeryItemName("Strawberry Shortcake"), Price.fromDollars("32.00")),
            new BakeryItem(new BakeryItemName("Sourdough Bread"), Price.fromDollars("6.80")),
            new BakeryItem(new BakeryItemName("Cinnamon Roll"), Price.fromDollars("2.50")),
            new BakeryItem(new BakeryItemName("Lemon Tart"), Price.fromDollars("4.20")),
            new BakeryItem(new BakeryItemName("Vanilla Cupcake"), Price.fromDollars("2.20"))
        };
    }

    /**
     * Returns a sample book with clients, bakery items, and complete and incomplete orders.
     * Deadlines are relative to today so overdue, due-today, and upcoming examples remain useful.
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
        LocalDate today = LocalDate.now();
        int[] deadlineOffsets = {-3, -1, 0, 0, 1, 2, 3, 5, 7, 10, 14, 21};
        for (int i = 0; i < persons.length; i++) {
            sampleAb.addOrder(ClientOrder.create(persons[i], items[i % items.length], new Quantity((i + 1) * 2),
                    new Deadline(today.plusDays(deadlineOffsets[i]).toString())).withCompletion(i % 3 == 0));
            sampleAb.addOrder(ClientOrder.create(persons[i], items[(i + 3) % items.length], new Quantity(i + 1),
                    new Deadline(today.plusDays(deadlineOffsets[i] - 2).toString())).withCompletion(i % 3 != 1));
        }
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
