#!/bin/bash

# CMS Backend - Manual Database Seeding Script
# This script populates the database with sample blog posts, users, and tags.
# Run this AFTER the database is up and migrations have been applied.

set -e

echo "🌱 Starting database seeding..."

# Database connection details (modify if needed)
DB_HOST="${DB_HOST:-localhost}"
DB_PORT="${DB_PORT:-5433}"
DB_NAME="${DB_NAME:-cms_db}"
DB_USER="${DB_USER:-cms_user}"
DB_PASSWORD="${DB_PASSWORD:-cms_password}"

# Check if PostgreSQL client is installed
if ! command -v psql &> /dev/null; then
    echo "❌ Error: psql (PostgreSQL client) is not installed."
    echo "Install it with: brew install postgresql (macOS)"
    exit 1
fi

# Check if database is accessible
echo "🔍 Checking database connection..."
export PGPASSWORD="$DB_PASSWORD"
if ! psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" -c '\q' 2>/dev/null; then
    echo "❌ Error: Cannot connect to database."
    echo "Make sure Docker containers are running: docker compose up -d"
    exit 1
fi

echo "✅ Database connection successful"

# Run the seed SQL file
echo "🌱 Seeding database with sample data..."
psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" -f src/main/resources/db/seed_data.sql

echo ""
echo "✅ Database seeded successfully!"
echo ""
echo "📊 Seeded data includes:"
echo "   - 5 users (admin@cms.com, jane.doe@cms.com, etc.)"
echo "   - 15 tags (React, TypeScript, Docker, etc.)"
echo "   - 7 blog posts with full MDX content"
echo ""
echo "🔑 Default password for all users: password123"
echo ""
echo "🚀 You can now access the API at http://localhost:9090/api/v1"
echo ""

