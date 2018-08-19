package com.psquare.delivery;

/**
 * Created by DELL on 12-04-2018.
 */

public class LoginRequestModel {

    private String user_name;
    private String password;

    public LoginRequestModel(String user_name, String password) {
        this.user_name = user_name;
        this.password = password;
    }
}
