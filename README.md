# Digital Nepal Citizen Ecosystem

A public backend platform for citizen services in Nepal built with Java 21, Spring Boot, PostgreSQL, and PostGIS.

## Project overview

This repository contains the backend services and DevOps configuration for the Digital Nepal ecosystem.

### Key components

- `modules/stub-backend` - the runnable Spring Boot application used by CI and Docker
- `db/migrations` - database schema and migration scripts
- `.github/workflows/ci.yml` - GitHub Actions CI/CD pipeline
- `docker-compose.yml` - local development orchestration
- `scripts/` - helper scripts for deployment, backup, and health checks
- `CONTRIBUTING.md` - collaboration guidelines
- `.github/PULL_REQUEST_TEMPLATE.md` - PR template for consistent review

## Getting started

### Prerequisites

- Java 21
- Maven 3.9+
- Docker 24+
- Docker Compose 3.8+
- Git

### Local startup

```bash
git clone https://github.com/intersectinfodevelopers/Digital_Nepal_Ecosystem.git
cd Digital_Nepal_Ecosystem
cp .env.example .env
./mvnw clean package -DskipTests
docker compose up -d
```

### Health check

```bash
make health-check
docker compose logs -f
```

## Collaboration guide

- `main` is the release branch.
- `develop` is the active collaboration branch.
- Create feature branches from `develop`.
- Always pull before push.
- Open PRs against `develop` for routine work.
- Use the PR template and request at least one review.

## CI/CD

GitHub Actions validates PRs and builds the application. The workflow runs on push and pull requests targeting `main`, `develop`, and `dev`.

## Public repository practices

- Do not commit secrets.
- Use `.env.example` for local configuration.
- Keep PRs small, descriptive, and reviewed.

## License

This project is public and currently maintained by Intersect Info Developers.
