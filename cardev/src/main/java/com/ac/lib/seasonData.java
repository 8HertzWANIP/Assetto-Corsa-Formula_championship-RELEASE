package com.ac.lib;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

import javax.swing.JFileChooser;

import com.ac.App;
import com.ac.AI.aiAPI;
import com.ac.fileparsing.jsonReader;
import com.ac.fileparsing.jsonWriter;
import com.ac.seasons.newSeason.seasonSettings;
import com.ac.seasons.newSeason.teamSetup;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class seasonData {
    public ArrayList<teamSetup> loadedTeams;
    public ArrayList<aeroPartInventory[]> loadedInventories;
    public static seasonSettings season = new seasonSettings("", 0, 0, 0, 0, 0, 0f, 0f, 0f, 0f, 1000, 0, 0, 0, 0, false, false, false, false, null, null);

    public void loadSeasonData() {
        season = jsonReader.parseSeasonSettings();
        loadTeamlist();
    }

    private  void loadTeamlist() {
        loadedTeams = new ArrayList<teamSetup>();
        loadedInventories = new ArrayList<aeroPartInventory[]>();
        File file = new File(new JFileChooser().getFileSystemView().getDefaultDirectory().toString() + "\\Ac Cardev save data\\" + App.loadedProfile);
        String[] directories = file.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
        });
        ObservableList<String> teams = FXCollections.observableArrayList(directories);
        
        ArrayList<String> teamList = new ArrayList<String>();
        for (int i = 0; i < season.getTeamCount(); i++) {
            teamList.add(teams.get(i));
        }

        for (int i = 0; i < season.teamCount; i++) {
            if (teamList.get(i) != null) {
                loadedTeams.add(jsonReader.parseTeam(teamList.get(i)));
                aiAPI.doFacilitiesExist(loadedTeams.get(i), season);
                if (loadedTeams.get(i).controller.equals("Player Team") && !season.getPlayerTeam().equals(loadedTeams.get(i).getTeamName())) {
                    loadedTeams.get(i).setController("AI");
                    jsonWriter.saveTeam(loadedTeams.get(i));
                }
                loadedInventories.add(jsonReader.parseAeroParts(loadedTeams.get(i)));
                System.out.println("Team: [" + loadedTeams.get(i).getTeamName() + "] loaded");
            } else {
                System.out.println("Target team is null: [" + teamList.get(i) + "]");
            }
        }
    }
}
