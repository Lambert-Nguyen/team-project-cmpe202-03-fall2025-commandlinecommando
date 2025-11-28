# Campus Marketplace - Working Curl API Tests

## Prerequisites
Backend server must be running on `http://localhost:8080`

## 1. Authentication Tests

### Register a BUYER User
```bash
curl -X POST 'http://localhost:8080/api/auth/register' \
  -H 'Content-Type: application/json' \
  -d '{
    "username": "testbuyer",
    "email": "buyer@example.com",
    "password": "Password123",
    "firstName": "Test",
    "lastName": "Buyer",
    "role": "BUYER"
  }'
```

### Register a SELLER User
```bash
curl -X POST 'http://localhost:8080/api/auth/register' \
  -H 'Content-Type: application/json' \
  -d '{
    "username": "testseller",
    "email": "seller@example.com",
    "password": "Password123",
    "firstName": "Test",
    "lastName": "Seller",
    "role": "SELLER"
  }'
```

### Login as SELLER
```bash
curl -X POST 'http://localhost:8080/api/auth/login' \
  -H 'Content-Type: application/json' \
  -d '{
    "username": "testseller",
    "password": "Password123"
  }'
```

**Save the `accessToken` from the response for use in subsequent requests**

### Login with Seed Data User (if database has seed data)
```bash
curl -X POST 'http://localhost:8080/api/auth/login' \
  -H 'Content-Type: application/json' \
  -d '{
    "username": "carol_seller",
    "password": "password123"
  }'
```

## 2. Listing API Tests (NEW ENDPOINTS)

### Create a Listing (SELLER only)
```bash
TOKEN="YOUR_SELLER_TOKEN_HERE"

curl -X POST 'http://localhost:8080/api/listings' \
  -H 'Content-Type: application/json' \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "title": "MacBook Pro 2021",
    "description": "Like new MacBook Pro M1, 16GB RAM, 512GB SSD. Perfect condition!",
    "category": "ELECTRONICS",
    "price": 1200.00,
    "condition": "LIKE_NEW",
    "location": "San Jose State University"
  }'
```

### Search All Listings
```bash
curl -X GET 'http://localhost:8080/api/listings/search?page=0&size=10' \
  -H "Authorization: Bearer $TOKEN"
```

### Search Listings with Filters
```bash
# By category
curl -X GET 'http://localhost:8080/api/listings/search?category=ELECTRONICS' \
  -H "Authorization: Bearer $TOKEN"

# By price range
curl -X GET 'http://localhost:8080/api/listings/search?minPrice=500&maxPrice=1500' \
  -H "Authorization: Bearer $TOKEN"

# By keyword
curl -X GET 'http://localhost:8080/api/listings/search?keyword=MacBook' \
  -H "Authorization: Bearer $TOKEN"

# Multiple filters
curl -X GET 'http://localhost:8080/api/listings/search?category=ELECTRONICS&minPrice=1000&maxPrice=2000&condition=LIKE_NEW' \
  -H "Authorization: Bearer $TOKEN"
```

### Get Listing by ID
```bash
curl -X GET 'http://localhost:8080/api/listings/1' \
  -H "Authorization: Bearer $TOKEN"
```

### Get My Listings (SELLER only)
```bash
curl -X GET 'http://localhost:8080/api/listings/my-listings' \
  -H "Authorization: Bearer $TOKEN"
```

### Update a Listing (SELLER only)
```bash
curl -X PUT 'http://localhost:8080/api/listings/1' \
  -H 'Content-Type: application/json' \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "title": "MacBook Pro 2021 - Price Reduced",
    "description": "Like new MacBook Pro M1 - Must sell fast!",
    "category": "ELECTRONICS",
    "price": 1100.00,
    "condition": "LIKE_NEW",
    "location": "San Jose State University"
  }'
```

### Mark Listing as Sold (SELLER only)
```bash
curl -X PUT 'http://localhost:8080/api/listings/1/mark-sold' \
  -H "Authorization: Bearer $TOKEN"
```

### Delete a Listing (SELLER only)
```bash
curl -X DELETE 'http://localhost:8080/api/listings/1' \
  -H "Authorization: Bearer $TOKEN"
```

## 3. Existing Product API Tests

