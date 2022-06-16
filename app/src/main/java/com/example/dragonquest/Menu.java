package com.example.dragonquest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
public class Menu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_menu);

        Button tap_continue = findViewById(R.id.continue1);
        // lambda式
        tap_continue.setOnClickListener(v -> finish());

        Button tap_giveup = findViewById(R.id.giveup);
        tap_giveup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyDialog dialog = new MyDialog();
                dialog.show(getSupportFragmentManager(), "my_dialog");
            }
        });
    }
}
