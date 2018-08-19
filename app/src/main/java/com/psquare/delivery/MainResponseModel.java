package com.psquare.delivery;

/**
 * Created by DELL on 12-04-2018.
 */

public class MainResponseModel {
    private String message;

    private String success;

    private Delivery_details[] delivery_details;



    public String getMessage ()
    {
        return message;
    }

    public void setMessage (String message)
    {
        this.message = message;
    }

    public String getSuccess ()
    {
        return success;
    }

    public void setSuccess (String success)
    {
        this.success = success;
    }

    public Delivery_details[] getDelivery_details ()
    {
        return delivery_details;
    }

    public void setDelivery_details (Delivery_details[] delivery_details)
    {
        this.delivery_details = delivery_details;
    }
}
