package com.example.dragonquest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.example.dragonquest.databinding.ActivityResultBinding;

public class ResultActivity extends AppCompatActivity {
    private ActivityResultBinding binding;

    //データベース接続用変数
    private DatabaseHelper helper = null;
    private Handler handler;//スレッド内でテキストをいじる際に使用

    public ResultActivity(){handler = new Handler();}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResultBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // ヘルパーを準備
        helper = new DatabaseHelper(this);

        handler.postDelayed(() -> {
            //1秒待って実行
            //ステータス表示
            StatusView();
        }, 1000); // 約2000ミリ秒（2秒）後


        binding.endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), TitleActivity.class);
                startActivity(intent);
            }
        });
    }

    //DBに接続して表示するデータを取得
    protected void StatusView(){
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
                ChangeStatus(binding.resultHp,cursor.getInt(1));
                ChangeStatus(binding.resultAtk, cursor.getInt(2));
                ChangeStatus(binding.resultDef, cursor.getInt(3));
                ChangeStatus(binding.resultDex, cursor.getInt(4));
                ResultTurn(cursor.getInt(9),cursor.getInt(10));
            }
        }
    }

    //ターン数の表示
    protected void ResultTurn(int turn, int stage){
        //合計ターン
        int view_turn = turn + (stage-1)*10;
        String view = Integer.valueOf(view_turn).toString();
        binding.resultTurn.setText(view);

    }


    //表示するステータスを徐々に上げる処理
    protected void ChangeStatus(TextView view,int num){
        //カウントアップ
        int point_up = num/20 + 1;
        //表示する値
        int view_num;
        Thread change_hp = new Thread(new Runnable() {
            int view_num = 0;
            @Override
            public void run() {
                while (view_num < num){
                    if (view_num < num){
                        view_num += point_up;
                    }else if (view_num > num){
                        view_num = num;
                    }
                    //スレッド内でUI変更
                    handler.post(() -> {
                        String string_num = Integer.valueOf(view_num).toString();
                        view.setText(string_num);
                    });

                    try {
                        Thread.sleep(50);   //50ミリ秒待つ
                    }catch (InterruptedException ignored) { }
                }

            }
        });
        change_hp.start();
    }
}