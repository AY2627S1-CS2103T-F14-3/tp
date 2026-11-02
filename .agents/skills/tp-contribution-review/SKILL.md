---
name: tp-contribution-review
description: Review personal contributions to BakeBase against CS2103T tP expectations, especially implementation code quality and effort. Use for contribution audits, grading readiness, or prioritising improvements before submission.
---

# Review tP contributions

Produce an evidence-based review and prioritised improvement plan for this repository. Focus on implementation (15 marks), while checking related individual expectations. Do not promise marks or invent a numerical breakdown between code quality and effort. A review request authorises inspection and checks; change product code only when the user requests fixes.

## Establish evidence and scope

- Read applicable repository instructions, `git status`, `build.gradle`, and the relevant source, tests, and guides. Preserve existing user changes.
- Establish the contributor and review range from the request and repository evidence. `docs/AboutUs.md` currently associates Kia Hao with `neokh719`; treat that as a candidate identity, not proof of every Git alias. Inspect `git log`, author emails, co-authorship, diffs, and relevant PRs where available. Ask only if ambiguity changes attribution; continue reviewing identified changes meanwhile.
- For a change review, include staged, unstaged, and relevant untracked files. For a whole-contribution audit, inspect historical contributions still present in the product, not just the latest diff. State the base/ref and files reviewed. Do not assume a branch or remote exists.
- Distinguish user-authored logic, inherited AB3 code, teammates' work, generated code, and shared work. Blame and line counts are clues, not sole proof of ownership or value. Trace moved/refactored code when attribution matters. Do not claim credit for inherited logging or assertions merely because the file was edited.
- Consult the linked course sources for current rules during a grading audit. Baseline verified on 2026-10-20; identify conflicts with the user's supplied extract or unavailable pages rather than silently replacing rules. Treat inaccessible GitHub/peer evidence as unknown.

## 2A: Code quality

Inspect the contributor's functional code and tests manually; successful Checkstyle alone is insufficient. For each criterion, record evidence with file/line links and classify it as evidenced, gap, unknown, or justified not applicable at the reviewed scope.

| Criterion | What to inspect |
| --- | --- |
| Logging | Useful diagnostic events, appropriate levels and context, consistent use of existing logging facilities; flag noisy or sensitive output. |
| Exceptions | Specific failures, useful messages, sensible propagation/recovery; avoid swallowed failures and using exceptions as normal branching. |
| Assertions | Meaningful, side-effect-free internal invariants in functional code. JUnit assertions are test evidence, not a substitute. External-input validation must work with Java assertions disabled. |
| Defensive coding | Boundary validation, preservation of invariants and state on failure, protection against unwanted mutation. Prefer checks at appropriate boundaries over redundant checks everywhere. |
| Java standard | Manually check boolean-sounding names, collection plurals, meaningful naming, scope, formatting, imports, and documentation against SE-EDU rules. |
| SLAP | Each method has a coherent abstraction level; inspect mixed orchestration/details, excessive nesting, and long methods. Explain readability problems rather than inventing a course method-length limit. |
| Duplication | Compare production and test blocks that differ only in values or small details. Suggest focused helpers or parameterisation without hiding test intent or merging unrelated concepts. |
| Other course quality guidance | Check magic values, complex expressions, misleading comments, dead code, unnecessary cleverness, coupling, and clarity of the happy path. Propose the smallest useful refactoring. |

Track logging, exceptions, assertions, and defensive coding separately across the claimed contribution. If evidence is absent, identify a meaningful opportunity and its rationale; do not scatter ceremonial statements into every method to tick boxes. Distinguish concrete rubric risks from optional preferences.

## 2B: Effort and value

Assess product value first, then attribution. Summarise delivered behaviours, integration, and ownership with commits/PRs and tests. The effort baseline requires a team assessment above 5 and agreement that the contributor performed approximately an equal implementation share. Local Git data cannot establish either evaluator judgment.

Use the course's half-iP-effort comparison as primary context; 500–600 functional lines per person is only a rough secondary guide, excluding tests and documentation. Do not impose a line quota, reward padding or commit counts, or recommend extra features after sufficient scope merely for marks. Prioritise quality once effort is adequate.

Check that enhancements serve BakeBase's home-bakery users and have team agreement. Do not infer agreement from code presence. Clearly separate functional implementation from testing, documentation, reviews, and coordination when presenting evidence.

## Related expectations and verification

- Check meaningful automated tests for the contributor's enhancement, including appropriate boundaries, invalid inputs, failure state, and regressions. There is no mandatory course coverage percentage. Check feature-specific UG and DG updates for consistency with actual behaviour.
- Identify evidence of team tasks, assigned responsibilities, and reviews of others' work. Prefer feature ownership spanning implementation, tests, and documentation; flag missing evidence without claiming these activities substitute for functional code.
- Re-read `build.gradle` before choosing commands. Current project uses Java 25, JUnit 5, Checkstyle, and JaCoCo. On Windows use `.\gradlew.bat`. Run `checkstyleMain checkstyleTest` and relevant `test --tests <test-class-or-pattern>` checks; use the full `test` suite when reviewing the whole contribution or cross-cutting fixes. Use `coverage` only when coverage evidence helps assess gaps. Refer to `docs/Testing.md` for applicable manual checks and `docs/Logging.md` for logging conventions.
- Report commands, results, and scope. If Java/dependencies/network prevent checks, state the limitation; do not report a pass or change build settings to conceal it. Existing failures outside the contribution need separate attribution.

## Deliver the review

Lead with the most consequential evidenced mark risks. Give each finding its criterion, concrete file/line evidence or missing artifact, impact, and smallest actionable improvement. Separate confirmed defects from missing evidence and optional polish. Include a compact criterion/evidence/status table, actual verification results, and a prioritised next-action list. Explain which effort judgments require team/tutor evidence. If no issues are found, say what was checked and what remains unknown; never certify full marks.

## Authoritative sources

- [tP expectations](https://nus-cs2103-ay2627-s1.github.io/website/admin/tp-expectations.html): contribution scope, effort context, testing, documentation, teamwork.
- [tP grading](https://nus-cs2103-ay2627-s1.github.io/website/admin/tp-grading.html): implementation section 2A/2B and related assessment context.
- [Error handling](https://nus-cs2103-ay2627-s1.github.io/website/se-book-adapted/chapters/errorHandling.html): logging, exceptions, assertions, defensive programming.
- [SE-EDU Java standard](https://se-education.org/guides/conventions/java/intermediate.html): manual review alongside `config/checkstyle/checkstyle.xml`.
- [SLAP](https://nus-cs2103-ay2627-s1.github.io/website/se-book-adapted/chapters/codeQuality.html#slap-hard) and [course quality guidelines](https://nus-cs2103-ay2627-s1.github.io/website/book/codeQuality/): readability and maintainability review.
