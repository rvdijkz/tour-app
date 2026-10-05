package com.rvdijkz.tour.application;

import java.time.Duration;

public final class EntryRules {

    public static final int TEAM_SIZE = 14;
    public static final int STANDARD_TEAM_LAST_POSITION = 10;
    public static final int NATIONALITY_POSITION = 11;
    public static final int RESERVE_ONE_POSITION = 12;
    public static final int RESERVE_TWO_POSITION = 13;
    public static final int KLUNS_POSITION = 14;
    public static final int MAIN_TEAM_BUDGET = 100;
    public static final int PREDICTION_LIST_SIZE = 5;
    public static final int CLASSIFICATION_COUNT = 3;
    public static final int PASSWORD_VALIDITY_HOURS = 12;
    public static final Duration PASSWORD_VALIDITY = Duration.ofHours(PASSWORD_VALIDITY_HOURS);

    private EntryRules() {
    }
}
