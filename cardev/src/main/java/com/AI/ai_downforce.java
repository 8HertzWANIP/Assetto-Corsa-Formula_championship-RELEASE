package com.AI;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.JFileChooser;

import com.ac.backend.jsonReader;
import com.ac.backend.aeroPart;
import com.ac.App;
import com.ac.backend.aeroAPI;
import com.ac.backend.fileReader;
import com.ac.backend.fileWriter;
import com.ac.backend.jsonWriter;
import com.ac.backend.seasonSettings;
import com.ac.backend.teamSetup;

public class ai_downforce extends aiController {
    jsonReader jReader = new jsonReader();
    jsonWriter jWriter = new jsonWriter();
    fileReader fReader = new fileReader();
    teamSetup targetTeam = new teamSetup(null, null, null, null, null, 0, 0, 0, 0);
    seasonSettings season = new seasonSettings(null, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, false, false, false, false);

    ArrayList<aeroPart> aeroParts = new ArrayList<aeroPart>();
    
    float dForceCost;
    float dragCost;
    float maxDf;
    float maxDr;
    float minDf;
    float minDr;
    public int fwAngle;
    public int rwAngle;

    Double improveDownforceFunds;
    Double improveDragFunds;

    Double loadedDownforce;
    Double loadedDrag;

    Double dfIncrease = 0d;
    Double dragDecrease = 0d;

    int partInvestment;
    // Investment focus during investment. int corresponds to the slidervalue of drag / downforce
    int aeroFocus = 50;
    // AI will cycle through these values while upgrading over the season.
    int[] aiFocusRate = {50, 60, 65, 75};

    int aiSpendLimit = 250000;
    float aeroIncreaseRate = 1.0f;
    float dragIncreaseRate = 1.0f;
    
    public void setSeason(seasonSettings season) {
        this.season = season;
    }

    public void setTargetTeam(teamSetup targetTeam) {
        this.targetTeam = targetTeam;
    }

    public void startAi() {
        // Extra funds to add to part construction.
        int extraFunds = 0;
        System.out.println("start AI team [" + targetTeam.getTeamName() + "]");

        // Clear any previous loaded aerodynamic parts.
        aeroParts.clear();

        // Load in team cars aero parts
        fileReader.setAeroParts(new ArrayList<aeroPart>());
        fileReader.fillAeroParts(targetTeam.getCarFolder() + "\\data\\aero.ini");
        aeroParts = fReader.getAeroParts();
        System.out.println("Car folder loaded");
        System.out.println("Loaded aeroparts: [" + aeroParts.size() + "]");

        // Set cost values and the amount to invest in new parts
        dForceCost = Math.round(2600000 / aeroParts.size());
        dragCost =  Math.round(2600000 / aeroParts.size());
        partInvestment = Math.round(targetTeam.money * 0.9f);
        int teamAvailableFunds = partInvestment;
        System.out.println("partInvestment: [" + partInvestment + "]");
        System.out.println("partInvestment 2: [" + (dForceCost + dragCost) + "]");
        int amountToUpgrade = Math.round(partInvestment / (dForceCost + dragCost));

        // Convert loaded aero part values
        if (aeroAPI.getCarAeroRating(aeroParts) > 10000) {
            aeroParts = aeroAPI.filterAeroStats(aeroParts, season);
        }
        else if(aeroAPI.getCarAeroRating(aeroParts) < 10) {
            aeroParts = aeroAPI.convertAeroStats(aeroParts, season);
        }

        int carRating = aeroAPI.getCarAeroRating(aeroParts);
        ArrayList<Integer> partsToUpgrade = pickPartsToUpgrade(carRating);
        
        System.out.println("Start AI 0 aeroparts upgrade");
        System.out.println("Amount to upgrade: [" + amountToUpgrade + "]");
        // Upgrade picked parts to upgrade.
        // the teamAvailableFunds value is a value which gets decreased towards 0 with every upgrade untill there isn't enough funds left.
        if (amountToUpgrade > aeroParts.size() && teamAvailableFunds > aiSpendLimit) {
            // Spread funds per part evenly across all parts
            int fundsPerPart = Math.round(teamAvailableFunds / amountToUpgrade) * Math.round(amountToUpgrade / aeroParts.size());
            for (int j = 0; j < partsToUpgrade.size(); j++) {
                if (fundsPerPart + extraFunds < targetTeam.money) {
                
                    fundsPerPart = extraFunds + fundsPerPart;
                    boolean partSuccess = improvePart(fundsPerPart, partsToUpgrade.get(j), targetTeam);
                    
                    if (partSuccess && teamAvailableFunds >= (fundsPerPart + extraFunds)) {
                        teamAvailableFunds -= (fundsPerPart + extraFunds);
                        fundsPerPart -= extraFunds;
                        extraFunds = 0;
                        System.out.println("SUCCESS Improve part Funds: [" + teamAvailableFunds + "]");
                        System.out.println("fundsPerPart: [" + fundsPerPart + "]");
                        System.out.println("extraFunds: [" + extraFunds + "]");
                    } else {
                        extraFunds += (extraFunds + fundsPerPart);
                        System.out.println("FAILED Improve part Funds: [" + teamAvailableFunds + "]");
                        System.out.println("fundsPerPart: [" + fundsPerPart + "]");
                        System.out.println("extraFunds: [" + extraFunds + "]");
                    }
                }
            }
            targetTeam.money = Math.round(targetTeam.money * 0.1f) + teamAvailableFunds;
            jWriter.saveTeam(targetTeam);
        }
    }
    
