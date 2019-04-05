package com.fuu.lukak.fuu;

import android.app.DownloadManager;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.Call;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    Spinner AllFacs;
    OkHttpClient client = new OkHttpClient();
    Spinner Year;
    ArrayList<Faculty> res;

    //TODO OnFail / Loading....
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AllFacs = findViewById(R.id.spinner3);

        try {
            RequestFaculties(getResources().getString(R.string.ServURL) + "/api/v2/urnik/faculties");
        } catch (IOException e) {
            e.printStackTrace();
        }
        AllFacs.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                TinyDB tiny = new TinyDB(getApplicationContext());
                tiny.putString("faks", AllFacs.getSelectedItem().toString());
                tiny.putString("faksshort", res.get(AllFacs.getSelectedItemPosition()).ShortName);
                try {
                    RequestPathsList(getResources().getString(R.string.ServURL) + "/api/v2/urnik/" + res.get(AllFacs.getSelectedItemPosition()).ShortName + "/groups/years");
                } catch (IOException e) {
                    e.printStackTrace();

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    void RequestPathsList(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(final okhttp3.Call call, IOException e) {
                        // Error

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // For the example, you can show an error dialog or a toast
                                // on the main UI thread

                            }
                        });
                    }

                    @Override
                    public void onResponse(okhttp3.Call call, final okhttp3.Response response) throws IOException {

                        final String json = response.body().string();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //TODO prestavi v tab_predm
                                // For the example, you can show an error dialog or a toast
                                // on the main UI thread
                                Gson gson = new Gson();
                                ArrayList<GroupWYears> res = new ArrayList<>(Arrays.asList(gson.fromJson(json, GroupWYears[].class)));
                                java.util.Collections.sort(res, new SortByName());
                                TinyDB tiny = new TinyDB(getApplicationContext());
                                tiny.putString("groupswyears",json);
                                ArrayList<String> allpaths = new ArrayList<>();
                                for (GroupWYears s : res) {
                                    allpaths.add(s.Name);
                                }

                                tiny.putListString("allpaths", allpaths);
                                TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
                                tabLayout.removeAllTabs();
                                tabLayout.addTab(tabLayout.newTab().setText("Po programu"));
                                tabLayout.addTab(tabLayout.newTab().setText("Po profesorju"));
                                tabLayout.addTab(tabLayout.newTab().setText("Po predmetu"));

                                tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

                                final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
                                final PagerAdapter adapter2 = new PagerAdapter
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

    void RequestFaculties(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(final okhttp3.Call call, IOException e) {
                        // Error

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // For the example, you can show an error dialog or a toast
                                // on the main UI thread

                            }
                        });
                    }

                    @Override
                    public void onResponse(okhttp3.Call call, final okhttp3.Response response) throws IOException {

                        final String json = response.body().string();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // For the example, you can show an error dialog or a toast
                                // on the main UI thread
                                Gson gson = new Gson();
                                res = new ArrayList<>(Arrays.asList(gson.fromJson(json, Faculty[].class)));
                                TinyDB tiny = new TinyDB(getApplicationContext());
                                ArrayList<String> zadapter = new ArrayList<>();
                                for (Faculty f : res) {
                                    zadapter.add(f.LongName);
                                }
                                ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getApplicationContext(),
                                        android.R.layout.simple_spinner_item, zadapter);

                                adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                int index22 = zadapter.indexOf(tiny.getString("faks"));
                                AllFacs.setAdapter(adapter2);
                                if (index22 != -1) {
                                    AllFacs.setSelection(index22);
                                } else {
                                    AllFacs.setSelection(0);
                                }


                            }
                        });


                    }
                });
    }

    class SortByName implements Comparator<GroupWYears> {
        public int compare(GroupWYears a, GroupWYears b) {

            return a.Name.compareTo(b.Name);
        }
    }
}
