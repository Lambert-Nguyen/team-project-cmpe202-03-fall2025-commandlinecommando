# Epic 3: Search & Discovery Features

## Overview

The Search & Discovery system provides comprehensive product search capabilities including full-text search, advanced filtering, autocomplete, and personalized recommendations for the Campus Marketplace platform.

## Architecture

### Components

1. **Backend Service** (port 8080): Main search implementation with PostgreSQL full-text search
2. **Listing API** (port 8100): Proxy layer for backward compatibility
3. **Redis**: Caching layer for search results and autocomplete
4. **PostgreSQL**: Database with GIN indexes and full-text search vectors

### Technology Stack

- **Full-Text Search**: PostgreSQL `ts_rank`, `search_vector`, `plainto_tsquery`
- **Typo Tolerance**: PostgreSQL `pg_trgm` extension with similarity matching
- **Caching**: Redis with Spring Cache abstraction
- **Dynamic Filtering**: JPA Specifications for complex queries
- **Proxy Pattern**: RestTemplate for service-to-service communication

## API Endpoints

### Backend Service (`:8080/api`)

#### Search

**POST** `/search`

Search products with full-text search, filters, and pagination.

```json
{
  "query": "laptop",
  "categories": ["ELECTRONICS"],
  "conditions": ["NEW", "LIKE_NEW"],
  "minPrice": 100,
  "maxPrice": 1000,
  "location": "San Jose",
  "dateFrom": "2024-01-01T00:00:00",
  "sortBy": "relevance",
  "page": 0,
  "size": 20
}
```

**Response:**
```json
{
  "results": [
    {
      "productId": "uuid",
      "title": "MacBook Pro 13-inch",
      "description": "...",
      "price": 1200.00,
      "category": "ELECTRONICS",
      "condition": "LIKE_NEW",
      "sellerName": "John Doe",
      "location": "San Jose, CA",
      "viewCount": 45,
      "createdAt": "2024-01-15T10:30:00",
      "relevanceScore": 0.95
    }
  ],
  "totalResults": 150,
  "totalPages": 8,
  "currentPage": 0,
  "pageSize": 20,
  "hasNext": true,
  "hasPrevious": false,
  "metadata": {
    "searchTimeMs": 125,
    "appliedFilters": "Categories: ELECTRONICS, Price: $100 - $1000",
    "totalFilters": 2,
    "sortedBy": "relevance",
    "cached": false
  }
}
```

#### Autocomplete

**GET** `/search/autocomplete?q=laptop`

Get search suggestions as user types (minimum 2 characters).

**Response:**
```json
[
  "Laptop Stand",
  "MacBook Laptop",
  "Gaming Laptop"
]
```

#### Search History

**GET** `/search/history`

Get recent search queries for the current user.

**Response:**
```json
[
  "laptop",
  "textbooks",
  "desk"
]
```

### Discovery Endpoints

#### Trending Products

**GET** `/discovery/trending?limit=10`

Get trending products based on views and favorites.

#### Recommended Products

**GET** `/discovery/recommended?limit=10`

Get personalized recommendations based on browsing history.

#### Similar Products

**GET** `/discovery/similar/{productId}?limit=6`

Get products similar to a specific product.

#### Recently Viewed

**GET** `/discovery/recently-viewed?limit=10`

Get products recently viewed by the current user.

### Listing API Proxy Endpoints (`:8100/api/listings`)

For backward compatibility, listing-api provides proxy endpoints:

- **POST** `/search/v2` → Proxies to backend `/search`
- **GET** `/search/autocomplete` → Proxies to backend `/search/autocomplete`
- **GET** `/discovery/{endpoint}` → Proxies to backend `/discovery/*`

**Legacy endpoint** `/search` remains functional for existing clients.

## Features

### 1. Full-Text Search

- **PostgreSQL ts_rank**: Relevance scoring based on search_vector column
- **Weighted Search**: Title (A) > Description (B) > Category (C)
- **Query Support**: Phrase matching, boolean operators

**Example:**
```
Query: "macbook pro 13"
Matches: Products with "macbook", "pro", "13" in title/description
```

### 2. Typo Tolerance & Fuzzy Search

- **Trigram Similarity**: Uses pg_trgm extension
- **Similarity Threshold**: 0.3 (configurable)
- **Fallback Strategy**: If no exact matches, automatically tries fuzzy search

**Example:**
```
Query: "laptp" (typo)
Matches: Products with "laptop" (fuzzy match)
```

### 3. Advanced Filtering

- **Categories**: Multiple selection (ELECTRONICS, TEXTBOOKS, etc.)
- **Conditions**: NEW, LIKE_NEW, GOOD, FAIR
- **Price Range**: Min/Max price filters
- **Location**: Fuzzy location matching
- **Date Filters**: Last 24h, 7d, 30d, 90d

### 4. Multi-Criteria Sorting

- **relevance**: ts_rank score (default)
- **price_asc**: Price low to high
- **price_desc**: Price high to low
- **date_desc**: Newest first
- **date_asc**: Oldest first
- **popularity**: Views + favorites count

### 5. Autocomplete

- **Trigram Similarity**: Matches partial queries
- **Caching**: 10-minute TTL
- **Limit**: Top 10 suggestions
- **Minimum Length**: 2 characters

### 6. Search History

- **Per-User Tracking**: Stores last 10 searches
- **Async Saving**: Non-blocking operation
- **Cleanup**: Auto-delete after 90 days

### 7. Discovery Features

#### Trending Items
- Based on recent views + favorites
- Cached for 15 minutes
- University-scoped

#### Recommended Items
- Analyzes browsing history
- Category-based recommendations
- Fallback to trending if no history

