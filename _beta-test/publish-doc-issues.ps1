$ErrorActionPreference = 'Stop'
$betaIssueRoot = $PSScriptRoot
$issuePlan = Get-Content -LiteralPath (Join-Path $betaIssueRoot 'github-doc-issues.json') -Raw | ConvertFrom-Json
$createdIssues = @()
$encoding = [System.Text.UTF8Encoding]::new($false)

foreach ($plannedIssue in $issuePlan.create) {
    $request = @{title = $plannedIssue.title; body = $plannedIssue.body; milestone = $issuePlan.milestone}
    $requestFile = Join-Path $betaIssueRoot 'github-issue-request.json'
    [System.IO.File]::WriteAllText($requestFile, ($request | ConvertTo-Json -Depth 5), $encoding)
    $response = gh api --method POST "repos/$($issuePlan.repository)/issues" --input $requestFile
    if ($LASTEXITCODE -ne 0) { throw "Issue creation failed: $($plannedIssue.title)" }
    $createdIssue = $response | ConvertFrom-Json
    $createdIssues += [pscustomobject]@{number=$createdIssue.number; title=$createdIssue.title; url=$createdIssue.html_url}
    [System.IO.File]::WriteAllText((Join-Path $betaIssueRoot 'github-created-issues.json'),
        (ConvertTo-Json -InputObject $createdIssues -Depth 5), $encoding)
    Write-Output "Created #$($createdIssue.number): $($createdIssue.title)"
}

$currentResponse = gh api "repos/$($issuePlan.repository)/issues/$($issuePlan.update.number)"
if ($LASTEXITCODE -ne 0) { throw 'Could not read existing diagram issue before updating it' }
$currentIssue = $currentResponse | ConvertFrom-Json
$sectionMarker = '## Additional v1.5 beta-test evidence'
$sectionOffset = $issuePlan.update.body.IndexOf($sectionMarker)
$newBody = $currentIssue.body
if (-not $newBody.Contains($sectionMarker)) {
    $newBody += "`n`n" + $issuePlan.update.body.Substring($sectionOffset)
}
$requestFile = Join-Path $betaIssueRoot 'github-diagram-update-request.json'
[System.IO.File]::WriteAllText($requestFile,
    (@{body=$newBody; milestone=$issuePlan.milestone} | ConvertTo-Json -Depth 5), $encoding)
$response = gh api --method PATCH "repos/$($issuePlan.repository)/issues/$($issuePlan.update.number)" --input $requestFile
if ($LASTEXITCODE -ne 0) { throw 'Could not update diagram issue' }
$updatedIssue = $response | ConvertFrom-Json
$allIssues = @($createdIssues) + @([pscustomobject]@{number=$updatedIssue.number;title=$updatedIssue.title;url=$updatedIssue.html_url})

foreach ($trackedIssue in $allIssues) {
    $verifiedResponse = gh api "repos/$($issuePlan.repository)/issues/$($trackedIssue.number)"
    if ($LASTEXITCODE -ne 0) { throw "Could not verify #$($trackedIssue.number)" }
    $verifiedIssue = $verifiedResponse | ConvertFrom-Json
    if ($verifiedIssue.milestone.number -ne $issuePlan.milestone) {
        throw "Incorrect milestone on #$($trackedIssue.number)"
    }
    Write-Output "Verified #$($verifiedIssue.number) [$($verifiedIssue.milestone.title)]: $($verifiedIssue.html_url)"
}
