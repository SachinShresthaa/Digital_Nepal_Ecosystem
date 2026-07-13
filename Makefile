.PHONY: help build test clean docker-build docker-up docker-down deploy health-check backup logs

# ============================================================================
# VARIABLES
# ============================================================================
PROJECT_NAME := digital-nepal-ecosystem
ENVIRONMENT ?= dev
VERSION := 1.0.0
DOCKER_REGISTRY ?= localhost
DOCKER_IMAGE := $(DOCKER_REGISTRY)/$(PROJECT_NAME)

#Natively defined docker compose v2 binary
DOCKER_COMPOSE := docker compose

# Colors
BLUE := \033[0;34m
GREEN := \033[0;32m
YELLOW := \033[1;33m
RED := \033[0;31m
NC := \033[0m

# ============================================================================
# HELP
# ============================================================================
help:
	@echo "$(BLUE)Digital Nepal Ecosystem - Make Commands$(NC)"
	@echo ""
	@echo "$(GREEN)Build & Test:$(NC)"
	@echo "  make build              Build the project with Maven"
	@echo "  make test               Run unit tests"
	@echo "  make test-integration   Run integration tests"
	@echo "  make clean              Clean build artifacts"
	@echo "  make format             Format code"
	@echo ""
	@echo "$(GREEN)Docker:$(NC)"
	@echo "  make docker-build       Build Docker image"
	@echo "  make docker-up          Start services with docker-compose"
	@echo "  make docker-down        Stop services"
	@echo "  make docker-logs        View Docker logs"
	@echo "  make docker-clean       Remove containers and volumes"
	@echo ""
	@echo "$(GREEN)Database:$(NC)"
	@echo "  make db-migrate         Run database migrations"
	@echo "  make db-reset           Reset database"
	@echo ""
	@echo "$(GREEN)Operations:$(NC)"
	@echo "  make health-check       Run health checks"
	@echo "  make backup             Create database backup"
	@echo "  make logs               View application logs"
	@echo "  make ps                 Show running containers"
	@echo ""
	@echo "$(GREEN)Deployment:$(NC)"
	@echo "  make deploy-staging     Deploy to staging"
	@echo "  make deploy-production  Deploy to production"
	@echo ""

# ============================================================================
# BUILD & TEST
# ============================================================================
build:
	@echo "$(BLUE)Building project...$(NC)"
	@if [ -x ./mvnw ]; then \
		chmod +x ./mvnw || true; \
		./mvnw clean package -DskipTests; \
	else \
		mvn clean package -DskipTests; \
	fi

test:
	@echo "$(BLUE)Running unit tests...$(NC)"
	@if [ -x ./mvnw ]; then \
		chmod +x ./mvnw || true; \
		./mvnw test; \
	else \
		mvn test; \
	fi

test-integration:
	@echo "$(BLUE)Running integration tests...$(NC)"
	@if [ -x ./mvnw ]; then \
		chmod +x ./mvnw || true; \
		./mvnw verify; \
	else \
		mvn verify; \
	fi

clean:
	@echo "$(BLUE)Cleaning build artifacts...$(NC)"
	@if [ -x ./mvnw ]; then \
		chmod +x ./mvnw || true; \
		./mvnw clean; \
	else \
		mvn clean; \
	fi

format:
	@echo "$(BLUE)Formatting code...$(NC)"
	@if [ -x ./mvnw ]; then \
		chmod +x ./mvnw || true; \
		./mvnw spotless:apply; \
	else \
		mvn spotless:apply; \
	fi

# ============================================================================
# DOCKER
# ============================================================================
docker-build:
	@echo "$(BLUE)Building Docker image: $(DOCKER_IMAGE):$(VERSION)$(NC)"
	@docker build -t $(DOCKER_IMAGE):$(VERSION) \
	              -t $(DOCKER_IMAGE):latest .

docker-up:
	@echo "$(BLUE)Starting Docker containers...$(NC)"
	@$(DOCKER_COMPOSE) up -d
	@echo "$(GREEN)✓ Services started$(NC)"
	@make health-check

docker-down:
	@echo "$(BLUE)Stopping Docker containers...$(NC)"
	@$(DOCKER_COMPOSE) down

docker-logs:
	@$(DOCKER_COMPOSE) logs -f

docker-clean:
	@echo "$(BLUE)Removing containers and volumes...$(NC)"
	@$(DOCKER_COMPOSE) down -v
	@echo "$(GREEN)✓ Cleanup complete$(NC)"

# ============================================================================
# DATABASE
# ============================================================================
db-migrate:
	@echo "$(BLUE)Running database migrations...$(NC)"
	@$(DOCKER_COMPOSE) exec -T postgres psql -U admin -d digital_nepal \
	  -f /docker-entrypoint-initdb.d/001_init_schema.sql

db-reset:
	@echo "$(RED)Resetting database...$(NC)"
	@read -p "Are you sure? (yes/no) " -r; \
	if [ $$REPLY = "yes" ]; then \
	  $(DOCKER_COMPOSE) exec -T postgres dropdb -U admin digital_nepal; \
	  $(DOCKER_COMPOSE) exec -T postgres createdb -U admin digital_nepal; \
	  make db-migrate; \
	fi

# ============================================================================
# OPERATIONS
# ============================================================================
health-check:
	@echo "$(BLUE)Running health checks...$(NC)"
	@bash ./scripts/health-check.sh

backup:
	@echo "$(BLUE)Creating backup...$(NC)"
	@bash ./scripts/backup.sh

logs:
	@$(DOCKER_COMPOSE) logs -f backend

ps:
	@$(DOCKER_COMPOSE) ps

# ============================================================================
# DEPLOYMENT
# ============================================================================
deploy-staging:
	@echo "$(BLUE)Deploying to staging...$(NC)"
	@bash ./scripts/deploy.sh staging

deploy-production:
	@echo "$(RED)Deploying to production...$(NC)"
	@bash ./scripts/deploy.sh production

# ============================================================================
# DEVELOPMENT
# ============================================================================
run-local:
	@echo "$(BLUE)Running application locally...$(NC)"
	@if [ -x ./mvnw ]; then \
		chmod +x ./mvnw || true; \
		./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"; \
	else \
		mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"; \
	fi

shell-db:
	@echo "$(BLUE)Connecting to database shell...$(NC)"
	@$(DOCKER_COMPOSE) exec postgres psql -U admin -d digital_nepal

install-runner:
	@echo "$(BLUE)GitHub Actions uses hosted runners; no local runner install is required.$(NC)"
	@bash ./scripts/setup-runner.sh

# ============================================================================
# CI/CD (Local testing)
# ============================================================================
ci-test:
	@echo "$(BLUE)Running CI pipeline locally...$(NC)"
	@make clean
	@make build
	@make test
	@make docker-build
	@echo "$(GREEN)✓ CI pipeline passed$(NC)"

# ============================================================================
# DEFAULT
# ============================================================================
.DEFAULT_GOAL := help
