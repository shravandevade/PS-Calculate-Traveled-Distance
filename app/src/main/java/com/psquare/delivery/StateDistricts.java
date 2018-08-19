package com.psquare.delivery;

/**
 * Created by DELL on 13-04-2018.
 */

public class StateDistricts {
    private String message;

    private States[] states;

    private Districts[] district;

    private int success;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public States[] getStates() {
        return states;
    }

    public void setStates(States[] states) {
        this.states = states;
    }

    public Districts[] getDistrict() {
        return district;
    }

    public void setDistrict(Districts[] district) {
        this.district = district;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return "ClassPojo [message = " + message + ", states = " + states + ", district = " + district + ", success = " + success + "]";
    }
}
