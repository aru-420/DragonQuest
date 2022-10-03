package com.example.dragonquest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
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
import java.util.Random;

public class CharacterCreationActivity extends AppCompatActivity {
    private ActivityCharacterCreationBinding binding;

    //キャラを変える(選択)する際に使用する変数
    private String CharaNameStr[] = new String[2];
    private int CharaCount = 0;

    //ランダム変数
    int skill_ran;

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


        //データベース接続
        helper = new DatabaseHelper(this);

        //継承するスキルの数をランダムで決定
        Random rand = new Random();
        skill_ran = rand.nextInt(3) + 1;

        //初期画面を表示する
        JsonView(CharaNameStr[0]);


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

    //強くてニューゲーム
    protected Actor GrowPoint(){
        //空のキャラクターを作成
        Actor re_actor = new Actor();

        // データベースから取得する項目を設定
        String[] cols = {DBTables.CharacterTable.CHARA_SAVE_NAME, DBTables.CharacterTable.CHARA_SAVE_HP,
                DBTables.CharacterTable.CHARA_SAVE_ATK, DBTables.CharacterTable.CHARA_SAVE_DEF, DBTables.CharacterTable.CHARA_SAVE_DEX,
                DBTables.CharacterTable.CHARA_SAVE_SKILL1, DBTables.CharacterTable.CHARA_SAVE_SKILL2, DBTables.CharacterTable.CHARA_SAVE_SKILL3, DBTables.CharacterTable.CHARA_SAVE_SKILL4,
                DBTables.CharacterTable.CHARA_SAVE_TURN
        };
        //where文IDを指定
        String my_where = DBTables.CharacterTable.CHARA_SAVE_ID + " = 4";

        // 読み込みモードでデータベースをオープン
        try (SQLiteDatabase db = helper.getReadableDatabase()) {
            // データを取得するSQLを実行
            // 取得したデータがCursorオブジェクトに格納される
            //SELECT文
            Cursor cursor = db.query(DBTables.CharacterTable.TABLE_NAME, cols, my_where,
                    null, null, null, null, null);

            //一行読み込み
            if (cursor.moveToFirst()) {
                //名前がなければDB取得せずあればデータにあった内容で取得
                int magnification = cursor.getInt(9) + 10;
                switch (cursor.getString(0)){
                    case "":
                        break;
                    case "戦士":
                    case "魔法使い":
                        Skill skill1 = new Skill(cursor.getString(5));
                        Skill skill2 = new Skill(cursor.getString(6));
                        Skill skill3 = new Skill(cursor.getString(7));
                        Skill skill4 = new Skill(cursor.getString(8));
                        re_actor = new Actor(cursor.getString(0), cursor.getInt(1)*magnification/200, cursor.getInt(2)*magnification/100,
                                cursor.getInt(3)* magnification/100, cursor.getInt(4)*magnification/100,
                                skill1,skill2,skill3,skill4);
                        break;
                }
            }
        }
        return re_actor;
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

            //前回データ参照
            Actor re_actor = GrowPoint();

            //jsonからデータを引っ張る

            //name
            String name = json.getJSONObject(Name).getString("NAME");
            //hp
            int int_hp = json.getJSONObject(Name).getInt("HP") + re_actor.getHp();
            String hp = Integer.toString(int_hp);
            //atk
            int int_atk = json.getJSONObject(Name).getInt("ATK") + re_actor.getAtk();
            String atk = Integer.toString(int_atk);
            //def
            int int_def = json.getJSONObject(Name).getInt("DEF") + re_actor.getDef();
            String def = Integer.toString(int_def);
            //dex
            int int_dex = json.getJSONObject(Name).getInt("DEX") + re_actor.getDex();
            String dex = Integer.toString(int_dex);

            //ランダムスキル
            String skill1;
            String skill2 = "";
            String skill3 = "";
            String skill4 = "";

            switch (skill_ran){
                case 1:
                    //skill2
                    skill2 = re_actor.getSkill2().getSkill_name();
                    break;
                case 2:
                    //skill2
                    skill2 = re_actor.getSkill2().getSkill_name();
                    //skill3
                    skill3 = re_actor.getSkill3().getSkill_name();
                    break;
                case 3:
                    //skill2
                    skill2 = re_actor.getSkill2().getSkill_name();
                    //skill3
                    skill3 = re_actor.getSkill3().getSkill_name();
                    //skill4
                    skill4 = re_actor.getSkill4().getSkill_name();
                    break;
            }
            //skill1
            skill1 = json.getJSONObject(Name).getString("SKILL1");




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

            binding.skill3.setEnabled(true);
            binding.skill3.setBackgroundColor(Color.parseColor("#3700b3"));
            binding.skill4.setEnabled(true);
            binding.skill4.setBackgroundColor(Color.parseColor("#3700b3"));
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