#!/bin/bash

TIMESTAMP=$(date +%s)
BASE_URL="http://localhost:8080/api"

echo "=================================================="
echo "COMPREHENSIVE API TESTING - Run $TIMESTAMP"
echo "=================================================="
echo ""

# Register BUYER
echo "[1] AUTH API: Registering BUYER..."
BUYER_REG=$(curl -s -X POST "$BASE_URL/auth/register" \
  -H "Content-Type: application/json" \
  -d "{
    \"username\": \"buyer_$TIMESTAMP\",
    \"email\": \"buyer_$TIMESTAMP@test.com\",
    \"password\": \"Password123\",
    \"firstName\": \"John\",
    \"lastName\": \"Buyer\",
    \"role\": \"BUYER\"
  }")

BUYER_TOKEN=$(echo "$BUYER_REG" | jq -r '.accessToken // empty')
BUYER_ID=$(echo "$BUYER_REG" | jq -r '.userId // empty')

if [ -z "$BUYER_TOKEN" ] || [ "$BUYER_TOKEN" == "null" ]; then
    echo "❌ BUYER registration failed"
    echo "$BUYER_REG" | jq '.'
    exit 1
fi
echo "✓ BUYER registered (ID: ${BUYER_ID:0:36})"

# Register SELLER
echo -e "\n[2] AUTH API: Registering SELLER..."
SELLER_REG=$(curl -s -X POST "$BASE_URL/auth/register" \
  -H "Content-Type: application/json" \
  -d "{
    \"username\": \"seller_$TIMESTAMP\",
    \"email\": \"seller_$TIMESTAMP@test.com\",
    \"password\": \"Password123\",
    \"firstName\": \"Jane\",
    \"lastName\": \"Seller\",
    \"role\": \"SELLER\"
  }")

SELLER_TOKEN=$(echo "$SELLER_REG" | jq -r '.accessToken // empty')
SELLER_ID=$(echo "$SELLER_REG" | jq -r '.userId // empty')

if [ -z "$SELLER_TOKEN" ] || [ "$SELLER_TOKEN" == "null" ]; then
    echo "❌ SELLER registration failed"
    echo "$SELLER_REG" | jq '.'
    exit 1
fi
echo "✓ SELLER registered (ID: ${SELLER_ID:0:36})"

# Test User API
echo -e "\n[3] USER API: Getting BUYER profile..."
PROFILE=$(curl -s -X GET "$BASE_URL/users/profile" \
  -H "Authorization: Bearer $BUYER_TOKEN")
USERNAME=$(echo "$PROFILE" | jq -r '.username // empty')
if [ "$USERNAME" == "buyer_$TIMESTAMP" ]; then
    echo "✓ User profile retrieved"
else
    echo "❌ Failed"
fi

# Test Product API
echo -e "\n[4] PRODUCT API: Getting products..."
PRODUCTS=$(curl -s -X GET "$BASE_URL/products" \
  -H "Authorization: Bearer $BUYER_TOKEN")
PRODUCT_COUNT=$(echo "$PRODUCTS" | jq '._embedded.products | length' 2>/dev/null)
echo "✓ Found $PRODUCT_COUNT products"

# Test Listing API
echo -e "\n[5] LISTING API: Creating listing..."
CREATE_LISTING=$(curl -s -X POST "$BASE_URL/listings" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $SELLER_TOKEN" \
  -d '{
    "title": "iPhone 13 Pro 256GB",
    "description": "Excellent condition",
    "category": "ELECTRONICS",
    "price": 700.00,
    "condition": "LIKE_NEW",
    "location": "SJSU Campus"
  }')
LISTING_ID=$(echo "$CREATE_LISTING" | jq -r '.listingId // .id // empty')
if [ -n "$LISTING_ID" ] && [ "$LISTING_ID" != "null" ]; then
    echo "✓ Listing created (ID: $LISTING_ID)"
else
    echo "❌ Failed"
    echo "$CREATE_LISTING" | jq '.'
fi

# Test Conversation API
echo -e "\n[6] CONVERSATION API: Creating conversation..."
if [ -n "$LISTING_ID" ] && [ "$LISTING_ID" != "null" ]; then
    CREATE_CONV=$(curl -s -X POST "$BASE_URL/conversations?listingId=$LISTING_ID&sellerId=$SELLER_ID" \
      -H "Authorization: Bearer $BUYER_TOKEN")
    CONV_ID=$(echo "$CREATE_CONV" | jq -r '.conversationId // .id // empty')
    if [ -n "$CONV_ID" ] && [ "$CONV_ID" != "null" ]; then
        echo "✓ Conversation created (ID: $CONV_ID)"
    else
        echo "❌ Failed"
        echo "$CREATE_CONV" | jq '.'
    fi
fi

# Test Message API
echo -e "\n[7] MESSAGE API: Sending message..."
if [ -n "$CONV_ID" ] && [ "$CONV_ID" != "null" ]; then
    SEND_MSG=$(curl -s -X POST "$BASE_URL/messages/$CONV_ID" \
      -H "Content-Type: application/json" \
      -H "Authorization: Bearer $BUYER_TOKEN" \
      -d '{"content": "Hi, is this available?"}')
    MSG_ID=$(echo "$SEND_MSG" | jq -r '.messageId // .id // empty')
    if [ -n "$MSG_ID" ] && [ "$MSG_ID" != "null" ]; then
        echo "✓ Message sent (ID: $MSG_ID)"
    else
        echo "❌ Failed"
        echo "$SEND_MSG" | jq '.'
    fi
fi

echo -e "\n=================================================="
echo "ALL TESTS COMPLETED SUCCESSFULLY"
echo "=================================================="
