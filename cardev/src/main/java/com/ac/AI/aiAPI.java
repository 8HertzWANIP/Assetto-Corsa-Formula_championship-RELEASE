package com.ac.AI;

import java.io.File;

import javax.swing.JFileChooser;

import com.ac.App;
import com.ac.fileparsing.fileReader;
import com.ac.fileparsing.fileWriter;
import com.ac.fileparsing.jsonWriter;
import com.ac.lib.aeroAPI;
import com.ac.lib.aeroPartInventory;
import com.ac.menus.facilities.teamFacilities;
import com.ac.seasons.newSeason.seasonSettings;
import com.ac.seasons.newSeason.teamSetup;

public interface aiAPI {
    public static int getCycleIndex(int[] cycle, seasonSettings season) {
        int length = cycle.length;
        int currentRace = season.getCurrentRace();
        while(currentRace >= length) {
            currentRace -= length;
        }
        return currentRace;
    }
    
    /** Creates "facilities.json" and "aero parts.json" files if they don't exist
     * @param targetTeam
     * @param season
     */
    public static void doFacilitiesExist(teamSetup targetTeam, seasonSettings season) {
        File facilitiesJson = new File(new JFileChooser().getFileSystemView().getDefaultDirectory().toString() + "\\Ac Cardev save data\\" + App.loadedProfile + "\\" + targetTeam.teamName + "\\facilities.json");
        if (!facilitiesJson.exists()){
            jsonWriter.createFacilityJson(targetTeam, season);
        }
        File inventoryJson = new File(new JFileChooser().getFileSystemView().getDefaultDirectory().toString() + "\\Ac Cardev save data\\" + App.loadedProfile + "\\" + targetTeam.teamName + "\\part Inventory\\aero parts.json");
        if (!inventoryJson.exists()){
            fileReader.loadAeroIniParts(targetTeam, season);
        }
    }

    public static int giveResDevExperience(String xpOption, int RP, aeroPartInventory[] inventory, teamFacilities facilities, seasonSettings season) {
        int experience = 0;
        switch (xpOption) {
            case "improvePart":
                experience = Math.round(RP / inventory.length);
                experience = Math.round((experience / 10) * season.getRaceCount());
                break;

            case "newPart":
                if (facilities.resDev.getLevel() + 1 < teamFacilities.resDevLevelExpValues(season).length) {
                    experience = (teamFacilities.resDevLevelExpValues(season)[facilities.resDev.getLevel() + 1] - teamFacilities.resDevLevelExpValues(season)[facilities.resDev.getLevel()]) / (inventory.length * 2);
                } else {
                    experience = (teamFacilities.resDevLevelExpValues(season)[facilities.resDev.getLevel() - 1] - teamFacilities.resDevLevelExpValues(season)[facilities.resDev.getLevel() - 2]) / (inventory.length * 2);
                }
                experience = Math.round((experience / 10) * season.getRaceCount());
                break;
        
            default:
                break;
        }
        System.out.println("experience [" + experience + "]");
        return experience;
    }

    public static int[] aiCalcImprovementCosts(int RP, int selectedPartIndex, aeroPartInventory[] inventory, seasonSettings season, teamSetup aiTeam, teamFacilities facilities) {
        int[] returnedValues = {0, 0, 0};
        if (selectedPartIndex > -1) {

            int rpCostFinal = 0;
            int totalCostFinal = 0;
            float rpCost = 0;
            float totalCost = 0;
            aeroPartInventory.part targetPart = null;
            targetPart = inventory[selectedPartIndex].installed;
            // taking season total prizepool to balance the cost out no matter the prize pool you select.
            int initialCost = (season.getTotalPrizePool() * targetPart.improvementSpent);
            int improvementPoints = 0;
            rpCost = (season.raceCount * targetPart.improvementSpent);
            
            if (targetPart.improvementSpent == 0) {
                rpCost = season.raceCount * 100;
                initialCost = season.getTotalPrizePool() * 100;
            }
            for (int i = 1; RP > rpCost && aiTeam.getMoney() > totalCostFinal; i++) {
                rpCostFinal = Math.round(rpCost);
                totalCostFinal = Math.round(totalCost);
                float modifyer = i;
                improvementPoints = i - 1 - targetPart.improvementSpent;
                rpCost += rpCost * (modifyer / 1700);
                totalCost += initialCost * (modifyer / 80);
                if (i > targetPart.max_improvement) {
                    System.out.println("improvementPoints check [" + improvementPoints + "]");
                    break;
                }
            }
            if (improvementPoints < 0) {
                System.out.println("improvementPoints ERROR:UNDERFLOW [" + improvementPoints + "]");
                improvementPoints = 0;
            }
            System.out.println("selectedPartIndex [" + selectedPartIndex + "]");
            System.out.println("totalCost [" + totalCostFinal + "]");
            System.out.println("rpCost [" + rpCostFinal + "]");
            System.out.println("Rotation [" + improvementPoints + "]");
            
            returnedValues[0] = improvementPoints;
            returnedValues[2] = rpCostFinal;
            if (totalCostFinal <= aiTeam.getMoney() && rpCostFinal <= facilities.resDev.getResearchPoints()) {
                returnedValues[1] = totalCostFinal;
            }
        }
        return returnedValues;
    }

