package com.ac.menus.facilities;

import java.util.ArrayList;

import com.ac.seasons.newSeason.seasonSettings;
import com.ac.seasons.newSeason.teamSetup;

public class teamFacilities {
    public windTunnel wTunnel = new windTunnel();
    public researchDevelopment resDev = new researchDevelopment();    

    public teamFacilities(windTunnel wTunnel, researchDevelopment resDev) {
        this.wTunnel = wTunnel;
        this.resDev = resDev;
    }

    public static int[] tunnelLevelValues = {
        0,      // Level 0
        1,      // Level 1
        2,      // Level 2
        3,      // Level 3
        3,      // Level 4
        4,      // Level 5
        5,      // Level 6
        6,      // Level 7
        7,      // Level 8
        7,      // Level 9
        7       // Level 10
    };

    public static int[] tunnelCharges = {
        2,      // Level 0
        3,      // Level 1
        3,      // Level 2
        4,      // Level 3
        4,      // Level 4
        5,      // Level 5
        6,      // Level 6
        7,      // Level 7
        8,      // Level 8
        8,      // Level 9
        9       // Level 10
    };

    public static float[] tunnelRunningCost = {
        1.00f,      // Level 0
        1.05f,      // Level 1
        1.10f,      // Level 2
        1.16f,      // Level 3
        1.22f,      // Level 4
        1.30f,      // Level 5
        1.38f,      // Level 6
        1.48f,      // Level 7
        1.58f,      // Level 8
        1.70f,      // Level 9
        1.82f       // Level 10
    };

    public static float[] tunnelRpModifyer = {
        0.00f,      // Level 0
        0.05f,      // Level 1
        0.06f,      // Level 2
        0.08f,      // Level 3
        0.11f,      // Level 4
        0.14f,      // Level 5
        0.18f,      // Level 6
        0.23f,      // Level 7
        0.29f,      // Level 8
        0.36f,      // Level 9
        0.44f       // Level 10
    };

    public static float[] privTestingRpModifyer = {
        0.15f,      // Level 0
        0.15f,      // Level 1
        0.16f,      // Level 2
        0.18f,      // Level 3
        0.21f,      // Level 4
        0.24f,      // Level 5
        0.28f,      // Level 6
        0.33f,      // Level 7
        0.39f,      // Level 8
        0.46f,      // Level 9
        0.54f       // Level 10
    };

    public static int[] resDevRpValues = {
        0,          // Level 0
        403,        // Level 1
        481,        // Level 2
        616,        // Level 3
        882,        // Level 4
        1162,       // Level 5
        1648,       // Level 6
        2191,       // Level 7
        3301,       // Level 8
        4597,       // Level 9
        6307        // Level 10
    };

    public static int[] resDevRpValuesBackup = {
        0,          // Level 0
        806,        // Level 1
        962,        // Level 2
        1232,       // Level 3
        1664,       // Level 4
        2324,       // Level 5
        3296,       // Level 6
        4682,       // Level 7
        6602,       // Level 8
        9194,       // Level 9
        12614       // Level 10
    };

    public static Integer[] resDevLevelExpValues(seasonSettings season) {
        ArrayList<Integer> resDevLevelExpValues = new ArrayList<Integer>();
        int raceCount = 5 + season.getRaceCount();
        int startingXp = 4000;
        // Level + 2 to include level 0 and values for level 11 xp so there is no crashes
        int maxLevel = 10 + 2;
        int totalXp = 0;
        // resDevLevelExpValues.add(startingXp);
        for (int i = 0; i < maxLevel; i++) {
            totalXp += startingXp * raceCount / ((maxLevel + 1) - i);
            totalXp = totalXp / 100 * 100;
            resDevLevelExpValues.add(totalXp);
        }
        Integer[] returnInt = resDevLevelExpValues.toArray(new Integer[0]);
        return returnInt;
    }

    public static int returnTunnelCost(String target, seasonSettings season, teamFacilities fac) {
        int finalCost = 0;
        int costMultiplyer = 0;
        int flatCosts = 0;
        switch (target) {
            case "testParts":
                costMultiplyer = 12000;
                flatCosts = season.getTotalPrizePool() * costMultiplyer;
                finalCost = Math.round((float) flatCosts / season.getRaceCount());
                finalCost += Math.round((float) finalCost * tunnelRpModifyer[fac.wTunnel.level]);
                
                break;
    
            case "research":
                costMultiplyer = 10500;
                flatCosts = season.getTotalPrizePool() * costMultiplyer;
                finalCost = Math.round((float) flatCosts / season.getRaceCount());
                finalCost += Math.round((float) finalCost * tunnelRpModifyer[fac.wTunnel.level]);
                
                break;

            case "researchRP":
                finalCost = Math.round(giveFacilityRP("windTunnel", fac, season));
                break;
        
            case "levelup":
                float levelMultiplyer = 1.5f * fac.wTunnel.level;
                costMultiplyer = 540;
                flatCosts = season.getTotalPrizePool() * costMultiplyer;
                finalCost = Math.round((float) flatCosts * (levelMultiplyer * (fac.wTunnel.level + 1) * fac.wTunnel.level));
                break;
            default:
                break;
        }


        return finalCost;
    }

