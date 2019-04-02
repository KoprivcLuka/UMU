package com.fuu.lukak.fuu;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.gson.Gson;

import java.io.IOException;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class tab_prof extends Fragment {

    OkHttpClient client = new OkHttpClient();
    List<String> profs = new ArrayList<>();
    Spinner spins;
    Button selectprof;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            RequestProfs(getResources().getString(R.string.ServURL) + "/api/v1/urnik/professors");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return inflater.inflate(R.layout.fragment_tab_prof, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final TinyDB tiny = new TinyDB(view.getContext().getApplicationContext());
        String odmakni = tiny.getString("prof");

        /*tiny.putString("events", json);
        startActivity(new Intent(getContext(), ViewActivity.class)); */

        spins = view.findViewById(R.id.spinner4);
        selectprof = view.findViewById(R.id.ButtonSelectProf);
        selectprof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final TinyDB tiny = new TinyDB(view.getContext().getApplicationContext());
                tiny.putString("prof", spins.getSelectedItem().toString());
                try {
                    RequestProfsEvents(getResources().getString(R.string.ServURL) + "/api/v1/urnik/professor/" + spins.getSelectedItem().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    void RequestProfs(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(final okhttp3.Call call, IOException e) {
                        // Error
                        getActivity().runOnUiThread(new Runnable() {
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
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // For the example, you can show an error dialog or a toast
                                // on the main UI thread
                                Gson gson = new Gson();
                                profs = Arrays.asList(gson.fromJson(json, String[].class));
                                Collections.sort(profs);
                                ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getContext(),
                                        android.R.layout.simple_spinner_item, profs);

                                adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


                                selectprof.setEnabled(true);
                                spins.setAdapter(adapter2);
                                TinyDB tiny = new TinyDB(getContext().getApplicationContext());
                                int index22 = profs.indexOf(tiny.getString("prof"));
                                if (index22 != -1) {
                                    spins.setSelection(index22);
                                } else {
                                    spins.setSelection(0);
                                }



                            }
                        });


                    }
                });
    }

    void RequestProfsEvents(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(final okhttp3.Call call, IOException e) {
                        // Error
                        getActivity().runOnUiThread(new Runnable() {
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
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // For the example, you can show an error dialog or a toast
                                // on the main UI thread
                                TinyDB tiny = new TinyDB(getContext());
                                tiny.putString("events", json);
                                tiny.putString("currpath", spins.getSelectedItem().toString());
                                tiny.putString("letnik", "");
                                startActivity(new Intent(getContext(), ViewActivity.class));

                            }
                        });


                    }
                });
    }
}