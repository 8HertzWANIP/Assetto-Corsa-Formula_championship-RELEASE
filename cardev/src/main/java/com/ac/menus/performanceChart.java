package com.ac.menus;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.swing.JFileChooser;

import com.ac.App;
import com.ac.fileparsing.fileReader;
import com.ac.fileparsing.jsonReader;
import com.ac.lib.aeroPart;
import com.ac.seasons.newSeason.seasonSettings;
import com.ac.seasons.newSeason.teamSetup;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class performanceChart extends App implements Initializable{

    fileReader fReader = new fileReader();
    ArrayList<Double> teamRatings = new ArrayList<Double>();
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

    @FXML
    private Button btnMoney;
    
    @FXML
    private Button btnReturn;

    @FXML
    private ComboBox<?> cmbSelectedTeam;

    @FXML
    private TextField txtAeroRating;

    @FXML
    private TextField txtChampPos;
    
    @FXML
    private AnchorPane performancePane;

    @FXML
    private TextField txtChhampPoints;

    @FXML
    private TextField txtMoney;

    @FXML
    private TextField txtMoneyModifyer;

    @FXML
    private TextField txtResearch;

    @FXML
    private TextField txtTeamName;


    @FXML
    void btnReturnClick(ActionEvent event) throws IOException {
        App.setRoot("teamMenu");
    }

    @FXML
    void btnMoneyClick(ActionEvent event) {

    }

    @FXML
    void cmbSelectTeam(ActionEvent event) {

    }

    @FXML
    void txtResearchChanged(KeyEvent event) {

    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        season = jsonReader.parseSeasonSettings();


        loadTeamlist();
        generateBarsAndLabels();
    }

    public void generateBarsAndLabels() {
        Label[] labels = new Label[season.teamCount];
        ProgressBar[] bars = new ProgressBar[season.teamCount];
        ArrayList<teamPerformanceIndex> teams = new ArrayList<teamPerformanceIndex>();
        ArrayList<String> setTeams = new ArrayList<String>();

        for (int i = 0; i < season.teamCount; i++) {
            teams.add(i, new teamPerformanceIndex());  
            aeroParts.clear();
            teamSetup targetTeam = loadedTeams.get(i);
            fileReader.setAeroParts(new ArrayList<aeroPart>());
            fileReader.fillAeroParts(targetTeam.getCarFolder() + "\\data\\aero.ini");
            aeroParts = fReader.getAeroParts();
            convertAeroStats(aeroParts);
            teams.get(i).setScaledRating(getTeamRatingProgressBarValue(true));
            teams.get(i).setRating(getTeamRatingProgressBarValue(false));
            teams.get(i).setTeamName(targetTeam.getTeamName());
        }

        for (int i = 0; i < season.teamCount; i++) {
            teamPerformanceIndex targetTeam = new teamPerformanceIndex();
            teamPerformanceIndex checkingTeam = new teamPerformanceIndex();
            targetTeam = teams.get(0);
            
            for (int j = 0; j < season.teamCount; j++) {
                checkingTeam = teams.get(j);
                if (setTeams.contains(targetTeam.getTeamName())) {
                    targetTeam = checkingTeam;
                }
                if (checkingTeam.getTeamName() != targetTeam.getTeamName() && !setTeams.contains(checkingTeam.getTeamName())) {
                    if (targetTeam.getRating() < checkingTeam.getRating()) {
                        targetTeam = checkingTeam;
                    }
                }
            }
            targetTeam.setIndex(i);
            if (!setTeams.contains(targetTeam.getTeamName())) {
                
                setTeams.add(targetTeam.getTeamName());
                System.out.println("team added [" + targetTeam.getTeamName() + "]");
                labels[i] = new Label();
                labels[i].setTranslateX(15);
                labels[i].setFont(Font.font("System", FontWeight.BOLD, 14));
                labels[i].setTranslateY(45 + (30 * i));
                labels[i].setText(targetTeam.getTeamName());
                performancePane.getChildren().add(labels[i]);
                bars[i] = new ProgressBar();
                bars[i].setTranslateX(180);
                bars[i].setTranslateY(42 + (30 * i));
                bars[i].setPrefWidth(800);
                bars[i].setPrefHeight(25);
                bars[i].setProgress(targetTeam.getScaledRating());
                // System.out.println("Label added");
                // System.out.println("team name: [" + targetTeam.getTeamName() + "]");
                // System.out.println("team rating: [" + targetTeam.getScaledRating() + "]");
                performancePane.getChildren().add(bars[i]);
            }
        }
    }

    private Double getTeamRatingProgressBarValue(boolean scaledRating) {
        float downforceObjects = 0;
        float dragObjects = 0;
        Float rating = 0f;
        Float rating2 = 0f;
        for (int i = 0; i < aeroParts.size(); i++) {
            if (aeroParts.get(i).getDownforce() > 0) {
                downforceObjects++;
            }
            if (aeroParts.get(i).getDrag() > 0) {
                dragObjects++;
            }
            rating += aeroParts.get(i).getDownforce();
            rating2 += aeroParts.get(i).getDrag();
        }
        float intRating = rating / downforceObjects + rating2 / dragObjects;
        System.out.println("team rating: [" + intRating + "]");
        Double prgrsBarValue = (((double) intRating - (season.baseline * 1.3)) * 100) / (season.baseline) / 100;
        System.out.println(prgrsBarValue);
        if (scaledRating) {
            return prgrsBarValue;
        } else {
            return (double) intRating;
        }
    }

    private ArrayList<aeroPart> convertAeroStats(ArrayList<aeroPart> parts) {
        for (int i = 0; i < parts.size(); i++) {
            if (parts.get(i).drag != 0.0f) {
                parts.get(i).drag = parts.get(i).drag * season.baseline;
                parts.get(i).drag = Math.round(season.baseline - (parts.get(i).drag - season.baseline));
                
            }
            if (parts.get(i).downforce != 0.0f) {
                parts.get(i).downforce = Math.round(parts.get(i).downforce * season.baseline);
            }
        }
        return parts;
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
                loadedTeams.add(jsonReader.parseTeam(teamList.get(i)));
                System.out.println("Team: [" + loadedTeams.get(i).profileName + "] loaded");
            } else {
                System.out.println("Target team is null: [" + teamList.get(i) + "]");
            }
        }
    }
    /**
     * teamPerformanceIndex
     */
    public class teamPerformanceIndex {
        String teamName;
        Double scaledRating;
        Double rating;
        int index;
        public String getTeamName() {
            return teamName;
        }
        public void setTeamName(String teamName) {
            this.teamName = teamName;
        }
        public Double getRating() {
            return rating;
        }
        public void setRating(Double rating) {
            this.rating = rating;
        }
        public Double getScaledRating() {
            return scaledRating;
        }
        public void setScaledRating(Double scaledRating) {
            this.scaledRating = scaledRating;
        }
        public int getIndex() {
            return index;
        }
        public void setIndex(int index) {
            this.index = index;
        }

    }
}