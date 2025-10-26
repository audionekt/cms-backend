# CMS Backend

A powerful, feature-rich Content Management System (CMS) backend built with Kotlin, Spring Boot, PostgreSQL, and AWS S3. This CMS is designed for creating intricate, customizable blog posts with **MDX support**, allowing you to write React code directly in your content.

## Features

### Core Features
- 📝 **MDX Blog Posts**: Store blog posts as stringified MDX, enabling React components in your content
- 🖼️ **Media Management**: Upload and manage images/files with AWS S3 integration
- 👥 **User Management**: Multi-role support (Admin, Editor, Author)
- 🏷️ **Categories & Tags**: Organize content with categories and tags
- 📊 **Analytics**: Track view counts and reading time
- 🔍 **Search**: Full-text search capabilities
- 📄 **Pagination**: Efficient pagination for all list endpoints
- 🎨 **SEO Ready**: Built-in meta fields for SEO optimization

### Technical Features
- 🐳 **Dockerized**: Complete Docker setup with docker-compose
- 🔐 **Security**: Spring Security integration with BCrypt password encoding
- 🗄️ **Database Migrations**: Flyway for version-controlled schema management
- 🚨 **Error Handling**: Comprehensive exception handling
- 📝 **Validation**: Request validation with Jakarta Validation
- 🔄 **RESTful API**: Clean, well-structured REST endpoints
- 🧪 **Test Coverage**: 82% code coverage with comprehensive test suite
- 🤖 **CI/CD**: Automated testing and coverage enforcement via GitHub Actions

## Tech Stack

- **Language**: Kotlin 1.9.20
- **Framework**: Spring Boot 3.2.0
- **Database**: PostgreSQL 16
- **Storage**: AWS S3
- **Build Tool**: Gradle 8.5
- **Java Version**: 17
- **Containerization**: Docker & Docker Compose

## Project Structure

```
cms-backend/
├── src/
│   └── main/
│       ├── kotlin/
│       │   └── com/cms/backend/
│       │       ├── config/          # Configuration classes
│       │       ├── controller/      # REST controllers
│       │       ├── dto/             # Data Transfer Objects
│       │       ├── entity/          # JPA entities
│       │       ├── exception/       # Exception handling
│       │       ├── repository/      # Data repositories
│       │       └── service/         # Business logic
│       └── resources/
│           ├── db/migration/        # Flyway migrations
│           └── application.yml      # Application configuration
├── build.gradle.kts
├── docker-compose.yml
├── Dockerfile
└── README.md
```

## Getting Started

### Prerequisites

- Docker and Docker Compose installed
- AWS account with S3 bucket created
- AWS credentials (Access Key ID and Secret Access Key)

### Environment Setup

1. Copy the example environment file:
```bash
cp .env.example .env
```

2. Update the `.env` file with your AWS credentials:
```env
AWS_ACCESS_KEY_ID=your_access_key_id
AWS_SECRET_ACCESS_KEY=your_secret_access_key
AWS_REGION=us-east-1
AWS_S3_BUCKET=your-cms-bucket-name
```

### Running with Docker

1. Start all services:
```bash
docker-compose up -d
```

2. Check the logs:
```bash
docker-compose logs -f backend
```

3. The API will be available at: `http://localhost:9090`

4. (Optional) Seed the database with sample data:
```bash
./seed-database.sh
```

This will populate the database with:
- 5 users (password: `password123` for all)
- 15 tags
- 7 blog posts with MDX content (6 published, 1 draft)

### Running Locally (without Docker)

1. Start PostgreSQL:
```bash
docker-compose up -d postgres
```

2. Build the project:
```bash
./gradlew build
```

3. Run the application:
```bash
./gradlew bootRun
```

## API Endpoints

### Users
- `POST /api/v1/users` - Create user
- `GET /api/v1/users/{id}` - Get user by ID
- `GET /api/v1/users/username/{username}` - Get user by username
- `GET /api/v1/users` - Get all users
- `PUT /api/v1/users/{id}` - Update user
- `DELETE /api/v1/users/{id}` - Delete user

### Blog Posts
- `POST /api/v1/posts` - Create blog post
- `GET /api/v1/posts/{id}` - Get post by ID
- `GET /api/v1/posts/slug/{slug}` - Get post by slug
- `GET /api/v1/posts` - Get all posts (paginated)
- `GET /api/v1/posts/published` - Get published posts
- `GET /api/v1/posts/status/{status}` - Get posts by status
- `GET /api/v1/posts/author/{authorId}` - Get posts by author
- `GET /api/v1/posts/category/{categoryId}` - Get posts by category
- `GET /api/v1/posts/tag/{tagId}` - Get posts by tag
- `GET /api/v1/posts/featured` - Get featured posts
- `GET /api/v1/posts/search?searchTerm={term}` - Search posts
- `PUT /api/v1/posts/{id}` - Update post
- `POST /api/v1/posts/{id}/view` - Increment view count
- `DELETE /api/v1/posts/{id}` - Delete post

