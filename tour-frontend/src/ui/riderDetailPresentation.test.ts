import { describe, expect, it } from "vitest";
import { buildRiderStageScorePreview } from "./riderDetailPresentation";
import type { PlayerStandingDetail } from "../api/types";

const playerDetails: PlayerStandingDetail[] = [
  {
    editionId: 1,
    stage: 5,
    player: {
      rank: 1,
      playerId: 100,
      playerName: "Player One",
      teamName: "Alpha",
      totalPoints: 130
    },
    stageHistory: [
      {
        stage: 5,
        stagePoints: 15,
        totalPoints: 130,
        substitutions: [],
        riderScores: [
          {
            position: 3,
            rider: {
              riderId: 7,
              riderName: "Rider Seven",
              raceBibNumber: 77
            },
            substitute: false,
            points: 8,
            scoreItems: [
              {
                ruleCode: "STAGE_PLACING",
                description: "Stage placing",
                points: 6
              },
              {
                ruleCode: "BONUS",
                description: "Bonus",
                points: 2
              },
              {
                ruleCode: "EXTRA",
                description: "Extra",
                points: 1
              }
            ]
          }
        ]
      }
    ]
  },
  {
    editionId: 1,
    stage: 5,
    player: {
      rank: 2,
      playerId: 101,
      playerName: "Player Two",
      teamName: "Beta",
      totalPoints: 120
    },
    stageHistory: [
      {
        stage: 4,
        stagePoints: 10,
        totalPoints: 112,
        substitutions: [],
        riderScores: [
          {
            position: 5,
            rider: {
              riderId: 7,
              riderName: "Rider Seven",
              raceBibNumber: 77
            },
            substitute: true,
            points: 4,
            scoreItems: []
          }
        ]
      }
    ]
  }
];

describe("riderDetailPresentation", () => {
  it("extracts preview rows for a rider and sorts by stage desc", () => {
    const preview = buildRiderStageScorePreview(playerDetails, 7);

    expect(preview).toHaveLength(2);
    expect(preview[0].stage).toBe(5);
    expect(preview[0].playerId).toBe(100);
    expect(preview[0].playerName).toBe("Player One");
    expect(preview[1].stage).toBe(4);
    expect(preview[1].playerName).toBe("Player Two");
  });

  it("limits contribution output", () => {
    const preview = buildRiderStageScorePreview(playerDetails, 7, 1);

    expect(preview[0].contributions).toEqual(["STAGE_PLACING — Stage placing (+6)"]);
  });

  it("returns empty when rider is absent", () => {
    expect(buildRiderStageScorePreview(playerDetails, 999)).toEqual([]);
  });
});