### Get All Products
```bash
curl -X GET 'http://localhost:8080/api/products' \
  -H "Authorization: Bearer $TOKEN"
```

### Get Product by ID
```bash
curl -X GET 'http://localhost:8080/api/products/PRODUCT_ID_HERE' \
  -H "Authorization: Bearer $TOKEN"
```

### Search Products
```bash
curl -X GET 'http://localhost:8080/api/products/search?query=laptop' \
  -H "Authorization: Bearer $TOKEN"
```

## 4. User Management Tests

### Get Current User Profile
```bash
curl -X GET 'http://localhost:8080/api/users/profile' \
  -H "Authorization: Bearer $TOKEN"
```

### Update User Profile
```bash
curl -X PUT 'http://localhost:8080/api/users/profile' \
  -H 'Content-Type: application/json' \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "email": "newemail@example.com",
    "firstName": "Updated",
    "lastName": "Name"
  }'
```

## 5. Full Example Workflow

Here's a complete workflow from registration to creating a listing:

```bash
# Step 1: Register as SELLER
REGISTER_RESPONSE=$(curl -s -X POST 'http://localhost:8080/api/auth/register' \
  -H 'Content-Type: application/json' \
  -d '{
    "username": "newseller",
    "email": "newseller@example.com",
    "password": "Password123",
    "firstName": "New",
    "lastName": "Seller",
    "role": "SELLER"
  }')

echo "Registration Response:"
echo "$REGISTER_RESPONSE" | jq '.'

# Step 2: Extract token
TOKEN=$(echo "$REGISTER_RESPONSE" | jq -r '.accessToken')
echo "Token: $TOKEN"

# Step 3: Create a listing
echo "Creating listing..."
curl -s -X POST 'http://localhost:8080/api/listings' \
  -H 'Content-Type: application/json' \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "title": "iPhone 13 Pro",
    "description": "Excellent condition, 256GB",
    "category": "ELECTRONICS",
    "price": 800.00,
    "condition": "LIKE_NEW",
    "location": "SJSU Campus"
  }' | jq '.'

# Step 4: Get my listings
echo "My listings:"
curl -s -X GET 'http://localhost:8080/api/listings/my-listings' \
  -H "Authorization: Bearer $TOKEN" | jq '.'

# Step 5: Search all listings
echo "All listings:"
curl -s -X GET 'http://localhost:8080/api/listings/search' \
  -H "Authorization: Bearer $TOKEN" | jq '{total: .totalElements, listings: [.content[] | {id: .listingId, title, price, status}]}'
```

## 5. Communication API Tests (NEW ENDPOINTS)

### Create or Get Conversation for a Listing
```bash
# BUYER initiates conversation with SELLER about a listing
LISTING_ID=1
SELLER_ID=2

curl -X POST "http://localhost:8080/api/conversations?listingId=${LISTING_ID}&sellerId=${SELLER_ID}" \
  -H "Authorization: Bearer $BUYER_TOKEN"
```

### Get All My Conversations
```bash
curl -X GET 'http://localhost:8080/api/conversations' \
  -H "Authorization: Bearer $TOKEN"
```

### Get Specific Conversation
```bash
CONVERSATION_ID=1

curl -X GET "http://localhost:8080/api/conversations/${CONVERSATION_ID}" \
  -H "Authorization: Bearer $TOKEN"
```

### Send a Message in a Conversation
```bash
CONVERSATION_ID=1

curl -X POST "http://localhost:8080/api/messages/${CONVERSATION_ID}" \
  -H 'Content-Type: application/json' \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "content": "Hi, is this MacBook still available?"
  }'
```

### Get All Messages in a Conversation
```bash
CONVERSATION_ID=1

curl -X GET "http://localhost:8080/api/messages/${CONVERSATION_ID}" \
  -H "Authorization: Bearer $TOKEN"
```

### Mark Messages as Read
```bash
CONVERSATION_ID=1

curl -X PUT "http://localhost:8080/api/messages/${CONVERSATION_ID}/mark-read" \
  -H "Authorization: Bearer $TOKEN"
```

### Get All Unread Messages
```bash
curl -X GET 'http://localhost:8080/api/messages/unread' \
  -H "Authorization: Bearer $TOKEN"
```

