package com.ac.fileparsing;

import java.io.BufferedReader;
import java.io.FileReader;
import javax.swing.JFileChooser;

import com.ac.App;
import com.ac.lib.aeroPartInventory;
import com.ac.menus.facilities.teamFacilities;
import com.ac.seasons.newSeason.seasonSettings;
import com.ac.seasons.newSeason.teamSetup;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

public class jsonReader extends App{

    public static seasonSettings parseSeasonSettings() {
        seasonSettings season = null;
        try {
            String pathToFile = new JFileChooser().getFileSystemView().getDefaultDirectory().toString() + "\\Ac Cardev save data\\" + App.loadedProfile + "\\season_settings.json";
            BufferedReader bufferedReader = new BufferedReader(new FileReader(pathToFile));
    
            Gson gson = new Gson();

            season = gson.fromJson(bufferedReader, seasonSettings.class);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return season;
    }

    public static float[] jsonArrayToFloatArray(JsonArray array) {
        float[] floatArray = new float[array.size()];
        System.out.println(array.size());
        if (array.size() > 0) {
            for (int i = 0; i < array.size(); i++) {
                if(array.get(i) != null) {
                    floatArray[i] =  array.get(i).getAsFloat();
                }
            }
        }
        return floatArray;
    }

    public static int[] jsonArrayToIntArray(JsonArray array) {
        int[] intArray = new int[array.size()];
        System.out.println(array.get(0));
        if (array.size() > 0) {
            for (int i = 0; i < array.size(); i++) {
                if(array.get(i) != null) {
                    intArray[i] = array.get(i).getAsInt();
                }
            }
        }
        return intArray;
    }

    public static teamSetup parseTeam(String targetTeam) {
        // System.out.println("Loading team: [" + targetTeam + "] from profile: [" + loadedProfile + "]");
        teamSetup team = null;
        try {
            String pathToFile = new JFileChooser().getFileSystemView().getDefaultDirectory().toString() + "\\Ac Cardev save data\\" + loadedProfile + "\\" + targetTeam +  "\\team.json";
            BufferedReader bufferedReader = new BufferedReader(new FileReader(pathToFile));
    
            Gson gson = new Gson();

            team = gson.fromJson(bufferedReader, teamSetup.class);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return team;
    }

    public static teamFacilities parseFacilities(teamSetup targetTeam) {
        // System.out.println("Loading team: [" + targetTeam + "] from profile: [" + loadedProfile + "]");
        teamFacilities facilities = null;
        try {
            String pathToFile = new JFileChooser().getFileSystemView().getDefaultDirectory().toString() + "\\Ac Cardev save data\\" + loadedProfile + "\\" + targetTeam.getTeamName() +  "\\facilities.json";
            BufferedReader bufferedReader = new BufferedReader(new FileReader(pathToFile));
    
            Gson gson = new Gson();

            facilities = gson.fromJson(bufferedReader, teamFacilities.class);
            System.out.println("resDev xp: [" + facilities.resDev.experience + "] resdev level: [" + facilities.resDev.level + "]");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return facilities;
    }

    public static aeroPartInventory[] parseAeroParts(teamSetup targetTeam) {
        // System.out.println("Loading team: [" + targetTeam + "] from profile: [" + loadedProfile + "]");
        aeroPartInventory[] inventory = null;
        try {
            String pathToFile = new JFileChooser().getFileSystemView().getDefaultDirectory().toString() + "\\Ac Cardev save data\\" + loadedProfile + "\\" + targetTeam.getTeamName() +  "\\part Inventory\\aero parts.json";
            BufferedReader bufferedReader = new BufferedReader(new FileReader(pathToFile));
    
            Gson gson = new Gson();

            inventory = gson.fromJson(bufferedReader, aeroPartInventory[].class);
            System.out.println("Loaded parts: [" + inventory.length + "]");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return inventory;
    }
}
