#!/bin/bash

# Campus Marketplace - Comprehensive API Testing Script
# Tests ALL endpoints including Auth, Users, Products, Listings, Conversations, and Messages

BASE_URL="http://localhost:8080/api"

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

echo "=================================================="
echo "CAMPUS MARKETPLACE - COMPREHENSIVE API TEST"
echo "=================================================="
echo ""

# Test counter
TOTAL_TESTS=0
PASSED_TESTS=0
FAILED_TESTS=0

test_api() {
    TOTAL_TESTS=$((TOTAL_TESTS + 1))
    local test_name="$1"
    local http_code="$2"

    echo -e "${YELLOW}[TEST $TOTAL_TESTS] $test_name${NC}"

    if [ "$http_code" -ge 200 ] && [ "$http_code" -lt 300 ]; then
        echo -e "${GREEN}✓ PASSED (HTTP $http_code)${NC}\n"
        PASSED_TESTS=$((PASSED_TESTS + 1))
    else
        echo -e "${RED}✗ FAILED (HTTP $http_code)${NC}\n"
        FAILED_TESTS=$((FAILED_TESTS + 1))
    fi
}

echo "=================================================="
echo "1. AUTHENTICATION API"
echo "=================================================="

# Register BUYER
echo -e "\n${BLUE}Registering BUYER user...${NC}"
BUYER_REGISTER=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "buyer_test",
    "email": "buyer_test@example.com",
    "password": "Password123",
    "firstName": "John",
    "lastName": "Buyer",
    "role": "BUYER"
  }')
BUYER_HTTP_CODE=$(echo "$BUYER_REGISTER" | tail -n1)
BUYER_BODY=$(echo "$BUYER_REGISTER" | sed '$d')
echo "$BUYER_BODY" | jq '.'
test_api "Register BUYER user" "$BUYER_HTTP_CODE"

BUYER_TOKEN=$(echo $BUYER_BODY | jq -r '.accessToken')
BUYER_ID=$(echo $BUYER_BODY | jq -r '.userId')

# Register SELLER
echo -e "${BLUE}Registering SELLER user...${NC}"
SELLER_REGISTER=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "seller_test",
    "email": "seller_test@example.com",
    "password": "Password123",
    "firstName": "Jane",
    "lastName": "Seller",
    "role": "SELLER"
  }')
SELLER_HTTP_CODE=$(echo "$SELLER_REGISTER" | tail -n1)
SELLER_BODY=$(echo "$SELLER_REGISTER" | sed '$d')
echo "$SELLER_BODY" | jq '.'
test_api "Register SELLER user" "$SELLER_HTTP_CODE"

SELLER_TOKEN=$(echo $SELLER_BODY | jq -r '.accessToken')
SELLER_ID=$(echo $SELLER_BODY | jq -r '.userId')

echo -e "${GREEN}✓ BUYER Token obtained${NC}"
echo -e "${GREEN}✓ SELLER Token obtained${NC}"
echo -e "${GREEN}✓ BUYER ID: $BUYER_ID${NC}"
echo -e "${GREEN}✓ SELLER ID: $SELLER_ID${NC}\n"

# Login
echo -e "${BLUE}Testing login...${NC}"
LOGIN=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "buyer_test",
    "password": "Password123"
  }')
LOGIN_HTTP_CODE=$(echo "$LOGIN" | tail -n1)
echo "$LOGIN" | sed '$d' | jq '.'
test_api "Login with credentials" "$LOGIN_HTTP_CODE"

echo "=================================================="
echo "2. USER MANAGEMENT API"
echo "=================================================="

# Get profile
echo -e "\n${BLUE}Getting user profile...${NC}"
PROFILE=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/users/profile" \
  -H "Authorization: Bearer $BUYER_TOKEN")
PROFILE_HTTP_CODE=$(echo "$PROFILE" | tail -n1)
echo "$PROFILE" | sed '$d' | jq '.'
test_api "Get current user profile" "$PROFILE_HTTP_CODE"

# Update profile
echo -e "${BLUE}Updating user profile...${NC}"
UPDATE_PROFILE=$(curl -s -w "\n%{http_code}" -X PUT "$BASE_URL/users/profile" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $BUYER_TOKEN" \
  -d '{
    "email": "buyer_updated@example.com",
    "firstName": "John",
    "lastName": "Buyer Updated"
  }')
UPDATE_HTTP_CODE=$(echo "$UPDATE_PROFILE" | tail -n1)
echo "$UPDATE_PROFILE" | sed '$d' | jq '.'
test_api "Update user profile" "$UPDATE_HTTP_CODE"

echo "=================================================="
echo "3. LISTING API"
echo "=================================================="

# Create listing (SELLER)
echo -e "\n${BLUE}Creating listing as SELLER...${NC}"
CREATE_LISTING=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/listings" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $SELLER_TOKEN" \
  -d '{
    "title": "MacBook Pro 2021 M1",
    "description": "Like new MacBook Pro M1, 16GB RAM, 512GB SSD",
    "category": "ELECTRONICS",
    "price": 1200.00,
    "condition": "LIKE_NEW",
    "location": "San Jose State University"
  }')
