package com.example.dragonquest;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.dragonquest.databinding.ActivityNurtureBinding;
import com.example.dragonquest.databinding.ActivityStatusBinding;

public class StatusActivity extends AppCompatActivity {
    private ActivityStatusBinding binding;

    //データベース接続用変数
    private DatabaseHelper helper = null;

    //キャラを変える(選択)する際に使用する変数
    private String CharaNameStr[] = new String[2];

    //DBからデータを受け取る際に入れる変数
    String name, skill, skill1, skill2, skill3, skill4;
    int hp, atk, def, dex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStatusBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //配列の初期化
        //CharaNameStr
        CharaNameStr[0] = "戦士";
        CharaNameStr[1] = "魔法使い";

        // ヘルパーを準備
        helper = new DatabaseHelper(this);

        // 読み込みモードでデータベースをオープン
        try (SQLiteDatabase db = helper.getReadableDatabase()) {

            // データベースから取得する項目を設定
            String[] cols = {DBTables.CharacterTable.CHARA_SAVE_NAME, DBTables.CharacterTable.CHARA_SAVE_HP,
                    DBTables.CharacterTable.CHARA_SAVE_ATK, DBTables.CharacterTable.CHARA_SAVE_DEF, DBTables.CharacterTable.CHARA_SAVE_DEX,
                    DBTables.CharacterTable.CHARA_SAVE_SKILL1, DBTables.CharacterTable.CHARA_SAVE_SKILL2, DBTables.CharacterTable.CHARA_SAVE_SKILL3, DBTables.CharacterTable.CHARA_SAVE_SKILL4
            };
            //where文
            String my_where = DBTables.CharacterTable.CHARA_SAVE_ID + " = 1";

            Cursor cursor;
            cursor = db.query(DBTables.CharacterTable.TABLE_NAME, cols, my_where,
                    null, null, null, null, null);

            if (cursor.moveToFirst()) {
                name = cursor.getString(0);
                hp = Integer.parseInt(cursor.getString(1));
                atk = Integer.parseInt(cursor.getString(2));
                def = Integer.parseInt(cursor.getString(3));
                dex = Integer.parseInt(cursor.getString(4));
                skill1 = cursor.getString(5);
                skill2 = cursor.getString(6);
                skill3 = cursor.getString(7);
                skill4 = cursor.getString(8);
            }

            //ステータス表示
            binding.txtHP2.setText(Integer.toString(hp));
            binding.txtATK2.setText(Integer.toString(atk));
            binding.txtDEF2.setText(Integer.toString(def));
            binding.txtDEX2.setText(Integer.toString(dex));

            //スキル表示
            binding.Skill1.setText(skill1);
            binding.Skill2.setText(skill2);
            binding.Skill3.setText(skill3);
            binding.Skill4.setText(skill4);

            //画像の変更
            if (name.equals(CharaNameStr[0])) {
                //画像の変更(warrior)
                ImageView imageView = findViewById(R.id.character_Image_nu);
                imageView.setImageResource(R.drawable.warrior);
            } else if (name.equals(CharaNameStr[1])) {
                //画像の変更(magician)
                ImageView imageView = findViewById(R.id.character_Image_nu);
                imageView.setImageResource(R.drawable.magician);
            }
        }

        binding.statusBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}