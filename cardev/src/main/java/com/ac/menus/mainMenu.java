package com.ac.menus;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

import javax.swing.JFileChooser;

import com.ac.App;
import com.ac.fileparsing.jsonReader;
import com.ac.seasons.newSeason.seasonSettings;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class mainMenu extends App implements Initializable{

    @FXML
    private Button btnLoadSeason;

    @FXML
    private Button btnNewSeason;

    @FXML
    private Button btnDiscord;

    @FXML
    private Button btnQuit;

    @FXML
    private ComboBox<String> cmbSelectSeason;

    @FXML
    private Label lblErrorContent;

    @FXML
    private Label lblErrorMsg;
    
    public static String unsupportedCars = "";

    @FXML
    void btnCloseWindow(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    void btnLoadSeasonClick(ActionEvent event) throws IOException {
        if (loadedProfile != "") {
            App.setSeasonData();
            if (!seasonData.unsupportedCarFound)
                loadSeason(jsonReader.parseSeasonSettings());
            else
                App.setRoot(new FXMLLoader(getClass().getResource("/landingPage.fxml")));
        }
    }

    @FXML
    void btnDiscordClick(ActionEvent event) throws IOException {
        try {
            String url = "https://discord.gg/JKHJzYA99w";
            java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
        }
        catch (java.io.IOException e) {
            System.out.println(e.getMessage());
        }
    }

    void loadSeason(seasonSettings season) throws IOException {
        if (season.getCurrentRace() > season.getRaceCount()) {
            System.out.println("Season finished");
            setRoot(new FXMLLoader(getClass().getResource("/endOfSeason.fxml")));
        } else {
            // Load the season and set/reset values
            setRoot(new FXMLLoader(getClass().getResource("/teamMenu.fxml")));
        }
    }

    @FXML
    void cmbSelectSeasonFunc(ActionEvent event) {
        loadedProfile = cmbSelectSeason.getValue();
        System.out.println("loadedProfile: [" + loadedProfile + "]");
    }

    @FXML
    void startNewSeason(ActionEvent event) throws IOException {
        App.setRoot(new FXMLLoader(getClass().getResource("/newSeason.fxml")));
    }

    public void changeStage(Stage stage, String fxmlString) throws IOException {
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource(fxmlString));
            Scene scene = new Scene(root);
            stage.setTitle("AC Car Development");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {}
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        File file = new File(new JFileChooser().getFileSystemView().getDefaultDirectory().toString() + "\\Ac Cardev save data\\");
        if (! file.exists()){
            file.mkdirs();
        }
        String[] directories = file.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
        });
        System.out.println(Arrays.toString(directories));
        ObservableList<String> test = FXCollections.observableArrayList(directories);
        cmbSelectSeason.setItems(test);

        if (!unsupportedCars.equals("")) {
            lblErrorMsg.setVisible(true);
            lblErrorContent.setVisible(true);
            lblErrorContent.setText(unsupportedCars);
            unsupportedCars = "";
        }
    }
}
