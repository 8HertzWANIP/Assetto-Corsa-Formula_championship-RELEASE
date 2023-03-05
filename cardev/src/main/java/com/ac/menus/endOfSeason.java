package com.ac.menus;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.ac.App;
import com.ac.fileparsing.jsonReader;
import com.ac.lib.carScaler;
import com.ac.lib.uiAPI;
import com.ac.menus.facilities.teamFacilities;
import com.ac.seasons.newSeason.seasonSettings;
import com.ac.seasons.newSeason.teamSetup;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class endOfSeason implements Initializable {
    private boolean sandboxMode = false;
    private seasonSettings season = new seasonSettings("", 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, false, false, false, false, null, null);
    public static teamFacilities facilities = null;
    private teamSetup playerTeam;
    private carScaler scaler = new carScaler();

    @FXML
    private Button btnReturn;

    @FXML
    private ComboBox<?> cmbSelectedTeam;

    @FXML
    private Label lblCarFuel;

    @FXML
    private Label lblFinishingPos2;

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
    private TextField txtResDevLvl;

    @FXML
    private TextField txtTeamName;

    @FXML
    void btnReturnClick(ActionEvent event) throws IOException {
        App.setRoot("landingPage");
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
            facilities = jsonReader.parseFacilities(playerTeam);

            
            txtResearch.setText(Integer.toString(facilities.resDev.getResearchPoints()));
            txtChampPos.setText(Integer.toString(playerTeam.getChampPos()));
            txtChhampPoints.setText(Integer.toString(playerTeam.getPoints()));
            txtMoney.setText(uiAPI.setCurrencyFormat(playerTeam.getMoney()));
            txtResearch.setText(Integer.toString(facilities.resDev.getExperience()));
            txtResDevLvl.setText(Integer.toString(facilities.resDev.getLevel()));
            recTeamColor.setFill(Color.valueOf(playerTeam.getTeamColor()));
            txtTeamName.setText(playerTeam.getTeamName());

            if (playerTeam.carInfo.getDefaultFuel() > 0)
                scaler.resetCarsFuelToDefault(season);
            else if (playerTeam.carInfo.getDefaultFuel() == -1)
                lblCarFuel.setText("Alpha-v0.1 save detected, car fuel could not be reset. Create new car folders if starting a new season.");
            else
                lblCarFuel.setText("");

            switch (uiAPI.returnTextInt(txtChampPos)) {
                case 1:
                    lblFinishingPos2.setText("st");
                    break;
                    case 2:
                        lblFinishingPos2.setText("nd");
                        break;
                        case 3:
                            lblFinishingPos2.setText("rd");
                            break;
            
                default:
                    break;
            }
        }
    }

}
