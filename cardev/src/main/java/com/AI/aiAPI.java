package com.AI;

import com.ac.backend.seasonSettings;

public interface aiAPI {
    public static int getCycleIndex(int[] cycle, seasonSettings season) {
        int length = cycle.length;
        int currentRace = season.getCurrentRace();
        while(currentRace >= length) {
            currentRace -= length;
        }
        return currentRace;
    }
}
