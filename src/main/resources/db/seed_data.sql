-- Seed Users
-- Password for all users is "password123" (BCrypt encoded)
INSERT INTO users (email, password, first_name, last_name, username, bio, avatar_url, role, active, created_at, updated_at) VALUES
('admin@cms.com', '$2a$10$rF8e3qKZ3qP9VxQYLX3NZe8YfKvR8rF8e3qKZ3qP9VxQYLX3NZe8Yf', 'Admin', 'User', 'admin', 'System administrator and chief editor', 'https://i.pravatar.cc/150?img=1', 'ADMIN', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('jane.doe@cms.com', '$2a$10$rF8e3qKZ3qP9VxQYLX3NZe8YfKvR8rF8e3qKZ3qP9VxQYLX3NZe8Yf', 'Jane', 'Doe', 'janedoe', 'Tech enthusiast, full-stack developer, and open source contributor', 'https://i.pravatar.cc/150?img=5', 'AUTHOR', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('john.smith@cms.com', '$2a$10$rF8e3qKZ3qP9VxQYLX3NZe8YfKvR8rF8e3qKZ3qP9VxQYLX3NZe8Yf', 'John', 'Smith', 'johnsmith', 'Frontend architect specializing in React and modern web technologies', 'https://i.pravatar.cc/150?img=12', 'AUTHOR', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('sarah.wilson@cms.com', '$2a$10$rF8e3qKZ3qP9VxQYLX3NZe8YfKvR8rF8e3qKZ3qP9VxQYLX3NZe8Yf', 'Sarah', 'Wilson', 'sarahw', 'Backend developer with a passion for scalable systems and clean code', 'https://i.pravatar.cc/150?img=9', 'EDITOR', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('mike.johnson@cms.com', '$2a$10$rF8e3qKZ3qP9VxQYLX3NZe8YfKvR8rF8e3qKZ3qP9VxQYLX3NZe8Yf', 'Mike', 'Johnson', 'mikej', 'DevOps engineer and cloud architecture specialist', 'https://i.pravatar.cc/150?img=15', 'AUTHOR', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Seed Tags
INSERT INTO tags (name, slug, created_at, updated_at) VALUES
('React', 'react', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('JavaScript', 'javascript', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('TypeScript', 'typescript', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Node.js', 'nodejs', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Spring Boot', 'spring-boot', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Kotlin', 'kotlin', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Docker', 'docker', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('AWS', 'aws', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('PostgreSQL', 'postgresql', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('MDX', 'mdx', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Next.js', 'nextjs', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('REST API', 'rest-api', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('GraphQL', 'graphql', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Microservices', 'microservices', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Testing', 'testing', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Seed Blog Posts with MDX Content
INSERT INTO blog_posts (title, slug, excerpt, mdx_content, author_id, status, meta_title, meta_description, meta_keywords, published_at, view_count, reading_time_minutes, allow_comments, featured, created_at, updated_at) VALUES
(
  'Getting Started with MDX: The Future of Content',
  'getting-started-with-mdx',
  'Learn how to combine the power of Markdown with React components using MDX for creating interactive and dynamic content.',
  '# Getting Started with MDX: The Future of Content

MDX is a powerful format that allows you to use **JSX** in your Markdown documents. This means you can import and use React components directly in your content!

## Why MDX?

MDX gives you the best of both worlds:

1. The simplicity of Markdown for writing content
2. The power of React components for interactivity
3. Type safety when using TypeScript

<Alert type="info">
  MDX is used by many popular frameworks including Next.js, Gatsby, and Docusaurus!
</Alert>

## Basic Example

Here''s a simple example of using a custom component in MDX:

```jsx
import { CustomButton } from "./components/Button";

# My Article

This is regular markdown content.

<CustomButton onClick={() => alert("Hello!")}>
  Click me!
</CustomButton>
```

## Advanced Features

### Inline Components

You can create inline components:

<Callout emoji="💡">
  **Pro tip:** Use MDX for documentation, blog posts, and even presentations!
</Callout>

### Code Highlighting

MDX supports code blocks with syntax highlighting:

```javascript
const greeting = "Hello, MDX!";
console.log(greeting);

function fibonacci(n) {
  if (n <= 1) return n;
  return fibonacci(n - 1) + fibonacci(n - 2);
}
```

## Conclusion

MDX opens up endless possibilities for creating rich, interactive content. Start using it in your next project!',
  2,
  'PUBLISHED',
  'Getting Started with MDX: The Future of Content | Tech Blog',
  'Learn how to combine the power of Markdown with React components using MDX',
  'mdx, react, markdown, components, interactive content',
  CURRENT_TIMESTAMP - INTERVAL '5 days',
  1247,
  8,
  true,
  true,
  CURRENT_TIMESTAMP - INTERVAL '5 days',
  CURRENT_TIMESTAMP
),
(
  'Building a REST API with Spring Boot and Kotlin',
  'building-rest-api-spring-boot-kotlin',
  'A comprehensive guide to building production-ready REST APIs using Spring Boot and Kotlin with best practices.',
  '# Building a REST API with Spring Boot and Kotlin

Spring Boot combined with Kotlin creates a powerful and elegant stack for building REST APIs. Let''s explore how to build a production-ready API.

## Why Kotlin for Backend?

Kotlin offers several advantages over Java:

- **Concise syntax** - Less boilerplate code
- **Null safety** - Eliminate NullPointerExceptions
- **Extension functions** - Add functionality to existing classes
- **Coroutines** - Simplified async programming

## Project Setup

First, create a new Spring Boot project with these dependencies:

```kotlin
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
}
```

## Creating Your First Entity

```kotlin
@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @Column(nullable = false)
    val name: String,
    
    @Column(unique = true, nullable = false)
    val email: String
)
```

## Building the Repository

```kotlin
@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String): Optional<User>
}
```

## Creating the Service Layer

```kotlin
@Service
class UserService(private val userRepository: UserRepository) {
    
    fun createUser(request: CreateUserRequest): User {
        val user = User(
            name = request.name,
            email = request.email
        )
        return userRepository.save(user)
    }
    
    fun getUserById(id: Long): User {
        return userRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("User not found") }
    }
}
```

## REST Controller

```kotlin
@RestController
@RequestMapping("/api/v1/users")
class UserController(private val userService: UserService) {
    
    @PostMapping
    fun createUser(@Valid @RequestBody request: CreateUserRequest): ResponseEntity<User> {
        val user = userService.createUser(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(user)
    }
    
    @GetMapping("/{id}")
    fun getUser(@PathVariable id: Long): ResponseEntity<User> {
        val user = userService.getUserById(id)
        return ResponseEntity.ok(user)
    }
}
```

<Alert type="success">
  This architecture follows the **Controller → Service → Repository** pattern for clean separation of concerns.
</Alert>

## Best Practices

1. **Use DTOs** for request/response
2. **Implement proper error handling** with @RestControllerAdvice
3. **Add validation** with Jakarta Validation
4. **Write tests** for all layers
5. **Document your API** with OpenAPI/Swagger

## Conclusion

Spring Boot and Kotlin make an excellent combination for building modern REST APIs. The type safety and conciseness of Kotlin combined with Spring''s ecosystem create a productive development experience.',
  4,
  'PUBLISHED',
  'Building a REST API with Spring Boot and Kotlin | Backend Development',
  'A comprehensive guide to building production-ready REST APIs using Spring Boot and Kotlin',
  'spring boot, kotlin, rest api, backend, web development',
  CURRENT_TIMESTAMP - INTERVAL '3 days',
  892,
  12,
  true,
  true,
  CURRENT_TIMESTAMP - INTERVAL '3 days',
  CURRENT_TIMESTAMP
),
(
  'Mastering React Hooks: A Complete Guide',
  'mastering-react-hooks-complete-guide',
  'Deep dive into React Hooks including useState, useEffect, useContext, and custom hooks with practical examples.',
  '# Mastering React Hooks: A Complete Guide

React Hooks revolutionized how we write React components. Let''s explore the most important hooks and when to use them.

## useState - Managing State

The most basic hook for managing component state:

```jsx
import { useState } from "react";

function Counter() {
  const [count, setCount] = useState(0);
  
  return (
    <div>
      <p>Count: {count}</p>
      <button onClick={() => setCount(count + 1)}>
        Increment
      </button>
    </div>
  );
}
```

<Callout emoji="⚡">
  Always use the functional update form when the new state depends on the previous state!
</Callout>

```jsx
// Good ✅
setCount(prev => prev + 1);

// Bad ❌
setCount(count + 1);
```

## useEffect - Side Effects

Handle side effects in your components:

```jsx
import { useState, useEffect } from "react";

function UserProfile({ userId }) {
  const [user, setUser] = useState(null);
  
  useEffect(() => {
    async function fetchUser() {
      const response = await fetch(`/api/users/${userId}`);
      const data = await response.json();
      setUser(data);
    }
    
    fetchUser();
  }, [userId]); // Re-run when userId changes
  
  if (!user) return <div>Loading...</div>;
  
  return <div>{user.name}</div>;
}
```

## useContext - Sharing State

Share state across components without prop drilling:

```jsx
import { createContext, useContext, useState } from "react";

const ThemeContext = createContext();

function App() {
  const [theme, setTheme] = useState("light");
  
  return (
    <ThemeContext.Provider value={{ theme, setTheme }}>
      <Header />
      <Main />
    </ThemeContext.Provider>
  );
}

function Header() {
  const { theme, setTheme } = useContext(ThemeContext);
  
  return (
    <header className={theme}>
      <button onClick={() => setTheme(theme === "light" ? "dark" : "light")}>
        Toggle Theme
      </button>
    </header>
  );
}
```

## Custom Hooks

Create reusable logic with custom hooks:

```jsx
function useFetch(url) {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  
  useEffect(() => {
    async function fetchData() {
      try {
        const response = await fetch(url);
        const json = await response.json();
        setData(json);
      } catch (err) {
        setError(err);
      } finally {
        setLoading(false);
      }
    }
    
    fetchData();
  }, [url]);
  
  return { data, loading, error };
}

// Usage
function UserList() {
  const { data, loading, error } = useFetch("/api/users");
  
  if (loading) return <div>Loading...</div>;
  if (error) return <div>Error: {error.message}</div>;
  
  return (
    <ul>
      {data.map(user => <li key={user.id}>{user.name}</li>)}
    </ul>
  );
}
```

## Rules of Hooks

<Alert type="warning">
  Always follow these rules when using hooks:
  
  1. Only call hooks at the top level
  2. Only call hooks from React functions
  3. Custom hooks must start with "use"
</Alert>

## Performance Optimization

Use `useMemo` and `useCallback` for optimization:

```jsx
import { useMemo, useCallback } from "react";

function ExpensiveComponent({ items, filter }) {
  const filteredItems = useMemo(() => {
    return items.filter(item => item.category === filter);
  }, [items, filter]);
  
  const handleClick = useCallback((id) => {
    console.log(`Clicked item ${id}`);
  }, []);
  
  return (
    <ul>
      {filteredItems.map(item => (
        <li key={item.id} onClick={() => handleClick(item.id)}>
          {item.name}
        </li>
      ))}
    </ul>
  );
}
```

## Conclusion

React Hooks provide a powerful and flexible way to add state and side effects to functional components. Master these patterns and you''ll write cleaner, more maintainable React code!',
  3,
  'PUBLISHED',
  'Mastering React Hooks: A Complete Guide | React Tutorial',
  'Deep dive into React Hooks with practical examples and best practices',
  'react, hooks, useState, useEffect, useContext, frontend',
  CURRENT_TIMESTAMP - INTERVAL '7 days',
  2341,
  15,
  true,
  true,
  CURRENT_TIMESTAMP - INTERVAL '7 days',
  CURRENT_TIMESTAMP
),
(
  'Docker and Docker Compose: A Practical Guide',
  'docker-docker-compose-practical-guide',
  'Learn how to containerize your applications with Docker and orchestrate multi-container applications with Docker Compose.',
  '# Docker and Docker Compose: A Practical Guide

Docker has revolutionized how we develop, ship, and run applications. Let''s explore practical Docker usage with real-world examples.

## What is Docker?

Docker is a platform for developing, shipping, and running applications in containers. Containers are lightweight, standalone packages that include everything needed to run an application.

## Benefits of Docker

- **Consistency** - Same environment everywhere
- **Isolation** - Applications don''t interfere with each other
- **Portability** - Run anywhere Docker is installed
- **Efficiency** - Lightweight compared to VMs

## Creating a Dockerfile

Here''s a multi-stage Dockerfile for a Spring Boot application:

```dockerfile
FROM gradle:8.5-jdk17 AS build
WORKDIR /app
COPY build.gradle.kts settings.gradle.kts ./
COPY src ./src
RUN gradle build --no-daemon -x test

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 9090
ENTRYPOINT ["java", "-jar", "app.jar"]
```

<Callout emoji="🐳">
  Multi-stage builds keep your final image small by only including runtime dependencies!
</Callout>

## Building and Running

```bash
# Build the image
docker build -t my-app:latest .

# Run the container
docker run -d -p 9090:9090 --name my-app-container my-app:latest

# View logs
docker logs -f my-app-container

# Stop and remove
docker stop my-app-container
docker rm my-app-container
```

## Docker Compose

For multi-container applications, use Docker Compose:

```yaml
version: ''3.8''

services:
  postgres:
    image: postgres:16-alpine
    environment:
      POSTGRES_DB: myapp_db
      POSTGRES_USER: myapp_user
      POSTGRES_PASSWORD: myapp_pass
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

  backend:
    build: .
    ports:
      - "9090:9090"
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/myapp_db
      SPRING_DATASOURCE_USERNAME: myapp_user
      SPRING_DATASOURCE_PASSWORD: myapp_pass
    depends_on:
      - postgres

volumes:
  postgres_data:
```

## Running with Docker Compose

```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop all services
docker-compose down

# Remove volumes too
docker-compose down -v
```

## Best Practices

<Alert type="info">
  Follow these Docker best practices:
  
  1. Use specific image tags, not "latest"
  2. Minimize layer count
  3. Use .dockerignore
  4. Don''t run as root
  5. Use health checks
  6. Keep images small
</Alert>

## Health Checks

Add health checks to your services:

```yaml
services:
  backend:
    build: .
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:9090/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 40s
```

## Volumes and Data Persistence

Three types of data storage:

1. **Volumes** - Managed by Docker (recommended)
2. **Bind mounts** - Link to host filesystem
3. **tmpfs** - In-memory storage

```yaml
services:
  app:
    volumes:
      # Named volume
      - app_data:/app/data
      # Bind mount
      - ./config:/app/config:ro
      # tmpfs
      - type: tmpfs
        target: /app/temp
```

## Networking

Docker Compose automatically creates a network for your services:

```yaml
services:
  backend:
    networks:
      - app-network
  
  database:
    networks:
      - app-network

networks:
  app-network:
    driver: bridge
```

## Environment Variables

Use .env files for configuration:

```bash
# .env
DATABASE_PASSWORD=supersecret
API_KEY=your-api-key
```

```yaml
services:
  backend:
    environment:
      - DATABASE_PASSWORD=${DATABASE_PASSWORD}
      - API_KEY=${API_KEY}
```

## Conclusion

Docker and Docker Compose simplify application deployment and make development environments consistent. Start containerizing your applications today!',
  5,
  'PUBLISHED',
  'Docker and Docker Compose: A Practical Guide | DevOps',
  'Learn how to containerize and orchestrate applications with Docker',
  'docker, docker-compose, containers, devops, deployment',
  CURRENT_TIMESTAMP - INTERVAL '2 days',
  1567,
  14,
  true,
  false,
  CURRENT_TIMESTAMP - INTERVAL '2 days',
  CURRENT_TIMESTAMP
),
(
  'Building Type-Safe APIs with TypeScript and Express',
  'building-type-safe-apis-typescript-express',
  'Learn how to build robust, type-safe REST APIs using TypeScript and Express with validation and error handling.',
  '# Building Type-Safe APIs with TypeScript and Express

TypeScript brings type safety to Node.js development. Let''s build a production-ready Express API with TypeScript.

## Project Setup

```bash
npm init -y
npm install express
npm install -D typescript @types/express @types/node ts-node nodemon
```

Create `tsconfig.json`:

```json
{
  "compilerOptions": {
    "target": "ES2020",
    "module": "commonjs",
    "outDir": "./dist",
    "rootDir": "./src",
    "strict": true,
    "esModuleInterop": true,
    "skipLibCheck": true
  }
}
```

## Type-Safe Request Handlers

Define types for your requests and responses:

```typescript
import { Request, Response, NextFunction } from "express";

interface User {
  id: string;
  name: string;
  email: string;
}

interface CreateUserRequest {
  name: string;
  email: string;
}

type TypedRequest<T> = Request<{}, {}, T>;
type TypedResponse<T> = Response<T>;

async function createUser(
  req: TypedRequest<CreateUserRequest>,
  res: TypedResponse<User>,
  next: NextFunction
) {
  try {
    const { name, email } = req.body;
    const user: User = {
      id: generateId(),
      name,
      email
    };
    res.status(201).json(user);
  } catch (error) {
    next(error);
  }
}
```

## Validation Middleware

Use Zod for runtime validation:

```typescript
import { z } from "zod";

const CreateUserSchema = z.object({
  name: z.string().min(2).max(100),
  email: z.string().email(),
});

function validate<T>(schema: z.ZodSchema<T>) {
  return (req: Request, res: Response, next: NextFunction) => {
    try {
      schema.parse(req.body);
      next();
    } catch (error) {
      if (error instanceof z.ZodError) {
        res.status(400).json({
          error: "Validation failed",
          details: error.errors
        });
      } else {
        next(error);
      }
    }
  };
}

// Usage
app.post("/users", validate(CreateUserSchema), createUser);
```

## Error Handling

Centralized error handler:

```typescript
class AppError extends Error {
  constructor(
    public statusCode: number,
    public message: string,
    public isOperational = true
  ) {
    super(message);
  }
}

function errorHandler(
  err: Error,
  req: Request,
  res: Response,
  next: NextFunction
) {
  if (err instanceof AppError) {
    res.status(err.statusCode).json({
      status: "error",
      message: err.message
    });
  } else {
    console.error(err);
    res.status(500).json({
      status: "error",
      message: "Internal server error"
    });
  }
}

app.use(errorHandler);
```

<Alert type="success">
  TypeScript catches errors at compile time, preventing runtime issues!
</Alert>

## Database Integration

Type-safe database queries with Prisma:

```typescript
import { PrismaClient } from "@prisma/client";

const prisma = new PrismaClient();

async function getUsers() {
  const users = await prisma.user.findMany({
    select: {
      id: true,
      name: true,
      email: true,
    }
  });
  return users;
}
```

## Conclusion

TypeScript makes Express APIs more maintainable and less error-prone. The initial setup pays off with better developer experience and fewer bugs.',
  2,
  'PUBLISHED',
  'Building Type-Safe APIs with TypeScript and Express',
  'Build robust REST APIs with TypeScript, Express, and type safety',
  'typescript, express, nodejs, rest api, type safety',
  CURRENT_TIMESTAMP - INTERVAL '1 day',
  743,
  10,
  true,
  false,
  CURRENT_TIMESTAMP - INTERVAL '1 day',
  CURRENT_TIMESTAMP
),
(
  'Introduction to PostgreSQL: Getting Started',
  'introduction-postgresql-getting-started',
  'A beginner-friendly guide to PostgreSQL covering installation, basic queries, and essential concepts.',
  '# Introduction to PostgreSQL: Getting Started

PostgreSQL is a powerful, open-source relational database. Let''s learn the fundamentals.

## Why PostgreSQL?

- **ACID compliant** - Reliable transactions
- **Feature rich** - JSON support, full-text search, etc.
- **Extensible** - Custom functions and types
- **Performance** - Handles large datasets efficiently

## Installation

**Using Docker:**
```bash
docker run -d \
  --name postgres \
  -e POSTGRES_PASSWORD=mysecretpassword \
  -e POSTGRES_DB=mydb \
  -p 5432:5432 \
  postgres:16
```

## Basic Queries

Create a table:
```sql
CREATE TABLE users (
  id SERIAL PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  email VARCHAR(255) UNIQUE NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

Insert data:
```sql
INSERT INTO users (name, email) 
VALUES (''John Doe'', ''john@example.com'');
```

Query data:
```sql
SELECT * FROM users WHERE name LIKE ''%John%'';
```

## Conclusion

PostgreSQL is an excellent choice for modern applications. Start with these basics and explore its advanced features!',
  4,
  'PUBLISHED',
  'Introduction to PostgreSQL: Getting Started',
  'Learn PostgreSQL basics including installation and fundamental queries',
  'postgresql, database, sql, tutorial',
  CURRENT_TIMESTAMP - INTERVAL '4 days',
  1024,
  7,
  true,
  false,
  CURRENT_TIMESTAMP - INTERVAL '4 days',
  CURRENT_TIMESTAMP
),
(
  'Next.js 14: The Complete Guide to Server Components',
  'nextjs-14-server-components-guide',
  'Explore Next.js 14 Server Components, Server Actions, and the new App Router with practical examples.',
  '# Next.js 14: The Complete Guide to Server Components

Next.js 14 introduces powerful features for building modern web applications. Let''s explore Server Components and the App Router.

## Server Components vs Client Components

**Server Components** (default):
- Run on the server
- No JavaScript sent to client
- Can access backend resources directly
- Better performance

**Client Components:**
- Run in the browser
- Interactive with hooks
- Use "use client" directive

## Example Server Component

```tsx
// app/posts/page.tsx
async function getPosts() {
  const res = await fetch("http://localhost:9090/api/v1/posts/published");
  return res.json();
}

export default async function PostsPage() {
  const { content: posts } = await getPosts();
  
  return (
    <div>
      <h1>Blog Posts</h1>
      {posts.map(post => (
        <article key={post.id}>
          <h2>{post.title}</h2>
          <p>{post.excerpt}</p>
        </article>
      ))}
    </div>
  );
}
```

## Server Actions

Handle form submissions without API routes:

```tsx
"use server"

async function createPost(formData: FormData) {
  const title = formData.get("title");
  const content = formData.get("content");
  
  await db.post.create({
    data: { title, content }
  });
  
  revalidatePath("/posts");
}

export default function CreatePostForm() {
  return (
    <form action={createPost}>
      <input name="title" required />
      <textarea name="content" required />
      <button type="submit">Create Post</button>
    </form>
  );
}
```

<Alert type="info">
  Server Actions eliminate the need for separate API endpoints for mutations!
</Alert>

## Streaming and Suspense

Show loading states while data fetches:

```tsx
import { Suspense } from "react";

export default function Page() {
  return (
    <div>
      <h1>My Page</h1>
      <Suspense fallback={<div>Loading posts...</div>}>
        <Posts />
      </Suspense>
    </div>
  );
}

async function Posts() {
  const posts = await getPosts();
  return <PostList posts={posts} />;
}
```

## Conclusion

Next.js 14 Server Components offer incredible performance benefits. Start using them in your projects today!',
  3,
  'DRAFT',
  'Next.js 14: The Complete Guide to Server Components',
  'Master Next.js 14 Server Components and the App Router',
  'nextjs, react, server components, app router',
  NULL,
  0,
  11,
  true,
  false,
  CURRENT_TIMESTAMP,
  CURRENT_TIMESTAMP
);

-- Link Blog Posts to Tags
INSERT INTO blog_post_tags (blog_post_id, tag_id) VALUES
(1, 1), (1, 10),  -- MDX, React
(2, 5), (2, 6), (2, 12),  -- Spring Boot, Kotlin, REST API
(3, 1), (3, 2),  -- React, JavaScript
(4, 7), (4, 8),  -- Docker, AWS
(5, 2), (5, 3), (5, 4), (5, 12),  -- TypeScript, JavaScript, Node.js, REST API
(6, 9), (6, 5),  -- PostgreSQL, Spring Boot
(7, 1), (7, 11), (7, 3);  -- React, Next.js, TypeScript



