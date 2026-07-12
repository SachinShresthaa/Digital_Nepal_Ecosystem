# GitHub Actions CI/CD Overview

This repository now uses GitHub Actions for CI/CD.

## Workflow file
- `.github/workflows/ci.yml`

## Workflow stages
- Build: Maven package
- Test: Unit and integration tests
- Docker: Build and push Docker image
- Deploy: Manual staging deploy placeholder

## Working with the workflow

1. Push a branch to GitHub.
2. Open the `Actions` tab.
3. Select the latest workflow run.
4. Inspect job logs and artifacts.

## Notes

- The GitHub Actions workflow runs on GitHub-hosted runners by default.
- Use GitHub Secrets for registry credentials and deployment keys.
