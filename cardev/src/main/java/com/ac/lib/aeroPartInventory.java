package com.ac.lib;

public class aeroPartInventory {
    public int id;
    public String part_name;
    public int angle;
    public part installed;
    public part constructed;
    public aeroPartInventory(int id, String part_name, int angle) {
        this.id = id;
        this.part_name = part_name;
        this.angle = angle;
        this.installed = new part(0, 0, 0);
        this.constructed = null;
    }

    /**
     * part
     */
    public static class part {
        public int partLevel;
        public int downforce;
        public float filteredDownforce = 0;
        public String projectedDownforce;
        public int drag;
        public float filteredDrag = 0;
        public String projectedDrag;
        public int average;
        public String projectedAverage;
        public int max_improvement = 0;
        public int improvementSpent = 0;
        public boolean stats_hidden = false;
        public boolean improvement_hidden = false;
        public boolean installed = true;
        
        public part(int downforce, int drag, int average) {
            this.partLevel = 0;
            this.downforce = downforce;
            this.filteredDownforce = 0;
            this.projectedDownforce = "";
            this.drag = drag;
            this.projectedDrag = "";
            this.filteredDrag = 0;
            this.average = average;
            this.projectedAverage = "";
            this.max_improvement = 0;
            this.improvementSpent = 0;
            this.stats_hidden = false;
            this.improvement_hidden = false;
            this.installed = true;
        }
    }
}