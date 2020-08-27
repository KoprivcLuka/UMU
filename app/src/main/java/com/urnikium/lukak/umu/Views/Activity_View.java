package com.urnikium.lukak.umu.Views;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.urnikium.lukak.umu.Adapters.Adapter_Day;
import com.urnikium.lukak.umu.Classes.Event;
import com.urnikium.lukak.umu.Classes.TinyDB;
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
    RecyclerView rec;
    Boolean isNewQuery = false; //zaradi sistema cachiranja.. naj preveri šele ko je zagnan nov query ne ko je prvič narisan UI
    List<Event> res = new ArrayList<>();
    List<Date> dates = new ArrayList<>();
    List<String> cats = new ArrayList<>();
    ArrayList<String> types = new ArrayList<>();
    ArrayList<String> predms = new ArrayList<>();
    ArrayList<Date> validDates = new ArrayList<>();
    ArrayList<String> ignoredGroups = new ArrayList<>();
    ArrayList<String> ignoredCourses = new ArrayList<>();
    ArrayList<String> ignoredTypes = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final TinyDB tiny = new TinyDB(getApplicationContext());

        if (tiny.getBoolean("IsDarkMode")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        if (!tiny.getBoolean("agreed")) {
            finish();
            startActivity(new Intent(this, Activity_Selection.class));
            return;
        }

        setContentView(R.layout.activity_week_view);

        //Nastavi status bar
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        rec = findViewById(R.id.recvieweek);

        Locale locale = new Locale(tiny.getString("lang"));
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.locale = locale;

        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        Locale current = getResources().getConfiguration().locale;

        if (!(current.getLanguage().equals("sl") || current.getLanguage().equals("en"))) {
            current = new Locale("sl");
            Locale.setDefault(current);

            Configuration config2 = new Configuration();
            config2.locale = current;

            getBaseContext().getResources().updateConfiguration(config2, getBaseContext().getResources().getDisplayMetrics());

            tiny.putString("lang", "sl");
        }

        if (tiny.getString("lastq").equals("")) {
            startActivity(new Intent(this, Activity_Selection.class));
            finish();

            return;
        } else {
            RequestPath(getResources().getString(R.string.ServURL) + tiny.getString("lastq") + "?client=umu-mobile-prod");
        }

        refresh();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Locale current = getResources().getConfiguration().locale;

        TinyDB tiny = new TinyDB(getApplicationContext());

        if (tiny.getBoolean("SettingsChanged")) {
            tiny.putBoolean("SettingsChanged", false);
            recreate();

            refresh();
        }

        if (current.getISO3Language().length() >= 2) {
            if (!current.getISO3Language().substring(0, 2).equals(tiny.getString("lang"))) {
                refresh();
            }
        }
    }

    public void refresh() {
        final TinyDB tiny = new TinyDB(this);

        res = new ArrayList<>();
        cats = new ArrayList<>();
        dates = new ArrayList<>();
        types = new ArrayList<>();
        predms = new ArrayList<>();
        validDates = new ArrayList<>();

        ignoredGroups = tiny.getListString(tiny.getString("currpath") + tiny.getString("letnik"));
        ignoredCourses = tiny.getListString(tiny.getString("currpath") + tiny.getString("letnik") + "predmsIgnore");
        ignoredTypes = tiny.getListString(tiny.getString("currpath") + tiny.getString("letnik") + "typesIgnore");

        Gson gson = new Gson();
        String json = tiny.getString("events");

        if (json.equals("")) {
            return;
        }

        Locale locale = new Locale(tiny.getString("lang"));
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.locale = locale;

        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        try {
            if (json.trim().equals("null") || json.trim().equals("[]") || json.trim().equals("{}")) {
                throw new JsonParseException("Null query");
            }

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

        int zadntedn = 0; //to niso barilla špageti, to so dizajnerski špageti © Luka 2019

        for (int i = 0; i < res.size(); i++) {
            res.get(i).setStartTime(res.get(i).getStartTime().replace('.', ':'));

            if (res.get(i).getEndWeek() >= zadntedn) {
                zadntedn = res.get(i).getEndWeek();
            }

            if (!types.contains(res.get(i).getType()) && !res.get(i).getType().equals("")) {
                types.add(res.get(i).getType());
            }

            if (!predms.contains(res.get(i).getCourse()) && !res.get(i).getCourse().equals("")) {
                predms.add(res.get(i).getCourse());
            }

            if (!cats.contains(res.get(i).getGroup().getSubGroup()) && !res.get(i).getGroup().getSubGroup().equals("")) {
                cats.add(res.get(i).getGroup().getSubGroup());
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

        if (begining.get(Calendar.MONTH) < 7) {
            begining.set(begining.get(Calendar.YEAR) - 1, 9, 1, 0, 0, 0);
        } else {
            begining.set(begining.get(Calendar.YEAR), 9, 1, 0, 0, 0);
        }

        if (!tiny.getBoolean("ShowGoneEvents")) {
            now.setTimeInMillis(begining.getTimeInMillis());
        }

        now.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH), 0, 0);
        begining.setTimeInMillis(begining.getTimeInMillis() - ((begining.get(Calendar.DAY_OF_WEEK) - begining.getFirstDayOfWeek()) * 1000 * 60 * 60 * 24));

        Calendar end = Calendar.getInstance();
        end.setTimeInMillis(begining.getTimeInMillis());
        end.add(Calendar.HOUR, (zadntedn * 168) - 24);

        while (now.getTimeInMillis() < end.getTimeInMillis()) {
            if (now.getTimeInMillis() >= begining.getTimeInMillis()) { //ne dodajamo dni izven časa semestra
                dates.add(new Date(now.getTimeInMillis()));
            }

            now.add(Calendar.HOUR, 24);
        }

        List<Event> unigonored = DobiFiltirane(res);
        ArrayList<ArrayList<Event>> everything = new ArrayList<>();

        for (Date d : dates) {
            Calendar td = new GregorianCalendar();
            ArrayList<Event> today = new ArrayList<>();

            td.setTimeInMillis(d.getTime());
            int weeks = (int) (Math.floor((td.getTimeInMillis() - begining.getTimeInMillis()) / (1000 * 60 * 60 * 24 * 7)) + 1);

            for (int i = 0; i < unigonored.size(); i++) {
                Event ev = unigonored.get(i);

                if (ev.getEndWeek() >= weeks && ev.getBeginWeek() <= weeks && (ev.getDayOfWeek() == (td.get(Calendar.DAY_OF_WEEK)) - 2)) {
                    today.add(ev);
                }
            }

            if (today.size() != 0) {
                validDates.add(d);
                everything.add(today);
                Collections.sort(today, new SortByHour());
            }
        }

        rec = findViewById(R.id.recvieweek);
        int orientation = getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            rec.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        } else {
            rec.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        }

        rec.setAdapter(new Adapter_Day(everything, validDates));

        if (!tiny.getBoolean(tiny.getString("currpath") + tiny.getString("letnik") + "isset") && isNewQuery) {
            final ArrayList<String> toignore = tiny.getListString(tiny.getString("currpath") + tiny.getString("letnik"));

            int cntr = 0;
            boolean[] checkboxes = new boolean[cats.size()];

            for (String s : cats) {
                checkboxes[cntr++] = !toignore.contains(s);
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.FilterGroups);
            builder.setMultiChoiceItems(cats.toArray(new String[]{}), checkboxes,
                    new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                            if (!b) {
                                toignore.add(cats.get(i));
                            } else toignore.remove(cats.get(i));
                        }
                    });

            builder.setPositiveButton(R.string.Save, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {

                    tiny.putListString(tiny.getString("currpath") + tiny.getString("letnik"), toignore);
                    tiny.putBoolean((tiny.getString("currpath") + tiny.getString("letnik") + "isset"), true);
                    refresh();
                }
            });

            builder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {

                }
            });

            builder.show();
        }

        now = Calendar.getInstance();
        ScrollToPosition(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH), false);
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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_calendar:
                final Calendar cal = Calendar.getInstance();

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int y, int m, int dom) {

                        ScrollToPosition(y, m, dom, true);

                    }
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setFirstDayOfWeek(2);

                if (dates.size() != 0) {
                    datePickerDialog.getDatePicker().setMaxDate(dates.get(dates.size() - 1).getTime());
                }

                datePickerDialog.show();
                break;
            case R.id.menu_settings:
                final Context con = this;

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                assert inflater != null;
                View v = inflater.inflate(R.layout.sortdialog, null);
                v.findViewById(R.id.buttonSelectSkup).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final TinyDB tiny = new TinyDB(getApplicationContext());
                        final ArrayList<String> toignore = tiny.getListString(tiny.getString("currpath") + tiny.getString("letnik"));

                        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(con, R.style.AlertDialogCustom));
                        builder.setTitle(R.string.FilterGroups);

                        int cntr = 0;
                        boolean[] checkboxes = new boolean[cats.size()];

                        for (String s : cats) {
                            checkboxes[cntr++] = !toignore.contains(s);
                        }

                        builder.setMultiChoiceItems(cats.toArray(new String[]{}), checkboxes,
                                new DialogInterface.OnMultiChoiceClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                                        if (!b) {
                                            toignore.add(cats.get(i));
                                        } else toignore.remove(cats.get(i));
                                    }
                                });

                        builder.setPositiveButton(R.string.Save, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {

                                tiny.putListString(tiny.getString("currpath") + tiny.getString("letnik"), toignore);
                                tiny.putBoolean((tiny.getString("currpath") + tiny.getString("letnik") + "isset"), true);
                                refresh();

                            }
                        });

                        builder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });

                        builder.show();

                        return;
                    }
                });

                v.findViewById(R.id.buttonSelectPredm).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final TinyDB tiny = new TinyDB(getApplicationContext());
                        final ArrayList<String> toignore = tiny.getListString(tiny.getString("currpath") + tiny.getString("letnik") + "predmsIgnore");

                        int cntr = 0;
                        boolean[] checkboxes = new boolean[predms.size()];

                        for (String s : predms) {
                            checkboxes[cntr++] = !toignore.contains(s);
                        }

                        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(con, R.style.AlertDialogCustom));
                        builder.setTitle(R.string.FilterCourses);

                        builder.setMultiChoiceItems(predms.toArray(new String[]{}), checkboxes,
                                new DialogInterface.OnMultiChoiceClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                                        if (!b) {
                                            toignore.add(predms.get(i));
                                        } else toignore.remove(predms.get(i));


                                    }
                                });

                        builder.setPositiveButton(R.string.Save, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                tiny.putListString(tiny.getString("currpath") + tiny.getString("letnik") + "predmsIgnore", toignore);
                                refresh();
                            }
                        });

                        builder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });

                        builder.show();

                        return;
                    }

                });

                v.findViewById(R.id.buttonSelectTip).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final TinyDB tiny = new TinyDB(getApplicationContext());
                        final ArrayList<String> toignore = tiny.getListString(tiny.getString("currpath") + tiny.getString("letnik") + "typesIgnore");

                        int cntr = 0;
                        boolean[] checkboxes = new boolean[types.size()];

                        for (String s : types) {
                            checkboxes[cntr++] = !toignore.contains(s);
                        }

                        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(con, R.style.AlertDialogCustom));
                        builder.setTitle(R.string.FilterTypes);

                        builder.setMultiChoiceItems(types.toArray(new String[]{}), checkboxes,
                                new DialogInterface.OnMultiChoiceClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                                        if (!b) {
                                            toignore.add(types.get(i));
                                        } else toignore.remove(types.get(i));


                                    }
                                });

                        builder.setPositiveButton(R.string.Save, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                tiny.putListString(tiny.getString("currpath") + tiny.getString("letnik") + "typesIgnore", toignore);
                                refresh();
                            }
                        });

                        builder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });

                        builder.show();

                        return;
                    }

                });
                builder.setView(v);
                builder.show();

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
            Date d1, d2;
            SimpleDateFormat parser = new SimpleDateFormat("HH:mm");

            try {
                d1 = parser.parse(a.getStartTime());
                d2 = parser.parse(b.getStartTime());
            } catch (ParseException e) {
                return 0;
            }

            if (d1.getTime() < d2.getTime())
                return -1;
            else if (d1.getTime() == d1.getTime())
                return 0;
            else
                return 1;
        }
    }

    public void PreracunajEndTime(Event ev) {
        SimpleDateFormat parser = new SimpleDateFormat("HH:mm");

        Date d1 = new Date();

        try {
            d1 = parser.parse(ev.getStartTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long NewTime = d1.getTime() + (long) (ev.getDuration() * 60 * 1000);

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(NewTime);

        String HourOutput;
        int ura = cal.get(Calendar.HOUR_OF_DAY);

        if (ura < 10) {
            HourOutput = "0" + ura;
        } else {
            HourOutput = ura + "";
        }

        if (cal.get(Calendar.MINUTE) < 10) {
            ev.setEndTime(HourOutput + ":0" + cal.get(Calendar.MINUTE));
        } else {
            ev.setEndTime(HourOutput + ":" + cal.get(Calendar.MINUTE));
        }
    }

    void ScrollToPosition(int y, int m, int dom, boolean smooth) {
        if (validDates.size() == 0) {
            return;
        }

        int index = -1;
        Calendar cal2 = Calendar.getInstance();
        cal2.setTimeInMillis(new GregorianCalendar(y, m, dom).getTimeInMillis());

        int offSet = ((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                56,
                getResources().getDisplayMetrics()
        ));

        for (int i = 0; i < validDates.size(); i++) {
            Calendar cal3 = Calendar.getInstance();
            cal3.setTimeInMillis(validDates.get(i).getTime());
            cal3.setTimeInMillis((new GregorianCalendar(cal3.get(Calendar.YEAR), cal3.get(Calendar.MONTH), cal3.get(Calendar.DAY_OF_MONTH)).getTimeInMillis()));
            Long diff = cal3.getTimeInMillis() - cal2.getTimeInMillis();
            if (diff == 0) {
                index = i;
                break;
            }
        }


        if (index != -1) {
            if (smooth) {
                rec.smoothScrollToPosition(index);
            } else {
                rec.scrollToPosition(index);
            }
        } else {
            int nearest = -1;

            for (int j = 0; j < validDates.size(); j++) {
                if (cal2.getTimeInMillis() - validDates.get(j).getTime() < 0) {
                    nearest = j;
                    break;
                }
            }

            if (nearest == -1) {
                if (smooth) {
                    rec.smoothScrollToPosition(validDates.size() - 1);
                    rec.smoothScrollBy(0, offSet);
                } else {
                    rec.scrollToPosition(validDates.size() - 1);
                    rec.smoothScrollBy(0, offSet);
                }
            } else {
                if (smooth && nearest != 0) {
                    rec.smoothScrollToPosition(nearest);
                    rec.smoothScrollBy(0, offSet);
                } else if (nearest != 0) {
                    rec.scrollToPosition(nearest);
                    rec.smoothScrollBy(0, offSet);
                }
            }
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
                                isNewQuery = true;
                                refresh();
                            }
                        });
                    }
                });
    }

    public ArrayList<Event> DobiFiltirane(List<Event> evs) {
        ArrayList<Event> result = new ArrayList<>();

        for (Event ev : evs) {
            if (!ignoredGroups.contains(ev.getGroup().getSubGroup()) && (!ignoredCourses.contains(ev.getCourse()) && (!ignoredTypes.contains(ev.getType())))) {
                result.add(ev);
            }
        }

        return result;
    }
}
