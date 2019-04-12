package com.urnikium.lukak.umu.Views;


import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.urnikium.lukak.umu.Classes.Faculty;
import com.urnikium.lukak.umu.Classes.GroupWYears;
import com.urnikium.lukak.umu.Adapters.PagerAdapter;
import com.urnikium.lukak.umu.Classes.TinyDB;
import com.urnikium.lukak.umu.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;


import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;


public class MainActivity extends AppCompatActivity {
    Spinner AllFaculties;
    OkHttpClient client = new OkHttpClient();
    ArrayList<Faculty> res;
    TinyDB tiny;

    //TODO OnFail / Loading....
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AllFaculties = findViewById(R.id.spinner3);
        tiny = new TinyDB(this);

        RequestFaculties(getResources().getString(R.string.ServURL) + "/api/v2/urnik/faculties");
        AllFaculties.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                tiny.putString("faks", AllFaculties.getSelectedItem().toString());
                tiny.putString("faksshort", res.get(AllFaculties.getSelectedItemPosition()).ShortName);
                RequestPathsList(getResources().getString(R.string.ServURL) + "/api/v2/urnik/" + res.get(AllFaculties.getSelectedItemPosition()).ShortName + "/groups/years");

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

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

                        final String json = response.body().string();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //TODO prestavi v tab_predm
                                Gson gson = new Gson();
                                ArrayList<GroupWYears> res = new ArrayList<>(Arrays.asList(gson.fromJson(json, GroupWYears[].class)));
                                java.util.Collections.sort(res, new SortByName());
                                tiny.putString("groupswyears", json);
                                ArrayList<String> allpaths = new ArrayList<>();
                                for (GroupWYears s : res) {
                                    allpaths.add(s.Name);
                                }

                                tiny.putListString("allpaths", allpaths);
                                TabLayout tabLayout = findViewById(R.id.tab_layout);
                                tabLayout.removeAllTabs();
                                tabLayout.addTab(tabLayout.newTab().setText(R.string.ByProgramme));
                                tabLayout.addTab(tabLayout.newTab().setText(R.string.ByProfessor));
                                tabLayout.addTab(tabLayout.newTab().setText(R.string.ByCourse));

                                tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

                                final ViewPager viewPager = findViewById(R.id.pager);
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

    void RequestFaculties(String url) {
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
                                java.util.Collections.sort(res, new SortByNameFac());
                                ArrayList<String> zadapter = new ArrayList<>();
                                for (Faculty f : res) {
                                    zadapter.add(f.LongName);
                                }
                                ArrayAdapter<String> adapter2 = new ArrayAdapter<>(getApplicationContext(),
                                        android.R.layout.simple_spinner_item, zadapter);

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

            return a.Name.compareTo(b.Name);
        }
    }

    class SortByNameFac implements Comparator<Faculty> {
        public int compare(Faculty a, Faculty b) {

            return a.LongName.compareTo(b.LongName);
        }
    }
}