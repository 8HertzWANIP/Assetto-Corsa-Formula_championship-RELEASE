package com.ac.menus;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import com.ac.App;
import com.ac.AI.aiController;
import com.ac.fileparsing.fileReader;
import com.ac.fileparsing.jsonReader;
import com.ac.fileparsing.jsonWriter;
import com.ac.lib.aeroPart;
import com.ac.lib.aeroPartInventory;
import com.ac.lib.uiAPI;
import com.ac.seasons.newSeason.seasonSettings;
import com.ac.seasons.newSeason.teamSetup;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.effect.ImageInput;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

public class startRace extends App implements Initializable {
    ArrayList<Double> teamRatings = new ArrayList<Double>();
    ArrayList<Pane> uiCarTypes;
    aiController ai_controller_1 = new aiController();
    static ArrayList<aeroPartInventory[]> loadedInventories;
    public static ArrayList<teamSetup> loadedTeams;
    public static int carAngleSetupIndex = 0;
    ArrayList<aeroPart> aeroParts = new ArrayList<aeroPart>();
    seasonSettings season = new seasonSettings("", 0, 0, 0, 0, 0, 0f, 0f, 0f, 0f, 1000, 0, 0, 0, 0, false, false, false, false, null, null);
    @FXML
    private Button btnCancelRace;

    @FXML
    private Button btnFinishRace;

    @FXML
    private Button btnReturn;

    @FXML
    private Button btnStartRace;

    @FXML
    private CheckBox chkPreSeasonTesting;

    @FXML
    private Button btnDownforceInc;

    @FXML
    private Button btnDownforceMax;

    @FXML
    private Button btnDownforceMin;

    @FXML
    private Button btnDownforceRed;

    @FXML
    private Button btnDownforceAvg;

    @FXML
    private Label lblRaceCount;

    @FXML
    private Label lblPreseasonTesting;

    @FXML
    private Label lblDownforceAvg;

    @FXML
    private Label lblDownforceInc;

    @FXML
    private Label lblDownforceMax;

    @FXML
    private Label lblDownforceMin;

    @FXML
    private Label lblDownforceRed;

    @FXML
    private Label lblDragAvg;

    @FXML
    private Label lblDragInc;

    @FXML
    private Label lblDragMax;

    @FXML
    private Label lblDragMin;

    @FXML
    private Label lblDragRed;
    
    @FXML
    private Label lblLoadingTeam;

    @FXML
    private ProgressBar prgLoadingBar;

    @FXML
    private AnchorPane paneRace;

    @FXML
    private Slider sldDownforceLvl;

    @FXML
    void sldDownforceLvlDrop(MouseEvent event) {
        setTrackDownforceLevel((int) Math.round(sldDownforceLvl.getValue()));
        setDfUi((int) Math.round(sldDownforceLvl.getValue()));
    }

    @FXML
    void btnCancelRaceClick(ActionEvent event) throws IOException {
        season.setRaceCanceled(true);
            App.setRoot(new FXMLLoader(getClass().getResource("/teamMenu.fxml")));

    }

    @FXML
    void btnFinishRaceClick(ActionEvent event) throws IOException {
        if (season.getCurrentRace() == 0 && !season.isPreseasonTestingCompleted()) {
            season.setCurrentRace(season.getCurrentRace() + 1);
            System.out.println("saved race Count: [" + season.getCurrentRace() + "]");
            jsonWriter.saveSeasonSettings(season);
            App.setRoot(new FXMLLoader(getClass().getResource("/incomeWindow.fxml")));

        } else {
            App.setRoot(new FXMLLoader(getClass().getResource("/champPoints.fxml")));

        }
    }

    @FXML
    void btnReturnClick(ActionEvent event) throws IOException {
            App.setRoot(new FXMLLoader(getClass().getResource("/teamMenu.fxml")));

    }

    @FXML
    void btnStartRaceClick(ActionEvent event) throws InterruptedException, IOException {
        sldDownforceLvl.setVisible(false);
        if (!season.raceCanceled) {
            ai_controller_1.setLblLoadingTeam(lblLoadingTeam);
            ai_controller_1.setLoadedTeams(loadedTeams);
            ai_controller_1.setLoadedInventories(loadedInventories);
            ai_controller_1.init();
        } else {
            lblLoadingTeam.setText("AI Teams setup. You can start the session in Assetto Corsa");
        }
        // Immediatly run AI and skip cancel/finished button clicking to continue to start from the first race and skip pre-season testing.
        if (chkPreSeasonTesting.isSelected()) {
            season.setPreseasonTestingCompleted(true);
            season.setCurrentRace(season.getCurrentRace() + 1);
            season.setRaceCanceled(false);
            lblRaceCount.setText(season.getCurrentRace() + " / " + season.getRaceCount());
            chkPreSeasonTesting.setSelected(false);
            lblPreseasonTesting.setVisible(false);
            chkPreSeasonTesting.setVisible(false);
            jsonWriter.saveSeasonSettings(season);
            App.setRoot(new FXMLLoader(getClass().getResource("/incomeWindow.fxml")));

        } else {
            season.setTrackDownforceInt((int) Math.round(sldDownforceLvl.getValue()));
            season.setRaceCanceled(true);
            btnFinishRace.setDisable(false);
            btnCancelRace.setDisable(false);
            btnStartRace.setDisable(true);
            btnReturn.setDisable(true);
        }
        jsonWriter.saveSeasonSettings(season);
    }

