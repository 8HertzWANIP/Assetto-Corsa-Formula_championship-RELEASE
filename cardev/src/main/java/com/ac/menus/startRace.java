package com.ac.menus;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.swing.JFileChooser;

import com.ac.App;
import com.ac.AI.aiController;
import com.ac.fileparsing.jsonReader;
import com.ac.fileparsing.jsonWriter;
import com.ac.lib.aeroPart;
import com.ac.seasons.newSeason.seasonSettings;
import com.ac.seasons.newSeason.teamSetup;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

public class startRace extends App implements Initializable {
    ArrayList<Double> teamRatings = new ArrayList<Double>();
    ArrayList<Pane> uiCarTypes;
    aiController ai_controller_1 = new aiController();
    public static ArrayList<teamSetup> loadedTeams = new ArrayList<teamSetup>();
    public static ArrayList<carAngleSetup> carAngleSetups = new ArrayList<carAngleSetup>();
    public static int carAngleSetupIndex = 0;
    ArrayList<aeroPart> aeroParts = new ArrayList<aeroPart>();
    seasonSettings season = new seasonSettings(
        "",
        0,
        0,
        0,
        0,
        0,
        0f,
        0f,
        0f,
        0f,
        1000,
        0,
        0,
        0,
        0,
        false,
        false,
        false,
        false,
        null,
        null
        );
    @FXML
    private Button btnCancelRace;

    @FXML
    private Button btnFinishRace;

    @FXML
    private Button btnReturn;

    @FXML
    private Button btnStartRace;

    @FXML
    private CheckBox chkPreSeasonTesting;

    @FXML
    private Label lblRaceCount;

    @FXML
    private Label lblPreseasonTesting;
    
    @FXML
    private Label lblLoadingTeam;

    @FXML
    private ProgressBar prgLoadingBar;

    @FXML
    private AnchorPane paneRace;

    @FXML
    void btnCancelRaceClick(ActionEvent event) throws IOException {
        season.setRaceCanceled(true);
        App.setRoot("teamMenu");
    }

    @FXML
    void btnFinishRaceClick(ActionEvent event) throws IOException {
        if (season.getCurrentRace() == 0 && !season.isPreseasonTestingCompleted()) {
            season.setCurrentRace(season.getCurrentRace() + 1);
            System.out.println("saved race Count: [" + season.getCurrentRace() + "]");
            jsonWriter.saveSeasonSettings(season);
            App.setRoot("incomeWindow");
        } else {
            App.setRoot("champPoints");
        }
    }

    @FXML
    void btnReturnClick(ActionEvent event) throws IOException {
        App.setRoot("teamMenu");
    }

    @FXML
    void btnStartRaceClick(ActionEvent event) throws InterruptedException, IOException {
        ArrayList<String> teamList = new ArrayList<String>();
        for (int i = 0; i < season.getTeamCount(); i++) {
            teamList.add(loadedTeams.get(i).getTeamName());
        }
        // ai_controller_1.fwAngle = carAngleSetups.get(0).getFwAngle();
        // ai_controller_1.rwAngle = carAngleSetups.get(0).getRwAngle();
        if (!season.raceCanceled) {
            ai_controller_1.setLblLoadingTeam(lblLoadingTeam);
            ai_controller_1.init(teamList);
        } else {
            lblLoadingTeam.setText("AI Teams setup. You can start the session in Assetto Corsa");
        }
        // Immediatly run AI and skip cancel/finished button clicking to continue to start from the first race and skip pre-season testing.
        if (chkPreSeasonTesting.isSelected()) {
            season.setPreseasonTestingCompleted(true);
            season.setCurrentRace(season.getCurrentRace() + 1);
            season.setRaceCanceled(false);
            lblRaceCount.setText(season.getCurrentRace() + " / " + season.getRaceCount());
            chkPreSeasonTesting.setSelected(false);
            lblPreseasonTesting.setVisible(false);
            chkPreSeasonTesting.setVisible(false);
            jsonWriter.saveSeasonSettings(season);
            App.setRoot("incomeWindow");
        } else {
            season.setRaceCanceled(true);
            btnFinishRace.setDisable(false);
            btnCancelRace.setDisable(false);
            btnStartRace.setDisable(true);
            btnReturn.setDisable(true);
        }
        jsonWriter.saveSeasonSettings(season);
    }

