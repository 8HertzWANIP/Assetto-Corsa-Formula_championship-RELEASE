package com.AI;

import java.util.ArrayList;

import com.ac.backend.jsonReader;
import com.ac.backend.jsonWriter;
import com.ac.backend.aeroPart;
import com.ac.backend.seasonSettings;
import com.ac.backend.teamSetup;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import com.ac.backend.fileReader;

/**
 * ai_1
 */
public class aiController {
    jsonReader jReader = new jsonReader();
    fileReader fReader = new fileReader();
    ArrayList<teamSetup> loadedTeams = new ArrayList<teamSetup>();
    ArrayList<aeroPart> aeroParts = new ArrayList<aeroPart>();
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
        0,
        false,
        false,
        false,
        false,
        null,
        null
        );
    
    float dForceCost;
    float dragCost;
    float maxDf;
    float maxDr;
    float minDf;
    float minDr;
    float aeroIncreaseRate = 1.0f;
    float dragIncreaseRate = 1.0f;

    Double improveDownforceFunds;
    Double improveDragFunds;

    Double loadedDownforce;
    Double loadedDrag;

    Double dfIncrease = 0d;
    Double dragDecrease = 0d;

    int partInvestment;
    public int fwAngle;
    public int rwAngle;
    public String loadingLabelString;
    public double progressbarValue = 0;

    @FXML
    private Label lblLoadingTeam;

    public void setLblLoadingTeam(Label lblLoadingTeam) {
        this.lblLoadingTeam = lblLoadingTeam;
    }

    public void init(ArrayList<String> teamList) throws InterruptedException {
        season = jsonReader.parseSeasonSettings();

        maxDf = season.baseline * (season.maxDownforce / 100);
        minDf = season.baseline * (season.minDownforce / 100);
        maxDr = season.baseline * (season.maxDrag / 100);
        minDr = season.baseline * (season.minDrag / 100);
        aeroIncreaseRate = season.returnDifficultyValue(season.difficulty);
        dragIncreaseRate = season.returnDifficultyValue(season.difficulty);

        System.out.println("Season Loaded");
        for (int i = 0; i < season.teamCount; i++) {
            if (teamList.get(i) != null) {
                loadedTeams.add(jReader.parseTeam(teamList.get(i)));
            } else {
                System.out.println("Target team is null: [" + teamList.get(i) + "]");
            }
        }
        System.out.println("Teams loaded");
        startLoadedTeamsAi();
    }

    private void startLoadedTeamsAi() {
        System.out.println("Starting Team AI");
        for (int i = 0; i < loadedTeams.size(); i++) {
            System.out.println("Starting Team [" + loadedTeams.get(i).getTeamName() + "]");
            System.out.println("Team Controller [" + loadedTeams.get(i).getController() + "]");
            // Switch on team controller.
            switch (loadedTeams.get(i).getController()) {
                case "Player Team":
                    
                    break;
            
                case "AI-Balanced":
                    ai_balanced ai_balanced = new ai_balanced();
                    ai_balanced.fwAngle = fwAngle;
                    ai_balanced.rwAngle = rwAngle;
                    ai_balanced.setTargetTeam(loadedTeams.get(i));
                    ai_balanced.setSeason(season);
                    ai_balanced.startAi();
                break;
   
                case "AI-Downforce":
                    ai_downforce ai_downforce = new ai_downforce();
                    ai_downforce.fwAngle = fwAngle;
                    ai_downforce.rwAngle = rwAngle;
                    ai_downforce.setTargetTeam(loadedTeams.get(i));
                    ai_downforce.setSeason(season);
                    ai_downforce.startAi();
                break;
                
                case "AI-Drag":
                    ai_drag ai_drag = new ai_drag();
                    ai_drag.setTargetTeam(loadedTeams.get(i));
                    ai_drag.setSeason(season);
                    ai_drag.startAi();
                break;

                default:
                    ai_balanced ai_balanced_default = new ai_balanced();
                    ai_balanced_default.fwAngle = fwAngle;
                    ai_balanced_default.rwAngle = rwAngle;
                    ai_balanced_default.setTargetTeam(loadedTeams.get(i));
                    ai_balanced_default.setSeason(season);
                    ai_balanced_default.startAi();
                    break;
                }
        }
        lblLoadingTeam.setText("Race ready. You can start the session in Assetto Corsa");
    }
}