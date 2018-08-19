package com.psquare.delivery;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.psquare.delivery.utils.Url;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class ChangePasswordActivity extends AppCompatActivity {
    EditText user_name, currentPassword, newPassword, confirmPassword;
    Button save, cancel;
    private final int passwordLength = 6;
    public SharedPreferences prefs;
    private String prefName = "MyPref";
    private static final String TEXT_VALUE_KEY = "nothing";
    private AppPreference preference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        user_name = (EditText) findViewById(R.id.user_name);
        currentPassword = (EditText) findViewById(R.id.oldpassword);
        newPassword = (EditText) findViewById(R.id.newpassword);
        confirmPassword = (EditText) findViewById(R.id.confirmpassword);
        save = (Button) findViewById(R.id.save);

        cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {

                finish();
            }
        });
        preference = new AppPreference(this);
        save.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                Log.d("TAG", "setOnclickListener" + arg0);
                if (confirmPassword.getText().toString().equalsIgnoreCase("") ||
                        currentPassword.getText().toString().equalsIgnoreCase("") ||
                        newPassword.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getBaseContext(), "Please Complete the Information", Toast.LENGTH_SHORT).show();
                } else if (!newPassword.getText().toString().equals(confirmPassword.getText().toString())) {
                    Toast.makeText(getBaseContext(),
                            "These Passwords Don't Match !", Toast.LENGTH_SHORT).show();
                } else if (!preference.getPassword().equals(currentPassword.getText().toString())) {
                    Toast.makeText(getBaseContext(),
                            "Current Password is Incorrect!", Toast.LENGTH_SHORT).show();
                    //  Log.d("TAG"," setOnclicklistener", +  );
                } else {

                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("user_id", preference.getUserId());
                        jsonObject.put("password", preference.getPassword());
                        jsonObject.put("newpassword", newPassword.getText().toString().trim());
                        jsonObject.put("confirmnewpassword", confirmPassword.getText().toString().trim());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Url.ChangePass_URL, jsonObject, new Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("TAG", "onResponse: " + response);
                            try {
                                if (response.getInt("success") == 1) {
                                    preference.setPassword(newPassword.getText().toString().trim());
                                }
                                Toast.makeText(ChangePasswordActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(ChangePasswordActivity.this,LoginActivity.class);
                                startActivity(i);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("TAG", "onErrorResponse: " + error);
                        }
                    }) {
                        @Override
                        protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                            try {
                                String json = new String(
                                        response.data,
                                        "UTF-8"
                                );
                                Log.d("TAG", "parseNetworkResponse: json " + json);
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            return super.parseNetworkResponse(response);
                        }
                    };
                    ApplicationManager.getInstance().addToRequestQueue(jsonObjectRequest);

                }

            }
        });

    }

    @Override
    public void onAttachedToWindow() {
        //this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
        super.onAttachedToWindow();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_HOME)

            BackToMainIntent();

        else if (keyCode == KeyEvent.KEYCODE_BACK) {
            BackToMainIntent();
        }
        return false;
    }

    public void BackToMainIntent() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}


