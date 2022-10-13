package com.example.dragonquest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.example.dragonquest.databinding.ActivityResultBinding;

public class ResultActivity extends AppCompatActivity {
    private ActivityResultBinding binding;

    //データベース接続用変数
    private DatabaseHelper helper = null;
    private Handler handler;//スレッド内でテキストをいじる際に使用

    int view_turn,last_turn;

    public ResultActivity(){handler = new Handler();}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResultBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        //終了ボタン非表示
        binding.endButton.setVisibility(View.INVISIBLE);
        binding.imageView2.setVisibility(View.INVISIBLE);

        // ヘルパーを準備
        helper = new DatabaseHelper(this);
        handler.postDelayed(() -> {
            //1秒待って実行
            //ステータス表示
            StatusView();
        }, 1000); // 約2000ミリ秒（2秒）後


        //終了ボタンを押したとき
        binding.endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // キャラクターDBの内容を初期化
                // 入力されたタイトルとコンテンツをContentValuesに設定
                // ContentValuesは、項目名と値をセットで保存できるオブジェクト
                ContentValues cv = new ContentValues();
                cv.put(DBTables.CharacterTable.CHARA_SAVE_NAME, "");
                cv.put(DBTables.CharacterTable.CHARA_SAVE_HP, 0);
                cv.put(DBTables.CharacterTable.CHARA_SAVE_ATK, 0);
                cv.put(DBTables.CharacterTable.CHARA_SAVE_DEF, 0);
                cv.put(DBTables.CharacterTable.CHARA_SAVE_DEX, 0);
                cv.put(DBTables.CharacterTable.CHARA_SAVE_SKILL1, "");
                cv.put(DBTables.CharacterTable.CHARA_SAVE_SKILL2, "");
                cv.put(DBTables.CharacterTable.CHARA_SAVE_SKILL3, "");
                cv.put(DBTables.CharacterTable.CHARA_SAVE_SKILL4, "");
                cv.put(DBTables.CharacterTable.CHARA_SAVE_TURN, 0);
                cv.put(DBTables.CharacterTable.CHARA_SAVE_STAGE, 1);

                //where文 今回はidを指定して
                String where = DBTables.CharacterTable.CHARA_SAVE_ID + " = " + 1;

                // 書き込みモードでデータベースをオープン
                try (SQLiteDatabase db = helper.getWritableDatabase()) {

                    //アップデート
                    db.update(DBTables.CharacterTable.TABLE_NAME, cv, where, null);
                    cv.put(DBTables.CharacterTable.CHARA_SAVE_STAGE, 0);
                    where = DBTables.CharacterTable.CHARA_SAVE_ID + " = " + 3;
                    db.update(DBTables.CharacterTable.TABLE_NAME, cv, where, null);
                    where = DBTables.CharacterTable.CHARA_SAVE_ID + " = " + 2;
                    db.update(DBTables.CharacterTable.TABLE_NAME, cv, where, null);
                    if (view_turn == 40 && last_turn > 40){
                        where = DBTables.CharacterTable.CHARA_SAVE_ID + " = " + 4;
                        db.update(DBTables.CharacterTable.TABLE_NAME, cv, where, null);
                    }else {
                        //合計ターン保存
                        where = DBTables.CharacterTable.CHARA_SAVE_ID + " = " + 4;
                        ContentValues cv2 = new ContentValues();
                        cv2.put(DBTables.CharacterTable.CHARA_SAVE_TURN, view_turn);
                        db.update(DBTables.CharacterTable.TABLE_NAME, cv2, where, null);
                    }

                }
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
                ResultTurn(cursor);

            }
        }
    }

    //ターン数の表示
    protected void ResultTurn(Cursor cursor){
        //合計ターン
        last_turn = cursor.getInt(9) + (cursor.getInt(10)-1)*10;
        view_turn = last_turn;
        if (view_turn > 40){
            view_turn = 40;
        }
        ChangeStatus(binding.resultTurn,view_turn);
        //アドバイス表示
        handler.postDelayed(() -> {
            //1秒待って実行
            ResultAdvice(cursor,last_turn);
        }, 1000);
    }

    //アドバイス
    protected void ResultAdvice(Cursor cursor, int turn){
        String txt = "";
        if (cursor.getString(7).equals("") || cursor.getString(8).equals("") || cursor.getString(6).equals("")){
            txt = "スキルは４つまで覚えられる。\r色んな選択肢を選んでみよう！";
        }else if (turn <=10){
            txt = "スライムは回復スキルを覚えている。\rATKを上げて短期決戦を臨もう！";
        }else if (turn <= 20){
            if (cursor.getInt(2) <= 300){
                txt = "ゴーレムは高防御だ。今の攻撃力では勝てそうにない。魔法スキルなら…";
            }else {
                txt = "ゴーレムは高防御だ。魔法スキルなら…";
            }
        }else if (turn <= 30){
            txt = "ドラゴンの一撃は強力だ！\nやられる前にやってしまおう！";
        }else if (turn < 40){
            txt = "ラストステージは様々な敵が襲ってくる。\nHPに気を付けながら臨もう";
        }else if (turn == 40){
            txt = "魔王は強敵だ。何度か転生し直しステータスを上げよう！";
        }else if(turn > 40){
            txt = "全ての魔物を倒し切った！\nおめでとう！君が真のヒーローだ！";
            //エフェクト
            ImageView imageView = binding.imageView2;
            GlideDrawableImageViewTarget target = new GlideDrawableImageViewTarget(imageView, 0);
            Glide.with(this).load(R.drawable.confetti).into(target);
            binding.imageView2.setVisibility(View.VISIBLE);
        }
        binding.resultAdvice.setText(txt);
        binding.endButton.setVisibility(View.VISIBLE);

    }


    //表示するステータスを徐々に上げる処理
    protected void ChangeStatus(TextView view,int num){
        //カウントアップ
        int point_up = num/20 + 1;

        Thread change_hp = new Thread(new Runnable() {
            //表示する値
            int view_num = 0;
            @Override
            public void run() {
                while (view_num != num){
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