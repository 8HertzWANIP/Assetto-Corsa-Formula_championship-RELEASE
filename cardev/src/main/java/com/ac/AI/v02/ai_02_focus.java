package com.ac.AI.v02;

import java.util.concurrent.ThreadLocalRandom;

import com.ac.AI.aiAPI;
import com.ac.fileparsing.jsonReader;
import com.ac.lib.aeroAPI;
import com.ac.lib.aeroPartInventory;
import com.ac.menus.facilities.teamFacilities;
import com.ac.seasons.newSeason.seasonSettings;
import com.ac.seasons.newSeason.teamSetup;

public class ai_02_focus {
    public teamSetup aiTeam = new teamSetup(null, null, null, null, null, 0, 0, 0, 0);
    public seasonSettings season = new seasonSettings(null, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, false, false, false, false, null, null);
    private aeroPartInventory[] inventory = null;
    private teamFacilities facilities = null;
    private int aiSpendLimit = 0;
    // AI will cycle through these values while upgrading over the season.
    private int[] aiFocusRate;
    // Slidervalue set for pre-season improvement focus
    private double sliderValue = 65;
    private int createNewPartAttempts = 3;
    private boolean privTestingAllowed;
    private int lastCreatedPartIndex;

    public void setSeason(seasonSettings season) {
        this.season = season;
    }

    public void setFocusRate(int[] aiFocusRate) {
        this.aiFocusRate = aiFocusRate;
    }

    public void setAiTeam(teamSetup targetTeam) {
        this.aiTeam = targetTeam;
    }

    public void setInventory(aeroPartInventory[] inventory) {
        this.inventory = inventory;
    }

    /** Loads all required variables for the various stages of the AI*/
    public void startAi() {
        facilities = jsonReader.parseFacilities(aiTeam);
        privTestingAllowed = season.regulations.privTesting;
        if (season.getCurrentRace() > 0)
            giveAiRaceRp();

        aiDirector("");
        aiAPI.saveFileData(aiTeam, inventory, facilities, season);
    }

    private void aiDirector(String target) {
        // value initialisation
        facilities.wTunnel.setCharges(season.getCurrentRace());
        facilities.wTunnel.chargesSpent = 0;
        createNewPartAttempts = 1;
        // Gets the slider value from the aifocusrate increasing its index after every race.
        if (season.getCurrentRace() > 0)
            sliderValue = aiFocusRate[aiAPI.getCycleIndex(aiFocusRate, season)];

        // AI director conditions
        if (target.equals("")) {
            // start pre-season setup
            if (season.getCurrentRace() == 0) {
                aiSpendLimit = (int) Math.round((float) aiTeam.getMoney() * 0.3f);
                target = "preS-createCar";
            } 
            // Start race session behavior
            else if (season.getCurrentRace() > 0) {
                aiSpendLimit = teamFacilities.returnTunnelCost("research", season, facilities) * facilities.wTunnel.level;
                inventory = aeroAPI.testPartsInTunnel(inventory, facilities, 0);
                target = "improveCar";
            }
        } else if (target.equals("testParts")) {
            if (aiTeam.getMoney() > aiSpendLimit && facilities.wTunnel.chargesSpent < facilities.wTunnel.charges) {
                target = "testPartsTunnel";
            } else {
                target = "";
            }
        }
        System.out.println("Team AI: [" + aiTeam.getTeamName() + "] stage: [" + target + "]");
        System.out.println("Team AI funds: [" + aiTeam.getMoney() + "]");
        System.out.println("Team AI RP: [" + facilities.resDev.getResearchPoints() + "]");
        // AI director actions.
        switch (target) {
            // Pre-season development
            case "preS-createCar":
                preSeasonCreateCar();
                aiDirector("preS-upgradeCar");
                break;
            case "preS-upgradeCar":
                preSeasonUpgradeParts();
                windTunnel("preS-checkParts");
                while (aiTeam.getMoney() > aiSpendLimit && facilities.wTunnel.chargesSpent < facilities.wTunnel.charges) {
                    windTunnel("genRP");
                }
                if (aiTeam.getMoney() > aiSpendLimit && privTestingAllowed) {
                    privTesting();
                }
                if (aiTeam.getMoney() > aiSpendLimit) {
                    startSeasonImprovement();
                }
                break;
            // Season development
            case "improveCar":
                startSeasonImprovement();
                windTunnel("checkParts");
                while (aiTeam.getMoney() > aiSpendLimit && facilities.wTunnel.chargesSpent < facilities.wTunnel.charges) {
                    windTunnel("genRP");
                }
                if (aiTeam.getMoney() > aiSpendLimit * 2 && privTestingAllowed) {
                    privTesting();
                }
                if (aiTeam.getMoney() > aiSpendLimit) {
                    startSeasonImprovement();
                }
                break;
            default:
                break;
        }
    }

