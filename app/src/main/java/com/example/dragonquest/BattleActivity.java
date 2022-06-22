package com.example.dragonquest;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import com.example.dragonquest.databinding.ActivityBattleBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;

import static java.lang.String.valueOf;

public class BattleActivity extends AppCompatActivity {
    private ActivityBattleBinding binding;

    //自身のステータスを保存するクラス
    Actor my_actor;
    //エネミーステータスを保存するクラス
    Actor enemy_actor;
    //メッセージをクリックしたときに消えるかどうかのフラグ
    boolean messageclickflag = false;

    //データベース接続用変数
    private DatabaseHelper helper = null;

    //メッセージテキスト
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

        //キャラ作成
        Create_Actor();

        //データベース接続
        helper = new DatabaseHelper(this);

        //初期非表示
        binding.battleMessage.setVisibility(View.INVISIBLE);    //バトルメッセージ非表示
        binding.battleEndButton.setVisibility(View.INVISIBLE);  //バトル終了ボタン非表示

        //ボタンのカラーによって役割を表す
        //青ならスキル灰色ならスキルなし
        buttonsColor(binding.skill1);
        buttonsColor(binding.skill2);
        buttonsColor(binding.skill3);
        buttonsColor(binding.skill4);



    }

    private void buttonsColor(Button skill){
        //スキル4がなければボタンを押せなくして灰色にする
        if (skill.getText().equals("")) {
            skill.setEnabled(false);
            skill.setBackgroundColor(Color.GRAY);
        }else{
            skill.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View view) {
                            //メッセージの表示
                            binding.battleMessage.setVisibility(View.VISIBLE);
                            binding.grid.setVisibility(View.INVISIBLE);
                            binding.battleMessage.setClickable(false);
                            BattleStart((String) skill.getText());
                        }
                    });
        }
    }

    //ダメージを与える処理
    private void damageCalculation(boolean tof , int damage){
        //HPが減る処理
        int hpPoint = damage / 60 + 1;    //一ずつ減る
        final int maxHp = binding.myHpBar.getMax();

        //スレッド
        Thread change_hp = new Thread(new Runnable() {
            @Override
            public void run() {
                int result_HP;
                //エネミーかキャラクターか
                if (tof){
                    //キャラクターのHP処理
                    result_HP = my_actor.getHp() -damage;
                    myhp = my_actor.getHp();
                    while (myhp >= result_HP) {

                        //HPがゼロ以下になったらボタン表示
                        if (myhp <= 0) {
                            //HPを0に
                            my_actor.setHp(0);
                            //スレッド内でUI変更
                            handler.post(() -> {
                                //ゲームオーバー
                                messagetext = my_actor.getName() + "は死んでしまった！";
                                binding.battleMessage.setText(messagetext);
                                binding.battleMessage.setBackgroundColor(Color.RED);
                                binding.battleEndButton.setText("リザルト画面へ");
                                binding.battleEndButton.setVisibility(View.VISIBLE);

                            });
                            //ループ終了
                            break;
                        }

                        if (myhp == result_HP){
                            //メッセージを消せるようにする
                            if (messageclickflag){
                                messageclickflag = false;
                                //スレッド内でUI変更
                                handler.post(()->{
                                    //メッセージのクリック処理有効化
                                    binding.battleMessage.setClickable(true);

                                });
                            }
                            my_actor.setHp(myhp);
                            break;
                        }


                        //HPをhpPointずつ減らす
                        myhp -= hpPoint;
                        if (myhp < result_HP && myhp >= 0){
                            myhp = result_HP;
                        }else if (myhp < 0){
                            myhp = 0;
                        }

                        binding.myHpBar.setProgress(myhp);  //バーの表示更新
                        //スレッド内でUI変更
                        handler.post(() -> {
                            binding.myHpText.setText(myhp + "/" + maxHp);    //テキストの更新

                        });
                        try {
                            Thread.sleep(20);   //50ミリ秒待つ
                        } catch (InterruptedException ignored) { }
                    }
                }else {
                    //エネミーのHP処理
                    //残りHPを計算
                    ememyHp = enemy_actor.getHp();
                    result_HP = ememyHp -damage;
                    //残りHPになるまでループ
                    while (ememyHp >= result_HP){
                        //HPがゼロ以下になったらボタン表示
                        if (ememyHp <= 0){
                            //HPを0に
                            enemy_actor.setHp(ememyHp);
                            //スレッド内でUI変更
                            handler.post(()->{
                                binding.battleEndButton.setText("次の育成へ");
                                binding.battleEndButton.setVisibility(View.VISIBLE);
                            });
                            //ループ終了
                            break;
                        }

                        //メッセージを消せるようにする
                        if (ememyHp == result_HP){
                            if (messageclickflag){
                                messageclickflag = false;
                                //スレッド内でUI変更
                                handler.post(()->{
                                    //メッセージのクリック処理有効化
                                    binding.battleMessage.setClickable(true);
                                });
                            }
                            enemy_actor.setHp(ememyHp);
                            break;
                        }

                        //HPをhpPointずつ減らす
                        ememyHp -= hpPoint;
                        if (ememyHp < result_HP && ememyHp >=0){
                            ememyHp = result_HP;
                        }else if (ememyHp < 0){
                            ememyHp = 0;
                        }
                        binding.ememyHpBar.setProgress(ememyHp);  //バーの表示更新
                        try {
                            Thread.sleep(20);   //50ミリ秒待つ
                        }catch (InterruptedException ignored) { }
                    }
                }
            }
        });
        change_hp.start();
        //スレッドここまで
    }

    //バトル順序
    protected void BattleStart(String skill_name){
        //素早さを比較してダメ――ジ処理に移行
        if (my_actor.getDex() >= enemy_actor.getDef()){
            //プレイヤーの方が早い場合
            //プレイヤーのダメージ処理
            onShow(skill_name, my_actor.getName(),false);
            //エネミーのHPが０でなければ
            if (enemy_actor.getHp() != 0){
                //表示2秒後に処理を移す
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //メッセージビューのクリックリスナーを管理するフラグ
                        messageclickflag = true;
                        onShow(enemy_actor.getSkill1(), enemy_actor.getName(), true);
                        //メッセージのクリック処理有効化
                        binding.battleMessage.setClickable(true);
                    }
                }, 2000); // 2000ミリ秒（2秒）後
            }

        }else {
            //エネミーの方が早い場合
            //エネミーのダメージ処理
            onShow(enemy_actor.getSkill1() , enemy_actor.getName(),true);
            //表示2秒後に処理を移す
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //キャラのHPが０でなければ
                    if (my_actor.getHp() != 0){
                        messageclickflag = true;
                        onShow(skill_name, my_actor.getName(), false);
                        //メッセージのクリック処理有効化
                        binding.battleMessage.setClickable(true);
                    }
                }
            }, 2000); // 2000ミリ秒（2秒）後

        }
    }


    // データを表示する
    protected void onShow(String skill_name, String name, boolean tof) {
        //アセットにあるjsonを開く
        try {
            //アセットマネージャー
            AssetManager assetManager = getResources().getAssets();
            // jsonを取得する
            //ファイル名を指定
            InputStream inputStream = assetManager.open("skill.json");
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
            int skill_effect = Integer.parseInt(json.getJSONObject(skill_name).getString("skill_effect"));

            //ダメージ計算

            int damage = damegeRandom(tof, skill_effect);

            //表示するメッセージ
            messagetext = name + "の" + skill_name + "\n" +  damage + "ダメージ!";

            //バトルメッセージに表示
            binding.battleMessage.setText(messagetext);
            //HPバー減少処理
            damageCalculation(tof, damage);

        }catch (IOException | JSONException e) {
            e.printStackTrace();
        }

    }

    //キャラ作成
    protected void Create_Actor(){
        //アセットにあるjsonを開く
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

            //jsonからプレイヤーデータを引っ張る
            JSONObject my_json = json.getJSONObject("charaID1");
            String name = my_json.getString("NAME");
            int hp = my_json.getInt("HP");
            int atk = my_json.getInt("ATK");
            int def = my_json.getInt("DEF");
            int dex = my_json.getInt("DEX");
            String skill1 = my_json.getString("SKILL1");
            String skill2 = my_json.getString("SKILL2");
            my_actor = new Actor(name, hp, atk, def, dex, skill1,skill2,"","");
            binding.myCharaName.setText(my_actor.getName());
            myhp = my_actor.getHp();
            binding.myHpBar.setMax(myhp);   //キャラの最大値の設定
            binding.myHpBar.setProgress(myhp);      //キャラの現在地の設定
            binding.myHpText.setText(myhp + "/" + myhp);    //キャラのHPテキスト
            binding.skill1.setText(my_actor.getSkill1());   //スキルをセット
            binding.skill2.setText(my_actor.getSkill2());
            binding.skill3.setText(my_actor.getSkill3());
            binding.skill4.setText(my_actor.getSkill4());

            //jsonからエネミーデータを引っ張る
            JSONObject enemy_json = json.getJSONObject("charaID2");
            String enemy_name = enemy_json.getString("NAME");
            int enemy_hp = enemy_json.getInt("HP");
            int enemy_atk = enemy_json.getInt("ATK");
            int enemy_def = enemy_json.getInt("DEF");
            int enemy_dex = enemy_json.getInt("DEX");
            String enemy_skill1 = enemy_json.getString("SKILL1");
            String enemy_skill2 = enemy_json.getString("SKILL2");
            enemy_actor = new Actor(enemy_name, enemy_hp, enemy_atk, enemy_def, enemy_dex,
                    enemy_skill1,enemy_skill2,"","");
            binding.ememyName.setText(enemy_actor.getName());
            ememyHp = enemy_actor.getHp();
            binding.ememyHpBar.setMax(ememyHp);   //エネミーの最大値の設定
            binding.ememyHpBar.setProgress(ememyHp);      //エネミーの現在地の設定

        }catch (IOException | JSONException e) {
            e.printStackTrace();
        }

    }

    //ダメージ計算
    private int damegeRandom(boolean MoE, int skill_effect){
        //(攻撃力+乱数)×ダメージエフェクト-防御力

        //ダメージの乱数
        Random rnd = new Random();
        int random;

        int damege = 0;
        if (MoE){
            //乱数はATKの10分の1で生成しその半分の値を減産した値をATKに加算する
            //例：ATK100なら乱数は「-5~5」の値をとる。よってATKは「95~105」になる
            random = rnd.nextInt(enemy_actor.getAtk()/10)-( (enemy_actor.getAtk()/10) / 2);
            String moi = valueOf(random);
            binding.skill4.setText(moi);
            damege = (enemy_actor.getAtk() + random) * skill_effect - my_actor.getDex();
        }else {
            random = rnd.nextInt(my_actor.getAtk()/10)-( (my_actor.getAtk()/10) / 2);
            damege = my_actor.getAtk() * skill_effect - enemy_actor.getDex();
        }
        return damege;
    }

}

