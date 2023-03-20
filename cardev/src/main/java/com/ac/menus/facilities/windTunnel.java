package com.ac.menus.facilities;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class windTunnel implements Initializable {

    private boolean sandboxMode = false;
    private seasonSettings season = new seasonSettings("", 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, false, false, false, false, null, null);
    public static teamFacilities facilities = null;
    public static aeroPartInventory[] inventory = null;
    private teamSetup playerTeam;
    private ArrayList<String> resultStrings = new ArrayList<String>();
    private ObservableList<String> resultListString = FXCollections.observableArrayList();

    @FXML
    private AnchorPane ancInventoryPane;

    @FXML
    private Button btnResearch;

    @FXML
    private Button btnReturn;

    @FXML
    private Button btnTestParts;

    @FXML
    private Button btnUpgrade;

    @FXML
    private ComboBox<?> cmbSelectedTeam;
    
    @FXML
    private ListView<String> lstTestResults;

    @FXML
    private Label lblFacilityName;

    @FXML
    private Label lblResearchCost;

    @FXML
    private Label lblResearchRP;

    @FXML
    private Label lblTestCost;

    @FXML
    private Label lblFailureChance;

    @FXML
    private Label lblUpgradeCost;

    @FXML
    private Label lblTunnelLevel;

    @FXML
    private Label lblTunnelUses;

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
    void ancInventoryPaneClick(MouseEvent event) {

    }

    @FXML
    void btnResearchClick(ActionEvent event) {
        facilities.resDev.setResearchPoints(facilities.resDev.getResearchPoints() + teamFacilities.returnTunnelCost("researchRP", season, facilities));
        playerTeam.setMoney(playerTeam.getMoney() - teamFacilities.returnTunnelCost("research", season, facilities));
        txtResearch.setText(Integer.toString(facilities.resDev.getResearchPoints()));
        txtMoney.setText(uiAPI.setCurrencyFormat(playerTeam.getMoney()));
        facilities.wTunnel.chargesSpent++;
        lblTunnelUses.setText((facilities.wTunnel.charges - facilities.wTunnel.chargesSpent) + " / " + facilities.wTunnel.charges);
        disableButtons();
    }

    @FXML
    void btnReturnClick(ActionEvent event) throws IOException {
        jsonWriter.saveFacilitiesToJson(facilities, playerTeam);
        jsonWriter.savePartsToJson(inventory, playerTeam);
        jsonWriter.saveTeam(playerTeam);
        App.setRoot(new FXMLLoader(getClass().getResource("/Facilities.fxml")));

    }

    @FXML
    private void btnTestPartsClick(ActionEvent event) {
        float failureChance = facilities.wTunnel.getFailureChance();
        inventory = aeroAPI.testPartsInTunnel(inventory, facilities, failureChance);
        getFailedParts(inventory);
        playerTeam.setMoney(playerTeam.getMoney() - teamFacilities.returnTunnelCost("testParts", season, facilities));
        txtMoney.setText(uiAPI.setCurrencyFormat(playerTeam.getMoney()));
        facilities.wTunnel.chargesSpent++;
        lblTunnelUses.setText((facilities.wTunnel.charges - facilities.wTunnel.chargesSpent) + " / " + facilities.wTunnel.charges);
        disableButtons();
    }

    @FXML
    void btnUpgradeClick(ActionEvent event) {
        facilities.wTunnel.level++;

        playerTeam.setMoney(playerTeam.getMoney() - teamFacilities.returnTunnelCost("levelup", season, facilities));
        txtMoney.setText(uiAPI.setCurrencyFormat(playerTeam.getMoney()));
        lblFailureChance.setText("Success chance: " + Float.toString(100f - facilities.wTunnel.getFailureChance()) + "%");
        lblResearchRP.setText("RP: " + Integer.toString(teamFacilities.returnTunnelCost("researchRP", season, facilities)));
        lblResearchCost.setText("Cost: €" + uiAPI.setCurrencyFormat(teamFacilities.returnTunnelCost("research", season, facilities)));
        lblUpgradeCost.setText("Cost: €" + uiAPI.setCurrencyFormat(teamFacilities.returnTunnelCost("levelup", season, facilities)));
        lblTestCost.setText("Cost: €" + uiAPI.setCurrencyFormat(teamFacilities.returnTunnelCost("testParts", season, facilities)));
        lblTunnelLevel.setText("Level: " + facilities.wTunnel.level);
        facilities.wTunnel.setCharges(season.getCurrentRace());
        lblTunnelUses.setText((facilities.wTunnel.charges - facilities.wTunnel.chargesSpent) + " / " + facilities.wTunnel.charges);
        disableButtons();
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
        if (sandboxMode) {
            cmbSelectedTeam.setVisible(true);
        } else {
            cmbSelectedTeam.setVisible(false);
            playerTeam = uiAPI.loadPlayerTeam(season);
            File facilitiesJson = new File(new JFileChooser().getFileSystemView().getDefaultDirectory().toString() + "\\Ac Cardev save data\\" + App.loadedProfile + "\\" + playerTeam.teamName + "\\facilities.json");
            if (!facilitiesJson.exists()){
                jsonWriter.createFacilityJson(playerTeam, season);
            }
            facilities = jsonReader.parseFacilities(playerTeam);
            txtResearch.setText(Integer.toString(facilities.resDev.getResearchPoints()));
        }
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

        // Set tunnel UI elements
        facilities.wTunnel.setCharges(season.getCurrentRace());
        lblTunnelUses.setText((facilities.wTunnel.charges - facilities.wTunnel.chargesSpent) + " / " + facilities.wTunnel.charges);
        lblFailureChance.setText("Success chance: " + Float.toString(100f - facilities.wTunnel.getFailureChance()) + "%");
        lblResearchRP.setText("RP: " + Integer.toString(teamFacilities.returnTunnelCost("researchRP", season, facilities)));
        lblResearchCost.setText("Cost: €" + uiAPI.setCurrencyFormat(teamFacilities.returnTunnelCost("research", season, facilities)));
        lblUpgradeCost.setText("Cost: €" + uiAPI.setCurrencyFormat(teamFacilities.returnTunnelCost("levelup", season, facilities)));
        lblTestCost.setText("Cost: €" + uiAPI.setCurrencyFormat(teamFacilities.returnTunnelCost("testParts", season, facilities)));
        lblTunnelLevel.setText("Level: " + facilities.wTunnel.level);
        disableButtons();
    }

    private void disableButtons() {
        if (teamFacilities.returnTunnelCost("research", season, facilities) < playerTeam.getMoney() && facilities.wTunnel.charges > facilities.wTunnel.chargesSpent) {
            btnResearch.setDisable(false);
        } else if(facilities.wTunnel.charges <= facilities.wTunnel.chargesSpent) {
            btnResearch.setDisable(true);
        } else {
            btnResearch.setDisable(true);
        }
        if (teamFacilities.returnTunnelCost("levelup", season, facilities) < playerTeam.getMoney() && facilities.wTunnel.charges > facilities.wTunnel.chargesSpent) {
            btnUpgrade.setDisable(false);
        } else if(facilities.wTunnel.charges <= facilities.wTunnel.chargesSpent) {
            btnUpgrade.setDisable(true);
        } else {
            btnUpgrade.setDisable(true);
        }
        if (teamFacilities.returnTunnelCost("testParts", season, facilities) < playerTeam.getMoney() && facilities.wTunnel.charges > facilities.wTunnel.chargesSpent && aeroAPI.countUntestedParts(inventory) > -1) {
            btnTestParts.setDisable(false);
        } else if(facilities.wTunnel.charges <= facilities.wTunnel.chargesSpent || aeroAPI.countUntestedParts(inventory) == -1) {
            btnTestParts.setDisable(true);
        } else {
            btnTestParts.setDisable(true);
        }
        
    }
    
    public void getFailedParts(aeroPartInventory[] inventory) {
        boolean partFound = false;
        boolean flawlessVictory = true;
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
                resultStrings.add("-- stats_hidden");
            }
            if(inventory[i].installed.improvement_hidden) {
                if (!partFound) {
                    resultStrings.add(inventory[i].part_name);
                }
                partFound = true;
                flawlessVictory = false;
                resultStrings.add("-- improvement_hidden");
            }
            if (Objects.nonNull(inventory[i].constructed)) {
                if(inventory[i].constructed.stats_hidden) {
                    if (!partFound) {
                            resultStrings.add(inventory[i].part_name);
                    }
                    partFound = true;
                    flawlessVictory = false;
                    resultStrings.add("-- stats_hidden");
                }
                if(inventory[i].constructed.improvement_hidden) {
                    if (!partFound) {
                            resultStrings.add(inventory[i].part_name);
                    }
                    partFound = true;
                    flawlessVictory = false;
                    resultStrings.add("-- improvement_hidden");
                }
            }
        }
        if (flawlessVictory) {
            resultStrings.add("All parts succesfully tested!");
            resultStrings.add("-- No failures");
        }
        resultListString.addAll(resultStrings);
        lstTestResults.setItems(resultListString);
    }
}
