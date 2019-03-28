package com.fuu.lukak.fuu;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        TinyDB tiny = new TinyDB(getApplicationContext());
        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);

        TextView smer = findViewById(R.id.smer);
        TextView letnik = findViewById(R.id.letnik);
        Button button = findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent inte = new Intent(getApplicationContext(),MainActivity.class);
                inte.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(inte);
                finish();

            }
        });

        smer.setText(tiny.getString("currpath"));
        letnik.setText(tiny.getString("letnik")+". letnik");


        this.setTitle("Nastavitve");
        String json = tiny.getString("events");
        Gson gson = new Gson();
        List<Event> res = Arrays.asList(gson.fromJson(json, Event[].class));
        List<String> cats = new ArrayList<>();
        for (int i = 0; i < res.size(); i++) {
            //če bom uporabo contains bo fiks po referenci gleu lol
            if (!cats.contains(res.get(i).group.subGroup)) {
                if (!res.get(i).group.subGroup.equals("")) {

                    cats.add(res.get(i).group.subGroup);
                }
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
