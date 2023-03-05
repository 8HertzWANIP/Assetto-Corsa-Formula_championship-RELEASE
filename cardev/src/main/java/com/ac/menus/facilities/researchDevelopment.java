package com.ac.menus.facilities;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

import javax.swing.JFileChooser;

import com.ac.App;
import com.ac.fileparsing.fileReader;
import com.ac.fileparsing.fileWriter;
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
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;

public class researchDevelopment extends App implements Initializable{

    public static boolean createNewPart = false;
    public static String partsToLoad = "installed";
    public static int uiIndex = 0;
    public static int selectedPartIndex = -1;
    public static int previousPartIndex = -1;
    public static int displayedPartIndex = 0;
    public static seasonSettings season = new seasonSettings("", 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, false, false, false, false, null, null);
    public static aeroPartInventory[] inventory = null;
    public static teamFacilities facilities = null;
    public static teamSetup playerTeam;
    private aeroPartInventory.part tempPart = null;
    private aeroPartInventory[] tempInv = null;
    private int improvementPoints = 0;
    private int newPartCost = 0;
    private int partCost = 0;
    private int partRpCost = 0;
    private int maxPartPages = 0;
    private boolean sandboxMode = false;
    private boolean newCarBuilt = false;
    private boolean createPartsOpen = false;
    private ArrayList<Pane> installedPartPanes = new ArrayList<Pane>();
    private ArrayList<Pane> newPartPanes = new ArrayList<Pane>();
    
    @FXML
    private AnchorPane ancInventoryPane;

    @FXML
    private AnchorPane ancPartSlider;

    @FXML
    private Button btnCreateCar;

    @FXML
    private Button btnCreateNewPart;

    @FXML
    private Button btnInstallParts;

    @FXML
    private Button btnInstalledParts;

    @FXML
    private Button btnCancelInstall;

    @FXML
    private Button btnCancelCreate;

    @FXML
    private Button btnLoadAeroParts;

    @FXML
    private Button btnNextPage;

    @FXML
    private Button btnNewPartsInventory;

    @FXML
    private Button btnPrevPage;

    @FXML
    private Button btnReturn;

    @FXML
    private Button btnSaveParts;

    @FXML
    private Button btnSaveToFile;

    @FXML
    private ComboBox<String> cmbSelectedTeam;

    @FXML
    private ComboBox<String> cmbSelectedPart;

    @FXML
    private Label lblAverage21211;

    @FXML
    private Label lblCost;

    @FXML
    private Label lblCostStatic;

    @FXML
    private Label lblCostNew;

    @FXML
    private Label lblImproveCreatePart;

    @FXML
    private Label lblRdExpNmbr;

    @FXML
    private Label lblRdLevel;

    @FXML
    private Label lblSelectedPartAverageScore;

    @FXML
    private Label lblSelectedPartDf;

    @FXML
    private Label lblSelectedPartDr;

    @FXML
    private Label lblSelectedPartImprovement;

    @FXML
    private Label lblSelectedPartName;

    @FXML
    private Label lblSelectedPartPage;

    @FXML
    private Label lblSelectedPartTestTip;

    @FXML
    private Pane panNewPartPabe;

    @FXML
    private Pane panUpgradePartPane;

    @FXML
    private ProgressBar prgPotential;

    @FXML
    private ProgressBar prgPotentialProjected;

    @FXML
    private ProgressBar prgRdExp;

    @FXML
    private Rectangle recSelectedPartRarity;

    @FXML
    private Rectangle recTeamColor;

    @FXML
    private Slider slrDragDownforce;

    @FXML
    private TextField txtChampPos;

    @FXML
    private TextField txtChhampPoints;

    @FXML
    private TextField txtMoney;

    @FXML
    private TextField txtRP;

    @FXML
    private TextField txtResearch;

    @FXML
    private TextField txtTeamName;

    
/* 
//////////// ---------------------- \\\\\\\\\\\\
|||||||||||| JAVAFX ELEMENT METHODS ||||||||||||
\\\\\\\\\\\\ ---------------------- ////////////
*/   
    /** Event to refresh selected aero part
     * @param event
     */
    @FXML
    public void ancInventoryPaneClick(MouseEvent event) {
        // facilities.resDev.setResearchPoints(uiAPI.returnTextInt(txtRP));
        if (inventory != null && !createNewPart) {
            if (createPartsOpen) {
                tempInv = null;
                tempPart = null;
                txtRP.setText("0");
                panNewPartPabe.setVisible(false);
                createPartsOpen = false;
            }
            selectPartFromInventory();
        }
    }

