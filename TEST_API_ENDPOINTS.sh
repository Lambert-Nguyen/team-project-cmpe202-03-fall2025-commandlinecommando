#!/bin/bash

# Campus Marketplace API Testing Script
# Tests all endpoints including Auth, Users, Products, Listings, and Communication

BASE_URL="http://localhost:8080/api"
ADMIN_TOKEN=""
SELLER_TOKEN=""
BUYER_TOKEN=""

echo "=================================================="
echo "Campus Marketplace API Testing"
echo "=================================================="
echo ""

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

function test_endpoint() {
    local method=$1
    local endpoint=$2
    local data=$3
    local token=$4
    local description=$5

    echo -e "${YELLOW}Testing: $description${NC}"
    echo "Endpoint: $method $endpoint"

    if [ -n "$token" ]; then
        if [ -n "$data" ]; then
            response=$(curl -s -w "\n%{http_code}" -X $method "$BASE_URL$endpoint" \
                -H "Content-Type: application/json" \
                -H "Authorization: Bearer $token" \
                -d "$data")
        else
            response=$(curl -s -w "\n%{http_code}" -X $method "$BASE_URL$endpoint" \
                -H "Authorization: Bearer $token")
        fi
    else
        if [ -n "$data" ]; then
            response=$(curl -s -w "\n%{http_code}" -X $method "$BASE_URL$endpoint" \
                -H "Content-Type: application/json" \
                -d "$data")
        else
            response=$(curl -s -w "\n%{http_code}" -X $method "$BASE_URL$endpoint")
        fi
    fi

    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | sed '$d')

    if [ "$http_code" -ge 200 ] && [ "$http_code" -lt 300 ]; then
        echo -e "${GREEN}✓ SUCCESS (HTTP $http_code)${NC}"
    else
        echo -e "${RED}✗ FAILED (HTTP $http_code)${NC}"
    fi

    echo "Response: $body" | jq '.' 2>/dev/null || echo "Response: $body"
    echo ""
}

# ============================================
# 1. AUTHENTICATION TESTS
# ============================================
echo "=================================================="
echo "1. AUTHENTICATION & AUTHORIZATION"
echo "=================================================="
echo ""

# Register a BUYER user
test_endpoint "POST" "/auth/register" '{
    "username": "buyer1",
    "email": "buyer@test.com",
    "password": "password123",
    "firstName": "John",
    "lastName": "Buyer",
    "role": "BUYER"
}' "" "Register BUYER user"

# Register a SELLER user
test_endpoint "POST" "/auth/register" '{
    "username": "seller1",
    "email": "seller@test.com",
    "password": "password123",
    "firstName": "Jane",
    "lastName": "Seller",
    "role": "SELLER"
}' "" "Register SELLER user"

# Login as BUYER
echo -e "${YELLOW}Logging in as BUYER...${NC}"
login_response=$(curl -s -X POST "$BASE_URL/auth/login" \
    -H "Content-Type: application/json" \
    -d '{
        "username": "buyer1",
        "password": "password123"
    }')
BUYER_TOKEN=$(echo $login_response | jq -r '.accessToken' 2>/dev/null)
echo "BUYER Token obtained: ${BUYER_TOKEN:0:50}..."
echo ""

# Login as SELLER
echo -e "${YELLOW}Logging in as SELLER...${NC}"
login_response=$(curl -s -X POST "$BASE_URL/auth/login" \
    -H "Content-Type: application/json" \
    -d '{
        "username": "seller1",
        "password": "password123"
    }')
SELLER_TOKEN=$(echo $login_response | jq -r '.accessToken' 2>/dev/null)
echo "SELLER Token obtained: ${SELLER_TOKEN:0:50}..."
echo ""

# ============================================
# 2. LISTING TESTS (NEW ENDPOINTS)
# ============================================
echo "=================================================="
echo "2. LISTING API (NEW)"
echo "=================================================="
echo ""

