package com.urnikium.lukak.umu.Views;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.PopupMenu;

import com.google.gson.Gson;
import com.urnikium.lukak.umu.Classes.Event;
import com.urnikium.lukak.umu.Classes.TinyDB;
import com.urnikium.lukak.umu.Adapters.WeekListAdapter;
import com.urnikium.lukak.umu.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class weekView extends AppCompatActivity {

    List<Event> res = new ArrayList<>();
    List<Date> dates = new ArrayList<>();
    ArrayList<Date> validdates = new ArrayList<>();
    List<String> cats = new ArrayList<>();
    ArrayList<String> types = new ArrayList<>();
    RecyclerView rec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_view);
        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        rec = findViewById(R.id.recvieweek);


        TinyDB tiny = new TinyDB(getApplicationContext());
        if (!tiny.getString("letnik").equals("")) {
            this.setTitle(tiny.getString("currpath") + " - " + tiny.getString("letnik") + ". letnik");
        } else {
            this.setTitle(tiny.getString("currpath"));
        }

        String json = tiny.getString("events");
        Gson gson = new Gson();
        res = Arrays.asList(gson.fromJson(json, Event[].class));

        int zadntedn = 0;
        for (int i = 0; i < res.size(); i++) {

            res.get(i).startTime = res.get(i).startTime.replace('.', ':');
            if (res.get(i).endWeek >= zadntedn) {
                zadntedn = res.get(i).endWeek;
            }

            if (!types.contains(res.get(i).type)) {
                if (!res.get(i).type.equals("")) {

                    types.add(res.get(i).type);
                }
            }

            if (!cats.contains(res.get(i).group.subGroup)) {
                if (!res.get(i).group.subGroup.equals("")) {

                    cats.add(res.get(i).group.subGroup);
                }
            }

            PreracunajEndTime(res.get(i));

        }

        Collections.sort(types);
        java.util.Collections.sort(cats);
        tiny.putListString(tiny.getString("currpath") + tiny.getString("letnik") + "types", types);

        Calendar now = Calendar.getInstance();
        Calendar begining = Calendar.getInstance();


        if (begining.get(Calendar.MONTH) < 9) {
            begining.set(begining.get(Calendar.YEAR) - 1, 9, 1);
        } else {
            begining.set(begining.get(Calendar.YEAR), 9, 1);
        }
        Calendar end = Calendar.getInstance();
        end.setTimeInMillis(begining.getTimeInMillis());
        end.add(Calendar.HOUR, (zadntedn * 168) - 24);

        while (now.getTimeInMillis() < end.getTimeInMillis()) {

            dates.add(new Date(now.getTimeInMillis()));
            now.add(Calendar.HOUR, 24);
        }

        ArrayList<ArrayList<Event>> everything = new ArrayList<>();
        for (Date d : dates) {
            ArrayList<Event> today = new ArrayList<>();
            Calendar td = new GregorianCalendar();
            td.setTimeInMillis(d.getTime());
            int weeks = Math.round((float) (td.getTimeInMillis() - begining.getTimeInMillis()) / (1000 * 60 * 60 * 24 * 7)) + 1;
            for (int i = 0; i < res.size(); i++) {
                Event ev = res.get(i);
                if (ev.endWeek >= weeks && ev.beginWeek <= weeks && (ev.dayOfWeek + 1 == (td.get(Calendar.DAY_OF_WEEK)) - 1)) {
                    today.add(ev);

                }
            }
            if (today.size() != 0) {
                everything.add(today);
                validdates.add(d);
                Collections.sort(today, new SortByHour());
            }
            today = new ArrayList<>();
        }

        //Evo maš evente ki majo dneve, g g

        rec = findViewById(R.id.recvieweek);
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            rec.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        } else {
            rec.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
          /*  SnapHelper helper = new LinearSnapHelper();
            helper.attachToRecyclerView(recyclerView); */

        }


        rec.setAdapter(new WeekListAdapter(everything, validdates));

    }


    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
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

            case R.id.menu_calendar:
                final Calendar cal = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int y, int m, int dom) {


                        int index = -1;
                        Calendar cal2 = Calendar.getInstance();
                        cal2.setTimeInMillis(new GregorianCalendar(y, m, dom).getTimeInMillis());


                        for (int i = 0; i < validdates.size(); i++) {
                            Calendar cal3 = Calendar.getInstance();
                            cal3.setTimeInMillis(validdates.get(i).getTime());
                            cal3.setTimeInMillis((new GregorianCalendar(cal3.get(Calendar.YEAR), cal3.get(Calendar.MONTH), cal3.get(Calendar.DAY_OF_MONTH)).getTimeInMillis()));
                            Long diff = cal3.getTimeInMillis() - cal2.getTimeInMillis();
                            if (diff == 0) {
                                index = i;
                                break;
                            }
                        }

                        if (index != -1) {

                            rec.smoothScrollToPosition(index);


                        } else {
                            int nearest = -1;
                            for (int j = 0; j < validdates.size(); j++) {

                                if (cal2.getTimeInMillis() - validdates.get(j).getTime() < 0) {
                                    nearest = j;
                                    break;
                                }
                            }
                            if (nearest == -1) {
                                rec.smoothScrollToPosition(validdates.size() - 1);
                            } else {
                                rec.smoothScrollToPosition(nearest);
                            }

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
                        rec.getAdapter().notifyDataSetChanged();


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

    class SortByHour implements Comparator<Event> {
        public int compare(Event a, Event b) {
            SimpleDateFormat parser = new SimpleDateFormat("HH:mm");
            Date d1 = new Date();
            Date d2 = new Date();
            try {
                d1 = parser.parse(a.startTime);
                d2 = parser.parse(b.startTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (d1.getTime() < d2.getTime()) return -1;
            else if (d1.getTime() == d1.getTime()) return 0;
            else return 1;
        }
    }

    public void PreracunajEndTime(Event ev) {
        SimpleDateFormat parser = new SimpleDateFormat("HH:mm");
        Date d1 = new Date();
        try {
            d1 = parser.parse(ev.startTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar cal = Calendar.getInstance();
        long NewTime = d1.getTime() + (long) (ev.duration * 60 * 1000);
        cal.setTimeInMillis(NewTime);
        int ura = cal.get(Calendar.HOUR_OF_DAY);

        String HourOutput = "";


        if (ura < 10) {
            HourOutput = "0" + ura;
        } else {
            HourOutput = ura + "";
        }
        if (cal.get(Calendar.MINUTE) < 10) {
            ev.endTime = HourOutput + ":0" + cal.get(Calendar.MINUTE);
        } else {
            ev.endTime = HourOutput + ":" + cal.get(Calendar.MINUTE);
        }


    }
}

