export const API_CONFIG = {
  USE_MOCK_API: false,  // Disable mock API to use real backend
  
  BACKEND_URL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api',
  // LISTING_API_URL and COMMUNICATION_URL removed - using unified backend
  
  TIMEOUT: 5000,
};

export function isUsingMockApi(): boolean {
  return API_CONFIG.USE_MOCK_API;
}

export function toggleMockApi(useMock: boolean): void {
  API_CONFIG.USE_MOCK_API = useMock;
}
