package com.ac.backend;

import com.ac.App;
import com.ac.backend.seasonSettings;
import com.google.gson.JsonObject;

// Updates any outdated json savefiles and fills in any missing variables due to app updates
public interface jsonUpdater {

    /** Updates the season_settings.json file. Used for app updates.
     * @param seasonJObject JsonObject of the season_settings.json
     * @param saveJson save to json file
     * @return seasonSettings object
     */
    static seasonSettings updateSeasonSettings(JsonObject seasonJObject, boolean saveJson) {
        seasonSettings season = new seasonSettings(null, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, saveJson, saveJson, saveJson, saveJson, null, null);
        //ALPHA-v0.1.2
        if (seasonJObject.get("participationPrizeRate") == null && seasonJObject.get("seasonPointAwards") == null && seasonJObject.get("seasonPrizeAwards") == null) {
            season = new seasonSettings(
            seasonJObject.get("profileName").getAsString(),
            Integer.parseInt(seasonJObject.get("teamCount").getAsString()),
            Integer.parseInt(seasonJObject.get("raceCount").getAsString()),
            Integer.parseInt(seasonJObject.get("raceLength").getAsString()),
            Integer.parseInt(seasonJObject.get("fuelTankSize").getAsString()),
            Integer.parseInt(seasonJObject.get("totalPrizePool").getAsString()),
            Float.parseFloat(seasonJObject.get("maxDownforce").getAsString()),
            Float.parseFloat(seasonJObject.get("minDownforce").getAsString()),
            Float.parseFloat(seasonJObject.get("minDrag").getAsString()),
            Float.parseFloat(seasonJObject.get("maxDrag").getAsString()),
            1000,
            0.5f,
            Integer.parseInt(seasonJObject.get("difficulty").getAsString()),
            Integer.parseInt(seasonJObject.get("raceRewards").getAsString()),
            Integer.parseInt(seasonJObject.get("currentRace").getAsString()),
            seasonJObject.get("equalDev").getAsBoolean(),
            seasonJObject.get("equalFunds").getAsBoolean(),
            seasonJObject.get("raceCanceled").getAsBoolean(),
            seasonJObject.get("preseasonTestingCompleted").getAsBoolean(),
            new int[]{25, 18, 15, 12, 10, 8, 6, 4, 2, 1},
            new float[]{0.19f, 0.16f, 0.13f, 0.11f, 0.10f, 0.09f, 0.07f, 0.06f, 0.05f, 0.04f}
            );
            if (saveJson)
                jsonWriter.saveSeasonSettings(season);
        } else {
            season = new seasonSettings(
            seasonJObject.get("profileName").getAsString(),
            Integer.parseInt(seasonJObject.get("teamCount").getAsString()),
            Integer.parseInt(seasonJObject.get("raceCount").getAsString()),
            Integer.parseInt(seasonJObject.get("raceLength").getAsString()),
            Integer.parseInt(seasonJObject.get("fuelTankSize").getAsString()),
            Integer.parseInt(seasonJObject.get("totalPrizePool").getAsString()),
            Float.parseFloat(seasonJObject.get("maxDownforce").getAsString()),
            Float.parseFloat(seasonJObject.get("minDownforce").getAsString()),
            Float.parseFloat(seasonJObject.get("minDrag").getAsString()),
            Float.parseFloat(seasonJObject.get("maxDrag").getAsString()),
            1000,
            Float.parseFloat(seasonJObject.get("participationPrizeRate").getAsString()),
            Integer.parseInt(seasonJObject.get("difficulty").getAsString()),
            Integer.parseInt(seasonJObject.get("raceRewards").getAsString()),
            Integer.parseInt(seasonJObject.get("currentRace").getAsString()),
            seasonJObject.get("equalDev").getAsBoolean(),
            seasonJObject.get("equalFunds").getAsBoolean(),
            seasonJObject.get("raceCanceled").getAsBoolean(),
            seasonJObject.get("preseasonTestingCompleted").getAsBoolean(),
            jsonReader.jsonArrayToIntArray(seasonJObject.get("seasonPointAwards").getAsJsonArray()),
            jsonReader.jsonArrayToFloatArray(seasonJObject.get("seasonPrizeAwards").getAsJsonArray())
        );
        
        }
        return season;
    }
}
/** 
            System.out.println("Season profile: " + season.profileName);
            if (Float.isNaN(season.getParticipationPrizeRate())) {
            System.out.println("getParticipationPrizeRate is null, setting default");
            season.setParticipationPrizeRate(0.5f);
        }
        if (season.getSeasonPointAwards() == null) {
            System.out.println("getSeasonPointAwards is null, setting default");
            int[] defaultPointAwards = {25, 18, 15, 12, 10, 8, 6, 4, 2, 1};
            season.setSeasonPointAwards(defaultPointAwards);
        }
        if (season.getSeasonPrizeAwards() == null) {
            System.out.println("getSeasonPrizeAwards is null, setting default");
            float[] defaultPrizeAwards = {0.19f, 0.16f, 0.13f, 0.11f, 0.10f, 0.09f, 0.07f, 0.06f, 0.05f, 0.04f};
            season.setSeasonPrizeAwards(defaultPrizeAwards);
        }
 */
