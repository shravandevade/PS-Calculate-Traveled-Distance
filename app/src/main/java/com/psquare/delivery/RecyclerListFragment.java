
package com.psquare.delivery;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.psquare.delivery.helper.OnStartDragListener;
import com.psquare.delivery.helper.SimpleItemTouchHelperCallback;
import com.psquare.delivery.utils.Url;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.android.volley.VolleyLog.TAG;

public class RecyclerListFragment extends Activity implements OnStartDragListener, View.OnClickListener {
    private ItemTouchHelper mItemTouchHelper;
    private Button btn_submit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_list);
        btn_submit = findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(this);
        ArrayList<Delivery_details> address = ApplicationManager.getInstance().getArrayAddress();
        if (address != null) {
            RecyclerListAdapter adapter = new RecyclerListAdapter(this, this, address);
            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
            mItemTouchHelper = new ItemTouchHelper(callback);
            mItemTouchHelper.attachToRecyclerView(recyclerView);
        }
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public void onClick(View view) {
        Intent i = new Intent(RecyclerListFragment.this, DeliveryMainActivity.class);
        startActivity(i);
        finish();
    }


    public void removeItemFromList(String id) {
        Log.d(TAG, "removeItemFromList: " + id);


        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("delivery_id", id);
            jsonObject.put("is_delivered", "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Url.IS_DELIVERED, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "onResponse: " + response);
                Toast.makeText(RecyclerListFragment.this, "Delivered sucessfully..", Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: " + error);
            }
        }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                return super.parseNetworkResponse(response);
            }
        };
        ApplicationManager.getInstance().addToRequestQueue(jsonObjectRequest);
    }
}
