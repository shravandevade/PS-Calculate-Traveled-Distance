package com.psquare.delivery;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.psquare.delivery.utils.Url;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();

    protected static final SharedPreferences settings = null;
    EditText username, Password;
    Button LogIn, newRegistration;
    String PasswordHolder, usernameHolder;
    Boolean CheckEditText;
    HashMap<String, String> hashMap = new HashMap<>();
    HttpParse httpParse = new HttpParse();
    public static final String UserName = "";
    AppPreference preference;
    CheckBox saveLoginCheckbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = (EditText) findViewById(R.id.email);
        Password = (EditText) findViewById(R.id.password);
        saveLoginCheckbox = (CheckBox) findViewById(R.id.checkbox);

        preference = new AppPreference(this);
        if (preference.isSaveLogin()) {
            username.setText(preference.getUsername());
            Password.setText(preference.getPassword());
        } else {
            username.setText(null);
            Password.setText(null);
        }
        if (preference.isUserLogin()) {
            if (preference.getUserType().equals("1")) {
//                Delivery Boy
                Intent Dintent = new Intent(LoginActivity.this, DeliveryAddressActivity.class);
                startActivity(Dintent);
                finish();
            } else if (preference.getUserType().equals("2")) {
                Intent Sintent = new Intent(LoginActivity.this,SurveyCurrentLocationActivity.class);
                startActivity(Sintent);
                finish();
            }

        } else {

        }


        LogIn = (Button) findViewById(R.id.Login);

        LogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckEditTextIsEmptyOrNot();
                if (CheckEditText) {
                    JSONObject jsonObject = null;
                    try {
                        Gson gson = new Gson();
                        LoginRequestModel loginRequestModel = new LoginRequestModel(usernameHolder, PasswordHolder);
                        jsonObject = new JSONObject(gson.toJson(loginRequestModel));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    final ProgressDialog progressDialog= new ProgressDialog(LoginActivity.this);
                    progressDialog.setTitle("Log in");
                    progressDialog.setMessage("Please wait");
                    progressDialog.show();
                    Log.d(TAG, "onClick: " + jsonObject.toString());
                    final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Url.LOGIN_URL, jsonObject, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(TAG, "onResponse: " + response);
                            progressDialog.dismiss();
                            Gson gson = new Gson();
                            LoginResponseModel loginResponseModel = gson.fromJson(response.toString(), LoginResponseModel.class);

                            try {
                                if (loginResponseModel.getSuccess() == 1) {
                                    preference.setUserLogin(true);
                                    preference.setUsername(usernameHolder);
                                    preference.setPassword(PasswordHolder);
                                    preference.setUserId(loginResponseModel.getUserId());
                                    preference.setUserType(loginResponseModel.getUsertype());
                                    preference.setPincode(loginResponseModel.getPincode_id());
                                    SessionRequest();
                                }else {
                                    Toast.makeText(LoginActivity.this, loginResponseModel.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                                if (saveLoginCheckbox.isChecked()) {
                                    preference.setSaveLogin(true);
                                    preference.setUsername(usernameHolder);
                                    preference.setPassword(PasswordHolder);
                                } else {
                                    preference.setSaveLogin(false);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d(TAG, "onErrorResponse: " + error);
                            progressDialog.dismiss();
                        }
                    }) {
                        @Override
                        protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                            try {
                                String json = new String(response.data,"UTF-8");
                                Log.d(TAG, "parseNetworkResponse: json " + json);
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            return super.parseNetworkResponse(response);
                        }
                    };
                    ApplicationManager.getInstance().addToRequestQueue(jsonObjectRequest);
//                    UserLoginFunction(usernameHolder, PasswordHolder);


                } else {

                    Toast.makeText(LoginActivity.this, "Please fill all form fields.", Toast.LENGTH_LONG).show();

                }

            }

        });




    }
    public void SessionRequest(){
        JSONObject jsonObject1=new JSONObject();
        try {
            jsonObject1.put("userId",preference.getUserId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest1 = new JsonObjectRequest(Request.Method.POST, Url.GET_LOGIN_TIME, jsonObject1, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("TAG", "onResponse: " + response);
                try {
                    if(response.getInt("success")==1) {

                        preference.setSessionId(response.getString("Session_id"));
                        if (preference.getUserType().equals("1")) {
                            Intent intent = new Intent(LoginActivity.this, DeliveryAddressActivity.class);
                            intent.putExtra(UserName, usernameHolder);
                            startActivity(intent);
                            finish();

                        } else if (preference.getUserType().equals("2")) {
                            Intent intent = new Intent(LoginActivity.this, SurveyCurrentLocationActivity.class);
                            intent.putExtra(UserName, usernameHolder);
                            startActivity(intent);
                            finish();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("TAG", "onErrorResponse: " + error);
            }
        }){
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
        ApplicationManager.getInstance().addToRequestQueue(jsonObjectRequest1);

    }

    public void CheckEditTextIsEmptyOrNot() {

        usernameHolder = username.getText().toString();
        PasswordHolder = Password.getText().toString();

        if (TextUtils.isEmpty(usernameHolder) || TextUtils.isEmpty(PasswordHolder)) {
            CheckEditText = false;
        } else {

            CheckEditText = true;
        }
    }
}