    public static teamFacilities convertResearchLevelToFacilities(teamSetup team, seasonSettings season) {
        int resDevExp = 0;
        int resDevLvl = 0;
        researchDevelopment resDev = new researchDevelopment();
        windTunnel wTunnel = new windTunnel();
        teamFacilities newTeamFacilities = new teamFacilities(wTunnel, resDev);
        // if (team.getResearchLevel() < 39) {
        //     resDevExp = 4000 + team.getResearchLevel() * 150;
        // } else if (team.getResearchLevel() < 49) {
        //     resDevExp = 4000 + team.getResearchLevel() * 220;
        // } else if (team.getResearchLevel() < 59) {
        //     resDevExp = 4000 + team.getResearchLevel() * 300;
        // } else {
        //     resDevExp = 4000 + team.getResearchLevel() * 390;
        // }
        // resDevLvl = getResDevLevelFromExp(resDevExp, season);
        resDevLvl = Math.floorDiv(team.getResearchLevel(), 10);
        int expReq = resDevLevelExpValues(season)[resDevLvl] - resDevLevelExpValues(season)[resDevLvl - 1];
        System.out.println(team.getTeamName() + " expReq: [" + expReq + "]");
        float lvlPercent = (long) (team.getResearchLevel() - (resDevLvl * 10)) / 10f;
        System.out.println(team.getTeamName() + " lvlPercent: [" + lvlPercent + "]");
        resDevLvl--;
        resDevExp = resDevLevelExpValues(season)[resDevLvl] + (Math.round(expReq * lvlPercent));
        System.out.println(team.getTeamName() + " resDev experience: [" + resDevExp + "] resDev level: [" + resDevLvl + "]");
        resDev.experience = resDevExp;
        resDev.level = resDevLvl;
        // if (resDevLvl > 0)
        //     resDev.level = resDevLvl;
        // else {
        //     resDev.level = 1;
        //     resDevExp = resDevLevelExpValues(season)[1];
        // }
        wTunnel.level = tunnelLevelValues[Math.floorDiv(team.getResearchLevel(), 10)];

        return newTeamFacilities;
    }

    public static int getResDevLevelFromExp(int experience, seasonSettings season) {
        int level = 0;
        for (int i = 0; i < resDevLevelExpValues(season).length; i++) {
            if (experience < resDevLevelExpValues(season)[i + 1]) {
                level = i;
                break;
            }
        }
        return level;
    }

    public static int giveFacilityRP(String facilityString, teamFacilities facilities, seasonSettings season) {
        int rp = 0;
        switch (facilityString) {
            case "resDev":
                rp = Math.round(((float) resDevRpValues[facilities.resDev.getLevel()] / 10) * season.getRaceCount());
                break;
        
            case "windTunnel":
                rp = Math.round(((float) resDevRpValues[facilities.resDev.getLevel()] / 10) * season.getRaceCount());
                rp = Math.round((float) rp * tunnelRpModifyer[facilities.wTunnel.level]);
                break;
        
            default:
                break;
        }
        return rp;
    }

    /** Calculates the RP gained from private testing. the RP ceiling decreases with more parts to test. With no parts to test, the RP floor goes up.
     * @param testingParts (int)
     * @param facilityString (String)
     * @param facilities (teamFacilities)
     * @param season (seasonSettings)
     * @return [0] rpFloor [1] rpCeiling
     */
    public static int[] returnPrivTestingRP(int testingParts, teamFacilities facilities, seasonSettings season) {
        int rp[] = {0, 0};
        if (testingParts > 0)
            rp[1] = Math.round(((((float) resDevRpValues[facilities.resDev.getLevel()] / 10) * season.getRaceCount()) * privTestingRpModifyer[facilities.wTunnel.level]) / testingParts);
        else
            rp[1] = Math.round(((((float) resDevRpValues[facilities.resDev.getLevel()] / 10) * season.getRaceCount()) * privTestingRpModifyer[facilities.wTunnel.level + 1]));


        if (testingParts == 0)
            testingParts = 1;
        if (facilities.wTunnel.level > 0)
            rp[0] = Math.round(((((float) resDevRpValues[facilities.resDev.getLevel()] / 10) * season.getRaceCount()) * tunnelRpModifyer[facilities.wTunnel.level - 1]) / testingParts);
        else
            rp[0] = Math.round(((((float) resDevRpValues[facilities.resDev.getLevel()] / 10) * season.getRaceCount()) * tunnelRpModifyer[0]) / testingParts);

        return rp;
    }

    /**
     * windTunnel
     */
    public static class windTunnel {
        public int level;
        public int charges;
        public int chargesSpent;
        public float[] failureChance = {
            35,     // Level 0
            30,     // Level 1
            25,     // Level 2
            20,     // Level 3
            16,     // Level 4
            12,     // Level 5
            9,      // Level 6
            6,      // Level 7
            3,      // Level 8
            1,      // Level 9
            0       // Level 10
        };

        public float getFailureChance() {
            return failureChance[level];
        }

        public void setCharges(int currentRace) {
            this.charges = tunnelCharges[this.level];
            if (currentRace == 0)
                this.charges += 2;
        }
    }

    /**
     * researchDevelopment
     */
    public static class researchDevelopment {
        public int level;
        public int experience;
        public int researchPoints;
        
        public int getResearchPoints() {
            return researchPoints;
        }
        public void setResearchPoints(int researchPoints) {
            this.researchPoints = researchPoints;
        }
        public void addResearchPoints(int researchPoints) {
            this.researchPoints += researchPoints;
        }
        public int getLevel() {
            return level;
        }
        public void setLevel(int level) {
            this.level = level;
        }
        public int getExperience() {
            return experience;
        }
        public void setExperience(int experience) {
            this.experience = experience;
        }
        
    }
}