### Delete a Message
```bash
MESSAGE_ID=1

curl -X DELETE "http://localhost:8080/api/messages/${MESSAGE_ID}" \
  -H "Authorization: Bearer $TOKEN"
```

### Delete a Conversation
```bash
CONVERSATION_ID=1

curl -X DELETE "http://localhost:8080/api/conversations/${CONVERSATION_ID}" \
  -H "Authorization: Bearer $TOKEN"
```

## 6. Full Example Workflow with Communication

Here's a complete workflow showing BUYER and SELLER communication:

```bash
# Step 1: Register BUYER
BUYER_RESPONSE=$(curl -s -X POST 'http://localhost:8080/api/auth/register' \
  -H 'Content-Type: application/json' \
  -d '{
    "username": "buyeruser",
    "email": "buyer@test.com",
    "password": "Password123",
    "firstName": "John",
    "lastName": "Buyer",
    "role": "BUYER"
  }')

BUYER_TOKEN=$(echo "$BUYER_RESPONSE" | jq -r '.accessToken')
echo "BUYER Token: $BUYER_TOKEN"

# Step 2: Register SELLER
SELLER_RESPONSE=$(curl -s -X POST 'http://localhost:8080/api/auth/register' \
  -H 'Content-Type: application/json' \
  -d '{
    "username": "selleruser",
    "email": "seller@test.com",
    "password": "Password123",
    "firstName": "Jane",
    "lastName": "Seller",
    "role": "SELLER"
  }')

SELLER_TOKEN=$(echo "$SELLER_RESPONSE" | jq -r '.accessToken')
SELLER_ID=$(echo "$SELLER_RESPONSE" | jq -r '.userId')
echo "SELLER Token: $SELLER_TOKEN"
echo "SELLER ID: $SELLER_ID"

# Step 3: SELLER creates a listing
LISTING_RESPONSE=$(curl -s -X POST 'http://localhost:8080/api/listings' \
  -H 'Content-Type: application/json' \
  -H "Authorization: Bearer $SELLER_TOKEN" \
  -d '{
    "title": "iPhone 13 Pro",
    "description": "Excellent condition, 256GB",
    "category": "ELECTRONICS",
    "price": 800.00,
    "condition": "LIKE_NEW",
    "location": "SJSU Campus"
  }')

LISTING_ID=$(echo "$LISTING_RESPONSE" | jq -r '.listingId')
echo "Listing ID: $LISTING_ID"

# Step 4: BUYER searches for listings
echo "Searching for listings..."
curl -s -X GET 'http://localhost:8080/api/listings/search?category=ELECTRONICS' \
  -H "Authorization: Bearer $BUYER_TOKEN" | jq '.'

# Step 5: BUYER initiates conversation about the listing
CONV_RESPONSE=$(curl -s -X POST "http://localhost:8080/api/conversations?listingId=${LISTING_ID}&sellerId=${SELLER_ID}" \
  -H "Authorization: Bearer $BUYER_TOKEN")

CONVERSATION_ID=$(echo "$CONV_RESPONSE" | jq -r '.conversationId')
echo "Conversation ID: $CONVERSATION_ID"

# Step 6: BUYER sends a message
echo "Buyer sending message..."
curl -s -X POST "http://localhost:8080/api/messages/${CONVERSATION_ID}" \
  -H 'Content-Type: application/json' \
  -H "Authorization: Bearer $BUYER_TOKEN" \
  -d '{
    "content": "Hi! Is this iPhone still available? Can we meet at the library?"
  }' | jq '.'

# Step 7: SELLER views unread messages
echo "Seller checking unread messages..."
curl -s -X GET 'http://localhost:8080/api/messages/unread' \
  -H "Authorization: Bearer $SELLER_TOKEN" | jq '.'

# Step 8: SELLER reads conversation and marks as read
echo "Seller reading conversation..."
curl -s -X GET "http://localhost:8080/api/messages/${CONVERSATION_ID}" \
  -H "Authorization: Bearer $SELLER_TOKEN" | jq '.'

curl -s -X PUT "http://localhost:8080/api/messages/${CONVERSATION_ID}/mark-read" \
  -H "Authorization: Bearer $SELLER_TOKEN"

# Step 9: SELLER replies
echo "Seller replying..."
curl -s -X POST "http://localhost:8080/api/messages/${CONVERSATION_ID}" \
  -H 'Content-Type: application/json' \
  -H "Authorization: Bearer $SELLER_TOKEN" \
  -d '{
    "content": "Yes, its available! Library at 3pm tomorrow works for me."
  }' | jq '.'
```

