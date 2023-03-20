package com.ac.seasons.newSeason;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javax.swing.JFileChooser;

import com.ac.App;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class carsFolder implements Initializable {

    public static String pathToCarFolder;

    @FXML
    private Button btnBrowse;

    @FXML
    private Button btnContinue;

    @FXML
    private Button btnNo;

    @FXML
    private Button btnReturn;

    @FXML
    private Button btnReturnSeasonSetup;

    @FXML
    private Button btnYes;

    @FXML
    private Label lblCarsBrowse;

    @FXML
    private Label lblCarsFolderFound;

    @FXML
    private Label lblCorrectFolder;

    @FXML
    private Label lblFilePath;

    @FXML
    private TextArea tarBrowseInfo;

    @FXML
    private TextField txtContentCars;

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
            btnContinue.setDisable(false);
        }
    }

    @FXML
    void btnContinueClick(ActionEvent event) throws IOException {
        if (!pathToCarFolder.contains("assettocorsa") && !pathToCarFolder.contains("content") && !pathToCarFolder.contains("cars")) {
            txtContentCars.setText("CARS FOLDER NOT VALID");
            pathToCarFolder = "";
        } else {
            App.setRoot(new FXMLLoader(getClass().getResource("/newTeam.fxml")));
        }

    }

    @FXML
    void btnNoClick(ActionEvent event) {
        btnNo.setVisible(false);
        btnYes.setVisible(false);
        lblCarsBrowse.setDisable(false);
        txtContentCars.setDisable(false);
        btnBrowse.setDisable(false);
        tarBrowseInfo.setDisable(false);
    }

    @FXML
    void btnReturnSeasonSetupClick(ActionEvent event) throws IOException {
        App.setRoot(new FXMLLoader(getClass().getResource("/seasonPointsWindow.fxml")));
    }

    @FXML
    void btnReturnToMainMenu(ActionEvent event) throws IOException {
        App.setRoot(new FXMLLoader(getClass().getResource("/landingPage.fxml")));
    }

    @FXML
    void btnYesClick(ActionEvent event) {
        btnNo.setVisible(false);
        btnYes.setVisible(false);
        btnContinue.setDisable(false);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        pathToCarFolder = "";

        File file = new File(System.getProperty("user.dir"));
        String strParentDirectory = file.getParent() + "\\content\\cars";
        System.out.println("Parent directory is : " + strParentDirectory);
        System.out.println("Working Directory = " + System.getProperty("user.dir"));

        if (strParentDirectory.contains("assettocorsa")) {
            txtContentCars.setText(strParentDirectory);
            pathToCarFolder = strParentDirectory;
            System.out.println("carFolder: [" + pathToCarFolder + "]");
    
            if (pathToCarFolder.contains("content\\cars") || pathToCarFolder.contains("content/cars")) {
                lblCarsFolderFound.setText("Assetto Corsa cars folder found:");
                lblFilePath.setText(pathToCarFolder);
            } else {
                lblCarsFolderFound.setText("Assetto Corsa cars folder not found:");
                btnNo.setVisible(false);
                btnYes.setVisible(false);
                lblCorrectFolder.setVisible(false);
                lblFilePath.setVisible(false);
                lblCarsBrowse.setDisable(false);
                txtContentCars.setDisable(false);
                btnBrowse.setDisable(false);
                tarBrowseInfo.setDisable(false);
            }
        } else {
            lblCarsFolderFound.setText("Assetto Corsa cars folder not found:");
            btnNo.setVisible(false);
            btnYes.setVisible(false);
            lblCorrectFolder.setVisible(false);
            lblFilePath.setVisible(false);
            lblCarsBrowse.setDisable(false);
            txtContentCars.setDisable(false);
            btnBrowse.setDisable(false);
            tarBrowseInfo.setDisable(false);
        }
    }

}
