package com.ac.seasons.newSeason;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadLocalRandom;


import com.ac.App;
import com.ac.fileparsing.jsonWriter;
import com.ac.lib.carScaler;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

public class newTeam extends App implements Initializable {
    carScaler carScaler = new carScaler();
    String strParentDirectory;
    int teamIndex = 1;
    int teamCount = newSeason.season.getTeamCount();
    jsonWriter writer = new jsonWriter();
    ArrayList<Integer> positions = new ArrayList<Integer>();
    ArrayList<String> teamList = new ArrayList<String>();
    teamSetup targetTeam;
    private String[] philosiphies = {"Balanced", "Downforce", "Drag", "Random"};
    private String[] personalities = {"Wide", "Focussed", "Random"};
    private static int previousSeasonPosition = 0;

    @FXML
    private Button btnContinue;

    @FXML
    private Button btnReturn;

    @FXML
    private Button btnReturnSeasonSetup;

    @FXML
    private ColorPicker clrTeamColor;

    @FXML
    private ComboBox<String> cmbPers;

    @FXML
    private ComboBox<String> cmbPhil;

    @FXML
    private ComboBox<Integer> cmbPrevPos;

    @FXML
    private Label lblFullPath;

    @FXML
    private Label lblTeamCounter;

    @FXML
    private RadioButton radAi;

    @FXML
    private RadioButton radPlayer;

    @FXML
    private TextField txtCarFolder;

    @FXML
    private Label txtNewTeamLabel;

    @FXML
    private TextField txtTeamName;

    @FXML
    void btnContinueClick(ActionEvent event) throws IOException {
        setNameAndCarFolder();

        verifyTeamInfo();
        if (!btnContinue.isDisable()) {
            if (radPlayer.isSelected()) {
                targetTeam.setController("Player Team");
                radPlayer.setSelected(false);
                radPlayer.setDisable(true);
                newSeason.season.setPlayerTeam(targetTeam.getTeamName());
            } else if (radAi.isSelected()
            && Objects.nonNull(cmbPhil.getValue())
            && Objects.nonNull(cmbPers.getValue())
            && !cmbPhil.getValue().equals("")
            && !cmbPers.getValue().equals("")) {
                targetTeam.addAI(cmbPers.getValue(), cmbPhil.getValue());
            }
            teamList.add(targetTeam.getTeamName());
            jsonWriter.saveTeam(targetTeam);
            teamIndex++;

            // Load main season window when all teams have been put in.
            if (teamIndex > newSeason.season.getTeamCount()) {
                jsonWriter.saveSeasonSettings(newSeason.season);
                carScaler.scaleCars(teamList);
                App.setRoot("teamMenu");
            }
            else {
                // Increment team number.
                lblTeamCounter.setText(teamIndex + "/" + newSeason.season.getTeamCount());

                // Remove the selected previous season finishing position.
                positions.remove(cmbPrevPos.getValue());
                ObservableList<Integer> positionList = FXCollections.observableArrayList(positions);
                cmbPrevPos.setItems(positionList);
                cmbPrevPos.setValue(0);
                setupNewTeam();
            }
        }
    }

    @FXML
    void btnReturnSeasonSetupClick(ActionEvent event) throws IOException {
        App.setRoot("casFolder");
    }

    @FXML
    void btnReturnToMainMenu(ActionEvent event) throws IOException {
        App.setRoot("landingPage");
    }

    @FXML
    void clrTeamColorAction(ActionEvent event) {
        targetTeam.setTeamColor(clrTeamColor.getValue().toString());
        System.out.println("Team color set: " + clrTeamColor.getValue().toString());
        verifyTeamInfo();
    }

    @FXML
    void cmbPersAction(ActionEvent event) {
        setTeamAi();
        verifyTeamInfo();

    }

    @FXML
    void cmbPhilAction(ActionEvent event) {
        setTeamAi();
        targetTeam.ai.setPhilosophy(cmbPhil.getValue());
        System.out.println("Team philosophy set: " + cmbPhil.getValue());
        verifyTeamInfo();
    }

    @FXML
    void cmbSelectPos(ActionEvent event) {
        System.out.println(cmbPrevPos.getValue());
        if (Objects.nonNull(cmbPrevPos.getValue())) {
            previousSeasonPosition = cmbPrevPos.getValue();
            setStartingFunds();
            setResearch();
            verifyTeamInfo();
            targetTeam.champPos = previousSeasonPosition;
        }
    }

