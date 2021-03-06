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
        tod.setTimeInMillis(dates.get(i).getTime());

        myViewHolder.DateText.setText(Dnevi[tod.get(Calendar.DAY_OF_WEEK) - 1] + " , " + tod.get(Calendar.DAY_OF_MONTH) + "." + (tod.get(Calendar.MONTH) + 1));
        myViewHolder.rec.setLayoutManager(new LinearLayoutManager(myViewHolder.rec.getContext(), LinearLayoutManager.VERTICAL, false));

        ArrayList<Event> enaUra = new ArrayList<>();
        ArrayList<ArrayList<Event>> listPoUrah = new ArrayList<>();

        for (int j = 0; j < list.get(i).size(); j++) {
            if (j == 0) {
                enaUra.add(list.get(i).get(j));
            } else {
                if (!list.get(i).get(j - 1).getStartTime().equals(list.get(i).get(j).getStartTime())) {
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

        myViewHolder.rec.setAdapter(new Adapter_DayContent(listPoUrah, dates.get(i)));
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
            return Integer.compare(a.getDuration(), b.getDuration()) * -1;

        }
    }

}
