# AWS S3 Setup Guide

## Creating Your S3 Bucket

### Best Practices
- ✅ Create a **separate bucket** for this CMS project
- ✅ Use a descriptive naming convention: `cms-backend-media-{environment}`
- ✅ Enable versioning for backup/recovery
- ✅ Configure lifecycle rules for cost optimization
- ✅ Set appropriate CORS policies for frontend access

### Step-by-Step Setup

#### 1. Create S3 Bucket

```bash
# Recommended bucket names:
# - cms-backend-media-dev (development)
# - cms-backend-media-staging (staging)
# - cms-backend-media-prod (production)
```

**Via AWS Console:**
1. Go to AWS S3 Console
2. Click "Create bucket"
3. Enter bucket name (e.g., `cms-backend-media-dev`)
4. Select your region (e.g., `us-east-1`)
5. **Block Public Access settings:**
   - Uncheck "Block all public access" (only if you want public read access)
   - Or keep blocked and use CloudFront for delivery
6. Enable "Bucket Versioning" (recommended)
7. Create bucket

#### 2. Configure CORS Policy

Add this CORS configuration to your bucket:

```json
[
    {
        "AllowedHeaders": ["*"],
        "AllowedMethods": ["GET", "PUT", "POST", "DELETE", "HEAD"],
        "AllowedOrigins": ["http://localhost:3000", "https://yourdomain.com"],
        "ExposeHeaders": ["ETag"],
        "MaxAgeSeconds": 3000
    }
]
```

**To add CORS:**
1. Go to your bucket → Permissions → CORS
2. Paste the JSON above (update `AllowedOrigins` with your frontend URLs)
3. Save

#### 3. Create IAM User for Programmatic Access

**Via AWS Console:**
1. Go to IAM → Users → Add User
2. Username: `cms-backend-s3-user`
3. Access type: "Programmatic access"
4. Permissions: Attach policies directly → Create policy

**IAM Policy (Least Privilege):**
```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "s3:PutObject",
                "s3:GetObject",
                "s3:DeleteObject",
                "s3:ListBucket"
            ],
            "Resource": [
                "arn:aws:s3:::cms-backend-media-dev",
                "arn:aws:s3:::cms-backend-media-dev/*"
            ]
        }
    ]
}
```

5. Complete user creation
6. **Save the Access Key ID and Secret Access Key** (you won't see them again!)

#### 4. Set Environment Variables

```bash
# .env or docker-compose.yml
AWS_ACCESS_KEY_ID=your_access_key_here
AWS_SECRET_ACCESS_KEY=your_secret_key_here
AWS_REGION=us-east-1
AWS_S3_BUCKET=cms-backend-media-dev
```

#### 5. Optional: Enable CloudFront (CDN)

For production, consider using CloudFront for:
- ✅ Faster global content delivery
- ✅ HTTPS/SSL for media files
- ✅ Cost optimization
- ✅ DDoS protection

**Steps:**
1. Go to CloudFront → Create Distribution
2. Origin Domain: Select your S3 bucket
3. Origin Access: Origin Access Control (OAC) recommended
4. Viewer Protocol Policy: Redirect HTTP to HTTPS
5. Create distribution
6. Update your `S3Service` to use CloudFront URL

#### 6. Lifecycle Rules (Cost Optimization)

Configure lifecycle rules to automatically transition old files to cheaper storage:

```json
{
    "Rules": [
        {
            "Id": "MoveToIA",
            "Status": "Enabled",
            "Transitions": [
                {
                    "Days": 90,
                    "StorageClass": "STANDARD_IA"
                },
                {
                    "Days": 365,
                    "StorageClass": "GLACIER"
                }
            ]
        }
    ]
}
```

---

## Security Checklist

- [ ] Created separate S3 bucket for this project
- [ ] Configured CORS policy with specific origins (not `*`)
- [ ] Created IAM user with least-privilege access
- [ ] Stored credentials in environment variables (never in code)
- [ ] Enabled bucket versioning
- [ ] (Optional) Set up CloudFront for production
- [ ] (Optional) Configured lifecycle rules
- [ ] Tested file upload/download/delete operations

---

## Testing Your Setup

```bash
# Start your backend
docker compose up -d

# Test upload endpoint
curl -X POST http://localhost:9090/api/v1/media/upload \
  -F "file=@/path/to/test-image.jpg" \
  -F "altText=Test image" \
  -F "uploadedById=1"

# Check the response - you should get a fileUrl pointing to your S3 bucket
```

---

## Environment-Specific Buckets

| Environment | Bucket Name Example | Purpose |
|-------------|-------------------|---------|
| Development | `cms-backend-media-dev` | Local/dev testing |
| Staging | `cms-backend-media-staging` | Pre-production testing |
| Production | `cms-backend-media-prod` | Live application |

**Why separate buckets?**
- 🔒 Better security isolation
- 💰 Easier cost tracking per environment
- 🔄 Prevents accidental data mixing
- 🎯 Different policies per environment

