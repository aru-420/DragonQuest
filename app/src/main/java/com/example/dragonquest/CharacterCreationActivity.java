package com.example.dragonquest;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import com.example.dragonquest.databinding.ActivityBattleBinding;

public class CharacterCreationActivity extends AppCompatActivity {
    private ActivityBattleBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBattleBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.btn.setText("こんにちは");
            }
        });
    }
}