package com.ac.backend;

import java.util.ArrayList;
import java.util.Arrays;

public class seasonSettings {
    public String profileName;
    public int teamCount;
    public int raceCount;
    public int raceLength;
    public int fuelTankSize;
    public int totalPrizePool;
    public float maxDownforce;
    public float minDownforce;
    public float minDrag;
    public float maxDrag;
    public float baseline;
    public float participationPrizeRate;
    public int difficulty;
    public int raceRewards;
    public int currentRace;
    public boolean equalDev;
    public boolean equalFunds;
    public boolean raceCanceled;
    public boolean preseasonTestingCompleted;
    public int[] seasonPointAwards = {};
    public float[] seasonPrizeAwards = {};

    public Double aeroIncreaseRate = 0.3d;
    public Double dragIncreaseRate = 0.4d;

    public seasonSettings(String profileName, int teamCount, int raceCount, int raceLength, int fuelTankSize,
            int totalPrizePool, float maxDownforce, float minDownforce, float minDrag, float maxDrag, float baseline,
            float participationPrizeRate, int difficulty, int raceRewards, int currentRace, boolean equalDev,
            boolean equalFunds, boolean raceCanceled, boolean preseasonTestingCompleted, int[] seasonPointAwards,
            float[] seasonPrizeAwards) {
        this.profileName = profileName;
        this.teamCount = teamCount;
        this.raceCount = raceCount;
        this.raceLength = raceLength;
        this.fuelTankSize = fuelTankSize;
        this.totalPrizePool = totalPrizePool;
        this.maxDownforce = maxDownforce;
        this.minDownforce = minDownforce;
        this.minDrag = minDrag;
        this.maxDrag = maxDrag;
        this.baseline = baseline;
        this.participationPrizeRate = participationPrizeRate;
        this.difficulty = difficulty;
        this.raceRewards = raceRewards;
        this.currentRace = currentRace;
        this.equalDev = equalDev;
        this.equalFunds = equalFunds;
        this.raceCanceled = raceCanceled;
        this.preseasonTestingCompleted = preseasonTestingCompleted;
        this.seasonPointAwards = seasonPointAwards;
        this.seasonPrizeAwards = seasonPrizeAwards;
    }


    public float getParticipationPrizeRate() {
        return participationPrizeRate;
    }

    public void setParticipationPrizeRate(float participationPrizeRate) {
        this.participationPrizeRate = participationPrizeRate;
    }

    public int[] getSeasonPointAwards() {
        return seasonPointAwards;
    }

    public void setSeasonPointAwards(int[] seasonPointAwards) {
        this.seasonPointAwards = seasonPointAwards;
    }

    public float[] getSeasonPrizeAwards() {
        return seasonPrizeAwards;
    }

    public void setSeasonPrizeAwards(float[] seasonPrizeAwards) {
        this.seasonPrizeAwards = seasonPrizeAwards;
    }
    
    public int getCurrentRace() {
        return currentRace;
    }

    public void setCurrentRace(int currentRace) {
        this.currentRace = currentRace;
    }

    public boolean isRaceCanceled() {
        return raceCanceled;
    }

    public void setRaceCanceled(boolean raceCanceled) {
        this.raceCanceled = raceCanceled;
    }

    public boolean isPreseasonTestingCompleted() {
        return preseasonTestingCompleted;
    }

    public void setPreseasonTestingCompleted(boolean preseasonTestingCompleted) {
        this.preseasonTestingCompleted = preseasonTestingCompleted;
    }

    
    public Double getAeroIncreaseRate() {
        return aeroIncreaseRate * returnDifficultyValue(getDifficulty());
    }

    public Double getDragIncreaseRate() {
        return aeroIncreaseRate * returnDifficultyValue(getDifficulty());
    }
    
    public Double getAeroIncreaseRatePlayer() {
        return aeroIncreaseRate * returnDifficultyValue(2);
    }

    public Double getDragIncreaseRatePlayer() {
        return aeroIncreaseRate * returnDifficultyValue(2);
    }
    
    public String getProfileName() {
        return profileName;
    }
    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }
    public int getTeamCount() {
        return teamCount;
    }
    public void setTeamCount(int teamCount) {
        this.teamCount = teamCount;
    }
    public int getRaceCount() {
        return raceCount;
    }
    public void setRaceCount(int raceCount) {
        this.raceCount = raceCount;
    }
    public int getTotalPrizePool() {
        return totalPrizePool;
    }
    public void setTotalPrizePool(int totalPrizePool) {
        this.totalPrizePool = totalPrizePool;
    }
    public float getMaxDownforce() {
        return maxDownforce;
    }
    public void setMaxDownforce(float maxDownforce) {
        this.maxDownforce = maxDownforce;
    }
    public float getMinDownforce() {
        return minDownforce;
    }
    public void setMinDownforce(float minDownforce) {
        this.minDownforce = minDownforce;
    }
    public float getMinDrag() {
        return minDrag;
    }
    public void setMinDrag(float minDrag) {
        this.minDrag = minDrag;
    }
    public float getMaxDrag() {
        return maxDrag;
    }
    public void setMaxDrag(float maxDrag) {
        this.maxDrag = maxDrag;
    }
    public float getBaseline() {
        return baseline;
    }
    public void setBaseline(float baseline) {
        this.baseline = baseline;
    }

    public int getRaceLength() {
        return raceLength;
    }

    public void setRaceLength(int raceLength) {
        this.raceLength = raceLength;
    }

    public int getFuelTankSize() {
        return fuelTankSize;
    }

    public void setFuelTankSize(int fuelTankSize) {
        this.fuelTankSize = fuelTankSize;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public int getRaceRewards() {
        return raceRewards;
    }

    public void setRaceRewards(int raceRewards) {
        this.raceRewards = raceRewards;
    }

    public boolean isEqualDev() {
        return equalDev;
    }

    public void setEqualDev(boolean equalDev) {
        this.equalDev = equalDev;
    }

    public boolean isEqualFunds() {
        return equalFunds;
    }

    public void setEqualFunds(boolean equalFunds) {
        this.equalFunds = equalFunds;
    }

    public float returnDifficultyValue(int difficultyValue) {
        switch (difficultyValue) {
            case 0:
                return 1.8f;
            
            case 1:
                return 1.9f;
            
            case 2:
                return 2f;
            
            case 3:
                return 2.1f;
            default:
                System.out.println("Difficulty not found, returning 0.9");
                return 0.9f;
        }
    }

    public int returnFinishingPositionPoints(int position) {
        if (position > seasonPointAwards.length){
            return 0;
        } else {
            return seasonPointAwards[position - 1];
        }
    }
}
