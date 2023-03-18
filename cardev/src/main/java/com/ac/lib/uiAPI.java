package com.ac.lib;

import java.io.File;
import java.io.FilenameFilter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

import javax.swing.JFileChooser;

import com.ac.App;
import com.ac.fileparsing.jsonReader;
import com.ac.fileparsing.jsonWriter;
import com.ac.seasons.newSeason.seasonSettings;
import com.ac.seasons.newSeason.teamSetup;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TextField;

public interface uiAPI {
    
    /** Returns an Integer from the supplied textfield
     * @param tField textField
     * @return int
     */
    public static int returnTextInt(TextField tField) {
        return Integer.parseInt(tField.getText());
    }
    
    /** Returns a String with the currency format
     * @param int money to convert to string
     * @return Currency String
     */
    public static String setCurrencyFormat(int money) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.GERMANY);
        String moneyString = formatter.format(money);
        if (moneyString.endsWith(",00 €")) {
            int centsIndex = moneyString.lastIndexOf(",00 €");
            if (centsIndex != -1) {
                moneyString = moneyString.substring(0, centsIndex);
            }
        }
        return moneyString;
    }

    /** sets the supplied textFields to only accept numeric characters
     * @param txtFields Array
     */
    public static void setNumbersOnly(TextField[] txtFields) {
        for (TextField textField : txtFields) {
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
        }
    }

    public static teamSetup loadPlayerTeam(seasonSettings season) {
        System.out.println("Player team is [" + season.getPlayerTeam() + "]");
        teamSetup playerTeam = new teamSetup(null, null, null, null, null, 0, 0, 0, 0);
        if (Objects.isNull(season.getPlayerTeam()))
            playerTeam = setPlayerTeam(season);
        else {
            playerTeam = jsonReader.parseTeam(season.getPlayerTeam());
        }
        return playerTeam;
    }

    private static teamSetup setPlayerTeam(seasonSettings season) {
        ArrayList<String> teamList = new ArrayList<String>();
        ArrayList<teamSetup> loadedTeams = new ArrayList<teamSetup>();
        teamSetup playerTeam = new teamSetup(null, null, null, null, null, 0, 0, 0, 0);
        for (int i = 0; i < season.getTeamCount(); i++) {
            teamList.add(loadTeamlistStrings().get(i));
            loadedTeams.add(jsonReader.parseTeam(teamList.get(i)));
            System.out.println("Controller: " + loadedTeams.get(i).getController());

            if (loadedTeams.get(i).getController().contains("Player Team")) {
                System.out.println("Player team found");
                season.setPlayerTeam(loadedTeams.get(i).getTeamName());
                playerTeam = loadedTeams.get(i);
                jsonWriter.saveSeasonSettings(season);
                break;
            }
        }
        return playerTeam;
    }

    private static ObservableList<String> loadTeamlistStrings() {
        File file = new File(new JFileChooser().getFileSystemView().getDefaultDirectory().toString() + "\\Ac Cardev save data\\" + App.loadedProfile);
        String[] directories = file.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
        });
        ObservableList<String> teams = FXCollections.observableArrayList(directories);
        return teams;
    }
}
