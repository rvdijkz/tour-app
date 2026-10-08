import { describe, expect, it, vi } from "vitest";
import { FetchHttpClient } from "./httpClient";
import { TokenProvider } from "./tokenProvider";

describe("FetchHttpClient", () => {
  it("adds bearer token when token provider returns one", async () => {
    const fetchMock = vi.fn().mockResolvedValue({
      ok: true,
      json: async () => ({ status: "UP" })
    });

    vi.stubGlobal("fetch", fetchMock);

    const client = new FetchHttpClient({
      baseUrl: "http://localhost:8080",
      tokenProvider: new TokenProvider(() => "sample-token")
    });

    await client.request<{ status: string }>("/health");

    const [, requestInit] = fetchMock.mock.calls[0] as [string, RequestInit];
    const headers = requestInit.headers as Headers;

    expect(headers.get("Authorization")).toBe("Bearer sample-token");
    vi.unstubAllGlobals();
  });

  it("does not add authorization header when token is missing", async () => {
    const fetchMock = vi.fn().mockResolvedValue({
      ok: true,
      json: async () => ({ status: "UP" })
    });

    vi.stubGlobal("fetch", fetchMock);

    const client = new FetchHttpClient({
      baseUrl: "http://localhost:8080",
      tokenProvider: new TokenProvider(() => null)
    });

    await client.request<{ status: string }>("/health");

    const [, requestInit] = fetchMock.mock.calls[0] as [string, RequestInit];
    const headers = requestInit.headers as Headers;

    expect(headers.get("Authorization")).toBeNull();
    vi.unstubAllGlobals();
  });
});

