package com.psquare.delivery;

/**
 * Created by DELL on 14-04-2018.
 */

class Tehsil {
    private String id;

    private String districts_id;

    private String tehsil_name;

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getDistricts_id ()
    {
        return districts_id;
    }

    public void setDistricts_id (String districts_id)
    {
        this.districts_id = districts_id;
    }

    public String getTehsil_name ()
    {
        return tehsil_name;
    }

    public void setTehsil_name (String tehsil_name)
    {
        this.tehsil_name = tehsil_name;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [id = "+id+", districts_id = "+districts_id+", tehsil_name = "+tehsil_name+"]";
    }
}
