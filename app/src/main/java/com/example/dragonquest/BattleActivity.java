package com.example.dragonquest;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.example.dragonquest.DBTables.CharacterTable;
import com.example.dragonquest.databinding.ActivityBattleBinding;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;

public class BattleActivity extends AppCompatActivity {
    private ActivityBattleBinding binding;

    //自身のステータスを保存するクラス
    Actor my_actor;
    //エネミーステータスを保存するクラス
    Actor enemy_actor;
    //メッセージをクリックしたときに消えるかどうかのフラグ
    boolean messageclickflag = false;
    boolean screen_flag;
    //ボタンを複数押せないようにする
    boolean button_not_double = true;


    //データベース接続用変数
    private DatabaseHelper helper = null;

    //メッセージテキスト
    private String messagetext;

    private Handler handler;//スレッド内でテキストをいじる際に使用
    private int myhp = 100; //キャラのHPを保存する変数
    private int ememyHp = 100;  //エネミーのHPを保存する変数
    private int delayTime = 2000;   //ディレイタイムの設定

    public BattleActivity() {
        handler = new Handler();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBattleBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // ヘルパーを準備
        helper = new DatabaseHelper(this);

        screen_flag = false;

        // 入力されたタイトルとコンテンツをContentValuesに設定
        // ContentValuesは、項目名と値をセットで保存できるオブジェクト
//        ContentValues cv = new ContentValues();
//        cv.put(CharacterTable.CHARA_SAVE_NAME, "戦士");
//        cv.put(CharacterTable.CHARA_SAVE_HP, 500);
//        cv.put(CharacterTable.CHARA_SAVE_ATK, 50);
//        cv.put(CharacterTable.CHARA_SAVE_DEF, 50);
//        cv.put(CharacterTable.CHARA_SAVE_DEX, 60);
//        cv.put(CharacterTable.CHARA_SAVE_SKILL1, "スラッシュ");
//        cv.put(CharacterTable.CHARA_SAVE_SKILL2, "ブレス");
//        cv.put(CharacterTable.CHARA_SAVE_SKILL3, "ダブルスラッシュ");
//        cv.put(CharacterTable.CHARA_SAVE_SKILL4, "じこさいせい");
//        cv.put(CharacterTable.CHARA_SAVE_TURN, 20);
//
//        //where文 今回はidを指定して
//        String where = CharacterTable.CHARA_SAVE_ID + " = " + 1;

//        // 書き込みモードでデータベースをオープン
//        try (SQLiteDatabase db = helper.getWritableDatabase()) {
//
//            //アップデート
//            db.update(CharacterTable.TABLE_NAME, cv, where, null);
//
//        }

        //エフェクト初期非表示
        binding.myEffect.setVisibility(View.INVISIBLE);
        binding.enemyEffect.setVisibility(View.INVISIBLE);

        //キャラ作成
        Create_Actor();

        //データベース接続
        helper = new DatabaseHelper(this);

        //初期非表示
        binding.battleMessage.setVisibility(View.INVISIBLE);    //バトルメッセージ非表示
        binding.battleEndButton.setVisibility(View.INVISIBLE);  //バトル終了ボタン非表示

        //メニューボタン
        binding.menuButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), Menu.class);
                startActivity(intent);

            }
        });

        //スキル説明テキスト非表示
        binding.skillContext.setVisibility(View.INVISIBLE);

        //ボタンのカラーによって役割を表す
        //青ならスキル灰色ならスキルなし
        buttonsColor(binding.skill1, my_actor.getSkill1());
        buttonsColor(binding.skill2, my_actor.getSkill2());
        buttonsColor(binding.skill3, my_actor.getSkill3());
        buttonsColor(binding.skill4, my_actor.getSkill4());

    }

    //ボタンの色変えと処理
    @SuppressLint("ClickableViewAccessibility")
    private void buttonsColor(Button button_skill, Skill skills){
        if (skills.getSkill_subject().equals("my")){
            //自身にかける効果なら緑
            button_skill.setBackgroundColor(Color.GREEN);
        }
        //スキル4がなければボタンを押せなくして灰色にする
        if (button_skill.getText().equals("")) {
            button_skill.setEnabled(false);
            button_skill.setBackgroundColor(Color.GRAY);

        }else {
            //ボタンを長押ししたとき
            button_skill.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    binding.skillContext.setVisibility(View.VISIBLE);
                    binding.skillContext.setText(skills.getSkill_context());

                    //trueならonclickを呼び出さない
                    return true;
                }
            });
            //ボタンを離したとき
            button_skill.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        //離したとき
                        binding.skillContext.setVisibility(View.INVISIBLE);
                    }

                    return false;
                }
            });
            button_skill.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    if (button_not_double){
                        button_not_double = false;
                        Skill skill = my_actor.returnSkill((String) button_skill.getText());
                        //メッセージの表示
                        binding.battleMessage.setVisibility(View.VISIBLE);
                        //ボタンの非表示
                        binding.grid.setVisibility(View.INVISIBLE);
                        //メッセージのクリックイベント解除
                        screen_flag = false;
                        BattleStart(skill);
                    }
                }
            });
        }
    }

    //画面をタップしたとき
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (screen_flag){
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    //メッセージの非表示
                    binding.battleMessage.setVisibility(View.INVISIBLE);
                    //スキルボタンの表示
                    binding.grid.setVisibility(View.VISIBLE);
                    break;
            }
        }
        return false;
    }


    //バトル順序
    protected void BattleStart(Skill skill){
        //敵のスキル決定
        Skill enemy_skill = getEnemyUseSkill();

        //ディレイ時間変更
        changeTime(skill);
        //素早さを比較してダメ――ジ処理に移行
        if (my_actor.getDex() >= enemy_actor.getDex()){
            //プレイヤーの方が早い場合
            //プレイヤーのダメージ処理
            onShow(skill, false);
            //表示2秒後に処理を移す
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //エネミーのHPが０でなければ
                    if (enemy_actor.getHp() != 0){
                        //メッセージビューのクリックリスナーを管理するフラグ
                        messageclickflag = true;
                        button_not_double = true;
                        onShow(enemy_skill,  true);
                    }else {
                        GameClear();
                    }
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (my_actor.getHp() == 0){
                                GameOver();
                            }
                        }
                    },delayTime - 500); // 2000ミリ秒（2秒）後
                }

            }, delayTime); // 2000ミリ秒（2秒）後
        }else {
            //エネミーの方が早い場合
            //エネミーのダメージ処理
            onShow(enemy_skill,true);
            //表示2秒後に処理を移す
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //キャラのHPが０でなければ
                    if (my_actor.getHp() != 0){
                        messageclickflag = true;
                        onShow(skill,false);
                    }else {
                        GameOver();
                    }
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (enemy_actor.getHp() == 0){
                                GameClear();
                            }
                        }
                    },delayTime - 500); // 2000ミリ秒（2秒）後
                }
            }, delayTime); // 2000ミリ秒（2秒）後

        }
    }


    // データを表示する
    protected void onShow(Skill skill,  boolean tof) {
        //ダメージ系スキル
        if (skill.getSkill_subject().equals("enemy") ||skill.getSkill_subject().equals("element")){
            //ダメージ計算
            damegeRandom(tof, skill);

        }else if(skill.getSkill_subject().equals("my")){
            //回復系スキル
            recoverHP(tof, skill);
        }else if (skill.getSkill_subject().equals("double")){
            //二回攻撃
            //ダメージ計算
            damegeRandom(tof, skill);
            //表示2秒後に処理を移す
            handler.postDelayed(() -> {
                //２秒待って実行
                if (my_actor.getHp() != 0 && enemy_actor.getHp() != 0){
                    damegeRandom(tof, skill);
                }
            }, delayTime/2 - 100); // 約2000ミリ秒（2秒）後

        }
    }

    //ダメージ計算
    private void damegeRandom(boolean MoE, Skill skill){
        //(攻撃力+乱数)×ダメージエフェクト-防御力

        int damage = 0;
        int def;

        if (MoE){
            //乱数はATKの10分の1で生成しその半分の値を減産した値をATKに加算する
            //例：ATK100なら乱数は「-5~5」の値をとる。よってATKは「95~105」になる

            def = my_actor.getDef();
            //属性攻撃なら防御を半分にする
            if (skill.getSkill_subject().equals("element")){
                def = def / 2;
            }
            damage = minDamage(enemy_actor.getAtk(), def, skill.getSkill_effect());
            //表示するメッセージ
            messagetext = enemy_actor.getName() + "の" + skill.getSkill_name() + "\n" +  damage + "ダメージ!";
        }else {
            def = enemy_actor.getDef();
            //属性攻撃なら防御を半分にする
            if (skill.getSkill_subject().equals("element")){
                def = def / 2;
            }
            damage = minDamage(my_actor.getAtk(), def,skill.getSkill_effect());
            //表示するメッセージ
            messagetext = my_actor.getName() + "の" + skill.getSkill_name() + "\n" +  damage + "ダメージ!";
        }
        //エフェクト表示
        effect_show(MoE, skill.getSkill_gif());
        //バトルメッセージに表示
        binding.battleMessage.setText(messagetext);
        //HPバー減少処理
        damageCalculation(MoE, damage);
    }

    //最低保証
    private int minDamage(int atk, int def, double skill_effect){
        //ダメージの乱数
        Random rnd = new Random();
        int random;
        int damage;
        random = rnd.nextInt(atk/10 + 1) - atk/20;
        damage = (int) ((atk + random) * skill_effect - def);
        if (damage <= 0){
            damage = atk/20 + 1;
        }
        return damage;
    }


    //キャラクターがやられたときの処理
    private void GameOver(){
        //ゲームオーバー
        messagetext = my_actor.getName() + "は死んでしまった！";
        binding.battleMessage.setText(messagetext);
        binding.battleMessage.setBackgroundColor(Color.RED);
        binding.battleEndButton.setText("リザルト画面へ");
        binding.battleEndButton.setVisibility(View.VISIBLE);
        //DB更新
        EndDBChange();
        //リザルト画面へ移行
        binding.battleEndButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), ResultActivity.class);
                startActivity(intent);
            }
        });
    }

    private void GameClear(){
        messagetext = enemy_actor.getName() + "を倒した！";
        binding.battleMessage.setText(messagetext);
        binding.battleEndButton.setText("次の育成へ");
        binding.battleEndButton.setVisibility(View.VISIBLE);

        //DB更新
        EndDBChange();
        //育成画面へ移行
        binding.battleEndButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), NurtureActivity.class);
                startActivity(intent);
            }
        });
    }

    //回復
    private void recoverHP(boolean MoE, Skill skill){
        int recovery;
        if (MoE) {
            recovery = (int) (enemy_actor.getAtk() * skill.getSkill_effect());
            //表示するメッセージ
            messagetext = enemy_actor.getName() + "の" + skill.getSkill_name() + "\n" +  recovery + "回復!";
        }else {
            recovery = (int) (my_actor.getAtk() * skill.getSkill_effect());
            //表示するメッセージ
            messagetext = my_actor.getName() + "の" + skill.getSkill_name() + "\n" +  recovery + "回復!";
        }
        //エフェクト表示
        effect_show(!MoE, skill.getSkill_gif());
        //バトルメッセージに表示
        binding.battleMessage.setText(messagetext);
        //HPバー増加処理
        recoveryCalculation(MoE, recovery);
    }

    //DBにアップデート保存
    private void onSave(Actor acto, int id){
        // 入力されたタイトルとコンテンツをContentValuesに設定
        // ContentValuesは、項目名と値をセットで保存できるオブジェクト
        ContentValues cv = new ContentValues();
        cv.put(CharacterTable.CHARA_SAVE_NAME, acto.getName());
        cv.put(CharacterTable.CHARA_SAVE_HP, acto.getHp());
        cv.put(CharacterTable.CHARA_SAVE_ATK, acto.getAtk());
        cv.put(CharacterTable.CHARA_SAVE_DEF, acto.getDef());
        cv.put(CharacterTable.CHARA_SAVE_DEX, acto.getDex());
        cv.put(CharacterTable.CHARA_SAVE_SKILL1, acto.getSkill1().getSkill_name());
        cv.put(CharacterTable.CHARA_SAVE_SKILL2, acto.getSkill2().getSkill_name());
        cv.put(CharacterTable.CHARA_SAVE_SKILL3, acto.getSkill3().getSkill_name());
        cv.put(CharacterTable.CHARA_SAVE_SKILL4, acto.getSkill4().getSkill_name());

        //where文 今回はidを指定して
        String where = CharacterTable.CHARA_SAVE_ID + " = " + id;

        // 書き込みモードでデータベースをオープン
        try (SQLiteDatabase db = helper.getWritableDatabase()) {

            //アップデート
            db.update(CharacterTable.TABLE_NAME, cv, where, null);

        }
    }

    //バトル終了時データベースを更新する
    private void EndDBChange(){
        // 入力されたタイトルとコンテンツをContentValuesに設定
        // ContentValuesは、項目名と値をセットで保存できるオブジェクト
        ContentValues cv = new ContentValues();
        cv.put(CharacterTable.CHARA_SAVE_NAME, "");
        cv.put(CharacterTable.CHARA_SAVE_HP, 0);
        cv.put(CharacterTable.CHARA_SAVE_ATK, 0);
        cv.put(CharacterTable.CHARA_SAVE_DEF, 0);
        cv.put(CharacterTable.CHARA_SAVE_DEX, 0);
        cv.put(CharacterTable.CHARA_SAVE_SKILL1, "");
        cv.put(CharacterTable.CHARA_SAVE_SKILL2, "");
        cv.put(CharacterTable.CHARA_SAVE_SKILL3, "");
        cv.put(CharacterTable.CHARA_SAVE_SKILL4, "");

        //where文 今回はidを指定して
        String where = CharacterTable.CHARA_SAVE_ID + " = " + 2;

        // 書き込みモードでデータベースをオープン
        try (SQLiteDatabase db = helper.getWritableDatabase()) {

            //アップデート
            db.update(CharacterTable.TABLE_NAME, cv, where, null);
            where = CharacterTable.CHARA_SAVE_ID + " = " + 3;
            db.update(CharacterTable.TABLE_NAME, cv, where, null);
            //キャラクターのターン数リセット
            ContentValues cv2 = new ContentValues();
            cv2.put(CharacterTable.CHARA_SAVE_STAGE, 0);
            where = CharacterTable.CHARA_SAVE_ID + " = " + 1;
            db.update(CharacterTable.TABLE_NAME, cv2, where, null);
        }

    }

    //スキル情報を取得
    private JSONObject getSkillJSON(){
        //jsonオブジェクトの宣言
        JSONObject json = null;
        try {
            //アセットマネージャー
            AssetManager assetManager = getResources().getAssets();
            // jsonを取得する
            //ファイル名を指定
            InputStream inputStream = assetManager.open("skill.json");
            //バッファに読み込み
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            //読み込んだファイルをString型にする
            String data = "";
            //読み込んだファイルを一行読み込み
            String str = bufferedReader.readLine();
            while (str != null) {
                //一行ずつ書き込んでいく
                data += str;
                //読み込んだファイルを一行読み込み
                str = bufferedReader.readLine();
            }

            //jsonに変換
            json = new JSONObject(data);

            inputStream.close();
            bufferedReader.close();
        }catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    //スキル内容を保存
    private Skill SettingSkill(int skill_number,String skill_name){
        Skill skill = new Skill(0,"", 0.0, "","","");
        try {
            //jsonオブジェクトの宣言
            JSONObject json = null;

            //jsonに変換
            json = getSkillJSON();

            json = json.getJSONObject(skill_name);

            Double effect = json.getDouble("skill_effect");
            String subject = json.getString("skill_subject");
            String gif = json.getString("skill_gif");
            String context = json.getString("skill_context");
            skill = new Skill(skill_number,skill_name, effect, subject, gif, context);

            return skill;
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return skill;
    }


    //キャラ作成
    @SuppressLint("SetTextI18n")
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

            // データベースから取得する項目を設定
            String[] cols = {CharacterTable.CHARA_SAVE_NAME, CharacterTable.CHARA_SAVE_HP,
                    CharacterTable.CHARA_SAVE_ATK, CharacterTable.CHARA_SAVE_DEF, CharacterTable.CHARA_SAVE_DEX,
                    CharacterTable.CHARA_SAVE_SKILL1, CharacterTable.CHARA_SAVE_SKILL2, CharacterTable.CHARA_SAVE_SKILL3, CharacterTable.CHARA_SAVE_SKILL4,
                    CharacterTable.CHARA_SAVE_STAGE
            };
            //where文
            String my_where = CharacterTable.CHARA_SAVE_ID + " = 3";

            //エネミーの名前を保存する変数
            String enemy_names = "";

            // 読み込みモードでデータベースをオープン
            try (SQLiteDatabase db = helper.getReadableDatabase()) {
                // データを取得するSQLを実行
                // 取得したデータがCursorオブジェクトに格納される
                //SELECT文
                Cursor cursor2 = db.query(CharacterTable.TABLE_NAME, cols, my_where,
                        null, null, null, null, null);

                //一行読み込み
                if (cursor2.moveToFirst()){
                    //名前がなければDB取得
                    if (cursor2.getString(0).equals("")){
                        //取得するIDを変更
                        my_where = CharacterTable.CHARA_SAVE_ID +  " = 1";

                        // データを取得するSQLを実行
                        // 取得したデータがCursorオブジェクトに格納される
                        //SELECT文
                        cursor2 = db.query(CharacterTable.TABLE_NAME, cols, my_where,
                                null, null, null, null, null);

                        //一行読み込み
                        if (cursor2.moveToFirst()){
                            Skill my_skill1 = SettingSkill(1,cursor2.getString(5));
                            Skill my_skill2 = SettingSkill(2,cursor2.getString(6));
                            Skill my_skill3 = SettingSkill(3,cursor2.getString(7));
                            Skill my_skill4 = SettingSkill(4,cursor2.getString(8));
                            //検索結果からmy_actorを作成
                            my_actor = new Actor(cursor2.getString(0), cursor2.getInt(1), cursor2.getInt(2),
                                    cursor2.getInt(3), cursor2.getInt(4),
                                    my_skill1,my_skill2,my_skill3,my_skill4);
                        }


                        //名前があれば
                    }else {
                        Skill my_skill1 = SettingSkill(1,cursor2.getString(5));
                        Skill my_skill2 = SettingSkill(2,cursor2.getString(6));
                        Skill my_skill3 = SettingSkill(3,cursor2.getString(7));
                        Skill my_skill4 = SettingSkill(4,cursor2.getString(8));
                        //検索結果からmy_actorを作成
                        my_actor = new Actor(cursor2.getString(0), cursor2.getInt(1), cursor2.getInt(2),
                                cursor2.getInt(3), cursor2.getInt(4),
                                my_skill1,my_skill2,my_skill3,my_skill4);
                    }

                    //キャラクターの最大HPを取得する
                    //取得するIDを変更
                    my_where = CharacterTable.CHARA_SAVE_ID +  " = 1";

                    //SELECT文
                    cursor2 = db.query(CharacterTable.TABLE_NAME, cols, my_where,
                            null, null, null, null, null);

                    //一行読み込み
                    if (cursor2.moveToFirst()){
                        binding.myHpBar.setMax(cursor2.getInt(1));   //キャラの最大値の設定
                        binding.myCharaName.setText(my_actor.getName());
                        myhp = my_actor.getHp();
                        binding.myHpBar.setProgress(myhp);      //キャラの現在地の設定
                        binding.myHpText.setText(myhp + "/" + cursor2.getInt(1));    //キャラのHPテキスト
                        binding.skill1.setText(my_actor.getSkill1().getSkill_name());   //スキルをセット
                        binding.skill2.setText(my_actor.getSkill2().getSkill_name());
                        binding.skill3.setText(my_actor.getSkill3().getSkill_name());
                        binding.skill4.setText(my_actor.getSkill4().getSkill_name());
                        enemy_names = getEnemyName(cursor2.getInt(9));
                    }

                }
            }


            //エネミーの作成処理
            //jsonから基準値を取得
            JSONObject enemy_json = json.getJSONObject(enemy_names);
            int enemy_hp = enemy_json.getInt("HP");
            int enemy_atk = enemy_json.getInt("ATK");
            int enemy_def = enemy_json.getInt("DEF");
            int enemy_dex = enemy_json.getInt("DEX");

            //where文
            String where = CharacterTable.CHARA_SAVE_ID +  " = 2";

            // 読み込みモードでデータベースをオープン
            try (SQLiteDatabase db = helper.getReadableDatabase()) {

                // データを取得するSQLを実行
                // 取得したデータがCursorオブジェクトに格納される
                Cursor cursor = db.query(CharacterTable.TABLE_NAME, cols, where,
                        null, null, null, null, null);

                // moveToFirstで、カーソルを検索結果セットの先頭行に移動
                // 検索結果が0件の場合、falseが返る
                if (cursor.moveToFirst()){

                    //名前がなければjsonから取得
                    if (cursor.getString(0).equals("")){
                        //jsonからエネミーデータを引っ張る
                        String enemy_name = enemy_json.getString("NAME");
                        String skill1 = enemy_json.getString("SKILL1");
                        String skill2 = enemy_json.getString("SKILL2");
                        Skill enemy_skill1 = SettingSkill(1,skill1);
                        Skill enemy_skill2 = SettingSkill(2,skill2);
                        enemy_actor = new Actor(enemy_name, enemy_hp, enemy_atk, enemy_def, enemy_dex,
                                enemy_skill1,enemy_skill2,enemy_skill2,enemy_skill2);
                    }else {
                        Skill enemy_skill1 = SettingSkill(1,cursor.getString(5));
                        Skill enemy_skill2 = SettingSkill(2,cursor.getString(6));
                        Skill enemy_skill3 = SettingSkill(3,cursor.getString(7));
                        Skill enemy_skill4 = SettingSkill(4,cursor.getString(8));
                        //検索結果からenemy_actorを作成
                        enemy_actor = new Actor(cursor.getString(0), cursor.getInt(1), cursor.getInt(2),
                                cursor.getInt(3), cursor.getInt(4),
                                enemy_skill1,enemy_skill2,enemy_skill3,enemy_skill4);
                    }

                }
            }
            //エネミーの初期画面設定
            binding.ememyName.setText(enemy_actor.getName());
            ememyHp = enemy_actor.getHp();
            binding.ememyHpBar.setMax(enemy_hp);   //エネミーの最大値の設定
            binding.ememyHpBar.setProgress(ememyHp);      //エネミーの現在地の設定

        }catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        setCharaImage();
    }

    String[] Enemy_names ={"スライム","charaID2","ドラゴン","魔王"};
    //エネミーの決定
    private String getEnemyName(int turn){
        ImageView image = binding.ememyImage;
        int num = turn -1;
        switch (num){
            case 0:
                image.setImageResource(R.drawable.slime);
                break;
            case 1:
                image.setImageResource(R.drawable.golem);
                break;
            case 2:
                image.setImageResource(R.drawable.dragon);
                break;
        }
        return Enemy_names[num];
    }

    //キャラクターの画像
    private void setCharaImage(){
        String name = my_actor.getName();
        ImageView image = binding.myCharaImage;
        if (name.equals("戦士")){
            image.setImageResource(R.drawable.warrior);
        }else if (name.equals("魔法使い")){
            image.setImageResource(R.drawable.magician);
        }
    }

    private Skill getEnemyUseSkill(){
        Skill skill = new Skill(0,"",0.0,"","","");
        Random rnd = new Random();
        int random;
        random = rnd.nextInt(4) + 1;
        //random =1;
        switch (random){
            case 1:
                skill = enemy_actor.getSkill1();
                break;
            case 2:
                skill = enemy_actor.getSkill2();
                break;
            case 3:
                skill = enemy_actor.getSkill3();
                break;
            case 4:
                skill = enemy_actor.getSkill4();
                break;
        }
        return skill;
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

                            //ループ終了
                            break;
                        }

                        if (myhp == result_HP){
                            //メッセージを消せるようにする
                            if (messageclickflag){
                                //画面タップでメッセージ非表示
                                messageclickflag = false;
                                screen_flag = true;
                            }
                            my_actor.setHp(myhp);
                            //データベース更新
                            onSave(my_actor, 3);
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

                            });
                            //ループ終了
                            break;
                        }

                        //メッセージを消せるようにする
                        if (ememyHp == result_HP){
                            if (messageclickflag){
                                //画面タップでメッセージ非表示
                                messageclickflag = false;
                                screen_flag = true;
                            }
                            enemy_actor.setHp(ememyHp);

                            //データベース更新
                            onSave(enemy_actor, 2);
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

    //回復計算をする処理
    private void recoveryCalculation(boolean tof , int recovery){
        //HPが減る処理
        int hpPoint = recovery / 60 + 1;    //60分の1ずつ増える
        final int maxHp = binding.myHpBar.getMax();
        final int enemy_maxHp = binding.ememyHpBar.getMax();

        //スレッド
        Thread change_hp = new Thread(new Runnable() {
            @Override
            public void run() {
                int result_HP;
                //エネミーかキャラクターか
                if (!tof){
                    //キャラクターのHP処理
                    result_HP = my_actor.getHp() + recovery;
                    if (result_HP >= maxHp){
                        result_HP = maxHp;
                    }
                    myhp = my_actor.getHp();
                    while (myhp <= result_HP) {

                        //HPが回復しきったら
                        if (myhp == result_HP){
                            //メッセージを消せるようにする
                            if (messageclickflag){
                                //画面タップでメッセージ非表示
                                messageclickflag = false;
                                screen_flag = true;
                            }
                            my_actor.setHp(myhp);
                            //データベース更新
                            onSave(my_actor, 3);
                            break;
                        }
                        //HPをhpPointずつ増やす
                        myhp += hpPoint;
                        if (myhp > result_HP){
                            myhp = result_HP;
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
                    result_HP = ememyHp + recovery;
                    if (result_HP >= enemy_maxHp){
                        result_HP = enemy_maxHp;
                    }
                    //残りHPになるまでループ
                    while (ememyHp <= result_HP){
                        //メッセージを消せるようにする
                        if (ememyHp == result_HP){
                            if (messageclickflag){
                                //画面タップでメッセージ非表示
                                messageclickflag = false;
                                screen_flag = true;
                            }
                            enemy_actor.setHp(ememyHp);

                            //データベース更新
                            onSave(enemy_actor, 2);
                            break;
                        }

                        //HPをhpPointずつ増やす
                        ememyHp += hpPoint;
                        if (ememyHp > result_HP){
                            ememyHp = result_HP;
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

    //エフェクト表示
    private void effect_show(boolean tof, String gif){
        if (tof){
            binding.myEffect.setVisibility(View.VISIBLE);

            //エフェクト
            ImageView imageView = binding.myEffect;
            GlideDrawableImageViewTarget target = new GlideDrawableImageViewTarget(imageView, 1);
            return_gif(gif).into(target);

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //エフェクトを非表示に
                    binding.myEffect.setVisibility(View.INVISIBLE);
                }
            }, 1000); // 1000ミリ秒（1秒）後
        }else {
            binding.enemyEffect.setVisibility(View.VISIBLE);
            //エフェクト
            ImageView imageView = binding.enemyEffect;
            GlideDrawableImageViewTarget target = new GlideDrawableImageViewTarget(imageView, 1);
            return_gif(gif).into(target);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //エフェクトを非表示に
                    binding.enemyEffect.setVisibility(View.INVISIBLE);
                }
            }, 1000); // 1000ミリ秒（1秒）後
        }
    }

    //エフェクトを決める
    private DrawableTypeRequest<Integer> return_gif(String gif){
        DrawableTypeRequest<Integer> re_gif = null;
        switch (gif){
            case "sword_effect":
                re_gif = Glide.with(this).load(R.drawable.sword_effect);
                break;
            case "fire_effect":
                re_gif = Glide.with(this).load(R.drawable.fire_effect);
                break;
            case "recover_effect":
                re_gif = Glide.with(this).load(R.drawable.recover_effect);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + gif);
        }
        return re_gif;
    }

    //ディレイの時間変更
    private void changeTime(Skill skill){
        if (skill.getSkill_subject().equals("double")){
            delayTime = 3600;
        }else {
            delayTime = 1800;
        }
    }

}

