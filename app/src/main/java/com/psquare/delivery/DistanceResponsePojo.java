package com.psquare.delivery;

/**
 * Created by DELL on 30-04-2018.
 */

public class DistanceResponsePojo {
    private String message;

    private Addressess[] addressess;

    private String success;

    public String getMessage ()
    {
        return message;
    }

    public void setMessage (String message)
    {
        this.message = message;
    }

    public Addressess[] getAddressess ()
    {
        return addressess;
    }

    public void setAddressess (Addressess[] addressess)
    {
        this.addressess = addressess;
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
        return "ClassPojo [message = "+message+", addressess = "+addressess+", success = "+success+"]";
    }
}