CREATE_LISTING_HTTP_CODE=$(echo "$CREATE_LISTING" | tail -n1)
CREATE_LISTING_BODY=$(echo "$CREATE_LISTING" | sed '$d')
echo "$CREATE_LISTING_BODY" | jq '.'
test_api "Create listing (SELLER)" "$CREATE_LISTING_HTTP_CODE"

LISTING_ID=$(echo $CREATE_LISTING_BODY | jq -r '.listingId // .id // empty')
echo -e "${GREEN}✓ Listing ID: $LISTING_ID${NC}\n"

# Search listings
echo -e "${BLUE}Searching all listings...${NC}"
SEARCH=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/listings/search?page=0&size=10" \
  -H "Authorization: Bearer $BUYER_TOKEN")
SEARCH_HTTP_CODE=$(echo "$SEARCH" | tail -n1)
echo "$SEARCH" | sed '$d' | jq '.content[] | {id: .listingId, title, price, status}' 2>/dev/null || echo "$SEARCH" | sed '$d'
test_api "Search all listings" "$SEARCH_HTTP_CODE"

# Get listing by ID
if [ -n "$LISTING_ID" ] && [ "$LISTING_ID" != "null" ]; then
    echo -e "${BLUE}Getting listing by ID...${NC}"
    GET_LISTING=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/listings/$LISTING_ID" \
      -H "Authorization: Bearer $BUYER_TOKEN")
    GET_LISTING_HTTP_CODE=$(echo "$GET_LISTING" | tail -n1)
    echo "$GET_LISTING" | sed '$d' | jq '.'
    test_api "Get listing by ID" "$GET_LISTING_HTTP_CODE"
fi

# Get my listings
echo -e "${BLUE}Getting SELLER's listings...${NC}"
MY_LISTINGS=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/listings/my-listings" \
  -H "Authorization: Bearer $SELLER_TOKEN")
MY_LISTINGS_HTTP_CODE=$(echo "$MY_LISTINGS" | tail -n1)
echo "$MY_LISTINGS" | sed '$d' | jq '.[] | {id: .listingId, title, status}' 2>/dev/null || echo "$MY_LISTINGS" | sed '$d'
test_api "Get my listings (SELLER)" "$MY_LISTINGS_HTTP_CODE"

# Update listing
if [ -n "$LISTING_ID" ] && [ "$LISTING_ID" != "null" ]; then
    echo -e "${BLUE}Updating listing...${NC}"
    UPDATE_LISTING=$(curl -s -w "\n%{http_code}" -X PUT "$BASE_URL/listings/$LISTING_ID" \
      -H "Content-Type: application/json" \
      -H "Authorization: Bearer $SELLER_TOKEN" \
      -d '{
        "title": "MacBook Pro 2021 M1 - Price Reduced!",
        "description": "Like new MacBook Pro M1 - Must sell fast!",
        "category": "ELECTRONICS",
        "price": 1100.00,
        "condition": "LIKE_NEW",
        "location": "San Jose State University"
      }')
    UPDATE_LISTING_HTTP_CODE=$(echo "$UPDATE_LISTING" | tail -n1)
    echo "$UPDATE_LISTING" | sed '$d' | jq '.'
    test_api "Update listing (SELLER)" "$UPDATE_LISTING_HTTP_CODE"
fi

echo "=================================================="
echo "4. CONVERSATION API"
echo "=================================================="

# Create conversation
if [ -n "$LISTING_ID" ] && [ "$LISTING_ID" != "null" ]; then
    echo -e "\n${BLUE}Creating conversation about listing...${NC}"
    CREATE_CONV=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/conversations?listingId=$LISTING_ID&sellerId=$SELLER_ID" \
      -H "Authorization: Bearer $BUYER_TOKEN")
    CREATE_CONV_HTTP_CODE=$(echo "$CREATE_CONV" | tail -n1)
    CREATE_CONV_BODY=$(echo "$CREATE_CONV" | sed '$d')
    echo "$CREATE_CONV_BODY" | jq '.'
    test_api "Create conversation" "$CREATE_CONV_HTTP_CODE"

    CONVERSATION_ID=$(echo $CREATE_CONV_BODY | jq -r '.conversationId // .id // empty')
    echo -e "${GREEN}✓ Conversation ID: $CONVERSATION_ID${NC}\n"
fi

# Get all conversations
echo -e "${BLUE}Getting all conversations...${NC}"
GET_CONVS=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/conversations" \
  -H "Authorization: Bearer $BUYER_TOKEN")
GET_CONVS_HTTP_CODE=$(echo "$GET_CONVS" | tail -n1)
echo "$GET_CONVS" | sed '$d' | jq '.' 2>/dev/null || echo "$GET_CONVS" | sed '$d'
test_api "Get all conversations" "$GET_CONVS_HTTP_CODE"