    private ArrayList<Integer> pickPartsToUpgrade(int rating) {
        ArrayList<Integer> pickedParts = new ArrayList<Integer>();

        for (int i = 0; i < aeroParts.size(); i++) {
            if (aeroParts.get(i).downforce + aeroParts.get(i).drag <= rating) {
                System.out.println("Add part team index: [" + i + "]");
                pickedParts.add(i);
            }
            else if (aeroParts.get(i).drag == 0) {
                if (aeroParts.get(i).downforce < rating / 2) {
                    System.out.println("Add part team index: [" + i + "]");
                    pickedParts.add(i);
                }
            }
            else if (aeroParts.get(i).downforce == 0) {
                if (aeroParts.get(i).drag < rating / 2) {
                    System.out.println("Add part team index: [" + i + "]");
                    pickedParts.add(i);
                }
            }

            if (aeroParts.get(i).getlabel().equals("Front Wing") || aeroParts.get(i).getlabel().equals("FW")) {
                aeroParts.get(i).setAngle(fwAngle);
            } 
            else if (aeroParts.get(i).getlabel().equals("Rear Wing") || aeroParts.get(i).getlabel().equals("RW")) {
                aeroParts.get(i).setAngle(rwAngle);
            }
        }
        // If all parts are equal, pick a random part.
        if (pickedParts.size() == 0) {
            pickedParts.add(ThreadLocalRandom.current().nextInt(aeroParts.size()));
        }
        return pickedParts;
    }

    boolean improvePart(int improvePartFunds, int selectedPartIndex, teamSetup team) {
        System.out.println("Improve part index: [" + selectedPartIndex + "]");
        System.out.println("Improve part funds: [" + improvePartFunds + "]");

        // Gets the slider value from the aifocusrate increasing its index after every race.
        double sliderValue = aiFocusRate[aiAPI.getCycleIndex(aiFocusRate, season)];
        
        if (aeroParts.get(selectedPartIndex).downforce == 0) {
            sliderValue = 0;
        }
        if (aeroParts.get(selectedPartIndex).drag == 0) {
            sliderValue = 100;
        }

        double dfModifyer = (sliderValue / 100);
        double dragModifyer = (100 - sliderValue) / 100;
        improveDownforceFunds = improvePartFunds * dfModifyer;
        improveDragFunds = improvePartFunds * dragModifyer;
        loadedDownforce =  (double) aeroParts.get(selectedPartIndex).getDownforce();
        loadedDrag =  (double) aeroParts.get(selectedPartIndex).getDrag();
        dfIncrease = 0d;
        dragDecrease = 0d;

        System.out.println(sliderValue);
        System.out.println("Improve part loadedDownforce: [" + loadedDownforce + "]");
        System.out.println("Improve part loadedDrag: [" + loadedDrag + "]");
        
        if (loadedDownforce > 0) {
            System.out.println("calculate AI downforce");
            dfIncrease = aeroAPI.calculateAiDownforce(improveDownforceFunds, partInvestment, dForceCost, dfModifyer, team, season, aeroParts.get(selectedPartIndex), dfIncrease);
        }
        if (loadedDrag > 0) {
            System.out.println("Calculate AI drag");
            dragDecrease = aeroAPI.calculateAiDrag(improveDragFunds, partInvestment, dragCost, dragModifyer, team, season, aeroParts.get(selectedPartIndex), dragDecrease);
        }

        if (dfIncrease + dragDecrease > 20) {
            String aeroIni = team.carFolder + "\\data\\aero.ini";
            String copyToIni = new JFileChooser().getFileSystemView().getDefaultDirectory().toString() + "\\Ac Cardev save data\\" + App.loadedProfile + "\\copyToIni.ini";
            System.out.println("Saving downforce & drag to team");
            aeroParts.get(selectedPartIndex).downforce = Math.round(aeroParts.get(selectedPartIndex).downforce + dfIncrease);
            aeroParts.get(selectedPartIndex).drag = Math.round(aeroParts.get(selectedPartIndex).drag + dragDecrease);
            System.out.println("Saving team to json file");
            aeroParts = aeroAPI.filterAeroStats(aeroParts, season);
            fileWriter.writeAeroStats(aeroIni, copyToIni, aeroParts);
            aeroParts = aeroAPI.convertAeroStats(aeroParts, season);
            return true;
        } else {
            return false;
        }
    }
}
