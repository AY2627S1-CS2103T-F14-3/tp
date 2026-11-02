# v1.5 documentation and design audit

Reviewed the documentation in the exact release source snapshot `e449792` at
`_beta-test/source/AY2627S1-CS2103T-F14-3-tp-e449792`, and inspected the committed PNG images visually.
Release JAR: `_beta-test/BakeBase-v1.5.jar`; SHA-256
`1b1407a74debb1b2be7730535ca47e09127f290167f2d43a2c133f6860ec4cda`.
Runtime reproduction uses release classes through `DocsProbe.java`, with a separate disposable
`_beta-test/docs-probe/addressbook.json`; transcript: `docs-probe-transcript.txt`.
These are suggested PE classifications and severities, subject to team/tutor triage.

The release-attached `UserGuide-v1.5.md` and `DeveloperGuide-v1.5.md` match the snapshot after
normalizing line endings. The currently published HTML is saved as `published-UserGuide.html` and
`published-DeveloperGuide.html`; it contains the same visibility claim, wrong JAR filename,
unfinished testing subsection, and image references described below. Downloaded live PNGs are
byte-for-byte identical to the snapshot images (SHA-256 checked):

| Live image | SHA-256 |
| --- | --- |
| `ArchitectureSequenceDiagram.png` | `d69fa69e99d3f89574b173a84603fbd57340147d75693b9e12d38cc6607a1393` |
| `DeleteSequenceDiagram.png` | `3b32c5e1a357c76e266b7263f4a3c96c062ee04b628f2ad020c29f988af21510` |
| `helpMessage.png` | `0d4c1f7f3707d285b443abec9261f93b53f2f369f8eb36163462239869af7b08` |

## D1: UG promises completed orders stay visible, even when incomplete filter is active

- Type: DocumentationBug. Suggested severity: Low.
- Evidence: release `docs/UserGuide.md:308` says, unconditionally, “The order remains visible in the order panel.”
  The same UG at lines 383–384 correctly explains that completing an order removes it from the incomplete view.
- Reproduction in a disposable book: add a client, item, and order; run `list-incomplete-orders`, then `mark 1 o/1`.
  Release JAR reports success, the stored order count stays 1, and the displayed order count changes from 1 to 0.
  `list-orders` restores the visible completed order.
- Expected documentation: qualify visibility as applying to the all-orders view; under an active completion filter,
  the order can disappear from the displayed list while remaining stored.
- Impact: a user following the marking instructions can reasonably think the order disappeared unexpectedly.
  This is a documentation contradiction, not a functional error in the completion filter.

## D2: Committed DG sequence diagrams still use unsupported `delete 1`

- Type: DocumentationBug. Suggested severity: Low.
- Evidence: release `docs/DeveloperGuide.md:65–67` and `:105–107` introduce the scenario as `delete-client 1`.
  The embedded `docs/images/ArchitectureSequenceDiagram.png` and `docs/images/DeleteSequenceDiagram.png`
  visibly show `delete 1` and `execute("delete 1")` instead. The matching `.puml` files have already been changed
  to `delete-client 1`, so the exported PNGs are stale.
- Reproduction: view those images in the DG; type `delete 1` in the release. Actual response: `Unknown command.`
- Additional inaccuracy in `DeleteSequenceDiagram.png`: `DeleteCommand` invokes `Model.deletePerson(1)`, while
  `Model.deletePerson` accepts a `Person`; displayed index resolution occurs before that call.
- Expected: regenerate the PNGs from corrected diagram source and use the resolved `Person` parameter.
- Impact: maintainers and testers see a command/API flow that cannot occur in the released implementation.
  This entry groups a maintenance action. The architecture PNG and logic PNG are independently editable:
  correcting one does not correct the other. Do not automatically classify reports concerning the two
  different PNGs as PE duplicates. Assess each report's actual root cause and required correction.

## D3: UG Help screenshot describes pre-confirmation clear behavior

- Type: DocumentationBug. Suggested severity: Low.
- Evidence: release `docs/UserGuide.md:427` embeds `docs/images/helpMessage.png`. The screenshot says
  `clear: Clears all data from the address book.` and `Example: clear`. Its general command list also lacks `undo`.
  Actual v1.5 Help usage is `clear: Shows the deletion warning and record counts.` followed by the explicit
  `clear confirm` behavior and `Usage: clear [confirm]`.
- Reproduction: inspect the screenshot, then run `clear` in the release with records present.
  The command reports that no data was deleted and shows the confirmation warning; all records remain.
