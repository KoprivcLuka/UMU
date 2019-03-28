package com.fuu.lukak.fuu;

import android.app.DownloadManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.Call;
import android.util.Log;
import android.view.View;
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
import java.util.Date;
import java.util.List;


import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    Spinner AllPaths;
    OkHttpClient client = new OkHttpClient();
    Spinner Year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AllPaths = findViewById(R.id.spinner);
        try {
            RequestPathsList(getResources().getString(R.string.ServURL) + "/api/v1/urnik/groups");
        } catch (IOException e) {
            e.printStackTrace();
        }
        TinyDB tiny = new TinyDB(getApplicationContext());

        List<String> krsmlen =Arrays.asList( new String[]{"1", "2", "3"});
        int index = krsmlen.indexOf(tiny.getString("letnik"));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_spinner_item, krsmlen);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Year = findViewById(R.id.spinner2);
        Year.setAdapter(adapter);

        if (index != -1) {
            Year.setSelection(index);
        } else {
            Year.setSelection(0);
        }

        Button PathSelected = findViewById(R.id.ButtonSelectPath);
        PathSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//I mean lazji je use stegnt kr niam counterja za letnike.. 30kb razlike...
                try {
                    RequestPath(getResources().getString(R.string.ServURL) + "/api/v1/urnik/" + AllPaths.getSelectedItem() + "/" + Year.getSelectedItem());
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
                                // For the example, you can show an error dialog or a toast
                                // on the main UI thread
                                Gson gson = new Gson();
                                List<String> res = Arrays.asList(gson.fromJson(json, String[].class));
                                java.util.Collections.sort(res);
                                TinyDB tiny = new TinyDB(getApplicationContext());

                                int index = res.indexOf(tiny.getString("currpath"));
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, res);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                AllPaths.setAdapter(adapter);
                                if (index != -1) {
                                    AllPaths.setSelection(index);
                                } else {
                                    AllPaths.setSelection(0);
                                }

                                Button PathSelected = findViewById(R.id.ButtonSelectPath);
                                PathSelected.setEnabled(true);

                            }
                        });


                    }
                });
    }

    void RequestPath(String url) throws IOException {
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
                             /*   Gson gson = new Gson();
                                Type type = new TypeToken<List<Event>>() {}.getType();
                                List<Event> res =  Arrays.asList(gson.fromJson(json,Event[].class ));*/
                                TinyDB tiny = new TinyDB(getApplicationContext());
                                tiny.putString("events", json);
                                tiny.putString("currpath", AllPaths.getSelectedItem().toString());
                                tiny.putString("letnik", Year.getSelectedItem().toString());

                                startActivity(new Intent(getApplicationContext(), ViewActivity.class));
                                finish();


                            }
                        });


                    }
                });
    }
}
