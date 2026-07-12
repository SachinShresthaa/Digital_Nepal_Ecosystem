# Quick Start - GitHub Actions

## What changed
This repository now uses GitHub Actions for CI/CD instead of GitLab CI.

## Getting started

### 1. Clone the repository
```bash
git clone <repo-url>
cd digital-nepal-ecosystem
cp .env.example .env
# Edit .env with your configuration
```

### 2. Start the application
```bash
make docker-up
```

### 3. Verify the system
```bash
make health-check
make logs
make shell-db
```

## CI/CD with GitHub Actions

- Push to any branch to trigger the workflow
- Open a pull request to `dev` or `main` to run validation
- View workflow status in the repository `Actions` tab

## Docker image publishing

The Docker image is built in GitHub Actions and can be pushed to GitHub Container Registry (`ghcr.io`).

## Notes

- The GitHub Actions workflow lives in `.github/workflows/ci.yml`
- No local runner installation is required for GitHub-hosted workflows
- If you use self-hosted runners, configure them in GitHub repository settings
