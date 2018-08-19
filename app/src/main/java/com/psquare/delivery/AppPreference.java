package com.psquare.delivery;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by DELL on 03-04-2018.
 */

public class AppPreference {

    private final SharedPreferences.Editor editor;
    private final SharedPreferences sharedPreferences;

    public AppPreference(Context context) {
        sharedPreferences = context.getSharedPreferences("P_SQOURE", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void clearPreference() {
        editor.clear().commit();
    }

    public void setUserLogin(boolean b) {
        editor.putBoolean("IS_USER_LOGIN", b);
        editor.apply();
    }

    public boolean isUserLogin() {

        return sharedPreferences.getBoolean("IS_USER_LOGIN", false);
    }

    public void setPassword(String password) {
        editor.putString("PASSWORD", password);
        editor.apply();
    }

    public String getPassword() {
        return sharedPreferences.getString("PASSWORD", null);
    }

    public void setUsername(String username) {
        editor.putString("USERNAME", username);
        editor.apply();
    }

    public String getUsername() {
        return sharedPreferences.getString("USERNAME", null);
    }

    public void setUserType(String usertype) {
        editor.putString("USER_TYPE", usertype);
        editor.commit();
    }

    public String getUserType() {
        return sharedPreferences.getString("USER_TYPE", null);
    }

    public void setUserId(String userId) {
        editor.putString("USER_ID", userId);
        editor.commit();
    }

    public String getUserId() {
        return sharedPreferences.getString("USER_ID", null);
    }

    public void setSaveLogin(boolean b) {
        editor.putBoolean("SAVE_USER_LOGIN", b);
        editor.apply();
    }

    public boolean isSaveLogin() {
        return sharedPreferences.getBoolean("SAVE_USER_LOGIN", false);
    }

    public String getSessionId() {
        return sharedPreferences.getString("SESSION_ID", null);
    }

    public void setSessionId(String SessionId) {
        editor.putString("SESSION_ID", SessionId);
        editor.apply();
    }

    public void setPincode(String pincode) {
        editor.putString("PINCODE", pincode);
        editor.apply();
    }

    public String getPincode() {
        return sharedPreferences.getString("PINCODE", null);
    }

    public void setContact(String contact){
        editor.putString("Contact",contact);
        editor.apply();
    }

    public String getContact(){
        return sharedPreferences.getString("Contact", null);

    }
}
