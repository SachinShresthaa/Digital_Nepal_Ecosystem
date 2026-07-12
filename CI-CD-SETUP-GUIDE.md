# CI/CD Setup Guide

This repository uses GitHub Actions for CI/CD.
The workflow is defined in `.github/workflows/ci.yml`.

## Pipeline structure

- `build-backend` - build Maven project and produce the JAR
- `test-backend` - run unit and integration tests
- `docker-build` - build and optionally push Docker image
- `deploy-staging` - manual staging deploy placeholder

## GitHub Secrets

Configure the following secrets in the GitHub repository settings:

- `GITHUB_TOKEN` (automatically available)
- `GHCR_PAT` or use `GITHUB_TOKEN` for GHCR push
- `STAGING_SERVER_HOST`
- `PRODUCTION_SERVER_HOST`
- `DEPLOY_SSH_PRIVATE_KEY`
- `DB_PASSWORD`

## Setting up GitHub Actions

1. Ensure `.github/workflows/ci.yml` exists in the repository.
2. Commit and push changes to GitHub.
3. Open the `Actions` tab to verify the workflow runs.

## Running the workflow

- Push any branch to trigger the build and test jobs.
- Open a pull request against `dev` or `main` to validate changes.
- For Docker image builds, use `dev` or `main` branches.

## Docker image publishing

The workflow tags images using the current commit SHA and pushes to `ghcr.io/${{ github.repository_owner }}/digital-nepal-backend`.

## Notes

- GitHub-hosted runners are available by default, so no manual runner install is required.
- If you need self-hosted runners, configure them in GitHub repository settings instead of using GitLab Runner.
