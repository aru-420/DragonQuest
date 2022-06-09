package com.example.dragonquest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import com.example.dragonquest.databinding.ActivityBattleBinding;

public class BattleActivity extends AppCompatActivity {
    private ActivityBattleBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBattleBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //メッセージ非表示
        binding.battleMessage.setVisibility(View.INVISIBLE);
        //メッセージのクリック処理有効化
        binding.battleMessage.setClickable(true);

        //左上ボタンクリック時の処理
        binding.skill1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //メッセージの表示
                binding.battleMessage.setVisibility(View.VISIBLE);
            }
        });

        binding.battleMessage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //メッセージの非表示
                binding.battleMessage.setVisibility(View.INVISIBLE);
            }

        });
    }


}