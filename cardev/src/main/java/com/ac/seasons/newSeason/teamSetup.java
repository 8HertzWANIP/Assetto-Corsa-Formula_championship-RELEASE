package com.ac.seasons.newSeason;

import java.util.concurrent.ThreadLocalRandom;

public class teamSetup {
    public String profileName;
    public String controller;
    public String teamName;
    public String teamColor;
    public String carFolder;
    public int money;
    public int researchLevel;
    public int points;
    public int champPos;
    public aiSetup ai;
    public carInfo carInfo;
    
    public teamSetup(String profileName, String controller, String teamName, String teamColor, String carFolder, int money,
            int researchLevel, int points, int champPos) {
        this.profileName = profileName;
        this.controller = controller;
        this.teamName = teamName;
        this.teamColor = teamColor;
        this.carFolder = carFolder;
        this.money = money;
        this.researchLevel = researchLevel;
        this.points = points;
        this.champPos = champPos;
        this.ai = null;
        this.carInfo = new carInfo(-1);
    }
    public String getController() {
        return controller;
    }
    public void setController(String controller) {
        this.controller = controller;
    }
    public String getProfileName() {
        
        return profileName;
    }
    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }
    public String getTeamName() {
        return teamName;
    }
    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }
    public String getTeamColor() {
        return teamColor;
    }
    public void setTeamColor(String teamColor) {
        this.teamColor = teamColor;
    }
    public String getCarFolder() {
        return carFolder;
    }
    public void setCarFolder(String carFolder) {
        this.carFolder = carFolder;
    }
    public int getMoney() {
        return money;
    }
    public void setMoney(int money) {
        this.money = money;
    }
    public void subtractMoney(int money) {
        this.money -= money;
    }
    public int getResearchLevel() {
        return researchLevel;
    }
    public void setResearchLevel(int researchLevel) {
        this.researchLevel = researchLevel;
    }
    public int getPoints() {
        return points;
    }
    public void setPoints(int points) {
        this.points = points;
    }
    public int getChampPos() {
        return champPos;
    }
    public void setChampPos(int champPos) {
        this.champPos = champPos;
    }
    public void addAI(String personality, String philosophy) {
        this.controller = "AI";
        // Set personality
        if (personality.equals("Random")) {
            switch (ThreadLocalRandom.current().nextInt(1, 2)) {
                case 1:
                    personality = "Wide";
                case 2:
                    personality = "Focussed";
            }
        }

        // Set philosophy
        if (philosophy.equals("Random")) {
            switch (ThreadLocalRandom.current().nextInt(1, 3)) {
                case 1:
                    philosophy = "Downforce";
                case 2:
                    philosophy = "Drag";
                case 3:
                    philosophy = "Balanced";
            }
        }
        System.out.println("AI personality: [" + personality + "]");
        System.out.println("AI philosophy: [" + philosophy + "]");

        this.ai = new aiSetup(personality, philosophy);
    }
    
    /**
     * aiSetup
     */
    public class aiSetup {
        public int minBudgetPerRace = 0;
        public String personality;
        public String philosophy;


        public aiSetup(String personality, String philosophy) {
            this.personality = personality;
            this.philosophy = philosophy;
        }
        public int getMinBudgetPerRace() {
            return minBudgetPerRace;
        }
        public void setMinBudgetPerRace(int minBudgetPerRace) {
            this.minBudgetPerRace = minBudgetPerRace;
        }
        public String getPersonality() {
            return personality;
        }
        public void setPersonality(String personality) {
            this.personality = personality;
        }
        public String getPhilosophy() {
            return philosophy;
        }
        public void setPhilosophy(String philosophy) {
            this.philosophy = philosophy;
        }
    }

    /**
     * carInfo
     */
    public class carInfo {
        public int defaultFuel;

        public int FwAngle;
        public int FwStep;
        public int FwMaxAngle;
        public int FwMinAngle;
        public int RwAngle;
        public int RwStep;
        public int RwMaxAngle;
        public int RwMinAngle;

        public int getFwAngle() {
            return FwAngle;
        }
        public void setFwAngle(int fwAngle) {
            FwAngle = fwAngle;
        }
        public int getFwMaxAngle() {
            return FwMaxAngle;
        }
        public void setFwMaxAngle(int fwMaxAngle) {
            FwMaxAngle = fwMaxAngle;
        }
        public int getFwMinAngle() {
            return FwMinAngle;
        }
        public void setFwMinAngle(int fwMinAngle) {
            FwMinAngle = fwMinAngle;
        }
        public int getRwAngle() {
            return RwAngle;
        }
        public void setRwAngle(int rwAngle) {
            RwAngle = rwAngle;
        }
        public int getRwMaxAngle() {
            return RwMaxAngle;
        }
        public void setRwMaxAngle(int rwMaxAngle) {
            RwMaxAngle = rwMaxAngle;
        }
        public int getRwMinAngle() {
            return RwMinAngle;
        }
        public void setRwMinAngle(int rwMinAngle) {
            RwMinAngle = rwMinAngle;
        }

        public int getDefaultFuel() {
            return defaultFuel;
        }

        public void setDefaultFuel(int defaultFuel) {
            this.defaultFuel = defaultFuel;
        }

        public carInfo(int defaultFuel) {
            this.defaultFuel = defaultFuel;
            this.FwAngle = 0;
            this.FwStep = 0;
            this.FwMaxAngle = 0;
            this.FwMinAngle = 0;
            this.RwAngle = 0;
            this.RwStep = 0;
            this.RwMaxAngle = 0;
            this.RwMinAngle = 0;
        }
        
    }
}
