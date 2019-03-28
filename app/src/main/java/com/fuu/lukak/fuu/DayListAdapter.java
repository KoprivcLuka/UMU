package com.fuu.lukak.fuu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DayListAdapter extends RecyclerView.Adapter<DayListAdapter.MyViewHolder> {
    private List<Date> list;
    final String[] Dnevi = {"NED", "PON", "TOR", "SRE", "ČET", "PET", "SOB"};
    public View.OnClickListener mClickListener;
    public Calendar date = Calendar.getInstance();
    public  int LastSelected = -1;

    public DayListAdapter(List<Date> list) {
        this.list = list;

    }

    public void setClickListener(View.OnClickListener callback) {
        mClickListener = callback;
    }

    public long GetDateOfItem() {
        return date.getTimeInMillis();
    }

    @NonNull
    @Override
    public DayListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycle_view_month, viewGroup, false);
        MyViewHolder holder = new MyViewHolder(v);

        holder.DateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onClick(view);
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final DayListAdapter.MyViewHolder myViewHolder, int i) {
        date.setTimeInMillis(list.get(i).getTime());
        String sklamfi = Dnevi[date.get(Calendar.DAY_OF_WEEK) - 1] + " , " + date.get(Calendar.DAY_OF_MONTH) + "." + (date.get(Calendar.MONTH) + 1);
        myViewHolder.DateText.setText(sklamfi);
        myViewHolder.DateText.setTag(i);

        myViewHolder.DateText.setBackgroundColor(Color.WHITE);
        if(i != LastSelected)
        {
            myViewHolder.DateText.setBackgroundColor(Color.BLUE);
        }
      //  myViewHolder.itemView.findViewById(R.id.datetext).performClick();


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public Button DateText;
        LinearLayout reclayout;


        public MyViewHolder(View v) {
            super(v);
            DateText = v.findViewById(R.id.datetext);
            reclayout = v.findViewById(R.id.recycleviewlayout);
        }
    }


}
