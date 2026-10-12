package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalBakeryItems.BANANA_MUFFIN;
import static seedu.address.testutil.TypicalBakeryItems.CHOCOLATE_MUFFIN;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.AddressBook;
import seedu.address.testutil.TypicalPersons;

public class JsonSerializableAddressBookTest {

    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonSerializableAddressBookTest");
    private static final Path TYPICAL_PERSONS_FILE = TEST_DATA_FOLDER.resolve("typicalPersonsAddressBook.json");
    private static final Path INVALID_PERSON_FILE = TEST_DATA_FOLDER.resolve("invalidPersonAddressBook.json");
    private static final Path DUPLICATE_PERSON_FILE = TEST_DATA_FOLDER.resolve("duplicatePersonAddressBook.json");

    @Test
    public void toJsonString_addressBookWithBakeryItems_serializesBakeryItems() throws Exception {
        AddressBook addressBook = new AddressBook();
        addressBook.setBakeryItems(List.of(BANANA_MUFFIN, CHOCOLATE_MUFFIN));

        JsonSerializableAddressBook serializableAddressBook = new JsonSerializableAddressBook(addressBook);
        String json = JsonUtil.toJsonString(serializableAddressBook);
        JsonNode bakeryItems = JsonUtil.fromJsonString(json, JsonNode.class).get("bakeryItems");

        assertEquals(2, bakeryItems.size());
        assertEquals("Banana Muffin", bakeryItems.get(0).get("itemName").asText());
        assertEquals("1.20", bakeryItems.get(0).get("price").asText());
        assertEquals("Chocolate Muffin", bakeryItems.get(1).get("itemName").asText());
        assertEquals("1.50", bakeryItems.get(1).get("price").asText());
    }

    @Test
    public void toJsonString_addressBookWithoutBakeryItems_serializesEmptyList() throws Exception {
        JsonSerializableAddressBook serializableAddressBook = new JsonSerializableAddressBook(new AddressBook());
        String json = JsonUtil.toJsonString(serializableAddressBook);
        JsonNode bakeryItems = JsonUtil.fromJsonString(json, JsonNode.class).get("bakeryItems");

        assertEquals(0, bakeryItems.size());
    }

    @Test
    public void toModelType_typicalPersonsFile_success() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(TYPICAL_PERSONS_FILE,
                JsonSerializableAddressBook.class).get();
        AddressBook addressBookFromFile = dataFromFile.toModelType();
        AddressBook typicalPersonsAddressBook = TypicalPersons.getTypicalAddressBook();
        assertEquals(addressBookFromFile, typicalPersonsAddressBook);
    }

    @Test
    public void toModelType_invalidPersonFile_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(INVALID_PERSON_FILE,
                JsonSerializableAddressBook.class).get();
        assertThrows(IllegalValueException.class, dataFromFile::toModelType);
    }

    @Test
    public void toModelType_duplicatePersons_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(DUPLICATE_PERSON_FILE,
                JsonSerializableAddressBook.class).get();
        assertThrows(IllegalValueException.class, JsonSerializableAddressBook.MESSAGE_DUPLICATE_PERSON,
                dataFromFile::toModelType);
    }

}