    private void windTunnel(String target) {
        switch (target) {
            case "preS-checkParts":
                while (aeroAPI.countUntestedParts(inventory) > Math.floor(inventory.length / 2)
                && facilities.wTunnel.chargesSpent < facilities.wTunnel.charges
                && aiTeam.getMoney() > teamFacilities.returnTunnelCost("testParts", season, facilities)) {
                    windTunnel("testParts");
                }
                break;
            case "checkParts":
                if (aeroAPI.countUntestedParts(inventory) > 2)
                    windTunnel("testParts");
                break;

            case "testParts":
                if (aiTeam.getMoney() > teamFacilities.returnTunnelCost("testParts", season, facilities)) {
                    float failureChance = facilities.wTunnel.getFailureChance();
                    inventory = aeroAPI.testPartsInTunnel(inventory, facilities, failureChance);
                    aiTeam.setMoney(aiTeam.getMoney() - teamFacilities.returnTunnelCost("testParts", season, facilities));
                    facilities.wTunnel.chargesSpent++;
                    facilities.wTunnel.setCharges(season.getCurrentRace());
                }
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

    private void privTesting() {
        System.out.println("AI Starting private testing");
        int [] calcRP = teamFacilities.returnPrivTestingRP(aeroAPI.countUntestedParts(inventory), facilities, season);
        
        int costMultiplyer = 35000;
        int flatCosts = season.getTotalPrizePool() * costMultiplyer;
        int sessionCost = Math.round((float) flatCosts / season.getRaceCount());
        int rpReward = ThreadLocalRandom.current().nextInt(calcRP[0], calcRP[1]);
        rpReward = Math.round((float) rpReward * season.returnAIDifficultyValue(season.getDifficulty()));

        aiTeam.setMoney(aiTeam.getMoney() - sessionCost);

        facilities.resDev.addResearchPoints(rpReward);
        inventory = aeroAPI.testInstalledParts(inventory, facilities, 0);

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
                aeroPartInventory.part tempPart = aeroAPI.generateNewAeroPart(season, inventory, part.id, facilities.resDev.getLevel());
                aiTeam.setMoney(aiFunds);

                //Experience
                int experience = facilities.resDev.getExperience();
                facilities.resDev.setExperience(experience + aiAPI.giveResDevExperience("newPart", -1, inventory, facilities, season));
                if (facilities.resDev.getExperience() >= teamFacilities.resDevLevelExpValues(season)[facilities.resDev.getLevel() + 1]) {
                    System.out.println("R&D LEVEL UP: [" + facilities.resDev.getLevel() + "]");
                    facilities.resDev.setLevel(facilities.resDev.getLevel() + 1);
                }
                System.out.println("new experience [" + facilities.resDev.getExperience() + "]");
                if (tempPart.average > inventory[part.id].installed.average) {
                    inventory[part.id].installed = tempPart;
                }
            }
        } 
    }
    
