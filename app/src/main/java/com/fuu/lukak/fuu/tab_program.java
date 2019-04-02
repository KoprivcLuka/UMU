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
import java.util.List;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class tab_program extends Fragment {
    OkHttpClient client = new OkHttpClient();
    Spinner Year;
    Spinner AllPaths;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab_program, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        TinyDB tiny = new TinyDB(view.getContext());
        ArrayList<String> res = tiny.getListString("allpaths");
        int index = res.indexOf(tiny.getString("currpath"));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item, res);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        AllPaths = view.findViewById(R.id.spinner);
        AllPaths.setAdapter(adapter);

        if (index != -1) {
            AllPaths.setSelection(index);
        } else {
            AllPaths.setSelection(0);
        }

        Button PathSelected = view.findViewById(R.id.ButtonSelectPath);
        PathSelected.setEnabled(true);

        List<String> krsmlen = Arrays.asList(new String[]{"1", "2", "3"});
        int index22 = krsmlen.indexOf(tiny.getString("letnik"));
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, krsmlen);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Year = view.findViewById(R.id.spinner2);
        Year.setAdapter(adapter2);

        if (index22 != -1) {
            Year.setSelection(index22);
        } else {
            Year.setSelection(0);
        }

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

    void RequestPath(String url) throws IOException {
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
                               /* Gson gson = new Gson();
                                List<Event> res = Arrays.asList(gson.fromJson(json, Event[].class)); */
                                TinyDB tiny = new TinyDB(getContext());
                                tiny.putString("events", json);
                                tiny.putString("currpath", AllPaths.getSelectedItem().toString());
                                tiny.putString("letnik", Year.getSelectedItem().toString());

                                startActivity(new Intent(getContext(), ViewActivity.class));


                            }
                        });


                    }
                });
    }
}