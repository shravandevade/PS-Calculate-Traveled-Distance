package com.psquare.delivery;

/**
 * Created by DELL on 14-04-2018.
 */

class DistrictsTehsil {
    private String message;

    private Tehsil[] tehsil;

    private Districts[] districts;

    private int success;

    public String getMessage ()
    {
        return message;
    }

    public void setMessage (String message)
    {
        this.message = message;
    }

    public Tehsil[] getTehsil ()
    {
        return tehsil;
    }

    public void setTehsil (Tehsil[] tehsil)
    {
        this.tehsil = tehsil;
    }

    public Districts[] getDistricts ()
    {
        return districts;
    }

    public void setDistricts (Districts[] districts)
    {
        this.districts = districts;
    }

    public int getSuccess ()
    {
        return success;
    }

    public void setSuccess (int success)
    {
        this.success = success;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [message = "+message+", tehsil = "+tehsil+", districts = "+districts+", success = "+success+"]";
    }
}
