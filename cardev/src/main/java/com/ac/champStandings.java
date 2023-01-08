package com.ac;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.swing.JFileChooser;

import com.ac.backend.jsonReader;
import com.ac.backend.aeroPart;
import com.ac.backend.fileReader;
import com.ac.backend.seasonSettings;
import com.ac.backend.teamSetup;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

public class champStandings extends App implements Initializable {

    jsonReader jReader = new jsonReader();
    fileReader fReader = new fileReader();
    ArrayList<Double> teamRatings = new ArrayList<Double>();
    public static ArrayList<teamSetup> loadedTeams;
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
    public static int loadingTeamIndex = 0;

    public static ArrayList<teamSetup> getLoadedTeams() {
        return loadedTeams;
    }

    public static int getLoadingTeamIndex() {
        return loadingTeamIndex;
    }

    
    @FXML
    private Button btnAwardPoints;

    @FXML
    private AnchorPane paneStandings;

    @FXML
    void btnAwardPointsClick(ActionEvent event) throws IOException {
        App.setRoot("champPoints");

    }

    @FXML
    void btnMoneyClick(ActionEvent event) {

    }

    @FXML
    void btnReturnClick(ActionEvent event) throws IOException {
        App.setRoot("improvePart");

    }

    @FXML
    void cmbSelectTeam(ActionEvent event) {

    }

    @FXML
    void txtResearchChanged(KeyEvent event) {

    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        loadingTeamIndex = 0;
        season = jReader.parseSeasonSettings();
        loadedTeams = new ArrayList<teamSetup>();

        loadTeamlist();
        if (loadedTeams.size() == 0) {}
        try {
            generateTeamStandingUi();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void generateTeamStandingUi() throws IOException {
        ArrayList<teamPointsIndex> teams = new ArrayList<teamPointsIndex>();
        ArrayList<String> setTeams = new ArrayList<String>();

        // copy teams info to teamPointsIndex class for ordering.
        for (int i = 0; i < season.teamCount; i++) {
            teams.add(i, new teamPointsIndex());  
            teamSetup targetTeam = loadedTeams.get(i);
            teams.get(i).setIndex(i);
            teams.get(i).setPoints(targetTeam.getPoints());
            teams.get(i).setTeamName(targetTeam.getTeamName());
        }

        // Order the teams descending on points
        for (int i = 0; i < season.teamCount; i++) {
            teamPointsIndex targetTeam = new teamPointsIndex();
            teamPointsIndex checkingTeam = new teamPointsIndex();
            targetTeam = teams.get(0);
            System.out.println("Team target is: [" + targetTeam.getTeamName() + "]");
            
            for (int j = 0; j < season.teamCount; j++) {
                checkingTeam = teams.get(j);
                if (setTeams.contains(targetTeam.getTeamName())) {
                    targetTeam = checkingTeam;
                    System.out.println("Team target changed setTeams: [" + targetTeam.getTeamName() + "]");
                }
                if (checkingTeam.getTeamName() != targetTeam.getTeamName() && !setTeams.contains(checkingTeam.getTeamName())) {
                    if (targetTeam.getPoints() < checkingTeam.getPoints()) {
                        targetTeam = checkingTeam;
                        System.out.println("Team target changed points: [" + targetTeam.getTeamName() + "]");
                    }
                }
            }
            // targetTeam.setIndex(i);
            if (!setTeams.contains(targetTeam.getTeamName())) {
                
                setTeams.add(targetTeam.getTeamName());
                loadingTeamIndex = targetTeam.getIndex();
                System.out.println("Team index is: [" + loadingTeamIndex + "]");
                Pane newLoadedPane =  FXMLLoader.load(getClass().getResource("teamObject.fxml"));
                newLoadedPane.setLayoutX(10);
                newLoadedPane.setLayoutY(45 + (60 * i));
                paneStandings.getChildren().add(newLoadedPane);
            }
        }

        // for (int i = 0; i < loadedTeams.size(); i++) {
        //     Pane newLoadedPane =  FXMLLoader.load(getClass().getResource("teamObject.fxml"));
        //     newLoadedPane.setLayoutX(10);
        //     newLoadedPane.setLayoutY(45 + (60 * i));
        //     paneStandings.getChildren().add(newLoadedPane);
        //     loadingTeamIndex++;
        // }
    }

    private void loadTeamlist() {
        File file = new File(new JFileChooser().getFileSystemView().getDefaultDirectory().toString() + "\\Ac Cardev save data\\" + loadedProfile);
        String[] directories = file.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
        });
        // System.out.println(Arrays.toString(directories));
        ObservableList<String> teams = FXCollections.observableArrayList(directories);
        
        ArrayList<String> teamList = new ArrayList<String>();
        for (int i = 0; i < season.getTeamCount(); i++) {
            teamList.add(teams.get(i));
        }

        for (int i = 0; i < season.teamCount; i++) {
            if (teamList.get(i) != null) {
                loadedTeams.add(jReader.parseTeam(teamList.get(i)));
                System.out.println("Team: [" + loadedTeams.get(i).profileName + "] loaded");
            } else {
                System.out.println("Target team is null: [" + teamList.get(i) + "]");
            }
        }
    }
    /**
     * teamPointsIndex
     */
    public class teamPointsIndex {
        String teamName;
        int points;
        int index;
        public String getTeamName() {
            return teamName;
        }
        public void setTeamName(String teamName) {
            this.teamName = teamName;
        }
        public int getPoints() {
            return points;
        }
        public void setPoints(int points) {
            this.points = points;
        }
        public int getIndex() {
            return index;
        }
        public void setIndex(int index) {
            this.index = index;
        }

    }
}