    @FXML
    void btnCreateCarClick(ActionEvent event) throws IOException {
        btnSaveParts.setText("Create new car");
        createPartsOpen = true;
        tempInv = null;
        tempPart = null;
        newCarBuilt = false;
        createNewCar();
    }
    
    @FXML
    void btnCreateNewPartClick(ActionEvent event) throws IOException {
        btnSaveParts.setText("Create new part");
        createPartsOpen = true;
        cmbSelectedPart.setVisible(true);
    }

    @FXML
    void btnLoadAeroPartsClick(ActionEvent event) throws IOException {
        inventory =  jsonReader.parseAeroParts(playerTeam);
        generateAeroPartsUi(false);
        selectPartFromInventory();
    }

    @FXML
    void btnNextPageClick(ActionEvent event) {
        if (inventory.length > 9) {
            if (partsToLoad.equals("installed"))
                setPartUiPage(2, true);
            else
                setPartUiPage(2, false);
        }
    }

    @FXML
    void btnPrevPageClick(ActionEvent event) {
        if (inventory.length > 9) {
            if (partsToLoad.equals("installed"))
                setPartUiPage(1, true);
            else
                setPartUiPage(1, false);
        }
    }

    @FXML
    void btnNewPartsInventoryClick(ActionEvent event) throws IOException {
        partsToLoad = "constructed";
        for (Pane newPartPane : newPartPanes) {
            newPartPane.setVisible(true);
            newPartPane.setDisable(false);
        }
        for (Pane installedPane : installedPartPanes) {
            installedPane.setVisible(false);
            installedPane.setDisable(true);
        }
        generateAeroPartsUi(false);
        setPartUiPage(1, false);
    }

    @FXML
    void btnInstalledPartsClick(ActionEvent event) throws IOException {
        partsToLoad = "installed";
        for (Pane installedPane : installedPartPanes) {
            installedPane.setVisible(true);
            installedPane.setDisable(false);
        }
        for (Pane newPartPane : newPartPanes) {
            newPartPane.setVisible(false);
            newPartPane.setDisable(true);
        }
        generateAeroPartsUi(false);
        setPartUiPage(1, true);
    }

    @FXML
    void btnReturnClick(ActionEvent event) throws IOException {
        saveFileData();
        App.setRoot("Facilities");

    }

    @FXML
    void btnSavePartsClick(ActionEvent event) throws IOException {
        if (newPartCost <= playerTeam.getMoney()) {
            if (!newCarBuilt) {
                inventory[selectedPartIndex].constructed = tempPart;
                tempPart = null;
                playerTeam.setMoney(playerTeam.getMoney() - newPartCost);
                txtMoney.setText(uiAPI.setCurrencyFormat(playerTeam.getMoney()));
                giveResDevExperience("newPart", -1);
                generateAeroPartsUi(true);
                resetPartUI();
                setPartUiPage(1, false);
                setPartUiPage(1, true);
            } else {
                for (int i = 0; i < inventory.length; i++) {
                    tempInv = inventory;
                    tempInv[i].constructed = aeroAPI.generateNewAeroPart(season, inventory, i, facilities.resDev.getLevel());
                    giveResDevExperience("newPart", -1);
                }
                newCarBuilt = false;
                tempInv = null;
                playerTeam.setMoney(playerTeam.getMoney() - newPartCost);
                txtMoney.setText(uiAPI.setCurrencyFormat(playerTeam.getMoney()));
                generateAeroPartsUi(true);
                resetPartUI();
                setPartUiPage(1, false);
                setPartUiPage(1, true);
            }
            saveFileData();
        }
    }

    @FXML
    void btnCancelCreateClick(ActionEvent event) throws IOException {
        resetPartUI();
    }
    
    @FXML
    void btnCancelInstallClick(ActionEvent event) throws IOException {
        txtRP.setText("0");
        resetPartUI();
    }
    
