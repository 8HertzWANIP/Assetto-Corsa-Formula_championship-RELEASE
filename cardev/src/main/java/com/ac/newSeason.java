package com.ac;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.fxml.Initializable;

import com.ac.backend.jsonReader;
import com.ac.backend.jsonWriter;
import com.ac.backend.seasonSettings;

public class newSeason extends App implements Initializable{
    jsonWriter writer = new jsonWriter();
    jsonReader reader = new jsonReader();
    String prizeAllocation;
    int difficulty = 0;
    int raceRewards = 0;
    int setNumbersOnlyIndex;
    ToggleGroup raceTg = new ToggleGroup();


    @FXML
    private Button btnContinue;

    @FXML
    private Button btnReturn;
    
    @FXML
    private CheckBox chkEqualDev;

    @FXML
    private CheckBox chkEqualFunds;

    @FXML
    private Label lblEasy;

    @FXML
    private Label lblHard;

    @FXML
    private Label lblMedium;

    @FXML
    private Label lblVeryHard;
    
    @FXML
    private Label lblContinueDisabled;

    @FXML
    private RadioButton radMoneyAllocBoth;

    @FXML
    private RadioButton radMoneyAllocRace;

    @FXML
    private RadioButton radMoneyAllocSeason;

    @FXML
    private Slider sldDifficulty;

    @FXML
    private Slider sldMaxDf;

    @FXML
    private Slider sldMaxDrag;

    @FXML
    private Slider sldMinDf;

    @FXML
    private Slider sldMinDrag;

    @FXML
    private TextField txtFuelSize;

    @FXML
    private TextField txtMaxDfValue;

    @FXML
    private TextField txtMaxDragValue;

    @FXML
    private TextField txtMinDfValue;

    @FXML
    private TextField txtMinDragValue;

    @FXML
    private TextField txtPrizePool;

    @FXML
    private TextField txtProfileName;

    @FXML
    private TextField txtRaceCount;

    @FXML
    private TextField txtRaceLength;

    @FXML
    private TextField txtTeamCount;

    private void setPrizeMoney(String radioButtonResult) {
        switch (radioButtonResult) {
            case "Per race rewards only":
                raceRewards = 0;
                break;
            case "Final Championship standing rewards only":
                raceRewards = 1;
                break;
            case "Per race + Final Championship standing rewards":
                raceRewards = 2;
                break;
        
            default:
                break;
        }
    }

    @FXML
    void sldDifficultyChange(MouseEvent event) {
        difficulty = (int) Math.round(sldDifficulty.getValue());
        switch (difficulty) {
            case 0:
                lblEasy.setVisible(true);
                lblMedium.setVisible(false);
                lblHard.setVisible(false);
                lblVeryHard.setVisible(false);
                break;
            case 1:
                lblEasy.setVisible(false);
                lblMedium.setVisible(true);
                lblHard.setVisible(false);
                lblVeryHard.setVisible(false);
                break;
            case 2:
                lblEasy.setVisible(false);
                lblMedium.setVisible(false);
                lblHard.setVisible(true);
                lblVeryHard.setVisible(false);
                break;
            case 3:
                lblEasy.setVisible(false);
                lblMedium.setVisible(false);
                lblHard.setVisible(false);
                lblVeryHard.setVisible(true);
                break;
            default:
                break;
        }
    }

    @FXML
    void chkEqualDevToggle(KeyEvent event) {
        
    }

    @FXML
    void chkEqualFundsToggle(KeyEvent event) {

    }


