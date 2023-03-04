package com.ac.menus.uiObjects;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import com.ac.fileparsing.jsonReader;
import com.ac.menus.champPoints;
import com.ac.seasons.newSeason.seasonSettings;
import com.ac.seasons.newSeason.teamSetup;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class teamPointsObject extends champPoints {

    int index = 0;
    jsonReader jReader = new jsonReader();
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
    
    @FXML
    private Button btnDriver1;

    @FXML
    private Button btnDriver2;
    
    @FXML
    private Button btnFastestLap;
    
    @FXML
    private Label lblTeamChampPos;

    @FXML
    private Label lblTeamName;

    @FXML
    private Label lblTeamPoints;

    @FXML
    private Rectangle recTeamColor;

    @FXML
    private Rectangle recTeamIcon;

    @FXML
    private Rectangle recTeamName;

    @FXML
    private Rectangle recTeamPoints;
    ArrayList<teamSetup> loadedTeams = champPoints.getLoadedTeams();
    teamSetup loadedTeam = new teamSetup(null, null, null, null, null, 0, 0, 0, 0);

    @FXML
    void btnDriver1Click(ActionEvent event) {
        int teamPoints = Integer.parseInt(lblTeamPoints.getText());
        int pointsAwarded = season.returnFinishingPositionPoints(finishingPosition);
        lblTeamPoints.setText(Integer.toString(teamPoints + pointsAwarded));
        btnDriver1.disableProperty().set(true);
        saveTeamPoints(pointsAwarded, false);
        finishingPosition++;
    }

    @FXML
    void btnDriver2Click(ActionEvent event) {
        int teamPoints = Integer.parseInt(lblTeamPoints.getText());
        int pointsAwarded = season.returnFinishingPositionPoints(finishingPosition);
        lblTeamPoints.setText(Integer.toString(teamPoints + pointsAwarded));
        btnDriver2.disableProperty().set(true);
        saveTeamPoints(pointsAwarded, false);
        finishingPosition++;
    }
    
    @FXML
    void btnFastestLapClick(ActionEvent event) {
        System.out.println(champPoints.fastestLapPoints);
        int teamPoints = Integer.parseInt(lblTeamPoints.getText());
        lblTeamPoints.setText(Integer.toString(teamPoints + champPoints.fastestLapPoints));
        btnFastestLap.disableProperty().set(true);
        saveTeamPoints(fastestLapPoints, true);
        champPoints.fastestLapPoints = 0;
        disableFastestLaps();
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        index = loadingTeamIndex;
        season = jsonReader.parseSeasonSettings();
        if (champPoints.getLoadingTeamIndex() > loadedTeams.size()) {
            champPoints.setLoadingTeamIndex(0);
        }
        loadedTeam = loadedTeams.get(champPoints.getLoadingTeamIndex());
        recTeamIcon.setFill(Color.valueOf(loadedTeam.getTeamColor()));
        recTeamColor.setFill(Color.valueOf(loadedTeam.getTeamColor()));
        lblTeamName.setText(loadedTeam.getTeamName());
        lblTeamPoints.setText(Integer.toString(loadedTeam.getPoints()));
        lblTeamChampPos.setText(Integer.toString(loadedTeam.getChampPos()));
        System.out.println("Team loaded index: [" + index + "]");
    }

    public void disableFastestLaps() {
        this.btnFastestLap.disableProperty().set(false);
    }

    public void saveTeamPoints(int pointsToAdd, boolean fastestLap) {
        pointListToAdd.set(index, pointListToAdd.get(index) + pointsToAdd);
        System.out.println("Team [" + loadedTeams.get(index).getTeamName() + "] points: [" + loadedTeams.get(index).getPoints() + "]");
        System.out.println("pointListToAdd [" + pointListToAdd.get(index) + "]");
        if (index == playerIndex) {
            finishingPositionStrings.add("Finishing position [" + finishingPosition + "]");
            finishingPositionStrings.add(Integer.toString(pointsToAdd));
        }
        if (!fastestLap) {
            addMoneyToTeam(index);
        }
    }
}
