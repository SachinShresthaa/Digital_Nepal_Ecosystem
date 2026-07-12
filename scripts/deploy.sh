#!/bin/bash
# Digital Nepal Ecosystem - Deployment Script
# Deploys application to staging or production environment

set -e

# ============================================================================
# CONFIGURATION
# ============================================================================
ENVIRONMENT=${1:-staging}
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

# Color codes
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# ============================================================================
# FUNCTIONS
# ============================================================================

log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_usage() {
    echo "Usage: $0 {staging|production}"
    echo "Example: $0 production"
    exit 1
}

validate_environment() {
    if [ "$ENVIRONMENT" != "staging" ] && [ "$ENVIRONMENT" != "production" ]; then
        log_error "Invalid environment. Use 'staging' or 'production'"
        print_usage
    fi
}

check_dependencies() {
    log_info "Checking dependencies..."
    
    for cmd in docker docker-compose ssh git; do
        if ! command -v "$cmd" &> /dev/null; then
            log_error "$cmd is not installed"
            exit 1
        fi
    done
    
    log_info "All dependencies found ✓"
}

build_docker_image() {
    log_info "Building Docker image for $ENVIRONMENT..."
    
    cd "$PROJECT_DIR"
    docker build -t digital-nepal:$ENVIRONMENT .
    
    log_info "Docker build completed ✓"
}

backup_database() {
    log_info "Creating database backup..."
    
    BACKUP_DIR="/opt/digital-nepal/backups"
    BACKUP_FILE="backup-$(date +%Y%m%d_%H%M%S).sql.gz"
    
    docker-compose exec -T postgres pg_dump -U $DB_USER $DB_NAME | \
        gzip > "$PROJECT_DIR/$BACKUP_FILE"
    
    log_info "Database backup saved to $BACKUP_FILE"
}

deploy_to_environment() {
    log_info "Deploying to $ENVIRONMENT environment..."
    
    case "$ENVIRONMENT" in
        staging)
            DEPLOY_HOST=$STAGING_SERVER_HOST
            DEPLOY_PORT=22
            DEPLOY_USER="deploy"
            ;;
        production)
            DEPLOY_HOST=$PRODUCTION_SERVER_HOST
            DEPLOY_PORT=22
            DEPLOY_USER="deploy"
            read -p "You are about to deploy to PRODUCTION. Are you sure? (yes/no) " -r
            if [[ ! $REPLY =~ ^[Yy][Ee][Ss]$ ]]; then
                log_error "Deployment cancelled"
                exit 1
            fi
            ;;
    esac
    
    if [ -z "$DEPLOY_HOST" ]; then
        log_error "Deployment host not configured"
        exit 1
    fi
    
    # Connect and deploy
    ssh -p $DEPLOY_PORT $DEPLOY_USER@$DEPLOY_HOST << 'EOF'
        cd /opt/digital-nepal
        git fetch origin
        git checkout dev
        docker-compose pull
        docker-compose down
        docker-compose up -d
        
        # Wait for services to be healthy
        sleep 10
        
        # Run health check
        if ! curl -f http://localhost:8080/actuator/health; then
            echo "Health check failed!"
            exit 1
        fi
        
        echo "Deployment successful!"
EOF
    
    log_info "Deployment completed ✓"
}

health_check() {
    log_info "Running health checks..."
    
    # Check if containers are running
    if ! docker-compose ps | grep -q "Up"; then
        log_error "Services are not running"
        exit 1
    fi
    
    # Check application health
    HEALTH=$(curl -s http://localhost:8080/actuator/health | grep -o '"status":"[^"]*"')
    if [[ $HEALTH == *"UP"* ]]; then
        log_info "Health check passed ✓"
    else
        log_error "Health check failed: $HEALTH"
        exit 1
    fi
}

# ============================================================================
# MAIN
# ============================================================================

main() {
    log_info "Digital Nepal Deployment Script"
    log_info "Environment: $ENVIRONMENT"
    
    validate_environment
    check_dependencies
    build_docker_image
    backup_database
    deploy_to_environment
    health_check
    
    log_info "Deployment pipeline completed successfully!"
}

main "$@"
