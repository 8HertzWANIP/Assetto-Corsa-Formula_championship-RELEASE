package com.ac.fileparsing;

import com.ac.App;
import com.ac.seasons.newSeason.seasonSettings;
import com.google.gson.JsonObject;

// Updates any outdated json savefiles and fills in any missing variables due to app updates
public interface jsonUpdater {
    /** Updates the season_settings.json file. Used for app updates.
     * @param season old outdated seasonSettings
     * @return updated seasonSettings object
     */
    public static seasonSettings updateSeasonSettingsNew(seasonSettings season) {
        seasonSettings newSeasonSettings = null;
        //ALPHA-v0.1.1 -> v0.1.2
        if (season.getSeasonPointAwards() == null && season.getSeasonPrizeAwards() == null) {
            season.setSeasonPointAwards(new int[]{25, 18, 15, 12, 10, 8, 6, 4, 2, 1});
            season.setSeasonPrizeAwards(new float[]{0.19f, 0.16f, 0.13f, 0.11f, 0.10f, 0.09f, 0.07f, 0.06f, 0.05f, 0.04f});
        }
        //ALPHA-v0.1.2 -> v0.2
        if (season.regulations == null) {
            season.addSeasonRegs();
        }
        jsonWriter.saveSeasonSettings(season);
        return newSeasonSettings;
    }
}