    @FXML
    void onKeyReleasedScene(KeyEvent event) {
        verifyTeamInfo();
    }

    @FXML
    void radAiToggle(ActionEvent event) {
        radPlayer.setSelected(false);
        cmbPers.setDisable(false);
        cmbPhil.setDisable(false);
        verifyTeamInfo();
    }

    @FXML
    void radPlayerToggle(ActionEvent event) {
        radAi.setSelected(false);
        cmbPers.setDisable(true);
        cmbPhil.setDisable(true);
        verifyTeamInfo();
    }

    @FXML
    public void initialize(URL arg0, ResourceBundle arg1) {
        System.out.println("Setting teamCounter to: [" + teamIndex + "/" + newSeason.season.getTeamCount() + "]");
        lblTeamCounter.setText(teamIndex + "/" + newSeason.season.getTeamCount());
        ArrayList<String> philosophyArrayList = new ArrayList<String>();

        for (String philosophy : philosiphies) {
            philosophyArrayList.add(philosophy);
        }
        ArrayList<String> personalitieArrayList = new ArrayList<String>();

        for (String personality : personalities) {
            personalitieArrayList.add(personality);
        }

        // Fill positions array with # of teams in this season
        for (int i = 1; i <= newSeason.season.getTeamCount(); i++) {
            positions.add(i);
        }

        cmbPhil.setItems(FXCollections.observableArrayList(philosophyArrayList));
        cmbPers.setItems(FXCollections.observableArrayList(personalitieArrayList));
        cmbPrevPos.setItems(FXCollections.observableArrayList(positions));
        setupNewTeam();
        verifyTeamInfo();
    }

    private void setStartingFunds() {// Set team starting funds
        int raceRewardTotal = 0;
        int startingFunds = 0;
        if (newSeason.season.equalFunds) {
            // Convert season rewards to millions
            raceRewardTotal = Math.round((newSeason.season.getTotalPrizePool() * 1000000) / 2);
            // Set team starting funds
            startingFunds = Math.round((raceRewardTotal / 2) / newSeason.season.getTeamCount());
        } else {
            switch (newSeason.season.getRaceRewards()) {
                // Race + Season rewards
                case 0:
                    // Convert season rewards to millions
                    raceRewardTotal = Math.round((newSeason.season.getTotalPrizePool() * 1000000) / 2);
                    // Set team starting funds
                    startingFunds = Math.round((raceRewardTotal / 2) / newSeason.season.getTeamCount());
                    if (!newSeason.season.equalFunds) {
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
                        startingFunds = Math.round(raceRewardTotal / newSeason.season.getTeamCount());
                    }
                    break;

                // Race rewards only
                case 1:
                    // Convert season rewards to millions
                    raceRewardTotal = Math.round((newSeason.season.getTotalPrizePool() * 1000000) / 2);
                    // Set team starting funds
                    startingFunds = Math.round((raceRewardTotal / 2) / newSeason.season.getTeamCount());
                    break;

                // Season rewards only
                case 2:
                    // Convert season rewards to millions
                    raceRewardTotal = Math.round(newSeason.season.getTotalPrizePool() * 1000000) / 2;
                    // Set team starting funds
                    startingFunds = Math.round(raceRewardTotal / newSeason.season.getTeamCount());
                    if (!newSeason.season.equalFunds) {
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
                        startingFunds = Math.round(raceRewardTotal / newSeason.season.getTeamCount());
                    }
                    break;
                default:
                    break;
            }
        }
        targetTeam.setMoney(startingFunds);
    }

