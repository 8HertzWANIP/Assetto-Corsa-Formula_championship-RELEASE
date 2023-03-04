package com.ac.menus.facilities;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

import com.ac.lib.aeroPartInventory;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;

public class aeropartObject extends researchDevelopment {
    int partIndex = 0;
    aeroPartInventory targetPart = null;

    @FXML
    private AnchorPane ancPartPane;

    @FXML
    private AnchorPane ancPartStats;

    @FXML
    private Label lblAverage;

    @FXML
    private Label lblDownforce;

    @FXML
    private Label lblDrag;

    @FXML
    private Label lblName;

    @FXML
    private Label lblLevel;

    @FXML
    private Label lblUntestedPart;

    @FXML
    private ProgressBar prgImprovement;

    @FXML
    private Rectangle recRarity;

    @FXML
    public void ancPartPaneClicked(MouseEvent event) {
        selectedPartIndex = partIndex;
        createNewPart = false;
        System.out.println("PARTINDEX [" + partIndex + "]");
        // txtSelectedPartName.setText(inventory[selectedPartIndex].part_name);
        // txtSelectedPartDf.setText(Integer.toString(inventory[selectedPartIndex].installed.downforce));
        // txtSelectedPartDr.setText(Integer.toString(inventory[selectedPartIndex].installed.drag));
        // txtSelectedPartAverageScore.setText(Integer.toString(inventory[selectedPartIndex].installed.average));
        // recSelectedPartRarity.setFill(returnRarityColor(season, inventory[selectedPartIndex].installed.average));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        partIndex = uiIndex;
        // aeroPartInventory[] localInventory =  jsonReader.parseAeroParts(playerTeam);
        targetPart = researchDevelopment.inventory[partIndex];
        System.out.println("Part name [" + targetPart.part_name + "]");
        float ProjectedImprovementBar;
        lblName.setText(targetPart.part_name);
        if (partsToLoad.equals("installed")) {
            lblLevel.setText("Level: " + Integer.toString(targetPart.installed.partLevel));
            if (targetPart.installed.stats_hidden) {
                ancPartStats.setVisible(false);
                lblUntestedPart.setVisible(true);
                prgImprovement.setProgress(1);
            } else {
                ancPartStats.setVisible(true);
                lblUntestedPart.setVisible(false);
                lblDownforce.setText(Integer.toString(targetPart.installed.downforce));
                lblDrag.setText(Integer.toString(targetPart.installed.drag));
                lblAverage.setText(Integer.toString(targetPart.installed.average));
                recRarity.setFill(returnRarityColor(season, targetPart.installed.average));
                if (targetPart.installed.improvementSpent > 0) {
                    ProjectedImprovementBar = 1 - (float) targetPart.installed.improvementSpent / (float) targetPart.installed.max_improvement;
                    prgImprovement.setProgress(ProjectedImprovementBar);
                }
                else if (targetPart.installed.max_improvement == 0) {
                    prgImprovement.setProgress(0);
                } else { 
                    prgImprovement.setProgress(1);
                }
            }
            recRarity.setFill(returnRarityColor(season, targetPart.installed.average));
        } else if  (partsToLoad.equals("constructed") && Objects.isNull(targetPart.constructed)) {
            lblName.setText("---");
            lblDownforce.setText("");
            lblDrag.setText("");
            lblAverage.setText("");
        } else if  (partsToLoad.equals("constructed") && !Objects.isNull(targetPart.constructed)) {
            lblLevel.setText("Level: " + Integer.toString(targetPart.constructed.partLevel));
            if (targetPart.constructed.stats_hidden) {
                ancPartStats.setVisible(false);
                lblUntestedPart.setVisible(true);
                prgImprovement.setProgress(1);
            } else {
                ancPartStats.setVisible(true);
                lblUntestedPart.setVisible(false);
                lblDownforce.setText(Integer.toString(targetPart.constructed.downforce));
                lblDrag.setText(Integer.toString(targetPart.constructed.drag));
                lblAverage.setText(Integer.toString(targetPart.constructed.average));
                if (targetPart.constructed.improvementSpent > 0) {
                    ProjectedImprovementBar = 1 - (float) targetPart.constructed.improvementSpent / (float) targetPart.constructed.max_improvement;
                    prgImprovement.setProgress(ProjectedImprovementBar);
                }
                else if (targetPart.constructed.max_improvement == 0) 
                    prgImprovement.setProgress(0);
                else 
                    prgImprovement.setProgress(1);
                    
            }
            recRarity.setFill(returnRarityColor(season, targetPart.constructed.average));
        }
    }
}
