package com.ac.backend;

import java.util.concurrent.ThreadLocalRandom;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

public class researchButton {

    @FXML
    private Button btnBuildPart;

    @FXML
    private Button btnResearchPart;

    @FXML
    private AnchorPane performancePane;

    @FXML
    private TextField txtResearchResult;

    @FXML
    void btnBuildPartClick(ActionEvent event) {
        int improvement = -2;
        int randomNum = ThreadLocalRandom.current().nextInt(1, 100);
        if (randomNum <= getResearchLevel()) {
            System.out.println("randomNum: [" + randomNum + "] target: [" + (getResearchLevel()) + "]");
            improvement = -1;
            randomNum = ThreadLocalRandom.current().nextInt(1, (100)) * 2;
            if (randomNum <= getResearchLevel()) {
                System.out.println("randomNum: [" + randomNum + "] target: [" + (getResearchLevel()) + "]");
                improvement = 1;
                randomNum = ThreadLocalRandom.current().nextInt(1, (100)) * 3;
                if (randomNum <= getResearchLevel()) {
                    System.out.println("randomNum: [" + randomNum + "] target: [" + (getResearchLevel()) + "]");
                    improvement = 3;
                    System.out.println("SUCCESS!");
                }
            }
        }
        if (Integer.parseInt(txtResearchResult.getText()) >= 0)
        txtResearchResult.setText(Integer.toString(improvement + Integer.parseInt(txtResearchResult.getText())));
    }

    private int getResearchLevel() {
        return Integer.parseInt(txtResearchResult.getText());
    }

    @FXML
    void btnMoneyClick(ActionEvent event) {
    }

    @FXML
    void btnResearchPartClick(ActionEvent event) {
        int improvement = 0;
        int randomNum = ThreadLocalRandom.current().nextInt(1, 100);
        if (randomNum >= getResearchLevel()) {
            System.out.println("randomNum: [" + randomNum + "] target: [" + (getResearchLevel()) + "]");
            improvement = 1;
            randomNum = ThreadLocalRandom.current().nextInt(1, (100)) * 2;
            if (randomNum >= getResearchLevel()) {
                System.out.println("randomNum: [" + randomNum + "] target: [" + (getResearchLevel()) + "]");
                improvement = 2;
                randomNum = ThreadLocalRandom.current().nextInt(1, (100)) * 3;
                if (randomNum >= getResearchLevel()) {
                    System.out.println("randomNum: [" + randomNum + "] target: [" + (getResearchLevel()) + "]");
                    improvement = 3;
                    System.out.println("SUCCESS!");
                }
            }
        }
        if (Integer.parseInt(txtResearchResult.getText()) >= 0)
        txtResearchResult.setText(Integer.toString(improvement + Integer.parseInt(txtResearchResult.getText())));

    }

    @FXML
    void btnReturnClick(ActionEvent event) {

    }

    @FXML
    void cmbSelectTeam(ActionEvent event) {

    }

    @FXML
    void txtResearchChanged(KeyEvent event) {

    }

}