    public void setResearch() {
        // Set starting research level to 60 if equal, otherwise per previous season finising position.
        int startingResearchLevel = 0;
        int rngValue = newSeason.resDevRng;
        if (newSeason.season.equalDev) {
            startingResearchLevel = 55;
        } else {
            switch (previousSeasonPosition) {
            case 1:
                startingResearchLevel = 75;
                startingResearchLevel = ThreadLocalRandom.current().nextInt(startingResearchLevel - rngValue, startingResearchLevel + rngValue);
                break;
            case 2:
                startingResearchLevel = 75;
                startingResearchLevel = ThreadLocalRandom.current().nextInt(startingResearchLevel - rngValue, startingResearchLevel + rngValue);
                break;
            case 3:
                startingResearchLevel = 70;
                startingResearchLevel = ThreadLocalRandom.current().nextInt(startingResearchLevel - rngValue, startingResearchLevel + rngValue);
                break;
            case 4:
                startingResearchLevel = 65;
                startingResearchLevel = ThreadLocalRandom.current().nextInt(startingResearchLevel - rngValue, startingResearchLevel + rngValue);
                break;
            case 5:
                startingResearchLevel = 60;
                startingResearchLevel = ThreadLocalRandom.current().nextInt(startingResearchLevel - rngValue, startingResearchLevel + rngValue);
                break;
            case 6:
                startingResearchLevel = 55;
                startingResearchLevel = ThreadLocalRandom.current().nextInt(startingResearchLevel - rngValue, startingResearchLevel + rngValue);
                break;
            case 7:
                startingResearchLevel = 50;
                startingResearchLevel = ThreadLocalRandom.current().nextInt(startingResearchLevel - rngValue, startingResearchLevel + rngValue);
                break;
            case 8:
                startingResearchLevel = 45;
                startingResearchLevel = ThreadLocalRandom.current().nextInt(startingResearchLevel - rngValue, startingResearchLevel + rngValue);
                break;
            case 9:
                startingResearchLevel = 40;
                if (rngValue > 5)
                    rngValue = 5;
                startingResearchLevel = ThreadLocalRandom.current().nextInt(startingResearchLevel - rngValue, startingResearchLevel + rngValue);
                break;
            case 10:
                startingResearchLevel = 35;
                if (rngValue > 5)
                    rngValue = 5;
                startingResearchLevel = ThreadLocalRandom.current().nextInt(startingResearchLevel - rngValue, startingResearchLevel + rngValue);
                break;
            default:
                startingResearchLevel = 30;
                if (rngValue > 5)
                    rngValue = 5;
                startingResearchLevel = ThreadLocalRandom.current().nextInt(startingResearchLevel - rngValue, startingResearchLevel + rngValue);
                break;
            }
        }
        targetTeam.setResearchLevel(startingResearchLevel);
    }

    private void setNameAndCarFolder() {
        targetTeam.setTeamName(txtTeamName.getText().trim());
        targetTeam.setCarFolder(carsFolder.pathToCarFolder + "\\" + txtCarFolder.getText().trim());
    }


    private void setupNewTeam() {
        targetTeam = new teamSetup(App.loadedProfile, "AI", null, null, null, 0, 0, 0, 0);
        lblFullPath.setText("Full Path: ");
        txtTeamName.setText("");
        txtCarFolder.setText("");
        clrTeamColor.setValue(null);
        cmbPrevPos.setValue(positions.get(0));
        radAi.setSelected(true);
        cmbPers.setDisable(false);
        cmbPhil.setDisable(false);
    }

    private void setTeamAi() {
        if (radAi.isSelected()
        && Objects.nonNull(cmbPhil.getValue())
        && Objects.nonNull(cmbPers.getValue())
        && !cmbPhil.getValue().equals("")
        && !cmbPers.getValue().equals("")) {
            targetTeam.addAI(cmbPers.getValue(), cmbPhil.getValue());
        }
    }
    private void verifyTeamInfo() {
        if (!txtCarFolder.getText().equals(""))
            lblFullPath.setText("Full Path: " + carsFolder.pathToCarFolder + "\\" + txtCarFolder.getText());

        boolean buttonDisabled = false;
        if (txtTeamName.getText().equals(""))
            buttonDisabled = true;

        if (txtCarFolder.getText().equals(""))
            buttonDisabled = true;

        if (Objects.isNull(clrTeamColor.getValue()))
            buttonDisabled = true;

        if (Objects.isNull(cmbPrevPos.getValue()))
            buttonDisabled = true;
            
        if (radAi.isSelected()) {
            if (Objects.isNull(cmbPers.getValue()))
                buttonDisabled = true;

            if (Objects.isNull(cmbPhil.getValue()))
                buttonDisabled = true;
        } else if (!radPlayer.isSelected())
            buttonDisabled = true;

        btnContinue.setDisable(buttonDisabled);
    }
}
