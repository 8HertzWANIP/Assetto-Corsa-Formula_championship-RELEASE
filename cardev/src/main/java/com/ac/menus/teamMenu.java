package com.ac.menus;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;

import com.ac.App;
import com.ac.fileparsing.fileReader;
import com.ac.fileparsing.jsonReader;
import com.ac.fileparsing.jsonUpdater;
import com.ac.fileparsing.jsonWriter;
import com.ac.lib.aeroPartInventory;
import com.ac.lib.carScaler;
import com.ac.lib.uiAPI;
import com.ac.menus.facilities.teamFacilities;
import com.ac.seasons.newSeason.seasonSettings;
import com.ac.seasons.newSeason.teamSetup;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.effect.ImageInput;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class teamMenu extends App implements Initializable {

    boolean resetCarsButton = true;
    private carScaler carScaler = new carScaler();
    private boolean sandboxMode = false;
    private seasonSettings season = new seasonSettings("", 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, false, false, false, false, null, null);
    public static teamFacilities facilities = null;
    public static aeroPartInventory[] inventory = null;
    private teamSetup playerTeam;

    @FXML
    private Button btnChampStandings;

    @FXML
    private Button btnPerformanceChart;

    @FXML
    private Button btnMainMenu;

    @FXML
    private Button btnResetCars;

    @FXML
    private Button btnStartRace;

    @FXML
    private Button btnfacilities;

    @FXML
    private ComboBox<String> cmbSelectedTeam;

    @FXML
    private SplitPane mainOptionsWindow;

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
    void btnChampStandingsClick(ActionEvent event) throws IOException {
        App.setRoot(new FXMLLoader(getClass().getResource("/champStandings.fxml")));

    }

    @FXML
    void btnPerformanceChartClick(ActionEvent event) throws IOException {
        App.setRoot(new FXMLLoader(getClass().getResource("/carPerformanceChart.fxml")));

    }

    /**
     * Resets all cars in the current season by scaling them to their default size.
     * @param event The action event that triggered this method.
     */
    @FXML
    void btnResetCars(ActionEvent event) {
        // Create a new ArrayList of Strings to hold team names
        ArrayList<String> teamList = new ArrayList<String>();
        
        // Loop through each team in the current season and add its name to the list
        for (int i = 0; i < season.getTeamCount(); i++) {
            teamList.add(season.loadTeamlist().get(i));
        }
        
        // Scale all cars belonging to teams in the list back to their default size
        carScaler.scaleCars(teamList);
    }

    /**
     * Redirects user to main menu page when clicked.
     * @param event The action event that triggered this method.
     * @throws IOException If an I/O error occurs while loading the FXML file for the main menu page.
     */
    @FXML
    void btnMainMenuClick(ActionEvent event) throws IOException {
        // Load and display main menu page using FXMLLoader 
        App.setRoot(new FXMLLoader(getClass().getResource("/landingPage.fxml")));
    }

    /**
     * Redirects user to start race page if there are still races left in the current season,
     * otherwise redirects user to end of season page. 
     *
     *@param	event	The action event that triggered this method.	
    *@throws	IOException	If an I/O error occurs while loading either FXML file for start race or end of season pages. 
    */
    @FXML
    void btnStartRaceClick(ActionEvent event) throws IOException {
        if (season.getCurrentRace() > season.getRaceCount()) {   // Check if there are any more races left in current racing session
            
            // If no more races left, load and display end-of-season screen using FXMLLoader  
            try {
                App.setRoot(new FXMLLoader(getClass().getResource("/endOfSeason.fxml")));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {   // If there are still races left in current racing session
            
            // Load and display start race screen using FXMLLoader 
            try {   
                App.setRoot(new FXMLLoader(getClass().getResource("/startRace.fxml")));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @FXML
    void btnStartRaceExit(MouseEvent event) throws IOException {
        ImageInput imgInputRace = new ImageInput();
        URL url = getClass().getResource("/images/indexgrey(e).png");
        BufferedImage imgRace = ImageIO.read(url);
        Image image = SwingFXUtils.toFXImage(imgRace, null);
        imgInputRace.setSource(image);
        btnStartRace.setEffect(imgInputRace);

    }

    @FXML
    void btnStartRaceHover(MouseEvent event) throws IOException {
        ImageInput imgInputRace = new ImageInput();
        URL url = getClass().getResource("/images/index2(e).png");
        BufferedImage imgRace = ImageIO.read(url);
        Image image = SwingFXUtils.toFXImage(imgRace, null);
        imgInputRace.setSource(image);
        btnStartRace.setEffect(imgInputRace);

    }

    @FXML
    void btnfacilitiesClick(ActionEvent event) throws IOException {
        if (season.getCurrentRace() > season.getRaceCount()) {
            try {
                App.setRoot(new FXMLLoader(getClass().getResource("/endOfSeason.fxml")));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                App.setRoot(new FXMLLoader(getClass().getResource("/Facilities.fxml")));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    
    @FXML
    void btnfacilitiesExit(MouseEvent event) throws IOException {
        ImageInput imgInput = new ImageInput();
        URL url = getClass().getResource("/images/campusgrey(e).png");
        BufferedImage imgRace = ImageIO.read(url);
        Image image = SwingFXUtils.toFXImage(imgRace, null);
        imgInput.setSource(image);
        btnfacilities.setEffect(imgInput);
    }

    @FXML
    void btnfacilitiesHover(MouseEvent event) throws IOException {
        ImageInput imgInput = new ImageInput();
        URL url = getClass().getResource("/images/campus(e).png");
        BufferedImage imgRace = ImageIO.read(url);
        Image image = SwingFXUtils.toFXImage(imgRace, null);
        imgInput.setSource(image);
        btnfacilities.setEffect(imgInput);
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
        jsonUpdater.updateSeasonSettingsNew(season);
        if (sandboxMode) {
            cmbSelectedTeam.setVisible(true);
            loadTeamlistCombobox();
        } else {
            cmbSelectedTeam.setVisible(false);
            playerTeam = uiAPI.loadPlayerTeam(season);

            //ALPHA-V0.2.1 FIX
            if (playerTeam.getController().equals("AI")) {
                playerTeam.setController("Player Team");
                playerTeam.ai = null;
                jsonWriter.saveTeam(playerTeam);
            }

            File facilitiesJson = new File(new JFileChooser().getFileSystemView().getDefaultDirectory().toString() + "\\Ac Cardev save data\\" + loadedProfile + "\\" + playerTeam.teamName + "\\facilities.json");
            if (!facilitiesJson.exists()){
                jsonWriter.createFacilityJson(playerTeam, season);
            }
            facilities = jsonReader.parseFacilities(playerTeam);
            txtResearch.setText(Integer.toString(facilities.resDev.getResearchPoints()));

            File inventoryJson = new File(new JFileChooser().getFileSystemView().getDefaultDirectory().toString() + "\\Ac Cardev save data\\" + loadedProfile + "\\" + playerTeam.teamName + "\\part Inventory\\aero parts.json");
            if (!inventoryJson.exists()){
                fileReader.loadAeroIniParts(playerTeam, season);
            }
            inventory =  jsonReader.parseAeroParts(playerTeam);
        }
        // Set player team UI elements
        btnResetCars.setVisible(false);
        txtChampPos.setText(Integer.toString(playerTeam.getChampPos()));
        txtChhampPoints.setText(Integer.toString(playerTeam.getPoints()));
        txtMoney.setText(uiAPI.setCurrencyFormat(playerTeam.getMoney()));
        txtResearch.setText(Integer.toString(facilities.resDev.getResearchPoints()));
        recTeamColor.setFill(Color.valueOf(playerTeam.getTeamColor()));
        txtTeamName.setText(playerTeam.getTeamName());

        URL facilitiesImageUrl = getClass().getResource("/images/campusgrey(e).png");
        URL startRaceImageUrl = getClass().getResource("/images/indexgrey(e).png");
        btnStartRace.setEffect(uiAPI.setDfImages(startRaceImageUrl));
        btnfacilities.setEffect(uiAPI.setDfImages(facilitiesImageUrl));
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

}
