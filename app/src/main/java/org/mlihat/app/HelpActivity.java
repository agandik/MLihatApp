package org.mlihat.app;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        TextView mendeteksi = (TextView) findViewById(R.id.mendeteksi_objek);
        TextView pencarian = (TextView) findViewById(R.id.pencarian_objek);
        TextView model = (TextView) findViewById(R.id.model_objek);
        TextView aboutMachine = (TextView) findViewById(R.id.aboutMachine);
        TextView AboutMe = (TextView) findViewById(R.id.aboutMe);
        TextView contact = (TextView) findViewById(R.id.contact);
        TextView catatan = (TextView) findViewById(R.id.catatan);
        ScrollView scrollView = (ScrollView) findViewById(R.id.scroll);
        TextView scrl_mendeteksi = (TextView) findViewById(R.id.scrl_mendeteksi_objek);
        TextView scrl_pencarian = (TextView) findViewById(R.id.scrl_pencarian_objek);
        TextView scrl_model = (TextView) findViewById(R.id.scrl_model_objek);
        TextView scrl_aboutMachine = (TextView) findViewById(R.id.scrl_aboutMachine);
        TextView scrl_AboutMe = (TextView) findViewById(R.id.scrl_aboutMe);
        TextView scrl_contact = (TextView) findViewById(R.id.scrl_contact);
        TextView scrl_catatan = (TextView) findViewById(R.id.scrl_catatan);


        mendeteksi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //scrollView.smoothScrollTo(0, (int)pindahkesini.getY());
                ObjectAnimator.ofInt(scrollView, "scrollY",  (int)scrl_mendeteksi.getY()).setDuration(1000).start();
                }
        });

        pencarian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //scrollView.smoothScrollTo(0, (int)pindahkesini.getY());
                ObjectAnimator.ofInt(scrollView, "scrollY",  (int)scrl_pencarian.getY()).setDuration(1000).start();
            }
        });

        model.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //scrollView.smoothScrollTo(0, (int)pindahkesini.getY());
                ObjectAnimator.ofInt(scrollView, "scrollY",  (int)scrl_model.getY()).setDuration(1000).start();
            }
        });

        aboutMachine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //scrollView.smoothScrollTo(0, (int)pindahkesini.getY());
                ObjectAnimator.ofInt(scrollView, "scrollY",  (int)scrl_aboutMachine.getY()).setDuration(1000).start();
            }
        });

        AboutMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //scrollView.smoothScrollTo(0, (int)pindahkesini.getY());
                ObjectAnimator.ofInt(scrollView, "scrollY",  (int)scrl_AboutMe.getY()).setDuration(1000).start();
            }
        });

        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //scrollView.smoothScrollTo(0, (int)pindahkesini.getY());
                ObjectAnimator.ofInt(scrollView, "scrollY",  (int)scrl_contact.getY()).setDuration(1000).start();
            }
        });

        catatan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //scrollView.smoothScrollTo(0, (int)pindahkesini.getY());
                ObjectAnimator.ofInt(scrollView, "scrollY",  (int)scrl_catatan.getY()).setDuration(1000).start();
            }
        });

    }
}