# Contributing to Digital Nepal Ecosystem

Thank you for contributing! This project is public and welcomes collaborators.

## Branch strategy

- `main` is the protected release branch.
- `develop` is the shared integration branch for active development.
- Create feature branches from `develop`: `feature/<name>`, `bugfix/<name>`, `hotfix/<name>`.
- Do not push directly to `main` or `develop`.

## Always pull before push

Before you start work:

```bash
git checkout develop
git pull origin develop
git checkout -b feature/<short-description>
```

Before you push:

```bash
git add .
git commit -m "<short summary>"
git pull --rebase origin develop
git push origin feature/<short-description>
```

## Pull request workflow

- Open PRs against `develop` for routine work.
- Open PRs against `main` only for hotfixes or release updates from `develop`.
- Use the `.github/PULL_REQUEST_TEMPLATE.md`.
- Do not approve your own PR.
- Request at least one approving review from another collaborator.

## Review policy

- Each PR should have at least one approval before merge.
- Fix review comments by updating the branch and pushing again.
- Keep changes small and focused.
- Add a description of what changed and how it was tested.

## GitHub Actions

- CI is enabled for PRs to `develop` and `main`.
- Builds also run on push to `develop` and `main`.
- The workflow publishes a Docker image when the branch is `develop` or `main`.

## Local development

- Use `.env.example` as a template; do not commit secrets.
- Run unit tests with:

```bash
./mvnw test
```

- Build locally with:

```bash
./mvnw clean package -DskipTests
```

## Public collaboration

- Keep the repository public-friendly:
  - add clear descriptions
  - avoid secret data in commits
  - create clean PRs
- This project is open to contributors and maintains a standard review process.
