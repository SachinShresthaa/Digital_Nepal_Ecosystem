# Digital Nepal Citizen Ecosystem - Backend Infrastructure

A comprehensive **Java 21 + Spring Boot 3.x** backend system for managing citizen services across Nepal with PostGIS geographic support.

## 📋 Project Structure

```
digital-nepal-ecosystem/
├── .github/workflows/ci.yml    # GitHub Actions workflow
├── Dockerfile                  # Multi-stage Java build
├── docker-compose.yml          # Services orchestration
├── pom.xml                     # Maven parent configuration
├── Makefile                    # Development commands
├── .env.example                # Environment template
├── .dockerignore               # Docker build exclusions
│
├── modules/                    # Maven modules
│   ├── auth/                   # Authentication & Authorization
│   ├── citizen-registry/       # Citizen management + PostGIS
│   ├── employment/             # Employment records
│   ├── health/                 # Health records
│   ├── education/              # Academic records
│   ├── id-management/          # National ID verification
│   └── reporting/              # Analytics & Reports
│   └── stub-backend/           # Main runnable Spring Boot application (used for Docker/CI)
│
├── db/                         # Database configuration
│   └── migrations/
│       └── 001_init_schema.sql # PostgreSQL + PostGIS schema
│
├── scripts/                    # DevOps utilities
│   ├── deploy.sh               # Deployment script
│   ├── health-check.sh         # Health monitoring
│   ├── backup.sh               # Database backup (encrypted)
│   └── setup-runner.sh         # Legacy helper (GitHub Actions uses hosted runners)
│
├── src/
│   └── main/resources/
│       └── application.yml     # Spring Boot configuration
│
└── documentation/
    ├── CI-CD-SETUP-GUIDE.md    # Complete setup guide
    ├── QUICK-START.md          # 6-step activation
    └── README-CICD.md          # CI/CD overview
```

## 🚀 Quick Start

### Prerequisites
- Docker 24+
- Docker Compose 3.8+
- Maven 3.9+
- Java 21+
- Git

### 1. Clone & Setup
```bash
# Clone repository
git clone <repo-url>
cd digital-nepal-ecosystem

# Copy environment
cp .env.example .env
# Edit .env with your configuration
```

### 2. Start Services
```bash
# Using make (recommended)
make docker-up

# Or using docker-compose directly
docker-compose up -d
```

### 3. Verify Installation
```bash
# Run health checks
make health-check

# View logs
make logs

# Connect to database
make shell-db
```

## 📦 Architecture

### Technology Stack

| Component | Version |
|-----------|---------|
| **Backend** | Java 21 + Spring Boot 3.2 |
| **Build** | Maven 3.9 |
| **Database** | PostgreSQL 16 + PostGIS |
| **Container** | Docker 24 |
| **Orchestration** | Docker Compose 3.8 |
| **CI/CD** | GitHub Actions |

### Modules

- **Auth Service**: User authentication, JWT, role-based access control
- **Citizen Registry**: Citizen profiles with geographic coordinates (PostGIS)
- **Employment**: Employment records and job history
- **Health**: Medical records and health history
- **Education**: Academic records and qualifications
- **ID Management**: National ID issuance and verification
- **Reporting**: Analytics, dashboards, and audit logs

Note: The canonical runnable Spring Boot module for Docker and CI is `modules/stub-backend` (it contains the `@SpringBootApplication` entrypoint). CI and the runtime image use the artifact produced by this module.

### Database Schema

```
citizen_registry.citizens          (PostGIS geographic data)
auth.users
auth.roles
auth.user_roles
employment.employment_records
health.health_records
```

## 🛠️ GitHub Actions CI/CD

The CI/CD workflow is defined in `.github/workflows/ci.yml`.

- `build-backend`: Maven package
- `test-backend`: Unit and integration tests
- `docker-build`: Docker image build and optional push to GitHub Container Registry
- `deploy-staging`: Manual placeholder deploy on `dev`

### Where to view results
Use the repository `Actions` tab in GitHub to inspect workflow runs, logs, and artifacts.

## 🚨 Troubleshooting

### Services won't start
```bash
docker-compose logs
make health-check
docker-compose restart
```

### Database connection error
```bash
docker-compose exec postgres psql -U admin -l
make db-reset
```

### Build failures
```bash
make clean
make build
java -version    # Must be 21+
```

### GitHub Actions issues
- Open the GitHub repository `Actions` tab
- Select the workflow run
- Review job logs and outputs

## 📚 Documentation

- **[CI-CD-SETUP-GUIDE.md](CI-CD-SETUP-GUIDE.md)** - Complete setup and configuration
- **[QUICK-START.md](QUICK-START.md)** - 6-step activation checklist
- **[README-CICD.md](README-CICD.md)** - CI/CD pipeline overview

## 🔗 Related Resources
- [Spring Boot 3.2 Documentation](https://spring.io/projects/spring-boot)
- [PostgreSQL 16 Docs](https://www.postgresql.org/docs/16/)
- [PostGIS Documentation](https://postgis.net/documentation/)
- [Docker Documentation](https://docs.docker.com/)
- [GitHub Actions Documentation](https://docs.github.com/en/actions/)

## 📝 License

Government of Nepal - Digital Nepal Initiative
