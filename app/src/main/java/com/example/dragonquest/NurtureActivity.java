package com.example.dragonquest;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import com.example.dragonquest.databinding.ActivityNurtureBinding;

public class NurtureActivity extends AppCompatActivity {
    private ActivityNurtureBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNurtureBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

    }
}