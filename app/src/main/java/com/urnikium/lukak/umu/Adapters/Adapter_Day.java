package com.urnikium.lukak.umu.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.urnikium.lukak.umu.Classes.Event;
import com.urnikium.lukak.umu.R;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class Adapter_Day extends RecyclerView.Adapter<Adapter_Day.MyViewHolder> {
    private ArrayList<ArrayList<Event>> list;
    private ArrayList<Date> dates;
    private String[] Dnevi;

    public Adapter_Day(ArrayList<ArrayList<Event>> list, ArrayList<Date> dates) {
        this.list = list;
        this.dates = dates;

    }

    @NonNull
    @Override
    public Adapter_Day.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_day, viewGroup, false);
        Dnevi = viewGroup.getContext().getResources().getStringArray(R.array.Days);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final Adapter_Day.MyViewHolder myViewHolder, int i) {
        Calendar tod = Calendar.getInstance();
        Boolean IsToday = false;
        Boolean Past = false;
        if(tod.getTimeInMillis() > dates.get(i).getTime()) {
         Past = true;
        }
        tod.setTimeInMillis(dates.get(i).getTime());

        Calendar keriŠpageti = Calendar.getInstance();
        if(tod.get(Calendar.DATE) == keriŠpageti.get(Calendar.DATE) && tod.get(Calendar.MONTH) == keriŠpageti.get(Calendar.MONTH) && tod.get(Calendar.YEAR) == keriŠpageti.get(Calendar.YEAR))
        {
            IsToday = true;
        }
        myViewHolder.DateText.setText(Dnevi[tod.get(Calendar.DAY_OF_WEEK) - 1] + " , "
                + tod.get(Calendar.DAY_OF_MONTH) + "." + (tod.get(Calendar.MONTH) + 1));
        myViewHolder.rec.setLayoutManager(new LinearLayoutManager(myViewHolder.rec.getContext(), LinearLayoutManager.VERTICAL, false));
        ArrayList<Event> enaura = new ArrayList<>();
        ArrayList<ArrayList<Event>> listpourah = new ArrayList<>();

        for (int j = 0; j < list.get(i).size(); j++) {
            if (j == 0) {
                enaura.add(list.get(i).get(j));
            } else {
                if (!list.get(i).get(j - 1).startTime.equals(list.get(i).get(j).startTime)) {
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
        myViewHolder.rec.setAdapter(new Adapter_DayContent(listpourah,IsToday, Past));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
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
            //Returns reverse sorted
            return Integer.compare(a.duration, b.duration) * -1;

        }
    }

}
