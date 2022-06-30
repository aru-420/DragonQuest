package com.example.dragonquest;

import androidx.appcompat.app.AppCompatActivity;

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

        
    }
}