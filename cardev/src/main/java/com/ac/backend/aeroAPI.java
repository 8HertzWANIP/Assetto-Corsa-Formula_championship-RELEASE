package com.ac.backend;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public interface aeroAPI {

    /** Returns the average rating of all aeroparts. Excludes any part which has a 0 value.
     * @param aeroParts
     * @return int Rating
     */
    static int getCarAeroRating(ArrayList<aeroPart> aeroParts) {
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

    /** Filters the loaded ini parts to values ranging in the 1000's
     * @param parts Team car aeroParts
     * @param season loaded season
     * @return Team car aeroParts
     */
    static ArrayList<aeroPart> filterAeroStats(ArrayList<aeroPart> parts, seasonSettings season) {
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
    static ArrayList<aeroPart> convertAeroStats(ArrayList<aeroPart> parts, seasonSettings season) {
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
    static Double calculateAiDownforce(Double investment, int partInvestment, Float dForceCost, double sliderModifyer, teamSetup team, seasonSettings season, aeroPart part, Double dfIncrease)
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
    static Double calculateAiDrag(Double investment, int partInvestment, Float dragCost, double sliderModifyer, teamSetup team, seasonSettings season, aeroPart part, Double dragDecrease)
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
    
}
