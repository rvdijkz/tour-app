/**
 * Frontend environment contract for API connectivity.
 */
export interface FrontendEnvironment {
  apiBaseUrl: string;
}

const DEFAULT_API_BASE_URL = "http://localhost:8080";

/**
 * Reading environment values in one place keeps the app and API layer consistent.
 */
export const environment: FrontendEnvironment = {
  apiBaseUrl: import.meta.env.VITE_API_BASE_URL ?? DEFAULT_API_BASE_URL
};

