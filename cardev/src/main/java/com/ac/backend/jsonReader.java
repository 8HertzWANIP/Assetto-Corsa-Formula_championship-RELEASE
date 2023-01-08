package com.ac.backend;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import javax.swing.JFileChooser;

import com.ac.App;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class jsonReader extends App{

    public static seasonSettings parseSeasonSettings() {
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
            0f,
            0,
            0,
            0,
            false,
            false,
            false,
            false,
            null,
            null
        );
        File directory = new File(new JFileChooser().getFileSystemView().getDefaultDirectory().toString() + "\\Ac Cardev save data\\"+loadedProfile);

        try {
            String pathToFile = new JFileChooser().getFileSystemView().getDefaultDirectory().toString() + "\\Ac Cardev save data\\" + loadedProfile;
            String path = pathToFile + "\\season_settings.json";
            BufferedReader bufferedReader = new BufferedReader(new FileReader(path));
    
            Gson gson = new Gson();
            JsonObject js = gson.fromJson(bufferedReader, JsonObject.class);

            
            season = jsonUpdater.updateSeasonSettings(js, true);
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (directory.exists()) {
                System.out.println("File name: " + directory.getName());
                System.out.println("Absolute path: " + directory.getAbsolutePath());
                System.out.println("Writeable: " + directory.canWrite());
                System.out.println("Readable " + directory.canRead());
                System.out.println("File size in bytes " + directory.length());
            } else {
                System.out.println("The file does not exist.");
            }
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
