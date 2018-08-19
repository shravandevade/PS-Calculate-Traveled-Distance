
package com.psquare.delivery;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.psquare.delivery.helper.ItemTouchHelperAdapter;
import com.psquare.delivery.helper.ItemTouchHelperViewHolder;
import com.psquare.delivery.helper.OnStartDragListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class RecyclerListAdapter extends RecyclerView.Adapter<RecyclerListAdapter.ItemViewHolder>
        implements ItemTouchHelperAdapter {

    private List<Delivery_details> mItems = new ArrayList<>();
    private ArrayList<Double> latitudeArrayList = new ArrayList<>();
    private ArrayList<Double> longitudeArrayList = new ArrayList<>();
    Context context;
    String b = "";

    private final OnStartDragListener mDragStartListener;

    public RecyclerListAdapter(Context context, OnStartDragListener dragStartListener, ArrayList<Delivery_details> address) {
        mDragStartListener = dragStartListener;
        mItems = address;
        latitudeArrayList = ApplicationManager.getInstance().getArrayLat();
        longitudeArrayList = ApplicationManager.getInstance().getArrayLongi();
        this.context = context;

    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main, parent, false);
        ItemViewHolder itemViewHolder = new ItemViewHolder(view);
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, int position) {
        holder.srnoTextView.setText("" + (position + 1));
        holder.textView.setText(mItems.get(position).getContact());

        // Start a drag whenever the handle view it touched
        holder.handleView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onStartDrag(holder);
                }
                return false;
            }
        });
    }

    @Override
    public void onItemDismiss(int position) {
        ((RecyclerListFragment) context).removeItemFromList(mItems.get(position).getId());
        mItems.remove(position);
        latitudeArrayList.remove(position);
        longitudeArrayList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.show();
        Collections.swap(mItems, fromPosition, toPosition);
        Collections.swap(latitudeArrayList, fromPosition, toPosition);
        Collections.swap(longitudeArrayList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        String a = "";

        for (int i = 0; i < mItems.size(); i++) {
            String item = mItems.get(i).getContact();
            a += item;


        }
        ApplicationManager.getInstance().setArrayAddress((ArrayList<Delivery_details>) mItems);
        ApplicationManager.getInstance().setArrayLat(latitudeArrayList);
        ApplicationManager.getInstance().setArrayLongi(longitudeArrayList);
        b += toPosition;
        progressDialog.dismiss();
        return true;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    /**
     * Simple example of a view holder that implements {@link ItemTouchHelperViewHolder} and has a
     * "handle" view that initiates a drag event when touched.
     */
    public static class ItemViewHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder {

        public final TextView textView;
        public final ImageView handleView;
        public final TextView srnoTextView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.text);
            srnoTextView = (TextView) itemView.findViewById(R.id.srnoTextView);
            handleView = (ImageView) itemView.findViewById(R.id.handle);
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }
}
