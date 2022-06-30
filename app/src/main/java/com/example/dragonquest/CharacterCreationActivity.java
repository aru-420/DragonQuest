package com.example.dragonquest;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.JsonReader;
import android.view.View;
import android.widget.ImageView;
import com.example.dragonquest.databinding.ActivityCharacterCreationBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CharacterCreationActivity extends AppCompatActivity {
    private ActivityCharacterCreationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCharacterCreationBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        JsonView("charaID1");


        //startボタンが押された時の処理
        binding.startbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Nurtureに移る
                Intent intent = new Intent(getApplication(), NurtureActivity.class);
                startActivity(intent);
            }
        });

        // < を押したときの処理
        binding.leftbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //画像の変更（仮）
                ImageView imageView  = findViewById(R.id.Character_Image);
                imageView.setImageResource(R.drawable.warrior);
            }
        });

        // > を押したときの処理
        binding.rightbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //画像の変更（仮）
                ImageView imageView  = findViewById(R.id.Character_Image);
                imageView.setImageResource(R.drawable.dragon);
            }
        });
    }

    protected void JsonView(String Name){
        try {
            //アセットマネージャー
            AssetManager assetManager = getResources().getAssets();
            // jsonを取得する
            //ファイル名を指定
            InputStream inputStream = assetManager.open("chara.json");
            //バッファに読み込み
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            //jsonオブジェクトの宣言
            JSONObject json = null;

            //読み込んだファイルをString型にする
            String data = "";
            //読み込んだファイルを一行読み込み
            String str = bufferedReader.readLine();
            while(str != null){
                //一行ずつ書き込んでいく
                data += str;
                //読み込んだファイルを一行読み込み
                str = bufferedReader.readLine();
            }

            //jsonに変換
            json = new JSONObject(data);

            inputStream.close();
            bufferedReader.close();

            //jsonからデータを引っ張る
            /*skill.jsonからskill_nameのskill_effectを呼び出している
             * */

            //name
            String name = json.getJSONObject(Name).getString("NAME");
            //hp
            int int_hp = json.getJSONObject(Name).getInt("HP");
            String hp = Integer.toString(int_hp);
            //atk
            int int_atk = json.getJSONObject(Name).getInt("ATK");
            String atk = Integer.toString(int_atk);
            //def
            int int_def = json.getJSONObject(Name).getInt("DEF");
            String def = Integer.toString(int_def);
            //dex
            int int_dex = json.getJSONObject(Name).getInt("DEX");
            String dex = Integer.toString(int_dex);

            //skill1
            String skill1 = json.getJSONObject(Name).getString("SKILL1");
            //skill2
            String skill2 = json.getJSONObject(Name).getString("SKILL2");
            //skill3
            String skill3 = json.getJSONObject(Name).getString("SKILL3");
            //skill4
            String skill4 = json.getJSONObject(Name).getString("SKILL4");


            //テキストにJSONから得た情報を表示(名前とステータス)
            binding.name.setText(name);
            binding.txthp.setText(hp);
            binding.txtatk.setText(atk);
            binding.txtdef.setText(def);
            binding.txtdex.setText(dex);
            //テキスト(ボタン)にJSONから得た情報を表示(スキル)
            binding.skill1.setText(skill1);
            binding.skill2.setText(skill2);
            binding.skill3.setText(skill3);
            binding.skill4.setText(skill4);

            if(skill1.equals("")){
                binding.skill1.setEnabled(false);
                binding.skill1.setBackgroundColor(Color.GRAY);
            }
            if(skill2.equals("")){
                binding.skill2.setEnabled(false);
                binding.skill2.setBackgroundColor(Color.GRAY);
            }
            if(skill3.equals("")){
                binding.skill3.setEnabled(false);
                binding.skill3.setBackgroundColor(Color.GRAY);
            }
            if(skill4.equals("")){
                binding.skill4.setEnabled(false);
                binding.skill4.setBackgroundColor(Color.GRAY);
            }


        }catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }
}