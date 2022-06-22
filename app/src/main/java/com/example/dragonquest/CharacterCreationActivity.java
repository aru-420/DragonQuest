package com.example.dragonquest;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import com.example.dragonquest.databinding.ActivityCharacterCreationBinding;

public class CharacterCreationActivity extends AppCompatActivity {
    private ActivityCharacterCreationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCharacterCreationBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.startbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), NurtureActivity.class);
                startActivity(intent);
            }
        });

        // < を押したときの処理
        binding.leftbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView imageView  = findViewById(R.id.Character_Image);
                imageView.setImageResource(R.drawable.warrior);
            }
        });

        // > を押したときの処理
        binding.rightbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView imageView  = findViewById(R.id.Character_Image);
                imageView.setImageResource(R.drawable.dragon);
            }
        });

    }
}