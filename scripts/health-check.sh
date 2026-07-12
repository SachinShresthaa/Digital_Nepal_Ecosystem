#!/bin/bash
# Digital Nepal Ecosystem - Health Check Script
# Monitors application and database health

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

# Configuration
APP_URL="http://localhost:8080"
DB_HOST="${DB_HOST:-localhost}"
DB_PORT="${DB_PORT:-5432}"
DB_NAME="${DB_NAME:-digital_nepal}"
DB_USER="${DB_USER:-admin}"
CHECK_INTERVAL=30
MAX_RETRIES=3

#Added DB Container
DB_CONTAINER_NAME="${DB_CONTAINER_NAME:-digital-nepal-db}"

log_info() {
    echo -e "${GREEN}[✓]${NC} $1"
}

log_error() {
    echo -e "${RED}[✗]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[!]${NC} $1"
}

check_application() {
    echo "Checking Application Health..."
    
    for i in $(seq 1 $MAX_RETRIES); do
        if curl -sf "$APP_URL/actuator/health" > /dev/null 2>&1; then
            HEALTH=$(curl -s "$APP_URL/actuator/health" | grep -o '"status":"[^"]*"')
            log_info "Application is UP ($HEALTH)"
            return 0
        fi
        
        if [ $i -lt $MAX_RETRIES ]; then
            log_warning "Retry attempt $i/$MAX_RETRIES in ${CHECK_INTERVAL}s..."
            sleep $CHECK_INTERVAL
        fi
    done
    
    log_error "Application health check failed"
    return 1
}

check_database() {
    echo "Checking Database Health..."
    
    for i in $(seq 1 $MAX_RETRIES); do
        # APPROACH A: The Software Engineer Method (Uses host psql client if installed)
        if command -v psql > /dev/null 2>&1; then
            if PGPASSWORD="$DB_PASSWORD" psql -h "$DB_HOST" -p "$DB_PORT" \
                -U "$DB_USER" -d "$DB_NAME" -c "SELECT 1" > /dev/null 2>&1; then
                
                # Check PostGIS
                if PGPASSWORD="$DB_PASSWORD" psql -h "$DB_HOST" -p "$DB_PORT" \
                    -U "$DB_USER" -d "$DB_NAME" -c "SELECT PostGIS_version()" > /dev/null 2>&1; then
                    log_info "Database is UP with PostGIS (Verified via local network)"
                    return 0
                else
                    log_warning "Database is UP but PostGIS not available (Verified via local network)"
                    return 0
                fi
            fi
        # APPROACH B: The DevOps Fallback Method (Runs directly inside container if host psql is missing)
        else
            if docker exec "$DB_CONTAINER_NAME" pg_isready -U "$DOCKER_DB_USER" > /dev/null 2>&1; then
                # Check PostGIS inside container
                if docker exec "$DB_CONTAINER_NAME" psql -U "$DOCKER_DB_USER" -d "$DB_NAME" -c "SELECT PostGIS_version()" > /dev/null 2>&1; then
                    log_info "Database is UP with PostGIS (Verified via Docker runtime environment)"
                    return 0
                else
                    log_warning "Database is UP but PostGIS not available (Verified via Docker runtime environment)"
                    return 0
                fi
            fi
        fi
        
        if [ $i -lt $MAX_RETRIES ]; then
            log_warning "Retry attempt $i/$MAX_RETRIES in ${CHECK_INTERVAL}s..."
            sleep $CHECK_INTERVAL
        fi
    done
    
    log_error "Database health check failed"
    return 1
}

check_disk_space() {
    echo "Checking Disk Space..."
    
    USAGE=$(df / | awk 'NR==2 {print $5}' | sed 's/%//')
    THRESHOLD=80
    
    if [ "$USAGE" -gt "$THRESHOLD" ]; then
        log_error "Disk usage is ${USAGE}% (threshold: ${THRESHOLD}%)"
        return 1
    else
        log_info "Disk usage is ${USAGE}%"
        return 0
    fi
}

check_memory() {
    echo "Checking Memory..."
    
    USAGE=$(free | awk 'NR==2{printf("%.0f", $3/$2 * 100.0)}')
    THRESHOLD=85
    
    if [ "$USAGE" -gt "$THRESHOLD" ]; then
        log_error "Memory usage is ${USAGE}% (threshold: ${THRESHOLD}%)"
        return 1
    else
        log_info "Memory usage is ${USAGE}%"
        return 0
    fi
}

check_docker_containers() {
    echo "Checking Docker Containers..."
    
    RUNNING=$(docker compose ps | grep -c "Up" || true)
    EXPECTED=2  # postgres + backend
    
    if [ "$RUNNING" -ge "$EXPECTED" ]; then
        log_info "All $RUNNING containers are running"
        docker compose ps
        return 0
    else
        log_error "Only $RUNNING/$EXPECTED containers are running"
        return 1
    fi
}

# Main execution
main() {
    echo "================================"
    echo "Digital Nepal Health Check"
    echo "Time: $(date)"
    echo "================================"
    
    FAILED=0
    
    check_application || ((FAILED++))
    echo ""
    
    check_database || ((FAILED++))
    echo ""
    
    check_docker_containers || ((FAILED++))
    echo ""
    
    check_disk_space || ((FAILED++))
    echo ""
    
    check_memory || ((FAILED++))
    echo ""
    
    echo "================================"
    if [ $FAILED -eq 0 ]; then
        log_info "All health checks passed!"
        exit 0
    else
        log_error "$FAILED health check(s) failed"
        exit 1
    fi
}

main "$@"
