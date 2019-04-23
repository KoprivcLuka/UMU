package com.urnikium.lukak.umu.Views;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.PopupMenu;
import android.widget.Toast;


import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.urnikium.lukak.umu.Classes.Event;
import com.urnikium.lukak.umu.Classes.TinyDB;
import com.urnikium.lukak.umu.Adapters.Adapter_Day;
import com.urnikium.lukak.umu.R;

import java.io.IOException;
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
import java.util.Locale;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class Activity_View extends AppCompatActivity {

    List<Event> res = new ArrayList<>();
    List<Date> dates = new ArrayList<>();
    ArrayList<Date> validdates = new ArrayList<>();
    List<String> cats = new ArrayList<>();
    ArrayList<String> predms = new ArrayList<>();
    ArrayList<String> types = new ArrayList<>();
    ArrayList<String> IgnoredGroups = new ArrayList<>();
    ArrayList<String> IgnoredCourses = new ArrayList<>();
    RecyclerView rec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        rec = findViewById(R.id.recvieweek);
        TinyDB tiny = new TinyDB(getApplicationContext());

        Locale locale = new Locale(tiny.getString("lang"));
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());


        Locale current = getResources().getConfiguration().locale;
        if(!(current.getLanguage().equals("sl") || current.getLanguage().equals("en")) )
        {
            current = new Locale("sl");
            current.setDefault(current);
            Configuration config2 = new Configuration();
            config2.locale = current;
            getBaseContext().getResources().updateConfiguration(config2,
                    getBaseContext().getResources().getDisplayMetrics());
            tiny.putString("lang","sl");
        }

        if (tiny.getString("lastq").equals("")) {
            startActivity(new Intent(this, Activity_Selection.class));
            finish();
            return;
        } else {
            RequestPath(getResources().getString(R.string.ServURL) + tiny.getString("lastq"));
        }
        Refresh();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Locale current = getResources().getConfiguration().locale;

        TinyDB tiny = new TinyDB(getApplicationContext());
        if (current.getISO3Language().length() >= 2)
        {
            if (!current.getISO3Language().substring(0, 2).equals(tiny.getString("lang"))) {
                Refresh();
            }
        }
    }

    public void Refresh() {
        res = new ArrayList<>();
        dates = new ArrayList<>();
        validdates = new ArrayList<>();
        cats = new ArrayList<>();
        types = new ArrayList<>();
        predms = new ArrayList<>();
        TinyDB tiny = new TinyDB(this);
        IgnoredGroups = tiny.getListString(tiny.getString("currpath") +
                tiny.getString("letnik"));
        IgnoredCourses = tiny.getListString(tiny.getString("currpath") + tiny.getString("letnik") + "predmsIgnore");
        String json = tiny.getString("events");
        Gson gson = new Gson();
        Locale locale = new Locale(tiny.getString("lang"));
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        if (json.equals("")) return;
        try {
            res = Arrays.asList(gson.fromJson(json, Event[].class));
        } catch (JsonParseException e) {
            startActivity(new Intent(this, Activity_Selection.class));
            finish();
            return;
        }
        if (!tiny.getString("letnik").equals("")) {
            this.setTitle(tiny.getString("currpath") + " - " + tiny.getString("letnik") + ". letnik");
        } else {
            this.setTitle(tiny.getString("currpath"));
        }

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

            if (!predms.contains(res.get(i).course)) {
                if (!res.get(i).course.equals("")) {

                    predms.add(res.get(i).course);
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
        Collections.sort(cats);
        Collections.sort(predms);

        tiny.putListString(tiny.getString("currpath") + tiny.getString("letnik") + "types", types);
        tiny.putListString(tiny.getString("currpath") + tiny.getString("letnik") + "predms", predms);

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
            List<Event> unigonored = DobiFiltirane(res);
            for (int i = 0; i < unigonored.size(); i++) {
                Event ev = unigonored.get(i);
                if (ev.endWeek >= weeks && ev.beginWeek <= weeks && (ev.dayOfWeek == (td.get(Calendar.DAY_OF_WEEK)) - 2)) {
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


        rec.setAdapter(new Adapter_Day(everything, validdates));
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);

        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        startActivity(new Intent(this, Activity_Selection.class));
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
        return;
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

                final Context con = this;
                final PopupMenu menu = new PopupMenu(this, findViewById(R.id.menu_settings));

                menu.getMenu().add(R.string.FilterGroups).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(con);
                        builder.setTitle(R.string.FilterGroups);
                        final TinyDB tiny = new TinyDB(getApplicationContext());
                        final ArrayList<String> toignore = tiny.getListString(tiny.getString("currpath") + tiny.getString("letnik"));
                        boolean[] checkboxes = new boolean[cats.size()];
                        int cntr = 0;
                        for (String s : cats) {
                            if (!toignore.contains(s)) {
                                checkboxes[cntr++] = true;
                            } else {
                                checkboxes[cntr++] = false;
                            }
                        }

                        builder.setMultiChoiceItems(cats.toArray(new String[]{}), checkboxes,
                                new DialogInterface.OnMultiChoiceClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                                        if (!b) {
                                            toignore.add(cats.get(i));
                                        } else if (toignore.contains(cats.get(i))) {
                                            toignore.remove(cats.get(i));
                                        }
                                    }
                                });
                        builder.setPositiveButton(R.string.Save, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {

                                tiny.putListString(tiny.getString("currpath") + tiny.getString("letnik"), toignore);
                                Refresh();
                            }
                        });
                        builder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });
                        builder.show();

                        return false;
                    }
                });
                menu.getMenu().add(R.string.FilterCourses).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(con);
                        builder.setTitle(R.string.FilterCourses);
                        final TinyDB tiny = new TinyDB(getApplicationContext());
                        final ArrayList<String> toignore = tiny.getListString(tiny.getString("currpath") + tiny.getString("letnik") + "predmsIgnore");
                        boolean[] checkboxes = new boolean[predms.size()];
                        int cntr = 0;
                        for (String s : predms) {
                            if (!toignore.contains(s)) {
                                checkboxes[cntr++] = true;
                            } else {
                                checkboxes[cntr++] = false;
                            }
                        }

                        builder.setMultiChoiceItems(predms.toArray(new String[]{}), checkboxes,
                                new DialogInterface.OnMultiChoiceClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                                        if (!b) {
                                            toignore.add(predms.get(i));
                                        } else if (toignore.contains(predms.get(i))) {
                                            toignore.remove(predms.get(i));
                                        }


                                    }
                                });
                        builder.setPositiveButton(R.string.Save, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                tiny.putListString(tiny.getString("currpath") + tiny.getString("letnik") + "predmsIgnore", toignore);
                                Refresh();
                            }
                        });
                        builder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });
                        builder.show();

                        return false;

                    }
                });

                menu.show();
                break;

            case R.id.menu_about:

                startActivity(new Intent(getApplicationContext(), Activity_About.class));
                break;
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

    void RequestPath(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(final okhttp3.Call call, IOException e) {
                        // Error
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast toast = Toast.makeText(getApplicationContext(), R.string.ErrorRefresh, Toast.LENGTH_LONG);
                                toast.show();

                            }
                        });
                    }

                    @Override
                    public void onResponse(okhttp3.Call call, final okhttp3.Response response) throws IOException {

                        final String json = response.body().string();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TinyDB tiny = new TinyDB(getApplicationContext());
                                tiny.putString("events", json);
                                Refresh();
                            }
                        });
                    }
                });
    }

    public ArrayList<Event> DobiFiltirane(List<Event> evs) {
        ArrayList<Event> toreturn = new ArrayList<>();
        for (Event ev : evs) {
            if (!IgnoredGroups.contains(ev.group.subGroup) && (!IgnoredCourses.contains(ev.course))) {
                toreturn.add(ev);
            }
        }

        return toreturn;
    }
}

