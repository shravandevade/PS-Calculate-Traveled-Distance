package com.psquare.delivery;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.psquare.delivery.utils.Url;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import id.zelory.compressor.Compressor;

public class SurveyMainActivity extends AppCompatActivity implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static final String TAG = "locationActivity";
    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private static final int STORAGE_PERMISSION = 2;
    private AutoCompleteTextView mAutocompleteTextView;
    String postalCode;
    private PlaceArrayAdapter mPlaceArrayAdapter;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(18.520278, 73.856667), new LatLng(18.520278, 73.856667));

    public ArrayList<String> pincodes;
    public PincodeResponse pincodeResponse;


    TextView tvLocation;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation;
    String mLastUpdateTime;
    boolean GpsStatus = false;
    Location location;
    LocationManager locationManager;
    String Holder;
    Button Submit, Logout;
    Intent intent1;
    String lng, lat, name, add, type;
    EditText Mobile, orgnization_name, Post, pin, area1, area2, area3, Land1, Land2, Land3, Soci, Wing, Flat;

    Spinner orgnization_type, State, District, Tehsil;
    ImageView imageView;
    static final int CAM_REQUEST = 1;
    boolean check = true;
    String MobileHolder;
    Button camera;
    Bitmap compressedImage;
    private AppPreference preference;
    private ArrayList<String> statesArrayList, districtArrayList, tehsilArrayList, orgnizationArrayList;
    private StateDistricts stateDistricts;

    private DistrictsTehsil districtsTehsil;
    private String stateId;
    private String districtId;
    private String tehsilId;
    private String orgnizationId;
    private boolean newEntry = true;
    private Gson gson;
    private Org orgsResponse;
    private String imageUrl;


    @SuppressLint("RestrictedApi")
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_main);
        statesArrayList = new ArrayList<>();
        districtArrayList = new ArrayList<>();
        tehsilArrayList = new ArrayList<>();
        orgnizationArrayList = new ArrayList<>();
        preference = new AppPreference(this);
        pincodes = new ArrayList<>();


        gson = new Gson();
        // Intent intent = getIntent();
        //  int i = intent.getIntExtra(ImageListView.BITMAP_ID,0);
        CheckGpsStatus();
        Submit = (Button) findViewById(R.id.Submit);
        Mobile = (EditText) findViewById(R.id.mobile);
        orgnization_type = (Spinner) findViewById(R.id.spinner1);
        orgnization_name = (EditText) findViewById(R.id.EditName);
        getStateDistricts();
        getOrgnizationDetails();
        imageView = (ImageView) findViewById(R.id.img);
        pin = (EditText) findViewById(R.id.Pin);
        State = (Spinner) findViewById(R.id.State);
        District = (Spinner) findViewById(R.id.Dist);
        Tehsil = (Spinner) findViewById(R.id.Tehsil);
        Post = (EditText) findViewById(R.id.Post);
        area1 = (EditText) findViewById(R.id.area1);
        area2 = (EditText) findViewById(R.id.area2);
        area3 = (EditText) findViewById(R.id.area3);
        Land1 = (EditText) findViewById(R.id.land1);
        Land2 = (EditText) findViewById(R.id.land2);
        Land3 = (EditText) findViewById(R.id.land3);
        Soci = (EditText) findViewById(R.id.Soc);
        Wing = (EditText) findViewById(R.id.wing);
        Flat = (EditText) findViewById(R.id.flat);


        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();


        TextView detailsTextView = (TextView) findViewById(R.id.detailsTextView);
        final LinearLayout detailsLinearLayout = (LinearLayout) findViewById(R.id.detailsLinearLayout);
        detailsLinearLayout.setVisibility(View.GONE);
        detailsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (detailsLinearLayout.getVisibility() == View.VISIBLE) {
                    detailsLinearLayout.setVisibility(View.GONE);
                } else {
                    detailsLinearLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        mAutocompleteTextView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        mAutocompleteTextView.setThreshold(3);

        mGoogleApiClient = new GoogleApiClient.Builder(SurveyMainActivity.this)
                .addApi(Places.GEO_DATA_API)
                .addApi(LocationServices.API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();

        mAutocompleteTextView.setOnItemClickListener(mAutocompleteClickListener);
        mPlaceArrayAdapter = new PlaceArrayAdapter(this, android.R.layout.simple_list_item_1,
                BOUNDS_MOUNTAIN_VIEW, null);
        mAutocompleteTextView.setAdapter(mPlaceArrayAdapter);


        Intent intent = getIntent();
        MobileHolder = intent.getStringExtra(LoginActivity.UserName);

        orgnization_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    orgnizationId = null;
                } else {
                    orgnizationId = orgsResponse.getOrgnization()[i - 1].getId();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        State.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                districtArrayList.clear();
                districtArrayList.add("Select District");
                if (i == 0) {
                    stateId = null;
                } else {
                    stateId = stateDistricts.getStates()[i - 1].getId();

                    Log.d(TAG, "onItemSelected: " + stateId);
                    for (int j = 0; j < stateDistricts.getDistrict().length; j++) {
                        if (stateId.equals(stateDistricts.getDistrict()[j].getState_id())) {
                            districtArrayList.add(stateDistricts.getDistrict()[j].getName());
                        }

                    }
                }
                ArrayAdapter<String> districtAdapter1 = new ArrayAdapter<String>(SurveyMainActivity.this, android.R.layout.simple_spinner_dropdown_item, districtArrayList);
                districtAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                District.setAdapter(districtAdapter1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        District.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (i == 0) {
                    districtId = null;
                } else {
                    String districtName = districtArrayList.get(i);
                    for (int j = 0; j < stateDistricts.getDistrict().length; j++) {
                        if (stateId.equals(stateDistricts.getDistrict()[j].getState_id()) && districtName.equals(stateDistricts.getDistrict()[j].getName())) {
                            districtId = stateDistricts.getDistrict()[j].getId();
                            getDistrictsTehsil(districtId);
                            break;
                        }
                    }
                    Log.d(TAG, "onItemSelected: " + districtId);

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.District_array, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        District.setAdapter(adapter2);

        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this,
                R.array.Tehsil_array, android.R.layout.simple_spinner_item);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Tehsil.setAdapter(adapter3);
        Tehsil.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    tehsilId = null;
                } else {
                    tehsilId = districtsTehsil.getTehsil()[i - 1].getId();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
        //        R.array.spinner1_array, android.R.layout.simple_spinner_item);

        //  adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        //  orgnization_type.setAdapter(adapter);
        // orgnization_type.setOnItemSelectedListener(this);
        Submit.setOnClickListener(this);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AppPermission.checkCameraPermission(SurveyMainActivity.this) == PackageManager.PERMISSION_GRANTED && AppPermission.checkStoragePermission(SurveyMainActivity.this) == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent();
                } else {
                    AppPermission.requestStoragePermission(SurveyMainActivity.this, STORAGE_PERMISSION);
                }

            }
        });

        if (GpsStatus == true) {
            if (Holder != null) {
                if (ActivityCompat.checkSelfPermission(
                        SurveyMainActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        &&
                        ActivityCompat.checkSelfPermission(SurveyMainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                location = locationManager.getLastKnownLocation(Holder);
                locationManager.requestLocationUpdates(Holder, 12000, 7, (android.location.LocationListener) SurveyMainActivity.this);
            }
        } else {

            intent1 = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent1);


        }
        Log.d(TAG, "onCreate ...............................");
        if (!isGooglePlayServicesAvailable()) {
            finish();
        }
        createLocationRequest();
        tvLocation = (TextView) findViewById(R.id.tvLocation);
        updateUI();

        getAssignPincode();




/*
        int size=pincodes.size();
        Log.i("test123",""+size);
        //Log.i("pincod",""+pincodes.toString());
        Log.i("pincodes_2", "postPincode" + pincodes);
        //Log.i("pincodes",""+pincodes.get(0).toString());
        Log.i("test123456",""+postalCode)  ;

         for (int i=0;i<size;i++)
         {
             String pin123=pincodes.get(i);
             if (pin123.equals(postalCode))
             {

                 Log.i("test123",""+pin123)  ;
                 Log.i("test123456",""+postalCode)  ;
             }
         }

*/


        Mobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, final int i, int i1, int i2) {
                if (charSequence.length() == 10) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("contact", Mobile.getText().toString().trim());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Url.USERINFO_URL, jsonObject, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(TAG, "onResponse: " + response);
                            try {
                                if (response.getInt("success") == 1) {
                                    newEntry = false;

                                    mAutocompleteTextView.setText(response.getString("address"));
                                    orgnization_name.setText(response.getString("name"));
                                    pin.setText(response.getString("Pincode"));
                                    Post.setText(response.getString("Post"));
                                    area1.setText(response.getString("Area1"));
                                    area2.setText(response.getString("Area2"));
                                    area3.setText(response.getString("Area3"));
                                    Land1.setText(response.getString("Landmark1"));
                                    Land2.setText(response.getString("Landmark2"));
                                    Land3.setText(response.getString("Landmark3"));
                                    Soci.setText(response.getString("Society_Colony"));
                                    Wing.setText(response.getString("Wing_Lane"));
                                    Flat.setText(response.getString("Flat_HouseNo"));
                                    imageUrl = response.getString("Image_url");
                                    if (imageUrl != null && !imageUrl.isEmpty() && !imageUrl.equals(""))
                                        Picasso.get().load(imageUrl).into(imageView);

                                    for (int i = 0; i < orgsResponse.getOrgnization().length; i++) {
                                        if (orgsResponse.getOrgnization()[i].getId().equals(response.getString("type"))) {
                                            orgnization_type.setSelection(i);
                                            break;
                                        }
                                    }
                                    for (int i = 0; i < stateDistricts.getStates().length; i++) {
                                        if (stateDistricts.getStates()[i].getId().equals(response.getString("State"))) {
                                            Log.d("TAG", "if: ");
                                            State.setSelection(i);
                                            break;
                                        } else {
                                            Log.d("TAG", "else: ");
                                        }
                                    }
                                    for (int i = 0; i < stateDistricts.getDistrict().length; i++) {
                                        if (stateDistricts.getDistrict()[i].getId().equals(response.getString("District"))) {
                                            District.setSelection(i);
                                            break;
                                        }
                                    }
                                    tehsilId = response.getString("Tehsil");
                                    /*for (int i = 0; i < districtsTehsil.getTehsil().length; i++) {
                                        if (districtsTehsil.getTehsil()[i].getId().equals()) {
                                            Tehsil.setSelection(i);
                                            break;
                                        }
                                    }*/

                                } else if (response.getInt("success") == 0) {
                                    newEntry = true;
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d(TAG, "onErrorResponse: ");
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

                } else {
                    Mobile.setError("Mobile number must be 10 digits");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.psquare.delivery",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAM_REQUEST);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            dispatchTakePictureIntent();
        } else {
            Toast.makeText(this, "Please grant storage permission", Toast.LENGTH_SHORT).show();
        }
    }

    private void getStateDistricts() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Url.STATE_URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "onResponse: state " + response);
                Gson gson = new Gson();
                stateDistricts = gson.fromJson(response.toString(), StateDistricts.class);
                if (stateDistricts.getSuccess() == 1) {
                    statesArrayList.add("Select States");
                    for (int i = 0; i < stateDistricts.getStates().length; i++) {
                        statesArrayList.add(stateDistricts.getStates()[i].getName());
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(SurveyMainActivity.this, android.R.layout.simple_spinner_dropdown_item, statesArrayList);
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    State.setAdapter(arrayAdapter);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

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
    }

    private void getDistrictsTehsil(String districtId) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading");
        progressDialog.show();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("districtId", districtId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Url.DISTRICT_URL, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Gson gson = new Gson();
                progressDialog.dismiss();
                tehsilArrayList.clear();
                districtsTehsil = gson.fromJson(response.toString(), DistrictsTehsil.class);
                if (districtsTehsil.getSuccess() == 1) {
                    tehsilArrayList.add("Select Tehsil");
                    int position = 0;
                    for (int i = 0; i < districtsTehsil.getTehsil().length; i++) {
                        tehsilArrayList.add(districtsTehsil.getTehsil()[i].getTehsil_name());
                        if (tehsilId != null && !tehsilId.isEmpty()) {
                            if (districtsTehsil.getTehsil()[i].getId().equals(tehsilId)) {
                                position = i;
                            }
                        }
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(SurveyMainActivity.this, android.R.layout.simple_spinner_dropdown_item, tehsilArrayList);
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    Tehsil.setAdapter(arrayAdapter);
                    Tehsil.setSelection(position);

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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
    }

    private void getOrgnizationDetails() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Url.ORGANIZATION_URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "onResponse: org " + response);
                orgsResponse = gson.fromJson(response.toString(), Org.class);
                //Here you you will get org list
                orgnizationArrayList.clear();
                orgnizationArrayList.add("Select Orgnization_type");
                for (int i = 0; i < orgsResponse.getOrgnization().length; i++) {
                    orgnizationArrayList.add(orgsResponse.getOrgnization()[i].getOrg_type());
                }
                ArrayAdapter<String> orgnizationAdapter1 = new ArrayAdapter<String>(SurveyMainActivity.this, android.R.layout.simple_spinner_dropdown_item, orgnizationArrayList);
                orgnizationAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                orgnization_type.setAdapter(orgnizationAdapter1);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: org ");
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
    }


    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            Log.i(TAG, "Selected: " + item.description);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            Log.i(TAG, "Fetching details for ID: " + item.placeId);
        }
    };
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e(TAG, "Place query did not complete. Error: " +
                        places.getStatus().toString());
                return;
            }
            // Selecting the first object buffer.
            final Place place = places.get(0);
            CharSequence attributions = places.getAttributions();

        }
    };

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void CheckGpsStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart fired ..............");
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop fired ..............");
        mGoogleApiClient.disconnect();
        Log.d(TAG, "isConnected ...............: " + mGoogleApiClient.isConnected());
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
        startLocationUpdates();
        mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);
        Log.i(TAG, "Google Places API connected.");

    }

    protected void startLocationUpdates() {
        @SuppressLint("MissingPermission") PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
        Log.d(TAG, "Location update started ..............: ");
    }

    @Override
    public void onConnectionSuspended(int i) {
        mPlaceArrayAdapter.setGoogleApiClient(null);
        Log.e(TAG, "Google Places API connection suspended.");

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed: " + connectionResult.toString());
        Log.e(TAG, "Google Places API connection failed with error code: "
                + connectionResult.getErrorCode());

        Toast.makeText(this,
                "Google Places API connection failed with error code:" +
                        connectionResult.getErrorCode(),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Firing onLocationChanged..............................................");
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        updateUI();
    }

    private void updateUI() {
        Log.d(TAG, "UI update initiated .............");
        if (null != mCurrentLocation) {
            lat = String.valueOf(mCurrentLocation.getLatitude());
            lng = String.valueOf(mCurrentLocation.getLongitude());


            try {
                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(this, Locale.getDefault());

                addresses = geocoder.getFromLocation(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                Log.d(TAG, "updateUI: address  " + address);
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();
                tvLocation.setText("At Time: " + mLastUpdateTime + "\n" +
                        "Latitude: " + lat + "\n" +
                        "Longitude: " + lng + "\n"
                );

                mAutocompleteTextView.setText(
                        " " + address);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "location is null ...............");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    protected void stopLocationUpdates() {
        if (mGoogleApiClient.isConnected())
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
            Log.d(TAG, "Location update resumed .....................");
        }
    }

    @Override
    public void onClick(View view) {

        if (Mobile.getText().toString().length() == 0) {
            Toast.makeText(getApplicationContext(), "Please Enter Mobile number", Toast.LENGTH_LONG).show();
            Mobile.setError("Please Enter mobile number");
            return;
        } else if (orgnization_name.getText().toString().length() == 0) {
            Toast.makeText(getApplicationContext(), "Please Enter Orgnization name", Toast.LENGTH_LONG).show();
            orgnization_name.setError("Please Enter orgnization name");
            return;
        } else if (mAutocompleteTextView.getText().toString().length() == 0) {
            Toast.makeText(getApplicationContext(), "Please Enter Address", Toast.LENGTH_LONG).show();
            mAutocompleteTextView.setError("Please Enter Address");
            return;
        }
        Date currentTime = Calendar.getInstance().getTime();
        AsyncHttpClient client = new AsyncHttpClient();
        String contact = Mobile.getText().toString();
        String name = orgnization_name.getText().toString();
        String add = mAutocompleteTextView.getText().toString();
//        String type = orgnization_type.getSelectedItem().toString();
        String type = orgnizationId;
        String img_url = imageView.toString();
        String pin1 = pin.getText().toString();
        String post = Post.getText().toString();
        String Area1 = area1.getText().toString();
        String Area2 = area2.getText().toString();
        String Area3 = area3.getText().toString();
        String land1 = Land1.getText().toString();
        String land2 = Land2.getText().toString();
        String land3 = Land3.getText().toString();
        String Soc = Soci.getText().toString();
        String wing = Wing.getText().toString();
        String flat = Flat.getText().toString();

        HashMap<String, String> params = new HashMap<>();
        params.put("userId", preference.getUserId());
        params.put("longitude", lng);
        params.put("latitude", lat);
        params.put("contact", contact);
        params.put("orgnization_type", type);
        params.put("orgnization_name", name);
        params.put("address", add);
        params.put("time", mLastUpdateTime);
        params.put("Pincode", pin1);
        params.put("State", stateId);
        params.put("District", districtId);
        params.put("Tehsil", tehsilId);
        params.put("Post", post);
        params.put("Area1", Area1);
        params.put("Area2", Area2);
        params.put("Area3", Area3);
        params.put("Landmark1", land1);
        params.put("Landmark2", land2);
        params.put("Landmark3", land3);
        params.put("Society_Colony", Soc);
        params.put("Wing_Lane", wing);
        params.put("Flat_HouseNo", flat);
        params.put("image_url", imageUrl);

        ByteArrayOutputStream byteArrayOutputStreamObject;

        if (mCurrentPhotoPath != null) {
            try {
                compressedImage = new Compressor(getApplicationContext())
                        .setMaxWidth(400)
                        .setMaxHeight(400)
                        .setQuality(30)
                        .setCompressFormat(Bitmap.CompressFormat.WEBP)
                        .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_PICTURES).getAbsolutePath())
                        .compressToBitmap(new File(mCurrentPhotoPath));

//                Log.d(TAG, "onClick: image path " + compressedImage.getPath());
                // setCompressedImage();
            } catch (IOException e) {
                e.printStackTrace();

            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            compressedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
            byte[] b = baos.toByteArray();
            String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
            Log.d(TAG, "ImageUploadToServerFunction: " + encodedImage);
            params.put("image_url", encodedImage);
        }
        JSONObject jsonObject = new JSONObject(params);
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Image is uploading");
        progressDialog.setMessage("Please wait");
        progressDialog.show();
        String url = "";
        int requestType = Request.Method.POST;
        if (newEntry) {
            requestType = Request.Method.POST;
            url = Url.UPLOAD_URL;
        } else {
            requestType = Request.Method.POST;
            url = Url.UPDATE_URL;
        }
        try {
            jsonObject.put("userId", preference.getUserId());
            // jsonObject.put("contact",preference.getContact());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "onClick: update json  " + jsonObject);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(requestType, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {


                Log.d(TAG, "onResponse: " + response);

                progressDialog.dismiss();
                try {
                    if (response.getInt("success") == 1) {
                        Toast.makeText(SurveyMainActivity.this, "Image uploaded", Toast.LENGTH_SHORT).show();
                        Intent i1 = getIntent();
                        finish();
                        startActivity(i1);

                    } else {
                        Toast.makeText(SurveyMainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: ");
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
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String type = parent.getItemAtPosition(position).toString();

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    /* @Override
      protected void onActivityResult(int requestCode, int resultCode, Intent data) {
          String path = "sdcard/camera_app/cam_image.jpg";
          imageView.setImageDrawable(Drawable.createFromPath(path));
          imageFile = new File(path);
          if (requestCode == CAM_REQUEST && resultCode == RESULT_OK && data != null) {
              Log.d(TAG, "onActivityResult: if");

          } else {
              Log.d(TAG, "onActivityResult: else");
          }
      }*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            setPic();
        }
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();
        if (targetW == 0) {
            targetW = 100;
        }
        if (targetH == 0) {
            targetH = 100;
        }

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        imageView.setImageBitmap(bitmap);
    }

    private void getAssignPincode() {
        Log.i("getAssignPincode", "getAssignPincode");

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userId", preference.getUserId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Url.ASSING_PINCODE, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("TAG", "onResponse: assigned pincode " + response);
                pincodeResponse = gson.fromJson(response.toString(), PincodeResponse.class);
                if (pincodeResponse.getSuccess() == 1) {
                    for (int i = 0; i < pincodeResponse.getData().length; i++) {
                        String postPincode = pincodeResponse.getData()[i].getPost() + " - " + pincodeResponse.getData()[i].getPincode();

                        //String pin123=pincodes.get(i);
                        Log.i("pin123", "" + postPincode);
                        String[] separated = postPincode.split("-");
                        String postPincode1 = separated[1];
                        Log.i("postPincode1", "" + postPincode1);
                        String postPincode11 = postPincode1.trim();
                        Log.i("postPincode1", "" + postPincode11);
                        //

                        pincodes.add(postPincode11);
                        Log.i("postPincode", "postPincode" + postPincode);
                        Log.i("pincodes_method", "postPincode" + pincodes);

                    /* if(pincodeResponse.getData()!equals (postalCode)){

                     }*/
                    }

                   /* int size=pincodes.size();
                    Log.i("test123",""+size);
                    //Log.i("pincod",""+pincodes.toString());
                    Log.i("pincodes_2", "postPincode" + pincodes);
                    //Log.i("pincodes",""+pincodes.get(0).toString());
                    Log.i("test123456",""+postalCode)  ;*/

                    if (pincodes.contains(postalCode)) {
                        Log.i("contains", "postalCode");
                    } else {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(SurveyMainActivity.this);
                        dialog.setCancelable(false);
                        dialog.setTitle("Not survey are");
                        dialog.setMessage("" + postalCode);
                        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                //Action for "Delete".
                            }
                        })
                                .setNegativeButton("Cancel ", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //Action for "Cancel".
                                    }
                                });

                        final AlertDialog alert = dialog.create();
                        alert.show();

                    }
                    /*for (int i=0;i<size;i++)
                    {
                        String pin123=pincodes.get(i);
                        Log.i("pin123",""+pin123)  ;
                        String[] separated = pin123.split("-");
                        String pin1234= separated[1];
                        Log.i("pin1234",""+pin1234)  ;
                        String pin12345=pin1234.trim();
                        Log.i("pin12345",""+pin12345)  ;
                        //separated[1]; // this will contain " they taste good
                        Log.i("post_code",""+postalCode)  ;
                        int postalCode1=Integer.parseInt(postalCode);
                        int pin123456=Integer.parseInt(pin12345);
                    }*/
                    //ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(SurveyMapActivity.this, android.R.layout.simple_spinner_dropdown_item, pincodes);
                    //arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    //spinner.setAdapter(arrayAdapter);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        ApplicationManager.getInstance().addToRequestQueue(jsonObjectRequest);
    }


}

