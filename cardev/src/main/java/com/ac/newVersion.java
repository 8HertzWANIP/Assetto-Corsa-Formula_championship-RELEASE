package com.ac;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;

public class newVersion extends App {

    private static Scene mainScene;

    @FXML
    private Button btnContinue;

    @FXML
    private Button btnVersion;

    @FXML
    void btnContinueClick(ActionEvent event) throws IOException {
        mainScene = new Scene(loadFXML("landingPage"), 1280, 720);
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
