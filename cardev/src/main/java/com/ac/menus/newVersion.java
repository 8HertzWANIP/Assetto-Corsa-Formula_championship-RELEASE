package com.ac.menus;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import com.ac.App;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;

public class newVersion extends App {

    private static Scene mainScene;

    @FXML
    private Button btnContinue;

    @FXML
    private Button btnVersion;

    @FXML
    void btnContinueClick(ActionEvent event) throws IOException {
        URL urlLand = new File("cardev/src/main/resources/com/ac/" + defaultWindow + ".fxml").toURI().toURL();
        mainScene = new Scene(FXMLLoader.load(urlLand), 1280, 720);
        scene = mainScene;
        loadedStage.setScene(mainScene);
        loadedStage.setResizable(false);
        loadedStage.show();
    }

    @FXML
    void btnVersionClick(ActionEvent event) {
        try {
            String url = "https://www.patreon.com/Formula_Championship";
            java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
        }
        catch (java.io.IOException e) {
            System.out.println(e.getMessage());
        }
    }

}
