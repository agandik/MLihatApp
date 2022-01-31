package org.mlihat.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MenuActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        CardView help = (CardView) findViewById(R.id.Help_Me);
        CardView hewan = (CardView) findViewById(R.id.hewan);
        CardView tumbuhan = (CardView) findViewById(R.id.tumbuhan);
        CardView contact_me = (CardView) findViewById(R.id.Contact_me);
        CardView about_me = (CardView) findViewById(R.id.about_me);
        CardView machinelearning = (CardView) findViewById(R.id.machinelearning);

        hewan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pilih="hewan";
                Intent PindahHewan = new Intent(MenuActivity.this, ImageClassifier.class);
                PindahHewan.putExtra("Pilih",pilih);
                startActivity(PindahHewan);
            }
        });

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent PindahHelp = new Intent(MenuActivity.this,HelpActivity.class);
                startActivity(PindahHelp);
            }
        });

        tumbuhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pilih = "bunga";
                Intent PindahTumbuhan = new Intent(MenuActivity.this, ImageClassifier.class);
                PindahTumbuhan.putExtra("Pilih",pilih);
                PindahTumbuhan.putExtra("Jenis", "bunga");
                startActivity(PindahTumbuhan);
            }
        });

        contact_me.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMail = new Intent(Intent.ACTION_SEND);
                intentMail.setType("message/rfc822");
                intentMail.setPackage("com.google.android.gm");
                intentMail.putExtra(Intent.EXTRA_EMAIL, new String[]{
                        "dikiagustiann@email.com" });
                if (intentMail.resolveActivity(getPackageManager())!=null)
                    startActivity(intentMail);
                else
                    Toast.makeText(MenuActivity.this,"Aplikasi Gmail tidak terinstall",Toast.LENGTH_SHORT).show();

            }
        });

        about_me.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent about_me = new Intent(MenuActivity.this,ProfileActivity.class);
                startActivity(about_me);
            }
        });

        machinelearning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent machinelearning = new Intent(MenuActivity.this,AboutMachineActivity.class);
                startActivity(machinelearning);
            }
        });
    }
}