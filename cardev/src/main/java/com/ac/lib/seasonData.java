package com.ac.lib;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

import javax.swing.JFileChooser;

import com.ac.App;
import com.ac.AI.aiAPI;
import com.ac.fileparsing.jsonReader;
import com.ac.fileparsing.jsonWriter;
import com.ac.menus.mainMenu;
import com.ac.seasons.newSeason.seasonSettings;
import com.ac.seasons.newSeason.teamSetup;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class seasonData {
    public ArrayList<teamSetup> loadedTeams;
    public ArrayList<aeroPartInventory[]> loadedInventories;
    public static seasonSettings season = new seasonSettings("", 0, 0, 0, 0, 0, 0f, 0f, 0f, 0f, 1000, 0, 0, 0, 0, false, false, false, false, null, null);
    public boolean unsupportedCarFound = false;

    public void loadSeasonData() {
        season = jsonReader.parseSeasonSettings();
        loadTeamlist();
    }

    /**
     * Loads the team list and their inventories from saved data files.
     */
    private void loadTeamlist() {
        // Initialize empty lists for loaded teams and their inventories
        loadedTeams = new ArrayList<teamSetup>();
        loadedInventories = new ArrayList<aeroPartInventory[]>();
    
        // Get the default directory where save data is stored and append the name of the currently loaded profile to it
        File file = new File(new JFileChooser().getFileSystemView().getDefaultDirectory().toString() + "\\Ac Cardev save data\\" + App.loadedProfile);
    
        // List all directories in this folder using a FilenameFilter that only accepts directories
        String[] directories = file.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
        });
    
        // Convert array of directory names into an ObservableList for use in JavaFX UI components 
       ObservableList<String> teams = FXCollections.observableArrayList(directories);
        
       // Create an ArrayList to hold team names 
       ArrayList<String> teamList = new ArrayList<String>();
    
       // Loop through each team in season object and add its corresponding directory name to our list of teams if it exists  
       for (int i = 0; i < season.getTeamCount(); i++) {
           teamList.add(teams.get(i));
       }
    
       // Loop through each valid team in our list of teams 
       for (int i = 0; i < season.teamCount; i++) {
    
           if (teamList.get(i) != null) { 	// Check if this particular entry is not null
    
               /* Parse JSON files containing information about this particular team. The parseTeam method returns a TeamSetup object,
                  which contains information such as championship points, team name, etc. */
               loadedTeams.add(jsonReader.parseTeam(teamList.get(i)));
    
               /* Check if the facilities for this team exist in our season object. If not, create them using the aiAPI.doFacilitiesExist method.
                  This method takes a TeamSetup object and a Season object as arguments and creates all necessary facilities for that team within
                  the given season. */
               aiAPI.doFacilitiesExist(loadedTeams.get(i), season);
    
               // If the player team has been changed, set its controller to AI and save changes to file 
               if (loadedTeams.get(i).controller.equals("Player Team") && !season.getPlayerTeam().equals(loadedTeams.get(i).getTeamName())) {
                   loadedTeams.get(i).setController("AI");
                   jsonWriter.saveTeam(loadedTeams.get(i));
                }
    
                /* Parse JSON files containing information about aerodynamic parts owned by this particular team.
                   The parseAeroParts method returns an array of AeroPartInventory objects which contain information such as part type,
                   quantity owned, etc.*/
                loadedInventories.add(jsonReader.parseAeroParts(loadedTeams.get(i)));
    
                // Check if any advanced CSP physics are present in this inventory
                checkForCSPPhysics(i);
    
                // Print message indicating successful loading of current team 
                System.out.println("Team: [" + loadedTeams.get(i).getTeamName() + "] loaded");
    
            } else { 	// If entry is null print error message 
                 System.out.println("Target team is null: [" + teamList.get(i) + "]");
            }
        }
    }

    /** 
      * This method checks if each team's car folder contains "rss_formula_hybrid_2022" and whether its inventory at index 0 has a part name equal to "Front Wing Gap". 
      * It then displays either a warning message indicating which teams have an unsupported car or continues to the team window.
     */
    public void checkForCSPPhysics(int i) {
        // Check if car is RSS 2022 and has unsupported physics
        if (loadedTeams.get(i).getCarFolder().contains("rss_formula_hybrid_2022")) {
            if (loadedInventories.get(i)[0].part_name.equals("Front Wing Gap")) {
                mainMenu.unsupportedCars += (mainMenu.unsupportedCars.isEmpty() ? "" : ", ") + loadedTeams.get(i).getTeamName();
                unsupportedCarFound = true;
                System.out.println("Unsupported part found in " + loadedTeams.get(i).getTeamName() + "'s car: " + loadedInventories.get(i)[0].part_name);
            }
        }

        // If any unsupported cars were found, display a warning message
        if (unsupportedCarFound) {
            System.out.println("The following teams have an unsupported car: " + mainMenu.unsupportedCars);
        } else {
            System.out.println("All cars are supported.");
        }
    }
}
