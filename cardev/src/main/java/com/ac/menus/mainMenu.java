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
import javafx.stage.Stage;

public class mainMenu extends App implements Initializable{

    @FXML
    private Button btnLoadSeason;

    @FXML
    private Button btnNewSeason;

    @FXML
    private Button btnQuit;

    @FXML
    private ComboBox<String> cmbSelectSeason;

    @FXML
    void btnCloseWindow(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    void btnLoadSeasonClick(ActionEvent event) throws IOException {
        if (loadedProfile != "") {
            App.setSeasonData();
            loadSeason(jsonReader.parseSeasonSettings());
        }
    }

    void loadSeason(seasonSettings season) throws IOException {
        if (season.getCurrentRace() > season.getRaceCount()) {
            System.out.println("Season finished");
            setRoot("endOfSeason");
        } else {
            // Load the season and set/reset values
            setRoot("teamMenu");
        }
    }

    @FXML
    void cmbSelectSeasonFunc(ActionEvent event) {
        loadedProfile = cmbSelectSeason.getValue();
        System.out.println("loadedProfile: [" + loadedProfile + "]");
    }

    @FXML
    void startNewSeason(ActionEvent event) throws IOException {
        App.setRoot("newSeason");
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
    }
}
