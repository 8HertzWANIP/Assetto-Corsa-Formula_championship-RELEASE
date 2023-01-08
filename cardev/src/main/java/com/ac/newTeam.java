package com.ac;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.*;

import com.ac.backend.jsonReader;
import com.ac.backend.jsonWriter;
import com.ac.backend.seasonSettings;
import com.ac.backend.teamSetup;
import com.ac.backend.carScaler;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

public class newTeam extends App implements Initializable {
    jsonReader jsonLoader = new jsonReader();
    seasonSettings season = jsonLoader.parseSeasonSettings();
    carScaler carScaler = new carScaler();
    String strParentDirectory;
    int teamIndex = 1;
    int teamCount = season.getTeamCount();
    jsonWriter writer = new jsonWriter();
    String pathToCarFolder = "";
    String setController = "AI-Balanced"; // Defaults the controller to AI-Balanced when none are selected.
    ArrayList<Integer> positions = new ArrayList<Integer>();
    ArrayList<String> teamList = new ArrayList<String>();
    private String[] controllers = {"Player Team", "AI-Balanced", "AI-Drag", "AI-Downforce"};
    int previousSeasonPosition = 0;

    @FXML
    private Button btnBrowse;
    
    @FXML
    private Label txtNewTeamLabel;

    @FXML
    private Button btnContinue;

    @FXML
    private Button btnReturn;
    
    @FXML
    private ColorPicker clrTeamColor;
    
    @FXML
    private ComboBox<Integer> cmbPrevPos;
    
    @FXML
    private ComboBox<String> cmbController;

    @FXML
    private Button btnReturnSeasonSetup;

    @FXML
    private Label lblTeamCounter;

    @FXML
    private TextField txtContentCars;

    @FXML
    private TextField txtCarFolder;

    @FXML
    private TextField txtTeamName;

    @FXML
    void btnBrowseClick(ActionEvent event) {
        JFileChooser f = new JFileChooser();

        f.setCurrentDirectory(new File(pathToCarFolder));
        if (pathToCarFolder != "") {
        }

        f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        f.showOpenDialog(null);

        System.out.println(f.getCurrentDirectory());
        System.out.println(f.getSelectedFile());
        txtContentCars.setText(f.getSelectedFile().getPath());
        pathToCarFolder = f.getSelectedFile().toString();
        System.out.println("carFolder: [" + pathToCarFolder + "]");

        if (pathToCarFolder.contains("content\\cars") || pathToCarFolder.contains("content/cars")) {
            txtCarFolder.setDisable(false);
        }
    }

