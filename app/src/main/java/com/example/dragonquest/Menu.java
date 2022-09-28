package com.example.dragonquest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
public class Menu extends AppCompatActivity {

    //データベース接続用変数
    private DatabaseHelper helper = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_menu);

        //続ける
        Button tap_continue = findViewById(R.id.continue1);
        tap_continue.setOnClickListener(v -> finish());
        // ヘルパーを準備
        helper = new DatabaseHelper(this);
        Actor get_save = (Actor) this.getApplication();
        //ステータス
        Button tap_status = findViewById(R.id.status);
        tap_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
//仮読み込み
                Actor actor = get_save.GetActivityActor();
                Intent intent = new Intent(getApplication(), StatusActivity.class);
                startActivity(intent);
            }
        });

        //あきらめる
        Button tap_giveup = findViewById(R.id.giveup);
        tap_giveup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyDialog dialog = new MyDialog();
                dialog.show(getSupportFragmentManager(), "my_dialog");
            }
        });

        //戻るボタン(前の画面に戻る)
        Button tap_back = findViewById(R.id.back);
        tap_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //セーブボタン
        Button tap_save = findViewById(R.id.save);
        tap_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //仮読み込み
                Actor actor = get_save.GetActivityActor();
                //     510行目あたりのDB保存処理を参照
                // 入力されたタイトルとコンテンツをContentValuesに設定
                // ContentValuesは、項目名と値をセットで保存できるオブジェクト
                ContentValues cv = new ContentValues();
                cv.put(DBTables.CharacterTable.CHARA_SAVE_NAME, actor.getName());
                cv.put(DBTables.CharacterTable.CHARA_SAVE_HP, actor.getHp());
                cv.put(DBTables.CharacterTable.CHARA_SAVE_ATK, actor.getAtk());
                cv.put(DBTables.CharacterTable.CHARA_SAVE_DEF, actor.getDef());
                cv.put(DBTables.CharacterTable.CHARA_SAVE_DEX, actor.getDex());
                cv.put(DBTables.CharacterTable.CHARA_SAVE_SKILL1, actor.getSkill1().getSkill_name());
                cv.put(DBTables.CharacterTable.CHARA_SAVE_SKILL2, actor.getSkill2().getSkill_name());
                cv.put(DBTables.CharacterTable.CHARA_SAVE_SKILL3, actor.getSkill3().getSkill_name());
                cv.put(DBTables.CharacterTable.CHARA_SAVE_SKILL4, actor.getSkill4().getSkill_name());
                cv.put(DBTables.CharacterTable.CHARA_SAVE_TURN, actor.getSave_turn());

                //where文 今回はidを指定して
                String where = DBTables.CharacterTable.CHARA_SAVE_ID + " = 1";

                // 書き込みモードでデータベースをオープン
                try (SQLiteDatabase db = helper.getWritableDatabase()) {
                    //アップデート
                    db.update(DBTables.CharacterTable.TABLE_NAME, cv, where, null);
                }
                MyDialog2 dialog = new MyDialog2();
                dialog.show(getSupportFragmentManager(), "my_dialog");
            }
        });
    }
}
