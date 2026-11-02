BakeBase v1.5 beta-test evidence
==============================

Open beta-test-report.html for the consolidated findings, screenshots, priorities,
suggested PE classifications, source links, and verification limits.

The published JAR digest matches release.json. Source is a downloaded release-tag
snapshot, not this workspace's checkout. Product source and guides were not edited.

OrderProbe.java and OrderLogicProbe.java exercise the released parser/model/logic
and JSON storage. ClientItemProbe.java and ClientItemExtendedProbe.java cover
clients/items, design boundaries, undo, and real save failures. The corresponding
transcripts contain the actual results. OrderPerformanceProbe.java measures warmed
logic/storage commands; it does not measure GUI rendering.

UiProbe.java submits commands through the release CommandBox and captures scenes.
Its attempted dynamic window resizes did not propagate to the Scene in this
sandbox. Therefore its normal-data-default/minimum filenames are not reliable
indicators of dimensions. The log records the actual dimensions. Those captures
were NOT used as evidence for layout widths.

LayoutProbe.java independently starts the real release UI at a chosen initial
width. ui-run/default-740.png and ui-run/minimum-450.png, together with layout-740.log
and layout-450.log, are the reliable default/minimum layout evidence.

All product executions use the downloaded released JAR. Probes are external test
harnesses; they do not replace or recompile product classes. The Java compiler
sometimes reports a sandbox AccessDeniedException during JAR filesystem cleanup
after emitting the harness classes. Their actual executions completed.

The downloaded JAR, source snapshot, JSON test books, screenshots, transcripts, and
probe code are local review artifacts. Nothing has been committed or posted.
