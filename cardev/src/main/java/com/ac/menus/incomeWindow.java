package com.ac.menus;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

import com.ac.App;
import com.ac.fileparsing.jsonReader;
import com.ac.fileparsing.jsonWriter;
import com.ac.lib.aeroAPI;
import com.ac.lib.aeroPartInventory;
import com.ac.menus.facilities.teamFacilities;
import com.ac.seasons.newSeason.seasonSettings;
import com.ac.seasons.newSeason.teamSetup;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

public class incomeWindow implements Initializable {

    private ObservableList<String> rpIncomeList = FXCollections.observableArrayList();
    private ObservableList<String> moneyIncomeList = FXCollections.observableArrayList();
    private ObservableList<String> pointsIncomeList = FXCollections.observableArrayList();
    private teamFacilities facilities = null;
    private seasonSettings season = null;
    private teamSetup playerTeam = null;
    private aeroPartInventory[] inventory = null;

    @FXML
    private Button btnContinue;

    @FXML
    private ListView<String> lstMoney;

    @FXML
    private ListView<String> lstPoints;

    @FXML
    private ListView<String> lstRp;

    @FXML
    void btnContinueClick(ActionEvent event) throws IOException {
        season.setRaceCanceled(false);
        facilities.wTunnel.chargesSpent = 0;
        facilities.resDev.setResearchPoints(facilities.resDev.getResearchPoints() + teamFacilities.giveFacilityRP("resDev", facilities, season));
        jsonWriter.saveFacilitiesToJson(facilities, playerTeam);
        jsonWriter.saveSeasonSettings(season);
        jsonWriter.savePartsToJson(inventory, playerTeam);
        if (season.getCurrentRace() >= season.getRaceCount())
            App.setRoot("endOfSeason");
        else
            App.setRoot("teamMenu");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        season = jsonReader.parseSeasonSettings();
        playerTeam = jsonReader.parseTeam(season.getPlayerTeam());
        facilities = jsonReader.parseFacilities(playerTeam);
        inventory = aeroAPI.testInstalledParts(jsonReader.parseAeroParts(playerTeam), facilities, 0);
        if (!Objects.isNull(champPoints.playerIndex) && !Objects.isNull(champPoints.pointListToAdd)) {
            rpIncomeList.addAll("Research and Development: ", Integer.toString(teamFacilities.giveFacilityRP("resDev", facilities, season)));
            moneyIncomeList.addAll(champPoints.prizeMoneyStrings);
            pointsIncomeList.addAll(champPoints.finishingPositionStrings);
            lstMoney.setItems(moneyIncomeList);
            lstRp.setItems(rpIncomeList);
            lstPoints.setItems(pointsIncomeList);
        } else if (!season.preseasonTestingCompleted) {
            season.setPreseasonTestingCompleted(true);
            rpIncomeList.addAll("Research and Development: ", Integer.toString(teamFacilities.giveFacilityRP("resDev", facilities, season)));
            lstRp.setItems(rpIncomeList);
        }
    }
}
