"""Builds a local, self-contained beta-test findings index from verified evidence."""

from html import escape
from pathlib import Path

ROOT = Path(__file__).resolve().parent
SNAPSHOT = "source/AY2627S1-CS2103T-F14-3-tp-e449792/"
COURSE = "https://nus-cs2103-ay2627-s1.github.io/website/admin/tp-pe.html#guidelines-for-bug-triaging"


def code(text):
    return "<pre>" + escape(text) + "</pre>"


def source(path, line):
    url = "https://github.com/AY2627S1-CS2103T-F14-3/tp/blob/v1.5/" + path + "#L" + str(line)
    return f'<a href="{url}">{escape(path)}:{line}</a>'


findings = [
    {
        "id": "F1", "title": "Sample population can overwrite the live business book without confirmation",
        "type": "FeatureFlaw", "severity": "Medium", "status": "Reproduced design risk",
        "body": "<p>In a disposable empty book, run:</p>" + code(
            "add-client n/Business Client p/91234567 e/business@example.com a/Block 1\n"
            "add-item n/Business Cake p/20\nadd-order 1 i/1 q/2 d/2026-11-03\n"
            "populate\nadd-client n/New Customer p/98765432 e/new@example.com a/Block 2\nundo")
        + "<p><b>Actual:</b> population immediately replaces and saves every business record with samples. "
        "Undo after the subsequent addition removes only that addition; the original business book remains absent "
        "after reload. <code>populate typo</code> also performs the replacement. Immediate undo directly after "
        "population works correctly.</p><p><b>Impact:</b> trying a sample-data command in a live book risks losing "
        "all operational records. The UG documents replacement, so the claim concerns the absence of a safeguard, "
        "not incorrect implementation. Existing <code>clear</code> already has a confirmation design.</p>"
        "<p><b>Smallest useful mitigation:</b> require explicit confirmation for replacing a nonempty book, "
        "or permit population only in an empty book. Acceptance and Medium severity are judgment calls: documented "
        "behavior and immediate undo reduce likelihood, but do not remove the risk.</p>"
        + "<p>Evidence: " + source("src/main/java/seedu/address/logic/commands/PopulateCommand.java", 22)
        + '; <a href="client-item-extended-transcript.txt">extended transcript</a>.</p>',
    },
    {
        "id": "F2", "title": "Distinct customers with the same name cannot retain their correct names",
        "type": "FeatureFlaw", "severity": "Medium", "status": "Reproduced design limitation",
        "body": "<p>From an empty book:</p>" + code(
            "add-client n/John Tan p/91234567 e/john@one.com a/Block 1\n"
            "add-client n/John Tan p/98765432 e/john@two.com a/Block 2")
        + "<p><b>Actual:</b> the second, different customer is rejected as an existing client. Phone, email, and "
        "address do not distinguish identity. Changing the second name to <code>john  tan</code> permits it with "
        "a warning.</p><p><b>Impact:</b> bakers with customers sharing a common name must invent aliases, spacing, "
        "or capitalization; that can confuse order fulfillment. The UG explicitly documents the restriction.</p>"
        "<p><b>Direction:</b> allow a warning plus distinct client identities. This is a broader identity change: "
        "orders currently serialize references by name, so merely weakening duplicate rejection would be unsafe. "
        "Assess scope and effort before changing it; the workaround can support a lower severity.</p><p>Evidence: "
        + source("src/main/java/seedu/address/model/person/Person.java", 68)
        + '; <a href="client-item-probe-transcript.txt">command transcript</a>.</p>',
    },
    {
        "id": "U1", "title": "Ordinary delivery addresses are truncated at the default window width",
        "type": "FunctionalityBug", "severity": "Low", "status": "Reproduced UI defect",
        "body": "<p>Use a default-width 740px window, then enter:</p>" + code(
            "add-client n/Alexandra Catherine Tan p/91234567 e/alexandra.tan@example.com "
            "a/123 Ang Mo Kio Avenue 3 #12-456 Singapore 560123")
        + "<p><b>Actual:</b> the address ends visibly at <code>#12-4...</code>; the unit suffix and postal code "
        "cannot be read on the card. The label has no tooltip and wrapping is disabled. Its allocated width is "
        "about 199px versus a preferred 309px. The 450px permitted minimum also clips client/item names and "
        "contact details.</p><p><b>Impact:</b> this is ordinary delivery information, not an extreme long-input "
        "attack. Widening the window works for this address, so Low is proportionate. A no-op client edit can "
        "also print full details, but is an awkward workaround. Classifying deliberate truncation as FeatureFlaw "
        "instead would also be reasonable.</p><p><b>Fix:</b> wrap the address or expose its full value through "
        "a tooltip or details view.</p><figure><img src="
        '"ui-run/default-740.png" alt="Default-width release UI truncating a normal delivery address">'
        "<figcaption>Rendered from the released JAR at the default initial window width.</figcaption></figure>"
        "<p>Evidence: " + source("src/main/resources/view/PersonListCard.fxml", 32)
        + '; <a href="ui-run/layout-740.log">layout properties</a>; '
        '<a href="ui-run/minimum-450.png">minimum-width rendering</a>.</p>',
    },
    {
        "id": "D1", "title": "UG says marked orders stay visible even under the incomplete filter",
        "type": "DocumentationBug", "severity": "Low", "status": "Confirmed mismatch",
        "body": "<p>The marking section unconditionally says that an order remains visible. With an existing "
        "incomplete order, run:</p>" + code("list-incomplete-orders\nmark 1 o/1")
        + "<p><b>Actual:</b> the order correctly leaves the incomplete view while remaining stored. "
        "<code>list-orders</code> shows it again. The later completion-filter section already describes this "
        "correctly. The error is in the unqualified visibility statement.</p><p><b>Fix:</b> say that marking "
        "retains the order, and visibility depends on the active filter.</p><p>Evidence: "
        + source("docs/UserGuide.md", 308) + '; <a href="docs-probe-transcript.txt">release transcript</a>.</p>',
    },
    {
        "id": "D2", "title": "UG Help screenshot still describes the old clear command",
        "type": "DocumentationBug", "severity": "Low", "status": "Confirmed stale image",
        "body": "<p>The Help screenshot tells readers that <code>clear</code> clears all data and uses "
        "<code>clear</code> as its example. In v1.5, that command only shows a warning and counts; "
        "<code>clear confirm</code> performs deletion. The current Help also has an undo entry absent from "
        "the pictured command sequence.</p><p><b>Fix:</b> replace the screenshot with the released Help UI.</p>"
        "<p>Evidence: " + source("docs/UserGuide.md", 427)
        + f'; <a href="{SNAPSHOT}docs/images/helpMessage.png">stale screenshot</a>; '
        '<a href="ui-run/help-current.png">actual released Help</a>.</p>',
    },
    {
        "id": "D3", "title": "DG architecture sequence PNG still uses unsupported delete 1",
        "type": "DocumentationBug", "severity": "Low", "status": "Confirmed stale image",
        "body": "<p>The architecture scenario prose and diagram source use <code>delete-client 1</code>, "
        "but the embedded PNG still uses <code>delete 1</code>. Entering the pictured command in v1.5 "
        "returns <code>Unknown command.</code></p><p><b>Fix:</b> regenerate and verify the architecture "
        "sequence PNG.</p><p>Evidence: " + source("docs/DeveloperGuide.md", 67)
        + f'; <a href="{SNAPSHOT}docs/images/ArchitectureSequenceDiagram.png">published diagram asset</a>.</p>',
    },
    {
        "id": "D4", "title": "DG logic sequence PNG shows the old command and incorrect model argument",
        "type": "DocumentationBug", "severity": "Low", "status": "Confirmed stale image",
        "body": "<p>The logic sequence image shows <code>execute(\"delete 1\")</code> and "
        "<code>Model.deletePerson(1)</code>. The released parser requires <code>delete-client</code>, "
        "and the model API takes a resolved <code>Person</code>, not an integer.</p>"
        "<p><b>Fix:</b> correct the model argument in the diagram source and regenerate the PNG. This image "
        "can be fixed independently of D3; do not assume that the two are PE duplicates merely because both "
        "exports are stale.</p><p>Evidence: " + source("docs/DeveloperGuide.md", 107)
        + f'; <a href="{SNAPSHOT}docs/images/DeleteSequenceDiagram.png">published logic sequence asset</a>.</p>',
    },
    {
        "id": "D5", "title": "DG order-editing test launches a filename absent from the release",
        "type": "DocumentationBug", "severity": "Low", "status": "Confirmed failed instruction",
        "body": "<p>The manual order-editing test says to launch the release with:</p>"
        + code('java -jar "addressbook.jar"')
        + "<p>The downloadable file is <code>BakeBase-v1.5.jar</code>. In a clean release folder the stated "
        "command fails with <code>Unable to access jarfile addressbook.jar</code>.</p>"
        "<p><b>Fix:</b> use the published filename or explicitly tell readers to substitute the downloaded "
        "name. The local Gradle output really is named addressbook.jar; the incorrect instruction specifically "
        "says to launch the release.</p><p>Evidence: " + source("docs/DeveloperGuide.md", 646) + ".</p>",
    },
    {
        "id": "D6", "title": "DG corrupted-data manual test remains an unfilled template instruction",
        "type": "DocumentationBug", "severity": "Low", "status": "Confirmed missing instructions",
        "body": "<p>The data-storage subsection still contains the literal authoring prompt asking the team "
        "to explain how to simulate missing/corrupted files and state expected behavior. It supplies neither "
        "an executable setup nor expected fallback results.</p><p><b>Fix:</b> give separate missing-file "
        "and invalid-file cases, including backup precautions and what a subsequent command overwrites. "
        "This is incomplete testing guidance, not evidence that storage recovery itself fails.</p><p>Evidence: "
        + source("docs/DeveloperGuide.md", 626) + ".</p>",
    },
    {
        "id": "F3", "title": "Normal formatted phone numbers are rejected",
        "type": "FeatureFlaw", "severity": "Low", "status": "Reproduced design limitation",
        "body": code("add-client n/Mary Lee p/+6591234567 e/mary@example.com a/Block 1")
        + "<p><b>Actual:</b> the whole client is rejected because the phone contains a plus sign. "
        "<code>9123 4567</code> is also rejected; digits-only forms work.</p><p><b>Impact:</b> numbers copied "
        "from contacts require manual rewriting, and the international dialing marker cannot be preserved. "
        "The course explicitly treats overly restrictive phone validation as a possible feature flaw. "
        "Removing formatting is an easy workaround, so Low is appropriate.</p><p><b>Direction:</b> accept "
        "reasonable display formatting or normalize it with clear feedback.</p><p>Evidence: "
        + source("src/main/java/seedu/address/model/person/Phone.java", 15)
        + '; <a href="client-item-extended-transcript.txt">extended transcript</a>.</p>',
    },
    {
        "id": "F4", "title": "Real customer names outside ASCII cannot be entered faithfully",
        "type": "FeatureFlaw", "severity": "Low", "status": "Secondary design concern",
        "body": "<p>Names <code>José Tan</code>, <code>王小明</code>, and <code>Anne O’Neil</code> were "
        "rejected with otherwise valid client fields. <code>Anne O'Neil</code> with a straight apostrophe "
        "succeeds.</p><p><b>Impact:</b> home bakers serving multilingual customers must transliterate or alter "
        "their names. The release notes and UG explicitly disclose ASCII-only support, so this is a product-fit "
        "argument, not an undocumented parser bug. Keep it below the stronger issues; graceful rejection and "
        "the effort to support Unicode can affect scope triage.</p><p>Evidence: "
        + source("src/main/java/seedu/address/model/person/Name.java", 21)
        + '; <a href="client-item-extended-transcript.txt">reproduction</a>.</p>',
    },
    {
        "id": "F5", "title": "Retiring catalogue items requires losing completed order history",
        "type": "FeatureFlaw", "severity": "Low", "status": "Lower-confidence scope concern",
        "body": "<p>Create an item and order, mark the order complete, then run <code>delete-item 1</code>. "
        "Deletion is correctly blocked until all referencing orders are deleted, including historical completed "
        "orders.</p><p><b>Workflow cost:</b> a retired seasonal item must remain in the active catalogue or its "
        "history must be removed. This is intentional referential integrity, not failed deletion logic. "
        "An inactive/archive state could address it, but archiving is already described as future work and "
        "the implementation effort makes a NotInScope argument plausible. Do not prioritize it as a confirmed "
        'functional defect.</p><p>Evidence: <a href="docs-probe-transcript.txt">completed-order deletion case</a>.</p>',
    },
]

