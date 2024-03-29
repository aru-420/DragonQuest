package com.example.dragonquest;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

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

    //仮書き込み
//    Actor save = (Actor) this.getApplication();
//    save.SetActivityActor(name,hp,atk,def,dex,skill1,skill2,skill3,skill4,turncount);

    //仮読み込み
    //    Actor get_save = (Actor) this.getApplication();
    //    Actor actor = get_save.GetActivityActor();
    //      510行目あたりのDB保存処理を参照


    //自身のステータスを保存するクラス
    Actor my_actor;
    Actor my_feast;
    //エネミーステータスを保存するクラス
    Actor enemy_actor;
    //メッセージをクリックしたときに消えるかどうかのフラグ
    boolean messageclickflag = false;
    boolean screen_flag;
    //ボタンを複数押せないようにする
    boolean button_not_double = true;
    //レイアウト変更
    private LinearLayout.LayoutParams layoutParams;


    //データベース接続用変数
    private DatabaseHelper helper = null;

    //メッセージテキスト
    private String messagetext;

    private Handler handler;//スレッド内でテキストをいじる際に使用
    private int myhp = 100; //キャラのHPを保存する変数
    private int ememyHp = 100;  //エネミーのHPを保存する変数
    private int delayTime = 2000;   //ディレイタイムの設定
    private MediaPlayer mediaPlayer;    //BGM再生
    private SoundPool soundPool;
    private int mp3_sword, mp3_ice;
    private int mp3_heal;
    private int mp3_fire;
    private int stage_num;  //何ステージ目か

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

        screen_flag = true;
        //BGM
        playFromMediaPlayer();

        Actor save = (Actor) this.getApplication();

        //効果音
        //soundPoolの初期化
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            soundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        }else {
            AudioAttributes attr = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setAudioAttributes(attr)
                    //パラメーターはリソースの数に合わせる
                    .setMaxStreams(4)
                    .build();
        }
        //効果音読み込み
        mp3_fire = soundPool.load(this, R.raw.fire1,1);
        mp3_heal = soundPool.load(this, R.raw.heaal,1);
        mp3_sword = soundPool.load(this, R.raw.sword,1);
        mp3_ice = soundPool.load(this, R.raw.ice_effect,1);

        //エフェクト初期非表示
        binding.myEffect.setVisibility(View.INVISIBLE);
        binding.enemyEffect.setVisibility(View.INVISIBLE);

        //キャラ作成
        Create_Actor();

        //初期非表示
        binding.grid.setVisibility(View.INVISIBLE);;    //スキルボタン非表示
        binding.battleEndButton.setVisibility(View.INVISIBLE);  //バトル終了ボタン非表示

        //メニューボタン
        binding.menuButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //仮書き込み

                save.SetActivityActor(my_feast.getName(),my_feast.getHp(),
                        my_feast.getAtk(),my_feast.getDef(),my_feast.getDex(),
                        my_feast.getSkill1().getSkill_name(),my_feast.getSkill2().getSkill_name(),
                        my_feast.getSkill3().getSkill_name(),my_feast.getSkill4().getSkill_name(),10);

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

        binding.battleMessage.setText(enemy_actor.getName() + "が現れた！");

    }

    //下限上限
    private void StatusMaxMin(Actor actor){
        //下限
        if (actor.getHp() < 1){
            actor.setHp(1);
        }
        if (actor.getAtk() < 1){
            actor.setAtk(1);
        }
        if (actor.getDex() < 1){
            actor.setDex(1);
        }
        if (actor.getDef() < 1){
            actor.setDef(1);
        }

        //上限
        if (actor.getHp() > 9999){
            actor.setHp(9999);
        }
        if (actor.getAtk() > 9999){
            actor.setAtk(9999);
        }
        if (actor.getDex() > 9999){
            actor.setDex(9999);
        }
        if (actor.getDef() > 9999){
            actor.setDef(9999);
        }
    }

    //bgm再生
    private void playFromMediaPlayer() {
        //mediaPlayer = MediaPlayer.create(this,R.raw.bgm_battle);
        //mediaPlayer.start();
    }

    //ボタンの色変えと処理
    @SuppressLint("ClickableViewAccessibility")
    private void buttonsColor(Button button_skill, Skill skills){
        if (skills.getSkill_subject().equals("my")){
            //回復効果なら緑
            button_skill.setBackgroundColor(Color.GREEN);
        }else if (skills.getSkill_subject().equals("power_up")){
            //バフ効果なら赤色
            button_skill.setBackgroundColor(Color.RED);
        }else if (skills.getSkill_subject().equals("GROW_HERO")){
            //GROW_HEROなら文字赤色
            button_skill.setTextColor(Color.RED);
            button_skill.setTextSize(16);
        }
        //スキル4がなければボタンを押せなくして灰色にする
        if (button_skill.getText().equals("")) {
            //button_skill.setEnabled(false);
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
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                //メッセージの非表示
                binding.battleMessage.setVisibility(View.INVISIBLE);
                //スキルボタンの表示
                binding.grid.setVisibility(View.VISIBLE);
            }
        }
        return false;
    }


    //バトル順序
    protected void BattleStart(Skill skill){
        //敵のスキル決定
        Skill enemy_skill = getEnemyUseSkill();

        //素早さを比較してダメ――ジ処理に移行
        if (!messageclickflag){
            if (my_actor.getDex() >= enemy_actor.getDex()){
                onShow(skill, enemy_skill ,false, true);
            }else {
                onShow(enemy_skill, skill,true, true);
            }
        }
    }


    // データを表示する
    protected void onShow(Skill skill, Skill next_skill , boolean tof, boolean end) {
        //ディレイ時間変更
        changeTime(skill);

        //ダメージ系スキル
        if (skill.getSkill_subject().equals("enemy") || skill.getSkill_subject().equals("element")){
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
            if (my_actor.getHp() != 0 && enemy_actor.getHp() != 0){
            handler.postDelayed(() -> {
                //２秒待って実行

                    damegeRandom(tof, skill);

            }, delayTime/2 ); // 約2000ミリ秒（2秒）後
            }else {
                //GameEndCheck();
            }

        }else if (skill.getSkill_subject().equals("power_up")){
            Power_up(tof, skill);
            if (!end){
                messageclickflag = false;
            }
        }else if (skill.getSkill_subject().equals("GROW_HERO")){
            Grow_hero(tof, skill);
            if (!end){
                messageclickflag = false;
            }
        }
        //行動してないキャラがいるか
        if (end){
            //次の行動
            handler.postDelayed(() -> {
                //キャラがやられていないか敵がやられていないか
                if (my_actor.getHp() != 0 && enemy_actor.getHp() != 0){
                    //画面を押したときとボタンを押せるようになるフラグの変更
                    messageclickflag = true;
                    button_not_double = true;
                    //行動してないキャラの行動
                    onShow(next_skill, skill, !tof, false);
                }else {
                    //ゲームオーバーかゲームクリアか
                    GameEndCheck();
                }
                },delayTime);

        }else{
            handler.postDelayed(() -> {
                if (my_actor.getHp() != 0 && enemy_actor.getHp() != 0){
                    screen_flag = true;
                    onSave(enemy_actor, 2);
                    onSave(my_actor, 3);
                }else {
                    GameEndCheck();
                }

            },delayTime);

        }
    }

    //ゲームの終わりかどうか
    private void GameEndCheck(){
        //キャラのHPが０ならゲームオーバー、敵のHPが０ならゲームクリア
        if (my_actor.getHp() == 0){
            GameOver();
        }else if (enemy_actor.getHp() == 0){
            GameClear();
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
        //ダメージがatk/20以下なら
        if (damage <= atk/20){
            damage = atk/20 + 1;
        }
        return damage;
    }


    //キャラクターがやられたときの処理
    private void GameOver(){
        String name = my_actor.getName();
        ImageView image = binding.myCharaImage;
        if (name.equals("戦士")){
            image.setImageResource(R.drawable.warrior_dead);
        }else if (name.equals("魔法使い")){
            image.setImageResource(R.drawable.magition_dead);
        }
        //エフェクト
        AlphaAnimation alphaFadeout = new AlphaAnimation(1.0f, 0.0f);
        // animation時間
        alphaFadeout.setDuration(1000);
        // animationが終わったそのまま表示にする
        alphaFadeout.setFillAfter(true);

        binding.myCharaImage.startAnimation(alphaFadeout);
        //ゲームオーバー
        messagetext = my_actor.getName() + "はやられてしまった！";
        binding.battleMessage.setText(messagetext);
        binding.battleMessage.setBackgroundColor(Color.RED);
        binding.battleEndButton.setText("リザルト画面へ");
        binding.battleEndButton.setVisibility(View.VISIBLE);
        //DB更新
        EndDBChange(false);
        //リザルト画面へ移行
        binding.battleEndButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), ResultActivity.class);
                startActivity(intent);
            }
        });
    }

    //戦闘終了
    private void GameClear() {
        handler.postDelayed(() -> {
            //キャラクター表情変更
            String name = my_actor.getName();
            ImageView image = binding.myCharaImage;
            if (name.equals("戦士")){
                image.setImageResource(R.drawable.warrior_win);
            }else if (name.equals("魔法使い")){
                image.setImageResource(R.drawable.magition_win);
            }
        },500);
        messagetext = enemy_actor.getName() + "を倒した！";
        binding.battleMessage.setText(messagetext);
        //エフェクト
        AlphaAnimation alphaFadeout = new AlphaAnimation(1.0f, 0.0f);
        // animation時間
        alphaFadeout.setDuration(1000);
        // animationが終わったそのまま表示にする
        alphaFadeout.setFillAfter(true);

        binding.ememyImage.startAnimation(alphaFadeout);

        if (enemy_actor.getName().equals("魔王")){
            binding.battleEndButton.setText("リザルト画面へ");
            binding.battleEndButton.setVisibility(View.VISIBLE);
            EndDBChange(false);
            LastDBUpdate();
            //リザルト画面へ移行
            binding.battleEndButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplication(), ResultActivity.class);
                    startActivity(intent);
                }
            });
        }else {
            binding.battleEndButton.setText("次の育成へ");
            binding.battleEndButton.setVisibility(View.VISIBLE);

            //DB更新
            EndDBChange(true);
            //ステージカウント
            StageDBUpdate(stage_num);
            //育成画面へ移行
            binding.battleEndButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplication(), NurtureActivity.class);
                    startActivity(intent);
                }
            });
        }


    }

    //回復スキル
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

    //バフスキル
    private void Power_up(boolean MoE, Skill skill){
        //加算結果を入れる変数
        int result;
        //int型に変換
        int skill_effect = (int)(0+skill.getSkill_effect());

        if (MoE){
            result = enemy_actor.getAtk() + skill_effect;
            enemy_actor.setAtk(result);
            //表示するメッセージ
            messagetext = enemy_actor.getName() + "の" + skill.getSkill_name() + "\n" +  "攻撃力が"
                    + skill_effect + "上昇!";
        }else {
            result = my_actor.getAtk() + skill_effect;
            my_actor.setAtk(result);
            //表示するメッセージ
            messagetext = my_actor.getName() + "の" + skill.getSkill_name() + "\n" +  "攻撃力が"
                    + skill_effect + "上昇!";
        }
        //エフェクト表示
        effect_show(!MoE, skill.getSkill_gif());
        //バトルメッセージに表示
        binding.battleMessage.setText(messagetext);
    }

    //バフスキル
    private void Grow_hero(boolean MoE, Skill skill){
        //加算結果を入れる変数
        int atk,def,dex,view_atk,view_def,view_dex;
        //int型に変換
        int skill_effect = (int)(0+skill.getSkill_effect());

        atk = (int) (my_actor.getAtk() * skill.getSkill_effect());
        def = (int) (my_actor.getDef() * skill.getSkill_effect());
        dex = (int) (my_actor.getDex() * skill.getSkill_effect());

        if (atk < 9999 && def < 9999 && dex < 9999 ){
            view_atk = atk - my_actor.getAtk();
            view_def = def - my_actor.getDef();
            view_dex = dex - my_actor.getDex();
        }else {
            view_atk = 9999 - my_actor.getAtk();
            view_def = 9999 - my_actor.getDef();
            view_dex = 9999 - my_actor.getDex();
            StatusMaxMin(my_actor);
        }

        my_actor.setAtk(atk);
        my_actor.setDef(def);
        my_actor.setDex(dex);

        StatusMaxMin(my_actor);


        //表示するメッセージ
        messagetext = my_actor.getName() + "の" + skill.getSkill_name() + "\n" +  "攻撃力が"
                + view_atk + "上昇!\n"
                + "防御力が"  + view_def + "上昇!\n"
                + "素早さが"  + view_dex + "上昇!";

        //エフェクト表示
        effect_show(!MoE, skill.getSkill_gif());
        //バトルメッセージに表示
        binding.battleMessage.setText(messagetext);
    }

    //DBにアップデート保存
    private void onSave(Actor actor, int id){
        // 入力されたタイトルとコンテンツをContentValuesに設定
        // ContentValuesは、項目名と値をセットで保存できるオブジェクト
        ContentValues cv = new ContentValues();
        cv.put(CharacterTable.CHARA_SAVE_NAME, actor.getName());
        cv.put(CharacterTable.CHARA_SAVE_HP, actor.getHp());
        cv.put(CharacterTable.CHARA_SAVE_ATK, actor.getAtk());
        cv.put(CharacterTable.CHARA_SAVE_DEF, actor.getDef());
        cv.put(CharacterTable.CHARA_SAVE_DEX, actor.getDex());
        cv.put(CharacterTable.CHARA_SAVE_SKILL1, actor.getSkill1().getSkill_name());
        cv.put(CharacterTable.CHARA_SAVE_SKILL2, actor.getSkill2().getSkill_name());
        cv.put(CharacterTable.CHARA_SAVE_SKILL3, actor.getSkill3().getSkill_name());
        cv.put(CharacterTable.CHARA_SAVE_SKILL4, actor.getSkill4().getSkill_name());

        //where文 今回はidを指定して
        String where = CharacterTable.CHARA_SAVE_ID + " = " + id;

        // 書き込みモードでデータベースをオープン
        try (SQLiteDatabase db = helper.getWritableDatabase()) {

            //アップデート
            db.update(CharacterTable.TABLE_NAME, cv, where, null);

        }
    }

    //戦闘勝利時ステージカウント更新
    private void StageDBUpdate(int num){
        //ステージカウント更新
        num += 1;
        ContentValues cv = new ContentValues();
        cv.put(CharacterTable.CHARA_SAVE_STAGE, num);
        cv.put(CharacterTable.CHARA_SAVE_TURN, 0);
        try (SQLiteDatabase db = helper.getWritableDatabase()) {
            //データベース更新
            String where = CharacterTable.CHARA_SAVE_ID + " = " + 1;
            db.update(CharacterTable.TABLE_NAME, cv, where, null);
        }
    }

    //最終戦闘終了更新
    private void LastDBUpdate(){
        //ステージカウント更新
        ContentValues cv = new ContentValues();
        cv.put(CharacterTable.CHARA_SAVE_TURN, 11);
        try (SQLiteDatabase db = helper.getWritableDatabase()) {
            //データベース更新
            String where = CharacterTable.CHARA_SAVE_ID + " = " + 1;
            db.update(CharacterTable.TABLE_NAME, cv, where, null);
        }
    }

    //バトル終了時データベースを更新する
    private void EndDBChange(boolean end){
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

            if (!end){
                //キャラクターのターン数リセット
                ContentValues cv2 = new ContentValues();
                //cv2.put(CharacterTable.CHARA_SAVE_TURN, 0);
                //cv2.put(CharacterTable.CHARA_SAVE_STAGE, 1);
                //where = CharacterTable.CHARA_SAVE_ID + " = " + 1;
                //db.update(CharacterTable.TABLE_NAME, cv2, where, null);
            }

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
            //where文 戦闘中のデータ
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
                            my_feast = new Actor(cursor2.getString(0), cursor2.getInt(1), cursor2.getInt(2),
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
                        my_feast = new Actor(cursor2.getString(0), cursor2.getInt(1), cursor2.getInt(2),
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
                        stage_num = cursor2.getInt(9);
                        enemy_names = getEnemyName(stage_num);
                    }

                }

                onSave(my_actor, 4);
                onSave(my_actor, 3);
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
                        String skill3 = enemy_json.getString("SKILL3");
                        String skill4 = enemy_json.getString("SKILL4");
                        Skill enemy_skill1 = SettingSkill(1,skill1);
                        Skill enemy_skill2 = SettingSkill(2,skill2);
                        Skill enemy_skill3 = SettingSkill(1,skill3);
                        Skill enemy_skill4 = SettingSkill(2,skill4);
                        enemy_actor = new Actor(enemy_name, enemy_hp, enemy_atk, enemy_def, enemy_dex,
                                enemy_skill1,enemy_skill2,enemy_skill3,enemy_skill4);
                        onSave(enemy_actor, 2);
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

    String[] Enemy_names ={"enemyID1","enemyID2","enemyID3","enemyID4"};
    //エネミーの決定
    private String getEnemyName(int turn){
        ImageView image = binding.ememyImage;
        int num = turn - 1;
        switch (num){
            //スライム
            case 0:
                image.setImageResource(R.drawable.slime);
                binding.BattleSecen.setBackgroundResource(R.drawable.background);   //背景変更
                break;
            //ゴーレム
            case 1:
                image.setImageResource(R.drawable.golem);
                binding.BattleSecen.setBackgroundResource(R.drawable.background2);   //背景変更
                break;
            case 2:
                image.setImageResource(R.drawable.dragon);
                binding.BattleSecen.setBackgroundResource(R.drawable.background4);   //背景変更
                break;
            case 3:
                image.setImageResource(R.drawable.lastboss);
                binding.BattleSecen.setBackgroundResource(R.drawable.background3);   //背景変更
                break;
            default:
                image.setImageResource(R.drawable.golem);
                binding.BattleSecen.setBackgroundResource(R.drawable.background);   //背景変更
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

    //敵のスキルをランダム決定
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
        int hpPoint = damage / 60 + 1;    //ダメージの比率で減る
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

                            //ループ終了
                            break;
                        }

                        //メッセージを消せるようにする
                        if (ememyHp == result_HP){
                            if (messageclickflag){
                                //画面タップでメッセージ非表示
                                messageclickflag = false;
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
                            }
                            my_actor.setHp(myhp);
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
                            }
                            enemy_actor.setHp(ememyHp);
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
                soundPool.play(mp3_sword,1f , 1f, 0, 0, 1f);
                break;
            case "fire_effect":
                re_gif = Glide.with(this).load(R.drawable.fire_effect);
                soundPool.play(mp3_fire,1f , 1f, 0, 0, 1f);
                break;
            case "recover_effect":
                re_gif = Glide.with(this).load(R.drawable.recover_effect);
                soundPool.play(mp3_heal,1f , 1f, 0, 0, 1f);
                break;
            case"ice_effect":
                re_gif = Glide.with(this).load(R.drawable.ice2_effect);
                soundPool.play(mp3_ice,1f , 1f, 0, 0, 1f);
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

