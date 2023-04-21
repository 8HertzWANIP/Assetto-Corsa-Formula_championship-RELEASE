package com.ac.lib;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import com.ac.lib.aeroPartInventory.part;
import com.ac.menus.facilities.teamFacilities;
import com.ac.seasons.newSeason.seasonSettings;
import com.ac.seasons.newSeason.teamSetup;

public interface aeroAPI {

    /** Returns the average rating of all aeroparts. Excludes any part which has a 0 value.
     * @param aeroParts
     * @return int Rating
     */
    public static int getCarAeroRating(ArrayList<aeroPart> aeroParts) {
        int downforceObjects = 0;
        int dragObjects = 0;
        Float rating = 0f;
        Float rating2 = 0f;
        for (int i = 0; i < aeroParts.size(); i++) {
            if (aeroParts.get(i).getDownforce() > 0) {
                downforceObjects++;
            }
            if (aeroParts.get(i).getDrag() > 0) {
                dragObjects++;
            }
            rating += aeroParts.get(i).getDownforce();
            rating2 += aeroParts.get(i).getDrag();
        }
        // System.out.println("rating = [" + rating + "]");
        // System.out.println("rating 2 = [" + rating + "]");
        int intRating = ((Integer) Math.round(rating / downforceObjects)) + ((Integer) Math.round(rating2 / dragObjects));
        System.out.println("intRating = [" + intRating + "]");
        return intRating;
    }

