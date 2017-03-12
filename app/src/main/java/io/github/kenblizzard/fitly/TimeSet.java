package io.github.kenblizzard.fitly;

import java.io.Serializable;

/**
 * Created by Ken on 3/4/2017.
 */

public class TimeSet implements Serializable {
    private static final long serialVersionUID = -29238982928391L;

    private int duration;
    private int rest;
    private boolean isRest;

    public TimeSet(int duration, int rest, boolean isRest) {
        this.duration = duration;
        this.rest = rest;
        this.isRest = isRest;
    }

    public int getDuration(){
        return this.duration;
    }

    public int getRest() {
        return this.rest;
    }

    public boolean getIsRest() {
        return this.isRest;
    }
}
