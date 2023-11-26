package com.mileage.tracker.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mileage.tracker.Models.TripModel;
import com.mileage.tracker.R;

import java.util.List;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.CustomViewHolder> {
    List<TripModel> tripModels;
    Context context;
    private  onItemClickListener mListener;
      public  interface onItemClickListener{
          void  show(int position);
          void  delete(int position);
        }
     public  void setOnItemClickListener(onItemClickListener listener){//item click listener initialization
          mListener=listener;
     }
     public static class  CustomViewHolder extends RecyclerView.ViewHolder{
      TextView textViewTripName,textViewStartLocation
              ,textViewEndLocation,textViewStartTime
              ,textViewEndTime,textViewShowOnMap
              ,textViewTotalMiles;
      ImageView imageViewDelete;
          public CustomViewHolder(View itemView, final onItemClickListener listener) {
             super(itemView);
              textViewTripName=itemView.findViewById(R.id.textViewTripName);
              textViewStartLocation=itemView.findViewById(R.id.textViewStartLocation);
              textViewEndLocation=itemView.findViewById(R.id.textViewEndLocation);
              textViewStartTime=itemView.findViewById(R.id.textViewStartTime);
              textViewEndTime=itemView.findViewById(R.id.textViewEndTime);
              textViewShowOnMap=itemView.findViewById(R.id.textViewShowOnMap);
              textViewTotalMiles=itemView.findViewById(R.id.textViewTotalMiles);
              imageViewDelete=itemView.findViewById(R.id.imageViewDelete);
              textViewShowOnMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener!=null){
                        int position=getAdapterPosition();
                        if(position!= RecyclerView.NO_POSITION){
                            listener.show(position);
                        }
                    }
                }
            });
              imageViewDelete.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      if (listener!=null){
                          int position=getAdapterPosition();
                          if(position!= RecyclerView.NO_POSITION){
                              listener.delete(position);
                          }
                      }
                  }
              });

        }
    }
    public TripAdapter(List<TripModel> tripModels, Context context) {
        this.tripModels=tripModels;
        this.context = context;
    }
    @Override
    public int getItemViewType(int position) {
            return R.layout.trip_layout;
    }
    @Override
    public int getItemCount() {
        return  tripModels.size();
    }
    
    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CustomViewHolder(LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false),mListener);
    }
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        holder.textViewTripName.setText(tripModels.get(position).getTripName());
        holder.textViewStartLocation.setText(tripModels.get(position).getTripStartLocationName());
        holder.textViewEndLocation.setText(tripModels.get(position).getTripEndLocationName());
        holder.textViewStartTime.setText(tripModels.get(position).getTripStartDateAndTime());
        holder.textViewEndTime.setText(tripModels.get(position).getTripEndDateAndTime());
        holder.textViewTotalMiles.setText(tripModels.get(position).getTripTotalMiles());
    }
}
