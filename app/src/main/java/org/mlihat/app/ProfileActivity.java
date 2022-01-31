package org.mlihat.app;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.widget.TextView;
import android.view.animation.AnimationUtils;

public class ProfileActivity extends AppCompatActivity {
    TextView hello,name;
    Animation anim,animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        hello = findViewById(R.id.hello);
        name = findViewById(R.id.nama);
        animation = AnimationUtils.loadAnimation(this,R.anim.animate);
        anim = AnimationUtils.loadAnimation(this, R.anim.anim);
        hello.startAnimation(anim);
        name.startAnimation(animation);
    }
}