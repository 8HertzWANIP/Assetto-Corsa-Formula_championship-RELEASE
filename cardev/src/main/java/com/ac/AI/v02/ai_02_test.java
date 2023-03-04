package com.ac.AI.v02;

import com.ac.AI.aiAPI;
import com.ac.fileparsing.jsonReader;
import com.ac.lib.aeroAPI;
import com.ac.lib.aeroPartInventory;
import com.ac.menus.facilities.teamFacilities;
import com.ac.seasons.newSeason.seasonSettings;
import com.ac.seasons.newSeason.teamSetup;

public class ai_02_test {
    public teamSetup aiTeam = new teamSetup(null, null, null, null, null, 0, 0, 0, 0);
    public seasonSettings season = new seasonSettings(null, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, false, false, false, false, null, null);
    private aeroPartInventory[] inventory = null;
    private teamFacilities facilities = null;
    private int aiSpendLimit = 0;

    public void setSeason(seasonSettings season) {
        this.season = season;
    }

    public void setAiTeam(teamSetup targetTeam) {
        this.aiTeam = targetTeam;
    }

    /** Loads all required variables for the various stages of the AI*/
    public void startAi() {
        aiAPI.doFacilitiesExist(aiTeam, season);
        facilities = jsonReader.parseFacilities(aiTeam);
        inventory = jsonReader.parseAeroParts(aiTeam);
        if (season.getCurrentRace() > 0)
            giveAiRp();
        aiDirector("");
        aiAPI.saveFileData(aiTeam, inventory, facilities, season);
    }

    private void aiDirector(String target) {
        // value initialisation
        facilities.wTunnel.setCharges(season.getCurrentRace());

        // AI director conditions
        if (target.equals("")) {
            // start pre-season setup
            if (season.getCurrentRace() == 0) {
                aiSpendLimit = teamFacilities.returnTunnelCost("research", season, facilities) * 4;
                target = "preS-createCar";
            } 
            // Start race session behavior
            else if (season.getCurrentRace() > 0) {
                aiSpendLimit = teamFacilities.returnTunnelCost("research", season, facilities) * facilities.wTunnel.level;
                inventory = aeroAPI.testPartsInTunnel(inventory, facilities, 0);
                target = "improveCar";
            }
        } else if (target.equals("testParts")) {
            //TODO check for season privtesting allowed
            if (aiTeam.getMoney() > aiSpendLimit && facilities.wTunnel.chargesSpent < facilities.wTunnel.charges) {
                target = "testPartsTunnel";
            } else {
                target = "";
            }
        }

        System.out.println("Team AI: [" + aiTeam.getTeamName() + "] stage: [" + target + "]");
        // AI director actions.
        switch (target) {
            case "preS-createCar":
                preSeasonCreateCar();
                aiDirector("preS-upgradeCar");
                break;
            case "preS-upgradeCar":
                preSeasonUpgradeParts();
                windTunnel("checkParts");
                //TODO check for season privtesting allowed
                while (aiTeam.getMoney() > aiSpendLimit && facilities.wTunnel.chargesSpent < facilities.wTunnel.charges) {
                    windTunnel("genRP");
                }
                if (aiTeam.getMoney() > aiSpendLimit) {
                    startSeasonImprovement();
                }
                break;
            case "improveCar":
                startSeasonImprovement();
                windTunnel("checkParts");
                //TODO check for season privtesting allowed
                while (aiTeam.getMoney() > aiSpendLimit && facilities.wTunnel.chargesSpent < facilities.wTunnel.charges) {
                    windTunnel("genRP");
                }
                if (aiTeam.getMoney() > aiSpendLimit) {
                    startSeasonImprovement();
                }
                break;
            case "testPartsTrack":
                break;
            case "testPartsTunnel":
                windTunnel("testParts");
                break;
            default:
                break;
        }
    }

    private void windTunnel(String target) {
        switch (target) {
            case "checkParts":
                for (aeroPartInventory part : inventory) {
                    if (part.installed.improvement_hidden || part.installed.stats_hidden) {
                        aiDirector("testParts");
                        break;
                    }
                }
                break;

            case "testParts":
                float failureChance = facilities.wTunnel.getFailureChance();
                inventory = aeroAPI.testPartsInTunnel(inventory, facilities, failureChance);
                aiTeam.setMoney(aiTeam.getMoney() - teamFacilities.returnTunnelCost("testParts", season, facilities));
                facilities.wTunnel.chargesSpent++;
                facilities.wTunnel.setCharges(season.getCurrentRace());
                
                break;

            case "genRP":
                facilities.resDev.setResearchPoints(facilities.resDev.getResearchPoints() + (Math.round((float) teamFacilities.returnTunnelCost("researchRP", season, facilities) * season.returnAIDifficultyValue(season.getDifficulty()))));
                aiTeam.setMoney(aiTeam.getMoney() - teamFacilities.returnTunnelCost("research", season, facilities));
                facilities.wTunnel.chargesSpent++;
                facilities.wTunnel.setCharges(season.getCurrentRace());
                break;
        
            default:
                break;
        }
        
    }