    @FXML
    void btnContinueClick(ActionEvent event) throws IOException {
        if (cmbController.getValue() != "" && cmbPrevPos.getValue() > 0)
        {
            // Set team starting funds
            int raceRewardTotal = 0;
            int startingFunds = 0;
            switch (season.raceRewards) {
                // Race + Season rewards
                case 0:
                    // Convert season rewards to millions
                    raceRewardTotal = Math.round((season.getTotalPrizePool() * 1000000) / 2);
                    // Set team starting funds
                    startingFunds = Math.round((raceRewardTotal / 2) / season.getTeamCount());
                    if (!season.equalFunds) {
                        switch (previousSeasonPosition) {
                            case 1:
                                startingFunds += Math.round((raceRewardTotal / 2) * 0.19f);
                                break;
                            case 2:
                                startingFunds += Math.round((raceRewardTotal / 2) * 0.16f);
                                break;
                            case 3:
                                startingFunds += Math.round((raceRewardTotal / 2) * 0.13f);
                                break;
                            case 4:
                                startingFunds += Math.round((raceRewardTotal / 2) * 0.11f);
                                break;
                            case 5:
                                startingFunds += Math.round((raceRewardTotal / 2) * 0.10f);
                                break;
                            case 6:
                                startingFunds += Math.round((raceRewardTotal / 2) * 0.09f);
                                break;
                            case 7:
                                startingFunds += Math.round((raceRewardTotal / 2) * 0.07f);
                                break;
                            case 8:
                                startingFunds += Math.round((raceRewardTotal / 2) * 0.06f);
                                break;
                            case 9:
                                startingFunds += Math.round((raceRewardTotal / 2) * 0.05f);
                                break;
                            case 10:
                                startingFunds += Math.round((raceRewardTotal / 2) * 0.04f);
                                break;
                        
                            default:
                                break;
                        }
                    } else {
                        // Set team starting funds
                        startingFunds = Math.round(raceRewardTotal / season.getTeamCount());
                    }
                    break;

                // Race rewards only
                case 1:
                    // Convert season rewards to millions
                    raceRewardTotal = Math.round((season.getTotalPrizePool() * 1000000) / 2);
                    // Set team starting funds
                    startingFunds = Math.round((raceRewardTotal / 2) / season.getTeamCount());
                    break;

                // Season rewards only
                case 2:
                    // Convert season rewards to millions
                    raceRewardTotal = Math.round(season.getTotalPrizePool() * 1000000) / 2;
                    // Set team starting funds
                    startingFunds = Math.round(raceRewardTotal / season.getTeamCount());
                    if (!season.equalFunds) {
                        switch (previousSeasonPosition) {
                            case 1:
                                startingFunds += Math.round(raceRewardTotal * 0.19f);
                                break;
                            case 2:
                                startingFunds += Math.round(raceRewardTotal * 0.16f);
                                break;
                            case 3:
                                startingFunds += Math.round(raceRewardTotal * 0.13f);
                                break;
                            case 4:
                                startingFunds += Math.round(raceRewardTotal * 0.11f);
                                break;
                            case 5:
                                startingFunds += Math.round(raceRewardTotal * 0.10f);
                                break;
                            case 6:
                                startingFunds += Math.round(raceRewardTotal * 0.09f);
                                break;
                            case 7:
                                startingFunds += Math.round(raceRewardTotal * 0.07f);
                                break;
                            case 8:
                                startingFunds += Math.round(raceRewardTotal * 0.06f);
                                break;
                            case 9:
                                startingFunds += Math.round(raceRewardTotal * 0.05f);
                                break;
                            case 10:
                                startingFunds += Math.round(raceRewardTotal * 0.04f);
                                break;
                        
                            default:
                                break;
                        }
                    } else {
                        // Set team starting funds
                        startingFunds = Math.round(raceRewardTotal / season.getTeamCount());
                    }
                    break;
                default:
                    break;
            }

            // Set starting research level to 60 if equal, otherwise per previous season finising position.
            int startingResearchLevel = 0;
            if (season.equalDev) {
                startingResearchLevel = 60;
            } else {
                switch (previousSeasonPosition) {
                case 1:
                    startingResearchLevel = 80;
                    startingResearchLevel = ThreadLocalRandom.current().nextInt(startingResearchLevel - 10, startingResearchLevel + 10);
                    break;
                case 2:
                    startingResearchLevel = 80;
                    startingResearchLevel = ThreadLocalRandom.current().nextInt(startingResearchLevel - 10, startingResearchLevel + 10);
                    break;
                case 3:
                    startingResearchLevel = 70;
                    startingResearchLevel = ThreadLocalRandom.current().nextInt(startingResearchLevel - 10, startingResearchLevel + 10);
                    break;
                case 4:
                    startingResearchLevel = 60;
                    startingResearchLevel = ThreadLocalRandom.current().nextInt(startingResearchLevel - 10, startingResearchLevel + 10);
                    break;
                case 5:
                    startingResearchLevel = 60;
                    startingResearchLevel = ThreadLocalRandom.current().nextInt(startingResearchLevel - 10, startingResearchLevel + 10);
                    break;
                case 6:
                    startingResearchLevel = 50;
                    startingResearchLevel = ThreadLocalRandom.current().nextInt(startingResearchLevel - 10, startingResearchLevel + 10);
                    break;
                case 7:
                    startingResearchLevel = 50;
                    startingResearchLevel = ThreadLocalRandom.current().nextInt(startingResearchLevel - 10, startingResearchLevel + 10);
                    break;
                case 8:
                    startingResearchLevel = 40;
                    startingResearchLevel = ThreadLocalRandom.current().nextInt(startingResearchLevel - 10, startingResearchLevel + 10);
                    break;
                case 9:
                    startingResearchLevel = 35;
                    startingResearchLevel = ThreadLocalRandom.current().nextInt(startingResearchLevel - 10, startingResearchLevel + 10);
                    break;
                case 10:
                    startingResearchLevel = 35;
                    startingResearchLevel = ThreadLocalRandom.current().nextInt(startingResearchLevel - 10, startingResearchLevel + 10);
                    break;
                default:
                    startingResearchLevel = 35;
                    startingResearchLevel = ThreadLocalRandom.current().nextInt(startingResearchLevel - 10, startingResearchLevel + 10);
                    break;
                }
            }

            pathToCarFolder = txtContentCars.getText() + txtCarFolder.getText();
            pathToCarFolder.replace("/", "\\");
            if (!pathToCarFolder.contains("\\cars\\")) {
                pathToCarFolder = txtContentCars.getText() + "\\" + txtCarFolder.getText();
            }
            System.out.println("full Folder path: [" + pathToCarFolder + "]");
            if (cmbController.getValue() != null) {
                setController = cmbController.getValue();
            }

            // Setup team variables to save to Json.
            teamSetup team = new teamSetup(
                loadedProfile,
                setController,
                txtTeamName.getText(),
                clrTeamColor.getValue().toString(),
                pathToCarFolder,
                startingFunds,
                startingResearchLevel,
                0,
                cmbPrevPos.getValue()
                );
                
            teamList.add(team.getTeamName());
            writer.saveTeam(team);
            teamIndex++;

            // Load main season window when all teams have been put in.
            if (teamIndex > season.getTeamCount()) {
                carScaler.scaleCars(teamList);
                App.setRoot("improvePart");
            }
            else {
                // Reset input fields and increment team number.
                txtTeamName.setText("");
                txtCarFolder.setText("");
                lblTeamCounter.setText(teamIndex + "/" + season.getTeamCount());

                // Remove player team when selected
                if (cmbController.getValue() == "Player Team") {
                    String[] controllers = {"AI-Balanced", "AI-Drag", "AI-Downforce"};
                    ArrayList<String> controllersArrayList = new ArrayList<String>();
                    for (String controller : controllers) {
                        controllersArrayList.add(controller);
                    }
                    cmbController.setItems(FXCollections.observableArrayList(controllersArrayList));
                }
                // Remove the selected previous season finishing position.
                positions.remove(cmbPrevPos.getValue());
                ObservableList<Integer> positionList = FXCollections.observableArrayList(positions);
                cmbPrevPos.setItems(positionList);
            }
        }
    }
    
