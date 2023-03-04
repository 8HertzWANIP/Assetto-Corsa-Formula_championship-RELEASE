package com.ac.menus.uiObjects;

import java.net.URL;
import java.util.ResourceBundle;

import com.ac.menus.champStandings;
import com.ac.seasons.newSeason.teamSetup;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class teamObject extends champStandings {

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
    teamSetup loadedTeam = new teamSetup(null, null, null, null, null, 0, 0, 0, 0);

    private int index;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        index = loadingTeamIndex;
        loadedTeam = loadedTeams.get(index);
        recTeamIcon.setFill(Color.valueOf(loadedTeam.getTeamColor()));
        recTeamColor.setFill(Color.valueOf(loadedTeam.getTeamColor()));
        lblTeamName.setText(loadedTeam.getTeamName());
        lblTeamPoints.setText(Integer.toString(loadedTeam.getPoints()));
        lblTeamChampPos.setText(Integer.toString(loadedTeam.getChampPos()));
        System.out.println("Team loaded index: [" + index + "]");
        System.out.println("Team loaded points: [" + loadedTeam.getPoints() + "]");
    }

}
