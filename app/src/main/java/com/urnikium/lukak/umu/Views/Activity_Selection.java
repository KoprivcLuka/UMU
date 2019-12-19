package com.urnikium.lukak.umu.Views;


import android.content.DialogInterface;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.urnikium.lukak.umu.Classes.Faculty;
import com.urnikium.lukak.umu.Classes.GroupWYears;
import com.urnikium.lukak.umu.Adapters.Adapter_SelectionPager;
import com.urnikium.lukak.umu.Classes.TinyDB;
import com.urnikium.lukak.umu.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;


import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;


public class Activity_Selection extends AppCompatActivity {
    Spinner AllFaculties;
    OkHttpClient client = new OkHttpClient();
    ArrayList<Faculty> res;
    TinyDB tiny;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AllFaculties = findViewById(R.id.spinner3);
        tiny = new TinyDB(this);
        Switch sw = findViewById(R.id.switch1);

        sw.setChecked(tiny.getBoolean("IsDarkMode"));


        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    tiny.putBoolean("IsDarkMode",true);
                    recreate();


                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    tiny.putBoolean("IsDarkMode",false);
                    recreate();

                }
            }
        });

        RequestFaculties(getResources().getString(R.string.ServURL) + "/api/v2/faculties" + "?client=umu-mobile-prod");
        AllFaculties.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                ((TextView) adapterView.getChildAt(0)).setTextColor(getResources().getColor(R.color.textcolorprimary));
                tiny.putString("faks", AllFaculties.getSelectedItem().toString());
                tiny.putString("faksshort", res.get(AllFaculties.getSelectedItemPosition()).ShortName);
                RequestPathsList(getResources().getString(R.string.ServURL) + "/api/v2/groups/" + res.get(AllFaculties.getSelectedItemPosition()).ShortName + "/years" + "?client=umu-mobile-prod");

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if (!tiny.getBoolean("agreed")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.Warning);
            builder.setMessage(R.string.Disclaimer);
            builder.setPositiveButton(R.string.Agreed, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {

                  tiny.putBoolean("agreed",true);
                }
            });
            builder.setNegativeButton(R.string.Exit, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    System.exit(1);
                }
            });

            builder.show();
        }
    }

    void RequestPathsList(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(final okhttp3.Call call, IOException e) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                            }
                        });
                    }

                    @Override
                    public void onResponse(okhttp3.Call call, final okhttp3.Response response) throws IOException {

                        final String json = response.body().string().trim();
                        if (response.code() == 404) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    Toast toast = Toast.makeText(getApplicationContext(), R.string.NoData, Toast.LENGTH_LONG);
                                    toast.show();
                                    ViewPager pager = findViewById(R.id.pager);
                                    pager.setVisibility(View.INVISIBLE);
                                    TabLayout tabLayout = findViewById(R.id.tab_layout);
                                    tabLayout.setVisibility(View.INVISIBLE);
                                    tabLayout.removeAllTabs();
                                }
                            });

                            return;
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Gson gson = new Gson();
                                ArrayList<GroupWYears> res = new ArrayList<>(Arrays.asList(gson.fromJson(json, GroupWYears[].class)));
                                java.util.Collections.sort(res, new SortByName());
                                tiny.putString("groupswyears", json);
                                ArrayList<String> allpaths = new ArrayList<>();
                                for (GroupWYears s : res) {
                                    allpaths.add(s.Name);
                                }

                                tiny.putListString("allpaths", allpaths);

                                ViewPager pager = findViewById(R.id.pager);
                                pager.setVisibility(View.VISIBLE);
                                TabLayout tabLayout = findViewById(R.id.tab_layout);
                                tabLayout.removeAllTabs();
                                tabLayout.setVisibility(View.VISIBLE);
                                tabLayout.addTab(tabLayout.newTab().setText(R.string.ByProgramme));
                                tabLayout.addTab(tabLayout.newTab().setText(R.string.ByProfessor));
                                tabLayout.addTab(tabLayout.newTab().setText(R.string.ByCourse));
                                tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

                                final ViewPager viewPager = findViewById(R.id.pager);
                                final Adapter_SelectionPager adapter2 = new Adapter_SelectionPager
                                        (getSupportFragmentManager(), tabLayout.getTabCount());
                                viewPager.setAdapter(adapter2);
                                viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
                                tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                                    @Override
                                    public void onTabSelected(TabLayout.Tab tab) {
                                        viewPager.setCurrentItem(tab.getPosition());
                                    }

                                    @Override
                                    public void onTabUnselected(TabLayout.Tab tab) {

                                    }

                                    @Override
                                    public void onTabReselected(TabLayout.Tab tab) {

                                    }
                                });

                            }
                        });


                    }
                });
    }

    void RequestFaculties(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(final okhttp3.Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast toast = Toast.makeText(getApplicationContext(), R.string.ErrorLoad, Toast.LENGTH_LONG);
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
                                Gson gson = new Gson();
                                res = new ArrayList<>(Arrays.asList(gson.fromJson(json, Faculty[].class)));
                                java.util.Collections.sort(res, new SortByNameFac());
                                ArrayList<String> zadapter = new ArrayList<>();
                                for (Faculty f : res) {
                                    zadapter.add(f.LongName);
                                }
                                ArrayAdapter<String> adapter2 = new ArrayAdapter<>(getApplicationContext(),
                                        R.layout.spinner_item, zadapter);

                                adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                int index22 = zadapter.indexOf(tiny.getString("faks"));
                                AllFaculties.setAdapter(adapter2);
                                if (index22 != -1) {
                                    AllFaculties.setSelection(index22);
                                } else {
                                    AllFaculties.setSelection(0);
                                }
                            }
                        });
                    }
                });
    }

    class SortByName implements Comparator<GroupWYears> {
        public int compare(GroupWYears a, GroupWYears b) {
            return a.Name.toLowerCase().compareTo(b.Name.toLowerCase());
        }
    }

    class SortByNameFac implements Comparator<Faculty> {
        public int compare(Faculty a, Faculty b) {
            return a.LongName.toLowerCase().compareTo(b.LongName.toLowerCase());
        }
    }


}
