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
        //続けるボタンを押したとき
        tap_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getApplication(), Chara.class);
                startActivity(intent);
            }
        });

        Button tap_status = findViewById(R.id.status);
        //ステータスボタンを押したとき
        tap_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getApplication(), Chara.class);
                startActivity(intent);
            }
        });

        Button tap_giveup = findViewById(R.id.giveup);
        //あきらめるボタンを押したとき
        tap_giveup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyDialog dialog = new MyDialog();
                dialog.show(getSupportFragmentManager(), "my_dialog");
            }
        });
    }
}