# Create a listing (SELLER only)
test_endpoint "POST" "/listings" '{
    "title": "MacBook Pro 2021",
    "description": "Like new MacBook Pro M1, 16GB RAM, 512GB SSD",
    "category": "ELECTRONICS",
    "price": 1200.00,
    "condition": "LIKE_NEW",
    "location": "San Jose State University"
}' "$SELLER_TOKEN" "Create listing (SELLER)"

# Try to create listing as BUYER (should fail)
test_endpoint "POST" "/listings" '{
    "title": "Test Product",
    "description": "Should fail",
    "category": "TEXTBOOKS",
    "price": 50.00,
    "condition": "GOOD",
    "location": "SJSU"
}' "$BUYER_TOKEN" "Create listing (BUYER - should fail)"

# Search all listings
test_endpoint "GET" "/listings/search?page=0&size=10" "" "" "Search all listings (no auth required)"

# Search with filters
test_endpoint "GET" "/listings/search?category=ELECTRONICS&minPrice=1000&maxPrice=2000" "" "" "Search listings with filters"

# Get specific listing
test_endpoint "GET" "/listings/1" "" "" "Get listing by ID (no auth required)"

# Get my listings (SELLER)
test_endpoint "GET" "/listings/my-listings" "" "$SELLER_TOKEN" "Get my listings (SELLER)"

# Update listing (SELLER)
test_endpoint "PUT" "/listings/1" '{
    "title": "MacBook Pro 2021 - Price Reduced!",
    "description": "Like new MacBook Pro M1, 16GB RAM, 512GB SSD - Must sell fast!",
    "category": "ELECTRONICS",
    "price": 1100.00,
    "condition": "LIKE_NEW",
    "location": "San Jose State University"
}' "$SELLER_TOKEN" "Update listing (SELLER)"

# Mark as sold
test_endpoint "PUT" "/listings/1/mark-sold" "" "$SELLER_TOKEN" "Mark listing as sold (SELLER)"

# ============================================
# 3. EXISTING PRODUCT API TESTS
# ============================================
echo "=================================================="
echo "3. EXISTING PRODUCT API"
echo "=================================================="
echo ""

# Get all products
test_endpoint "GET" "/products" "" "$BUYER_TOKEN" "Get all products"

# Search products
test_endpoint "GET" "/products/search?query=laptop&page=0&size=10" "" "$BUYER_TOKEN" "Search products"

# ============================================
# 4. USER MANAGEMENT TESTS
# ============================================
echo "=================================================="
echo "4. USER MANAGEMENT"
echo "=================================================="
echo ""

# Get current user
test_endpoint "GET" "/users/profile" "" "$BUYER_TOKEN" "Get current user profile"

# Update profile
test_endpoint "PUT" "/users/profile" '{
    "email": "buyer.updated@test.com",
    "firstName": "John",
    "lastName": "Buyer Updated"
}' "$BUYER_TOKEN" "Update user profile"

# ============================================
# 5. SUMMARY
# ============================================
echo "=================================================="
echo "TEST SUMMARY"
echo "=================================================="
echo ""
echo -e "${GREEN}Testing complete!${NC}"
echo ""
echo "Available Endpoints:"
echo "  Auth:"
echo "    POST   /auth/register"
echo "    POST   /auth/login"
echo "    POST   /auth/refresh"
echo "    POST   /auth/logout"
echo ""
echo "  Listings (NEW):"
echo "    POST   /listings                    (SELLER only)"
echo "    GET    /listings/{id}               (public)"
echo "    GET    /listings/search             (public)"
echo "    GET    /listings/my-listings        (SELLER only)"
echo "    PUT    /listings/{id}               (SELLER only)"
echo "    DELETE /listings/{id}               (SELLER only)"
echo "    PUT    /listings/{id}/mark-sold     (SELLER only)"
echo ""
echo "  Products:"
echo "    GET    /products"
echo "    GET    /products/{id}"
echo "    GET    /products/search"
echo ""
echo "  Users:"
echo "    GET    /users/profile"
echo "    PUT    /users/profile"
echo ""
echo "Note: Communication endpoints (conversations/messages) can be added similarly"
echo ""
