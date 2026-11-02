import java.util.*;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.logging.LogManager;
import seedu.address.model.*;
import seedu.address.model.order.*;
import seedu.address.model.person.*;
import seedu.address.model.bakeryitem.*;
import seedu.address.logic.LogicManager;
import seedu.address.storage.*;

public class OrderPerformanceProbe {
    public static void main(String[] args) throws Exception {
        LogManager.getLogManager().reset();
        AddressBook book = new AddressBook();
        for (int i=0;i<100;i++) {
            book.addPerson(new Person(new Name("Client " + i),new Phone("1234567"),new Email("client"+i+"@example.com"),
                new Address("Street " + i),Set.of()));
            book.addBakeryItem(new BakeryItem(new BakeryItemName("Muffin " + i),Price.fromDollars("2.50")));
        }
        for (int i=0;i<1000;i++) {
            book.addOrder(ClientOrder.create(book.getPersonList().get(i%100),book.getBakeryItemList().get((i*7)%100),
                new Quantity(i%9999+1),new Deadline(LocalDate.of(2026,10,1).plusDays(i%31).toString()))
                .withCompletion(i%3==0));
        }
        ModelManager model=new ModelManager(book,new UserPrefs());
        JsonAddressBookStorage json=new JsonAddressBookStorage(Path.of("_beta-test/performance-data/addressbook.json"));
        json.saveAddressBook(book);
        LogicManager logic=new LogicManager(model,new StorageManager(json,
            new JsonUserPrefsStorage(Path.of("_beta-test/performance-data/preferences.json"))));
        String[] commands={"list-clients","find-clients Client","list-items","find-items Muffin",
            "list-orders","list-orders by-deadline","find-orders Client","find-orders 42",
            "list-complete-orders","list-complete-orders by-deadline","list-incomplete-orders",
            "list-incomplete-orders by-deadline"};
        double[][] samples=new double[commands.length][5];
        System.out.println("Release JAR 100 clients / 100 items / 1000 orders");
        System.out.println("Java " + System.getProperty("java.version") + "; OS " + System.getProperty("os.name")
            + " " + System.getProperty("os.arch") + "; processors " + Runtime.getRuntime().availableProcessors());
        System.out.println("Includes parse, full-book snapshot, command, full JSON serialization, atomic file replacement; excludes GUI rendering.");
        System.out.println("Two complete warmup cycles, then five measured cycles. Model constructed directly and persisted once before warmup.");
        for (int round=-2;round<5;round++) {
            for (int i=0;i<commands.length;i++) {
                long start=System.nanoTime();logic.execute(commands[i]);double ms=(System.nanoTime()-start)/1e6;
                if(round>=0)samples[i][round]=ms;
            }
        }
        double globalMax=0;
        for(int i=0;i<commands.length;i++){
            double[] sorted=samples[i].clone();Arrays.sort(sorted);globalMax=Math.max(globalMax,sorted[4]);
            System.out.printf(Locale.ROOT,"%-36s median=%8.2f ms max=%8.2f ms samples=%s%n",commands[i],sorted[2],sorted[4],Arrays.toString(samples[i]));
        }
        if(!book.equals(json.readAddressBook().get()))throw new AssertionError("Read-only commands changed stored data");
        System.out.printf(Locale.ROOT,"Maximum warmed observation %.2f ms; %s below 3000ms on this host.%n",globalMax,globalMax<3000?"all":"NOT all");
        System.out.println("Scope: cannot certify the DG NFR on the team's unspecified reference machine or end-to-end GUI response time. Cold start excluded.");
    }
}
