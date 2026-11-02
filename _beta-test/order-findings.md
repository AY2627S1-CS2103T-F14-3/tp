# Order beta-test findings (released v1.5)

Release binary tested: `_beta-test/BakeBase-v1.5.jar`.
Source reference: `_beta-test/source/AY2627S1-CS2103T-F14-3-tp-e449792` (release target e449792e137b14dcb3017da2c688d2a71f677a25).

## Confirmed function bugs

No new defensible order function bug was reproduced in this pass. This is a scoped result, not a claim that the feature is bug-free.

## Verification performed

`java --class-path .\_beta-test\BakeBase-v1.5.jar .\_beta-test\OrderProbe.java`

Result: **PASS 1,511 checks**. A deterministic sequence of 1,500 random order additions, edits and deletions checked the displayed list against independently calculated stable deadline sorting after every mutation. Rejected zero/negative/over-limit quantities, impossible dates, duplicate required prefixes and integer-overflow indices preserved model data.

`java --class-path .\_beta-test\BakeBase-v1.5.jar .\_beta-test\OrderLogicProbe.java`

Result: **PASS 59 checks** through the released `LogicManager` and real JSON storage. Transcript: `_beta-test/order-transcript.txt`. Valid calendar boundaries included years 0000, 0001 and 9999 and leap day in year 2000. Typo/unknown prefixes, missing separators, duplicate prefixes and invalid leap days were rejected without mutation. Verified displayed client/item indices under filters, automatic completion-filter membership, editing a hidden order, stable sorted ties, undo resetting sorting and showing all lists, no-op edits retaining undo, linked client/item renames updating order search membership, deletion protections, and storage round-trip equality after each successful command.

`java --class-path .\_beta-test\BakeBase-v1.5.jar .\_beta-test\OrderPerformanceProbe.java`

Result: no reproduced performance failure at the DG's stated 100 clients / 100 bakery items / 1000 orders scale. Twelve list/search/filter commands, including all deadline-sorted variants, were run through released `LogicManager` and real JSON storage after two complete warmup cycles, with five measured cycles. This included command parsing, full-book snapshots, serialization and atomic file replacement. Maximum warmed observation was **57.46 ms** (`find-orders Client`); `list-orders by-deadline` maximum was **52.73 ms**. Stored data remained unchanged after all read-only commands. Host: Java 25.0.4, Windows 11 amd64, 24 logical processors. Transcript: `_beta-test/order-performance-transcript.txt`.

These observations exclude GUI rendering and cold start. The DG specifies the team's reference machine, which is not identified; this host result does not certify that NFR.

## Documentation issues for consolidation

1. **UG unqualified visibility claim for marking** (release `docs/UserGuide.md:308`, under `mark-order-complete`). Says the order remains visible. In the incomplete-orders view, marking correctly removes it, as the later completion-filter section states at line 383. A low-severity documentation clarification; the proper wording is that the order remains stored and is visible in the all/completed views. Forwarded to parent; do not call the intended filtering behavior a function bug.

An initial local-HEAD suspect about a stale DG delete-order section was ruled out: the released DG already replaces it with the accurate 'Deleting a client order' section. It is not a v1.5 bug.

## Limitations

The order probes use public released parser/logic/model/storage classes, not GUI command entry. The parent audit covers GUI behavior. UI rendering under extreme input, 1000-order GUI response time, timezone changes while running, and multi-process access were not tested here.