    private void startSeasonImprovement() {
        int RP = facilities.resDev.getResearchPoints();
        System.out.println("SEASON IMPROVEMENT");
        System.out.println("Current RP: [" + RP + "]");
        int rpDiv = (Math.floorDiv(RP, 1500) - 1);
        if (rpDiv > 3)
            rpDiv = 3;
        if (rpDiv <= 0)
            rpDiv = 1;

        int rpBudget = Math.round((float) RP / rpDiv);


        while (RP > 0 && createNewPartAttempts > 0) {
            System.out.println("rpBudget: [" + rpBudget + "]");
            if (rpBudget > 1500 && aiTeam.getMoney() > aiSpendLimit) {
                int partIndex = aiAPI.partPicker("default-improve", inventory, facilities);
                System.out.println("Part picked: [" + partIndex + "]");
                int[] calcValues = aiAPI.aiCalcImprovementCosts(rpBudget, partIndex, inventory, season, aiTeam, facilities);
                System.out.println("improvementPoints: [" + calcValues[0] + "]");
                System.out.println("cost: [" + calcValues[1] + "]");
                System.out.println("rpCost: [" + calcValues[2] + "]");

                // Create new part if part improvement becomes too expensive.
                if (calcValues[0] < 5 && rpBudget > 6000 && aiTeam.getMoney() > aiSpendLimit * 5) {
                    partIndex = -1;
                }

                // Stop part improvement if spent RP will not improve the part
                if (partIndex != -1 && calcValues[0] == 0) {
                    break;
                }

                /* If a part is available to be improved and picked out, improve the part, subtract money and RP, and gain experience/levels
                 If no part is available the pickpart will return -1 and the create new part logic will run. */
                if (calcValues[1] < aiTeam.getMoney() && calcValues[0] > 0 && partIndex > -1) {
                    if (inventory[partIndex].installed.downforce == 0) {
                        sliderValue = 0;
                    }
                    if (inventory[partIndex].installed.drag == 0) {
                        sliderValue = 100;
                    }
                    inventory[partIndex].installed = aiAPI.aiImprovePart(calcValues[0], sliderValue, inventory, partIndex);
                    RP -= calcValues[2];
                    rpBudget -= calcValues[2];
                    aiTeam.subtractMoney(calcValues[1]);

                    //Experience
                    facilities.resDev.experience += aiAPI.giveResDevExperience("improvePart", calcValues[2], inventory, facilities, season);
                    System.out.println("experience gained [" +  aiAPI.giveResDevExperience("improvePart", calcValues[2], inventory, facilities, season) + "]");
                    System.out.println("new experience [" + facilities.resDev.getExperience() + "]");
                    System.out.println("required experience [" + (teamFacilities.resDevLevelExpValues(season)[facilities.resDev.getLevel() + 1] - facilities.resDev.getExperience()) + "]");
                    if (facilities.resDev.getExperience() >= teamFacilities.resDevLevelExpValues(season)[facilities.resDev.getLevel() + 1]) {
                        System.out.println("R&D LEVEL UP: [" + facilities.resDev.getLevel() + "]");
                        facilities.resDev.setLevel(facilities.resDev.getLevel() + 1);
                    }
                // If no available part is found to upgrade, create a new part then immediatly improve that part
                } else if (partIndex == -1) {
                    if (!aiCreateNewPart()) {
                        break;
                    } else {
                        partIndex = lastCreatedPartIndex;
                        System.out.println("Part picked: [" + partIndex + "]");
                        calcValues = aiAPI.aiCalcImprovementCosts(rpBudget, partIndex, inventory, season, aiTeam, facilities);
                        System.out.println("improvementPoints: [" + calcValues[0] + "]");
                        System.out.println("cost: [" + calcValues[1] + "]");
                        System.out.println("rpCost: [" + calcValues[2] + "]");
        
                        // Stop part improvement if spent RP will not improve the part
                        if (partIndex != -1 && calcValues[0] == 0) {
                            break;
                        }
        
                        /* If a part is available to be improved and picked out, improve the part, subtract money and RP, and gain experience/levels
                         If no part is available the pickpart will return -1 and the create new part logic will run. */
                        if (calcValues[1] < aiTeam.getMoney() && calcValues[0] > 0) {
                            if (inventory[partIndex].installed.downforce == 0) {
                                sliderValue = 0;
                            }
                            if (inventory[partIndex].installed.drag == 0) {
                                sliderValue = 100;
                            }
                            inventory[partIndex].installed = aiAPI.aiImprovePart(calcValues[0], sliderValue, inventory, partIndex);
                            RP -= calcValues[2];
                            rpBudget -= calcValues[2];
                            aiTeam.subtractMoney(calcValues[1]);
        
                            //Experience
                            facilities.resDev.experience += aiAPI.giveResDevExperience("improvePart", calcValues[2], inventory, facilities, season);
                            System.out.println("experience gained [" +  aiAPI.giveResDevExperience("improvePart", calcValues[2], inventory, facilities, season) + "]");
                            System.out.println("new experience [" + facilities.resDev.getExperience() + "]");
                            System.out.println("required experience [" + (teamFacilities.resDevLevelExpValues(season)[facilities.resDev.getLevel() + 1] - facilities.resDev.getExperience()) + "]");
                            if (facilities.resDev.getExperience() >= teamFacilities.resDevLevelExpValues(season)[facilities.resDev.getLevel() + 1]) {
                                System.out.println("R&D LEVEL UP: [" + facilities.resDev.getLevel() + "]");
                                facilities.resDev.setLevel(facilities.resDev.getLevel() + 1);
                            }
                        }
                    }
                } else
                    break;
            } else
                break;
        }
        if (facilities.resDev.getResearchPoints() != RP){
            facilities.resDev.setResearchPoints(RP);
        }
    }

