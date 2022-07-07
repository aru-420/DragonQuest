package com.example.dragonquest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
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

    //キャラを変える(選択)する際に使用する変数
    private String CharaNameStr[] = new String[2];
    private int CharaCount = 0;

    //データベース接続用変数
    private DatabaseHelper helper = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCharacterCreationBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //配列の初期化
        CharaNameStr[0] = "warrior";
        CharaNameStr[1] = "magician";

        //初期画面を表示する
        JsonView(CharaNameStr[0]);

        //データベース接続
        helper = new DatabaseHelper(this);


        //startボタンが押された時の処理
        binding.startbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 入力されたタイトルとコンテンツをContentValuesに設定
                // ContentValuesは、項目名と値をセットで保存できるオブジェクト
                ContentValues cv = new ContentValues();
                cv.put(DBTables.CharacterTable.CHARA_SAVE_NAME, binding.name.getText().toString());
                cv.put(DBTables.CharacterTable.CHARA_SAVE_HP, binding.txthp.getText().toString());
                cv.put(DBTables.CharacterTable.CHARA_SAVE_ATK, binding.txtatk.getText().toString());
                cv.put(DBTables.CharacterTable.CHARA_SAVE_DEF, binding.txtdef.getText().toString());
                cv.put(DBTables.CharacterTable.CHARA_SAVE_DEX, binding.txtdex.getText().toString());
                cv.put(DBTables.CharacterTable.CHARA_SAVE_SKILL1, binding.skill1.getText().toString());
                cv.put(DBTables.CharacterTable.CHARA_SAVE_SKILL2, binding.skill2.getText().toString());
                cv.put(DBTables.CharacterTable.CHARA_SAVE_SKILL3, binding.skill3.getText().toString());
                cv.put(DBTables.CharacterTable.CHARA_SAVE_SKILL4, binding.skill4.getText().toString());
                cv.put(DBTables.CharacterTable.CHARA_SAVE_STAGE, 1);
                //cv.put(CharacterTable.CHARA_SAVE_TURN, 0);

                //where文 今回はidを指定して
                String where = DBTables.CharacterTable.CHARA_SAVE_ID + " = 1";

                // 書き込みモードでデータベースをオープン
                try (SQLiteDatabase db = helper.getWritableDatabase()) {
                    //アップデート
                    db.update(DBTables.CharacterTable.TABLE_NAME, cv, where, null);
                }

                //Nurtureに移る
                Intent intent = new Intent(getApplication(), NurtureActivity.class);
                startActivity(intent);
            }
        });

        // < を押したときの処理
        binding.leftbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //次のキャラの情報をもらうために -1 をする
                CharaCount -= 1;

                //次の配列が格納されていない数値の場合 1 に戻す
                if(CharaCount < 0){
                    CharaCount = 1;
                }

                //次のキャラの画像を表示
                ImageView(CharaNameStr[CharaCount]);

                //次のキャラの情報を表示する
                JsonView(CharaNameStr[CharaCount]);

            }
        });

        // > を押したときの処理
        binding.rightbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //次のキャラの情報をもらうために +1 をする
                CharaCount += 1;

                //次の配列が格納されていない数値の場合 0 に戻す
                if(CharaCount > 1){
                    CharaCount = 0;
                }

                //次のキャラの画像を表示
                ImageView(CharaNameStr[CharaCount]);

                //次のキャラの情報を表示する
                JsonView(CharaNameStr[CharaCount]);

            }
        });
    }

    //画像の表示
    protected void ImageView(String Name){
        if(Name.equals(CharaNameStr[0])){
            //画像の変更(warrior)
            ImageView imageView  = findViewById(R.id.Character_Image);
            imageView.setImageResource(R.drawable.warrior);
        }
        else if(Name.equals(CharaNameStr[1])){
            //画像の変更(magician)
            ImageView imageView  = findViewById(R.id.Character_Image);
            imageView.setImageResource(R.drawable.magician);
        }
    }

    //JSONのデータを表示
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

            //スキルのないボタンを押せなくして灰色にする
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