    @FXML
    void btnInstallPartsClick(ActionEvent event) throws IOException {
        // When a part is installed, the "install part" button is replaced in functionality with the Improve Part methods.
        if(partsToLoad.equals("installed")) {
            if (partCost <= playerTeam.getMoney() && partRpCost <= facilities.resDev.getResearchPoints()) {
                inventory[selectedPartIndex].installed = improvePart(improvementPoints, slrDragDownforce.getValue());
                giveResDevExperience("improvePart", partRpCost);
                generateAeroPartsUi(true);
                txtRP.setText("0");
                resetPartUI();
                // selectedPartIndex = 99;
                // selectPartFromInventory();
                saveFileData();
            }
        } 
        // Install constructed part functionality
        else {
            aeroPartInventory.part tempPart = new aeroPartInventory.part(0, 0, 0);
            tempPart = inventory[selectedPartIndex].installed;
            inventory[selectedPartIndex].installed = inventory[selectedPartIndex].constructed;
            inventory[selectedPartIndex].constructed = tempPart;
            jsonWriter.savePartsToJson(inventory, playerTeam);
            generateAeroPartsUi(true);
            selectPartFromInventory();
        }
    }

    @FXML
    void btnSaveToFileClick(ActionEvent event) {
        fileReader.loadAeroIniParts(playerTeam, season);
    }

    @FXML
    void cmbSelectTeam(ActionEvent event) {

    }

    @FXML
    void cmbSelectPart(ActionEvent event) {
        tempInv = null;
        tempPart = null;
        newCarBuilt = false;
        System.out.println(cmbSelectedPart.getValue());
        System.out.println(retunPartIndex(cmbSelectedPart.getValue()));
        selectedPartIndex = retunPartIndex(cmbSelectedPart.getValue());
        cmbSelectedPart.setVisible(false);
        createNewPart = true;
        createNewPart();
    }


    private static int retunPartIndex(String input) {
        int index = 0;

        for (int i = 0; i < inventory.length; i++) {
            if (input.equals(inventory[i].part_name)) {
                index = inventory[i].id;
            }
        }
        return index;
    }

    @FXML
    void slrDragDownforceChanged(MouseEvent event) {

    }

    @FXML
    void txtRPAction(ActionEvent event) {
        improvementPoints = calcImprovementCosts(uiAPI.returnTextInt(txtRP));
    }

    @FXML
    void txtRPChanged(KeyEvent event) {
        if (txtRP.getText().equals(""))
            txtRP.setText("0");
    }

    @FXML
    void txtResearchChanged(KeyEvent event) {

    }

/* 
//////////// ---------------------------------------- \\\\\\\\\\\\
|||||||||||| LOAD UI ELEMENTS, TEAMS, AND  FACILITIES ||||||||||||
\\\\\\\\\\\\ ---------------------------------------- ////////////
*/

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        season = jsonReader.parseSeasonSettings();
        if (sandboxMode) {
            cmbSelectedTeam.setVisible(true);
            loadTeamlistCombobox();
        } else {
            cmbSelectedTeam.setVisible(false);
            playerTeam = uiAPI.loadPlayerTeam(season);
            File facilitiesJson = new File(new JFileChooser().getFileSystemView().getDefaultDirectory().toString() + "\\Ac Cardev save data\\" + loadedProfile + "\\" + playerTeam.teamName + "\\facilities.json");
            if (!facilitiesJson.exists()){
                jsonWriter.createFacilityJson(playerTeam, season);
            }
            facilities = jsonReader.parseFacilities(playerTeam);
            txtResearch.setText(Integer.toString(facilities.resDev.getResearchPoints()));
        }
        File inventoryJson = new File(new JFileChooser().getFileSystemView().getDefaultDirectory().toString() + "\\Ac Cardev save data\\" + loadedProfile + "\\" + playerTeam.teamName + "\\part Inventory\\aero parts.json");
        if (!inventoryJson.exists()){
            fileReader.loadAeroIniParts(playerTeam, season);
        }
        inventory =  jsonReader.parseAeroParts(playerTeam);
        // testAllParts();