    /** Create all parts for the car and install them. */
    private void preSeasonCreateCar() {
        // Loop through all inventory parts and create a new part.
        for (aeroPartInventory part : inventory) {
            // Money & part creation
            int cost = Math.round(((float) season.totalPrizePool * 24000) / inventory.length);
            int aiFunds = aiTeam.getMoney() - cost;
            if (aiFunds < 0) {
                break;
            }
            if (!aeroAPI.isPartEmpty(inventory[part.id].installed)) {
                inventory[part.id].installed = aeroAPI.generateNewAeroPart(season, inventory, part.id, facilities.resDev.getLevel());
                aiTeam.setMoney(aiFunds);

                //Experience
                int experience = facilities.resDev.getExperience();
                facilities.resDev.setExperience(experience + aiAPI.giveResDevExperience("newPart", -1, inventory, facilities, season));
                if (facilities.resDev.getExperience() >= teamFacilities.resDevLevelExpValues(season)[facilities.resDev.getLevel() + 1]) {
                    System.out.println("R&D LEVEL UP: [" + facilities.resDev.getLevel() + "]");
                    facilities.resDev.setLevel(facilities.resDev.getLevel() + 1);
                }
                System.out.println("new experience [" + facilities.resDev.getExperience() + "]");
            }
        } 
    }

    private void preSeasonUpgradeParts() {
        // Loop through all inventory parts again and replace parts with a lower part level than resdev level.
        for (aeroPartInventory part : inventory) {
            // Money & part creation
            int cost = Math.round(((float) season.totalPrizePool * 24000) / inventory.length);
            int aiFunds = aiTeam.getMoney() - cost;
            if (aiFunds < 0) {
                break;
            }
            if (!aeroAPI.isPartEmpty(inventory[part.id].installed) && part.installed.partLevel < facilities.resDev.getLevel()) {
                inventory[part.id].installed = aeroAPI.generateNewAeroPart(season, inventory, part.id, facilities.resDev.getLevel());
                aiTeam.setMoney(aiFunds);

                //Experience
                int experience = facilities.resDev.getExperience();
                facilities.resDev.setExperience(experience + aiAPI.giveResDevExperience("newPart", -1, inventory, facilities, season));
                if (facilities.resDev.getExperience() >= teamFacilities.resDevLevelExpValues(season)[facilities.resDev.getLevel() + 1]) {
                    System.out.println("R&D LEVEL UP: [" + facilities.resDev.getLevel() + "]");
                    facilities.resDev.setLevel(facilities.resDev.getLevel() + 1);
                }
                System.out.println("new experience [" + facilities.resDev.getExperience() + "]");
            }
        } 
    }
    
    private void startSeasonImprovement() {
        int RP = facilities.resDev.getResearchPoints();
        while (RP > 0) {
            System.out.println("Current RP: [" + RP + "]");
            if (RP > 1000 && aiTeam.getMoney() > aiSpendLimit) {
                int partIndex = aiAPI.partPicker("default-improve", inventory, facilities);
                int[] calcValues = aiAPI.aiCalcImprovementCosts(RP, partIndex, inventory, season, aiTeam, facilities);
                System.out.println("improvementPoints: [" + calcValues[0] + "]");
                System.out.println("cost: [" + calcValues[1] + "]");
                System.out.println("rpCost: [" + calcValues[2] + "]");

                if (partIndex != -1 && calcValues[0] == 0) {
                    break;
                }

                /* If a part is available to be improved and picked out, improve the part, subtract money and RP, and gain experience/levels
                 If no part is available the pickpart will return -1 and the create new part logic will run. */
                if (calcValues[1] < aiTeam.getMoney() && calcValues[0] > 0) {
                    inventory[partIndex].installed = aiAPI.aiImprovePart(calcValues[0], 0.5d, inventory, partIndex);
                    RP -= calcValues[2];

                    //Experience
                    facilities.resDev.experience += aiAPI.giveResDevExperience("improvePart", calcValues[2], inventory, facilities, season);
                    System.out.println("experience gained [" +  aiAPI.giveResDevExperience("improvePart", calcValues[2], inventory, facilities, season) + "]");
                    System.out.println("new experience [" + facilities.resDev.getExperience() + "]");
                    System.out.println("required experience [" + (teamFacilities.resDevLevelExpValues(season)[facilities.resDev.getLevel() + 1] - facilities.resDev.getExperience()) + "]");
                    if (facilities.resDev.getExperience() >= teamFacilities.resDevLevelExpValues(season)[facilities.resDev.getLevel() + 1]) {
                        System.out.println("R&D LEVEL UP: [" + facilities.resDev.getLevel() + "]");
                        facilities.resDev.setLevel(facilities.resDev.getLevel() + 1);
                    }
                } else if (partIndex == -1) {
                    partIndex = aiAPI.partPicker("average-newPart", inventory, facilities);
                    // Money & part creation
                    int cost = Math.round(((float) season.totalPrizePool * 24000) / inventory.length);
                    int aiFunds = aiTeam.getMoney() - cost;
                    if (aiFunds < 0) {
                        break;
                    }
                    inventory[partIndex].installed = aeroAPI.generateNewAeroPart(season, inventory, partIndex, facilities.resDev.getLevel());
                    aiTeam.setMoney(aiFunds);
        
                    int experience = facilities.resDev.getExperience();
                    facilities.resDev.setExperience(experience + aiAPI.giveResDevExperience("newPart", -1, inventory, facilities, season));
                    if (facilities.resDev.getExperience() >= teamFacilities.resDevLevelExpValues(season)[facilities.resDev.getLevel() + 1]) {
                        System.out.println("R&D LEVEL UP: [" + facilities.resDev.getLevel() + "]");
                        facilities.resDev.setLevel(facilities.resDev.getLevel() + 1);
                    }
                    break;
                } else
                    break;
            } else
                break;
        }
        if (facilities.resDev.getResearchPoints() != RP){
            facilities.resDev.setResearchPoints(RP);
        }
    }

    private void giveAiRp() {
        int RP = Math.round((float) teamFacilities.giveFacilityRP("resDev", facilities, season) * season.returnAIDifficultyValue(season.getDifficulty()));
        facilities.resDev.setResearchPoints(facilities.resDev.getResearchPoints() + RP);
    }
}
