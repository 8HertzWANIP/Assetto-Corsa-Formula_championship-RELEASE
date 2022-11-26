package com.ac;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.swing.JFileChooser;

import com.ac.backend.jsonReader;
import com.ac.backend.jsonWriter;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

public class champPoints extends App implements Initializable {

    jsonReader jReader = new jsonReader();
    fileReader fReader = new fileReader();
    jsonWriter jWriter = new jsonWriter();
    ArrayList<Double> teamRatings = new ArrayList<Double>();
    public static ArrayList<teamSetup> loadedTeams = new ArrayList<teamSetup>();
    public static ArrayList<Integer> pointListToAdd = new ArrayList<Integer>();
    public static ArrayList<Integer> moneyListToAdd = new ArrayList<Integer>();
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
        false,
        false,
        false,
        false
    );
    public static int loadingTeamIndex = 0;
    public static int finishingPosition = 1;
    public static int fastestLapPoints = 1;
    
    public static void setLoadingTeamIndex(int loadingTeamIndex) {
        champPoints.loadingTeamIndex = loadingTeamIndex;
    }

    public static void setFastestLapPoints(int fastestLapPoints) {
        champPoints.fastestLapPoints = fastestLapPoints;
    }

    public static int getFinishingPosition() {
        return finishingPosition;
    }

    public static void setFinishingPosition(int finishingPosition) {
        champPoints.finishingPosition = finishingPosition;
        System.out.println("finishing position: [" + champPoints.finishingPosition + "]");
    }

    public static ArrayList<teamSetup> getLoadedTeams() {
        return loadedTeams;
    }

    public static int getLoadingTeamIndex() {
        return loadingTeamIndex;
    }

    @FXML
    private Button btnMoney;

    @FXML
    private Button btnResetPoints;

    @FXML
    private Button btnReturn;

    @FXML
    private Button btnSavePoints;

    @FXML
    private AnchorPane champPointsPane;

    @FXML
    private ComboBox<?> cmbSelectedTeam;

    @FXML
    private TextField txtAeroRating;

    @FXML
    public TextField txtFinishPos;

    public void setTxtFinishPos(String text) {
        txtFinishPos.setText(text);
    }

    @FXML
    private TextField txtMoney;

    @FXML
    private TextField txtMoneyModifyer;

    @FXML
    private TextField txtResearch;

    @FXML
    private TextField txtTeamName;


    @FXML
    void btnResetPointsClick(ActionEvent event) throws IOException {
        fastestLapPoints = 1;
        champPoints.setFinishingPosition(1);
        champPoints.setLoadingTeamIndex(0);
        App.setRoot("champPoints");
    }

    @FXML
    void btnSavePointsClick(ActionEvent event) throws IOException {
        // int participationFunds = (((season.getTotalPrizePool() * 1000000) / 2) / season.getRaceCount()) / season.getTeamCount();
        for (int i = 0; i < loadedTeams.size(); i++) {
            loadedTeams.get(i).setPoints(loadedTeams.get(i).getPoints() + pointListToAdd.get(i));
            loadedTeams.get(i).setMoney(loadedTeams.get(i).getMoney() + moneyListToAdd.get(i));
        }
        saveTeamFilePointsInfo();
        fastestLapPoints = 1;
        App.setRoot("champStandings");
    }

    @FXML
    void btnMoneyClick(ActionEvent event) {

    }

    @FXML
    void btnReturnClick(ActionEvent event) throws IOException {
        champPoints.setFinishingPosition(1);
        champPoints.setLoadingTeamIndex(0);
        App.setRoot("improvePart");
    }

    @FXML
    void cmbSelectTeam(ActionEvent event) {

    }

    @FXML
    void txtResearchChanged(KeyEvent event) {

    }

    public void addMoneyToTeam(int teamIndex) {

        // TODO Season settings are being reset. Find out why.
        season = jReader.parseSeasonSettings();
        int raceRewardTotal = 0;
        int cashReward = 0;
        switch (season.raceRewards) {
            // Race + Season rewards
            case 0:
                // Convert season rewards to millions
                raceRewardTotal = ((season.getTotalPrizePool() * 1000000) / 2) / season.getRaceCount() / 2;
                cashReward = 0;
                switch (finishingPosition) {
                    case 1:
                        cashReward += Math.round(raceRewardTotal * 0.19f);
                        break;
                    case 2:
                        cashReward += Math.round(raceRewardTotal * 0.16f);
                        break;
                    case 3:
                        cashReward += Math.round(raceRewardTotal * 0.13f);
                        break;
                    case 4:
                        cashReward += Math.round(raceRewardTotal * 0.11f);
                        break;
                    case 5:
                        cashReward += Math.round(raceRewardTotal * 0.10f);
                        break;
                    case 6:
                        cashReward += Math.round(raceRewardTotal * 0.09f);
                        break;
                    case 7:
                        cashReward += Math.round(raceRewardTotal * 0.07f);
                        break;
                    case 8:
                        cashReward += Math.round(raceRewardTotal * 0.06f);
                        break;
                    case 9:
                        cashReward += Math.round(raceRewardTotal * 0.05f);
                        break;
                    case 10:
                        cashReward += Math.round(raceRewardTotal * 0.04f);
                        break;
                
                    default:
                        break;
                }
                moneyListToAdd.set(teamIndex, moneyListToAdd.get(teamIndex) + cashReward);
                System.out.println("money to add to team: [" + loadedTeams.get(teamIndex).getTeamName() + "] - [" + moneyListToAdd.get(teamIndex) + "]");
                break;
            // Race Rewards only
            case 1:
                // Convert season rewards to millions
                raceRewardTotal = ((season.getTotalPrizePool() * 1000000)) / season.getRaceCount() / 2;
                cashReward = 0;
                switch (finishingPosition) {
                    case 1:
                        cashReward += Math.round(raceRewardTotal * 0.19f);
                        break;
                    case 2:
                        cashReward += Math.round(raceRewardTotal * 0.16f);
                        break;
                    case 3:
                        cashReward += Math.round(raceRewardTotal * 0.13f);
                        break;
                    case 4:
                        cashReward += Math.round(raceRewardTotal * 0.11f);
                        break;
                    case 5:
                        cashReward += Math.round(raceRewardTotal * 0.10f);
                        break;
                    case 6:
                        cashReward += Math.round(raceRewardTotal * 0.09f);
                        break;
                    case 7:
                        cashReward += Math.round(raceRewardTotal * 0.07f);
                        break;
                    case 8:
                        cashReward += Math.round(raceRewardTotal * 0.06f);
                        break;
                    case 9:
                        cashReward += Math.round(raceRewardTotal * 0.05f);
                        break;
                    case 10:
                        cashReward += Math.round(raceRewardTotal * 0.04f);
                        break;
                
                    default:
                        break;
                }
                moneyListToAdd.set(teamIndex, moneyListToAdd.get(teamIndex) + cashReward);
                System.out.println("money to add to team: [" + loadedTeams.get(teamIndex).getTeamName() + "] - [" + moneyListToAdd.get(teamIndex) + "]");
                break;
        
            default:
                break;
        }
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        season = jReader.parseSeasonSettings();

        if (loadedTeams.size() == 0) {
            loadTeamlist();
        }
        try {
            generateTeamStandingUi();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int raceRewardTotal = 0;
        int cashReward = 0;
        switch (season.raceRewards) {
            // Race + Season rewards
            case 0:
                // Convert season rewards to millions
                raceRewardTotal = ((season.getTotalPrizePool() * 1000000) / 2) / season.getRaceCount() / 2;
                cashReward = raceRewardTotal / season.teamCount;
                break;
            // Race Rewards only
            case 1:
                // Convert season rewards to millions
                raceRewardTotal = ((season.getTotalPrizePool() * 1000000)) / season.getRaceCount() / 2;
                cashReward = raceRewardTotal / season.teamCount;
                break;
        
            default:
                break;
        }
        // Load a list of the same size as the loaded team list. This list is used to add and save to each team when the save button is clicked.
        for (int i = 0; i < loadedTeams.size(); i++) {
            pointListToAdd.add(i, 0);
            moneyListToAdd.add(i, cashReward);
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
            
            for (int j = 0; j < season.teamCount; j++) {
                checkingTeam = teams.get(j);
                if (setTeams.contains(targetTeam.getTeamName())) {
                    targetTeam = checkingTeam;
                }
                if (checkingTeam.getTeamName() != targetTeam.getTeamName() && !setTeams.contains(checkingTeam.getTeamName())) {
                    if (targetTeam.getPoints() < checkingTeam.getPoints()) {
                        targetTeam = checkingTeam;
                    }
                }
            }
            if (!setTeams.contains(targetTeam.getTeamName())) {
                setTeams.add(targetTeam.getTeamName());
                loadingTeamIndex = targetTeam.getIndex();
                System.out.println("Team index is: [" + loadingTeamIndex + "]");
                Pane newLoadedPane =  FXMLLoader.load(getClass().getResource("teamPointsObject.fxml"));
                newLoadedPane.setLayoutX(10);
                newLoadedPane.setLayoutY(45 + (60 * i));
                champPointsPane.getChildren().add(newLoadedPane);
            }
        }
    }

    private void saveTeamFilePointsInfo() {
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
                loadedTeams.get(targetTeam.getIndex()).setChampPos(i + 1);
                setTeams.add(targetTeam.getTeamName());
                System.out.println("Wanting to put champ points of [" + targetTeam.getTeamName() + "] in [" + loadedTeams.get(targetTeam.getIndex()).getTeamName() + "]");
                jWriter.saveTeam(loadedTeams.get(targetTeam.getIndex()));
            }
        }
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

    @FXML
    void updateFinishingPos(MouseEvent event) {
        txtFinishPos.setText(Integer.toString(finishingPosition));

    }
    /**
     * teamPointsIndex
     */
    public class teamPointsIndex {
        String teamName;
        int points;
        int champPos;
        int index;
        public int getChampPos() {
            return champPos;
        }
        public void setChampPos(int champPos) {
            this.champPos = champPos;
        }
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