    /** Returns the average rating of all parts in aeropartinventory. Excludes any part which has a 0 value.
     * @param aeroPartInventory[]
     * @return int Rating
     */
    public static int getCarAeroRating(aeroPartInventory[] inventory) {
        int downforceObjects = 0;
        int dragObjects = 0;
        Float rating = 0f;
        Float rating2 = 0f;
        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i].installed.downforce > 0) {
                downforceObjects++;
            }
            if (inventory[i].installed.drag > 0) {
                dragObjects++;
            }
            rating += inventory[i].installed.downforce;
            rating2 += inventory[i].installed.drag;
        }
        // System.out.println("rating = [" + rating + "]");
        // System.out.println("rating 2 = [" + rating + "]");
        int intRating = ((Integer) Math.round(rating / downforceObjects)) + ((Integer) Math.round(rating2 / dragObjects));
        System.out.println("intRating = [" + intRating + "]");
        return intRating;
    }

    /** Returns the average rating target part parts in aeropartinventory. Excludes any value if value equals zero.
     * @param aeroPartInventory.part
     * @return int Rating
     */
    public static int getPartTotalAverage(aeroPartInventory.part targetPart, boolean subtractImprovementSpent) {
        if(subtractImprovementSpent)
            return (targetPart.average - (targetPart.improvementSpent * returnPartNonZeroStats(targetPart)));
        else
           return (targetPart.average + ((targetPart.max_improvement * returnPartNonZeroStats(targetPart))));
    }

    public static int returnPartNonZeroStats(aeroPartInventory.part targetPart) {
        if (targetPart.downforce == 0 || targetPart.drag == 0) {
            return 1;
        } else if (isPartEmpty(targetPart)) {
            return 0;
        }
        return 2;
    }

    public static boolean isPartEmpty(aeroPartInventory.part targetPart) {
        if (targetPart.downforce == 0 && targetPart.drag == 0) {
            return true;
        }
        return false;
    }


    public static int returnPartAverage(part targetPart) {
        int average;
        if (targetPart.downforce > 0 && targetPart.drag > 0) { 
            average = (targetPart.downforce + targetPart.drag) / 2;
        } else {
            average = targetPart.downforce + targetPart.drag;
        }
        return average;
    }

    /** Filters the loaded ini part value to values ranging in the 1000's
     * @param parts Team car aeroParts
     * @param season loaded season
     * @return Team car aeroParts
     */
    public static int filterPartValue(float value, boolean calculateDownforce, seasonSettings season) {
        System.out.println(" filtering value [" + value + "]");
        if (calculateDownforce) {
            System.out.println("calculating downforce level");
            if (value != 0.0f) {
                value = Math.round(value * season.baseline);
            }
        } else {
            System.out.println("calculating drag level");
            if (value != 0.0f) {
                value = value * season.baseline;
                value = Math.round(season.baseline - (value - season.baseline));   
            }
        }
        System.out.println("value filtered [" + Math.round(value) + "]");
        return Math.round(value);
    }

    /** Filters the loaded ini parts to values ranging in the 1000's
     * @param parts Team car aeroParts
     * @param season loaded season
     * @return Team car aeroParts
     */
    public static ArrayList<aeroPart> filterAeroStats(ArrayList<aeroPart> parts, seasonSettings season) {
        for (int i = 0; i < parts.size(); i++) {
            if (parts.get(i).drag != 0.0f) {
                parts.get(i).drag = season.baseline - (parts.get(i).drag - season.baseline);
                parts.get(i).drag = parts.get(i).drag / season.baseline;
                parts.get(i).drag = Math.round(parts.get(i).drag * 100000.0f) / 100000.0f;
                System.out.println(parts.get(i).drag);
                
            } else {
                parts.get(i).drag = 0.0f;
                System.out.println(parts.get(i).drag);
            }
            if (parts.get(i).downforce > 0.01f) {
                parts.get(i).downforce = parts.get(i).downforce / season.baseline;
                parts.get(i).downforce = Math.round(parts.get(i).downforce * 100000.0f) / 100000.0f;
                System.out.println(parts.get(i).downforce);
            } else {
                parts.get(i).downforce = 0.0f;
                System.out.println(parts.get(i).downforce);
            }

        }
        return parts;
    }

    /** Filters the values in the UI back to the loaded ini values used by Assetto Corsa.
     * @param parts Team car aeroParts
     * @param season loaded season
     * @return Team car aeroParts
     */
    public static ArrayList<aeroPart> convertAeroStats(ArrayList<aeroPart> parts, seasonSettings season) {
        for (int i = 0; i < parts.size(); i++) {
            if (parts.get(i).drag != 0.0f) {
                parts.get(i).drag = parts.get(i).drag * season.baseline;
                parts.get(i).drag = Math.round(season.baseline - (parts.get(i).drag - season.baseline));
                
            }
            if (parts.get(i).downforce != 0.0f) {
                parts.get(i).downforce = Math.round(parts.get(i).downforce * season.baseline);
            }
        }
        return parts;
    }

    /** Filters the values in the UI back to the loaded ini values used by Assetto Corsa.
     * @param parts Team car aeroParts
     * @param season loaded season
     * @return Team car aeroParts
     */
    public static aeroPartInventory[] filterNewAeroStats(aeroPartInventory[] inventory, seasonSettings season) {
        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i].installed.drag != 0.0f) {
                inventory[i].installed.filteredDrag = season.baseline - ((float) inventory[i].installed.drag - season.baseline);
                inventory[i].installed.filteredDrag = (float) inventory[i].installed.filteredDrag / season.baseline;
                // System.out.println(inventory[i].installed.filteredDrag);
                inventory[i].installed.filteredDrag = Math.round((float) inventory[i].installed.filteredDrag * 100000.0f) / 100000.0f;
                
            } else {
                inventory[i].installed.filteredDrag = 0.0f;
                // System.out.println(inventory[i].installed.filteredDrag);
            }
            if (inventory[i].installed.downforce > 0.01f) {
                inventory[i].installed.filteredDownforce = (float) inventory[i].installed.downforce / season.baseline;
                // System.out.println(inventory[i].installed.filteredDownforce);
                inventory[i].installed.filteredDownforce = Math.round((float) inventory[i].installed.filteredDownforce * 100000.0f) / 100000.0f;
            } else {
                inventory[i].installed.filteredDownforce = 0.0f;
                // System.out.println(inventory[i].installed.filteredDownforce);
            }

        }
        return inventory;
    }

    /** Filters the loaded ini parts to values ranging in the 1000's
     * @param parts Team car aeroParts
     * @param season loaded season
     * @return Team car aeroParts
     */
    public static aeroPartInventory[] convertNewAeroStats(aeroPartInventory[] inventory, seasonSettings season) {
        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i].installed.drag != 0.0f) {
                inventory[i].installed.drag = Math.round(inventory[i].installed.drag * season.baseline);
                inventory[i].installed.drag = Math.round(season.baseline - (inventory[i].installed.drag - season.baseline));
                
            }
            if (inventory[i].installed.downforce != 0.0f) {
                inventory[i].installed.downforce = Math.round(inventory[i].installed.downforce * season.baseline);
            }
            // System.out.println(inventory[i].installed.drag);
            // System.out.println(inventory[i].installed.downforce);

        }
        return inventory;
    }

    // ------------------------------------------- \\
    // ---------- CALCULATE AI DOWNFORCE --------- \\
    // ------------------------------------------- \\
    /** Calculates and returns the amount of downforce to increase/decrease the aeropart by
     * @param investment
     * @param partInvestment
     * @param dForceCost
     * @param sliderModifyer
     * @param team
     * @param season
     * @param part
     * @return 
     */
    public static Double calculateAiDownforce(Double investment, int partInvestment, Float dForceCost, double sliderModifyer, teamSetup team, seasonSettings season, aeroPart part, Double dfIncrease)
    {
        float maxDf = season.getBaseline() * (season.maxDownforce / 100);
        Double funds = (double) Math.round(investment * sliderModifyer);
        Double currentDownforce = (double) part.getDownforce();
        dfIncrease = -80 + (sliderModifyer * 100);
        Double maxDfLimiter;
        if (partInvestment > dForceCost) {
            
            float newDForceCost = dForceCost / (team.getResearchLevel() / 2);
            
            for (int i = 0; funds > newDForceCost; i++) 
            {
                int improvement = 1;
                int randomNum = ThreadLocalRandom.current().nextInt(1, 100);
                if (randomNum < team.getResearchLevel()) {
                    // System.out.println("randomNum: [" + randomNum + "] target: [" + (team.getResearchLevel()) + "]");
                    improvement = 2;
                    randomNum = ThreadLocalRandom.current().nextInt(1, (150)) * 2;
                    if (randomNum < team.getResearchLevel()) {
                        // System.out.println("randomNum: [" + randomNum + "] target: [" + (team.getResearchLevel()) + "]");
                        improvement = 3;
                        randomNum = ThreadLocalRandom.current().nextInt(1, (200)) * 3;
                        if (randomNum < team.getResearchLevel()) {
                            // System.out.println("randomNum: [" + randomNum + "] target: [" + (team.getResearchLevel()) + "]");
                            improvement = 4;
                            System.out.println("SUCCESS!");
                        }
                    }
                }
                if (i > 20)
                    break;
                if (investment > 0)
                    investment -= newDForceCost;
                if (investment > 0) {
                    maxDfLimiter = 10 - (((100 * (currentDownforce + dfIncrease)) / maxDf));
                    dfIncrease += ((improvement * i) * season.getAeroIncreaseRate()) * maxDfLimiter;
                    System.out.println("dfIncrease: ["+dfIncrease+"]");
                    newDForceCost += (newDForceCost * 0.25) * i;
                }
            }
        }
        return dfIncrease;
    }

    // ------------------------------------------- \\
    // ------------ CALCULATE AI DRAG ------------ \\
    // ------------------------------------------- \\
    /** Calculates and returns the amount of drag to increase/decrease the aeropart by
     * @param investment
     * @param sliderModifyer
     * @param team
     * @return Double amount to increase/decrease
     */
    public static Double calculateAiDrag(Double investment, int partInvestment, Float dragCost, double sliderModifyer, teamSetup team, seasonSettings season, aeroPart part, Double dragDecrease)
    {
        float maxDr = season.getBaseline() * (season.maxDrag / 100);
        Double funds = (double) Math.round(investment * sliderModifyer);
        Double currentDrag = (double) part.getDrag();
        dragDecrease = -80 + (sliderModifyer * 100);
        Double maxDragLimiter;
        if (partInvestment > dragCost) {
            
            float newDragCost = dragCost / (team.getResearchLevel() / 2);
            
            for (int i = 0; funds > newDragCost; i++) 
            {
                int improvement = 1;
                int randomNum = ThreadLocalRandom.current().nextInt(1, 100);
                if (randomNum < team.getResearchLevel()) {
                    // System.out.println("randomNum: [" + randomNum + "] target: [" + (team.getResearchLevel()) + "]");
                    improvement = 2;
                    randomNum = ThreadLocalRandom.current().nextInt(1, (150)) * 2;
                    if (randomNum < team.getResearchLevel()) {
                        // System.out.println("randomNum: [" + randomNum + "] target: [" + (team.getResearchLevel()) + "]");
                        improvement = 3;
                        randomNum = ThreadLocalRandom.current().nextInt(1, (200)) * 3;
                        if (randomNum < team.getResearchLevel()) {
                            // System.out.println("randomNum: [" + randomNum + "] target: [" + (team.getResearchLevel()) + "]");
                            improvement = 4;
                            System.out.println("SUCCESS!");
                        }
                    }
                }
                if (i > 20)
                    break;
                if (investment > 0)
                    investment -= newDragCost;
                if (investment > 0) {
                    maxDragLimiter = 10 - (((100 * (currentDrag + dragDecrease)) / maxDr));
                    System.out.println("maxDragLimiter: ["+maxDragLimiter+"]");
                    dragDecrease += ((improvement * i) * season.getDragIncreaseRate()) * maxDragLimiter;
                    System.out.println("dragDecrease: ["+dragDecrease+"]");
                    newDragCost += (newDragCost * 0.25) * i;
                }
            }
        }
        return dragDecrease;
    }

    /*
     * New part
     * Money = reduces randomisation of the produced part stats
     * RP Increases improvement points
     * Produced part stats are difference between max and current stats, devided by the resDev level
     * hidden improvement rate half of devCeiling + RP increase
     */
    public static aeroPartInventory.part generateNewAeroPart(seasonSettings season, aeroPartInventory[] inventory, int newPartIndex, int resDevLevel) {
        System.out.println("resDevLevel: [" + resDevLevel + "]");
        // research & development level override
        // resDevLevel = 9;

        // Part development ceiling resdev level modifyers
        float levelModifyer[] = {
            3.5f,       // level 0
            3.7f,       // level 1
            4.1f,       // level 2
            4.5f,       // level 3
            5.1f,       // level 4
            5.7f,       // level 5
            6.5f,       // level 6
            7.3f,       // level 7
            7.5f,         // level 8
            8.5f,         // level 9
            9f          // level 10
        };
        int rngCeil = 50;
        int rngFlr = 1 * resDevLevel * 2;

        aeroPartInventory.part installedPart = inventory[newPartIndex].installed;
        aeroPartInventory.part newPart = new aeroPartInventory.part(0, 0, 0);
        newPart.partLevel = resDevLevel;
        newPart.installed = false;
        newPart.stats_hidden = true;
        newPart.improvement_hidden = true;

        if (resDevLevel >= 10) {
            newPart.installed = false;
            newPart.stats_hidden = false;
            newPart.improvement_hidden = false;
            resDevLevel = 9;
        }
        boolean hasDownforce = true;
        if (installedPart.downforce == 0) 
            hasDownforce = false;

        boolean hasDrag = true;
        if (installedPart.drag == 0)
            hasDrag = false;

        // Development ceiling for downforce according to R&D level
        int teamDfDevCeiling = (int) ((season.getMaxDownforce() - season.getMinDownforce()) / (9.5f - levelModifyer[resDevLevel]));
        // Development ceiling for drag according to R&D level
        int teamDrDevCeiling = (int) ((season.getMaxDrag() - season.getMinDrag()) / (9.5f - levelModifyer[resDevLevel]));
        System.out.println("Part teamDfDevCeiling: [" + teamDfDevCeiling + "]");
        System.out.println("Part teamDrDevCeiling: [" + teamDrDevCeiling + "]");

        int resDevCeiling = (int) Math.round((((season.returnDfValues()[1] + season.returnDrValues()[1]) / 2) - inventory[newPartIndex].installed.average) * (levelModifyer[resDevLevel] / 10d));
        int newPartStatModifier = resDevCeiling / 4;
        int newPartImprovementPoints = resDevCeiling / 2;
        if (hasDownforce) {
            newPart.downforce = ((int) season.getMinDownforce() + newPartStatModifier) +  ThreadLocalRandom.current().nextInt(rngFlr + newPartStatModifier, teamDfDevCeiling);
        } else {
            newPart.downforce = 0;
        }
        if (hasDrag) {
            newPart.drag = ((int) season.getMinDrag() + newPartStatModifier) +  ThreadLocalRandom.current().nextInt(rngFlr + newPartStatModifier, teamDrDevCeiling);
        } else {
            newPart.drag = 0;
        }
        newPart.improvementSpent = 0;
        System.out.println("Minimum improvement points: [" + newPartImprovementPoints * 0.1 + "]");
        newPart.max_improvement = ThreadLocalRandom.current().nextInt(10 + Math.round((float) newPartImprovementPoints * 0.5f), 10 + newPartImprovementPoints);
        newPart.average = returnPartAverage(newPart);

        if (newPart.downforce > 0) {
            newPart.projectedDownforce =  Integer.toString(rngFlr + newPart.downforce - ThreadLocalRandom.current().nextInt(1 + (int) (newPartStatModifier * (resDevLevel / 10d)), 1 + newPartStatModifier)) + " - " + Integer.toString(newPart.downforce + ThreadLocalRandom.current().nextInt(1, 2 + (int) (rngCeil + teamDfDevCeiling * (resDevLevel / 10d))));
        } else {
            newPart.projectedAverage = newPart.projectedDrag;
        }
        if (newPart.drag > 0) {
            newPart.projectedDrag =  Integer.toString(rngFlr + newPart.drag - ThreadLocalRandom.current().nextInt(1 + (int) (newPartStatModifier * (resDevLevel / 10d)), newPartStatModifier)) + " - " + Integer.toString(newPart.drag + ThreadLocalRandom.current().nextInt(1, 1 + (int) (rngCeil + teamDrDevCeiling * (resDevLevel / 10d))));
        } else {
            newPart.projectedAverage = newPart.projectedDownforce;
        }
        if (newPart.downforce > 0 && newPart.drag > 0) { 
            newPart.projectedAverage =  Integer.toString(rngFlr + newPart.average - ThreadLocalRandom.current().nextInt(1 + (int) (newPartStatModifier * (resDevLevel / 10d)), newPartStatModifier)) + " - " + Integer.toString(newPart.average + ThreadLocalRandom.current().nextInt(1, 1 + (int) ((rngCeil + teamDfDevCeiling + teamDrDevCeiling) / 2 * (resDevLevel / 10d))));
        }

        System.out.println("--==== NEW PART CREATED ====--");
        System.out.println("Part name: [" + inventory[newPartIndex].part_name + "]");
        System.out.println("Part resDevCeiling: [" + resDevCeiling + "]");
        System.out.println("part downforce: [" + newPart.downforce + "]");
        System.out.println("part drag: [" + newPart.drag + "]");
        System.out.println("part average: [" + newPart.average + "]");
        System.out.println("part projectedDownforce: [" + newPart.projectedDownforce + "]");
        System.out.println("part projectedDrag: [" + newPart.projectedDrag + "]");
        System.out.println("part projectedAverage: [" + newPart.projectedAverage + "]");
        System.out.println("part max_improvement: [" + newPart.max_improvement + "]");
        System.out.println("--==== [--------------] ====--");

        return newPart;
    }
    
    public static aeroPartInventory[] testPartsInTunnel(aeroPartInventory[] inventory, teamFacilities fac, float failureChance) {
        for (int i = 0; i < inventory.length; i++) {
            if(ThreadLocalRandom.current().nextInt(100) > failureChance && inventory[i].installed.stats_hidden) {
                inventory[i].installed.stats_hidden = false;
                System.out.println("Part testing success: [" + inventory[i].part_name + "]");
            }
            if(ThreadLocalRandom.current().nextInt(100) > failureChance && inventory[i].installed.improvement_hidden) {
                inventory[i].installed.improvement_hidden = false;
                System.out.println("Part testing success: [" + inventory[i].part_name + "]");
            }
            if (Objects.nonNull(inventory[i].constructed)) {
                if(ThreadLocalRandom.current().nextInt(100) > failureChance && inventory[i].constructed.stats_hidden) {
                    inventory[i].constructed.stats_hidden = false;
                    System.out.println("Part testing success: [" + inventory[i].part_name + "]");
                }
                if(ThreadLocalRandom.current().nextInt(100) > failureChance && inventory[i].constructed.improvement_hidden) {
                    inventory[i].constructed.improvement_hidden = false;
                    System.out.println("Part testing success: [" + inventory[i].part_name + "]");
                }
            }
        }
        return inventory;
    }
    
    public static aeroPartInventory[] testInstalledParts(aeroPartInventory[] inventory, teamFacilities fac, float failureChance) {
        for (int i = 0; i < inventory.length; i++) {
            if(ThreadLocalRandom.current().nextInt(100) > failureChance && inventory[i].installed.stats_hidden) {
                inventory[i].installed.stats_hidden = false;
                System.out.println("Part testing success: [" + inventory[i].part_name + "]");
            }
            if(ThreadLocalRandom.current().nextInt(100) > failureChance && inventory[i].installed.improvement_hidden) {
                inventory[i].installed.improvement_hidden = false;
                System.out.println("Part testing success: [" + inventory[i].part_name + "]");
            }
        }
        return inventory;
    }
    
    public static int countUntestedParts(aeroPartInventory[] inventory) {
        int count = -1;
        for (int i = 0; i < inventory.length; i++) {
            if(inventory[i].installed.stats_hidden) {
                count = i;
            }
            if(inventory[i].installed.improvement_hidden) {
                count = i;
            }
            if (Objects.nonNull(inventory[i].constructed)) {
                if(inventory[i].constructed.stats_hidden) {
                    count = i;
                }
                if(inventory[i].constructed.improvement_hidden) {
                    count = i;
                }
            }
        }
        return count;
    }
}
