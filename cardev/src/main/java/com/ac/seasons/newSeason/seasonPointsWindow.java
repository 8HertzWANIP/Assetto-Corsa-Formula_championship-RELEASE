package com.ac.seasons.newSeason;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.ac.App;
import com.ac.fileparsing.jsonReader;
import com.ac.fileparsing.jsonWriter;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.shape.Rectangle;

public class seasonPointsWindow extends newSeason {

    public static ObservableList<String> positionPointsNew = 
    FXCollections.observableArrayList();

    public static ObservableList<String> positionRewardsNew = 
    FXCollections.observableArrayList();

    public static ObservableList<String> positionPointsOld = 
    FXCollections.observableArrayList();

    public static ObservableList<String> positionRewardsOld = 
    FXCollections.observableArrayList();

    private static final float[] newRaceRewardRates = {0.19f, 0.16f, 0.13f, 0.11f, 0.10f, 0.09f, 0.07f, 0.06f, 0.05f, 0.04f};
    private static final float[] oldRaceRewardRates = {0.21f, 0.19f, 0.16f, 0.14f, 0.13f, 0.11f, 0.06f, 0.05f};

    private static final int[] newRacePoints = {25, 18, 15, 12, 10, 8, 6, 4, 2, 1};
    private static final int[] oldRacePoints = {10, 6, 4, 3, 2, 1};

    private seasonSettings loadedSeason = newSeason.season;

    @FXML
    private Button btnContinue;

    @FXML
    private Button btnReturn;

    @FXML
    private ListView<String> lstNewPointsView;

    @FXML
    private ListView<String> lstNewRewardsView;

    @FXML
    private ListView<String> lstOldPointsView;

    @FXML
    private ListView<String> lstOldRewardsView;

    @FXML
    private RadioButton radNew;

    @FXML
    private RadioButton radOld;

    @FXML
    private Rectangle recNewSelectionBox;

    @FXML
    private Rectangle recOldSelectionBox;

    @FXML
    void btnContinueClick(ActionEvent event) throws IOException {
        jsonWriter.saveSeasonSettings(loadedSeason);
        newSeason.season = loadedSeason;
        App.setRoot("carsFolder");
    }

    @FXML
    void btnReturnToMainMenu(ActionEvent event) throws IOException {
        App.setRoot("seasonRegulations");
    }

    @FXML
    void radNewAction(ActionEvent event) {
        recNewSelectionBox.setVisible(false);
        recOldSelectionBox.setVisible(true);
        loadedSeason.setParticipationPrizeRate(0.5f);
        loadedSeason.setSeasonPrizeAwards(newRaceRewardRates);
        loadedSeason.setSeasonPointAwards(newRacePoints);
    }

