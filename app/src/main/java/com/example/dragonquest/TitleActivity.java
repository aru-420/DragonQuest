package com.example.dragonquest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.example.dragonquest.databinding.ActivityTitleBinding;

public class TitleActivity extends AppCompatActivity {
    private ActivityTitleBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTitleBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //画面遷移
        Movechara();
    }

    //画面遷移
    protected void Movechara(){
        binding.titleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), Chara.class);
                startActivity(intent);
            }
        });
    }
}