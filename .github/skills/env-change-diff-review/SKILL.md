---
name: env-change-diff-review
description: 'Compare a feature branch against main to identify environment creation changes. Use for branch review, release checks, and infra/config impact analysis.'
argument-hint: 'Feature branch name and optional env keywords (example: feature/new-env-setup, env|docker|compose|terraform)'
user-invocable: true
---

# Environment Change Diff Review

## What This Skill Produces
- A focused change report for environment creation work between `main` and a feature branch.
- A categorized list of changed files (infrastructure, config, runtime, scripts, docs).
- A risk summary and follow-up checks before merge.

## When To Use
- You want to verify everything related to new environment setup in a feature branch.
- You need a pre-merge audit for dev/stage/prod environment creation changes.
- You need to isolate environment-specific changes from unrelated code edits.

## Inputs
- Feature branch name (or `HEAD` if already checked out).
- Baseline branch (default: `main`).
- Optional keyword filter for env-related terms.

## Procedure
1. Confirm repo state and current branch.
   - `git status --short`
   - `git branch --show-current`
2. Refresh baseline and validate branch existence.
   - `git fetch --all --prune`
   - `git show-ref --verify refs/heads/main` (or `refs/remotes/origin/main`)
3. Compute the compare range from merge-base.
   - `git merge-base main <feature-branch>`
   - Use range: `<merge-base>..<feature-branch>`
4. Get high-level summary and file list.
   - `git diff --stat <range>`
   - `git diff --name-status <range>`
5. Filter for environment creation change candidates.
   - Path filters to prioritize:
     - `Dockerfile`, `docker-compose*.yml`, `.env*`, `k8s/`, `helm/`, `terraform/`, `.github/workflows/`
     - `application*.yml`, `application*.properties`, provisioning scripts, startup scripts
   - Content keyword scan:
     - `environment|env|docker|compose|k8s|kubernetes|helm|terraform|provision|secret|vault|connection|profile`
6. Deep-review only filtered files.
   - `git diff <range> -- <path>` for each candidate file.
   - Note creation vs modification vs deletion.
7. Create the final report with categories.
   - Infrastructure and provisioning
   - Service configuration and profiles
   - Secrets and credential handling
   - CI/CD and deployment pipeline
   - Documentation and runbooks

## Decision Points
- If `main` is missing locally:
  - Compare against `origin/main`.
- If the feature branch has merge commits:
  - Keep merge-base strategy; do not compare raw `main..feature` blindly.
- If no env-related files are detected:
  - Run keyword search on full diff before concluding no relevant changes.
- If secrets appear added in plain text:
  - Raise a blocker and recommend rotation and secret-store migration.

## Completion Checks
- Compare range is based on merge-base and is explicitly documented.
- All env-related files are listed with change type (`A/M/D/R`).
- Each file has a short impact note.
- Risk items are marked as `blocker`, `high`, `medium`, or `low`.
- Report includes unresolved questions for the author.

## Output Template
Use this compact format:

```markdown
Branch Compare: `<feature-branch>` vs `main`
Range: `<merge-base>..<feature-branch>`

Changed Files (Env-Related)
- `<path>` (`A|M|D|R`) - `<what changed>` - Risk: `<level>`

Key Findings
- `<finding 1>`
- `<finding 2>`

Open Questions
- `<question 1>`

Merge Readiness
- Status: `<ready|needs changes|blocked>`
- Required Actions:
  - `<action 1>`
```

## Notes
- Prefer non-destructive git commands only.
- Keep review scoped to environment creation concerns first, then list unrelated deltas separately.
