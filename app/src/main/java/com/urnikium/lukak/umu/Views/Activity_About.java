package com.urnikium.lukak.umu.Views;

import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
        setContentView(R.layout.activity_about);
        Spinner spin = findViewById(R.id.spinner6);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.simple_spinner_item, getResources().getStringArray(R.array.Languages));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final TinyDB tiny = new TinyDB(getApplicationContext());
        spin.setAdapter(adapter);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.setTitle(getResources().getString(R.string.AboutProjectTitle));


        Switch sw = findViewById(R.id.switch1);
        sw.setChecked(tiny.getBoolean("nightmode"));

        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                tiny.putBoolean("nightmode",isChecked);
                UiModeManager uiManager = (UiModeManager) getSystemService(Context.UI_MODE_SERVICE);
                if(isChecked)
                {
                    uiManager.setNightMode(UiModeManager.MODE_NIGHT_YES);
                }
                else
                {
                    uiManager.setNightMode(UiModeManager.MODE_NIGHT_NO);
                }
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


        Button SendEmail = findViewById(R.id.button);
        SendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/html");
                intent.putExtra(Intent.EXTRA_EMAIL, R.string.ContactMail);
                startActivity(intent);
            }
        });
        TextView tx = findViewById(R.id.textView8);
        tx.setText(Html.fromHtml("<a href=https://feri-urnik.si:8080/privacy>" + getResources().getString(R.string.PrivacyPolicy) + " </a>"));
        tx.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