    @FXML
    void cmbControllerAction(ActionEvent event) {

    }
    
    @FXML
    void cmbSelectPos(ActionEvent event) {
        if (cmbPrevPos.getValue() != null) {
            previousSeasonPosition = cmbPrevPos.getValue();
        }
    }

    @FXML
    void btnReturnSeasonSetupClick(ActionEvent event) throws IOException {
        App.setRoot("newSeason");
    }

    @FXML
    void btnReturnToMainMenu(ActionEvent event) throws IOException {
        App.setRoot("landingpage");
    }
    
    @FXML
    void clrTeamColorAction(ActionEvent event) {
        System.out.println("ColorPicker");
        System.out.println(clrTeamColor.getValue());
    }
    
    @FXML
    void onKeyReleasedScene(KeyEvent event) {
        if (txtCarFolder.getText().equals("")
        || txtContentCars.getText().equals("")
        || txtNewTeamLabel.getText().equals("")
        || txtTeamName.getText().equals("")) {
            btnContinue.setDisable(true);
        } else {
            if (pathToCarFolder.contains("content\\cars") || pathToCarFolder.contains("content/cars")) {
                btnContinue.setDisable(false);
            }
        }

    }

    @FXML
    public void initialize(URL arg0, ResourceBundle arg1) {
        System.out.println("Setting teamCounter to: [" + teamIndex + "/" + jsonLoader.parseSeasonSettings().getTeamCount() + "]");
        lblTeamCounter.setText(teamIndex + "/" + jsonLoader.parseSeasonSettings().getTeamCount());
        ArrayList<String> controllersArrayList = new ArrayList<String>();

        File file = new File(System.getProperty("user.dir"));
        strParentDirectory = file.getParent() + "\\content\\cars";
        System.out.println("Parent directory is : " + strParentDirectory);
        System.out.println("Working Directory = " + System.getProperty("user.dir"));

        for (String controller : controllers) {
            controllersArrayList.add(controller);
        }

        // Fill positions array with # of teams in this season
        for (int i = 1; i <= season.teamCount; i++) {
            positions.add(i);
        }

        cmbController.setItems(FXCollections.observableArrayList(controllersArrayList));
        cmbPrevPos.setItems(FXCollections.observableArrayList(positions));
        cmbPrevPos.setValue(0);

        if (strParentDirectory.contains("assettocorsa")) {
            txtContentCars.setText(strParentDirectory);
            pathToCarFolder = strParentDirectory;
            System.out.println("carFolder: [" + pathToCarFolder + "]");
    
            if (pathToCarFolder.contains("content\\cars") || pathToCarFolder.contains("content/cars")) {
                txtCarFolder.setDisable(false);
            }
        }
    }
}
