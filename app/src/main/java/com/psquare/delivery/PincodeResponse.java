package com.psquare.delivery;

public class PincodeResponse {

    private int success;
    private String message;
    private PincodeModel[] data;

    public int getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public PincodeModel[] getData() {
        return data;
    }
}
