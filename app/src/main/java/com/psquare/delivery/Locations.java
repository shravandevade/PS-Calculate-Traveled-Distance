package com.psquare.delivery;

/**
 * Created by DELL on 26-04-2018.
 */

class Locations {
    private String user_id;

    private String longitude;

    private String latitude;


    public String getUser_id ()
    {
        return user_id;
    }

    public void setUser_id (String user_id)
    {
        this.user_id = user_id;
    }

    public String getLongitude ()
    {
        return longitude;
    }

    public void setLongitude (String longitude)
    {
        this.longitude = longitude;
    }

    public String getLatitude ()
    {
        return latitude;
    }

    public void setLatitude (String latitude)
    {
        this.latitude = latitude;
    }



    @Override
    public String toString()
    {
        return "ClassPojo [user_id = "+user_id+", longitude = "+longitude+", latitude = "+latitude+"]";
    }
}