    private void setTrackDownforceLevel(int downforceSetting) {
        int fwTotalSteps;
        int rwTotalSteps;
        int fwSetupAngle;
        int rwSetupAngle;
        int fwAngle;
        int rwAngle;
        for (int i = 0; i < loadedTeams.size(); i++) {
            teamSetup.carInfo car = loadedTeams.get(i).carInfo;
    
            switch (downforceSetting) {
                case 0:
                    loadedInventories.set(i, setInventoryWingAngles(true, car.FwMinAngle, loadedInventories.get(i)));
                    loadedInventories.set(i, setInventoryWingAngles(false, car.RwMinAngle, loadedInventories.get(i)));
                    break;
                    
                case 1:
                    fwTotalSteps = (car.FwMaxAngle - car.FwMinAngle) / car.FwStep;
                    rwTotalSteps = (car.RwMaxAngle - car.RwMinAngle) / car.RwStep;
                    fwSetupAngle = Math.round(fwTotalSteps / 2) - Math.round(fwTotalSteps / 4);
                    rwSetupAngle = Math.round(rwTotalSteps / 2) - Math.round(rwTotalSteps / 4) + 1;
                    fwAngle = fwSetupAngle * car.FwStep;
                    rwAngle = rwSetupAngle * car.RwStep;
                    loadedInventories.set(i, setInventoryWingAngles(true, fwAngle, loadedInventories.get(i)));
                    loadedInventories.set(i, setInventoryWingAngles(false, rwAngle, loadedInventories.get(i)));
                    break;
                    
                case 2:
                    fwTotalSteps = (car.FwMaxAngle - car.FwMinAngle) / car.FwStep;
                    rwTotalSteps = (car.RwMaxAngle - car.RwMinAngle) / car.RwStep;
                    fwSetupAngle = Math.round(fwTotalSteps / 2);
                    rwSetupAngle = Math.round(rwTotalSteps / 2) + 1;
                    fwAngle = fwSetupAngle * car.FwStep;
                    rwAngle = rwSetupAngle * car.RwStep;
                    loadedInventories.set(i, setInventoryWingAngles(true, fwAngle, loadedInventories.get(i)));
                    loadedInventories.set(i, setInventoryWingAngles(false, rwAngle, loadedInventories.get(i)));
                    break;
                    
                case 3:
                    fwTotalSteps = (car.FwMaxAngle - car.FwMinAngle) / car.FwStep;
                    rwTotalSteps = (car.RwMaxAngle - car.RwMinAngle) / car.RwStep;
                    fwSetupAngle = Math.round(fwTotalSteps / 2) + Math.round(fwTotalSteps / 4);
                    rwSetupAngle = Math.round(rwTotalSteps / 2) + Math.round(rwTotalSteps / 4) + 1;
                    fwAngle = fwSetupAngle * car.FwStep;
                    rwAngle = rwSetupAngle * car.RwStep;
                    loadedInventories.set(i, setInventoryWingAngles(true, fwAngle, loadedInventories.get(i)));
                    loadedInventories.set(i, setInventoryWingAngles(false, rwAngle, loadedInventories.get(i)));
                    break;
                    
                case 4:
                    loadedInventories.set(i, setInventoryWingAngles(true, car.FwMaxAngle, loadedInventories.get(i)));
                    loadedInventories.set(i, setInventoryWingAngles(false, car.RwMaxAngle, loadedInventories.get(i)));
                    break;
                    
                default:
                    break;
            }
        }
    }

    private static int getInventoryWingAngles(boolean frontWing, aeroPartInventory[] inventory) {
        String[] fwStrings = {"fw", "front wing"};
        String[] rwStrings = {"rw", "rear wing"};

        for (int i = 0; i < inventory.length; i++) {
            if (frontWing) {
                if (inventory[i].part_name.toLowerCase().contains(fwStrings[0]) || inventory[i].part_name.toLowerCase().contains(fwStrings[1])) {
                    System.out.println("Angle front wing is: [" + inventory[i].angle + "]");
                    return inventory[i].angle;
                }
            } else {
                if (inventory[i].part_name.toLowerCase().contains(rwStrings[0]) || inventory[i].part_name.toLowerCase().contains(rwStrings[1])) {
                    System.out.println("Angle rear wing is: [" + inventory[i].angle + "]");
                    return inventory[i].angle;
                }
            }
        }
        return -1;
    }

