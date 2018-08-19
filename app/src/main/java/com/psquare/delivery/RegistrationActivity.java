package com.psquare.delivery;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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


public class RegistrationActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String TAG = RegistrationActivity.class.getSimpleName();


    Button register;
    Spinner user_type;
    EditText user_Name, First_name, Last_Name, Mobile, Password, confirmPass;
    String u_type_holder, U_Name_Holder, F_Name_Holder, L_Name_Holder, MobileHolder, PasswordHolder;
    String finalResult;
    String HttpURL = Url.REGISTRATION_URL;
    Boolean CheckEditText;
    ProgressDialog progressDialog;
    HashMap<String, String> hashMap = new HashMap<>();
    HttpParse httpParse = new HttpParse();
    AppPreference preference;
    public static final String UserName = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        preference = new AppPreference(this);
        //Assign Id'S
        user_type = (Spinner) findViewById(R.id.usertype);
        user_Name = (EditText) findViewById(R.id.editTextUserName);
        First_name = (EditText) findViewById(R.id.editTextF_Name);
        Last_Name = (EditText) findViewById(R.id.editTextL_Name);
        Mobile = (EditText) findViewById(R.id.editTextEmail);
        Password = (EditText) findViewById(R.id.editTextPassword);
        confirmPass = (EditText) findViewById(R.id.confrmPassword);
        register = (Button) findViewById(R.id.Submit);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.user_Array, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        user_type.setAdapter(adapter);
        user_type.setOnItemSelectedListener(this);
        //button.setOnClickListener(this);


        //Adding Click Listener on button.

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckEditTextIsEmptyOrNot();

                if (!validate()) {
                    return;
                }
                if (CheckEditText) {
                    JSONObject jsonObject = null;
                    try {
                        Gson gson = new Gson();
                        RegistrationRequestModel registrationRequestModel = new RegistrationRequestModel(u_type_holder, U_Name_Holder, F_Name_Holder, L_Name_Holder, PasswordHolder, MobileHolder);
                        jsonObject = new JSONObject(gson.toJson(registrationRequestModel));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    final ProgressDialog progressDialog = new ProgressDialog(RegistrationActivity.this);
                    progressDialog.setTitle("Registered");
                    progressDialog.setMessage("Please wait");
                    progressDialog.show();

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, HttpURL, jsonObject, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // Log.d(TAG, "onResponse: " + response);
                            Gson gson = new Gson();
                            progressDialog.dismiss();
                            RegistrationResponseModel registrationResponseModel = gson.fromJson(response.toString(), RegistrationResponseModel.class);

                            try {
                                if (registrationResponseModel.getSuccess() == 1) {
//                                    preference.setUserLogin(true);
                                    preference.setUserType(u_type_holder);
                                    preference.setUsername(U_Name_Holder);
                                    preference.setPassword(PasswordHolder);
                                    preference.setUserId(registrationResponseModel.getUserId());
                                    if (registrationResponseModel.getUsertype().equals("1")) {
                                        Intent intent = new Intent(RegistrationActivity.this, DeliveryAddressActivity.class);
                                        intent.putExtra(UserName, U_Name_Holder);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Intent intent = new Intent(RegistrationActivity.this, SurveyCurrentLocationActivity.class);
                                        intent.putExtra(UserName, U_Name_Holder);
                                        startActivity(intent);
                                        finish();
                                    }
                                } else {
                                    Toast.makeText(RegistrationActivity.this, registrationResponseModel.getMessage(), Toast.LENGTH_SHORT).show();
                                }


                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            // If EditText is not empty and CheckEditText = True then this block will execute.

                            //  UserRegisterFunction(u_type_holder, U_Name_Holder, L_Name_Holder, AadharHolder, PasswordHolder);
                            //Intent intent1 = new Intent(RegistrationActivity.this, LoginActivity.class);
                            //startActivity(intent1);
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
                                String json = new String(
                                        response.data,
                                        "UTF-8"
                                );
                                Log.d(TAG, "parseNetworkResponse: json " + json);
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            return super.parseNetworkResponse(response);
                        }
                    };
                    ApplicationManager.getInstance().addToRequestQueue(jsonObjectRequest);
//                    UserLoginFunction(usernameHolder, PasswordHolder);


                } else

                {

                    // If EditText is empty then this block will execute .
                    Toast.makeText(RegistrationActivity.this, "Please fill all form fields.", Toast.LENGTH_LONG).show();

                }

            }

        });
    }

    private boolean validate() {

        String pass = Password.getText().toString();
        String cpass = confirmPass.getText().toString();

        boolean temp = true;
        if (!pass.equals(cpass)) {
            Toast.makeText(RegistrationActivity.this, "Password Not matching", Toast.LENGTH_SHORT).show();
            temp = false;
        }
        return temp;
    }

    public void CheckEditTextIsEmptyOrNot() {

        int position = user_type.getSelectedItemPosition();
        if (position == 0) {
            Toast.makeText(this, "Please select type", Toast.LENGTH_SHORT).show();
        } else if (position == 1) {
            //Delivery Boy
            u_type_holder = "1";
        } else if (position == 2) {
            //Survey Boy
            u_type_holder = "2";
        } else if (position == 3) {
            //Sell Boy
            u_type_holder = "3";
        }
        U_Name_Holder = user_Name.getText().toString();
        L_Name_Holder = Last_Name.getText().toString();
        F_Name_Holder = First_name.getText().toString();
        MobileHolder = Mobile.getText().toString();
        PasswordHolder = Password.getText().toString();


        if (TextUtils.isEmpty(u_type_holder) || TextUtils.isEmpty(U_Name_Holder) || TextUtils.isEmpty(F_Name_Holder) || TextUtils.isEmpty(L_Name_Holder) || TextUtils.isEmpty(MobileHolder) || TextUtils.isEmpty(PasswordHolder)) {

            CheckEditText = false;

        } else {

            CheckEditText = true;
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}

