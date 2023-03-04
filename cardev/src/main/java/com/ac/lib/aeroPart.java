package com.ac.lib;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class aeroPart {
    public int id;
    private final StringProperty label;
    public float downforce;
    public float drag;
    public int angle;

    
    public aeroPart(int id, String label, float downforce, float drag, int angle) {
        this.id = id;
        this.label = new SimpleStringProperty(label);
        this.downforce = downforce;
        this.drag = drag;
        this.angle = angle;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public StringProperty labelProperty() {
        return label;
    }
    public String getlabel() {
        return labelProperty().get();
    }
    public void setlabel(String label) {
        this.label.set(label);
    }
    public float getDownforce() {
        return downforce;
    }
    public void setDownforce(float downforce) {
        this.downforce = downforce;
    }
    public float getDrag() {
        return drag;
    }
    public void setDrag(float drag) {
        this.drag = drag;
    }
    public int getAngle() {
        return angle;
    }
    public void setAngle(int angle) {
        this.angle = angle;
    }
}