# Get specific conversation
if [ -n "$CONVERSATION_ID" ] && [ "$CONVERSATION_ID" != "null" ]; then
    echo -e "${BLUE}Getting specific conversation...${NC}"
    GET_CONV=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/conversations/$CONVERSATION_ID" \
      -H "Authorization: Bearer $BUYER_TOKEN")
    GET_CONV_HTTP_CODE=$(echo "$GET_CONV" | tail -n1)
    echo "$GET_CONV" | sed '$d' | jq '.'
    test_api "Get conversation by ID" "$GET_CONV_HTTP_CODE"
fi

echo "=================================================="
echo "5. MESSAGE API"
echo "=================================================="

# Send message
if [ -n "$CONVERSATION_ID" ] && [ "$CONVERSATION_ID" != "null" ]; then
    echo -e "\n${BLUE}Sending message (BUYER)...${NC}"
    SEND_MSG=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/messages/$CONVERSATION_ID" \
      -H "Content-Type: application/json" \
      -H "Authorization: Bearer $BUYER_TOKEN" \
      -d '{
        "content": "Hi! Is this MacBook still available?"
      }')
    SEND_MSG_HTTP_CODE=$(echo "$SEND_MSG" | tail -n1)
    SEND_MSG_BODY=$(echo "$SEND_MSG" | sed '$d')
    echo "$SEND_MSG_BODY" | jq '.'
    test_api "Send message (BUYER)" "$SEND_MSG_HTTP_CODE"

    MESSAGE_ID=$(echo $SEND_MSG_BODY | jq -r '.messageId // .id // empty')
fi

# Get messages in conversation
if [ -n "$CONVERSATION_ID" ] && [ "$CONVERSATION_ID" != "null" ]; then
    echo -e "${BLUE}Getting messages in conversation...${NC}"
    GET_MSGS=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/messages/$CONVERSATION_ID" \
      -H "Authorization: Bearer $SELLER_TOKEN")
    GET_MSGS_HTTP_CODE=$(echo "$GET_MSGS" | tail -n1)
    echo "$GET_MSGS" | sed '$d' | jq '.'
    test_api "Get conversation messages" "$GET_MSGS_HTTP_CODE"
fi

# Get unread messages
echo -e "${BLUE}Getting unread messages (SELLER)...${NC}"
GET_UNREAD=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/messages/unread" \
  -H "Authorization: Bearer $SELLER_TOKEN")
GET_UNREAD_HTTP_CODE=$(echo "$GET_UNREAD" | tail -n1)
echo "$GET_UNREAD" | sed '$d' | jq '.'
test_api "Get unread messages" "$GET_UNREAD_HTTP_CODE"

# Mark messages as read
if [ -n "$CONVERSATION_ID" ] && [ "$CONVERSATION_ID" != "null" ]; then
    echo -e "${BLUE}Marking messages as read...${NC}"
    MARK_READ=$(curl -s -w "\n%{http_code}" -X PUT "$BASE_URL/messages/$CONVERSATION_ID/mark-read" \
      -H "Authorization: Bearer $SELLER_TOKEN")
    MARK_READ_HTTP_CODE=$(echo "$MARK_READ" | tail -n1)
    test_api "Mark messages as read" "$MARK_READ_HTTP_CODE"
fi

echo "=================================================="
echo "6. PRODUCT API"
echo "=================================================="

# Get all products
echo -e "\n${BLUE}Getting all products...${NC}"
GET_PRODUCTS=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/products" \
  -H "Authorization: Bearer $BUYER_TOKEN")
GET_PRODUCTS_HTTP_CODE=$(echo "$GET_PRODUCTS" | tail -n1)
echo "$GET_PRODUCTS" | sed '$d' | jq 'if type=="array" then .[] | {id, title, price} else . end' 2>/dev/null || echo "$GET_PRODUCTS" | sed '$d'
test_api "Get all products" "$GET_PRODUCTS_HTTP_CODE"

# Search products
echo -e "${BLUE}Searching products...${NC}"
SEARCH_PRODUCTS=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/products/search?query=laptop&page=0&size=10" \
  -H "Authorization: Bearer $BUYER_TOKEN")
SEARCH_PRODUCTS_HTTP_CODE=$(echo "$SEARCH_PRODUCTS" | tail -n1)
echo "$SEARCH_PRODUCTS" | sed '$d' | jq '.' 2>/dev/null || echo "$SEARCH_PRODUCTS" | sed '$d'
test_api "Search products" "$SEARCH_PRODUCTS_HTTP_CODE"

echo ""
echo "=================================================="
echo "TEST SUMMARY"
echo "=================================================="
echo -e "${BLUE}Total Tests:${NC} $TOTAL_TESTS"
echo -e "${GREEN}Passed:${NC} $PASSED_TESTS"
echo -e "${RED}Failed:${NC} $FAILED_TESTS"
echo ""

if [ $FAILED_TESTS -eq 0 ]; then
    echo -e "${GREEN}✓ ALL TESTS PASSED!${NC}"
    exit 0
else
    echo -e "${RED}✗ SOME TESTS FAILED${NC}"
    exit 1
fi
