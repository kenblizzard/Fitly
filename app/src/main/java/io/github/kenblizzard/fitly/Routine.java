package io.github.kenblizzard.fitly;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Ken on 3/4/2017.
 */

public class Routine implements Serializable{
    private static final long serialVersionUID = -29245982928391L;

    private String label;
    private String description;
    private ArrayList<TimeSet> listTimeSet;
    private Date dateCreated;

    public int duration;
    public int reps;
    public int rest;

    public Routine() {
        this.dateCreated = new Date();
    }

    public void createRoutine(String label, String description, int duration, int reps, int rest) {
        this.label = label;
        this.description = description;
        this.duration = duration;
        this.reps = reps;
        this.rest = rest;
        this.dateCreated = new Date();
        this.listTimeSet = this.createSets(duration, reps, rest);


    }

    private ArrayList<TimeSet> createSets (int duration, int reps, int rest) {

        ArrayList<TimeSet> listSets = new ArrayList<TimeSet>();
        TimeSet ts;
        boolean isRest = true;
        int totalSets = reps * 2;

        for(int i = 0; i <= totalSets; i++) {
            ts = new TimeSet(duration, rest,isRest);
            isRest = !isRest;
            listSets.add(ts);
        }
        return listSets;
    }

    public String getLabel () {
        return this.label;
    }

    public String getDescription() {
        return this.description;
    }

    public  Date getDateCreated() {
        return this.dateCreated;
    }

    public ArrayList<TimeSet> getListTimeSet() {
        return this.listTimeSet;
    }


}
