package com.ac.backend;

import java.util.ArrayList;

import javax.swing.JFileChooser;

import com.ac.App;

public class carScaler {
    seasonSettings season = new seasonSettings(null, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, false, false, false, false);
    jsonReader jReader = new jsonReader();
    fileWriter fWriter = new fileWriter();
    fileReader fReader = new fileReader();
    ArrayList<teamSetup> loadedTeams = new ArrayList<teamSetup>();
    ArrayList<aeroPart> aeroParts = new ArrayList<aeroPart>();
    carConfig car = new carConfig(0, 0, 0, 0);
    float maxDf = 0f;
    float maxDr = 0f;
    float minDf = 0f;
    float minDr = 0f;

    public void scaleCars(ArrayList<String> teamList) {
        season = jReader.parseSeasonSettings();

        maxDf = season.maxDownforce;
        minDf = season.minDownforce;
        maxDr = season.maxDrag;
        minDr = season.minDrag;

        System.out.println("Season Loaded");
        for (int i = 0; i < season.teamCount; i++) {
            if (teamList.get(i) != null) {
                loadedTeams.add(jReader.parseTeam(teamList.get(i)));
            } else {
                System.out.println("Target team is null: [" + teamList.get(i) + "]");
            }
        }
        System.out.println("Teams loaded");
        // fileParser.setAeroParts(new ArrayList<aeroPart>());
        scaleCarsToDefault();
    }

    private void scaleCarsToDefault() {
        for (int i = 0; i < loadedTeams.size(); i++) {
            aeroParts.clear();
            teamSetup targetTeam = loadedTeams.get(i);
            System.out.println("start AI team [" + targetTeam.getTeamName() + "]");
            fileReader.setAeroParts(new ArrayList<aeroPart>());
            fileReader.fillAeroParts(targetTeam.getCarFolder() + "\\data\\aero.ini");
            aeroParts = fReader.getAeroParts();
            car = fileReader.fillCarStats(targetTeam.getCarFolder() + "\\data\\car.ini");
            System.out.println("Car folder loaded");
            System.out.println("Loaded aeroparts: [" + aeroParts.size() + "]");

            // Reset only the stats that have a value greater than 0
            for (int j = 0; j < aeroParts.size(); j++) {
                if (aeroParts.get(j).getDownforce() > 0) {
                    aeroParts.get(j).setDownforce(minDf);
                }
                if (aeroParts.get(j).getDrag() > 0) {
                    aeroParts.get(j).setDrag(minDr);
                }
                System.out.println("getDownforce: [" + aeroParts.get(j).getDownforce() + "]");
                System.out.println("getDrag: [" + aeroParts.get(j).getDrag() + "]");        
            }

            System.out.println("fuel: [" + car.getFuel() + "]");
            System.out.println("max_fuel: [" + car.getMaxFuel() + "]");        
            System.out.println("season fuel tank size: [" + season.getFuelTankSize() / 100d + "]");
            System.out.println("max_fuel resized: [" + Math.round(car.getMaxFuel() * (season.getFuelTankSize() / 100d)) + "]");
            // Set car fuel to season settings
            car.setOldMaxFuel(car.getMaxFuel());
            car.setOldFuel(car.getFuel());
            car.setMaxFuel((int) Math.round(car.getMaxFuel() * (season.getFuelTankSize() / 100d)));
            car.setFuelToTankSize();
            saveTeam(targetTeam);
        }
    }

    private void saveTeam(teamSetup team) {
        String aeroIni = team.getCarFolder() + "\\data\\aero.ini";
        String carIni = team.getCarFolder() + "\\data\\car.ini";
        String copyToIni = new JFileChooser().getFileSystemView().getDefaultDirectory().toString() + "\\Ac Cardev save data\\" + App.loadedProfile + "\\copyToIni.ini";
        System.out.println("Saving downforce & drag to team");
        System.out.println("Saving team to json file");
        aeroParts = filterAeroStats(aeroParts);
        fileWriter.writeAeroStats(aeroIni, copyToIni, aeroParts);
        fileWriter.writeCarStats(carIni, copyToIni, car);
        // aeroParts = convertAeroStats(aeroParts);
    }

    private ArrayList<aeroPart> filterAeroStats(ArrayList<aeroPart> parts) {
        for (int i = 0; i < parts.size(); i++) {
            if (parts.get(i).downforce > 0.01f) {
                parts.get(i).downforce = parts.get(i).downforce / season.baseline;
                parts.get(i).downforce = Math.round(parts.get(i).downforce * 100000.0f) / 100000.0f;
                System.out.println(parts.get(i).downforce);
            } else {
                parts.get(i).downforce = 0.0f;
                System.out.println(parts.get(i).downforce);
            }
            if (parts.get(i).drag != 0.0f) {
                parts.get(i).drag = season.baseline - (parts.get(i).drag - season.baseline);
                parts.get(i).drag = parts.get(i).drag / season.baseline;
                parts.get(i).drag = Math.round(parts.get(i).drag * 100000.0f) / 100000.0f;
                System.out.println(parts.get(i).drag);
                
            } else {
                parts.get(i).drag = 0.0f;
                System.out.println(parts.get(i).drag);
            }

        }
        return parts;
    }
}
