package com.urnikium.lukak.umu.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.urnikium.lukak.umu.Classes.Event;
import com.urnikium.lukak.umu.Classes.TinyDB;
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
        boolean isToday = false;
        boolean past = false;

        Date date8PM = getDateTo8PM(dates.get(i)); //Date is in the past After Eight

        if (tod.getTimeInMillis() > date8PM.getTime()) {
            past = true;
        }

        tod.setTimeInMillis(dates.get(i).getTime());
        Calendar calendar = Calendar.getInstance();

        if (tod.get(Calendar.DATE) == calendar.get(Calendar.DATE) && tod.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) && tod.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)) {
            isToday = true;
        }

        myViewHolder.DateText.setText(Dnevi[tod.get(Calendar.DAY_OF_WEEK) - 1] + " , " + tod.get(Calendar.DAY_OF_MONTH) + "." + (tod.get(Calendar.MONTH) + 1));
        myViewHolder.rec.setLayoutManager(new LinearLayoutManager(myViewHolder.rec.getContext(), LinearLayoutManager.VERTICAL, false));

        ArrayList<Event> enaUra = new ArrayList<>();
        ArrayList<ArrayList<Event>> listPoUrah = new ArrayList<>();

        for (int j = 0; j < list.get(i).size(); j++) {
            if (j == 0) {
                enaUra.add(list.get(i).get(j));
            } else {
                if (!list.get(i).get(j - 1).startTime.equals(list.get(i).get(j).startTime)) {
                    listPoUrah.add(enaUra);
                    enaUra = new ArrayList<>();
                    enaUra.add(list.get(i).get(j));
                } else {
                    enaUra.add(list.get(i).get(j));
                }
            }
        }

            listPoUrah.add(enaUra);

        for (int j = 0; j < listPoUrah.size(); j++) {
            Collections.sort(listPoUrah.get(j), new SortByDuration());
        }

        myViewHolder.rec.setAdapter(new Adapter_DayContent(listPoUrah, isToday, past));
    }


    private Date getDateTo8PM(Date date) {
        Calendar cal = Calendar.getInstance();

        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 55);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTime();
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
