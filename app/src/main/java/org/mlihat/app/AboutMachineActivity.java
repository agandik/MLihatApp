package org.mlihat.app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class AboutMachineActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_machine);
        LinearLayout pengertian = (LinearLayout) findViewById(R.id.pengertian);
        LinearLayout sejarah = (LinearLayout) findViewById(R.id.sejarah);
        LinearLayout penerapan = (LinearLayout) findViewById(R.id.penerapan);
        ScrollView textpengertian = (ScrollView) findViewById(R.id.textpengertian);
        ScrollView textsejarah = (ScrollView) findViewById(R.id.textsejarah);
        ScrollView textpenerapan = (ScrollView) findViewById(R.id.textpenerapan);

        textsejarah.setVisibility(View.INVISIBLE);
        textpenerapan.setVisibility(View.INVISIBLE);

        pengertian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textpengertian.setVisibility(View.VISIBLE);
                textsejarah.setVisibility(View.INVISIBLE);
                textpenerapan.setVisibility(View.INVISIBLE);
            }
        });

        sejarah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textsejarah.setVisibility(View.VISIBLE);
                textpengertian.setVisibility(View.INVISIBLE);
                textpenerapan.setVisibility(View.INVISIBLE);
            }
        });

        penerapan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textpenerapan.setVisibility(View.VISIBLE);
                textsejarah.setVisibility(View.INVISIBLE);
                textpengertian.setVisibility(View.INVISIBLE);
            }
        });

    }
}