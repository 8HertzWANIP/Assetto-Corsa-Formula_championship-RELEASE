package com.ac.seasons.newSeason;

import java.io.IOException;

import com.ac.App;
import com.ac.fileparsing.jsonWriter;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;

public class regulations {

    @FXML
    private Button btnContinue;

    @FXML
    private Button btnReturn;

    @FXML
    private CheckBox chkPrivTestingAllowed;

    @FXML
    void btnContinueClick(ActionEvent event) throws IOException {
        newSeason.season.addSeasonRegs();
        newSeason.season.regulations.setPrivTesting(chkPrivTestingAllowed.isSelected());
        jsonWriter.saveSeasonSettings(newSeason.season);
        App.setRoot(new FXMLLoader(getClass().getResource("/seasonPointsWindow.fxml")));
    }

    @FXML
    void btnReturnToMainMenu(ActionEvent event) throws IOException {
        App.setRoot(new FXMLLoader(getClass().getResource("/newSeason.fxml")));
    }
}
