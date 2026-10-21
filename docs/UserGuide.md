---
layout: page
title: User Guide
---

AddressBook Level 3 (AB3) is a **desktop application for managing contacts, optimized for use through a Command Line Interface (CLI)** while retaining the benefits of a Graphical User Interface (GUI). If you type quickly, AB3 can help you manage contacts faster than traditional GUI applications.

* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## Quick start

1. Ensure that Java `25` or later is installed on your computer.<br>
   **Mac users:** Ensure you have the precise JDK version prescribed [here](https://se-education.org/guides/tutorials/javaInstallationMac.html).

1. Download [BakeBase-v1.3.jar](https://github.com/AY2627S1-CS2103T-F14-3/tp/releases/download/v1.3/BakeBase-v1.3.jar) from the [BakeBase v1.3 release](https://github.com/AY2627S1-CS2103T-F14-3/tp/releases/tag/v1.3).

1. Copy the file to the folder you want to use as the _home folder_ for your AddressBook.

1. Open a terminal, `cd` to the folder containing the JAR file, and run `java -jar BakeBase-v1.3.jar`.<br>
   A GUI similar to the one below should appear in a few seconds. Note how the app contains some sample data.<br>
   ![Ui](images/Ui.png)

1. Type a command in the command box and press Enter to execute it. For example, type **`help`** and press Enter to open the help window.<br>
   Some example commands you can try:

   * `list-clients` : Lists all contacts.

   * `add-client n/John Doe p/98765432 e/johnd@example.com a/John street, block 123, #01-01` : Adds a contact named `John Doe` to the Address Book.

   * `delete-client 3` : Deletes the 3rd contact shown in the current list.

   * `add-item n/Banana Muffin p/5.99` : Adds a bakery item named `Banana Muffin` to the Address Book.

   * `clear` : Deletes all clients, bakery items, and orders.

   * `exit` : Exits the app.

2. Refer to the [Features](#features) section below for details of each command.

--------------------------------------------------------------------------------------------------------------------

## Features

<div markdown="block" class="alert alert-info">

**:information_source: Notes about the command format:**<br>

* Words in `UPPER_CASE` are the parameters to be supplied by the user.<br>
  For example, in `add-client n/NAME`, replace `NAME` with a value such as `John Doe`.

* Items in square brackets are optional.<br>
  For example, `n/NAME [t/TAG]` can be used as `n/John Doe t/friend` or as `n/John Doe`.

* Items followed by `…`​ can appear zero or more times.<br>
  For example, `[t/TAG]…​` may be omitted, or written as `t/friend` or `t/friend t/family`.

* Parameters can be in any order.<br>
  For example, if the command specifies `n/NAME p/PHONE_NUMBER`, `p/PHONE_NUMBER n/NAME` is also acceptable.

* Extraneous parameters for commands that take no parameters, such as `help`, `list-clients`, `list-items`, `populate`, `exit`, and `clear`, are ignored.<br>
  For example, `help 123` is interpreted as `help`.

* If you are using a PDF version of this document, be careful when copying and pasting commands that span multiple lines as space characters surrounding line-breaks may be omitted when copied over to the application.
</div>

### Client commands

#### Adding a client: `add-client`

Adds a client to the address book.

Format: `add-client n/NAME p/PHONE_NUMBER e/EMAIL a/ADDRESS [t/TAG]…​`

<div markdown="span" class="alert alert-primary">:bulb: **Tip:**
A client can have any number of tags, including zero.
</div>

Examples:
* `add-client n/John Doe p/98765432 e/johnd@example.com a/John street, block 123, #01-01`
* `add-client n/Betsy Crowe t/friend e/betsycrowe@example.com a/Newgate Prison p/1234567 t/criminal`

#### Deleting a client: `delete-client`

Deletes the specified client from the address book. If the client has orders, delete those orders first.

Format: `delete-client INDEX`

* Deletes the client at the specified `INDEX`.
* The index refers to the index number shown in the displayed client list.
* The index **must be a positive integer** 1, 2, 3, …​

Examples:
* `list-clients` followed by `delete-client 2` deletes the 2nd client in the address book.
* `find-clients Betsy` followed by `delete-client 1` deletes the 1st client in the results of the `find-clients` command.

#### Editing a client: `edit-client`

Edits an existing client in the address book. The client's existing orders are retained.

Format: `edit-client INDEX [n/NAME] [p/PHONE] [e/EMAIL] [a/ADDRESS] [t/TAG]…​`

* Edits the client at the specified `INDEX`. The index refers to the index number shown in the displayed client list. The index **must be a positive integer** 1, 2, 3, …​
* At least one of the optional fields must be provided.
* Existing values will be updated to the input values.
* When editing tags, all of the client's existing tags are removed; adding tags is not cumulative.
* To remove all of a client's tags, enter `t/` without a tag after it.

Examples:
*  `edit-client 1 p/91234567 e/johndoe@example.com` Edits the phone number and email address of the 1st client to be `91234567` and `johndoe@example.com` respectively.
*  `edit-client 2 n/Betsy Crower t/` Edits the name of the 2nd client to be `Betsy Crower` and clears all existing tags.

#### Listing all clients: `list-clients`

Shows a list of all clients in the address book.

Format: `list-clients`

#### Locating clients by name: `find-clients`

Finds clients whose names contain any of the given keywords.

Format: `find-clients KEYWORD [MORE_KEYWORDS]`

* The search is case-insensitive; for example, `hans` matches `Hans`.
* Keyword order does not matter; for example, `Hans Bo` matches `Bo Hans`.
* The search considers only names.
* Only full words match; for example, `Han` does not match `Hans`.
* Clients matching at least one keyword are returned (an `OR` search); for example, `Hans Bo` returns `Hans Gruber` and `Bo Yang`.

Examples:
* `find-clients John` returns `john` and `John Doe`
* `find-clients alex david` returns `Alex Yeoh`, `David Li`<br>
  ![result for finding Alex and David](images/findAlexDavidResult.png)

### Bakery item commands

#### Adding a bakery item: `add-item`

Adds the specified bakery item to the address book.

Format: `add-item n/ITEM_NAME p/PRICE_DOLLARS`

* Adds the bakery item with the leading and trailing whitespace removed from `ITEM_NAME`. The name must not be blank and must contain only printable ASCII characters.
* Printable ASCII is defined as the set of:
  * lowercase letters `a`–`z` and uppercase letters `A`-`Z`
  * digits `0`-`9`
  * the ordinary space character
  * the special characters ``!"#$%&'()*+,-./:;<=>?@[\]^_`{|}~``
* `/` can appear in an item name. However, `n/` and `p/` preceded by whitespace are interpreted as command prefixes and cannot be included literally in an item name. For example, `Cake/Cream` and `Cake / Cream` are accepted, but `Cake n/Cream` and `Cake p/Special` are not. There is no quoting or escaping syntax for these reserved sequences.
* Duplicate items are rejected. An item is a duplicate if they have the same item name after trimming, removing extra whitespace and converting to uppercase.
* `PRICE_DOLLARS` must be non-negative and can have the format `D`, `D.`, `D.c`, `D.cc`, `.c`, or `.cc`, where `D` represents one or more digits and `c` represents one digit.

Examples:
* `add-item n/Banana Muffin p/5.90`

#### Editing a bakery item: `edit-item`

Edits the name, price, or both fields of an existing bakery item. Existing orders for the item are retained; if the item is renamed, its order cards show the new name.

Format: `edit-item INDEX [n/ITEM_NAME] [p/PRICE]`

* Edits the bakery item at the specified `INDEX` in the displayed bakery item list.
* The index **must be a positive integer** 1, 2, 3, …​ and must not exceed the number of displayed bakery items.
* At least one of `n/ITEM_NAME` or `p/PRICE` must be provided. An omitted field is left unchanged.
* `ITEM_NAME` and `PRICE` follow the same validation rules as the [`add-item`](#adding-a-bakery-item-add-item) command.
* The edit is rejected without changing any data if the new item name duplicates another bakery item's normalized name. Letter case and repeated whitespace do not distinguish item names.
* Each of the `n/` and `p/` prefixes can appear at most once.

Examples:
* `edit-item 1 n/Blueberry Muffin` changes only the name of the 1st displayed bakery item.
* `edit-item 2 p/6.50` changes only the price of the 2nd displayed bakery item.
* `edit-item 3 n/Chocolate Tart p/18.90` changes both the name and price of the 3rd displayed bakery item.

#### Deleting a bakery item: `delete-item`

Deletes the specified bakery item from the address book. If orders reference this item, delete those orders first.

Format: `delete-item INDEX`

* Deletes the bakery item at the specified `INDEX`.
* The index refers to the index number shown in the displayed bakery item list.
* The index **must be a positive integer** 1, 2, 3, …​

Examples:
* `delete-item 1` deletes the 1st bakery item in the displayed bakery item list.

#### Listing all bakery items: `list-items`

Shows all bakery items in the item panel, with their index, name, and price.

Format: `list-items`

Like `list-clients`, this command ignores extra arguments. It does not change the displayed client list.
The displayed item indices can be used with `edit-item`, `delete-item`, and `add-order`.

### Order commands

Order commands use the displayed client and item lists. Create the client and bakery item before adding an order.

* `CONTACT_INDEX` is the client's index in the displayed client list. Use `list-clients` to show all clients after a search.
* `ITEM_INDEX` is the item's index in the displayed bakery item list.
* `ORDER_INDEX` is the order's position within that client's own orders, as shown on the order card. It is not the row number in the complete order list.
* Indices must be positive integers. Invalid indices or input fields leave existing data unchanged.

#### Adding an order: `add-order`

Adds a new incomplete order for an existing client and bakery item.

Format: `add-order CONTACT_INDEX i/ITEM_INDEX q/QUANTITY d/DEADLINE`

* Required `i/`, `q/`, and `d/` fields may appear in any order.
* `QUANTITY` must be a whole number from 1 to 9,999.
* `DEADLINE` must be a real calendar date in `YYYY-MM-DD` format. Past dates are accepted for record-keeping.
* Identical repeat orders are allowed and can be managed separately.
* The success message includes the new order's index within the client's orders.

Example:
* `add-order 1 i/1 q/5 d/2026-10-15` adds an order for five units of item 1 to client 1.

#### Deleting an order: `delete-order`

Deletes the selected order from the specified client.

Format: `delete-order CONTACT_INDEX o/ORDER_INDEX`

* Deletes only the selected order; other orders, including identical repeat orders, are retained.
* Later order indices for that client shift down after deletion. Use the updated numbers shown on the cards.

Example:
* `delete-order 1 o/2` deletes client 1's second order.

#### Marking an order complete: `mark-order-complete`

Marks the selected client order as complete.

Format: `mark-order-complete CONTACT_INDEX o/ORDER_INDEX`

* The order remains visible in the order panel.
* If the order is already complete, an error is shown and no data is changed.

Example:
* `mark-order-complete 1 o/2` marks client 1's second order complete.

#### Restoring an order to incomplete: `unmark`

Restores the selected completed order to incomplete.

Format: `unmark CONTACT_INDEX o/ORDER_INDEX`

* If the order is already incomplete, an error is shown and no data is changed.

Example:
* `unmark 1 o/2` restores client 1's second order to incomplete.

#### Listing all orders: `list-orders`

Shows all orders in insertion order, including completed orders. Each card shows its position
in the order list, item, quantity, client name, that client's own order number, deadline, and completion status.

Format: `list-orders`

* Leaves the displayed client and item lists unchanged.
* Extra arguments are ignored, like `list-clients`. No order filters are supported yet.
* Orders stay visible when clients are filtered with `find-clients`.
* Row numbers refer to this complete order list. The client line shows the client's displayed
  index and the order number within that client's own orders, for example `Client 2: Bernice Yu — Order 1`.
  Existing order commands take these two indices, for example `delete-order 2 o/1`.
  Client indices update with the displayed client list. If a client is hidden by a search,
  their name and order number remain visible, but their client index is omitted.
* Adding, deleting, or marking an order updates the panel automatically. Deleting an order
  renumbers the remaining rows in the order panel.

### General commands

#### Undoing the last data change: `undo`

Type `undo` or click the **Undo** button above the command input to reverse the last successfully saved data change.

* Supports adding, editing, or deleting clients/items, adding or deleting orders, marking/unmarking orders, `clear`, and `populate`.
* Only one change can be undone. A new saved data change replaces the previous undo opportunity. There is no redo.
* Listing, searching, help, failed commands, and edits that leave data unchanged do not replace the undo opportunity.
* Undo restores linked clients, items, and orders together. It shows all clients and items again, so use the refreshed indices.
* The button is disabled when nothing can be undone. Typing `undo` then reports an error without changing data.
* If saving undo fails, the current data and displayed filters are restored, and you can retry.
* Undo history is cleared on restart; successfully restored data remains saved. Extra arguments are rejected.

Example: `delete-order 1 o/2` followed by `undo` restores the deleted order.

#### Recalling commands with Up and Down

While the command input has focus, press **Up** to recall the latest submitted command and press it again for older commands. Press **Down** to move toward newer commands. Moving past the newest command restores the unfinished text you typed before navigating.

* You can edit recalled text and press Enter to submit it. This does not change the original history entry.
* Navigation keeps the caret at the end of the recalled text and does not execute commands.
* Non-empty submissions, including commands that produce errors, are remembered. Empty submissions are ignored.
* At the oldest entry, further Up presses keep that entry. Down at the draft keeps the current input.
* History lasts only for the current application session. Modified arrow shortcuts keep their normal behavior.

#### Viewing help: `help`

Shows a message explaining how to access the help page, and a very brief summary of the commands.

![help message](images/helpMessage.png)

Format: `help`

#### Replacing all entries with sample data: `populate`

Replaces all current clients, bakery items, and orders with the default sample data.
This overwrites existing data; it does not append to it. The replacement is saved automatically.

Format: `populate`

* Loads six sample clients, four bakery items, and six orders, including completed and incomplete orders.
* Resets client and item filters so all sample entries are displayed.
* Running it again replaces the data with a fresh sample book rather than adding duplicates.
* If saving fails, the previous data and filters are restored.

#### Clearing all entries: `clear`

Removes all clients, bakery items, and orders from the address book.

Format: `clear`

#### Exiting the program: `exit`

Exits the program.

Format: `exit`

## Data storage

### Saving the data

AddressBook automatically saves data after every command. You do not need to save manually.

Clients, bakery items, orders, and completion statuses are saved together. A successful change is reported only after saving succeeds. If saving fails, an error is shown and the previous in-memory data and displayed filters are restored.

### Editing the data file

AddressBook data is saved automatically as a JSON file `[JAR file location]/data/addressbook.json`. Advanced users are welcome to update data directly by editing that data file.

<div markdown="span" class="alert alert-warning">:exclamation: **Caution:**
If your changes make the data file invalid, AddressBook starts with an empty address book at the next run. The invalid file remains on disk until you run a command (AddressBook saves after every command). Still, we recommend backing up the file before editing it.<br>
Furthermore, certain edits can cause the AddressBook to behave in unexpected ways (e.g., if a value entered is outside of the acceptable range). Therefore, edit the data file only if you are confident that you can update it correctly.
</div>

Existing AB3 files remain supported. Orders refer to clients and bakery items by name in the file. If you manually rename one, update its order references as well. Keep each order's UUID unique and use valid quantities, dates, and completion statuses. Incorrect edits can prevent loading and cause data loss, as with AB3; back up the file before editing it.

### Archiving data files `[coming in v2.0]`

_Details coming soon ..._

--------------------------------------------------------------------------------------------------------------------

## FAQ

**Q**: How do I transfer my data to another computer?<br>
**A**: Install the app on the other computer and overwrite the data file it creates with the data file from your previous AddressBook home folder.

--------------------------------------------------------------------------------------------------------------------

## Known issues

1. **When using multiple screens**, if you move the application to a secondary screen, and later switch to using only the primary screen, the GUI will open off-screen. The remedy is to delete the `preferences.json` file created by the application before running the application again.
2. **If you minimize the Help Window** and then run the `help` command (or use the `Help` menu, or the keyboard shortcut `F1`) again, the original Help Window will remain minimized, and no new Help Window will appear. The remedy is to manually restore the minimized Help Window.

--------------------------------------------------------------------------------------------------------------------

## Command summary

### Client commands

Action | Format, Examples
--------|------------------
**Add client** | `add-client n/NAME p/PHONE_NUMBER e/EMAIL a/ADDRESS [t/TAG]…​`<br>e.g., `add-client n/James Ho p/22224444 e/jamesho@example.com a/123, Clementi Rd, 1234665 t/friend`
**Delete client** | `delete-client INDEX`<br>e.g., `delete-client 3`
**Edit client** | `edit-client INDEX [n/NAME] [p/PHONE] [e/EMAIL] [a/ADDRESS] [t/TAG]…​`<br>e.g., `edit-client 2 n/James Lee e/jameslee@example.com`
**List clients** | `list-clients`
**Find clients** | `find-clients KEYWORD [MORE_KEYWORDS]`<br>e.g., `find-clients James Jake`

### Bakery item commands

Action | Format, Examples
--------|------------------
**Add bakery item** | `add-item n/ITEM_NAME p/PRICE_DOLLARS`<br>e.g., `add-item n/Banana Muffin p/5.99`
**Edit bakery item** | `edit-item INDEX [n/ITEM_NAME] [p/PRICE]`<br>e.g., `edit-item 2 n/Blueberry Muffin p/6.50`
**Delete bakery item** | `delete-item INDEX`<br>e.g., `delete-item 3`
**List bakery items** | `list-items`

### Order commands

Action | Format, Examples
--------|------------------
**Add order** | `add-order CONTACT_INDEX i/ITEM_INDEX q/QUANTITY d/DEADLINE`<br>e.g., `add-order 1 i/2 q/5 d/2026-10-15`
**Delete order** | `delete-order CONTACT_INDEX o/ORDER_INDEX`<br>e.g., `delete-order 1 o/2`
**Mark order complete** | `mark-order-complete CONTACT_INDEX o/ORDER_INDEX`<br>e.g., `mark-order-complete 1 o/2`
**Restore order to incomplete** | `unmark CONTACT_INDEX o/ORDER_INDEX`<br>e.g., `unmark 1 o/2`
**List orders** | `list-orders`

### General commands

Action | Format
--------|-------
**Help** | `help`
**Undo last data change** | `undo`
**Populate sample data** | `populate`
**Clear all data** | `clear`
**Exit** | `exit`
