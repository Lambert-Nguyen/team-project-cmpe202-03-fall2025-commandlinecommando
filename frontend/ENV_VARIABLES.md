# Frontend Environment Variables Guide

This document describes all environment variables used in the frontend application.

## Environment Variable Names

All environment variables in Vite must be prefixed with `VITE_` to be exposed to the frontend code.

### Available Variables

| Variable Name | Description | Default Value | Used In |
|---------------|-------------|---------------|---------|
| `VITE_BACKEND_API_BASE_URL` | Main backend API for authentication and user management | `http://54.193.178.118:8080/api` | [config.ts](src/api/config.ts), [client.ts](src/api/client.ts) |
| `VITE_LISTING_API_URL` | Listing service API for marketplace listings | `http://54.193.178.118:8100/api` | [config.ts](src/api/config.ts) |
| `VITE_COMMUNICATION_URL` | Communication service API for messaging | `http://54.193.178.118:8200/api` | [config.ts](src/api/config.ts) |
| `VITE_AI_API_SERVICE_URL` | AI integration service for chat functionality | `http://localhost:3001` | [config.ts](src/api/config.ts), [vite.config.ts](vite.config.ts) |

## Configuration Files

### `.env` (Local Development - Gitignored)
Your personal local configuration. This file is **not** committed to git.

```bash
# Backend API Base URL (Main authentication and user service)
VITE_BACKEND_API_BASE_URL=http://localhost:8080/api

# Listing API URL (Marketplace listings service)
VITE_LISTING_API_URL=http://localhost:8100/api

# Communication API URL (Messaging service)
VITE_COMMUNICATION_URL=http://localhost:8200/api

# AI Integration Service URL (AI chat service)
VITE_AI_API_SERVICE_URL=http://localhost:3001
```

### `.env.example` (Template - Committed)
Template file for other developers to know what variables are available.

### `.env.local.example` (Additional Reference - Committed)
Detailed example with comments for local development setup.

## How Environment Variables Are Used

### 1. Vite Proxy Configuration ([vite.config.ts](vite.config.ts))
The AI service endpoint is proxied through Vite's dev server:

```typescript
proxy: {
  '/api': {
    target: env.VITE_AI_API_SERVICE_URL || 'http://localhost:3001',
    changeOrigin: true,
  },
}
```

This means:
- Frontend calls `/api/chat`
- Vite proxies to `${VITE_AI_API_SERVICE_URL}/api/chat`
- Default: `http://localhost:3001/api/chat`

### 2. API Configuration ([src/api/config.ts](src/api/config.ts))
All API endpoints are centralized in the config:

```typescript
export const API_CONFIG = {
  BACKEND_URL: import.meta.env.VITE_BACKEND_API_BASE_URL || 'http://54.193.178.118:8080/api',
  LISTING_API_URL: import.meta.env.VITE_LISTING_API_URL || 'http://54.193.178.118:8100/api',
  COMMUNICATION_URL: import.meta.env.VITE_COMMUNICATION_URL || 'http://54.193.178.118:8200/api',
  AI_SERVICE_URL: import.meta.env.VITE_AI_API_SERVICE_URL || 'http://localhost:3001',
};
```

### 3. Axios Client ([src/api/client.ts](src/api/client.ts))
The main axios instance uses the backend API base URL:

```typescript
const baseURL = import.meta.env.VITE_BACKEND_API_BASE_URL || 'http://localhost:8080/api'
```

## Setup Instructions

### First Time Setup
```bash
# 1. Navigate to frontend directory
cd frontend

# 2. Copy example environment file
cp .env.example .env

# 3. Edit .env with your local URLs (if different from defaults)
nano .env  # or vim, or any editor
```

### Running the Application
```bash
# Start the development server
npm run dev

# The server will read .env automatically
# Changes to .env require restarting the dev server
```

## Different Environments

### Local Development
Use `.env` with localhost URLs:
```bash
VITE_BACKEND_API_BASE_URL=http://localhost:8080/api
VITE_AI_API_SERVICE_URL=http://localhost:3001
```

### Production
Set environment variables in your deployment platform or use a production `.env` file:
```bash
VITE_BACKEND_API_BASE_URL=https://api.yourapp.com/api
VITE_LISTING_API_URL=https://listing-api.yourapp.com/api
VITE_COMMUNICATION_URL=https://comm-api.yourapp.com/api
VITE_AI_API_SERVICE_URL=https://ai.yourapp.com
```

### Docker
Environment variables can be passed via docker-compose:
```yaml
environment:
  - VITE_BACKEND_API_BASE_URL=http://backend:8080/api
  - VITE_AI_API_SERVICE_URL=http://ai-service:3001
```

## Troubleshooting

### Changes Not Reflected
**Problem:** Updated `.env` but changes aren't working.

**Solution:** Restart the Vite dev server:
```bash
# Stop the server (Ctrl+C)
# Then restart
npm run dev
```

### Variable Shows as Undefined
**Problem:** `import.meta.env.VITE_MY_VAR` is undefined.

**Solutions:**
1. Make sure variable name starts with `VITE_`
2. Restart dev server after adding new variables
3. Check that `.env` file is in the `frontend/` directory
4. Verify there are no syntax errors in `.env`

### AI Service Not Connecting
**Problem:** Frontend can't connect to AI service.

**Solutions:**
1. Check `VITE_AI_API_SERVICE_URL` in `.env`
2. Verify AI service is running on the configured port
3. Check browser console for CORS errors
4. Ensure vite.config.ts proxy is configured correctly

## Migration Notes

### Old Variable Names → New Variable Names
If updating from an older version:

| Old Name | New Name |
|----------|----------|
| `VITE_API_BASE_URL` | `VITE_BACKEND_API_BASE_URL` |
| `VITE_AI_SERVICE_URL` | `VITE_AI_API_SERVICE_URL` |

Update your `.env` file accordingly and restart the dev server.

## Security Notes

1. **Never commit `.env` files** - They're gitignored by default
2. **Don't put secrets in environment variables exposed to frontend** - They're visible in browser
3. **Use backend APIs to protect sensitive operations** - Frontend env vars are public
4. **API keys should only be in backend services** - Never in VITE_ variables

## References

- [Vite Environment Variables Documentation](https://vitejs.dev/guide/env-and-mode.html)
- [API Configuration](src/api/config.ts)
- [Vite Config](vite.config.ts)
