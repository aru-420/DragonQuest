package com.example.dragonquest;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import com.example.dragonquest.DBTables.SkillTable;

import com.example.dragonquest.databinding.ActivityBattleBinding;

public class BattleActivity extends AppCompatActivity {
    private ActivityBattleBinding binding;

    //データベース接続用変数
    private DatabaseHelper helper = null;

    private String messagetext;

    private Handler handler = new Handler();//スレッド内でテキストをいじる際に使用
    private int myhp = 100; //キャラのHPを保存する変数
    private int ememyHp = 100;  //エネミーのHPを保存する変数

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBattleBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //データベース接続
        helper = new DatabaseHelper(this);

        //初期非表示
        binding.battleMessage.setVisibility(View.INVISIBLE);    //バトルメッセージ非表示
        binding.battleEndButton.setVisibility(View.INVISIBLE);  //バトル終了ボタン非表示

        //ライフゲージ周りの設定
        binding.myHpBar.setMax(myhp);           //キャラの最大値の設定
        binding.myHpBar.setProgress(myhp);      //キャラの現在地の設定
        binding.ememyHpBar.setMax(myhp);        //エネミーの最大値の設定
        binding.ememyHpBar.setProgress(myhp);   //エネミーの現在地の設定
        binding.myHpText.setText(myhp + "/" + myhp);    //キャラのHPテキスト

        //左上ボタンクリック時の処理
        binding.skill1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //メッセージの表示
                binding.battleMessage.setVisibility(View.VISIBLE);
                binding.battleMessage.setText("スキル1が選択されました");
                //仮確認　キャラクターのｈｐ減少
                damageCalculation(true);
            }
        });

        //右上ボタンクリック時の処理
        binding.skill2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //メッセージの表示
                binding.battleMessage.setVisibility(View.VISIBLE);
                binding.battleMessage.setText("スキル2が選択されました");
                //仮確認　エネミーのｈｐ減少
                damageCalculation(false);
            }
        });

        //左下ボタンクリック時の処理
        binding.skill3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //メッセージの表示
                binding.battleMessage.setVisibility(View.VISIBLE);
                binding.battleMessage.setText("スキル3が選択されました");
                onSave();
            }
        });

        //右下ボタンクリック時の処理
        binding.skill4.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //メッセージの表示
                binding.battleMessage.setVisibility(View.VISIBLE);
                messagetext = "スキル4が選択されました";
                binding.battleMessage.setText(messagetext);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        messagetext = messagetext + "\n90ダメージ！";
                        binding.battleMessage.setText(messagetext);// ここは1秒後に表示させたいです
                    }
                }, 500); // 1000ミリ秒（1秒）後
            }
        });

        //メッセージのクリック処理有効化
        binding.battleMessage.setClickable(true);
        //メッセージクリック時の処理
        binding.battleMessage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //メッセージの非表示
                binding.battleMessage.setVisibility(View.INVISIBLE);
            }
        });



    }

    private void damageCalculation(boolean tof){
        //HPが減る処理
        int hpPoint = 1;
        final int maxHp = binding.myHpBar.getMax();

        //スレッド
        new Thread(new Runnable() {
            @Override
            public void run() {
                //エネミーかキャラクターか
                if (tof){
                    //キャラクターのHP処理
                    while (myhp > 10) {
                        //HPを１ずつ減らす
                        myhp -= hpPoint;

                        binding.myHpBar.setProgress(myhp);  //バーの表示更新
                        //スレッド内でUI変更
                        handler.post(() -> {
                            binding.myHpText.setText(myhp + "/" + maxHp);    //テキストの更新
                        });

                        //HPがゼロになったらボタン表示
                        if (myhp == 0) {
                            //スレッド内でUI変更
                            handler.post(() -> {
                                binding.battleEndButton.setText("リザルト画面へ");
                                binding.battleEndButton.setVisibility(View.VISIBLE);
                            });
                            //ループ終了
                            break;
                        }
                        try {
                            Thread.sleep(50);   //50ミリ秒待つ
                        } catch (InterruptedException ignored) { }
                    }
                }else {
                    //エネミーのHP処理
                    while (ememyHp > 0){
                        //HPを１ずつ減らす
                        ememyHp -= hpPoint;

                        binding.ememyHpBar.setProgress(ememyHp);  //バーの表示更新

                        //HPがゼロになったらボタン表示
                        if (ememyHp == 0){
                            //スレッド内でUI変更
                            handler.post(()->{
                                binding.battleEndButton.setText("次の育成へ");
                                binding.battleEndButton.setVisibility(View.VISIBLE);
                            });
                            //ループ終了
                            break;
                        }
                        try {
                            Thread.sleep(50);   //50ミリ秒待つ
                        }catch (InterruptedException ignored) { }
                    }
                }
            }


        }).start();
        //スレッドここまで
    }


    //データベース登録テスト
    public void onSave(){
        // 書き込みモードでデータベースをオープン
        try (SQLiteDatabase db = helper.getWritableDatabase()) {

            // 入力されたタイトルとコンテンツをContentValuesに設定
            // ContentValuesは、項目名と値をセットで保存できるオブジェクト
            ContentValues cv = new ContentValues();

            cv.put(SkillTable.COLUMN_NAME_SKILL_NAME, "スラッシュ2");
            cv.put(SkillTable.COLUMN_NAME_SKILL_EFFECT, "100ダメージ");

            // 現在テーブルに登録されているデータの_IDを取得
            Cursor cursor = db.query(SkillTable.TABLE_NAME,  new String[] {SkillTable.COLUMN_NAME_SKILL_ID}, null, null,
                    null, null, null, null);

            // テーブルにデータが登録されていれば更新処理
            if (cursor.moveToFirst()){

                // 取得した_IDをparamsに設定
                String[] params = {cursor.getString(0)};

                // _IDのデータを更新
                db.update(SkillTable.TABLE_NAME, cv, SkillTable.COLUMN_NAME_SKILL_ID + " = ?", params);

            } else {

                // データがなければ新規登録
                db.insert(SkillTable.TABLE_NAME, null, cv);
            }
        }
        onShow();
    }

    // データを表示する
    protected void onShow() {

        // データベースから取得する項目を設定
        String[] cols = {SkillTable.COLUMN_NAME_SKILL_NAME, SkillTable.COLUMN_NAME_SKILL_EFFECT};

        String sql = "select " + cols + " from " + SkillTable.TABLE_NAME + " WHERE skill_id = 1;";
        // 読み込みモードでデータベースをオープン
        try (SQLiteDatabase db = helper.getReadableDatabase()) {

            // データを取得するSQLを実行
            // 取得したデータがCursorオブジェクトに格納される
            Cursor cursor = db.query(SkillTable.TABLE_NAME, cols, "skill_id = 1",
                    null, null, null, null, null);

            // moveToFirstで、カーソルを検索結果セットの先頭行に移動
            // 検索結果が0件の場合、falseが返る
            if (cursor.moveToFirst()){

                // 表示用のテキスト・コンテンツに検索結果を設定
                binding.battleMessage.setText(cursor.getString(0) + cursor.getString(1));


            } else {
                // 検索結果が0件の場合のメッセージを設定
//                viewTitle.setText("データがありません");
//                viewContents.setText("");
//
//                editTitle.setText("");
//                editContents.setText("");
            }
        }

    }

}