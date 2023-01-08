package com.ac;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.JFileChooser;

import com.AI.aiController;
import com.ac.backend.jsonReader;
import com.ac.backend.aeroPart;
import com.ac.backend.carScaler;
import com.ac.backend.fileReader;
import com.ac.backend.fileWriter;
import com.ac.backend.jsonWriter;
import com.ac.backend.seasonSettings;
import com.ac.backend.teamSetup;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class aeroImprove extends App implements Initializable{

    @FXML
    private Button btnChampStandings;

    @FXML
    private Button btnImprove;

    @FXML
    private Button btnMoney;

    @FXML
    private Button btnPartNext;

    @FXML
    private Button btnPartPrev;

    @FXML
    private Button btnRunAI;

    @FXML
    private Button btnResetCars;

    @FXML
    private ComboBox<String> cmbSelectedTeam;

    @FXML
    private Label lblMinusDownforce;

    @FXML
    private Label lblMinusDrag;

    @FXML
    private Label lblPlusDownforce;

    @FXML
    private Label lblPlusDrag;

    @FXML
    private Slider sldImprove;

    @FXML
    private TextField txtChampPos;

    @FXML
    private TextField txtAeroRating;

    @FXML
    private TextField txtChhampPoints;

    @FXML
    private TextField txtCost;

    @FXML
    private TextField txtDownforce;

    @FXML
    private TextField txtDrag;

    @FXML
    private TextField txtInvestment;

    @FXML
    private TextField txtMoney;

    @FXML
    private TextField txtMoneyModifyer;

    @FXML
    private TextField txtPartName;

    @FXML
    private TextField txtResearch;

    @FXML
    private TextField txtTeamName;
    
    @FXML
    private Rectangle recTeamColor;
    
    Float maxDf;
    Float minDf;
    Float maxDr;
    Float minDr;
    int baseLine;
    int researchLevel;
    int foundAeroObjects;
    float dForceCost;
    float dragCost;
    private int money = 0;

    
    aiController ai_controller_1 = new aiController();
    fileReader fParser = new fileReader();
    fileWriter fWriter = new fileWriter();
    jsonReader jsonReader = new jsonReader();
    jsonWriter jsonWriter = new jsonWriter();
    seasonSettings season = new seasonSettings("", 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, false, false, false, false, null, null);
    ArrayList<aeroPart> aeroParts = fParser.getAeroParts();
    ArrayList<teamSetup> loadedTeams = new ArrayList<teamSetup>();
    teamSetup playerTeam;
    carScaler carScaler = new carScaler();
    private int selectedPartIndex = 0;
    Float improvePartFunds = 0f;
    Double improveDownforceFunds = 0d;
    Double improveDragFunds = 0d;
    public teamSetup team = new teamSetup("", "", "", "", "", 0, 0, 0, 0);
    //Checks if the aeroparts stats have been filtered.
    private boolean sandboxMode = false;

    private void loadSeasonSettings() {
        season = jsonReader.parseSeasonSettings();
        baseLine = (Integer) Math.round(season.getBaseline());
        maxDf = baseLine * (season.maxDownforce / 100);
        minDf = baseLine * (season.minDownforce / 100);
        maxDr = baseLine * (season.maxDrag / 100);
        minDr = baseLine * (season.minDrag / 100);

        System.out.println(maxDf);
        System.out.println(minDf);
        System.out.println(maxDr);
        System.out.println(minDr);
    }

    @FXML
    void btnChampStandingsClick(ActionEvent event) throws IOException {
        App.setRoot("champStandings");
    }

    @FXML
    void btnPerformanceChartClick(ActionEvent event) throws IOException {
        App.setRoot("carPerformanceChart");
    }

    @FXML
    void btnResetCars(ActionEvent event) {
        ArrayList<String> teamList = new ArrayList<String>();
        for (int i = 0; i < season.getTeamCount(); i++) {
            teamList.add(loadTeamlist().get(i));
        }
        carScaler.scaleCars(teamList);
    }

    private void loadTeam(String teamName) {
        if (team.teamName != "")
            aeroParts = filterAeroStats(aeroParts);
        lblPlusDrag.setText("0");
        lblMinusDrag.setText("0");

        if (sandboxMode) {
            team = jsonReader.parseTeam(teamName);
        } else {
            team = playerTeam;
        }
        recTeamColor.setFill(Color.valueOf(team.getTeamColor()));
        researchLevel = team.getResearchLevel();
        money = team.getMoney();

        txtTeamName.setText(teamName);
        txtResearch.setText(Integer.toString(researchLevel));
        aeroParts.clear();
        fileReader.setAeroParts(aeroParts);
        fileReader.fillAeroParts(team.getCarFolder() + "\\data\\aero.ini");

        foundAeroObjects = aeroParts.size();
        dForceCost = Math.round(2600000 / foundAeroObjects);
        dragCost =  Math.round(2600000 / foundAeroObjects);
        convertAeroStats(aeroParts);
        txtAeroRating.setText(Integer.toString(getCarAeroRating()));
        
        txtMoney.setText(Integer.toString(team.getMoney()));
        txtChampPos.setText(Integer.toString(team.getChampPos()));
        txtChhampPoints.setText(Integer.toString(team.getPoints()));
        loadSelectedPart();
    }

    private void loadTeamlistCombobox() {
        File file = new File(new JFileChooser().getFileSystemView().getDefaultDirectory().toString() + "\\Ac Cardev save data\\" + loadedProfile);
        String[] directories = file.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
        });
        System.out.println(Arrays.toString(directories));
        ObservableList<String> teams = FXCollections.observableArrayList(directories);
        cmbSelectedTeam.setItems(teams);
    }

    private ObservableList<String> loadTeamlist() {
        File file = new File(new JFileChooser().getFileSystemView().getDefaultDirectory().toString() + "\\Ac Cardev save data\\" + loadedProfile);
        String[] directories = file.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
        });
        // System.out.println(Arrays.toString(directories));
        ObservableList<String> teams = FXCollections.observableArrayList(directories);
        return teams;
    }

    @FXML
    void btnRunAiClick(ActionEvent event) throws IOException {
        App.setRoot("startRace");
        // ArrayList<String> teamList = new ArrayList<String>();
        // for (int i = 0; i < season.getTeamCount(); i++) {
        //     teamList.add(loadTeamlist().get(i));
        // }
        // ai_controller_1.init(teamList);
    }

    @FXML
    void cmbSelectTeam(ActionEvent event) {
        loadTeam(cmbSelectedTeam.getValue());
    }

    @FXML
    void txtResearchChanged(KeyEvent event) {
        researchLevel = Integer.parseInt(txtResearch.getText());
        team.setResearchLevel(researchLevel);
        jsonWriter.saveTeam(team);
    }

    @FXML
    void sliderChanged(MouseEvent event) {
        updatePartImprovement();        
    }

    @FXML
    void investmentChanged(KeyEvent event) {
        if (txtInvestment.getText() == "") {
            txtInvestment.setText("0");
        }
        updatePartImprovement();
    }

    void updatePartImprovement() {
        if (money >= Integer.parseInt(txtInvestment.getText())) {
            double sliderValue = sldImprove.getValue();
            double dfModifyer = (sliderValue / 100);
            double dragModifyer = (100 - sliderValue) / 100;
            improvePartFunds = Float.parseFloat(txtInvestment.getText());
            improveDownforceFunds = improvePartFunds * dfModifyer;
            improveDragFunds = improvePartFunds * dragModifyer;
            System.out.println(sliderValue);
            if (aeroParts.get(selectedPartIndex).downforce > 0) {
                calculateDownforce(improveDownforceFunds, dfModifyer);
            }
            if (aeroParts.get(selectedPartIndex).drag > 0) {
                calculateDrag(improveDragFunds, dragModifyer);
            }
        };
    }

    @FXML
    void btnMoneyClick(ActionEvent event) {
        moneyUpdate(Integer.parseInt(txtMoneyModifyer.getText()));
    }

    void moneyUpdate(int moneyChange) {
        money = money + moneyChange;
        txtMoney.setText(Integer.toString(money));
        team.setMoney(money);
        jsonWriter.saveTeam(team);
    }

    @FXML
    void selectNextPart(ActionEvent event) {
        if (selectedPartIndex < aeroParts.size() - 1)
            selectedPartIndex++;

        loadSelectedPart();
    }

    @FXML
    void selectPrevPart(ActionEvent event) {
        if (selectedPartIndex > 0)
            selectedPartIndex--;

        loadSelectedPart();
    }

    @FXML
    void improveConfirm(ActionEvent event) {
        moneyUpdate(Math.round(-improvePartFunds));
        txtInvestment.setText("0");
        String aeroIni = team.carFolder + "\\data\\aero.ini";
        String copyToIni = new JFileChooser().getFileSystemView().getDefaultDirectory().toString() + "\\Ac Cardev save data\\" + loadedProfile + "\\copyToIni.ini";
        ArrayList<Float> partImprovements = getPartImprovements();
        aeroParts.get(selectedPartIndex).downforce = Float.parseFloat(txtDownforce.getText()) + partImprovements.get(0);
        aeroParts.get(selectedPartIndex).drag = Float.parseFloat(txtDrag.getText()) + partImprovements.get(1);
        aeroParts = filterAeroStats(aeroParts);
        fWriter.writeAeroStats(aeroIni, copyToIni, aeroParts);
        aeroParts = convertAeroStats(aeroParts);
        txtAeroRating.setText(Integer.toString(getCarAeroRating()));
        loadSelectedPart();
        updatePartImprovement();
    }

    private ArrayList<Float> getPartImprovements() {
        ArrayList<Float> floatArray = new ArrayList<Float>();
        Float PlusDownforce = 0f;
        Float MinusDownforce = 0f;
        Float PlusDrag = 0f;
        Float MinusDrag = 0f;
        if (!lblPlusDownforce.getText().isEmpty())
            PlusDownforce = Float.parseFloat(lblPlusDownforce.getText());
        if (!lblMinusDownforce.getText().isEmpty())
            MinusDownforce = -Float.parseFloat(lblMinusDownforce.getText());
        if (!lblPlusDrag.getText().isEmpty())
            PlusDrag = Float.parseFloat(lblPlusDrag.getText());
        if (!lblMinusDrag.getText().isEmpty())
            MinusDrag = -Float.parseFloat(lblMinusDrag.getText());
        Float downforce = PlusDownforce - MinusDownforce;
        Float drag = PlusDrag - MinusDrag;
        System.out.println("DEBUG DOWNFORCE: [" + downforce + "]");
        System.out.println("DEBUG DRAG: [" + drag + "]");
        floatArray.add(downforce);
        floatArray.add(drag);
        lblPlusDownforce.setText("");
        lblMinusDownforce.setText("");
        lblPlusDrag.setText("");
        lblMinusDrag.setText("");


        return floatArray;
    }

    private void loadPlayerTeam() {
        ArrayList<String> teamList = new ArrayList<String>();
        boolean playerFound = false;
        for (int i = 0; i < season.getTeamCount(); i++) {
            teamList.add(loadTeamlist().get(i));
            loadedTeams.add(jsonReader.parseTeam(teamList.get(i)));
            System.out.println("Controller: " + loadedTeams.get(i).getController());

            if (loadedTeams.get(i).getController().contains("Player Team")) {
                System.out.println("Player team found");
                playerFound = true;
                playerTeam = loadedTeams.get(i);
                loadTeam(playerTeam.getTeamName());
                break;
            }
        }
        if (!playerFound) {
            sandboxMode = true;
            initialize(null, null);
        } else {
            txtChampPos.setText(Integer.toString(playerTeam.getChampPos()));
            txtChhampPoints.setText(Integer.toString(playerTeam.getPoints()));
        }

    }

    void loadSelectedPart(){
        txtDownforce.setText(Integer.toString(Math.round(aeroParts.get(selectedPartIndex).downforce)));
        txtDrag.setText(Integer.toString(Math.round(aeroParts.get(selectedPartIndex).drag)));
        txtPartName.setText(aeroParts.get(selectedPartIndex).getlabel());

    }
    
    // ------------------------------------------ \\
    // ----------- CALCULATE DOWNFORCE ---------- \\
    // ------------------------------------------ \\
    private void calculateDownforce(Double investment, double sliderModifyer)
    {
        lblPlusDownforce.setText("");
        lblMinusDownforce.setText("");
        Double funds = (double) Math.round(investment * sliderModifyer);
        Double currentDownforce = Double.parseDouble(txtDownforce.getText());
        Double dfIncrease = -80 + (sliderModifyer * 100);
        Double maxDfLimiter;
        if (Double.parseDouble(txtInvestment.getText()) > dForceCost) {
            
            float newDForceCost = dForceCost / (researchLevel / 2);
            
            for (int i = 0; funds > newDForceCost; i++) 
            {
                int improvement = 1;
                int randomNum = ThreadLocalRandom.current().nextInt(1, 100);
                if (randomNum < team.getResearchLevel()) {
                    // System.out.println("randomNum: [" + randomNum + "] target: [" + (team.getResearchLevel()) + "]");
                    improvement = 2;
                    randomNum = ThreadLocalRandom.current().nextInt(1, (150)) * 2;
                    if (randomNum < team.getResearchLevel()) {
                        // System.out.println("randomNum: [" + randomNum + "] target: [" + (team.getResearchLevel()) + "]");
                        improvement = 3;
                        randomNum = ThreadLocalRandom.current().nextInt(1, (200)) * 3;
                        if (randomNum < team.getResearchLevel()) {
                            // System.out.println("randomNum: [" + randomNum + "] target: [" + (team.getResearchLevel()) + "]");
                            improvement = 4;
                            System.out.println("SUCCESS!");
                        }
                    }
                }
                if (i > 20)
                    break;
                if (improveDownforceFunds > 0)
                    improveDownforceFunds -= newDForceCost;
                if (improveDownforceFunds > 0) {
                    maxDfLimiter = 10 - (((100 * (currentDownforce + dfIncrease)) / maxDf));
                    System.out.println("maxDfLimiter: ["+maxDfLimiter+"]");
                    dfIncrease += ((improvement * i) * season.getAeroIncreaseRatePlayer()) * maxDfLimiter;
                    newDForceCost += (newDForceCost * 0.25) * i;
                }
            }
            if (dfIncrease < 0) {
                lblMinusDownforce.setText(""+(int) Math.round(dfIncrease));
                lblPlusDownforce.setText("");
            } else if (dfIncrease > 0) {
                lblPlusDownforce.setText("+"+(int) Math.round(dfIncrease));
                lblMinusDownforce.setText("");
            }
            if (improveDownforceFunds - newDForceCost > 0) {
            }
        }
    }
    

    // ------------------------------------------ \\
    // ------------- CALCULATE DRAG ------------- \\
    // ------------------------------------------ \\
    private void calculateDrag(Double investment, double sliderModifyer)
    {
        
        lblPlusDrag.setText("");
        lblMinusDrag.setText("");
        Double funds = (double) Math.round(investment * sliderModifyer);
        Double currentDrag = Double.parseDouble(txtDrag.getText());
        Double dragDecrease = -80 + (sliderModifyer * 100);
        Double maxDragLimiter;
        if (Double.parseDouble(txtInvestment.getText()) > dragCost) {
            
            float newDragCost = dragCost / (researchLevel / 2);
            
            for (int i = 0; funds > newDragCost; i++) 
            {
                int improvement = 1;
                int randomNum = ThreadLocalRandom.current().nextInt(1, 100);
                if (randomNum < team.getResearchLevel()) {
                    // System.out.println("randomNum: [" + randomNum + "] target: [" + (team.getResearchLevel()) + "]");
                    improvement = 2;
                    randomNum = ThreadLocalRandom.current().nextInt(1, (150)) * 2;
                    if (randomNum < team.getResearchLevel()) {
                        // System.out.println("randomNum: [" + randomNum + "] target: [" + (team.getResearchLevel()) + "]");
                        improvement = 3;
                        randomNum = ThreadLocalRandom.current().nextInt(1, (200)) * 3;
                        if (randomNum < team.getResearchLevel()) {
                            // System.out.println("randomNum: [" + randomNum + "] target: [" + (team.getResearchLevel()) + "]");
                            improvement = 4;
                            System.out.println("SUCCESS!");
                        }
                    }
                }
                if (i > 20)
                    break;
                if (improveDragFunds > 0)
                    improveDragFunds -= newDragCost;
                if (improveDragFunds > 0) {
                    maxDragLimiter = 10 - (((100 * (currentDrag + dragDecrease)) / maxDr));
                    System.out.println("maxDragLimiter: ["+maxDragLimiter+"]");
                    dragDecrease += ((improvement * i) * season.getDragIncreaseRatePlayer()) * maxDragLimiter;
                    newDragCost += (newDragCost * 0.25) * i;
                }
            }
            if (dragDecrease < 0) {
                lblMinusDrag.setText(""+(int) Math.round(dragDecrease));
                lblPlusDrag.setText("");
            } else if (dragDecrease > 0) {
                lblPlusDrag.setText("+"+(int) Math.round(dragDecrease));
                lblMinusDrag.setText("");
            }
            if (improveDragFunds - newDragCost > 0) {
            }
        }
    }

    private int getCarAeroRating() {
        int downforceObjects = 0;
        int dragObjects = 0;
        Float rating = 0f;
        Float rating2 = 0f;
        for (int i = 0; i < aeroParts.size(); i++) {
            if (aeroParts.get(i).getDownforce() > 0) {
                downforceObjects++;
            }
            if (aeroParts.get(i).getDrag() > 0) {
                dragObjects++;
            }
            rating += aeroParts.get(i).getDownforce();
            rating2 += aeroParts.get(i).getDrag();
            System.out.println("rating = [" + rating + "]");
        }
        System.out.println("rating 2 = [" + rating + "]");
        int intRating = ((Integer) Math.round(rating / downforceObjects)) + ((Integer) Math.round(rating2 / dragObjects));
        System.out.println("rating 3 = [" + rating + "]");
        return intRating;
    }

    private ArrayList<aeroPart> filterAeroStats(ArrayList<aeroPart> parts) {
        for (int i = 0; i < parts.size(); i++) {
            if (parts.get(i).drag != 0.0f) {
                parts.get(i).drag = baseLine - (parts.get(i).drag - baseLine);
                parts.get(i).drag = parts.get(i).drag / baseLine;
                parts.get(i).drag = Math.round(parts.get(i).drag * 100000.0f) / 100000.0f;
                System.out.println(parts.get(i).drag);
                
            }
            if (parts.get(i).downforce > 0.01f) {
                parts.get(i).downforce = parts.get(i).downforce / baseLine;
                parts.get(i).downforce = Math.round(parts.get(i).downforce * 100000.0f) / 100000.0f;
                System.out.println(parts.get(i).downforce);
            }

        }
        return parts;
    }

    private ArrayList<aeroPart> convertAeroStats(ArrayList<aeroPart> parts) {
        for (int i = 0; i < parts.size(); i++) {
            if (parts.get(i).drag != 0.0f) {
                parts.get(i).drag = parts.get(i).drag * baseLine;
                parts.get(i).drag = Math.round(baseLine - (parts.get(i).drag - baseLine));
                
            }
            if (parts.get(i).downforce != 0.0f) {
                parts.get(i).downforce = Math.round(parts.get(i).downforce * baseLine);
            }
        }
        return parts;
    }

    @FXML
    public void initialize(URL arg0, ResourceBundle arg1) {
        researchLevel = Integer.parseInt(txtResearch.getText());
        loadSeasonSettings();
        if (sandboxMode) {
            cmbSelectedTeam.setVisible(true);
            // btnMoney.setVisible(true);
            // txtMoneyModifyer.setVisible(true);
            // btnResetCars.setVisible(true);
            loadTeamlistCombobox();
        } else {
            cmbSelectedTeam.setVisible(false);
            btnMoney.setVisible(false);
            txtMoneyModifyer.setVisible(false);
            btnResetCars.setVisible(false);
            loadPlayerTeam();
        }
        txtInvestment.setText("0");
    }
}

/*

// NUMBERS ONLY TEXT FIELD
textField.textProperty().addListener(new ChangeListener<String>() {
    @Override
    public void changed(ObservableValue<? extends String> observable, String oldValue, 
        String newValue) {
        if (!newValue.matches("\\d*")) {
            textField.setText(newValue.replaceAll("[^\\d]", ""));
        }
    }
});
 */