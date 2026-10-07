/**
 * Callback that returns a bearer token when a session is available.
 */
export type AccessTokenProvider = () => string | null;

/**
 * Mutable token provider to keep API code decoupled from auth implementation details.
 */
export class TokenProvider {
  private readonly readToken: AccessTokenProvider;

  constructor(readToken: AccessTokenProvider) {
    this.readToken = readToken;
  }

  getToken(): string | null {
    return this.readToken();
  }
}