    private void loadTeamlist() {
        File file = new File(new JFileChooser().getFileSystemView().getDefaultDirectory().toString() + "\\Ac Cardev save data\\" + loadedProfile);
        String[] directories = file.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
        });
        // System.out.println(Arrays.toString(directories));
        ObservableList<String> teams = FXCollections.observableArrayList(directories);
        
        ArrayList<String> teamList = new ArrayList<String>();
        for (int i = 0; i < season.getTeamCount(); i++) {
            teamList.add(teams.get(i));
        }

        for (int i = 0; i < season.teamCount; i++) {
            if (teamList.get(i) != null) {
                loadedTeams.add(jsonReader.parseTeam(teamList.get(i)));
                System.out.println("Team: [" + loadedTeams.get(i).getTeamName() + "] loaded");
            } else {
                System.out.println("Target team is null: [" + teamList.get(i) + "]");
            }
        }
    }

    //TODO REFACTOR WINGANGLE SCRIPT
    private void generateDownforceLevelUi() throws IOException {
        // int[] angles = {0, 0};
        // System.out.println("Generating track downforce UI");
        // carAngleSetups = new ArrayList<carAngleSetup>();
        // ArrayList<String> carTeamList = new ArrayList<String>();

        // // copy teams info to teamPointsIndex class for ordering.
        // for (int i = 0; i < loadedTeams.size(); i++) {
        //     teamSetup targetTeam = loadedTeams.get(i);
        //     fileReader.clearAeroParts();
        //     fileReader.fillAeroParts(targetTeam.getCarFolder() + "\\data\\aero.ini");
        //     ArrayList<aeroPart> aeroParts = fileReader.getAeroParts();
        //     boolean newCarType = false;
        //     boolean onlyOnce = true;
        //     System.out.println("Checking targetTeam: [" + targetTeam.getTeamName() + "] part size: [" + aeroParts.size() + "]");
        //     for (int j = 0; j < aeroParts.size(); j++) {
        //         aeroPart part = aeroParts.get(j);
        //         if (part.getlabel().equals("Front Wing") || part.getlabel().equals("FW")) {
        //             if (part.getAngle() != angles[0]) {
        //                 System.out.println("Checking Part: [" + part.getlabel() + "] Angle 0: [" + part.getAngle() + "]");
        //                 angles[0] = part.getAngle();
        //                 newCarType = true;
        //             } else if (part.getAngle() == angles[0]) {
        //                 newCarType = false;
        //             }
        //         } else if (part.getlabel().equals("Rear Wing") || part.getlabel().equals("RW")) {
        //             if (part.getAngle() != angles[1]) {
        //                 System.out.println("Checking Part: [" + part.getlabel() + "] Angle 1: [" + part.getAngle() + "]");
        //                 angles[1] = part.getAngle();
        //                 newCarType = true;
        //             } else if (part.getAngle() == angles[1]) {
        //                 newCarType = false;
        //             }
        //         } else if (angles[0] != 0 && angles[1] != 0 && newCarType && onlyOnce) {
        //             carTeamList.add(targetTeam.getTeamName());
        //             carAngleSetup car = new carAngleSetup(angles[0], angles[1], angles[0], angles[1], carTeamList);
        //             System.out.println("carTeamList size: [" + carTeamList.size() + "]");
        //             carAngleSetups.add(car);
        //             newCarType = false;
        //             onlyOnce = false;
        //             break;
        //         } else if (!newCarType && carAngleSetups.size() > 0) {
        //             carTeamList.add(targetTeam.getTeamName());
        //             carAngleSetup car = new carAngleSetup(angles[0], angles[1], angles[0], angles[1], carTeamList);
        //             System.out.println("carTeamList size: [" + carTeamList.size() + "]");
        //             carAngleSetups.set(carAngleSetups.size() - 1, car);
        //             break;
        //         }
        //     }
        // }

        // uiCarTypes = new ArrayList<Pane>();
        // for (int i = 0; i < carAngleSetups.size(); i++) {
        //     carAngleSetupIndex = i;
        //     System.out.println("Drawing car setup windows. Size is: [" + carAngleSetups.size() + "]");
        //     Pane newLoadedPane =  FXMLLoader.load(getClass().getResource("trackDownforceObject.fxml"));
        //     newLoadedPane.setLayoutX(10);
        //     newLoadedPane.setLayoutY(100 + (160 * i));
        //     paneRace.getChildren().add(newLoadedPane);
        //     uiCarTypes.add(newLoadedPane);
        // }
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        season = jsonReader.parseSeasonSettings();

        lblLoadingTeam.setText("Press 'Start Session' before starting the session in Assetto Corsa");
        chkPreSeasonTesting.setSelected(false);

        if (season.getCurrentRace() == 0 && !season.isPreseasonTestingCompleted()) {
            lblPreseasonTesting.setVisible(true);
            chkPreSeasonTesting.setVisible(true);
        } else {
            lblPreseasonTesting.setVisible(false);
            chkPreSeasonTesting.setVisible(false);
        }

        if (loadedTeams.size() == 0) {
            loadTeamlist();
        }
        try {
            generateDownforceLevelUi();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.print("Current race: [" + season.getCurrentRace() + "]");
        lblRaceCount.setText(season.getCurrentRace() + " / " + season.getRaceCount());
    }
    
    public class carAngleSetup {
        int defaultFwAngle;
        int defaultRwAngle;
        int fwAngle;
        int rwAngle;
        ArrayList<String> teams;

        public carAngleSetup(int defaultFwAngle, int defaultRwAngle, int fwAngle, int rwAngle, ArrayList<String> teams) {
            this.defaultFwAngle = defaultFwAngle;
            this.defaultRwAngle = defaultRwAngle;
            this.fwAngle = fwAngle;
            this.rwAngle = rwAngle;
            this.teams = teams;
        }
        public int getDefaultFwAngle() {
            return defaultFwAngle;
        }
        public void setDefaultFwAngle(int defaultFwAngle) {
            this.defaultFwAngle = defaultFwAngle;
        }
        public int getDefaultRwAngle() {
            return defaultRwAngle;
        }
        public void setDefaultRwAngle(int defaultRwAngle) {
            this.defaultRwAngle = defaultRwAngle;
        }
        public int getFwAngle() {
            return fwAngle;
        }
        public void setFwAngle(int fwAngle) {
            this.fwAngle = fwAngle;
        }
        public int getRwAngle() {
            return rwAngle;
        }
        public void setRwAngle(int rwAngle) {
            this.rwAngle = rwAngle;
        }
        public ArrayList<String> getTeams() {
            return teams;
        }
        public void setTeams(ArrayList<String> teams) {
            this.teams = teams;
        }
    }

}
