# Database Seeding Guide

The CMS backend database is **NOT seeded automatically** on startup. This gives you control over when and how to populate your database with sample data.

---

## 🌱 How to Seed the Database

You have **two options** depending on whether you have PostgreSQL client tools installed locally:

### Option 1: Using Docker (Recommended - No local psql needed)

```bash
./seed-database-docker.sh
```

**When to use:**
- ✅ You don't have PostgreSQL client installed locally
- ✅ Simpler - works out of the box
- ✅ Runs the seed SQL directly in the Docker container

---

### Option 2: Using Local PostgreSQL Client

```bash
./seed-database.sh
```

**When to use:**
- You have `psql` installed locally (`brew install postgresql`)
- You want to connect from your machine to the database

**Requirements:**
```bash
# macOS
brew install postgresql

# Ubuntu/Debian
sudo apt-get install postgresql-client

# Windows
# Download from https://www.postgresql.org/download/windows/
```

---

## 📋 What Gets Seeded

When you run the seeding script, it populates your database with:

### 👥 **5 Users**
| Email | Username | Role | Password |
|-------|----------|------|----------|
| admin@cms.com | admin | ADMIN | password123 |
| jane.doe@cms.com | janedoe | AUTHOR | password123 |
| john.smith@cms.com | johnsmith | AUTHOR | password123 |
| sarah.wilson@cms.com | sarahw | EDITOR | password123 |
| mike.johnson@cms.com | mikej | AUTHOR | password123 |

### 🏷️ **15 Tags**
React, JavaScript, TypeScript, Node.js, Spring Boot, Kotlin, Docker, AWS, PostgreSQL, MDX, Next.js, REST API, GraphQL, Microservices, Testing

### 📝 **7 Blog Posts**
1. **Getting Started with MDX** (PUBLISHED, Featured)
2. **Building a REST API with Spring Boot and Kotlin** (PUBLISHED, Featured)
3. **Mastering React Hooks** (PUBLISHED, Featured)
4. **Docker and Docker Compose: A Practical Guide** (PUBLISHED)
5. **Building Type-Safe APIs with TypeScript** (PUBLISHED)
6. **Introduction to PostgreSQL** (PUBLISHED)
7. **Next.js 14: Server Components Guide** (DRAFT)

All blog posts include:
- ✅ Full MDX content with code examples
- ✅ SEO metadata (meta title, description, keywords)
- ✅ Realistic view counts
- ✅ Reading time estimates
- ✅ Multiple tags per post

---

## 🔄 Workflow Examples

### Fresh Start with Seed Data
```bash
# 1. Start containers
docker compose up -d

# 2. Wait for backend to be ready (migrations run automatically)
docker compose logs -f backend
# Wait for "Started CmsBackendApplicationKt" message

# 3. Seed the database
./seed-database-docker.sh

# 4. Test it
curl http://localhost:9090/api/v1/posts/published
```

### Reset and Reseed
```bash
# 1. Stop containers and remove volumes
docker compose down -v

# 2. Start fresh
docker compose up -d

# 3. Wait for backend to be ready
sleep 10

# 4. Seed again
./seed-database-docker.sh
```

### Production (Don't Seed!)
```bash
# For production, you probably DON'T want to seed with fake data
# Just run migrations:
docker compose up -d

# Create real users via API or admin panel
```

---

## 🛠️ Manual Seeding

If you prefer to run the SQL manually:

```bash
# Via Docker
docker exec -i cms-postgres psql -U cms_user -d cms_db < src/main/resources/db/seed_data.sql

# Via local psql
psql -h localhost -p 5433 -U cms_user -d cms_db -f src/main/resources/db/seed_data.sql
```

---

## 🧹 Clearing Seeded Data

To remove all seeded data and start fresh:

```bash
# Option 1: Drop and recreate everything
docker compose down -v
docker compose up -d

# Option 2: Manual cleanup (keeps schema)
docker exec -i cms-postgres psql -U cms_user -d cms_db << EOF
TRUNCATE blog_post_tags, blog_posts, tags, users CASCADE;
EOF
```

---

## ❓ FAQ

### Q: Why isn't the database seeded automatically on startup?

**A:** We intentionally moved seeding out of Flyway migrations because:
- ✅ **Control** - You choose when to seed
- ✅ **Clean production** - No test data in production
- ✅ **Flexibility** - Different data for different environments
- ✅ **Speed** - Faster startup without seeding

Flyway migrations (in `db/migration/`) only create the schema, not populate data.

### Q: Do I need to seed every time I restart?

**A:** No! The data persists in Docker volumes. You only need to seed:
- After `docker compose down -v` (removes volumes)
- On first setup
- When you want to reset to default data

### Q: Can I customize the seed data?

**A:** Yes! Edit `src/main/resources/db/seed_data.sql` and run the seeding script again. Or create your own SQL file and run it manually.

### Q: What if seeding fails?

**A:** Common issues:
1. **Database not ready** - Wait a few seconds after `docker compose up`
2. **Duplicate data** - Data already seeded. Use `TRUNCATE` to clear first
3. **psql not found** - Use `seed-database-docker.sh` instead

---

## 🎯 Best Practices

### Development
```bash
# Seed with sample data for development
./seed-database-docker.sh
```

### Staging
```bash
# Maybe seed with anonymized production data
# Or use a custom staging seed script
```

### Production
```bash
# DON'T seed! Only run migrations
docker compose up -d

# Create real users and content via the application
```

---

**Happy Seeding!** 🌱

