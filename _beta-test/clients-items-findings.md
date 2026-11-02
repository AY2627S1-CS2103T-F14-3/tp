# Released v1.5 client/item probe

Tested the downloaded `_beta-test/BakeBase-v1.5.jar`, SHA-256 `1b1407a74debb1b2be7730535ca47e09127f290167f2d43a2c133f6860ec4cda`, using Java 25.0.4. `ClientItemProbe.java` links directly to the released JAR's `LogicManager`, `ModelManager`, and real JSON storage classes; it does not rebuild product code. This exercises the actual parser, command, model, undo, and persistence paths, but is not a GUI interaction test.

Source snapshot: `_beta-test/source/AY2627S1-CS2103T-F14-3-tp-e449792/`. Line references below are relative to this snapshot.

Transcript: `_beta-test/client-item-probe-transcript.txt` (39 commands; final persisted book reload equals in-memory book).

## Feature flaw candidates with reproduced behavior

### CI-1: `populate` overwrites the entire live bakery book without confirmation

- Suggested type: `type.FeatureFlaw`; suggested severity: `severity.Medium`, subject to team assessment of likelihood. Behavior is fully documented, so this is not a documentation/function mismatch.
- Starting with any non-empty live book, execute `populate` (or `populate typo`). Existing customers, items, and orders immediately disappear and are overwritten on disk with 12 sample clients, 12 sample items, and 24 sample orders. No warning precedes the replacement.
- Expected user-friendly design: require explicit intent before overwriting a non-empty book, e.g. `populate confirm`, as `clear confirm` already does; or reject population when the book is non-empty.
- Impact: a sample-data command exposes production users to accidental replacement of all operational records. Immediate `undo` works, but one subsequent successful data change or restart removes that recovery path. No automatic backup is created.
- Evidence: `src/main/java/seedu/address/logic/commands/PopulateCommand.java:22`; parser ignores every extra argument at `src/main/java/seedu/address/logic/parser/AddressBookParser.java:88`; one undo snapshot replacement at `src/main/java/seedu/address/logic/LogicManager.java:112`.
- UG explicitly documents overwrite at `docs/UserGuide.md:435` and ignored extra arguments at line 65. This mitigates likelihood, but does not make a design flaw immune under the PE triage guidelines.
- Confidence: observed behavior high; acceptance as a feature flaw medium. Avoid describing this as a broken documented feature or claiming immediate undo does not work.

### CI-2: Two different clients sharing the same name cannot both retain their correct name

- Suggested type: `type.FeatureFlaw`; suggested severity: `severity.Medium` (occasional inconvenience; case/spacing/alias workaround exists).
- Reproduction from an empty book:

  ```text
  add-client n/John Tan p/91234567 e/john@one.com a/Block 1
  add-client n/John Tan p/98765432 e/john@two.com a/Block 2
  ```

- Actual: second command rejects with `This client already exists in the address book.` No second record is created, even though phone, email, and address differ.
- Expected: warn about the shared name while allowing the baker to distinguish different real customers using other contact details, preserving their actual names.
- Impact: unrelated clients with common names must be stored under inaccurate aliases or artificial spacing/capitalization. This also affects customer selection for orders and can cause confusion when fulfilling orders.
- Evidence: `src/main/java/seedu/address/model/person/Person.java:68` defines identity by name; `src/main/java/seedu/address/logic/commands/AddCommand.java` rejects `model.hasPerson(toAdd)`; UG documents the restriction at `docs/UserGuide.md:77`.
- Control: `add-client n/john  tan p/98765432 e/john@two.com a/Block 2` succeeds with a warning, showing a workaround exists. The warning correctly compares near-duplicate names; do not report near-match detection as broken.
- Confidence: behavior high; feature-flaw acceptance medium. A more substantial identity redesign may justify discussion of `NotInScope` under PE scope rules; documentation alone is not a sufficient rejection reason.

### CI-3: Ordinary internationally formatted phone numbers are rejected

- Suggested type: `type.FeatureFlaw`; suggested severity: `severity.Low` because removing `+`/spaces is a simple workaround.
- Reproduction:

  ```text
  add-client n/Mary Lee p/+6591234567 e/mary@example.com a/Block
  ```

- Actual: rejects the entire client with `Phone numbers should only contain digits, and should be at least 3 digits long`.
- User impact: phone numbers copied from contacts/WhatsApp commonly contain `+` or spacing. Users must manually rewrite them, and cannot preserve an international dialing marker or multiple labeled numbers.
- Evidence: `src/main/java/seedu/address/model/person/Phone.java:15` uses `\\d{3,}`; there is no subsequent numeric operation that requires phone to be digits only.
- Course basis: PE guidelines explicitly mention overzealous phone validation as a feature-flaw example. Assess this as a design limitation, not a parser exception/crash.

## Lower-confidence observations (do not present as confirmed bugs)

