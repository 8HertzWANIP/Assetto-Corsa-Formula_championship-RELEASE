package com.ac.AI;

import java.util.ArrayList;
import java.util.Objects;

import com.ac.AI.v02.ai_02_focus;
import com.ac.AI.v02.ai_02_wide;
import com.ac.fileparsing.fileReader;
import com.ac.fileparsing.fileWriter;
import com.ac.fileparsing.jsonReader;
import com.ac.fileparsing.jsonWriter;
import com.ac.lib.aeroPartInventory;
import com.ac.seasons.newSeason.seasonSettings;
import com.ac.seasons.newSeason.teamSetup;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * ai_1
 */
public class aiController {
    jsonReader jReader = new jsonReader();
    fileReader fReader = new fileReader();
    ArrayList<teamSetup> loadedTeams = new ArrayList<teamSetup>();
    ArrayList<aeroPartInventory[]> loadedInventories = new ArrayList<aeroPartInventory[]>();

    seasonSettings season = new seasonSettings("", 0, 0, 0, 0, 0, 0f, 0f, 0f, 0f, 1000, 0, 0, 0, 0, false, false, false, false, null, null );

    int[] aiFocusBalanced = {50, 50, 50, 50};
    int[] aiFocusDownforce = {50, 60, 65, 75};
    int[] aiFocusDrag = {50, 40, 35, 30};
    public String loadingLabelString;
    
    public void setLoadedTeams(ArrayList<teamSetup> loadedTeams) {
        this.loadedTeams = loadedTeams;
    }

    public void setLoadedInventories(ArrayList<aeroPartInventory[]> loadedInventories) {
        this.loadedInventories = loadedInventories;
    }

    @FXML
    private Label lblLoadingTeam;

    public void setLblLoadingTeam(Label lblLoadingTeam) {
        this.lblLoadingTeam = lblLoadingTeam;
    }

    public void init() throws InterruptedException {
        season = jsonReader.parseSeasonSettings();

        System.out.println("Season Loaded, validating AI");
        for (int i = 0; i < season.teamCount; i++) {
            if (loadedTeams.get(i) != null) {
                if (Objects.nonNull(loadedTeams.get(i).ai))
                    fixMissingAiInfo(loadedTeams.get(i));
            } else {
                System.out.println("Target team is null: [" + loadedTeams.get(i).getTeamName() + "]");
            }
        }
        System.out.println("Teams loaded");

        startLoadedTeamsAi();
    }

    private void startLoadedTeamsAi() {
        System.out.println("Starting Team AI");
        for (int i = 0; i < loadedTeams.size(); i++) {
            System.out.println("Starting Team [" + loadedTeams.get(i).getTeamName() + "]");

            // Switch on team controller.
            switch (loadedTeams.get(i).getController()) {
                case "Player Team":
                    fileWriter.writeNewAeroStats(loadedTeams.get(i), loadedInventories.get(i), season);
                    break;                
                case "AI":
                    switch (loadedTeams.get(i).ai.getPersonality()) {
                        case "Wide":
                            ai_02_wide ai_02_wide = new ai_02_wide();
                            ai_02_wide.setAiTeam(loadedTeams.get(i));
                            ai_02_wide.setInventory(loadedInventories.get(i));
                            ai_02_wide.setSeason(season);
                            ai_02_wide.setFocusRate(getAiFocusRate(loadedTeams.get(i)));
                            ai_02_wide.startAi();
                            break;
                        case "Focussed":
                            ai_02_focus ai_02_focus = new ai_02_focus();
                            ai_02_focus.setAiTeam(loadedTeams.get(i));
                            ai_02_focus.setInventory(loadedInventories.get(i));
                            ai_02_focus.setSeason(season);
                            ai_02_focus.setFocusRate(getAiFocusRate(loadedTeams.get(i)));
                            ai_02_focus.startAi();
                            break;
                    
                        default:
                            break;
                    }
                break;

                default:
                    System.out.println("AI FAILED");
                    break;
                }
        }
        lblLoadingTeam.setText("Race ready. You can start the session in Assetto Corsa");
    }

    private int[] getAiFocusRate(teamSetup targetTeam) {
        switch (targetTeam.ai.getPhilosophy()) {
            case "Downforce":
                return aiFocusDownforce;
            case "Drag":
                return aiFocusDrag;
            case "Balanced":
                return aiFocusBalanced;
        }
        return null;
    }

    private void fixMissingAiInfo(teamSetup aiTeam) {
        if (aiTeam.getController().equals("AI") && aiTeam.getController() != season.getPlayerTeam()) {
            String pers = aiTeam.ai.getPersonality();
            String phil = aiTeam.ai.getPhilosophy();
            if (pers.equals(""))
                pers = "Random";
            if (phil.equals(""))
                phil = "Random";
            
            aiTeam.addAI(pers, phil);
            jsonWriter.saveTeam(aiTeam);
        }
    }
}