    /** Attempts to create a new part for the AI.
     * @return @true a new part has been created and money has been subtracted from ai, return true;
     *   @false there is no money for a new part;
     */
    private boolean aiCreateNewPart() {
        System.out.println("AI Create new part");
        int partIndex = aiAPI.partPicker("average-newPart", inventory, facilities);
        aeroPartInventory.part tempPart;
        // Money & part creation
        int cost = Math.round(((float) season.totalPrizePool * 24000) / inventory.length);
        int aiFunds = aiTeam.getMoney() - cost;
        if (aiFunds < 0 || partIndex < 0) {
            return false;
        }
        // Create new part
        tempPart = aeroAPI.generateNewAeroPart(season, inventory, partIndex, facilities.resDev.getLevel());
        aiTeam.setMoney(aiFunds);

        // Add AI experience.
        int experience = facilities.resDev.getExperience();
        facilities.resDev.setExperience(experience + aiAPI.giveResDevExperience("newPart", -1, inventory, facilities, season));
        if (facilities.resDev.getExperience() >= teamFacilities.resDevLevelExpValues(season)[facilities.resDev.getLevel() + 1]) {
            System.out.println("R&D LEVEL UP: [" + facilities.resDev.getLevel() + "]");
            facilities.resDev.setLevel(facilities.resDev.getLevel() + 1);
        }
        // Check if part is comparable to the part its replacing
        int carAverage = aeroAPI.getCarAeroRating(inventory);
        System.out.println("carAverage: [" + carAverage + "]");
        System.out.println("getPartTotalAverage: [" + aeroAPI.getPartTotalAverage(tempPart, true) + "]");
        if (aeroAPI.getPartTotalAverage(tempPart, true) > aeroAPI.getPartTotalAverage(inventory[partIndex].installed, true)) {
            inventory[partIndex].installed = tempPart;
        }
        createNewPartAttempts--;
        lastCreatedPartIndex = partIndex;
        return true;
    }

    private void giveAiRaceRp() {
        int RP = Math.round((float) teamFacilities.giveFacilityRP("resDev", facilities, season) * season.returnAIDifficultyValue(season.getDifficulty()));
        facilities.resDev.setResearchPoints(facilities.resDev.getResearchPoints() + RP);
    }
}