    private static aeroPartInventory[] setInventoryWingAngles(boolean frontWing, int value, aeroPartInventory[] inventory) {
        System.out.println("Setting inventory angles");
        System.out.println("value: " + value);
        String[] fwStrings = {"fw", "front wing"};
        String[] rwStrings = {"rw", "rear wing"};

        for (int i = 0; i < inventory.length; i++) {
            if (frontWing) {
                if (inventory[i].part_name.toLowerCase().contains(fwStrings[0]) || inventory[i].part_name.toLowerCase().contains(fwStrings[1])) {
                    inventory[i].angle = value;
                    System.out.println("Angle front wing is: [" + inventory[i].angle + "]");
                }
            } else {
                if (inventory[i].part_name.toLowerCase().contains(rwStrings[0]) || inventory[i].part_name.toLowerCase().contains(rwStrings[1])) {
                    inventory[i].angle = value;
                    System.out.println("Angle rear wing is: [" + inventory[i].angle + "]");
                }
            }
        }
        return inventory;
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        season = jsonReader.parseSeasonSettings();
        int downforceLevel = 2;
        if (season.raceCanceled)
            downforceLevel = season.trackDownforceInt;

        lblLoadingTeam.setText("Press 'Start Session' before starting the session in Assetto Corsa");
        chkPreSeasonTesting.setSelected(false);

        if (season.getCurrentRace() == 0 && !season.isPreseasonTestingCompleted()) {
            lblPreseasonTesting.setVisible(true);
            chkPreSeasonTesting.setVisible(true);
        } else {
            lblPreseasonTesting.setVisible(false);
            chkPreSeasonTesting.setVisible(false);
        }
        
        if (Objects.isNull(loadedTeams)) {
            loadedTeams = App.seasonData.loadedTeams;
            loadedInventories = App.seasonData.loadedInventories;
            validateTeamSetupInfo();
        }
        sldDownforceLvl.setValue(downforceLevel);
        setTrackDownforceLevel(downforceLevel);
        setDfUi(downforceLevel);
        System.out.print("Current race: [" + season.getCurrentRace() + "]");
        lblRaceCount.setText(season.getCurrentRace() + " / " + season.getRaceCount());
        URL lowestUrl = getClass().getResource("/images/lowest-df.png");
        URL lowUrl = getClass().getResource("/images/low-df.png");
        URL midUrl = getClass().getResource("/images/mid-df.png");
        URL highUrl = getClass().getResource("/images/high-df.png");
        URL highestUrl = getClass().getResource("/images/highest-df.png");
        btnDownforceInc.setEffect(uiAPI.setDfImages(lowestUrl));
        btnDownforceMax.setEffect(uiAPI.setDfImages(lowUrl));
        btnDownforceMin.setEffect(uiAPI.setDfImages(midUrl));
        btnDownforceRed.setEffect(uiAPI.setDfImages(highUrl));
        btnDownforceAvg.setEffect(uiAPI.setDfImages(highestUrl));
    }

    private void validateTeamSetupInfo() {
        for (int i = 0; i < season.teamCount; i++) {
            teamSetup targetTeam = loadedTeams.get(i);
            aeroPartInventory[] targetInventory = loadedInventories.get(i);
            if (targetTeam != null) {
                if ((targetTeam.carInfo.FwStep == 0 && targetTeam.carInfo.RwStep == 0) || Objects.isNull(targetTeam.carInfo.FwStep)) {
                    targetTeam = fileReader.getSetupAngles(targetTeam);
                    targetTeam.carInfo.setFwAngle(getInventoryWingAngles(true, targetInventory));
                    targetTeam.carInfo.setRwAngle(getInventoryWingAngles(false, targetInventory));
                    jsonWriter.saveTeam(targetTeam);
                }
            } else {
                System.out.println("Target team is null");
            }
        }
    }

    private void setDfUi(int value) {
        if (season.raceCanceled) {
            sldDownforceLvl.setVisible(false);
        } else {
            sldDownforceLvl.setVisible(true);
        }
        btnDownforceInc.setDisable(true);
        btnDownforceMax.setDisable(true);
        btnDownforceMin.setDisable(true);
        btnDownforceRed.setDisable(true);
        btnDownforceAvg.setDisable(true);
        lblDownforceAvg.setVisible(false);
        lblDownforceInc.setVisible(false);
        lblDownforceMax.setVisible(false);
        lblDownforceMin.setVisible(false);
        lblDownforceRed.setVisible(false);
        lblDragAvg.setVisible(false);
        lblDragInc.setVisible(false);
        lblDragMax.setVisible(false);
        lblDragMin.setVisible(false);
        lblDragRed.setVisible(false);
        switch (value) {
            case 0:
                btnDownforceMin.setDisable(false);
                lblDownforceMin.setVisible(true);
                lblDragMin.setVisible(true);
            break;
                
            case 1:
                btnDownforceRed.setDisable(false);
                lblDownforceRed.setVisible(true);
                lblDragRed.setVisible(true);
            break;
                
            case 2:
                btnDownforceAvg.setDisable(false);
                lblDownforceAvg.setVisible(true);
                lblDragAvg.setVisible(true);
                break;
                
            case 3:
                btnDownforceInc.setDisable(false);
                lblDownforceInc.setVisible(true);
                lblDragInc.setVisible(true);
            break;
                
            case 4:
                btnDownforceMax.setDisable(false);
                lblDownforceMax.setVisible(true);
                lblDragMax.setVisible(true);
            break;
                
            default:
                break;
        }
        
    }
}
