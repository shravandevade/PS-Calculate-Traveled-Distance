package com.psquare.delivery;

/**
 * Created by DELL on 23-04-2018.
 */

class Orgnization {
    private String id;

    private String Org_type;

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getOrg_type ()
    {
        return Org_type;
    }

    public void setOrg_type (String Org_type)
    {
        this.Org_type = Org_type;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [id = "+id+", Org_type = "+Org_type+"]";
    }
}