- Expected: capture the actual v1.5 Help window, including current clear usage and undo entry.
- Impact: users referring to the embedded screenshot receive different clear instructions from the actual app.
  Correcting only prose or `.puml` sources does not update this image.

## D4: DG order-editing manual test launches a filename absent from the release

- Type: DocumentationBug. Suggested severity: Low.
- Evidence: release `docs/DeveloperGuide.md:643–646` instructs users to launch the release with
  `java -jar "addressbook.jar"`; the v1.5 downloadable asset is `BakeBase-v1.5.jar`, also named correctly
  in release `docs/UserGuide.md:20–24`.
- Reproduction: download the release into an empty folder and execute the DG command there.
  Actual result: `Error: Unable to access jarfile addressbook.jar`. This was reproduced in `_beta-test`,
  which contains the actual released JAR and no `addressbook.jar`.
- Expected: name `BakeBase-v1.5.jar`, or explicitly instruct the reader to substitute the downloaded filename.
- Impact: the exact manual-test steps fail before the tester can reach the order-editing checks.
  The separate DevOps guide correctly calls the local build output `addressbook.jar`; this finding concerns
  the DG instruction to launch the downloaded release.

## D5: Incomplete DG data-storage manual testing section

- Type: DocumentationBug. Suggested severity: VeryLow/Low; secondary to reproducible discrepancies above.
- Evidence: release `docs/DeveloperGuide.md:622–628` retains the literal instruction
  `_{Explain how to simulate missing or corrupted data files and state the expected behavior.}_`
  and another `more test cases` placeholder.
- Actual: no executable missing/corrupted-file test steps or expected results are provided in this manual-test section.
- Expected: specify separate missing-file and invalid-file setup, launch, expected fallback behavior, and
  overwrite/backup consequences; remove unfinished template prompts.
- Impact: the advertised storage testing subsection cannot guide a tester through a data-loss-sensitive scenario.
  Avoid submitting every remaining `more test cases` placeholder as a separate bug.

## F1: Historical completed orders prevent retiring a bakery item

- Type: FeatureFlaw, not FunctionalityBug. Suggested severity: Low; design judgment, not certain acceptance.
- Evidence: release UG correctly documents that deleting an item requires deleting its referenced orders.
  DG user stories include removing bakery goods and retaining completed-order history. There is no archive/inactive
  item command in the release.
- Reproduction: create a client and `Seasonal Tart`, add an order, mark it complete, then run `delete-item 1`.
  Actual error: `Cannot delete this item: existing orders reference this item. Delete those orders first.`
- User problem: a baker retiring a seasonal/discontinued item must keep that obsolete entry in the active catalogue
  forever, or delete all its completed order history. Renaming or repricing does not solve retirement.
- Possible improvement: retain historical order references while marking catalogue items inactive/archived.
  Because this changes product design, assess feature-freeze rules before choosing an implementation change.
- Limit: retaining referential integrity is deliberate and implemented correctly; the flaw claim concerns the
  resulting business workflow. Do not claim that a completed-order reference should simply be ignored on deletion.

## Findings discarded after checking the exact release

The local checkout is behind the release documentation. The release already corrects the following apparent
problems, so they must not be filed against v1.5: the stale claim that `delete-order` is unimplemented,
missing no-orders prerequisites for the DG client-deletion test, default-sample duplicate quick-start commands,
and the UG claim that storage always resides beside the JAR. Current release UG correctly explains the
working-directory rule and instructs users to edit JSON while the app is closed.

The initial web-tool and sandboxed network fetches failed, but subsequent approved read-only fetches
obtained the public HTML and the three live PNGs. The evidence above therefore covers the release-attached
Markdown, release-tag snapshot, and current published website. Published HTML references the same PNGs
(`images/ArchitectureSequenceDiagram.png`, `/tp/images/DeleteSequenceDiagram.png`, and
`/tp/images/helpMessage.png`).

The release-attached Markdown files use relative `images/...` references and do not package an images
directory beside the downloaded `.md` assets. Opening either downloaded Markdown file alone will not
resolve those diagrams/screenshots unless the reader also supplies the repository's `docs/images` directory.
This is a document-distribution scope note, not an additional filed bug here: the published HTML resolves
the images correctly and is the normal complete document-viewing route.

Other items were intentionally not promoted to firm findings: all user stories need not be implemented;
archiving is clearly labeled future work; unsupported non-ASCII names are documented; required phone/email/tag
constraints omitted from the UG may be an improvement, but validation errors do explain them; typo prefixes can
also be legitimate literal text in free-text fields. No product source or guides were edited during this audit.
