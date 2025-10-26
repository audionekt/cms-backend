#!/bin/bash

# CMS Backend - Database Seeding Script (Docker version)
# This script seeds the database by running SQL inside the Docker container
# No need to have psql installed locally!

set -e

echo "🌱 Starting database seeding (via Docker)..."

# Check if postgres container is running
if ! docker ps | grep -q cms-postgres; then
    echo "❌ Error: PostgreSQL container 'cms-postgres' is not running."
    echo "Start it with: docker compose up -d postgres"
    exit 1
fi

echo "✅ PostgreSQL container is running"

# Copy seed file into container and execute
echo "🌱 Seeding database with sample data..."
docker exec -i cms-postgres psql -U cms_user -d cms_db < src/main/resources/db/seed_data.sql

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

