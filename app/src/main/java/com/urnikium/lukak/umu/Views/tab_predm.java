package com.urnikium.lukak.umu.Views;

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
import com.urnikium.lukak.umu.Classes.TinyDB;
import com.urnikium.lukak.umu.R;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class tab_predm extends Fragment {

    List<String> predmeti = new ArrayList<>();
    OkHttpClient client = new OkHttpClient();
    Spinner spins;
    Button selectpred;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab_predm, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        TinyDB tiny = new TinyDB(getContext());
        RequestPreds(getResources().getString(R.string.ServURL) + "/api/v2/urnik/" + tiny.getString("faksshort") + "/courses");
        super.onViewCreated(view, savedInstanceState);

    }

    void RequestPreds(String url) {
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
                        if (getActivity() == null) {
                            return;
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // For the example, you can show an error dialog or a toast
                                // on the main UI thread
                                Gson gson = new Gson();
                                predmeti = Arrays.asList(gson.fromJson(json, String[].class));
                                Collections.sort(predmeti);
                                ArrayAdapter<String> adapter2 = new ArrayAdapter<>(getView().getContext(),
                                        android.R.layout.simple_spinner_item, predmeti);
                                final TinyDB tiny = new TinyDB(getView().getContext());
                                adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spins = getView().findViewById(R.id.spinner5);
                                selectpred = getView().findViewById(R.id.ButtonSelectPred);
                                selectpred.setEnabled(true);

                                selectpred.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        tiny.putString("predm", spins.getSelectedItem().toString());
                                        tiny.putString("lastq", "/api/v2/urnik/" + tiny.getString("faksshort") + "/course/" + spins.getSelectedItem().toString());
                                        tiny.putString("currpath", spins.getSelectedItem().toString());
                                        tiny.putString("letnik", "");
                                        startActivity(new Intent(getView().getContext(), weekView.class));

                                    }
                                });

                                spins.setAdapter(adapter2);
                                int index22 = predmeti.indexOf(tiny.getString("predm"));
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


}