## Available Endpoints Summary

### Authentication (`/api/auth/*`)
- `POST /auth/register` - Register new user
- `POST /auth/login` - Login
- `POST /auth/refresh` - Refresh token
- `POST /auth/logout` - Logout

### Listings (`/api/listings/*`) - **NEW**
- `POST /listings` - Create listing (SELLER/ADMIN only)
- `GET /listings/search` - Search listings (requires auth)
- `GET /listings/{id}` - Get listing by ID (requires auth)
- `GET /listings/my-listings` - Get current user's listings (SELLER/ADMIN only)
- `PUT /listings/{id}` - Update listing (SELLER/ADMIN only)
- `DELETE /listings/{id}` - Delete listing (SELLER/ADMIN only)
- `PUT /listings/{id}/mark-sold` - Mark as sold (SELLER/ADMIN only)

### Communication (`/api/conversations/*`, `/api/messages/*`) - **NEW**
- `POST /conversations?listingId={id}&sellerId={id}` - Create/get conversation (requires auth)
- `GET /conversations` - Get all user's conversations (requires auth)
- `GET /conversations/{id}` - Get specific conversation (requires auth)
- `DELETE /conversations/{id}` - Delete conversation (requires auth)
- `POST /messages/{conversationId}` - Send message (requires auth)
- `GET /messages/{conversationId}` - Get conversation messages (requires auth)
- `PUT /messages/{conversationId}/mark-read` - Mark messages as read (requires auth)
- `GET /messages/unread` - Get unread messages (requires auth)
- `DELETE /messages/{messageId}` - Delete message (sender only)

### Products (`/api/products/*`) - Existing
- `GET /products` - Get all products
- `GET /products/{id}` - Get product by ID
- `GET /products/search` - Search products

### Users (`/api/users/*`)
- `GET /users/profile` - Get current user
- `PUT /users/profile` - Update profile

## Notes

1. **Role-Based Access Control:**
   - SELLER: Can create, read, update, delete own listings
   - BUYER: Can search and view listings
   - ADMIN: Full access to all operations

2. **Categories:** TEXTBOOKS, GADGETS, ELECTRONICS, STATIONARY, OTHER

3. **Conditions:** NEW, LIKE_NEW, GOOD, USED

4. **Listing Status:** PENDING, ACTIVE, SOLD, CANCELLED

5. All listing endpoints currently require authentication. To make search public, update the security configuration.

## Test Status

✅ Backend compiles successfully
✅ Server starts successfully on port 8080
✅ Auth endpoints working
✅ Listing endpoints created and functional
✅ Communication endpoints created and functional (NEW)
✅ Product endpoints working
✅ User management endpoints working
✅ H2 in-memory database configured for dev profile
⚠️ Note: User ID type mismatch between User (UUID) and Conversation/Message (Long) - temporary workaround in place

## Infrastructure Complete

The following are implemented and ready to use:

### Listing System
- ✅ Listing entities, repositories, services, controllers
- ✅ Search with multiple filters (keyword, category, condition, price, location)
- ✅ Pagination support
- ✅ Role-based authorization (SELLER can create/update/delete own listings)
- ✅ Mark listing as sold functionality

### Communication System (NEW)
- ✅ Conversation entities, repositories, services, controllers
- ✅ Message entities, repositories, services, controllers
- ✅ One conversation per listing-buyer-seller combination
- ✅ Real-time messaging between buyers and sellers
- ✅ Message read/unread tracking
- ✅ Unread message count per conversation
- ✅ Authorization (only conversation participants can view/send messages)

### Supporting Services
- ✅ File storage service (for future image uploads)
- ✅ Email notification service
- ✅ Role-based authorization (SELLER/BUYER/ADMIN)
- ✅ JWT authentication and token management
- ✅ Comprehensive error handling
- ✅ Database auto-schema generation (H2 dev profile)
- ✅ Flyway migrations (PostgreSQL prod profile)
