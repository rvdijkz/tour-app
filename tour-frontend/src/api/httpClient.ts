import { TokenProvider } from "./tokenProvider";

/**
 * Request parameters accepted by the small HTTP client wrapper.
 */
export interface RequestOptions {
  method?: "GET" | "POST" | "PUT" | "PATCH" | "DELETE";
  body?: unknown;
  headers?: Record<string, string>;
}

/**
 * Narrow HTTP client contract used by the API service layer.
 */
export interface HttpClient {
  request<TResponse>(path: string, options?: RequestOptions): Promise<TResponse>;
}

export interface HttpClientDependencies {
  baseUrl: string;
  tokenProvider: TokenProvider;
}

export class FetchHttpClient implements HttpClient {
  private readonly baseUrl: string;
  private readonly tokenProvider: TokenProvider;

  constructor(dependencies: HttpClientDependencies) {
    this.baseUrl = dependencies.baseUrl;
    this.tokenProvider = dependencies.tokenProvider;
  }

  async request<TResponse>(path: string, options: RequestOptions = {}): Promise<TResponse> {
    const token = this.tokenProvider.getToken();
    const headers = new Headers(options.headers ?? {});

    headers.set("Accept", "application/json");

    if (options.body !== undefined) {
      headers.set("Content-Type", "application/json");
    }

    if (token !== null && token.trim() !== "") {
      headers.set("Authorization", `Bearer ${token}`);
    }

    const response = await fetch(`${this.baseUrl}${path}`, {
      method: options.method ?? "GET",
      headers,
      body: options.body === undefined ? undefined : JSON.stringify(options.body)
    });

    if (!response.ok) {
      throw new Error(`API request failed: ${response.status} ${response.statusText}`);
    }

    return response.json() as Promise<TResponse>;
  }
}

