package com.fuu.lukak.fuu;

import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;

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
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.Duration;

public class ViewActivity extends AppCompatActivity {

    List<Event> res = new ArrayList<Event>();
    List<Date> dates = new ArrayList<Date>();
    RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        //TODO prestavi v on resume kar spada taj, ko bos nekoc dodau settings

        TinyDB tiny = new TinyDB(getApplicationContext());
        this.setTitle(tiny.getString("currpath"));
        String json = tiny.getString("events");
        Gson gson = new Gson();
        res = Arrays.asList(gson.fromJson(json, Event[].class));

        Calendar now = Calendar.getInstance();
        Calendar begining = Calendar.getInstance();
        //TODO problem z novim solskim letom
        //mesece zacne stet z 0..
        begining.set(2018, 9, 1);
        int weeks = Math.round((float) (now.getTimeInMillis() - begining.getTimeInMillis()) / (1000 * 60 * 60 * 24 * 7)) + 1;

        recyclerView = findViewById(R.id.recylcleviewmonth);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));


        int zadntedn = 0;
//kr streami + android ne radi obviously
        for (int i = 0; i < res.size(); i++) {
            if (res.get(i).endWeek >= zadntedn) {
                zadntedn = res.get(i).endWeek;
            }
        }

        begining.add(Calendar.HOUR, (zadntedn * 168) - 24);


        while (now.getTimeInMillis() < begining.getTimeInMillis()) {

            if (now.get(Calendar.DAY_OF_WEEK) >= Calendar.MONDAY &&
                    (now.get(Calendar.DAY_OF_WEEK) <= Calendar.FRIDAY)) {
                dates.add(new Date(now.getTimeInMillis()));
            }
            now.add(Calendar.HOUR, 24);
        }
        //Če ima urnik dejansko kaj gur
        if (dates.size() != 0) {
            final DayListAdapter adpt = new DayListAdapter(dates);
            adpt.LastSelected = 0;
            adpt.mClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //TODO ta if stavek... ni ga več v layoutmanagerju  ampak je se vedno nek rendran..
                    if (recyclerView.getLayoutManager().findViewByPosition(adpt.LastSelected) != null) {
                        recyclerView.getLayoutManager().findViewByPosition(adpt.LastSelected).findViewById(R.id.datetext).setBackgroundColor(Color.BLUE);
                    }

                    adpt.LastSelected = (int) view.getTag();
                    int position = (int) view.getTag();
                    view.setBackgroundColor(Color.WHITE);
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
        }

        //Teden začne štet z 1 ne 0


    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        final Context con = this;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);


        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {


            case R.id.menu_calendar:
                final Calendar cal = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int y, int m, int dom) {


                        int index = -1;
                        Calendar cal2 = Calendar.getInstance();
                        cal2.setTimeInMillis(new GregorianCalendar(y, m, dom).getTimeInMillis());


                        for (int i = 0; i < dates.size(); i++) {
                            Calendar cal3 = Calendar.getInstance();
                            cal3.setTimeInMillis(dates.get(i).getTime());
                            cal3.setTimeInMillis((new GregorianCalendar(cal3.get(Calendar.YEAR), cal3.get(Calendar.MONTH), cal3.get(Calendar.DAY_OF_MONTH)).getTimeInMillis()));
                            Long diff = cal3.getTimeInMillis() - cal2.getTimeInMillis();
                            if (diff == 0) {
                                index = i;
                                break;
                            }
                        }

                        if (index != -1) {
                            recyclerView.scrollToPosition(index);
                            //TODO problem za jutri, ce dela prenos po referenci smo guči
                            DayListAdapter adpt = (DayListAdapter) recyclerView.getAdapter();
                            adpt.LastSelected = index;
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            Calendar cal = Calendar.getInstance();
                            cal.setTimeInMillis(dates.get(index).getTime());
                            fragmentTransaction.replace(R.id.frame_urnikplac, DayFragment.newInstance(cal));
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();

                        }


                    }
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(cal.getTimeInMillis());

                if (dates.size() != 0) {
                    datePickerDialog.getDatePicker().setMaxDate(dates.get(dates.size() - 1).getTime());
                }
                datePickerDialog.show();
                break;
            case R.id.menu_settings:

                startActivity(new Intent(getApplicationContext(),Settings.class));


                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

}
