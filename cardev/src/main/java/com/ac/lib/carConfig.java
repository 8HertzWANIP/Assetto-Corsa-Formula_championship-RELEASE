package com.ac.lib;

public class carConfig {
    public int fuel;
    public int maxFuel;
    public int oldFuel;
    public int oldMaxFuel;
    
    public int getFuel() {
        return fuel;
    }
    public void setFuel(int fuel) {
        this.fuel = fuel;
    }
    public int getMaxFuel() {
        return maxFuel;
    }
    public void setMaxFuel(int maxFuel) {
        this.maxFuel = maxFuel;
    }
    public int getOldFuel() {
        return oldFuel;
    }
    public void setOldFuel(int oldFuel) {
        this.oldFuel = oldFuel;
    }
    public int getOldMaxFuel() {
        return oldMaxFuel;
    }
    public void setOldMaxFuel(int oldMaxFuel) {
        this.oldMaxFuel = oldMaxFuel;
    }
    public void setFuelToTankSize() {
        if (this.oldFuel > this.maxFuel) {
            this.fuel = (int) Math.round(((float) this.maxFuel / 2d));
        }
    }


    public carConfig(int fuel, int maxFuel, int oldFuel, int oldMaxFuel) {
        this.fuel = fuel;
        this.maxFuel = maxFuel;
        this.oldFuel = oldFuel;
        this.oldMaxFuel = oldMaxFuel;
    }
}