    @FXML
    void btnContinueClick(ActionEvent event) throws IOException {

        // Profile name not set
        if (txtProfileName.getText().equals("")) {
            lblContinueDisabled.setVisible(true);
            lblContinueDisabled.setText("Profile name not valid.");
        } 
        
        // Team count not set
        if (txtTeamCount.getText().equals("")) {
            lblContinueDisabled.setVisible(true);
            lblContinueDisabled.setText("Team count not valid.");
        }
        
        // Race count not set
        if (txtRaceCount.getText().equals("")) {
            lblContinueDisabled.setVisible(true);
            lblContinueDisabled.setText("Races in season not valid.");
        }
        
        // Race count not set
        if (txtRaceLength.getText().equals("")) {
            lblContinueDisabled.setVisible(true);
            lblContinueDisabled.setText("Race length not valid.");
        }
        
        // race fuel not set
        if (txtFuelSize.getText().equals("")) {
            lblContinueDisabled.setVisible(true);
            lblContinueDisabled.setText("Race fuel tank size not valid.");
        }
        
        // Prize pool not set
        if (txtPrizePool.getText().equals("")) {
            lblContinueDisabled.setVisible(true);
            lblContinueDisabled.setText("Prize pool not valid.");
        }

        // Everything is set, continue to team setup
        if (!txtProfileName.getText().equals("")
        && !txtProfileName.getText().equals("")
        && !txtTeamCount.getText().equals("")
        && !txtRaceCount.getText().equals("")
        && !txtFuelSize.getText().equals("")
        && !txtPrizePool.getText().equals(""))
        {

            float maxDownforce = Math.round((Float.parseFloat((txtMaxDfValue.getText())) / 100) * 1000);
            float minDownforce = Math.round((Float.parseFloat((txtMinDfValue.getText())) / 100) * 1000);
            float minDrag = Math.round((Float.parseFloat((txtMinDragValue.getText())) / 100) * 1000);
            float maxDrag = Math.round((Float.parseFloat((txtMaxDragValue.getText())) / 100) * 1000);
            
            seasonSettings season = new seasonSettings(
            txtProfileName.getText(), 
            Integer.parseInt(txtTeamCount.getText()), 
            Integer.parseInt(txtRaceCount.getText()),
            Integer.parseInt(txtRaceLength.getText()),
            Integer.parseInt(txtFuelSize.getText()),
            Integer.parseInt(txtPrizePool.getText()),
            maxDownforce,
            minDownforce,
            minDrag,
            maxDrag,
            1000,
            difficulty,
            raceRewards,
            0,
            chkEqualDev.isSelected(),
            chkEqualFunds.isSelected(),
            false,
            false
            );
            loadedProfile = txtProfileName.getText();
            App.printLoadedProfile();
            writer.saveSeasonSettings(season);
            App.setRoot("newTeam");
        
        // no profile namme set
        } 
            
    }
    @FXML
    void btnReturnToMainMenu(ActionEvent event) throws IOException {
        App.setRoot("landingpage");
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        radMoneyAllocBoth.setToggleGroup(raceTg);
        radMoneyAllocRace.setToggleGroup(raceTg);
        radMoneyAllocSeason.setToggleGroup(raceTg); 
        radMoneyAllocBoth.setSelected(true);

        raceTg.selectedToggleProperty().addListener(new ChangeListener<Toggle>(){
            public void changed(ObservableValue<? extends Toggle> ov,
            Toggle old_toggle, Toggle new_toggl) {
                if (raceTg.getSelectedToggle() != null) {
                    RadioButton rb = (RadioButton) raceTg.getSelectedToggle();
                    setPrizeMoney(rb.getText());                   
                }
            }
        });
        setNumbersOnly();
    }

    private void setNumbersOnly() {
        // NUMBERS ONLY TEXT FIELD
        txtFuelSize.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, 
                String newValue) {
                if (!newValue.matches("\\d*")) {
                    txtFuelSize.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
        // NUMBERS ONLY TEXT FIELD
        txtPrizePool.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, 
                String newValue) {
                if (!newValue.matches("\\d*")) {
                    txtPrizePool.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
        // NUMBERS ONLY TEXT FIELD
        txtTeamCount.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, 
                String newValue) {
                if (!newValue.matches("\\d*")) {
                    txtTeamCount.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
        // NUMBERS ONLY TEXT FIELD
        txtRaceCount.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, 
                String newValue) {
                if (!newValue.matches("\\d*")) {
                    txtRaceCount.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
        // NUMBERS ONLY TEXT FIELD
        txtRaceLength.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, 
                String newValue) {
                if (!newValue.matches("\\d*")) {
                    txtRaceLength.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
    }
    

    @FXML
    void sldMaxDfDone(MouseEvent event) {
        System.out.println(sldMaxDf.getValue());
        txtMaxDfValue.setText(Double.toString(Math.round(sldMaxDf.getValue())).replace(".0", ""));
    }

    @FXML
    void sldMaxDragDone(MouseEvent event) {
        txtMaxDragValue.setText(Double.toString(Math.round(sldMaxDrag.getValue())).replace(".0", ""));

    }

    @FXML
    void sldMinDfDone(MouseEvent event) {
        txtMinDfValue.setText(Double.toString(Math.round(sldMinDf.getValue())).replace(".0", ""));

    }

    @FXML
    void sldMinDragDone(MouseEvent event) {
        txtMinDragValue.setText(Double.toString(Math.round(sldMinDrag.getValue())).replace(".0", ""));

    }

}
