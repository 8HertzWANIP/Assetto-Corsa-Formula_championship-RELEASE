package com.ac.backend;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

import javax.swing.JFileChooser;

import com.ac.App;
import com.ac.startRace;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class trackDownforceObject extends startRace {
    int index;
    fileReader fReader = new fileReader();
    fileWriter fWriter = new fileWriter();

    @FXML
    private VBox VBox;

    @FXML
    private Button btnSave;

    @FXML
    private Label lblCarNumber;

    @FXML
    private Label lblTitle;

    @FXML
    private AnchorPane mainPane;

    @FXML
    private TextField txtFW;

    @FXML
    private TextField txtRW;

    @FXML
    void btnSaveClick(ActionEvent event) {
        // Set input wing angle
        carAngleSetups.get(index).setFwAngle(Integer.parseInt(txtFW.getText()));
        carAngleSetups.get(index).setRwAngle(Integer.parseInt(txtRW.getText()));

        // Cycle trough all teams to change default wing angle.
        int[] wingTargetIndex = {-1, -1};
        for (int i = 0; i < loadedTeams.size(); i++) {
            teamSetup targetTeam = loadedTeams.get(i);
            // Get target teamm aeroparts
            fileReader.clearAeroParts();
            fileReader.fillAeroParts(targetTeam.getCarFolder() + "\\data\\aero.ini");
            ArrayList<aeroPart> aeroParts = new ArrayList<aeroPart>();
            aeroParts = fReader.getAeroParts();

            // If front and rear wing have not been found, filter trough all parts till both are found.
            System.out.println("wingTargetIndex 1: [" + wingTargetIndex[0] + "] - [" + wingTargetIndex[1] + "]");
            if (wingTargetIndex[0] == -1 && wingTargetIndex[1] == -1) {
                for (int j = 0; j < aeroParts.size(); j++) {
                    aeroPart part = aeroParts.get(j);
                    if (part.getlabel().contains("Front Wing") || part.getlabel().contains("FW")) {
                        wingTargetIndex[0] = j;
                        // Pick the right downforceObjectBox (this) angle value
                        for (int k = 0; k < carAngleSetups.size(); k++) {
                            System.out.println("target team in teams array: [" + carAngleSetups.get(k).getTeams().contains(targetTeam.getTeamName()) + "]");
                            if (carAngleSetups.get(k).getTeams().contains(targetTeam.getTeamName())) {
                                aeroParts.get(j).setAngle(carAngleSetups.get(k).getFwAngle());
                            }
                            if (wingTargetIndex[0] == -1 && wingTargetIndex[1] == -1) {
                                // break;
                            }
                        }
                    }
                    if (part.getlabel().contains("Rear Wing") || part.getlabel().contains("RW")) {
                        wingTargetIndex[1] = j;
                        // Pick the right downforceObjectBox (this) angle value
                        for (int k = 0; k < carAngleSetups.size(); k++) {
                            if (carAngleSetups.get(k).getTeams().contains(targetTeam.getTeamName())) {
                                aeroParts.get(j).setAngle(carAngleSetups.get(k).getRwAngle());
                            }
                            if (wingTargetIndex[0] == -1 && wingTargetIndex[1] == -1) {
                                // break;
                            }
                        }
                    }
                }
                System.out.println("wingTargetIndex 2: [" + wingTargetIndex[0] + "] - [" + wingTargetIndex[1] + "]");
            } else if (wingTargetIndex[0] > -1 && wingTargetIndex[1] > -1) {
                // Pick the right downforceObjectBox (this) angle value
                if (carAngleSetups.get(index).getTeams().contains(targetTeam.getTeamName())) {
                    System.out.print("wingTarget 1: [" + wingTargetIndex[0] + "]");
                    System.out.print("getAngle 1: [" + aeroParts.get(wingTargetIndex[0]).getAngle() + "]");
                    System.out.print("setAngle 1: [" + carAngleSetups.get(index).getFwAngle() + "]");
                    System.out.print("wingTarget 2: [" + wingTargetIndex[1] + "]");
                    System.out.print("getAngle 2: [" + aeroParts.get(wingTargetIndex[1]).getAngle() + "]");
                    System.out.print("setAngle 2: [" + carAngleSetups.get(index).getRwAngle() + "]");
                    aeroParts.get(wingTargetIndex[0]).setAngle(carAngleSetups.get(index).getFwAngle());
                    aeroParts.get(wingTargetIndex[1]).setAngle(carAngleSetups.get(index).getRwAngle());
                }
            }
            String aeroIni = targetTeam.getCarFolder() + "\\data\\aero.ini";
            String copyToIni = new JFileChooser().getFileSystemView().getDefaultDirectory().toString() + "\\Ac Cardev save data\\" + App.loadedProfile + "\\copyToIni.ini";
            fileWriter.writeAeroStats(aeroIni, copyToIni, aeroParts);
        }
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        index = carAngleSetupIndex;
        int carNumber = index + 1;
        System.out.println("Drawing car setup windows. Size is: [" + startRace.carAngleSetups.size() + "] index is: [" + index + "]");
        txtFW.setText(Integer.toString(startRace.carAngleSetups.get(index).getDefaultFwAngle()));
        txtRW.setText(Integer.toString(startRace.carAngleSetups.get(index).getDefaultRwAngle()));
        lblCarNumber.setText("Car " + carNumber);
        if (index > 0) {
            lblTitle.setText("");
        }
    }

    void closeWindow() {
        final Stage stage = (Stage) mainPane.getScene().getWindow();
        stage.close();
    }

}