        // Set player team UI elements
        txtChampPos.setText(Integer.toString(playerTeam.getChampPos()));
        txtChhampPoints.setText(Integer.toString(playerTeam.getPoints()));
        txtMoney.setText(uiAPI.setCurrencyFormat(playerTeam.getMoney()));
        txtRP.setText(Integer.toString(facilities.resDev.getResearchPoints()));
        recTeamColor.setFill(Color.valueOf(playerTeam.getTeamColor()));
        txtTeamName.setText(playerTeam.getTeamName());
        // Generate the UI for the research and development screen.
        try {
            generateAeroPartsUi(false);
            if (season.getCurrentRace() > 0)
                btnCreateCar.setVisible(false);
            else
                btnCreateCar.setVisible(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //set numbers only textfields
        uiAPI.setNumbersOnly(new TextField[] {txtRP});

        // Loads the inventory on facility load
        selectPartFromInventory();
        loadPartlistCombobox();
        maxPartPages = inventory.length / 9 + ((inventory.length % 9 == 0) ? 0 : 1);
        lblSelectedPartPage.setText("Page 1 / " + maxPartPages);
        // Load resDev facility info and add it in the UI
        lblRdExpNmbr.setText(Integer.toString(facilities.resDev.getExperience()) + " / " + teamFacilities.resDevLevelExpValues(season)[teamFacilities.getResDevLevelFromExp(facilities.resDev.getExperience(), season) + 1]);
        lblRdLevel.setText("Level: " + Integer.toString(teamFacilities.getResDevLevelFromExp(facilities.resDev.getExperience(), season)));
        float currentExp = (long) facilities.resDev.getExperience() - teamFacilities.resDevLevelExpValues(season)[facilities.resDev.getLevel()];
        float maxExp = (long) teamFacilities.resDevLevelExpValues(season)[facilities.resDev.getLevel() + 1] - teamFacilities.resDevLevelExpValues(season)[facilities.resDev.getLevel()];
        float progressBarValue = currentExp / maxExp;
        System.out.println("level [" + facilities.resDev.getLevel() + "]");
        System.out.println("currentExp [" + currentExp + "]");
        System.out.println("maxExp [" + maxExp + "]");
        System.out.println("progressBarValue [" + progressBarValue + "]");
        if (progressBarValue > 0.99)
            progressBarValue = 0.99f;
        prgRdExp.setProgress(progressBarValue);
        resetPartUI();
    }
    
    /** Generates all the aerodynamic parts as UI objects on the screen
     * @throws IOException
     */
    private void generateAeroPartsUi(boolean resetUI) throws IOException {
        if (resetUI) {
            for (Pane pane : installedPartPanes) {
                pane.setVisible(false);
            }
            for (Pane pane : newPartPanes) {
                pane.setVisible(false);
            }
            installedPartPanes = new ArrayList<Pane>();
            newPartPanes = new ArrayList<Pane>();
        }
        if (partsToLoad.equals("installed") && installedPartPanes.size() == 0) {
            for (uiIndex = 0; uiIndex < inventory.length; uiIndex++) {
                if (uiIndex < 10) {
                    URL url = new File("cardev/src/main/resources/com/ac/aeropartObject.fxml").toURI().toURL();
                    Pane newLoadedPane =  FXMLLoader.load(url);
                    newLoadedPane.setLayoutX(320);
                    newLoadedPane.setLayoutY(257 + (105 * uiIndex));
                    if (uiIndex > 2 && uiIndex <= 5) {
                        newLoadedPane.setLayoutX(540);
                        newLoadedPane.setLayoutY(257 + (105 * (uiIndex - 3)));
                    } else if (uiIndex > 5) {
                        newLoadedPane.setLayoutX(760);
                        newLoadedPane.setLayoutY(257 + (105 * (uiIndex - 6)));
                    }
                    newLoadedPane.setVisible(true);
                    installedPartPanes.add(newLoadedPane);
                    ancInventoryPane.getChildren().add(newLoadedPane);
                }
            }
            for (uiIndex = 9; uiIndex < inventory.length; uiIndex++) {
                System.out.println("index [" + uiIndex + "]");
                URL url = new File("cardev/src/main/resources/com/ac/aeropartObject.fxml").toURI().toURL();
                Pane newLoadedPane =  FXMLLoader.load(url);
                newLoadedPane.setLayoutX(320);
                newLoadedPane.setLayoutY(257 + (105 * (uiIndex - 9)));
                if (uiIndex > 11 && uiIndex <= 14) {
                    newLoadedPane.setLayoutX(540);
                    newLoadedPane.setLayoutY(257 + (105 * (uiIndex - 3)));
                } else if (uiIndex > 14) {
                    newLoadedPane.setLayoutX(760);
                    newLoadedPane.setLayoutY(257 + (105 * (uiIndex - 6)));
                }
                newLoadedPane.setVisible(false);
                installedPartPanes.add(newLoadedPane);
                ancInventoryPane.getChildren().add(newLoadedPane);
            }
            setPartUiPage(1, true);
        } else if (partsToLoad.equals("constructed") && newPartPanes.size() == 0) {
            for (uiIndex = 0; uiIndex < inventory.length; uiIndex++) {
                if (uiIndex < 10) {
                    URL url = new File("cardev/src/main/resources/com/ac/aeropartObject.fxml").toURI().toURL();
                    Pane newLoadedPane =  FXMLLoader.load(url);
                    newLoadedPane.setLayoutX(320);
                    newLoadedPane.setLayoutY(257 + (105 * uiIndex));
                    if (uiIndex > 2 && uiIndex <= 5) {
                        newLoadedPane.setLayoutX(540);
                        newLoadedPane.setLayoutY(257 + (105 * (uiIndex - 3)));
                    } else if (uiIndex > 5) {
                        newLoadedPane.setLayoutX(760);
                        newLoadedPane.setLayoutY(257 + (105 * (uiIndex - 6)));
                    }
                    newLoadedPane.setVisible(true);
                    newPartPanes.add(newLoadedPane);
                    ancInventoryPane.getChildren().add(newLoadedPane);
                }
            }
            for (uiIndex = 9; uiIndex < inventory.length; uiIndex++) {
                System.out.println("index [" + uiIndex + "]");
                URL url = new File("cardev/src/main/resources/com/ac/aeropartObject.fxml").toURI().toURL();
                Pane newLoadedPane =  FXMLLoader.load(url);
                newLoadedPane.setLayoutX(320);
                newLoadedPane.setLayoutY(257 + (105 * (uiIndex - 9)));
                if (uiIndex > 11 && uiIndex <= 14) {
                    newLoadedPane.setLayoutX(540);
                    newLoadedPane.setLayoutY(257 + (105 * (uiIndex - 3)));
                } else if (uiIndex > 14) {
                    newLoadedPane.setLayoutX(760);
                    newLoadedPane.setLayoutY(257 + (105 * (uiIndex - 6)));
                }
                newLoadedPane.setVisible(false);
                newPartPanes.add(newLoadedPane);
                ancInventoryPane.getChildren().add(newLoadedPane);
            }
            setPartUiPage(1, false);
        }
    }

    private void setPartUiPage(int index, boolean installedParts) {
        lblSelectedPartPage.setText("Page " + index + " / " + maxPartPages);
        int loopIndex = 0;
        if (index > 1) {
            loopIndex = 9 * (index - 1);
        }

        ArrayList<Pane> targetPanes = new ArrayList<Pane>();
        if (installedParts) {
            targetPanes = installedPartPanes;
        }
        else {
            targetPanes = newPartPanes;
        }
        System.out.println("Part cards to load: [" + targetPanes.size() + "]");
        System.out.println("loopIndex: [" + loopIndex + "]");
        for (int i = 0; i < targetPanes.size(); i++) {
            targetPanes.get(i).setVisible(false);
        }

        for (int i = loopIndex; i < (9 * index) && i < targetPanes.size(); i++) {
            if (i < (9 * index)) {
                System.out.println("index: [" + i + "]");
                targetPanes.get(i).setVisible(true);
            }
        }
    }

    private void loadTeamlistCombobox() {
        File file = new File(new JFileChooser().getFileSystemView().getDefaultDirectory().toString() + "\\Ac Cardev save data\\" + loadedProfile);
        String[] directories = file.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
        });
        ObservableList<String> teams = FXCollections.observableArrayList(directories);
        cmbSelectedTeam.setItems(teams);
    }

    private void loadPartlistCombobox() {
        ArrayList<String> partList = new ArrayList<String>();
        for (int i = 0; i < inventory.length; i++) {
            partList.add(inventory[i].part_name);
        }
        cmbSelectedPart.setItems(FXCollections.observableList(partList));
    }

