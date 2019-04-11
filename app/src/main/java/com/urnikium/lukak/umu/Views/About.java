package com.urnikium.lukak.umu.Views;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.urnikium.lukak.umu.R;

public class About extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        this.setTitle("O projektu");
        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);

        Button SendEmail = findViewById(R.id.button);
        SendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/html");
                intent.putExtra(Intent.EXTRA_EMAIL, "urnik.um@gmail.com" );

                startActivity(intent);
            }
        });
        TextView tx = findViewById(R.id.textView8);
        tx.setText(Html.fromHtml("<a href=https://feri-urnik.si:8080/privacy> Politika zasebnosti </a>"));
        tx.setMovementMethod(LinkMovementMethod.getInstance());


    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
