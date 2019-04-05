package com.fuu.lukak.fuu;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.gson.Gson;

import java.io.IOException;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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


        final TinyDB tiny = new TinyDB(view.getContext());
        ArrayList<String> res = new ArrayList<>();
        int index = res.indexOf(tiny.getString("currpath"));
        Gson gson = new Gson();
        final List<GroupWYears> groupWYears =Arrays.asList(gson.fromJson(tiny.getString("groupswyears"), GroupWYears[].class));
        ArrayList<String> leta = new ArrayList<>();

        Collections.sort(groupWYears,new SortByName());
        for (GroupWYears e : groupWYears)
        {
            res.add(e.Name);


        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item, res);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        AllPaths = view.findViewById(R.id.spinner);
        Year = view.findViewById(R.id.spinner2);
        AllPaths.setAdapter(adapter);

        AllPaths.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                List<String> krsmlen = new ArrayList<>();
                //REEEE Ni sortiran
                for(int s : groupWYears.get(i).Years)
                {
                    krsmlen.add(s+"");
                }
                Collections.sort(krsmlen);
                int index22 = krsmlen.indexOf(tiny.getString("letnik"));
                ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getContext(),
                        android.R.layout.simple_spinner_item, krsmlen);
                adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                Year.setAdapter(adapter2);

                if (index22 != -1) {
                    Year.setSelection(index22);
                } else {
                    Year.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if (index != -1) {
            AllPaths.setSelection(index);
        } else {
            AllPaths.setSelection(0);
        }

        Button PathSelected = view.findViewById(R.id.ButtonSelectPath);
        PathSelected.setEnabled(true);



        PathSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//I mean lazji je use stegnt kr niam counterja za letnike.. 30kb razlike...
                try {
                    RequestPath(getResources().getString(R.string.ServURL) + "/api/v2/urnik/" + tiny.getString("faksshort") + "/" + AllPaths.getSelectedItem() + "/" + Year.getSelectedItem());
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

                                startActivity(new Intent(getContext(), weekView.class));


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