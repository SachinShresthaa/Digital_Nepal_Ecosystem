#!/bin/bash
# Digital Nepal Ecosystem - Backup Script
# Creates encrypted backups of database and application

set -e

# ============================================================================
# CONFIGURATION
# ============================================================================
BACKUP_DIR="${BACKUP_DIR:-/backups/digital-nepal}"
BACKUP_RETENTION_DAYS=30
DB_NAME="${DB_NAME:-digital_nepal}"
DB_USER="${DB_USER:-admin}"
DB_HOST="${DB_HOST:-postgres}"
GPG_RECIPIENT="${GPG_RECIPIENT_KEY:-your-gpg-key}"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

# ============================================================================
# BACKUP DATABASE
# ============================================================================

backup_database() {
    log_info "Starting database backup..."
    
    mkdir -p "$BACKUP_DIR"
    
    BACKUP_FILE="$BACKUP_DIR/db_backup_${TIMESTAMP}.sql"
    
    # Export database
    docker-compose exec -T postgres pg_dump \
        -U "$DB_USER" \
        -h "$DB_HOST" \
        "$DB_NAME" > "$BACKUP_FILE"
    
    if [ -f "$BACKUP_FILE" ]; then
        SIZE=$(du -h "$BACKUP_FILE" | cut -f1)
        log_info "Database backup created: $(basename $BACKUP_FILE) ($SIZE)"
        echo "$BACKUP_FILE"
    else
        log_error "Database backup failed"
        return 1
    fi
}

# ============================================================================
# BACKUP APPLICATION DATA
# ============================================================================

backup_application() {
    log_info "Starting application data backup..."
    
    BACKUP_FILE="$BACKUP_DIR/app_backup_${TIMESTAMP}.tar.gz"
    
    tar -czf "$BACKUP_FILE" \
        --exclude=target \
        --exclude=.git \
        --exclude=node_modules \
        --exclude=.env \
        .
    
    if [ -f "$BACKUP_FILE" ]; then
        SIZE=$(du -h "$BACKUP_FILE" | cut -f1)
        log_info "Application backup created: $(basename $BACKUP_FILE) ($SIZE)"
        echo "$BACKUP_FILE"
    else
        log_error "Application backup failed"
        return 1
    fi
}

# ============================================================================
# ENCRYPT BACKUP
# ============================================================================

encrypt_backup() {
    local BACKUP_FILE=$1
    
    log_info "Encrypting backup: $(basename $BACKUP_FILE)..."
    
    if command -v gpg &> /dev/null; then
        gpg --encrypt --recipient "$GPG_RECIPIENT" "$BACKUP_FILE"
        
        if [ -f "${BACKUP_FILE}.gpg" ]; then
            rm -f "$BACKUP_FILE"  # Remove unencrypted version
            log_info "Backup encrypted and unencrypted version removed"
            echo "${BACKUP_FILE}.gpg"
        else
            log_warning "GPG encryption failed, keeping unencrypted backup"
            echo "$BACKUP_FILE"
        fi
    else
        log_warning "GPG not installed, backup will not be encrypted"
        echo "$BACKUP_FILE"
    fi
}

# ============================================================================
# CLEANUP OLD BACKUPS
# ============================================================================

cleanup_old_backups() {
    log_info "Cleaning up backups older than $BACKUP_RETENTION_DAYS days..."
    
    find "$BACKUP_DIR" -type f -mtime +$BACKUP_RETENTION_DAYS -delete
    
    DELETED=$(find "$BACKUP_DIR" -type f -mtime +$BACKUP_RETENTION_DAYS | wc -l)
    if [ "$DELETED" -gt 0 ]; then
        log_info "Deleted $DELETED old backup files"
    fi
}

# ============================================================================
# VERIFY BACKUP
# ============================================================================

verify_backup() {
    local BACKUP_FILE=$1
    
    log_info "Verifying backup integrity..."
    
    if [[ "$BACKUP_FILE" == *.sql ]]; then
        # Check SQL file integrity
        if grep -q "PostgreSQL database dump" "$BACKUP_FILE"; then
            log_info "Database backup verification passed ✓"
            return 0
        fi
    elif [[ "$BACKUP_FILE" == *.tar.gz ]]; then
        # Check tar file integrity
        if tar -tzf "$BACKUP_FILE" > /dev/null 2>&1; then
            log_info "Application backup verification passed ✓"
            return 0
        fi
    fi
    
    log_error "Backup verification failed"
    return 1
}

# ============================================================================
# MAIN
# ============================================================================

main() {
    log_info "Digital Nepal Backup Script Started"
    log_info "Timestamp: $TIMESTAMP"
    
    # Create backups
    DB_BACKUP=$(backup_database)
    APP_BACKUP=$(backup_application)
    
    # Encrypt backups
    DB_BACKUP=$(encrypt_backup "$DB_BACKUP")
    APP_BACKUP=$(encrypt_backup "$APP_BACKUP")
    
    # Verify backups
    verify_backup "$DB_BACKUP"
    verify_backup "$APP_BACKUP"
    
    # Cleanup old backups
    cleanup_old_backups
    
    # Summary
    echo ""
    echo "================================"
    log_info "Backup Completed Successfully!"
    echo "================================"
    echo "Database Backup: $DB_BACKUP"
    echo "Application Backup: $APP_BACKUP"
    echo "Backup Location: $BACKUP_DIR"
    echo "Retention Period: $BACKUP_RETENTION_DAYS days"
    echo "================================"
}

main "$@"