#### Similar Items
- Same category as viewed product
- Excludes the product itself
- Returns up to 6 similar products

#### Recently Viewed
- Tracks one view per user per product per day
- Prevents duplicate views
- Used for recommendation engine

## Performance

### Caching Strategy

| Cache | TTL | Key |
|-------|-----|-----|
| Search Results | 10 min | Search parameters hash |
| Autocomplete | 10 min | Query + University ID |
| Trending Items | 15 min | University ID |
| Recommended Items | 10 min | User ID |
| Recently Viewed | 1 hour | User ID |

### Performance Targets

- ✅ Search: < 200ms (met via GIN indexes + Redis)
- ✅ Autocomplete: < 100ms (trigram + caching)
- ✅ Trending: < 50ms (materialized views + Redis)
- ✅ Discovery: < 150ms (cached recommendations)

### Database Optimization

- **GIN Indexes**: On search_vector, title, description
- **Composite Indexes**: university_id, category, is_active, price
- **Materialized Views**: v_popular_products, v_trending_products
- **Trigram Indexes**: For fuzzy matching

## Configuration

### Environment Variables

```bash
# Redis
REDIS_HOST=redis
REDIS_PORT=6379

# Backend URL (for listing-api proxy)
BACKEND_URL=http://backend:8080/api

# Search Configuration (application.yml)
app:
  search:
    cache-ttl: 600000              # 10 minutes
    max-results-per-page: 100
    autocomplete-min-length: 2
    fuzzy-match-threshold: 0.3
    trending-cache-ttl: 900000     # 15 minutes
```

### Docker Compose

Redis service is automatically started with:
```bash
docker-compose up redis
```

## Database Schema

### search_history
- `id` (UUID): Primary key
- `user_id` (UUID): Foreign key to users
- `search_query` (VARCHAR): Search query text
- `results_count` (INTEGER): Number of results
- `created_at` (TIMESTAMP): Creation timestamp

### product_views
- `id` (UUID): Primary key
- `user_id` (UUID): Foreign key to users
- `product_id` (UUID): Foreign key to products
- `viewed_at` (TIMESTAMP): View timestamp
- `viewed_at_date` (DATE): View date (for uniqueness)
- **Unique Constraint**: (user_id, product_id, viewed_at_date)

## Migration

### Frontend Migration Strategy

1. **Phase 1**: Use existing `/api/listings/search` endpoint
2. **Phase 2**: Gradually migrate to `/api/listings/search/v2` for enhanced features
3. **Phase 3**: Direct backend calls to `/api/search` for new clients

### Backward Compatibility

- Legacy `/api/listings/search` endpoint remains functional
- Proxy endpoints maintain existing API contract
- No breaking changes for existing clients

## Usage Examples

### Basic Search
```bash
curl -X POST http://localhost:8080/api/search \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "query": "laptop",
    "page": 0,
    "size": 20
  }'
```

### Advanced Search with Filters
```bash
curl -X POST http://localhost:8080/api/search \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "query": "textbook",
    "categories": ["TEXTBOOKS"],
    "minPrice": 10,
    "maxPrice": 100,
    "sortBy": "price_asc",
    "page": 0,
    "size": 20
  }'
```

### Autocomplete
```bash
curl http://localhost:8080/api/search/autocomplete?q=lap \
  -H "Authorization: Bearer $TOKEN"
```

### Trending Products
```bash
curl http://localhost:8080/api/discovery/trending?limit=10 \
  -H "Authorization: Bearer $TOKEN"
```

## Troubleshooting

### Search Returns No Results

1. **Check PostgreSQL Extensions**: Ensure `pg_trgm` extension is installed
   ```sql
   CREATE EXTENSION IF NOT EXISTS "pg_trgm";
   ```

2. **Verify search_vector**: Check if trigger is updating search_vector
   ```sql
   SELECT title, search_vector FROM products LIMIT 10;
   ```

3. **Check Redis**: Verify Redis is running
   ```bash
   docker-compose ps redis
   redis-cli ping
   ```

### Slow Search Performance

1. **Check Index Usage**: Use EXPLAIN ANALYZE
   ```sql
   EXPLAIN ANALYZE
   SELECT * FROM products
   WHERE search_vector @@ plainto_tsquery('english', 'laptop');
   ```

2. **Clear Cache**: If stale results
   ```bash
   redis-cli FLUSHALL
   ```

3. **Refresh Materialized Views**:
   ```sql
   REFRESH MATERIALIZED VIEW CONCURRENTLY mv_popular_products;
   ```

### Proxy Errors

1. **Check Backend URL**: Verify BACKEND_URL environment variable
2. **Check Network**: Ensure listing-api can reach backend service
3. **Check Logs**: Review listing-api logs for connection errors

## Testing

### Unit Tests

Located in `backend/src/test/java/com/commandlinecommandos/campusmarketplace/service/`

- `SearchServiceTest.java`: Search logic tests
- `ProductViewServiceTest.java`: View tracking tests
- `SearchHistoryServiceTest.java`: Search history tests

### Integration Tests

Run comprehensive search tests:
```bash
cd backend
mvn test -Dtest=SearchServiceIntegrationTest
```

### Performance Tests

Verify <200ms search response time:
```bash
curl -w "\nTime: %{time_total}s\n" \
  -X POST http://localhost:8080/api/search \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"query":"laptop"}'
```

## Support

For issues or questions:
1. Check existing documentation
2. Review logs: `backend/logs/campus-marketplace.log`
3. Verify database migrations: `db/migrations/V5__search_discovery_features.sql`
4. Contact the development team

## Version

- **Epic**: 3
- **Version**: 1.0.0
- **Last Updated**: 2024
- **Migration**: V5__search_discovery_features.sql

