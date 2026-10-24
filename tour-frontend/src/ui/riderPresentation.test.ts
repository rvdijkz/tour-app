import { describe, expect, it } from "vitest";
import {
  DEFAULT_RIDER_FILTERS,
  filterRiders,
  formatRiderValue,
  getNationalityOptions
} from "./riderPresentation";

const riders = [
  {
    riderId: 1,
    gameRiderNumber: 10,
    raceBibNumber: 101,
    name: "Alex Rider",
    value: 22,
    nationality: "NLD",
    withdrawn: false
  },
  {
    riderId: 2,
    gameRiderNumber: 11,
    raceBibNumber: 102,
    name: "Bernard Climber",
    value: 17,
    nationality: "FRA",
    withdrawn: true
  },
  {
    riderId: 3,
    gameRiderNumber: 12,
    raceBibNumber: 103,
    name: "Chris Sprint",
    value: 30,
    nationality: "NLD",
    withdrawn: false
  }
];

describe("riderPresentation", () => {
  it("formats rider values", () => {
    expect(formatRiderValue(42)).toBe("42 pts");
  });

  it("returns sorted unique nationality options", () => {
    expect(getNationalityOptions(riders)).toEqual(["FRA", "NLD"]);
  });

  it("filters riders by text, nationality, and withdrawn flag", () => {
    const filtered = filterRiders(riders, {
      searchQuery: "alex",
      nationality: "NLD",
      includeWithdrawn: false
    });

    expect(filtered).toHaveLength(1);
    expect(filtered[0].name).toBe("Alex Rider");
  });

  it("keeps all riders with default filters", () => {
    expect(filterRiders(riders, DEFAULT_RIDER_FILTERS)).toHaveLength(3);
  });
});