- Unknown prefixes are treated as literal field text. E.g. `edit-client 1 n/Johnny Tan x/typo` renames to `Johnny Tan x/typo`; `edit-client 1 a/Somewhere q/9` stores the whole address. A mistyped prefix can pollute data, but slash text is explicitly allowed, so rejecting all unknown prefixes would also reject legitimate content. Needs a concrete realistic scenario to justify a report.
- Tabs between prefixed arguments are not accepted by the tokenizer. A harness string with tab-separated `n/`, `p/`, `e/`, `a/` returns a normal parse error. This is a low-impact documentation precision issue if the UG says generic whitespace, and requires GUI paste verification; do not claim ordinary double spaces fail.
- Arbitrarily large prices are accepted (`99999999999999999999999999999999` succeeds), but no overflow or storage loss occurred. Deliberately extreme inputs are excluded by course rules unless they cause realistic user harm; no report recommended from this alone.
- Names are intentionally ASCII-only and common accented/non-Latin names may be blocked. Not tested by this probe; needs direct reproduction and product-fit reasoning. The UG already states the limitation.
- Email `name+@example.com` is rejected according to the displayed local-part constraint. No report without a concrete supported valid real-address scenario.

## Checks that passed

- Similar-name additions succeed with a clear warning and correct matching details.
- Exact-name duplicate clients reject without adding a second entry.
- Filtered client edit/delete and filtered item edit/delete target the displayed record correctly.
- Normalized bakery item duplicates (case and repeated internal spaces) reject without change.
- `.5` and `0` prices parse and format as `0.50` / `0.00`.
- Client/item renames update linked orders; JSON reload preserves the renamed references.
- Deleting a referenced client/item is rejected with a useful explanation.
- A no-op client edit preserves the prior data-changing undo opportunity.
- Undo restores deleted clients/items, resets views, and is consumed after one success.
- `clear` shows counts and leaves data unchanged; `clear confirm extra` rejects without change.
- `populate` can be immediately undone, restoring clients, items, and linked orders.
- Final JSON reload equals the in-memory book.

## Re-run

```powershell
javac -cp _beta-test/BakeBase-v1.5.jar -d _beta-test _beta-test/ClientItemProbe.java
java -cp '_beta-test;_beta-test/BakeBase-v1.5.jar' ClientItemProbe
```

The sandboxed Java compiler printed an `AccessDeniedException` while closing its ZIP filesystem for the JAR, but successfully emitted `ClientItemProbe.class`; the resulting Java execution completed with exit code 0 and the transcript above. This is a test-environment compiler cleanup problem, not evidence of a BakeBase defect.

Course reference: [PE guidelines for bug triaging](https://nus-cs2103-ay2627-s1.github.io/website/admin/tp-pe.html#guidelines-for-bug-triaging). The relevant principles are documented limitations can still be bugs, inherited AB3 behavior is still in scope, and overly strict validation is assessed from the end user's needs.

## Extended direct verification

`ClientItemExtendedProbe.java` and `client-item-extended-transcript.txt` add 33 checks against the same released JAR, using separate UUID-named scratch folders. All checks passed, meaning the observed behavior matched each explicit assertion below; the design limitations listed here were reproduced, not repaired.

| Check | Actual released-JAR result |
| --- | --- |
| Two `John Tan` records with distinct phones, emails, and addresses | First succeeds; second rejects; only one stored |
| `José Tan` | Rejected as non-ASCII |
| `王小明` | Rejected as non-ASCII |
| `Anne O’Neil` with a curly apostrophe | Rejected as non-ASCII |
| `Anne O'Neil` with a straight apostrophe | Accepted |
| Phone `+6591234567` | Rejected |
| Phone `9123 4567` | Rejected |
| Phone `6591234567` | Accepted |
| Add `Real Customer`, run `populate`, add `New Customer`, run `undo` | Returns to 12 sample clients; `Real Customer` still absent |
| Reload JSON after that sequence | Same 12 sample clients; `Real Customer` absent |
| Second `undo` | Reports no data change to undo |

For the save-failure check, the harness created Alice and Bob, Cake and Bread, and an Alice/Cake order. It filtered clients to Bob and items to Bread, then selected incomplete orders sorted by deadline. The most recent saved data change was the order addition. It moved the data file to a backup within its unique scratch directory and placed an empty directory at the exact data-file path.

An attempted `edit-client 1 a/Bob New Address` returned a save error. The entire prior book, the Bob/Bread filters, the incomplete-order filter, deadline sorting, and the earlier undo snapshot were restored. The untouched backup still loaded as the original book. Attempting `undo` while the destination remained blocked also returned a save error and restored the current book and views while retaining the snapshot. After removing only that empty blocking directory and restoring the backup file, retrying `undo` succeeded: it removed the original order addition, showed all clients/items, reset sorting, consumed the snapshot, and persisted the result. No rollback defect was found.

The ordinary-input checks support an additional `type.FeatureFlaw` candidate: ASCII-only client names prevent accurate storage of common accented, non-Latin, and curly-apostrophe names. Suggested severity is `Medium` for affected users, who must substitute an inaccurate ASCII spelling; the UG explicitly documents this restriction. This is a design limitation, not an unexpected exception or documentation mismatch.

Independent address-clipping inspection found no tooltip, wrapping, context menu/copy action, or client-details command in the released `PersonCard`, `PersonListCard.fxml`, and `PersonListPanel`. The default window width is 740 pixels. However, do not claim there is absolutely no nonmutating workaround: widening the window may reveal ordinary addresses, and `edit-client INDEX p/<unchanged visible phone>` produces a full-details success message without changing data or consuming undo. Any report should describe the default-width inconvenience and require the root UI probe's screenshot/layout verification.