    @FXML
    void radOldAction(ActionEvent event) {
        recOldSelectionBox.setVisible(false);
        recNewSelectionBox.setVisible(true);
        loadedSeason.setParticipationPrizeRate(0.65f);
        loadedSeason.setSeasonPrizeAwards(oldRaceRewardRates);
        loadedSeason.setSeasonPointAwards(oldRacePoints);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadedSeason = jsonReader.parseSeasonSettings();
        System.out.println("SEASON POINTS LOADING");
        System.out.println("SAVING TO PROFILE: " + loadedSeason.getProfileName());
        positionRewardsNew.clear();
        positionRewardsOld.clear();
        positionPointsNew.clear();
        positionPointsOld.clear();
        loadedSeason.setParticipationPrizeRate(0.5f);
        loadedSeason.setSeasonPrizeAwards(newRaceRewardRates);
        loadedSeason.setSeasonPointAwards(newRacePoints);
        int totalTeams = loadedSeason.getTeamCount();
        int totalDrivers = totalTeams * 2;
        int totalRaces = loadedSeason.getRaceCount();
        positionPointsNew.addAll(
            "1.    25 points",
            "2.    18 points",
            "3.    15 points",
            "4.    12 points",
            "5.    10 points",
            "6.    8  points",
            "7.    6  points",
            "8.    4  points",
            "9.    2  points",
            "10.  1  point"
        );
        positionPointsOld.addAll(
            "1.    10 points",
            "2.    6  points",
            "3.    4  points",
            "4.    3  points",
            "5.    2  points",
            "6.    1  points"
        );
        lstNewPointsView.setItems(positionPointsNew);
        lstOldPointsView.setItems(positionPointsOld);

        for (int i = 0; i < totalDrivers; i++) {
            int positionReward = returnRaceRewardMoney(i, totalRaces, totalTeams, true, false);
            if (i <= 8) {
                if (positionReward > 0)
                    positionRewardsNew.add(i, (i + 1) + ".    € " + positionReward);
            } else {
                if (positionReward > 0)
                    positionRewardsNew.add(i, (i + 1) + ".  € " + positionReward);
            }
        }
        positionRewardsNew.add("");
        positionRewardsNew.add("Participation prize:");
        positionRewardsNew.add("       € " + returnRaceRewardMoney(-1, totalRaces, totalTeams, true, true));

        lstNewRewardsView.setItems(positionRewardsNew);
        for (int i = 0; i < totalDrivers; i++) {
            int positionReward = returnRaceRewardMoney(i, totalRaces, totalTeams, false, false);
            if (i <= 8) {
                if (positionReward > 0)
                    positionRewardsOld.add(i, (i + 1) + ".    € " + positionReward);
            } else {
                if (positionReward > 0)
                    positionRewardsOld.add(i, (i + 1) + ".  € " + positionReward);
            }
        }
        
        positionRewardsOld.add("");
        positionRewardsOld.add("Participation prize:");
        positionRewardsOld.add("       € " + returnRaceRewardMoney(-1, totalRaces, totalTeams, false, true));
        lstOldRewardsView.setItems(positionRewardsOld);
    }

    int returnRaceRewardMoney(int position, int totalRaces, int teamCount, boolean newRaceRewards, boolean  returnParticiaptionPrize) {
        int seasonBudget = loadedSeason.getTotalPrizePool() * 1000000;
        int reward = 0;
        int perRaceBudget = 0;
        int positionPrizeMoney = 0;
        int tempRaceRewards = 0;
        int participationPrize = 0;
        float participationPrizeRate = loadedSeason.getParticipationPrizeRate();
        float posPrizeRate = 1f - participationPrizeRate;
        switch (tempRaceRewards) {
            case 0:
                seasonBudget = seasonBudget / 2;
                if (newRaceRewards) { 
                    perRaceBudget = Math.round((seasonBudget / totalRaces));
                    if (!returnParticiaptionPrize && position < newRaceRewardRates.length) {
                        positionPrizeMoney = Math.round((perRaceBudget * posPrizeRate) * newRaceRewardRates[position]);
                    } else if (returnParticiaptionPrize){
                        participationPrize = Math.round((perRaceBudget * participationPrizeRate) / teamCount);
                    }
                } else {
                    perRaceBudget = Math.round((seasonBudget / totalRaces));
                    if (!returnParticiaptionPrize && position < oldRaceRewardRates.length) {
                        positionPrizeMoney = Math.round((perRaceBudget * 0.35f) * oldRaceRewardRates[position]);
                    } else if (returnParticiaptionPrize){
                        participationPrize = Math.round((perRaceBudget * 0.65f) / teamCount);
                    }
                }
                
                break;
            case 1:
                if (newRaceRewards) { 
                    perRaceBudget = Math.round((seasonBudget / totalRaces) * posPrizeRate);
                    participationPrize = Math.round(((seasonBudget / totalRaces) * participationPrizeRate) / teamCount);
                } else {
                    perRaceBudget = Math.round((seasonBudget / totalRaces) * 0.35f);
                    participationPrize = Math.round(((seasonBudget / totalRaces) * 0.65f) / teamCount);
                }
                
                break;
            case 2:
                
                break;
        
            default:
                break;
        }
        if (raceRewards != 2) {
            if (newRaceRewards) {
                if (returnParticiaptionPrize) {
                    reward = participationPrize;
                } else {
                    reward = positionPrizeMoney;
                }
            } else {
                if (returnParticiaptionPrize) {
                    reward = participationPrize;
                } else {
                    reward = positionPrizeMoney;
                }
            }
        }
        return reward;
    }
}
