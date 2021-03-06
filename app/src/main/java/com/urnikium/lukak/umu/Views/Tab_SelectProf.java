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

public class Tab_SelectProf extends Fragment {

    OkHttpClient client = new OkHttpClient();
    List<String> profs = new ArrayList<>();
    Spinner spins;
    Button selectprof;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab_prof, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final TinyDB tiny = new TinyDB(getView().getContext());
        RequestProfs(getResources().getString(R.string.ServURL) + "/api/v2/professors/" + tiny.getString("faksshort") + "?client=umu-mobile-prod");
        spins = view.findViewById(R.id.spinner4);
        selectprof = view.findViewById(R.id.ButtonSelectProf);
        selectprof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final TinyDB tiny = new TinyDB(getView().getContext());
                tiny.putString("prof", spins.getSelectedItem().toString());
                tiny.putString("lastq", "/api/v2/schedule/professor/" + tiny.getString("faksshort") + "/" + spins.getSelectedItem().toString());
                tiny.putString("currpath", spins.getSelectedItem().toString());
                tiny.putString("letnik", "");
                startActivity(new Intent(getContext(), Activity_View.class));
                getActivity().finish();
            }
        });
    }

    void RequestProfs(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(final okhttp3.Call call, IOException e) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

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
                                Gson gson = new Gson();
                                profs = Arrays.asList(gson.fromJson(json, String[].class));
                                Collections.sort(profs);
                                ArrayAdapter<String> adapter2 = new ArrayAdapter<>(getView().getContext(),
                                        R.layout.spinner_item, profs);
                                adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                selectprof.setEnabled(true);
                                spins.setAdapter(adapter2);
                                TinyDB tiny = new TinyDB(getView().getContext());
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
}