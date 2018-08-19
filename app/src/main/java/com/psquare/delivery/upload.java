package com.psquare.delivery;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.psquare.delivery.utils.Url;

import java.util.HashMap;

import id.zelory.compressor.Compressor;


public class upload {
    private static final String TAG = upload.class.getSimpleName();
    Button uploadImageServer;
   Compressor compressedImage;
    String ImagePath = "image_path";

    ProgressDialog progressDialog;

    Bitmap bitmap;
    HttpParse httpParse = new HttpParse();
    boolean check = true;

private Context context;
    public upload(Context context) {
        this.context = context;
    }

    public void ImageUploadToServerFunction( final HashMap<String,String> stringHashMap ) {

        class AsyncTaskUploadClass extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {

                super.onPreExecute();

                // Showing progress dialog at image upload time.
                progressDialog = ProgressDialog.show(context, "Image is Uploading", "Please Wait", false, false);
            }

            @Override
            protected void onPostExecute(String string1) {

                super.onPostExecute(string1);

                // Dismiss the progress dialog after done uploading.
                progressDialog.dismiss();
                Log.d(TAG, "onPostExecute: "+string1);
                // Printing uploading success message coming from server on android app.
                Toast.makeText(context, string1, Toast.LENGTH_LONG).show();

                // Setting image as transparent after done uploading.
            }

            @Override
            protected String doInBackground(Void... params) {


//                Log.d(TAG, "doInBackground: "+stringHashMap.get("image_url"));
                String result=  httpParse.postRequest(stringHashMap, Url.UPLOAD_URL);
                Log.d(TAG, "doInBackground: "+result);
                return result;
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();

        AsyncTaskUploadClassOBJ.execute();
    }


    private void showError(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
    }
