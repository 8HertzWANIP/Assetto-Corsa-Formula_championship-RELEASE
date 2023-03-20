package com.ac.menus.facilities;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.JFileChooser;

import com.ac.App;
import com.ac.fileparsing.fileReader;
import com.ac.fileparsing.jsonReader;
import com.ac.fileparsing.jsonWriter;
import com.ac.lib.aeroAPI;
import com.ac.lib.aeroPartInventory;
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
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class privTesting implements Initializable {
    private seasonSettings season = new seasonSettings("", 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, false, false, false, false, null, null);
    public static teamFacilities facilities = null;
    public static aeroPartInventory[] inventory = null;
    private teamSetup playerTeam;
    private ArrayList<String> resultStrings = new ArrayList<String>();
    private ObservableList<String> resultListString = FXCollections.observableArrayList();

    private static int partsToTest = 0;
    private int sessionCost = 0;
    private int[] calcRP = {0, 0};
    private boolean sessionSetup = false;

    @FXML
    private Button btnFinish;

    @FXML
    private Button btnReturn;

    @FXML
    private Button btnSetup;

    @FXML
    private CheckBox chkSimSession;

    @FXML
    private ComboBox<?> cmbSelectedTeam;

    @FXML
    private Label lblCost;

    @FXML
    private Label lblReward;

    @FXML
    private ListView<String> lstTestingParts;

    @FXML
    private Rectangle recTeamColor;

    @FXML
    private TextField txtChampPos;

    @FXML
    private TextField txtChhampPoints;

    @FXML
    private TextField txtMoney;

    @FXML
    private TextField txtResearch;

    @FXML
    private TextField txtTeamName;

    @FXML
    void btnFinishClick(ActionEvent event) {
        int rpReward = ThreadLocalRandom.current().nextInt(calcRP[0], calcRP[1]);
        resultStrings.clear();
        resultListString.clear();
        resultStrings.add("All parts succesfully tested!");
        if (chkSimSession.isSelected()) {
            playerTeam.setMoney(playerTeam.getMoney() - sessionCost);
            txtMoney.setText(uiAPI.setCurrencyFormat(playerTeam.getMoney()));
        }
        resultStrings.add("- €" + uiAPI.setCurrencyFormat(sessionCost));
        resultStrings.add("+ RP: " + rpReward);
        facilities.resDev.addResearchPoints(rpReward);
        inventory = aeroAPI.testInstalledParts(inventory, facilities, 0);
        resultListString.addAll(resultStrings);
        lstTestingParts.setItems(resultListString);
        chkSimSession.setDisable(false);
        if (chkSimSession.isSelected()) {
            btnFinish.setDisable(false);
            btnSetup.setDisable(true);
        } else {
            btnFinish.setDisable(true);
            btnSetup.setDisable(false);
        }
        txtResearch.setText(Integer.toString(facilities.resDev.getResearchPoints()));
        sessionSetup = false;
        setUIVariables(0);
    }

    @FXML
    void btnReturnClick(ActionEvent event) throws IOException {
        if (sessionSetup)
            playerTeam.setMoney(playerTeam.getMoney() + sessionCost);


        jsonWriter.savePartsToJson(inventory, playerTeam);
        jsonWriter.saveFacilitiesToJson(facilities, playerTeam);
        jsonWriter.saveTeam(playerTeam);
        App.setRoot(new FXMLLoader(getClass().getResource("/Facilities.fxml")));

    }

    @FXML
    void btnSetupClick(ActionEvent event) {
        getFailedParts(inventory);
        chkSimSession.setDisable(true);
        btnSetup.setDisable(true);
        btnFinish.setDisable(false);
        playerTeam.setMoney(playerTeam.getMoney() - sessionCost);
        txtMoney.setText(uiAPI.setCurrencyFormat(playerTeam.getMoney()));
        sessionSetup = true;
    }

    @FXML
    void chkSimSessionToggle(ActionEvent event) {
        if (chkSimSession.isSelected()) {
            btnFinish.setDisable(false);
            btnSetup.setDisable(true);
        } else {
            btnFinish.setDisable(true);
            btnSetup.setDisable(false);
        }
    }

    @FXML
    void cmbSelectTeam(ActionEvent event) {

    }

    @FXML
    void txtResearchChanged(KeyEvent event) {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        season = jsonReader.parseSeasonSettings();
        cmbSelectedTeam.setVisible(false);
        playerTeam = uiAPI.loadPlayerTeam(season);
        File facilitiesJson = new File(new JFileChooser().getFileSystemView().getDefaultDirectory().toString() + "\\Ac Cardev save data\\" + App.loadedProfile + "\\" + playerTeam.teamName + "\\facilities.json");
        if (!facilitiesJson.exists()){
            jsonWriter.createFacilityJson(playerTeam, season);
        }
        facilities = jsonReader.parseFacilities(playerTeam);
        txtResearch.setText(Integer.toString(facilities.resDev.getResearchPoints()));

        File inventoryJson = new File(new JFileChooser().getFileSystemView().getDefaultDirectory().toString() + "\\Ac Cardev save data\\" + App.loadedProfile + "\\" + playerTeam.teamName + "\\part Inventory\\aero parts.json");
        if (!inventoryJson.exists()){
            fileReader.loadAeroIniParts(playerTeam, season);
        }
        inventory =  jsonReader.parseAeroParts(playerTeam);

        // Set player team UI elements
        txtChampPos.setText(Integer.toString(playerTeam.getChampPos()));
        txtChhampPoints.setText(Integer.toString(playerTeam.getPoints()));
        txtMoney.setText(uiAPI.setCurrencyFormat(playerTeam.getMoney()));
        txtResearch.setText(Integer.toString(facilities.resDev.getResearchPoints()));
        recTeamColor.setFill(Color.valueOf(playerTeam.getTeamColor()));
        txtTeamName.setText(playerTeam.getTeamName());
        getFailedParts(inventory);
        
        // Calculate RP reward + testing costs
        setUIVariables(partsToTest);

        if (playerTeam.getMoney() < sessionCost) {
            btnSetup.setDisable(true);
            chkSimSession.setDisable(true);
        }
    }

    private void setUIVariables(int parts) {
        calcRP = teamFacilities.returnPrivTestingRP(parts, facilities, season);
        lblReward.setText("RP reward: " + calcRP[0] + " - " + calcRP[1]);
        
        int costMultiplyer = 35000;
        int flatCosts = season.getTotalPrizePool() * costMultiplyer;
        sessionCost = Math.round((float) flatCosts / season.getRaceCount());
        lblCost.setText("Cost: €" + uiAPI.setCurrencyFormat(sessionCost));
    }
    
    public void getFailedParts(aeroPartInventory[] inventory) {
        boolean partFound = false;
        boolean flawlessVictory = true;
        partsToTest = 0;
        resultStrings.clear();
        resultListString.clear();
        for (int i = 0; i < inventory.length; i++) {
            partFound = false;
            if(inventory[i].installed.stats_hidden) {
                if (!partFound) {
                    resultStrings.add(inventory[i].part_name);
                }
                partFound = true;
                flawlessVictory = false;
                resultStrings.add("-- part stats unknown");
            }
            if(inventory[i].installed.improvement_hidden) {
                if (!partFound) {
                    resultStrings.add(inventory[i].part_name);
                }
                partFound = true;
                flawlessVictory = false;
                resultStrings.add("-- improvement potential unknown");
            }
            partsToTest = i;
        }
        if (flawlessVictory) {
            partsToTest = 0;
            resultStrings.add("All installed parts succesfully tested!");
            resultStrings.add("-- Extra RP reward");
        }
        resultListString.addAll(resultStrings);
        lstTestingParts.setItems(resultListString);
    }

}
