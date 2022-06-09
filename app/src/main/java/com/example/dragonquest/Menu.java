package com.example.dragonquest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
public class Menu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_menu);

        Button returnButton = findViewById(R.id.button2);
        // lambda式
        returnButton.setOnClickListener(v -> finish());

    }
}