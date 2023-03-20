package com.ac.menus;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

import javax.swing.JFileChooser;

import com.ac.App;
import com.ac.fileparsing.jsonReader;
import com.ac.fileparsing.jsonWriter;
import com.ac.lib.aeroPart;
import com.ac.lib.uiAPI;
import com.ac.seasons.newSeason.seasonSettings;
import com.ac.seasons.newSeason.teamSetup;

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
    ArrayList<Double> teamRatings = new ArrayList<Double>();
    public static int playerIndex;
    public static ArrayList<String> finishingPositionStrings = new ArrayList<String>();
    public static ArrayList<String> prizeMoneyStrings = new ArrayList<String>();
    public static ArrayList<teamSetup> loadedTeams = App.seasonData.loadedTeams;
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
        0,
        false,
        false,
        false,
        false,
        null,
        null
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
            App.setRoot(new FXMLLoader(getClass().getResource("/champPoints.fxml")));

        
    }

    @FXML
    void btnSavePointsClick(ActionEvent event) throws IOException {
        // int participationFunds = (((season.getTotalPrizePool() * 1000000) / 2) / season.getRaceCount()) / season.getTeamCount();
        prizeMoneyStrings.add("Participation prize:");
        prizeMoneyStrings.add("€ " + uiAPI.setCurrencyFormat(returnRaceRewardMoney(-1, season.getRaceCount(), season.getTeamCount(), true)));
        prizeMoneyStrings.add("Total:");
        prizeMoneyStrings.add("€ " + uiAPI.setCurrencyFormat(moneyListToAdd.get(playerIndex)));
        finishingPositionStrings.add("Total:");
        finishingPositionStrings.add(Integer.toString(pointListToAdd.get(playerIndex)));
        for (int i = 0; i < loadedTeams.size(); i++) {
            loadedTeams.get(i).setPoints(loadedTeams.get(i).getPoints() + pointListToAdd.get(i));
            loadedTeams.get(i).setMoney(loadedTeams.get(i).getMoney() + moneyListToAdd.get(i));
        }
        saveTeamFilePointsInfo();
        fastestLapPoints = 1;
        season.setRaceCanceled(false);
        season.setCurrentRace(season.getCurrentRace() + 1);
        jsonWriter.saveSeasonSettings(season);
        if (season.getCurrentRace() > season.getRaceCount())
            App.setRoot(new FXMLLoader(getClass().getResource("/endOfSeason.fxml")));

        else
            App.setRoot(new FXMLLoader(getClass().getResource("/incomeWindow.fxml")));

    }

    @FXML
    void btnMoneyClick(ActionEvent event) {

    }

    @FXML
    void btnReturnClick(ActionEvent event) throws IOException {
        champPoints.setFinishingPosition(1);
        champPoints.setLoadingTeamIndex(0);
            App.setRoot(new FXMLLoader(getClass().getResource("/teamMenu.fxml")));

    }

    @FXML
    void cmbSelectTeam(ActionEvent event) {

    }

    @FXML
    void txtResearchChanged(KeyEvent event) {

    }

    public void addMoneyToTeam(int teamIndex) {
        season = jsonReader.parseSeasonSettings();
        int prizeMoney = returnRaceRewardMoney(finishingPosition - 1, season.getRaceCount(), season.getTeamCount(), false);
        System.out.println("team: [ "+ loadedTeams.get(teamIndex).getTeamName() + "] awarded: [" + prizeMoney + "] with participation prize of: [" + returnRaceRewardMoney(-1, season.getRaceCount(), season.getTeamCount(), true) + "]");
        System.out.println("total prize money: [" + (prizeMoney + returnRaceRewardMoney(-1, season.getRaceCount(), season.getTeamCount(), true))+ "]");
        moneyListToAdd.set(teamIndex, moneyListToAdd.get(teamIndex) + prizeMoney);
        if (teamIndex == playerIndex) {
            prizeMoneyStrings.add("Finishing position: [" + finishingPosition + "]");
            prizeMoneyStrings.add("€ " + uiAPI.setCurrencyFormat(prizeMoney));
        }
    }

    int returnRaceRewardMoney(int position, int totalRaces, int teamCount, boolean returnParticiaptionPrize) {
        int seasonBudget = season.getTotalPrizePool() * 1000000;
        int reward = 0;
        int perRaceBudget = 0;
        int positionPrizeMoney = 0;
        int participationPrize = 0;
        float participationPrizeRate = season.getParticipationPrizeRate();
        float posPrizeRate = 1f - participationPrizeRate;
        switch (season.getRaceRewards()) {
            case 0:
                seasonBudget = seasonBudget / 2;
                perRaceBudget = Math.round((seasonBudget / totalRaces));
                if (!returnParticiaptionPrize && position < season.getSeasonPrizeAwards().length) {
                    positionPrizeMoney = Math.round((perRaceBudget * posPrizeRate) * season.getSeasonPrizeAwards()[position]);
                } else if (returnParticiaptionPrize){
                    participationPrize = Math.round((perRaceBudget * participationPrizeRate) / teamCount);
                }
                
                break;
            case 1:
                perRaceBudget = Math.round((seasonBudget / totalRaces));
                if (!returnParticiaptionPrize && position < season.getSeasonPrizeAwards().length) {
                    positionPrizeMoney = Math.round((perRaceBudget * posPrizeRate) * season.getSeasonPrizeAwards()[position]);
                } else if (returnParticiaptionPrize){
                    participationPrize = Math.round((perRaceBudget * participationPrizeRate) / teamCount);
                }
                
                break;
            case 2:
                
                break;
        
            default:
                break;
        }
        if (season.getRaceRewards() != 2) {
            if (returnParticiaptionPrize) {
                reward = participationPrize;
            } else {
                reward = positionPrizeMoney;
            }
        }
        return reward;
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        season = jsonReader.parseSeasonSettings();
        finishingPosition = 1;
        finishingPositionStrings = new ArrayList<String>();
        prizeMoneyStrings = new ArrayList<String>();
        
        if (Objects.isNull(loadedTeams)) {
            loadTeamlist();
        }
        try {
            generateTeamStandingUi();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int participationPrizeMoney = returnRaceRewardMoney(-1, season.getRaceCount(), season.getTeamCount(), true);
        // Load a list of the same size as the loaded team list. This list is used to add and save to each team when the save button is clicked.
        for (int i = 0; i < loadedTeams.size(); i++) {
            if (loadedTeams.get(i).getController().equals("Player Team")) {
                playerIndex = i;
            }
            pointListToAdd.add(i, 0);
            // Add participationPrizeMoney for each team 
            moneyListToAdd.add(i, participationPrizeMoney);
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
                URL url = getClass().getResource("/teamPointsObject.fxml");
                Pane newLoadedPane = FXMLLoader.load(url);
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
                jsonWriter.saveTeam(loadedTeams.get(targetTeam.getIndex()));
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
                loadedTeams.add(jsonReader.parseTeam(teamList.get(i)));
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
