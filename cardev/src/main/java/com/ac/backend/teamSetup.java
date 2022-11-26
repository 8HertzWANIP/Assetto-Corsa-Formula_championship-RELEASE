package com.ac.backend;

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
}
