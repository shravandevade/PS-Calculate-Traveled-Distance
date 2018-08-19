package com.psquare.delivery;

/**
 * Created by DELL on 23-04-2018.
 */

public class Org {


        private String message;

        private String success;

        private Orgnization[] orgnization;

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

        public  Orgnization[] getOrgnization()
        {
            return orgnization;
        }

        public void setOrgnization (Orgnization[] orgnization)
        {
            this.orgnization = orgnization;
        }

        @Override
        public String toString()
        {
            return "ClassPojo [message = "+message+", success = "+success+", orgnization = "+orgnization+"]";
        }
    }