### Categories
- `POST /api/v1/categories` - Create category
- `GET /api/v1/categories/{id}` - Get category by ID
- `GET /api/v1/categories/slug/{slug}` - Get category by slug
- `GET /api/v1/categories` - Get all categories
- `PUT /api/v1/categories/{id}` - Update category
- `DELETE /api/v1/categories/{id}` - Delete category

### Tags
- `POST /api/v1/tags` - Create tag
- `GET /api/v1/tags/{id}` - Get tag by ID
- `GET /api/v1/tags/slug/{slug}` - Get tag by slug
- `GET /api/v1/tags` - Get all tags
- `PUT /api/v1/tags/{id}` - Update tag
- `DELETE /api/v1/tags/{id}` - Delete tag

### Media
- `POST /api/v1/media/upload` - Upload file to S3
- `GET /api/v1/media/{id}` - Get media by ID
- `GET /api/v1/media` - Get all media (paginated)
- `GET /api/v1/media/type/{mediaType}` - Get media by type
- `GET /api/v1/media/user/{uploadedById}` - Get media by uploader
- `PUT /api/v1/media/{id}` - Update media metadata
- `DELETE /api/v1/media/{id}` - Delete media

## MDX Support

This CMS stores blog post content as stringified MDX, which means you can:

1. **Write Markdown**: Use standard Markdown syntax
2. **Embed React Components**: Include React components directly in your content
3. **Custom Styling**: Apply custom styles and layouts
4. **Interactive Content**: Create interactive blog posts with React

Example MDX content:
```mdx
# My Blog Post

This is a paragraph with **bold** and *italic* text.

<CustomComponent prop="value">
  This is a React component embedded in MDX!
</CustomComponent>

## Code Example

\`\`\`javascript
const greeting = "Hello, World!";
console.log(greeting);
\`\`\`
```

## Database Schema

The application uses the following main entities:
- **Users**: Authors and administrators
- **BlogPosts**: MDX content, metadata, and relationships
- **Categories**: Post categorization
- **Tags**: Post tagging
- **Media**: Uploaded files (stored in S3)

All tables include automatic timestamps (created_at, updated_at) and proper indexing for performance.

## Configuration

Key configuration options in `application.yml`:
- Database connection settings
- AWS S3 configuration
- File upload limits (default: 10MB)
- Flyway migration settings
- Logging levels

## Development

### Building the Project
```bash
./gradlew build
```

### Running Tests
```bash
./gradlew test
```

### Cleaning Build
```bash
./gradlew clean
```

## Testing & Coverage

### Running Tests
```bash
# Run all tests
./gradlew test

# Run tests with coverage report
./gradlew test jacocoTestReport

# Verify 80% coverage enforcement
./gradlew test jacocoTestCoverageVerification
```

### Test Suite (82% Coverage)

The project maintains **82% code coverage** with comprehensive testing:

**Repository Tests** (Real Database - No Mocking!)
- `UserRepositoryTest` - JPA/Hibernate operations with H2
- `BlogPostRepositoryTest` - Complex queries and relationships  
- `TagRepositoryTest` - CRUD operations

**Service Tests** (Minimal Mocking)
- `UserServiceTest` - Business logic, password encoding
- `BlogPostServiceTest` - Post management, status transitions
- `TagServiceTest` - Tag operations
- `MediaServiceTest` - File handling, type detection
- `S3ServiceTest` - AWS S3 operations (mocked AWS only)

**Controller Tests** (Integration Tests)
- `UserControllerTest` - REST endpoints with MockMvc
- `BlogPostControllerTest` - CRUD, pagination, search
- `TagControllerTest` - Tag management endpoints
- `MediaControllerTest` - File upload/download

### Coverage Reports

After running tests, view coverage reports at:
- HTML: `build/reports/jacoco/test/html/index.html`
- XML: `build/reports/jacoco/test/jacocoTestReport.xml`

## CI/CD

### GitHub Actions

**CI - Build, Test & Coverage** (`.github/workflows/ci.yml`)
- ✅ Runs on push to `main`/`develop` and all PRs
- ✅ Builds the project with Gradle
- ✅ Runs full test suite (82% coverage)
- ✅ Enforces 80% minimum coverage (build fails if below)
- ✅ Uploads coverage reports as artifacts
- ✅ Comments coverage report on PRs

### How It Works

1. Push code to GitHub
2. Workflow runs automatically
3. View results in "Actions" tab
4. Coverage reports available as downloadable artifacts
5. PRs get automatic coverage comments

### Status Badge

```markdown
![CI](https://github.com/YOUR_USERNAME/cms-backend/workflows/CI%20-%20Build,%20Test%20&%20Coverage/badge.svg)
```

## Security Notes

- Passwords are encrypted using BCrypt
- CORS is configured (update in production)
- CSRF is disabled for API usage
- Remember to secure your AWS credentials
- Never commit `.env` file to version control

## Future Enhancements

- [ ] JWT authentication
- [ ] Comment system
- [ ] Email notifications
- [ ] Image optimization
- [ ] CDN integration
- [ ] Rate limiting
- [ ] Admin dashboard
- [ ] Webhook support

## License

This project is open source and available under the MIT License.

## Support

For issues, questions, or contributions, please create an issue in the repository.

