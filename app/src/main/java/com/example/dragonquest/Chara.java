package com.example.dragonquest;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.dragonquest.databinding.ActivityBattleBinding;
import com.example.dragonquest.databinding.ActivityCharaBinding;
import com.example.dragonquest.databinding.ActivityMainBinding;

public class Chara extends AppCompatActivity {
    private ActivityCharaBinding binding;

    //データベース接続用変数
    private DatabaseHelper helper = null;
    protected String chara_name;
    protected int turn, battle_hp,stage_num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCharaBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        binding.background.setVisibility(View.INVISIBLE);

        // ヘルパーを準備
        helper = new DatabaseHelper(this);

        // データベースから取得する項目を設定
        String[] cols = {DBTables.CharacterTable.CHARA_SAVE_NAME, DBTables.CharacterTable.CHARA_SAVE_HP,
                DBTables.CharacterTable.CHARA_SAVE_ATK, DBTables.CharacterTable.CHARA_SAVE_DEF, DBTables.CharacterTable.CHARA_SAVE_DEX,
                DBTables.CharacterTable.CHARA_SAVE_SKILL1, DBTables.CharacterTable.CHARA_SAVE_SKILL2, DBTables.CharacterTable.CHARA_SAVE_SKILL3, DBTables.CharacterTable.CHARA_SAVE_SKILL4,
                DBTables.CharacterTable.CHARA_SAVE_TURN, DBTables.CharacterTable.CHARA_SAVE_STAGE
        };
        //where文 リザルトデータ
        String my_where = DBTables.CharacterTable.CHARA_SAVE_ID + " = 1";

        // 読み込みモードでデータベースをオープン
        try (SQLiteDatabase db = helper.getReadableDatabase()) {
            // データを取得するSQLを実行
            // 取得したデータがCursorオブジェクトに格納される
            //SELECT文
            Cursor cursor = db.query(DBTables.CharacterTable.TABLE_NAME, cols, my_where,
                    null, null, null, null, null);
            //一行読み込み
            if (cursor.moveToFirst()){
                //キャラの名前とターン数取得
                chara_name = cursor.getString(0);
                turn = cursor.getInt(9);
                stage_num = cursor.getInt(10);
            }
            if (turn >= 10){
                my_where = DBTables.CharacterTable.CHARA_SAVE_ID + " = 3";
                cursor = db.query(DBTables.CharacterTable.TABLE_NAME, cols, my_where,
                        null, null, null, null, null);
                //一行読み込み
                if (cursor.moveToFirst()){
                    //戦闘キャラのHP
                    battle_hp = cursor.getInt(1);
                }
            }
        }

        //名前表示
        binding.charaName.setText(chara_name);
        //画像処理
        ImageView image = binding.imageChara;
        if (chara_name.equals("戦士")){
            image.setImageResource(R.drawable.warrior);
        }else if (chara_name.equals("魔法使い")){
            image.setImageResource(R.drawable.magician);
        }else {
            image.setVisibility(View.INVISIBLE);
            binding.background.setVisibility(View.VISIBLE);
            binding.charaName.setText("");
        }

        //背景処理
        if (stage_num == 1){
            binding.charaSceen.setBackgroundResource(R.drawable.background);
        }else if (stage_num == 2){
            binding.charaSceen.setBackgroundResource(R.drawable.background2);
        }else if (stage_num == 3){
            binding.charaSceen.setBackgroundResource(R.drawable.background4);
        }else{
            binding.charaSceen.setBackgroundResource(R.drawable.background3);
        }

        //育成ボタンを押したとき
        Button tap_ikusei = binding.ikuseibutton;
        tap_ikusei.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                //名前がなければキャラクリエイト
                if (chara_name.equals("")){
                    Intent intent = new Intent(getApplication(), CharacterCreationActivity.class);
                    startActivity(intent);

                }else if (turn >= 10){
                    //10ターン目かつ戦闘中のキャラのHPが0でないなら戦闘画面
                    if (battle_hp != 0){
                        Intent intent = new Intent(getApplication(), BattleActivity.class);
                        startActivity(intent);
                        //HPが0ならリザルト画面
                    }else {
                        Intent intent = new Intent(getApplication(), ResultActivity.class);
                        startActivity(intent);
                    }
                    //それ以外なら育成画面
                }else{
                    Intent intent = new Intent(getApplication(), NurtureActivity.class);
                    startActivity(intent);
                }



                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }
}