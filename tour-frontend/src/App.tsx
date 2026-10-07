import { useMemo, useState } from "react";
import { environment } from "./config/env";
import { FetchHttpClient } from "./api/httpClient";
import { TokenProvider } from "./api/tokenProvider";
import { TourApiClient } from "./api/tourApiClient";

/**
 * Minimal app shell proving API layer usage stays outside UI components.
 */
export default function App() {
  const [message, setMessage] = useState<string>("Click to load current edition");

  const apiClient = useMemo(() => {
    const tokenProvider = new TokenProvider(() => null);
    const httpClient = new FetchHttpClient({
      baseUrl: environment.apiBaseUrl,
      tokenProvider
    });

    return new TourApiClient(httpClient);
  }, []);

  const loadCurrentEdition = async () => {
    try {
      const edition = await apiClient.getCurrentEdition();
      setMessage(`Edition ${edition.editionYear} - Stage ${edition.stage}`);
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : "Unknown error";
      setMessage(`Failed to load edition: ${errorMessage}`);
    }
  };

  return (
    <main>
      <h1>Tour Frontend</h1>
      <p>{message}</p>
      <button type="button" onClick={loadCurrentEdition}>Load Current Edition</button>
    </main>
  );
}