/* 
//////////// --------------------------- \\\\\\\\\\\\
|||||||||||| PART CREATION & IMPROVEMENT ||||||||||||
\\\\\\\\\\\\ --------------------------- ////////////
*/
    public void createNewPart() {
        tempPart = aeroAPI.generateNewAeroPart(season, inventory, selectedPartIndex, facilities.resDev.getLevel());
        int[] phImprovement = {0, 0};
        int cost = Math.round(((float) season.totalPrizePool * 24000) / inventory.length);
        lblCostNew.setText("€ " + uiAPI.setCurrencyFormat(cost));
        panUpgradePartPane.setVisible(false);
        panNewPartPabe.setVisible(true);
        ancPartSlider.setVisible(false);
        newPartCost = cost;
        loadPartStats(tempPart, inventory[selectedPartIndex].part_name, true, 1d, phImprovement);
    }
    public void createNewCar() {
        newCarBuilt = true;
        newPartCost = 0;
        for (int i = 0; i < inventory.length; i++) {
            int cost = Math.round(((float) season.totalPrizePool * 24000) / inventory.length);
            newPartCost += cost;
            lblCostNew.setText("€ " + uiAPI.setCurrencyFormat(newPartCost));
            panUpgradePartPane.setVisible(false);
            panNewPartPabe.setVisible(true);
            ancPartSlider.setVisible(false);
        }
    }

    private aeroPartInventory.part improvePart(int improvementPoints, double sliderValue) {
        aeroPartInventory.part targetPart = null;
        if (partsToLoad.equals("installed")) {
            targetPart = inventory[selectedPartIndex].installed;
        }
        else if (partsToLoad.equals("constructed")) {
            targetPart = inventory[selectedPartIndex].constructed;
        }
        int downforcePoints = Math.round(improvementPoints * (float) (sliderValue / 100));
        int dragPoints = Math.round(improvementPoints * (float) (1 - (sliderValue / 100)));
        System.out.println("sliderValue [" + sliderValue + "]");
        System.out.println("downforcePoints [" + downforcePoints + "]");
        System.out.println("dragPoints [" + dragPoints + "]");
        targetPart.downforce += downforcePoints;
        targetPart.drag += dragPoints;
        targetPart.improvementSpent += improvementPoints;
        targetPart.average = aeroAPI.returnPartAverage(targetPart);
        facilities.resDev.setResearchPoints(facilities.resDev.getResearchPoints() - uiAPI.returnTextInt(txtRP));
        txtResearch.setText(Integer.toString(facilities.resDev.getResearchPoints()));
        playerTeam.setMoney(playerTeam.getMoney() - partCost);
        txtMoney.setText(Integer.toString(playerTeam.getMoney()));

        return targetPart;
    }

    private int calcImprovementCosts(int RP) {
        if (selectedPartIndex > -1) {
            aeroPartInventory.part targetPart = null;
            partRpCost = 0;
            partCost = 0;
            if (partsToLoad.equals("installed")) {
                targetPart = inventory[selectedPartIndex].installed;
            }
            else if (partsToLoad.equals("constructed")) {
                targetPart = inventory[selectedPartIndex].constructed;
            }
            float rpCost = (season.raceCount * targetPart.improvementSpent);
            float totalCost = 0;
            // taking season total prizepool to balance the cost out no matter the prize pool you select.
            int initialCost = (season.totalPrizePool * targetPart.improvementSpent);
            int improvementPoints = 0;
            
            if (targetPart.improvementSpent == 0) {
                rpCost = season.raceCount * 100;
                initialCost = season.totalPrizePool * 100;
            }

            int rpCostFinal = 0;
            int totalCostFinal = 0;
            for (int i = 1; RP > rpCost; i++) {
                rpCostFinal = Math.round(rpCost);
                totalCostFinal = Math.round(totalCost);
                float modifyer = i;
                improvementPoints = i - 1 - targetPart.improvementSpent;
                rpCost += rpCost * (modifyer / 1700);
                totalCost += initialCost * (modifyer / 80);
                if (i > targetPart.max_improvement) {
                    break;
                }
            }
            System.out.println("totalCost [" + totalCostFinal + "]");
            System.out.println("rpCost [" + rpCostFinal + "]");
            System.out.println(1 - (float) (targetPart.improvementSpent + improvementPoints) / (float) targetPart.max_improvement);
            lblCost.setText("€ " + uiAPI.setCurrencyFormat(totalCostFinal));
            txtRP.setText(Integer.toString(rpCostFinal + 1));
            if (!targetPart.improvement_hidden)
                lblSelectedPartImprovement.setText(Integer.toString(targetPart.max_improvement - targetPart.improvementSpent - improvementPoints) + " / " + Integer.toString(targetPart.max_improvement));
            prgPotentialProjected.setVisible(true);
            float ProjectedImprovementBar = 1 - (float) (targetPart.improvementSpent + improvementPoints) / (float) targetPart.max_improvement;
            prgPotentialProjected.setProgress(ProjectedImprovementBar);
            System.out.println("Rotation [" + improvementPoints + "]");

            if (totalCostFinal <= playerTeam.getMoney() && rpCostFinal <= facilities.resDev.getResearchPoints() && improvementPoints > 0) {
                btnInstallParts.setDisable(false);
                partRpCost = rpCostFinal;
                partCost = totalCostFinal;
            }

            return improvementPoints;
        } else {
            return 0;
        }
    }

    private void giveResDevExperience(String xpOption, int RP) {
        int experience = 0;
        switch (xpOption) {
            case "improvePart":
                experience = Math.round(RP / inventory.length);
                experience = Math.round((experience / 5) * season.getRaceCount());
                break;

            case "newPart":
                if (facilities.resDev.getLevel() + 1 < teamFacilities.resDevLevelExpValues(season).length) {
                    experience = (teamFacilities.resDevLevelExpValues(season)[facilities.resDev.getLevel() + 1] - teamFacilities.resDevLevelExpValues(season)[facilities.resDev.getLevel()]) / (inventory.length * 2);
                } else {
                    experience = (teamFacilities.resDevLevelExpValues(season)[facilities.resDev.getLevel() - 1] - teamFacilities.resDevLevelExpValues(season)[facilities.resDev.getLevel() - 2]) / (inventory.length * 2);
                }
                experience = Math.round((experience / 10) * season.getRaceCount());
                break;
        
            default:
                break;
        }
        facilities.resDev.experience += experience;
        if (facilities.resDev.getExperience() >= teamFacilities.resDevLevelExpValues(season)[facilities.resDev.getLevel() + 1]) {
            facilities.resDev.setLevel(facilities.resDev.getLevel() + 1);
        }
        float currentExp = (long) facilities.resDev.getExperience() - teamFacilities.resDevLevelExpValues(season)[facilities.resDev.getLevel()];
        float maxExp = (long) teamFacilities.resDevLevelExpValues(season)[facilities.resDev.getLevel() + 1] - teamFacilities.resDevLevelExpValues(season)[facilities.resDev.getLevel()];
        float progressBarValue = currentExp / maxExp;
        System.out.println("level [" + facilities.resDev.getLevel() + "]");
        System.out.println("experience [" + experience + "]");
        System.out.println("currentExp [" + currentExp + "]");
        System.out.println("maxExp [" + maxExp + "]");
        System.out.println("progressBarValue [" + progressBarValue + "]");
        prgRdExp.setProgress(progressBarValue);
        lblRdExpNmbr.setText(Integer.toString(facilities.resDev.getExperience()) + " / " + teamFacilities.resDevLevelExpValues(season)[teamFacilities.getResDevLevelFromExp(facilities.resDev.getExperience(), season) + 1]);
        lblRdLevel.setText("Level: " + Integer.toString(teamFacilities.getResDevLevelFromExp(facilities.resDev.getExperience(), season)));
    }

