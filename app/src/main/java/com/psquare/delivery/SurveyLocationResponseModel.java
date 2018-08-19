package com.psquare.delivery;

/**
 * Created by DELL on 26-04-2018.
 */

public class SurveyLocationResponseModel {
    private String message;

    private Locations[] locations;

    private String success;

    public String getMessage ()
    {
        return message;
    }

    public void setMessage (String message)
    {
        this.message = message;
    }

    public Locations[] getLocations ()
    {
        return locations;
    }

    public void setLocations (Locations[] locations)
    {
        this.locations = locations;
    }

    public String getSuccess ()
    {
        return success;
    }

    public void setSuccess (String success)
    {
        this.success = success;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [message = "+message+", locations = "+locations+", success = "+success+"]";
    }
}
