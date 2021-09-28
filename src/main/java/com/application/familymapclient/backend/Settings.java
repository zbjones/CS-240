package com.application.familymapclient.backend;

import android.graphics.Color;

/**
 * Model class to hold information regarding whether switches are toggled.  Also contains the colors
 * for the polylines drawn
 */
public class Settings {

    private static Settings singletonInstance;

    private boolean lifeStoryLines = true;
    private boolean familyTreelines = true;
    private boolean spouseLines = true;
    private boolean fathersSide = true;
    private boolean mothersSide = true;
    private boolean maleEvents = true;
    private boolean femaleEvents = true;

    private int storyLineColor = Color.GREEN;
    private int familyTreeColor = Color.BLUE;
    private int spouseLineColor = Color.RED;

    public static Settings getSettings() {
        if (singletonInstance == null) {
            singletonInstance = new Settings();
        }
        return singletonInstance;
    }

    public boolean isLifeStoryLines() {
        return lifeStoryLines;
    }

    public void setLifeStoryLines(boolean lifeStoryLines) {
        this.lifeStoryLines = lifeStoryLines;
    }

    public boolean isFamilyTreelines() {
        return familyTreelines;
    }

    public void setFamilyTreelines(boolean familyTreelines) {
        this.familyTreelines = familyTreelines;
    }

    public boolean isSpouseLines() {
        return spouseLines;
    }

    public void setSpouseLines(boolean spouseLines) {
        this.spouseLines = spouseLines;
    }

    public boolean isFathersSide() {
        return fathersSide;
    }

    public void setFathersSide(boolean fathersSide) {
        this.fathersSide = fathersSide;
    }

    public boolean isMothersSide() {
        return mothersSide;
    }

    public void setMothersSide(boolean mothersSide) {
        this.mothersSide = mothersSide;
    }

    public boolean isMaleEvents() {
        return maleEvents;
    }

    public void setMaleEvents(boolean maleEvents) {
        this.maleEvents = maleEvents;
    }

    public boolean isFemaleEvents() {
        return femaleEvents;
    }

    public void setFemaleEvents(boolean femaleEvents) {
        this.femaleEvents = femaleEvents;
    }

    public int getStoryLineColor() {
        return storyLineColor;
    }

    public void setStoryLineColor(int storyLineColor) {
        this.storyLineColor = storyLineColor;
    }

    public int getFamilyTreeColor() {
        return familyTreeColor;
    }

    public void setFamilyTreeColor(int familyTreeColor) {
        this.familyTreeColor = familyTreeColor;
    }

    public int getSpouseLineColor() {
        return spouseLineColor;
    }

    public void setSpouseLineColor(int spouseLineColor) {
        this.spouseLineColor = spouseLineColor;
    }
}
