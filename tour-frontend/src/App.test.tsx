import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { cleanup, fireEvent, render, screen, waitFor, within } from "@testing-library/react";
import App from "./App";
import { TourApiClient } from "./api/tourApiClient";

function renderAdminView(): void {
  window.history.replaceState(null, "", "/?view=admin");
  render(<App />);
}

function getInputByLabel(labelText: string): HTMLInputElement {
  return screen.getByLabelText(labelText, { selector: "input" }) as HTMLInputElement;
}

function setInputValue(labelText: string, value: string): void {
  const input = getInputByLabel(labelText);
  fireEvent.change(input, { target: { value } });
}

describe("App admin MVP flows", () => {
  beforeEach(() => {
    vi.restoreAllMocks();
    window.localStorage.clear();
    window.history.replaceState(null, "", "/");
  });

  afterEach(() => {
    cleanup();
  });

  it("shows a validation message for invalid create edition year", async () => {
    const createEditionSpy = vi.spyOn(TourApiClient.prototype, "createEdition");

    renderAdminView();

    setInputValue("Edition year", "2019");
    fireEvent.click(screen.getByRole("button", { name: "Create edition" }));

    expect(createEditionSpy).not.toHaveBeenCalled();
    const validationMessage = await screen.findByText("Edition year must be an integer from 2020 onward.");
    const createEditionButton = screen.getByRole("button", { name: "Create edition" });
    const createEditionCard = screen.getByRole("heading", { name: "Create edition" }).closest("section");
    expect(createEditionCard).not.toBeNull();
    expect(within(createEditionCard as HTMLElement).getByText(validationMessage.textContent ?? "")).toBeDefined();
    expect(validationMessage.getAttribute("id")).toBe("admin-feedback-createEdition");
    expect(createEditionButton.getAttribute("aria-describedby")).toContain("admin-feedback-createEdition");
  });

  it("creates an edition and stores the returned edition id in target field", async () => {
    vi.spyOn(TourApiClient.prototype, "createEdition").mockResolvedValue({
      editionId: 11,
      year: 2028,
      createdAt: "2026-11-22T09:00:00Z",
      cleared: {
        teams: true,
        predictions: true,
        stageResults: true,
        standings: true
      }
    });

    renderAdminView();

    setInputValue("Edition year", "2028");
    fireEvent.click(screen.getByRole("button", { name: "Create edition" }));

    expect(await screen.findByText("Edition 11 created for year 2028.")).toBeDefined();
    await waitFor(() => {
      expect(getInputByLabel("Edition id").value).toBe("11");
    });
  });

  it("shows loading state while create edition request is in flight", async () => {
    let resolveCreateEdition!: (value: {
      editionId: number;
      year: number;
      createdAt: string;
      cleared: {
        teams: boolean;
        predictions: boolean;
        stageResults: boolean;
        standings: boolean;
      };
    }) => void;

    vi.spyOn(TourApiClient.prototype, "createEdition").mockImplementation(
      () =>
        new Promise((resolve) => {
          resolveCreateEdition = resolve;
        })
    );

    renderAdminView();

    setInputValue("Edition year", "2029");
    const createEditionButton = screen.getByRole("button", { name: "Create edition" });
    fireEvent.click(createEditionButton);

    await waitFor(() => {
      expect(createEditionButton.textContent).toBe("Saving...");
      expect(createEditionButton).toHaveProperty("disabled", true);
    });

    expect(screen.getAllByRole("button", { name: "Saving..." })).toHaveLength(1);
    expect(screen.getByRole("button", { name: "Save reserve budget" })).toHaveProperty("disabled", true);

    resolveCreateEdition({
      editionId: 12,
      year: 2029,
      createdAt: "2026-11-22T10:00:00Z",
      cleared: {
        teams: true,
        predictions: true,
        stageResults: true,
        standings: true
      }
    });

    expect(await screen.findByText("Edition 12 created for year 2029.")).toBeDefined();
  });

  it("loads admin context and pre-fills editable fields from current edition", async () => {
    const getCurrentEditionSpy = vi
      .spyOn(TourApiClient.prototype, "getCurrentEdition")
      .mockResolvedValueOnce({
        editionId: 9,
        year: 2027,
        entryDeadline: "2027-06-01T12:34:00Z",
        reserveBudget: 18,
        requiredNationality: "FRA",
        currentStage: 4,
        doublePointsStages: [4, 9]
      })
      .mockResolvedValueOnce({
        editionId: 9,
        year: 2027,
        entryDeadline: "2027-06-02T12:34:00Z",
        reserveBudget: 21,
        requiredNationality: "ESP",
        currentStage: 5,
        doublePointsStages: [5, 10]
      });

    renderAdminView();

    fireEvent.click(screen.getByRole("button", { name: "Load Admin Context" }));

    await waitFor(() => {
      expect(getCurrentEditionSpy).toHaveBeenCalledTimes(1);
      expect(getInputByLabel("Edition id").value).toBe("9");
      expect(getInputByLabel("Reserve budget").value).toBe("18");
      expect(getInputByLabel("3-letter code").value).toBe("FRA");
      expect(getInputByLabel("Comma-separated stages").value).toBe("4,9");
      expect(getInputByLabel("Deadline (local)").value).not.toBe("");
      expect(screen.getByRole("button", { name: "Save reserve budget" })).toHaveProperty("disabled", true);
      expect(screen.getByRole("button", { name: "Save reserve budget" }).getAttribute("aria-describedby")).toContain(
        "admin-hint-reserveBudget"
      );
      expect(screen.getByRole("region", { name: "Create edition" })).toBeDefined();
      expect(screen.getByRole("region", { name: "Reserve budget" })).toBeDefined();
    });

    expect(screen.getAllByText("No changes to save.")).toHaveLength(4);

    expect(screen.getByText("Edition 2027 - Stage 4")).toBeDefined();
    expect(screen.getByText("#9 (2027)")).toBeDefined();
    expect(screen.getByText("4, 9")).toBeDefined();
    expect(screen.queryByRole("button", { name: "Reset reserve budget" })).toBeNull();

    setInputValue("Reserve budget", "19");
    expect(screen.getByRole("button", { name: "Save reserve budget" })).toHaveProperty("disabled", false);
    expect(screen.getByRole("button", { name: "Save reserve budget" }).getAttribute("aria-describedby")).toBeNull();
    const reserveBudgetCard = screen.getByRole("heading", { name: "Reserve budget" }).closest("section");
    expect(reserveBudgetCard).not.toBeNull();
    expect(within(reserveBudgetCard as HTMLElement).queryByText("No changes to save.")).toBeNull();
    expect(screen.getByRole("button", { name: "Reset reserve budget" })).toBeDefined();
    fireEvent.click(screen.getByRole("button", { name: "Reset reserve budget" }));
    expect(getInputByLabel("Reserve budget").value).toBe("18");
    expect(screen.getByRole("button", { name: "Save reserve budget" })).toHaveProperty("disabled", true);
    expect(within(reserveBudgetCard as HTMLElement).getByText("No changes to save.")).toBeDefined();
    expect(screen.queryByRole("button", { name: "Reset reserve budget" })).toBeNull();

    setInputValue("Comma-separated stages", "9,4");
    expect(screen.queryByRole("button", { name: "Reset stages" })).toBeNull();

    fireEvent.click(screen.getByRole("button", { name: "Refresh context" }));

    await waitFor(() => {
      expect(getCurrentEditionSpy).toHaveBeenCalledTimes(2);
      expect(screen.getByText("Edition 2027 - Stage 5")).toBeDefined();
      expect(getInputByLabel("Reserve budget").value).toBe("21");
      expect(getInputByLabel("3-letter code").value).toBe("ESP");
      expect(getInputByLabel("Comma-separated stages").value).toBe("5,10");
    });
  });

  it("normalizes required nationality payload to trimmed uppercase code", async () => {
    const requiredNationalitySpy = vi.spyOn(TourApiClient.prototype, "updateRequiredNationality").mockResolvedValue({
      editionId: 7,
      requiredNationality: "BEL",
      locked: false
    });

    renderAdminView();

    setInputValue("Edition id", "7");
    setInputValue("3-letter code", " bel ");
    fireEvent.click(screen.getByRole("button", { name: "Save required nationality" }));

    await waitFor(() => {
      expect(requiredNationalitySpy).toHaveBeenCalledWith(7, { requiredNationality: "BEL" });
    });
    expect(await screen.findByText("Required nationality set to BEL for edition 7.")).toBeDefined();
    expect(getInputByLabel("3-letter code").value).toBe("BEL");
  });

  it("normalizes double points stages payload by deduplicating and sorting", async () => {
    const stagesSpy = vi.spyOn(TourApiClient.prototype, "updateDoublePointsStages").mockResolvedValue({
      editionId: 5,
      doublePointsStages: [1, 3, 12],
      locked: false
    });

    renderAdminView();

    setInputValue("Edition id", "5");
    setInputValue("Comma-separated stages", "12, 3, 12, 1");
    fireEvent.click(screen.getByRole("button", { name: "Save double points stages" }));

    await waitFor(() => {
      expect(stagesSpy).toHaveBeenCalledWith(5, { doublePointsStages: [1, 3, 12] });
    });
    expect(await screen.findByText("Double points stages updated for edition 5.")).toBeDefined();
    expect(getInputByLabel("Comma-separated stages").value).toBe("1,3,12");
  });

  it("shows error message when edition id is invalid for admin save actions", async () => {
    const reserveBudgetSpy = vi.spyOn(TourApiClient.prototype, "updateReserveBudget");

    renderAdminView();

    setInputValue("Edition id", "0");
    fireEvent.click(screen.getByRole("button", { name: "Save reserve budget" }));

    expect(reserveBudgetSpy).not.toHaveBeenCalled();
    expect(await screen.findByText("Enter a valid edition id.")).toBeDefined();
  });

  it("shows API error message for failed reserve budget update", async () => {
    vi.spyOn(TourApiClient.prototype, "updateReserveBudget").mockRejectedValue(new Error("backend unavailable"));

    renderAdminView();

    setInputValue("Edition id", "3");
    setInputValue("Reserve budget", "42");
    fireEvent.click(screen.getByRole("button", { name: "Save reserve budget" }));

    const failureMessage = await screen.findByText("Update reserve budget failed: backend unavailable");
    const reserveBudgetCard = screen.getByRole("heading", { name: "Reserve budget" }).closest("section");
    const createEditionCard = screen.getByRole("heading", { name: "Create edition" }).closest("section");
    expect(reserveBudgetCard).not.toBeNull();
    expect(createEditionCard).not.toBeNull();
    expect(within(reserveBudgetCard as HTMLElement).getByText(failureMessage.textContent ?? "")).toBeDefined();
    expect(within(createEditionCard as HTMLElement).queryByText("Update reserve budget failed: backend unavailable")).toBeNull();
    expect(failureMessage.getAttribute("role")).toBe("status");
    expect(failureMessage.getAttribute("aria-live")).toBe("polite");
    expect(failureMessage.getAttribute("id")).toBe("admin-feedback-reserveBudget");
    expect(screen.getByRole("button", { name: "Save reserve budget" }).getAttribute("aria-describedby")).toContain(
      "admin-feedback-reserveBudget"
    );
  });

  it("auto-clears success feedback messages after a short timeout", async () => {
    vi.spyOn(TourApiClient.prototype, "createEdition").mockResolvedValue({
      editionId: 15,
      year: 2030,
      createdAt: "2026-11-22T11:00:00Z",
      cleared: {
        teams: true,
        predictions: true,
        stageResults: true,
        standings: true
      }
    });

    renderAdminView();

    setInputValue("Edition year", "2030");
    fireEvent.click(screen.getByRole("button", { name: "Create edition" }));

    expect(await screen.findByText("Edition 15 created for year 2030.")).toBeDefined();

    await new Promise((resolve) => {
      window.setTimeout(resolve, 3100);
    });

    await waitFor(() => {
      expect(screen.queryByText("Edition 15 created for year 2030.")).toBeNull();
    });
  });

  it("keeps error feedback visible over time", async () => {
    vi.useFakeTimers();
    try {
      renderAdminView();

      setInputValue("Edition id", "0");
      fireEvent.click(screen.getByRole("button", { name: "Save reserve budget" }));

      await Promise.resolve();
      expect(screen.getByText("Enter a valid edition id.")).toBeDefined();

      vi.advanceTimersByTime(5000);
      expect(screen.getByText("Enter a valid edition id.")).toBeDefined();
    } finally {
      vi.useRealTimers();
    }
  });
});

