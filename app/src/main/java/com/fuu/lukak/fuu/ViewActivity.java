package com.fuu.lukak.fuu;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewParent;
import android.widget.Button;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.Duration;

public class ViewActivity extends AppCompatActivity {

    List<Event> res = new ArrayList<Event>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        TinyDB tiny = new TinyDB(getApplicationContext());
        String json = tiny.getString("events");
        Gson gson = new Gson();
        res = Arrays.asList(gson.fromJson(json, Event[].class));

        Calendar now = Calendar.getInstance();
        Calendar begining = Calendar.getInstance();
        //TODO problem z novim solskim letom
        //mesece zacne stet z 0..
        begining.set(2018, 9, 1);
        int weeks = Math.round((float) (now.getTimeInMillis() - begining.getTimeInMillis()) / (1000 * 60 * 60 * 24 * 7)) + 1;

        final RecyclerView recyclerView = findViewById(R.id.recylcleviewmonth);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));


        int zadntedn = 0;
//kr streami + android ne radi obviously
        for (int i = 0; i < res.size(); i++) {
            if (res.get(i).endWeek >= zadntedn) {
                zadntedn = res.get(i).endWeek;
            }
        }

        begining.add(Calendar.HOUR, (zadntedn * 168) - 24);
        final ArrayList<Date> dates = new ArrayList<Date>();

        while (now.getTimeInMillis() < begining.getTimeInMillis()) {

            if (now.get(Calendar.DAY_OF_WEEK) >= Calendar.MONDAY &&
                    (now.get(Calendar.DAY_OF_WEEK) <= Calendar.FRIDAY)) {
                dates.add(new Date(now.getTimeInMillis()));
            }
            now.add(Calendar.HOUR, 24);
        }

        final DayListAdapter adpt = new DayListAdapter(dates);
        adpt.mClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //dostuff
                int position = (int) view.getTag();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(dates.get(position).getTime());
                fragmentTransaction.replace(R.id.frame_urnikplac, DayFragment.newInstance(cal));
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        };

        recyclerView.setAdapter(adpt);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(dates.get(0).getTime());
        fragmentTransaction.replace(R.id.frame_urnikplac, DayFragment.newInstance(cal));
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        //Teden začne štet z 1 ne 0



    }
}
