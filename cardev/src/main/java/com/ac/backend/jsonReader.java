package com.ac.backend;

import java.io.BufferedReader;
import java.io.FileReader;

import javax.swing.JFileChooser;

import com.ac.App;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class jsonReader extends App{

    public seasonSettings parseSeasonSettings() {
        seasonSettings season = new seasonSettings(
            "",
            0,
            0,
            0,
            0,
            0,
            0f,
            0f,
            0f,
            0f,
            1000,
            0,
            0,
            0,
            false,
            false,
            false,
            false
        );
        try {
            String pathToFile = new JFileChooser().getFileSystemView().getDefaultDirectory().toString() + "\\Ac Cardev save data\\" + loadedProfile;
            String path = pathToFile + "\\season_settings.json";
            BufferedReader bufferedReader = new BufferedReader(new FileReader(path));
    
            Gson gson = new Gson();
            JsonObject js = gson.fromJson(bufferedReader, JsonObject.class);
    
            season = new seasonSettings(
                js.get("profileName").getAsString(),
                Integer.parseInt(js.get("teamCount").getAsString()),
                Integer.parseInt(js.get("raceCount").getAsString()),
                Integer.parseInt(js.get("raceLength").getAsString()),
                Integer.parseInt(js.get("fuelTankSize").getAsString()),
                Integer.parseInt(js.get("totalPrizePool").getAsString()),
                Float.parseFloat(js.get("maxDownforce").getAsString()),
                Float.parseFloat(js.get("minDownforce").getAsString()),
                Float.parseFloat(js.get("minDrag").getAsString()),
                Float.parseFloat(js.get("maxDrag").getAsString()),
                1000,
                Integer.parseInt(js.get("difficulty").getAsString()),
                Integer.parseInt(js.get("raceRewards").getAsString()),
                Integer.parseInt(js.get("currentRace").getAsString()),
                js.get("equalDev").getAsBoolean(),
                js.get("equalFunds").getAsBoolean(),
                js.get("raceCanceled").getAsBoolean(),
                js.get("preseasonTestingCompleted").getAsBoolean()
            );
            System.out.println("Loaded profile: [" + season.profileName + "]");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return season;
    }

    public teamSetup parseTeam(String targetTeam) {
        // System.out.println("Loading team: [" + targetTeam + "] from profile: [" + loadedProfile + "]");
        teamSetup team = new teamSetup(
            "",
            "",
            "",
            "",
            "",
            0,
            0,
            0,
            0
        );
        try {
            String pathToFile = new JFileChooser().getFileSystemView().getDefaultDirectory().toString() + "\\Ac Cardev save data\\" + loadedProfile + "\\" + targetTeam + "\\team.json";
            BufferedReader bufferedReader = new BufferedReader(new FileReader(pathToFile));
    
            Gson gson = new Gson();
            JsonObject js = gson.fromJson(bufferedReader, JsonObject.class);
    
            team = new teamSetup(
                js.get("profileName").getAsString(),
                js.get("controller").getAsString(),
                js.get("teamName").getAsString(),
                js.get("teamColor").getAsString(),
                js.get("carFolder").getAsString(),
                Integer.parseInt(js.get("money").getAsString()),
                Integer.parseInt(js.get("researchLevel").getAsString()),
                Integer.parseInt(js.get("points").getAsString()),
                Integer.parseInt(js.get("champPos").getAsString())
            );
            System.out.println("Loaded team: [" + team.getTeamName() + "]");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return team;
    }
}
