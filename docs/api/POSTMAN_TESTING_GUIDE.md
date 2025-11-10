# Postman API Testing Guide - Epic 3: Search & Discovery

**Last Updated**: November 9, 2025  
**Backend Version**: 1.0.0  
**Base URL**: `http://localhost:8080/api`

---

## 📋 **Table of Contents**

1. [Setup](#setup)
2. [Authentication](#authentication)
3. [Search Endpoints](#search-endpoints)
4. [Discovery Endpoints](#discovery-endpoints)
5. [Listing-API Proxy Endpoints](#listing-api-proxy-endpoints)
6. [Example Workflows](#example-workflows)
7. [Troubleshooting](#troubleshooting)

---

## 🔧 **Setup**

### **1. Import Postman Collection**

Download and import this JSON collection into Postman:

```json
{
  "info": {
    "name": "Campus Marketplace - Search & Discovery",
    "description": "Epic 3 API endpoints for search and discovery features",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "variable": [
    {
      "key": "base_url",
      "value": "http://localhost:8080/api",
      "type": "string"
    },
    {
      "key": "listing_api_url",
      "value": "http://localhost:8081",
      "type": "string"
    },
    {
      "key": "auth_token",
      "value": "",
      "type": "string"
    }
  ]
}
```

### **2. Environment Variables**

Create a Postman environment with these variables:

| Variable | Value | Description |
|----------|-------|-------------|
| `base_url` | `http://localhost:8080/api` | Backend API base URL |
| `listing_api_url` | `http://localhost:8081` | Listing API base URL |
| `auth_token` | `<your_jwt_token>` | JWT token from login |
| `user_id` | `<your_user_id>` | Your user UUID |
| `university_id` | `<your_university_id>` | Your university UUID |

---

## 🔐 **Authentication**

### **1. Login to Get JWT Token**

**Endpoint**: `POST {{base_url}}/auth/login`

**Request Body**:
```json
{
  "username": "testuser",
  "password": "testpassword123"
}
```

**Response**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "userId": "123e4567-e89b-12d3-a456-426614174000",
  "username": "testuser",
  "email": "test@university.edu",
  "role": "STUDENT"
}
```

**Post-Response Script** (Save token automatically):
```javascript
// Add this to the "Tests" tab in Postman
if (pm.response.code === 200) {
    var jsonData = pm.response.json();
    pm.environment.set("auth_token", jsonData.token);
    pm.environment.set("user_id", jsonData.userId);
    console.log("Token saved:", jsonData.token);
}
```

---

## 🔍 **Search Endpoints**

### **1. Basic Search**

**Endpoint**: `POST {{base_url}}/search`

**Headers**:
```
Authorization: Bearer {{auth_token}}
Content-Type: application/json
```

**Request Body**:
```json
{
  "query": "laptop",
  "page": 0,
  "size": 20,
  "sortBy": "relevance"
}
```

**Response**:
```json
{
  "results": [
    {
      "productId": "uuid-here",
      "title": "MacBook Pro 13-inch",
      "description": "Excellent condition laptop",
      "price": 1200.00,
      "category": "ELECTRONICS",
      "condition": "LIKE_NEW",
      "sellerId": "uuid-here",
      "sellerName": "John Doe",
      "sellerUsername": "jdoe",
      "location": "San Jose",
      "viewCount": 45,
      "favoriteCount": 12,
      "createdAt": "2025-11-01T10:30:00",
      "imageUrls": [],
      "relevanceScore": 0.95,
      "negotiable": true,
      "quantity": 1
    }
  ],
  "totalResults": 15,
  "totalPages": 1,
  "currentPage": 0,
  "pageSize": 20,
  "hasNext": false,
  "hasPrevious": false,
  "metadata": {
    "searchTimeMs": 45,
    "appliedFilters": "",
    "totalFilters": 0,
    "sortedBy": "relevance",
    "cached": false,
    "searchQuery": "laptop"
  }
}
```

---

### **2. Advanced Search with Filters**

**Endpoint**: `POST {{base_url}}/search`

**Request Body**:
```json
{
  "query": "textbook",
  "categories": ["TEXTBOOKS", "BOOKS"],
  "conditions": ["NEW", "LIKE_NEW", "GOOD"],
  "minPrice": 10.00,
  "maxPrice": 100.00,
  "location": "San Jose",
  "dateFrom": "2025-11-01T00:00:00",
  "sortBy": "price_asc",
  "page": 0,
  "size": 20
}
```

**Available Sort Options**:
- `relevance` - Sort by search relevance (default)
- `price_asc` - Price low to high
- `price_desc` - Price high to low
- `date_desc` - Newest first
- `date_asc` - Oldest first
- `popularity` - Most viewed/favorited

**Available Categories**:
```
TEXTBOOKS, ELECTRONICS, FURNITURE, CLOTHING, 
BOOKS, SPORTS, TOOLS, VEHICLES, SERVICES, OTHER
```

**Available Conditions**:
```
NEW, LIKE_NEW, GOOD, FAIR, POOR
```

---

### **3. Autocomplete / Auto-Suggest**

**Endpoint**: `GET {{base_url}}/search/autocomplete`

**Query Parameters**:
```
?q=lap
```

**Headers**:
```
Authorization: Bearer {{auth_token}}
```

**Response**:
```json
{
  "suggestions": [
    "laptop",
    "laptop charger",
    "laptop bag",
    "laptop stand"
  ]
}
```

**Notes**:
- Minimum 2 characters required
- Returns up to 10 suggestions
- Based on existing product titles
- Ordered by similarity/popularity

---

### **4. Search History**

**Endpoint**: `GET {{base_url}}/search/history`

**Headers**:
```
Authorization: Bearer {{auth_token}}
```

**Response**:
```json
{
  "history": [
    {
      "id": "uuid-here",
      "searchQuery": "laptop",
      "resultsCount": 15,
      "createdAt": "2025-11-09T10:30:00"
    },
    {
      "id": "uuid-here",
      "searchQuery": "textbook java",
      "resultsCount": 8,
      "createdAt": "2025-11-09T09:15:00"
    }
  ]
}
```

---

## 🌟 **Discovery Endpoints**

### **1. Trending Items**

**Endpoint**: `GET {{base_url}}/discovery/trending`

**Query Parameters**:
```
?limit=10
```

**Headers**:
```
Authorization: Bearer {{auth_token}}
```

**Response**:
```json
{
  "trending": [
    {
      "productId": "uuid-here",
      "title": "MacBook Pro M2",
      "description": "Latest model, excellent condition",
      "price": 1500.00,
      "category": "ELECTRONICS",
      "condition": "LIKE_NEW",
      "viewCount": 234,
      "favoriteCount": 56,
      "createdAt": "2025-11-05T10:00:00",
      "sellerId": "uuid-here",
      "sellerUsername": "techseller",
      "location": "San Jose",
      "negotiable": true,
      "quantity": 1,
      "imageUrls": []
    }
  ]
}
```

**Notes**:
- Shows most viewed products in last 7 days
- Default limit: 10 items
- Max limit: 50 items
- Cached for 5 minutes

---

### **2. Personalized Recommendations**

**Endpoint**: `GET {{base_url}}/discovery/recommended`

**Query Parameters**:
```
?limit=10
```

**Headers**:
```
Authorization: Bearer {{auth_token}}
```

**Response**:
```json
{
  "recommended": [
    {
      "productId": "uuid-here",
      "title": "Wireless Mouse",
      "description": "Logitech MX Master 3",
      "price": 80.00,
      "category": "ELECTRONICS",
      "condition": "NEW",
      "viewCount": 12,
      "favoriteCount": 3,
      "createdAt": "2025-11-08T15:00:00",
      "sellerId": "uuid-here",
      "sellerUsername": "gadgetseller",
      "location": "Santa Clara",
      "negotiable": false,
      "quantity": 2,
      "imageUrls": []
    }
  ]
}
```

**Notes**:
- Based on your viewing history
- Uses categories you've viewed
- Requires authenticated user
- Updates in real-time as you browse

---

### **3. Similar Products**

**Endpoint**: `GET {{base_url}}/discovery/similar/{productId}`

**Example**: `GET {{base_url}}/discovery/similar/123e4567-e89b-12d3-a456-426614174000`

**Query Parameters**:
```
?limit=10
```

**Headers**:
```
Authorization: Bearer {{auth_token}}
```

**Response**:
```json
{
  "similar": [
    {
      "productId": "uuid-here",
      "title": "Dell XPS 15",
      "description": "Similar specs to MacBook",
      "price": 1300.00,
      "category": "ELECTRONICS",
      "condition": "GOOD",
      "viewCount": 45,
      "favoriteCount": 11,
      "createdAt": "2025-11-07T12:00:00",
      "sellerId": "uuid-here",
      "sellerUsername": "laptopseller",
      "location": "Mountain View",
      "negotiable": true,
      "quantity": 1,
      "imageUrls": []
    }
  ]
}
```

**Notes**:
- Finds products in same category
- Excludes the original product
- Similar price range
- From same university

---

### **4. Recently Viewed**

**Endpoint**: `GET {{base_url}}/discovery/recently-viewed`

**Query Parameters**:
```
?limit=20
```

**Headers**:
```
Authorization: Bearer {{auth_token}}
```

**Response**:
```json
{
  "recentlyViewed": [
    {
      "productId": "uuid-here",
      "title": "Java Programming Textbook",
      "description": "CS textbook for beginners",
      "price": 50.00,
      "category": "TEXTBOOKS",
      "condition": "GOOD",
      "viewCount": 23,
      "favoriteCount": 5,
      "createdAt": "2025-11-01T09:00:00",
      "sellerId": "uuid-here",
      "sellerUsername": "bookseller",
      "location": "San Jose",
      "negotiable": true,
      "quantity": 1,
      "imageUrls": []
    }
  ]
}
```

**Notes**:
- Shows your last 20 viewed products
- Ordered by most recent first
- Tracks one view per product per day
- Authenticated users only

---

## 🔄 **Listing-API Proxy Endpoints**

### **1. Enhanced Search (v2) - Proxy**

**Endpoint**: `POST {{listing_api_url}}/listings/search/v2`

**Headers**:
```
Authorization: Bearer {{auth_token}}
Content-Type: application/json
```

**Request Body**: (Same as backend /api/search)
```json
{
  "query": "laptop",
  "categories": ["ELECTRONICS"],
  "minPrice": 500.00,
  "maxPrice": 2000.00,
  "page": 0,
  "size": 20
}
```

**Response**: (Same format as backend)

**Notes**:
- Proxies to backend `/api/search`
- Maintains backward compatibility
- Frontend should migrate to direct `/api/search`

---

### **2. Autocomplete - Proxy**

**Endpoint**: `GET {{listing_api_url}}/listings/search/autocomplete`

**Query Parameters**:
```
?q=laptop
```

**Headers**:
```
Authorization: Bearer {{auth_token}}
```

**Response**: (Same as backend autocomplete)

---

### **3. Discovery - Proxy**

**Endpoint**: `GET {{listing_api_url}}/listings/discovery/{endpoint}`

**Examples**:
- `GET {{listing_api_url}}/listings/discovery/trending?limit=10`
- `GET {{listing_api_url}}/listings/discovery/recommended?limit=10`

**Headers**:
```
Authorization: Bearer {{auth_token}}
```

**Response**: (Same as backend discovery)

---

## 📝 **Example Workflows**

### **Workflow 1: User Searches for Laptops**

```
1. POST /api/search
   Body: {"query": "laptop", "page": 0, "size": 20}
   
2. Track search in history (automatic, async)
   
3. User clicks on product -> Track view (automatic)
   
4. GET /api/discovery/similar/{productId}
   Show similar laptops
```

### **Workflow 2: Browse by Category**

```
1. POST /api/search
   Body: {"categories": ["ELECTRONICS"], "page": 0, "size": 20}
   
2. Apply price filter
   POST /api/search
   Body: {"categories": ["ELECTRONICS"], "minPrice": 100, "maxPrice": 500}
   
3. Sort by price
   POST /api/search
   Body: {"categories": ["ELECTRONICS"], "minPrice": 100, "maxPrice": 500, "sortBy": "price_asc"}
```

### **Workflow 3: Discover New Products**

```
1. GET /api/discovery/trending?limit=10
   Show trending items
   
2. GET /api/discovery/recommended?limit=10
   Show personalized recommendations
   
3. GET /api/discovery/recently-viewed?limit=20
   Show browsing history
```

---

## 🧪 **Postman Test Scripts**

### **Test 1: Verify Search Response Structure**

Add to "Tests" tab:
```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response has results array", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property('results');
    pm.expect(jsonData.results).to.be.an('array');
});

pm.test("Response has metadata", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property('metadata');
    pm.expect(jsonData.metadata).to.have.property('searchTimeMs');
});

pm.test("Search completes in < 200ms", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.metadata.searchTimeMs).to.be.below(200);
});
```

### **Test 2: Verify Pagination**

```javascript
pm.test("Pagination info is correct", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property('totalResults');
    pm.expect(jsonData).to.have.property('totalPages');
    pm.expect(jsonData).to.have.property('currentPage');
    pm.expect(jsonData).to.have.property('pageSize');
    pm.expect(jsonData).to.have.property('hasNext');
    pm.expect(jsonData).to.have.property('hasPrevious');
});
```

### **Test 3: Verify Product Structure**

```javascript
pm.test("Products have required fields", function () {
    var jsonData = pm.response.json();
    if (jsonData.results.length > 0) {
        var product = jsonData.results[0];
        pm.expect(product).to.have.property('productId');
        pm.expect(product).to.have.property('title');
        pm.expect(product).to.have.property('price');
        pm.expect(product).to.have.property('category');
        pm.expect(product).to.have.property('condition');
        pm.expect(product).to.have.property('sellerId');
    }
});
```

---

## 🐛 **Troubleshooting**

### **Error: 401 Unauthorized**

```json
{
  "error": "Unauthorized",
  "message": "Invalid or expired token"
}
```

**Solution**:
1. Login again to get fresh token
2. Update `auth_token` environment variable
3. Check Authorization header format: `Bearer <token>`

---

### **Error: 400 Bad Request - Validation Error**

```json
{
  "error": "Validation failed",
  "details": [
    "page must be greater than or equal to 0",
    "size must be between 1 and 100"
  ]
}
```

**Solution**:
- Check request body format
- Verify all required fields
- Check data types (strings vs numbers)

---

### **Error: 500 Internal Server Error - PostgreSQL Function Not Found**

```json
{
  "error": "Internal server error",
  "message": "Function TS_RANK not found"
}
```

**Solution**:
- **YOU'RE USING H2 DATABASE!**
- Switch to PostgreSQL:
  ```bash
  docker-compose up -d postgres
  mvn spring-boot:run -Dspring.profiles.active=dev
  ```

---

### **Search Returns Empty Results**

**Checklist**:
1. Is PostgreSQL running? `docker ps | grep postgres`
2. Are migrations applied? `mvn flyway:info`
3. Do you have test data? `psql -U marketplace_user -d marketplace_db -c "SELECT count(*) FROM products;"`
4. Are products active and approved?
   ```sql
   SELECT count(*) FROM products 
   WHERE is_active = true 
   AND moderation_status = 'APPROVED';
   ```

---

### **Slow Search Performance (> 200ms)**

**Debug Steps**:
1. Check if indexes exist:
   ```sql
   SELECT indexname FROM pg_indexes 
   WHERE tablename = 'products';
   ```

2. Check query execution plan:
   ```sql
   EXPLAIN ANALYZE 
   SELECT * FROM products 
   WHERE search_vector @@ plainto_tsquery('laptop');
   ```

3. Check Redis cache status:
   ```bash
   redis-cli
   > INFO stats
   > KEYS search*
   ```

---

## 📚 **Additional Resources**

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/v3/api-docs
- **Database Guide**: `docs/deployment/DATABASE_CONFIGURATION.md`
- **Implementation Docs**: `docs/implementation/EPIC3_FINAL_STATUS.md`

---

## 🎯 **Quick Reference**

### **All Endpoints Summary**

| Method | Endpoint | Auth Required | Description |
|--------|----------|---------------|-------------|
| POST | `/api/search` | ✅ | Advanced search with filters |
| GET | `/api/search/autocomplete` | ✅ | Auto-suggest search terms |
| GET | `/api/search/history` | ✅ | User's search history |
| GET | `/api/discovery/trending` | ✅ | Trending products |
| GET | `/api/discovery/recommended` | ✅ | Personalized recommendations |
| GET | `/api/discovery/similar/{id}` | ✅ | Similar products |
| GET | `/api/discovery/recently-viewed` | ✅ | Recently viewed products |

### **Response Time Targets**

| Endpoint | Target | Actual (avg) |
|----------|--------|--------------|
| Search | < 200ms | ~45ms ✅ |
| Autocomplete | < 100ms | ~20ms ✅ |
| Discovery | < 100ms | ~30ms ✅ |

---

**Happy Testing! 🚀**