rows = "".join(
    f'<tr><td><a href="#{f["id"]}">{f["id"]}</a></td><td>{escape(f["title"])}</td>'
    f'<td>{f["type"]}</td><td>{f["severity"]}</td><td>{f["status"]}</td></tr>' for f in findings
)
details = "".join(
    f'<article id="{f["id"]}"><h2>{f["id"]} · {escape(f["title"])}</h2>'
    f'<p class="tags">type.{f["type"]} · severity.{f["severity"]} · {f["status"]}</p>{f["body"]}</article>'
    for f in findings
)

page = """<!doctype html><html lang="en"><meta charset="utf-8">
<meta name="viewport" content="width=device-width,initial-scale=1">
<title>BakeBase v1.5 beta test findings</title>
<style>
body{font:16px/1.6 system-ui,sans-serif;color:#202830;background:#f4f6f8;margin:0}
main{max-width:1120px;margin:auto;padding:36px 28px}h1{font-size:32px;line-height:1.2}
h2{font-size:22px;line-height:1.35}a{color:#175cb5}p{max-width:95ch}
header,article,section{background:white;border:1px solid #dce2e8;border-radius:10px;padding:24px;margin:20px 0}
table{width:100%;border-collapse:collapse;font-size:14px}th,td{padding:10px;text-align:left;vertical-align:top;border-bottom:1px solid #dce2e8}
th{background:#edf1f6}pre{white-space:pre-wrap;overflow-wrap:anywhere;background:#eef2f6;padding:14px;border-radius:6px}
code{background:#eef2f6;padding:1px 4px}.tags{font-size:14px;color:#566675}
img{max-width:100%;height:auto;border:1px solid #dce2e8}figure{margin:20px 0}figcaption{font-size:13px;color:#566675}
article{scroll-margin:20px}.table-wrap{overflow:auto}.meta{overflow-wrap:anywhere;font-size:14px}
@media print{body{background:white}main{padding:0}article,section,header{border:0;padding:8px}a{color:#222}}
</style><main>
<header><h1>BakeBase v1.5 beta test findings</h1>
<p>Tested on 30 October 2026, using the published release JAR on Windows with Java 25.0.4.
One UI issue and six documentation issues were reproduced. Five design concerns are separated below;
their behavior is proven, while PE acceptance and severity require judgment. No High-severity issue or core
order, undo, sorting, or persistence failure was reproduced.</p>
<p class="meta">Release commit: e449792e137b14dcb3017da2c688d2a71f677a25<br>
JAR SHA-256: 1b1407a74debb1b2be7730535ca47e09127f290167f2d43a2c133f6860ec4cda<br>
The hash matches the GitHub release asset digest. Attached guides match the release snapshot after newline
normalization. The published HTML and three cited image assets were also checked.</p></header>
<section><h2>Priority and PE classification</h2>
<p>First address F1 and the clipped delivery address. Refresh the incorrect guide content and images next.
Review the same-name restriction with the team before an identity redesign. Phone/name flexibility and
catalogue retirement are lower-priority design decisions. This review changed no product code or guides.</p>
<p>FunctionalityBug covers behavior that fails its specification or reasonable expectation. FeatureFlaw
covers a design that impedes normal target-user work, even when documented. DocumentationBug covers
incorrect or missing reader guidance. Medium means occasional inconvenience; Low means minor or uncommon
inconvenience. High is reserved for major problems affecting most users. Deliberate sabotage alone is excluded;
documenting a limitation alone does not guarantee rejection. These are suggested classifications under the
<a href="COURSE_URL">course triage rules</a>, not a prediction of tutor decisions.</p>
<div class="table-wrap"><table><thead><tr><th>ID</th><th>Finding</th><th>Suggested type</th><th>Severity</th><th>Evidence status</th></tr></thead><tbody>
ROWS
</tbody></table></div></section>
<section><h2>What passed and what remains untested</h2>
<ul><li>Launched the actual JAR with <code>java -jar</code> in an isolated writable folder.</li>
<li>1,570 order assertions passed, including 1,500 deterministic random transitions checked against independent
stable-sort expectations, validation boundaries, filtered indices, hidden orders, linked renames, undo, and JSON reload.</li>
<li>39 client/item command probes and 33 further assertions covered duplicate detection, linked edits,
save-failure rollback, retained undo after failures, and retry persistence.</li>
<li>At 100 clients, 100 items, and 1,000 orders, all 60 warmed logic/storage timing samples passed 3 seconds.
The slowest was 57.46ms. These measurements include JSON saving and exclude GUI rendering and cold start.</li>
<li>12 commands were also submitted through the real release CommandBox and full JavaFX UI. Help opened;
marking updated the filter; undo worked; exit closed all showing windows. Scene snapshots verified initial
740px and 450px layouts.</li></ul>
<p>Linux/macOS, display scaling, actual multi-monitor behavior, dynamic native resizing, 1,000-order UI
rendering latency, and the unspecified team reference machine were not verified. The sandbox could not capture
the desktop; UI images are JavaFX scene snapshots. A javac ZIP-filesystem cleanup warning occurred after class
emission; probe executions completed. It is not a BakeBase failure.</p>
<p><a href="order-transcript.txt">Order evidence</a> · <a href="order-performance-transcript.txt">Timing evidence</a> ·
<a href="client-item-probe-transcript.txt">Client/item evidence</a> ·
<a href="client-item-extended-transcript.txt">Rollback and design evidence</a> ·
<a href="docs-probe-transcript.txt">Documentation reproduction</a> ·
<a href="ui-run/ui-probe.log">UI command evidence</a></p></section>
DETAILS
<section><h2>Reporting and fix discipline</h2>
<p>Use one independently fixable defect per issue. Stale screenshots and their incorrect prose are not
automatically duplicates. Keep runtime bugs separate from guide corrections and explain the real baker workflow
for every feature-flaw claim. Do not report the correctly implemented completion filter or reference-deletion
protection as a broken function.</p>
<p>Several apparent local-checkout defects were excluded because the release already fixes them: the old
unimplemented delete-order claim, missing deletion-test preconditions, conflicting quick-start examples,
and the wrong working-directory storage explanation. Extreme prices did not reproduce overflow or data loss.
No GitHub issues were posted.</p>
<p>Source materials: <a href="https://nus-cs2103-ay2627-s1.github.io/website/schedule/week11/project.html#6-beta-test-your-own-product">Week 11 beta-test task</a>,
<a href="COURSE_URL">PE triage rules</a>, <a href="https://github.com/AY2627S1-CS2103T-F14-3/tp/releases/tag/v1.5">v1.5 release</a>,
<a href="UserGuide-v1.5.md">downloaded release UG</a>, <a href="DeveloperGuide-v1.5.md">downloaded release DG</a>.</p></section>
</main></html>"""
page = page.replace("COURSE_URL", COURSE).replace("ROWS", rows).replace("DETAILS", details)
(ROOT / "beta-test-report.html").write_text(page, encoding="utf-8")
print(f"Created {len(findings)} findings in {ROOT / 'beta-test-report.html'}")
