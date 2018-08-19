package com.psquare.delivery;

/**
 * Created by DELL on 12-04-2018.
 */

public class RegistrationRequestModel {
    private String User_type;
    private String user_name;
    private String userFirst_name;
    private String userLast_name;
    private String password;
    private String Mobile_no;

    public RegistrationRequestModel(String user_type, String user_name,String First_name, String userLast_name, String password,String mobile_no) {
        this.User_type = user_type;
        this.user_name = user_name;
        this.userFirst_name =First_name;
        this.userLast_name = userLast_name;

        this.password = password;
        this.Mobile_no = mobile_no;
    }
}
