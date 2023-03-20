package com.ac.menus.facilities;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;

import com.ac.App;
import com.ac.fileparsing.jsonReader;
import com.ac.fileparsing.jsonWriter;
import com.ac.lib.uiAPI;
import com.ac.seasons.newSeason.seasonSettings;
import com.ac.seasons.newSeason.teamSetup;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class teamFacilitiesWindow implements Initializable{

    boolean resetCarsButton = true;
    private boolean sandboxMode = false;
    private seasonSettings season = new seasonSettings("", 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, false, false, false, false, null, null);
    public static teamFacilities facilities = null;
    private teamSetup playerTeam;


    @FXML
    private AnchorPane ancPrivTest;

    @FXML
    private AnchorPane ancPrivTestInfo;

    @FXML
    private AnchorPane ancResDev;

    @FXML
    private AnchorPane ancResDevInfo;

    @FXML
    private AnchorPane ancWindTunnel;

    @FXML
    private AnchorPane ancWindTunnelInfo;

    @FXML
    private Button btnGoToPrivTest;

    @FXML
    private Button btnGoToResDev;

    @FXML
    private Button btnGoToWindTunnel;

    @FXML
    private Button btnPrivTest;

    @FXML
    private Button btnResDev;

    @FXML
    private Button btnReturn;

    @FXML
    private Button btnWindTunnel;

    @FXML
    private ComboBox<?> cmbSelectedTeam;

    @FXML
    private ImageView imgPrivTest;

    @FXML
    private Label lblPrivTestAllowed;

    @FXML
    private Label lblPrivTestCost;

    @FXML
    private Label lblResDevLevel;

    @FXML
    private Label lblWindTunnelLevel;

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
    void btnGoToResDevClick(ActionEvent event) throws IOException {
        App.setRoot(new FXMLLoader(getClass().getResource("/researchDevelopment.fxml")));


    }

    @FXML
    void btnGoToWindTunnelClick(ActionEvent event) throws IOException {
        App.setRoot(new FXMLLoader(getClass().getResource("/windTunnel.fxml")));


    }

    @FXML
    void btnGoToPrivTestClick(ActionEvent event) throws IOException {
        if(season.regulations.privTesting)
            App.setRoot(new FXMLLoader(getClass().getResource("/privTesting.fxml")));


    }

    @FXML
    void btnPrivTestEnter(MouseEvent event) {
        ancPrivTest.setVisible(true);
        ancPrivTestInfo.setVisible(true);
        ancResDev.setVisible(false);
        ancResDevInfo.setVisible(false);
        ancWindTunnel.setVisible(false);
        ancWindTunnelInfo.setVisible(false);
    }

    @FXML
    void btnResDevEnter(MouseEvent event) {
        ancPrivTest.setVisible(false);
        ancPrivTestInfo.setVisible(false);
        ancResDev.setVisible(true);
        ancResDevInfo.setVisible(true);
        ancWindTunnel.setVisible(false);
        ancWindTunnelInfo.setVisible(false);

    }

    @FXML
    void btnWindTunnelEnter(MouseEvent event) {
        ancPrivTest.setVisible(false);
        ancPrivTestInfo.setVisible(false);
        ancResDev.setVisible(false);
        ancResDevInfo.setVisible(false);
        ancWindTunnel.setVisible(true);
        ancWindTunnelInfo.setVisible(true);

    }

    @FXML
    void btnReturnClick(ActionEvent event) throws IOException {
        App.setRoot(new FXMLLoader(getClass().getResource("/teamMenu.fxml")));


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
        // private testing costs
        int costMultiplyer = 35000;
        int flatCosts = season.getTotalPrizePool() * costMultiplyer;
        int sessionCost = Math.round((float) flatCosts / season.getRaceCount());

        // Set player team UI elements
        txtChampPos.setText(Integer.toString(playerTeam.getChampPos()));
        txtChhampPoints.setText(Integer.toString(playerTeam.getPoints()));
        txtMoney.setText(uiAPI.setCurrencyFormat(playerTeam.getMoney()));
        txtResearch.setText(Integer.toString(facilities.resDev.getResearchPoints()));
        recTeamColor.setFill(Color.valueOf(playerTeam.getTeamColor()));
        txtTeamName.setText(playerTeam.getTeamName());

        // Wind tunnel facility UI
        lblWindTunnelLevel.setText("Level: " + Integer.toString(facilities.wTunnel.level));

        // ResDev facility UI
        lblResDevLevel.setText("Level: " + Integer.toString(facilities.resDev.getLevel()));

        // PrivTest UI
        if(season.regulations.privTesting) {
            lblPrivTestAllowed.setText("Allowed by regulations");
            URL url = getClass().getResource("/images/private-testing.png");
            BufferedImage imgRace = null;
            try {
                imgRace = ImageIO.read(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Image image = SwingFXUtils.toFXImage(imgRace, null);
            imgPrivTest.setImage(image);
            lblPrivTestCost.setText("Cost: €" + uiAPI.setCurrencyFormat(sessionCost));
        } else {
            lblPrivTestAllowed.setText("Not allowed by regulations");
            URL url = getClass().getResource("/images/private-testing-grey.png");
            BufferedImage imgRace = null;
            try {
                imgRace = ImageIO.read(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Image image = SwingFXUtils.toFXImage(imgRace, null);
            imgPrivTest.setImage(image);
            lblPrivTestCost.setText("Cost: € ------");
            btnGoToPrivTest.setDisable(true);
        }
    }

}
