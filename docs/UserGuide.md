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

1. Download the latest `.jar` file from [here](https://github.com/se-edu/addressbook-level3/releases).

1. Copy the file to the folder you want to use as the _home folder_ for your AddressBook.

1. Open a terminal, `cd` to the folder containing the JAR file, and run `java -jar addressbook.jar`.<br>
   A GUI similar to the one below should appear in a few seconds. Note how the app contains some sample data.<br>
   ![Ui](images/Ui.png)

1. Type a command in the command box and press Enter to execute it. For example, type **`help`** and press Enter to open the help window.<br>
   Some example commands you can try:

   * `list` : Lists all contacts.

   * `add n/John Doe p/98765432 e/johnd@example.com a/John street, block 123, #01-01` : Adds a contact named `John Doe` to the Address Book.

   * `delete 3` : Deletes the 3rd contact shown in the current list.

   * `add-item n/Banana Muffin p/5.99` : Adds a bakery item named `Banana Muffin` to the Address Book.

   * `clear` : Deletes all contacts.

   * `exit` : Exits the app.

2. Refer to the [Features](#features) section below for details of each command.

--------------------------------------------------------------------------------------------------------------------

## Features

<div markdown="block" class="alert alert-info">

**:information_source: Notes about the command format:**<br>

* Words in `UPPER_CASE` are the parameters to be supplied by the user.<br>
  For example, in `add n/NAME`, replace `NAME` with a value such as `John Doe`.

* Items in square brackets are optional.<br>
  For example, `n/NAME [t/TAG]` can be used as `n/John Doe t/friend` or as `n/John Doe`.

* Items followed by `…`​ can appear zero or more times.<br>
  For example, `[t/TAG]…​` may be omitted, or written as `t/friend` or `t/friend t/family`.

* Parameters can be in any order.<br>
  For example, if the command specifies `n/NAME p/PHONE_NUMBER`, `p/PHONE_NUMBER n/NAME` is also acceptable.

* Extraneous parameters for commands that take no parameters, such as `help`, `list`, `list-items`, `exit`, and `clear`, are ignored.<br>
  For example, `help 123` is interpreted as `help`.

* If you are using a PDF version of this document, be careful when copying and pasting commands that span multiple lines as space characters surrounding line-breaks may be omitted when copied over to the application.
</div>

### Viewing help: `help`

Shows a message explaining how to access the help page.

![help message](images/helpMessage.png)

Format: `help`


### Adding a person: `add`

Adds a person to the address book.

Format: `add n/NAME p/PHONE_NUMBER e/EMAIL a/ADDRESS [t/TAG]…​`

<div markdown="span" class="alert alert-primary">:bulb: **Tip:**
A person can have any number of tags, including zero.
</div>

Examples:
* `add n/John Doe p/98765432 e/johnd@example.com a/John street, block 123, #01-01`
* `add n/Betsy Crowe t/friend e/betsycrowe@example.com a/Newgate Prison p/1234567 t/criminal`

### Listing all persons: `list`

Shows a list of all persons in the address book.

Format: `list`

### Editing a person: `edit`

Edits an existing person in the address book.

Format: `edit INDEX [n/NAME] [p/PHONE] [e/EMAIL] [a/ADDRESS] [t/TAG]…​`

* Edits the person at the specified `INDEX`. The index refers to the index number shown in the displayed person list. The index **must be a positive integer** 1, 2, 3, …​
* At least one of the optional fields must be provided.
* Existing values will be updated to the input values.
* When editing tags, all of the person's existing tags are removed; adding tags is not cumulative.
* To remove all of a person's tags, enter `t/` without a tag after it.

Examples:
*  `edit 1 p/91234567 e/johndoe@example.com` Edits the phone number and email address of the 1st person to be `91234567` and `johndoe@example.com` respectively.
*  `edit 2 n/Betsy Crower t/` Edits the name of the 2nd person to be `Betsy Crower` and clears all existing tags.

### Locating persons by name: `find`

Finds persons whose names contain any of the given keywords.

Format: `find KEYWORD [MORE_KEYWORDS]`

* The search is case-insensitive; for example, `hans` matches `Hans`.
* Keyword order does not matter; for example, `Hans Bo` matches `Bo Hans`.
* The search considers only names.
* Only full words match; for example, `Han` does not match `Hans`.
* Persons matching at least one keyword are returned (an `OR` search); for example, `Hans Bo` returns `Hans Gruber` and `Bo Yang`.

Examples:
* `find John` returns `john` and `John Doe`
* `find alex david` returns `Alex Yeoh`, `David Li`<br>
  ![result for 'find alex david'](images/findAlexDavidResult.png)

### Deleting a person: `delete`

Deletes the specified person from the address book.

Format: `delete INDEX`

* Deletes the person at the specified `INDEX`.
* The index refers to the index number shown in the displayed person list.
* The index **must be a positive integer** 1, 2, 3, …​

Examples:
* `list` followed by `delete 2` deletes the 2nd person in the address book.
* `find Betsy` followed by `delete 1` deletes the 1st person in the results of the `find` command.

### Listing all orders: `list-orders`

Shows all orders in insertion order, including completed orders. Each card shows its position
in the order list, item, quantity, client name, that client's own order number, deadline, and completion status.

Format: `list-orders`

* Leaves the displayed client and item lists unchanged.
* Extra arguments are ignored, like `list`. No order filters are supported yet.
* Orders stay visible when clients are filtered with `find`.
* Row numbers refer to this complete order list. The client line also shows the order number
  within that client's own orders. Existing order commands take a displayed client index and
  this client-local order number, as documented for those commands.
* Adding, deleting, or marking an order updates the panel automatically. Deleting an order
  renumbers the remaining rows in the order panel.

### Listing all bakery items: `list-items`

Shows all bakery items in the item panel, with their index, name, and price.

Format: `list-items`

Like `list`, this command ignores extra arguments. It does not change the displayed person list.
The displayed item indices can be used with `delete-item` and `add-order`.

### Adding a bakery item: `add-item`

Adds the specified bakery item to the address book.

Format: `add-item n/ITEM_NAME p/PRICE_DOLLARS`

* Adds the bakery item with the trimmed `ITEM_NAME` and `PRICE_DOLLARS`.
* Duplicate items are rejected. An item is a duplicate if they have the same item name after trimming, removing extra whitespace and converting to uppercase.
* `PRICE_DOLLARS` can have the format `D`, `D.`, `D.c`, or `D.cc`.

Examples:
* `add-item n/Banana Muffin p/5.90`

### Deleting a bakery item: `delete-item`

Deletes the specified bakery item from the address book.

Format: `delete-item INDEX`

* Deletes the bakery item at the specified `INDEX`.
* The index refers to the index number shown in the displayed bakery item list.
* The index **must be a positive integer** 1, 2, 3, …​

Examples:
* `delete-item 1` deletes the 1st bakery item in the displayed bakery item list.

### Clearing all entries: `clear`

Clears all entries from the address book.

Format: `clear`

### Exiting the program: `exit`

Exits the program.

Format: `exit`

### Saving the data

AddressBook automatically saves data after every command. You do not need to save manually.

### Editing the data file

AddressBook data is saved automatically as a JSON file `[JAR file location]/data/addressbook.json`. Advanced users are welcome to update data directly by editing that data file.

<div markdown="span" class="alert alert-warning">:exclamation: **Caution:**
If your changes make the data file invalid, AddressBook starts with an empty address book at the next run. The invalid file remains on disk until you run a command (AddressBook saves after every command). Still, we recommend backing up the file before editing it.<br>
Furthermore, certain edits can cause the AddressBook to behave in unexpected ways (e.g., if a value entered is outside of the acceptable range). Therefore, edit the data file only if you are confident that you can update it correctly.
</div>

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

Action | Format, Examples
--------|------------------
**Add** | `add n/NAME p/PHONE_NUMBER e/EMAIL a/ADDRESS [t/TAG]…​` <br> e.g., `add n/James Ho p/22224444 e/jamesho@example.com a/123, Clementi Rd, 1234665 t/friend t/colleague`
**Clear** | `clear`
**Delete** | `delete INDEX`<br> e.g., `delete 3`
**Edit** | `edit INDEX [n/NAME] [p/PHONE_NUMBER] [e/EMAIL] [a/ADDRESS] [t/TAG]…​`<br> e.g., `edit 2 n/James Lee e/jameslee@example.com`
**Find** | `find KEYWORD [MORE_KEYWORDS]`<br> e.g., `find James Jake`
**Add Bakery Item** | `add-item n/ITEM_NAME p/PRICE_DOLLARS` <br> e.g., `add-item n/Banana Muffin p/5.99`
**Delete Bakery Item** | `delete-item INDEX`<br> e.g., `delete-item 3`
**List** | `list`
**List Bakery Items** | `list-items`
**List Orders** | `list-orders`
**Help** | `help`

## Bakery items and client orders

Add an item with `add-item n/Muffin p/1.20`. Item names are unique ignoring case and extra spaces.
Add an order with `add-order 1 i/1 q/5 d/2026-10-15`.
The first number identifies the client in the displayed client list; `i/` identifies the item in the displayed item
list. Required `i/`, `q/`, and `d/` fields may appear in any order. The quantity must be a whole number from 1 to
9,999. The deadline must be a real calendar date in YYYY-MM-DD format; past deadlines are accepted.

New orders are incomplete. Identical repeat orders are allowed. The success message includes the order's index
within that client's orders. Order indices are client-local insertion positions, not positions in a consolidated
order list. Deleting an order will shift the later order indices for that client. Invalid indices or fields leave
the data unchanged. Orders, items, and completion statuses are saved automatically after successful commands.

Clients referenced by orders cannot be deleted until those orders are removed. Editing a client's details retains
their orders. The `clear` command removes clients, bakery items, and orders together.
Bakery items are displayed in the item panel; use `list-items` to show all bakery items.
The order panel displays all orders in insertion order, independently of the displayed client list.

The data file is editable JSON. Existing AB3 files remain supported. Orders refer to clients and items by name in
the file: if you manually rename one, update its order references as well. Keep each order's UUID unique and use
valid quantities, dates, and completion statuses. Incorrect edits can prevent loading and cause data loss, as with
AB3; back up the file before editing it. A failed save is reported as an error and its in-memory change is undone.

### mark-order-complete

Usage: `mark-order-complete CONTACT_INDEX o/ORDER_INDEX`. Example: `mark-order-complete 1 o/2`.

Marks the selected client order as complete. CONTACT_INDEX refers to the displayed client list. ORDER_INDEX refers to that client's orders in insertion order. After deleting an order, later order indices shift down. A status command reports an error if the requested status already applies. Successful changes are saved automatically; a save error restores the previous state.

### delete-order

Usage: `delete-order CONTACT_INDEX o/ORDER_INDEX`. Example: `delete-order 1 o/2`.

Deletes the selected client order. CONTACT_INDEX refers to the displayed client list. ORDER_INDEX refers to that client's orders in insertion order. After deleting an order, later order indices shift down. A status command reports an error if the requested status already applies. Successful changes are saved automatically; a save error restores the previous state.
