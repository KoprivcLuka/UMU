package com.urnikium.lukak.umu.Views;

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
import com.urnikium.lukak.umu.Classes.GroupWYears;
import com.urnikium.lukak.umu.Classes.TinyDB;
import com.urnikium.lukak.umu.R;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Tab_SelectProgramme extends Fragment {
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
        Gson gson = new Gson();
        final List<GroupWYears> groupWYears = Arrays.asList(gson.fromJson(tiny.getString("groupswyears"), GroupWYears[].class));
        Collections.sort(groupWYears, new SortByName());
        for (GroupWYears e : groupWYears) {
            res.add(e.Name);
        }
        int index = res.indexOf(tiny.getString("currpath"));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(view.getContext(), R.layout.spinner_item, res);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        AllPaths = view.findViewById(R.id.spinner);
        Year = view.findViewById(R.id.spinner2);
        AllPaths.setAdapter(adapter);
        AllPaths.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                List<String> krsmlen = new ArrayList<>();
                for (int s : groupWYears.get(i).Years) {
                    krsmlen.add(s + "");
                }
                Collections.sort(krsmlen);
                ArrayAdapter<String> adapter2 = new ArrayAdapter<>(getContext(),
                        R.layout.spinner_item, krsmlen);
                adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                Year.setAdapter(adapter2);
                Year.setSelection(0);
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
                TinyDB tiny = new TinyDB(getView().getContext());
                tiny.putString("lastq","/api/v2/schedule/faculty/" + tiny.getString("faksshort") + "/" + AllPaths.getSelectedItem() + "/" + Year.getSelectedItem());
                tiny.putString("currpath", AllPaths.getSelectedItem().toString());
                tiny.putString("letnik", Year.getSelectedItem().toString());
                startActivity(new Intent(getView().getContext(), Activity_View.class));
                getActivity().finish();
            }
        });
    }

    class SortByName implements Comparator<GroupWYears> {
        public int compare(GroupWYears a, GroupWYears b) {
            return a.Name.compareTo(b.Name);
        }
    }
}