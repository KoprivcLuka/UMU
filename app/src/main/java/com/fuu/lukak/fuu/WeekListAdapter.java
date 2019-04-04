package com.fuu.lukak.fuu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class WeekListAdapter extends RecyclerView.Adapter<WeekListAdapter.MyViewHolder> {
    private ArrayList<ArrayList<Event>> list;
    ArrayList<Date> dates;
    final String[] Dnevi = {"NED", "PON", "TOR", "SRE", "ČET", "PET", "SOB"};
    public View.OnClickListener mClickListener;
    public Calendar date = Calendar.getInstance();
    public int LastSelected = -1;

    public WeekListAdapter(ArrayList<ArrayList<Event>> list, ArrayList<Date> dates) {
        this.list = list;
        this.dates = dates;

    }

    public void setClickListener(View.OnClickListener callback) {
        mClickListener = callback;
    }

    public long GetDateOfItem() {
        return date.getTimeInMillis();
    }

    @NonNull
    @Override
    public WeekListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {


        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_week, viewGroup, false);
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
    public void onBindViewHolder(@NonNull final WeekListAdapter.MyViewHolder myViewHolder, int i) {
        Calendar tod = Calendar.getInstance();
        tod.setTimeInMillis(dates.get(i).getTime());
        myViewHolder.DateText.setText(Dnevi[tod.get(Calendar.DAY_OF_WEEK) - 1] + " , " + tod.get(Calendar.DAY_OF_MONTH) + "." + (tod.get(Calendar.MONTH) + 1));
        myViewHolder.rec.setLayoutManager(new LinearLayoutManager(myViewHolder.rec.getContext(), LinearLayoutManager.VERTICAL, false));

        ArrayList<Event> enaura = new ArrayList<>();

        ArrayList<ArrayList<Event>> listpourah = new ArrayList<>();
        for (int j = 0; j < list.get(i).size(); j++) {
            if (j == 0) {
                enaura.add(list.get(i).get(j));
            } else {
                if (!list.get(i).get(j-1).startTime.equals(list.get(i).get(j).startTime)) {

                    listpourah.add(enaura);
                    enaura = new ArrayList<>();
                    enaura.add(list.get(i).get(j));
                } else {
                    enaura.add(list.get(i).get(j));
                }
            }
        }
        listpourah.add(enaura);

        for (int j = 0; j < listpourah.size(); j++) {
            Collections.sort(listpourah.get(j), new SortByDuration());
        }
        myViewHolder.rec.setAdapter(new RecyclerItemWeek(listpourah));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView DateText;
        RecyclerView rec;


        public MyViewHolder(View v) {
            super(v);
            DateText = v.findViewById(R.id.textView5);
            rec = v.findViewById(R.id.vsakdan);
        }
    }
    class SortByDuration implements Comparator<Event> {
        public int compare(Event a, Event b) {

            if (a.duration < b.duration) return 1;
            else if (a.duration == b.duration) return 0;
            else return -1;
        }
    }

}