/* 
//////////// -------------------- \\\\\\\\\\\\
|||||||||||| DISPLAY LOADED PARTS ||||||||||||
\\\\\\\\\\\\ -------------------- ////////////
*/
    public static LinearGradient returnRarityColor(seasonSettings season, int partAverage) {
        Color[] rarityColors = {Color.WHITE, Color.LIME, Color.BLUE, Color.DARKORCHID, Color.ORANGE, Color.RED};
        Color returnedColor = rarityColors[0];
        float df_diff = season.getMaxDownforce() - season.getMinDownforce();
        float dr_diff = season.getMaxDrag() - season.getMinDrag();
        float averageDiff = (df_diff + dr_diff) / 2;
        float averageMin = (season.getMinDownforce() + season.getMinDrag()) / 2;
        float fPartAverage = partAverage - averageMin;
        float statPercentFromMax = fPartAverage / averageDiff;


        if (statPercentFromMax < 0.20) {
            returnedColor = rarityColors[0];
        } else if (statPercentFromMax <= 0.3 && statPercentFromMax > 0.2) {
            returnedColor = rarityColors[1];
        } else if (statPercentFromMax <= 0.5 && statPercentFromMax > 0.3) {
            returnedColor = rarityColors[2];
        } else if (statPercentFromMax <= 0.8 && statPercentFromMax > 0.5) {
            returnedColor = rarityColors[3];
        } else if (statPercentFromMax <= 0.99 && statPercentFromMax > 0.8) {
            returnedColor = rarityColors[4];
        } else {
            returnedColor = rarityColors[5];
        }
        Stop[] stops = new Stop[] {new Stop(0, returnedColor), new Stop(1, Color.WHITE)};
        LinearGradient lg = new LinearGradient(1, 1, 0.5, 1, true, CycleMethod.NO_CYCLE, stops); 
        return lg;
    }

    public void selectPartFromInventory() {
        if (uiAPI.returnTextInt(txtRP) == 0 ){
            prgPotentialProjected.setVisible(false);
            if (selectedPartIndex > -1) {
                // if (selectedPartIndex > -1 && previousPartIndex != selectedPartIndex) {
                slrDragDownforce.setVisible(true);
                panUpgradePartPane.setVisible(true);
                ancPartSlider.setVisible(true);
                if (partsToLoad.equals("installed")) {
                    btnInstallParts.setText("Improve part");
                    btnInstallParts.setDisable(true);
                    txtRP.setVisible(true);
                    lblCost.setVisible(true);
                    lblCostStatic.setVisible(true);
                    panNewPartPabe.setVisible(false);
                    int[] improvementRate = {inventory[selectedPartIndex].installed.improvementSpent, inventory[selectedPartIndex].installed.max_improvement};
                    loadPartStats(inventory[selectedPartIndex].installed, inventory[selectedPartIndex].part_name, inventory[selectedPartIndex].installed.stats_hidden, -1, improvementRate);
                }
                if (partsToLoad.equals("constructed") && !Objects.isNull(inventory[selectedPartIndex].constructed)) {
                    btnInstallParts.setText("Install part");
                    btnInstallParts.setDisable(false);
                    txtRP.setVisible(false);
                    lblCost.setVisible(false);
                    lblCostStatic.setVisible(false);
                    ancPartSlider.setVisible(false);
                    int[] improvementRate = {inventory[selectedPartIndex].constructed.improvementSpent, inventory[selectedPartIndex].constructed.max_improvement};
                    loadPartStats(inventory[selectedPartIndex].constructed, inventory[selectedPartIndex].part_name, inventory[selectedPartIndex].constructed.stats_hidden, -1, improvementRate);
                }
                if (uiAPI.returnTextInt(txtRP) > 0) {
                    calcImprovementCosts(0);
                } else {
                    lblCost.setText("€ 0");
                }
                previousPartIndex = selectedPartIndex;
            } else if ( selectedPartIndex == -1) {
                resetPartUI();
            }
        }
    }

    private void loadPartStats(aeroPartInventory.part part, String part_name, boolean projectedAverage, double progressBarValue, int[] improvementRate) {
        recSelectedPartRarity.setFill(returnRarityColor(season, part.average));
        lblSelectedPartName.setText(part_name);
        if (!projectedAverage) {
            lblSelectedPartDf.setText(Integer.toString(part.downforce));
            lblSelectedPartDr.setText(Integer.toString(part.drag));
            lblSelectedPartAverageScore.setText(Integer.toString(part.average));
        } else {
            lblSelectedPartDf.setText(part.projectedDownforce);
            lblSelectedPartDr.setText(part.projectedDrag);
            lblSelectedPartAverageScore.setText(part.projectedAverage);
        }

        if (!part.improvement_hidden) {
            lblSelectedPartImprovement.setText(Integer.toString(part.max_improvement - part.improvementSpent) + " / " + Integer.toString(part.max_improvement));
        } else {
            lblSelectedPartImprovement.setText("??? / ???");
        }
        
        if (progressBarValue == -1) {
            if (improvementRate[1] == 0 || improvementRate[0] >= improvementRate[1]) {
                prgPotential.setProgress(0d);
            } 
            else if (improvementRate[0] == 0) {
                prgPotential.setProgress(1d);
            } 
            else {
                prgPotential.setProgress(1 - ((double) improvementRate[0] / (double) improvementRate[1]));
            }
        } else {
            prgPotential.setProgress(progressBarValue);
        }
    }

    private void resetPartUI() {
        tempInv = null;
        tempPart = null;
        txtRP.setText("0");
        panUpgradePartPane.setVisible(false);
        panNewPartPabe.setVisible(false);
        ancPartSlider.setVisible(false);
        selectedPartIndex = -1;
        recSelectedPartRarity.setFill(returnRarityColor(season, (int) (season.getMinDownforce() + 1)));
        lblSelectedPartName.setText("no part selected");
        lblSelectedPartDf.setText("");
        lblSelectedPartDr.setText("");
        lblSelectedPartAverageScore.setText("");
        lblSelectedPartImprovement.setText("--- / ---");
        prgPotential.setProgress(0d);
    }

    private void saveFileData() {
        tempInv = null;
        tempPart = null;
        newCarBuilt = false;
        jsonWriter.saveTeam(playerTeam);
        jsonWriter.savePartsToJson(inventory, playerTeam);
        jsonWriter.saveFacilitiesToJson(facilities, playerTeam);
        fileWriter.writeNewAeroStats(playerTeam, inventory, season);
    }
}
