package com.urnikium.lukak.umu.Views;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.urnikium.lukak.umu.Classes.TinyDB;
import com.urnikium.lukak.umu.R;


public class Activity_About extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final TinyDB tiny = new TinyDB(getApplicationContext());

        setContentView(R.layout.activity_about);

        Spinner spin = findViewById(R.id.spinner6);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, getResources().getStringArray(R.array.Languages));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapter);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.setTitle(getResources().getString(R.string.AboutProjectTitle));

        Switch sw = findViewById(R.id.switch1);
        sw.setChecked(tiny.getBoolean("IsDarkMode"));

        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    tiny.putBoolean("IsDarkMode", true);
                    recreate();


                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    tiny.putBoolean("IsDarkMode", false);
                    recreate();

                }
                tiny.putBoolean("SettingsChanged", true);
            }
        });

        Switch goneEventsSwitch = findViewById(R.id.goneEventsSwitch);
        goneEventsSwitch.setChecked(!tiny.getBoolean("ShowGoneEvents"));

        goneEventsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                tiny.putBoolean("ShowGoneEvents", !isChecked);
                tiny.putBoolean("SettingsChanged", true);
            }
        });

        if (tiny.getString("lang").equals("sl")) {
            spin.setSelection(0);
        } else {
            spin.setSelection(1);
        }

        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        tiny.putString("lang", "sl");
                        break;

                    case 1:
                        tiny.putString("lang", "en");
                        break;
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        TextView Spletna = findViewById(R.id.button);
        Spletna.setText(Html.fromHtml("<a href=https://urnik-mb.cf/>" + getResources().getString(R.string.ContactMail) + " </a>"));
        Spletna.setMovementMethod(LinkMovementMethod.getInstance());

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
