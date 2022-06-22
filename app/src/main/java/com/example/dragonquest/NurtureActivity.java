package com.example.dragonquest;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import com.example.dragonquest.databinding.ActivityNurtureBinding;

public class NurtureActivity extends AppCompatActivity {
    private ActivityNurtureBinding binding;

    private int turncount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNurtureBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        //選択肢1の処理
        binding.Choices1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                turncount += 1;
                binding.turnCount.setText(turncount + "ターン");
            }
        });

        //選択肢2の処理
        binding.Choices2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                turncount += 1;
                binding.turnCount.setText(turncount + "ターン");
            }
        });



    }
}