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
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

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
    List<String> cats = new ArrayList<>();
    ArrayList<String> types = new ArrayList<>();
    RecyclerView recyclerView;
    FrameLayout frame;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        frame = findViewById(R.id.frame_urnikplac);
        TinyDB tiny = new TinyDB(getApplicationContext());
        if(!tiny.getString("letnik").equals(""))
        {
            this.setTitle(tiny.getString("currpath") + " - " + tiny.getString("letnik") + ". letnik");
        }
        else
        {
            this.setTitle(tiny.getString("currpath"));
        }

        String json = tiny.getString("events");
        Gson gson = new Gson();
        res = Arrays.asList(gson.fromJson(json, Event[].class));

        Calendar now = Calendar.getInstance();
        Calendar begining = Calendar.getInstance();
        if (begining.get(Calendar.MONTH) < 9) {
            begining.set(begining.get(Calendar.YEAR) - 1, 9, 1);
        } else {
            begining.set(begining.get(Calendar.YEAR), 9, 1);
        }

        int weeks = Math.round((float) (now.getTimeInMillis() - begining.getTimeInMillis()) / (1000 * 60 * 60 * 24 * 7)) + 1;

        recyclerView = findViewById(R.id.recylcleviewmonth);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        int zadntedn = 0;

        for (int i = 0; i < res.size(); i++) {

            if (!cats.contains(res.get(i).group.subGroup)) {
                if (!res.get(i).group.subGroup.equals("")) {

                    cats.add(res.get(i).group.subGroup);
                }
            }

            if (!types.contains(res.get(i).type)) {
                if (!res.get(i).type.equals("")) {

                    types.add(res.get(i).type);
                }
            }
            if (res.get(i).endWeek >= zadntedn) {
                zadntedn = res.get(i).endWeek;
            }
        }

        Collections.sort(types);
        tiny.putListString(tiny.getString("currpath") + tiny.getString("letnik") + "types", types);
        java.util.Collections.sort(cats);
        begining.add(Calendar.HOUR, (zadntedn * 168) - 24);


        while (now.getTimeInMillis() < begining.getTimeInMillis()) {

         /*   if (now.get(Calendar.DAY_OF_WEEK) >= Calendar.MONDAY &&
                    (now.get(Calendar.DAY_OF_WEEK) <= Calendar.FRIDAY)) {
                dates.add(new Date(now.getTimeInMillis()));
            } */
            dates.add(new Date(now.getTimeInMillis()));
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
                        Button b = (Button) recyclerView.getLayoutManager().findViewByPosition(adpt.LastSelected).findViewById(R.id.datetext);
                        b.setTextColor(Color.LTGRAY);
                        recyclerView.getLayoutManager().findViewByPosition(adpt.LastSelected).findViewById(R.id.podcrta).setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    }

                    adpt.LastSelected = (int) view.getTag();
                    int position = (int) view.getTag();
                    Button b = (Button) view;
                    b.setTextColor(Color.WHITE);
                    LinearLayout parent = (LinearLayout) b.getParent();
                    parent.findViewById(R.id.podcrta).setBackgroundColor(Color.WHITE);
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


        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);

        FrameLayout fame = findViewById(R.id.frame_urnikplac);


    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        final Context con = this;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);


        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
       finish();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {

            case  R.id.menu_rotate:

                startActivity(new Intent(getApplicationContext(),weekView.class));
                finish();
                break;

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
                            recyclerView.smoothScrollToPosition(index);

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
                datePickerDialog.getDatePicker().setFirstDayOfWeek(2);


                if (dates.size() != 0) {
                    datePickerDialog.getDatePicker().setMaxDate(dates.get(dates.size() - 1).getTime());
                }
                datePickerDialog.show();
                break;
            case R.id.menu_settings:

                final PopupMenu menu = new PopupMenu(this, findViewById(R.id.menu_settings));
                TinyDB tiny = new TinyDB(getApplicationContext());
                ArrayList<String> toignore = tiny.getListString(tiny.getString("currpath") + tiny.getString("letnik"));
                for (String s : cats) {

                    if (!toignore.contains(s)) {
                        menu.getMenu().add(s).setCheckable(true).setChecked(true);
                    } else {
                        menu.getMenu().add(s).setCheckable(true).setChecked(false);
                    }

                }
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        menuItem.setChecked(!menuItem.isChecked());

                        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
                        menuItem.setActionView(new View(getApplicationContext()));
                        menuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                            @Override
                            public boolean onMenuItemActionExpand(MenuItem item) {
                                return false;
                            }

                            @Override
                            public boolean onMenuItemActionCollapse(MenuItem item) {
                                return false;
                            }
                        });
                        return false;

                    }


                });
                menu.setOnDismissListener(new PopupMenu.OnDismissListener() {
                    @Override
                    public void onDismiss(PopupMenu popupMenu) {
                        ArrayList<String> ignorecat = new ArrayList<>();
                        TinyDB tiny = new TinyDB(getApplicationContext());
                        for (int i = 0; i < cats.size(); i++) {
                            if (!popupMenu.getMenu().getItem(i).isChecked()) {
                                ignorecat.add(popupMenu.getMenu().getItem(i).getTitle().toString());
                            }
                        }

                        tiny.putListString(tiny.getString("currpath") + tiny.getString("letnik"), ignorecat);

                        DayListAdapter adpt = (DayListAdapter) recyclerView.getAdapter();
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        Calendar cal = Calendar.getInstance();
                        cal.setTimeInMillis(dates.get(adpt.LastSelected).getTime());
                        fragmentTransaction.replace(R.id.frame_urnikplac, DayFragment.newInstance(cal));
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();


                    }
                });
                menu.show();


                break;

            case R.id.menu_about:

                startActivity(new Intent(getApplicationContext(), About.class));
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

}
