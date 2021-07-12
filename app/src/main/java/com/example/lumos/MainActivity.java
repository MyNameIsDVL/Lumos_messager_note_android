package com.example.lumos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static int SECOND_TO_LOGIN_PAGE = 5000;

    Animation top_anim, bottom_anim, left_anim;
    ImageView image, imageBgTop;
    TextView project_title, by_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Animation sets
        top_anim = AnimationUtils.loadAnimation(this, R.anim.main_page_top_animation);
        bottom_anim = AnimationUtils.loadAnimation(this, R.anim.main_page_bottom_animation);
        left_anim = AnimationUtils.loadAnimation(this, R.anim.main_page_left_animation);

        // Animation hooks for controls
        image = findViewById(R.id.imageView);
        project_title = findViewById(R.id.textView);
        by_text = findViewById(R.id.textView2);
        imageBgTop = findViewById(R.id.imageBgTop);

        // Set animations to controls
        imageBgTop.setAnimation(left_anim);
        image.setAnimation(top_anim);
        project_title.setAnimation(bottom_anim);
        by_text.setAnimation(bottom_anim);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        }, SECOND_TO_LOGIN_PAGE);
    }
}