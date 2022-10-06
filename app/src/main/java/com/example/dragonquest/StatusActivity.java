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
        Actor get_save = (Actor) this.getApplication();
        //仮読み込み
        Actor actor = get_save.GetActivityActor();

        name = actor.getName();
        hp = actor.getHp();
        atk = actor.getAtk();
        def = actor.getDef();
        dex = actor.getDex();
        skill1 = actor.getSkill1().getSkill_name();
        skill2 = actor.getSkill2().getSkill_name();
        skill3 = actor.getSkill3().getSkill_name();
        skill4 = actor.getSkill4().getSkill_name();

        //戦闘中かどうか
        // データベースから取得する項目を設定
        String[] cols = {DBTables.CharacterTable.CHARA_SAVE_NAME, DBTables.CharacterTable.CHARA_SAVE_HP,
                DBTables.CharacterTable.CHARA_SAVE_ATK, DBTables.CharacterTable.CHARA_SAVE_DEF, DBTables.CharacterTable.CHARA_SAVE_DEX,
                DBTables.CharacterTable.CHARA_SAVE_SKILL1, DBTables.CharacterTable.CHARA_SAVE_SKILL2, DBTables.CharacterTable.CHARA_SAVE_SKILL3, DBTables.CharacterTable.CHARA_SAVE_SKILL4,
                DBTables.CharacterTable.CHARA_SAVE_TURN, DBTables.CharacterTable.CHARA_SAVE_STAGE
        };
        //where文 リザルトデータ
        String my_where = DBTables.CharacterTable.CHARA_SAVE_ID + " = 3";

        // 読み込みモードでデータベースをオープン
        try (SQLiteDatabase db = helper.getReadableDatabase()) {
            // データを取得するSQLを実行
            // 取得したデータがCursorオブジェクトに格納される
            //SELECT文
            Cursor cursor = db.query(DBTables.CharacterTable.TABLE_NAME, cols, my_where,
                    null, null, null, null, null);
            //一行読み込み
            if (cursor.moveToFirst()){
                if (cursor.getString(0).equals(name)){
                    atk = cursor.getInt(2);
                    def = cursor.getInt(3);
                    dex = cursor.getInt(4);
                }
            }
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
                ImageView imageView = findViewById(R.id.character_Image_nu2);
                imageView.setImageResource(R.drawable.warrior);
            } else if (name.equals(CharaNameStr[1])) {
                //画像の変更(magician)
                ImageView imageView = findViewById(R.id.character_Image_nu2);
                imageView.setImageResource(R.drawable.magician);
            }


        binding.statusBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}