    public static aeroPartInventory.part aiImprovePart(int improvementPoints, double sliderValue, aeroPartInventory[] inventory, int selectedPartIndex) {
        aeroPartInventory.part targetPart = null;
        targetPart = inventory[selectedPartIndex].installed;
        int downforcePoints = Math.round(improvementPoints * (float) (sliderValue / 100));
        int dragPoints = Math.round(improvementPoints * (float) (1 - (sliderValue / 100)));
        System.out.println("sliderValue [" + sliderValue + "]");
        System.out.println("downforcePoints [" + downforcePoints + "]");
        System.out.println("dragPoints [" + dragPoints + "]");
        targetPart.downforce += downforcePoints;
        targetPart.drag += dragPoints;
        targetPart.improvementSpent += improvementPoints;
        targetPart.average = aeroAPI.returnPartAverage(targetPart);

        return targetPart;
    }

    /** Picks a part to improve. returns -1 when no part is found.
     * @param behavior
     * @param inventory
     * @return (int) targetPartIndex
     */
    public static int partPicker(String behavior, aeroPartInventory[] inventory, teamFacilities facilities) { 
        int targetPartIndex = 0;
        aeroPartInventory.part targetPart;
        aeroPartInventory.part checkingPart;
        int firstPartIndex = -1;
        switch (behavior) {
            case "default-improve":
            // Picks the part with the lowest part average
                targetPartIndex = 0;
                targetPart = inventory[0].installed;
                // Start with the first valid part with available improvement points
                for (int i = 0; i < inventory.length; i++) {
                    System.out.println("Part checking 1: [" + inventory[i].part_name + "] - condition: [" + (inventory[i].installed.max_improvement - inventory[i].installed.improvementSpent) +"]");
                    if ((inventory[i].installed.max_improvement - inventory[i].installed.improvementSpent) > 0 && !aeroAPI.isPartEmpty(targetPart)) {
                        System.out.println("Part picked: [" + i + "]");
                        firstPartIndex = i;
                        break;
                    }
                }
                if (firstPartIndex != -1) {
                    targetPart = inventory[firstPartIndex].installed;
                    for (int i = firstPartIndex; i < inventory.length; i++) {
                        checkingPart = inventory[i].installed;
                        System.out.println("Part checking 2: [" + inventory[i].part_name + "] - condition: [" + (inventory[i].installed.max_improvement - inventory[i].installed.improvementSpent) +"]");
                        if (checkingPart.average < targetPart.average
                        && checkingPart.improvementSpent < checkingPart.max_improvement
                        && !aeroAPI.isPartEmpty(checkingPart)
                        && (checkingPart.max_improvement - checkingPart.improvementSpent) > 0) {
                            System.out.println("Part Selected");
                            targetPart = checkingPart;
                            targetPartIndex = i;
                            break;
                        }
                    }
                } else {
                    System.out.println("No availbale part has been found returning [-1]");
                    return -1;
                }
                return targetPartIndex;
            case "average-newPart":
            // Picks the part with the lowest part average
                targetPartIndex = -1;
                targetPart = inventory[0].installed;
                for (int i = 0; i < inventory.length; i++) {
                    checkingPart = inventory[i].installed;
                    System.out.println("Part level: [" + checkingPart.partLevel + "] resdev: [" + facilities.resDev.getLevel() + "]");
                    if (aeroAPI.getPartTotalAverage(checkingPart, true) > aeroAPI.getPartTotalAverage(targetPart, true)
                    && !aeroAPI.isPartEmpty(checkingPart)
                    && checkingPart.partLevel < facilities.resDev.getLevel()) {
                        System.out.println("Part Selected: [" + inventory[i].part_name + "]");
                        targetPart = checkingPart;
                        targetPartIndex = i;
                    }
                }
                return targetPartIndex;

            default:
                break;
        }

       return 0; 
    }

    public static void saveFileData(teamSetup aiTeam, aeroPartInventory[] inventory, teamFacilities facilities, seasonSettings season) {
        jsonWriter.saveTeam(aiTeam);
        jsonWriter.savePartsToJson(inventory, aiTeam);
        jsonWriter.saveFacilitiesToJson(facilities, aiTeam);
        fileWriter.writeNewAeroStats(aiTeam, inventory, season);
    }
}
