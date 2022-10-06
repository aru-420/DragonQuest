
package com.example.dragonquest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Binder;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.example.dragonquest.databinding.ActivityNurtureBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;

public class NurtureActivity extends AppCompatActivity {
    private ActivityNurtureBinding binding;

    //変数
    int Event_choices1, Event_choices2, stage_num;

    boolean skill_btn = false;
    boolean skill_full = false;
    boolean skill_notEquals = false;

    //キャラを変える(選択)する際に使用する変数
    private String CharaNameStr[] = new String[2];

    //イベントの名前を保存
    //1ステージ目
    private String Event_Name[] = new String[10];
    //2ステージ目
    private String Event_Name2[] = new String[11];
    //3ステージ目
    private String Event_Name3[] = new String[10];
    //4ステージ目
    private String Event_Name4[] = new String[10];

    //データベース接続用変数
    private DatabaseHelper helper = null;

    //random　生成
    Random rnd = new Random();

    //DBからデータを受け取る際に入れる変数
    String name, skill, skill1, skill2, skill3, skill4;
    int hp, atk, def, dex, turncount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNurtureBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // ヘルパーを準備
        helper = new DatabaseHelper(this);

        //配列の初期化
        //CharaNameStr
        CharaNameStr[0] = "戦士";
        CharaNameStr[1] = "魔法使い";

        //Event_Name
        Event_Name[0] = "木を切り倒した";
        Event_Name[1] = "虫に遭遇した";
        Event_Name[2] = "雨が降ってきた";
        Event_Name[3] = "廃墟がある";
        Event_Name[4] = "水の音がきこえる";
        Event_Name[5] = "日記を書こう";
        Event_Name[6] = "底なし沼にはまった";
        Event_Name[7] = "土砂崩れだ！";
        Event_Name[8] = "食料調達";
        Event_Name[9] = "行商人だ！";

        //Event_Name2
        Event_Name2[0] = "遺跡探索";
        Event_Name2[1] = "オアシスだ！";
        Event_Name2[2] = "夜に歩く";
        Event_Name2[3] = "砂丘を超える";
        Event_Name2[4] = "ラクダに乗る";
        Event_Name2[5] = "人が倒れている！";
        Event_Name2[6] = "ノドが乾いた...";
        Event_Name2[7] = "雨が降り出した";
        Event_Name2[8] = "ピラミッドだ！";
        Event_Name2[9] = "大サソリが現れた！";
        Event_Name2[10] = "砂丘が連なる";

        //Event_Name3
        Event_Name3[0] = "氷壁に阻まれた";
        Event_Name3[1] = "狩りをしよう";
        Event_Name3[2] = "つららが出来ている";
        Event_Name3[3] = "ここはどこだ？";
        Event_Name3[4] = "猛吹雪だ";
        Event_Name3[5] = "山奥に村がある";
        Event_Name3[6] = "洞窟がある";
        Event_Name3[7] = "焚火をしよう";
        Event_Name3[8] = "神秘的な洞窟がある";
        Event_Name3[9] = "山の中で修行だ";

        //Event_Name4
        Event_Name4[0] = "石像が現れた！";
        Event_Name4[1] = "ケルベロスが現れた！";
        Event_Name4[2] = "悪魔が現れた！";
        Event_Name4[3] = "大蛇が現れた！";
        Event_Name4[4] = "どこからか声がする";
        Event_Name4[5] = "部屋に閉じ込められた！";
        Event_Name4[6] = "幻影師が現れた！";
        Event_Name4[7] = "デュラハンが現れた！";
        Event_Name4[8] = "メデューサが現れた！";
        Event_Name4[9] = "骸騎士が現れた！";



        //ステージの判定
        stage_num = stage_db();

        //初期画面の設定
        writeDB();

        //選択肢の更新
        random_Choices();

        //選択肢1の処理
        binding.Choices1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //イベント
                choices_judg("choices1");

                //押されるとターンが増える
                turncount += 1;
                binding.turnCount.setText(turncount + "ターン");

                //選択肢の更新
                random_Choices();
            }
        });

        //選択肢2の処理
        binding.Choices2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //イベント
                choices_judg("choices2");

                //押されるとターンが増える
                turncount += 1;
                binding.turnCount.setText(turncount + "ターン");

                //選択肢の更新
                random_Choices();
            }
        });

        //スタートボタンの処理
        binding.startbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //ステータスなどの更新
                updata_db();

                //BattleActivityに移る
                Intent intent = new Intent(getApplication(), BattleActivity.class);
                startActivity(intent);
            }
        });



        //スキルボタン1の処理
        binding.Skill1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(skill_full = true){
                    skill1 = skill;

                    binding.alert.setText("新しく「" + skill + "」を覚えた！" + "\n" +
                                          "ボタン以外の画面をクリック");

                    binding.Skill1.setText(skill1);

                    //他のボタンを押せなくする
                    binding.Skill2.setEnabled(false);
                    binding.Skill3.setEnabled(false);
                    binding.Skill4.setEnabled(false);

                    skill_full = false;
                }
            }
        });

        //スキルボタン2の処理
        binding.Skill2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(skill_full = true){
                    skill2 = skill;

                    binding.alert.setText("新しく「" + skill + "」を覚えた！" + "\n" +
                            "ボタン以外の画面をクリック");

                    binding.Skill2.setText(skill2);

                    //他のボタンを押せなくする
                    binding.Skill1.setEnabled(false);
                    binding.Skill3.setEnabled(false);
                    binding.Skill4.setEnabled(false);

                    skill_full = false;

                }
            }
        });

        //スキルボタン3の処理
        binding.Skill3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(skill_full = true){
                    skill3 = skill;

                    binding.alert.setText("新しく「" + skill + "」を覚えた！" + "\n" +
                            "ボタン以外の画面をクリック");

                    binding.Skill3.setText(skill3);

                    //他のボタンを押せなくする
                    binding.Skill1.setEnabled(false);
                    binding.Skill2.setEnabled(false);
                    binding.Skill4.setEnabled(false);

                    skill_full = false;

                }
            }
        });

        //スキルボタン4の処理
        binding.Skill4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(skill_full = true){
                    skill4 = skill;

                    binding.alert.setText("新しく「" + skill + "」を覚えた！" + "\n" +
                            "ボタン以外の画面をクリック");

                    binding.Skill4.setText(skill4);

                    //他のボタンを押せなくする
                    binding.Skill1.setEnabled(false);
                    binding.Skill2.setEnabled(false);
                    binding.Skill3.setEnabled(false);

                    skill_full = false;
                }
            }
        });

        //メニューボタン2の処理
        binding.menuButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MenuButton();
                Intent intent = new Intent(getApplication(), Menu.class);
                startActivity(intent);
            }
        });

    }


    //スキルを新しく覚えたとき元の画面に戻す処理
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if(skill_btn == true){
            //ボタンのあるviewを表示状態に
            binding.skills.setVisibility(View.INVISIBLE);

            //メッセージと結果を表示
            binding.message.setVisibility(View.VISIBLE);
            binding.result.setVisibility(View.VISIBLE);

            //アラート(text)を非表示に
            binding.alert.setVisibility(View.INVISIBLE);

            if(turncount >= 10){
                //選択肢(ボタン)を非表示にする
                binding.Choices1.setVisibility(View.INVISIBLE);
                binding.Choices2.setVisibility(View.INVISIBLE);

                //スタートボタンを表示
                binding.startbtn.setVisibility(View.VISIBLE);
            }
            else{
                //選択肢を表示
                binding.Choices1.setVisibility(View.VISIBLE);
                binding.Choices2.setVisibility(View.VISIBLE);
            }

            skill_btn = false;
        }
        return skill_btn;
    }



    //メソッド ↓
    //データベースの読み込み
    protected void writeDB() {
        // 読み込みモードでデータベースをオープン
        try (SQLiteDatabase db = helper.getReadableDatabase()) {

            // データベースから取得する項目を設定
            String[] cols = {DBTables.CharacterTable.CHARA_SAVE_NAME, DBTables.CharacterTable.CHARA_SAVE_HP,
                    DBTables.CharacterTable.CHARA_SAVE_ATK, DBTables.CharacterTable.CHARA_SAVE_DEF, DBTables.CharacterTable.CHARA_SAVE_DEX,
                    DBTables.CharacterTable.CHARA_SAVE_SKILL1, DBTables.CharacterTable.CHARA_SAVE_SKILL2, DBTables.CharacterTable.CHARA_SAVE_SKILL3, DBTables.CharacterTable.CHARA_SAVE_SKILL4,
                    DBTables.CharacterTable.CHARA_SAVE_TURN
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
                turncount = Integer.parseInt(cursor.getString(9));
            }

            //ステータス表示
            binding.txtHP.setText(Integer.toString(hp));
            binding.txtATK.setText(Integer.toString(atk));
            binding.txtDEF.setText(Integer.toString(def));
            binding.txtDEX.setText(Integer.toString(dex));

            //スキル表示
            binding.Skill1.setText(skill1);
            binding.Skill2.setText(skill2);
            binding.Skill3.setText(skill3);
            binding.Skill4.setText(skill4);

            //ターン数書き換え
            binding.turnCount.setText(turncount + "ターン");


            //画像の変更
            if(name.equals(CharaNameStr[0])){
                //画像の変更(warrior)
                ImageView imageView  = findViewById(R.id.character_Image_nu);
                imageView.setImageResource(R.drawable.warrior);
            }
            else if(name.equals(CharaNameStr[1])){
                //画像の変更(magician)
                ImageView imageView  = findViewById(R.id.character_Image_nu);
                imageView.setImageResource(R.drawable.magician);
            }

            //背景の変更
            switch (stage_num - 1){
                case 0:
                    System.out.println("ok");
                    binding.NurtureScene.setBackgroundResource(R.drawable.background);
                    break;
                case 1:
                    binding.NurtureScene.setBackgroundResource(R.drawable.background2);
                    break;
                case 2:
                    binding.NurtureScene.setBackgroundResource(R.drawable.background4);
                    break;
                case 3:
                    binding.NurtureScene.setBackgroundResource(R.drawable.background3);
                    break;
                default:
                    binding.NurtureScene.setBackgroundResource(R.drawable.background);
            }
        }
    }

    //いまが何ステージ目なのかをもらう処理
    protected int stage_db(){
        //返却値
        int res = 0;

        try (SQLiteDatabase db = helper.getReadableDatabase()) {

            // データベースから取得する項目を設定
            String[] cols = {DBTables.CharacterTable.CHARA_SAVE_STAGE};
            //where文
            String my_where = DBTables.CharacterTable.CHARA_SAVE_ID + " = 1";

            Cursor cursor;
            cursor = db.query(DBTables.CharacterTable.TABLE_NAME, cols, my_where,
                    null, null, null, null, null);

            if (cursor.moveToFirst()) {
                res = Integer.parseInt(cursor.getString(0));
            }
        }
        return res;
    }

    //選択ボタンを押したときの処理
    protected void EventJson(String NAME){
        try {
            //アセットマネージャー
            AssetManager assetManager = getResources().getAssets();
            // jsonを取得する
            //ファイル名を指定
            InputStream inputStream = assetManager.open("farst_event.json");
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
            //EventName
            String E_name = json.getJSONObject(NAME).getString("event_name");
            //イベントの結果
            String content = json.getJSONObject(NAME).getString("event_content");
            //ステータスの上昇値
            int E_hp = json.getJSONObject(NAME).getInt("event_hp");
            int E_atk = json.getJSONObject(NAME).getInt("event_atk");
            int E_def = json.getJSONObject(NAME).getInt("event_def");
            int E_dex = json.getJSONObject(NAME).getInt("event_dex");
            //スキル取得
            skill = json.getJSONObject(NAME).getString("event_skill");

            //ステータスを上昇値の値だけ増やす
            hp += E_hp;
            atk += E_atk;
            def += E_def;
            dex += E_dex;

            //増えたステータスがマイナスになった場合1にする
            if(hp < 1){
                hp = 1;
            }else if(atk < 1){
                atk = 1;
            }else if(def < 1){
                def = 1;
            }else if(dex < 1){
                dex = 1;
            }
            //増えたステータスが9999以上になった場合9999にする
            if(hp > 9999){
                hp = 9999;
            }else if(atk > 9999){
                atk = 9999;
            }else if(def > 9999){
                def = 9999;
            }else if(dex > 9999){
                dex = 9999;
            }


            //ボタンの初期化
            binding.Skill1.setBackgroundColor(Color.BLUE);
            binding.Skill2.setBackgroundColor(Color.BLUE);
            binding.Skill3.setBackgroundColor(Color.BLUE);
            binding.Skill4.setBackgroundColor(Color.BLUE);
            //ボタンを選択可能状態に
            binding.Skill1.setEnabled(true);
            binding.Skill2.setEnabled(true);
            binding.Skill3.setEnabled(true);
            binding.Skill4.setEnabled(true);

            //スキルの有無
            if(!(skill.equals(""))){
                //空きのあるスキルに新しいスキルを習得させる
                if(skill1.equals("") || skill1.equals(skill)){
                    if(skill1.equals(skill)){
                        skill_notEquals = true;
                        skill = "";
                    }
                    else{
                        skill1 = skill;
                        binding.alert.setText("新しく「" + skill + "」を覚えた！");
                        skill_btn = true;
                    }
                }
                else if(skill2.equals("") || skill2.equals(skill)){
                    if(skill2.equals(skill)){
                        skill_notEquals = true;
                        skill = "";
                    }
                    else{
                        skill2 = skill;
                        binding.alert.setText("新しく「" + skill + "」を覚えた！");
                        skill_btn = true;
                    }
                }
                else if(skill3.equals("") || skill3.equals(skill)){
                    if(skill3.equals(skill)){
                        skill_notEquals = true;
                        skill = "";
                    }
                    else{
                        skill3 = skill;
                        binding.alert.setText("新しく「" + skill + "」を覚えた！");
                        skill_btn = true;
                    }
                }
                else if(skill4.equals("") || skill4.equals(skill)){
                    if(skill4.equals(skill)){
                        skill_notEquals = true;
                        skill = "";
                    }
                    else{
                        skill4 = skill;
                        binding.alert.setText("新しく「" + skill + "」を覚えた！");
                        skill_btn = true;
                    }
                }
                else{
                    binding.alert.setText("新しく「" + skill + "」を覚えたい" + "\n" +
                                          "忘れるスキルをタップしてください。" + "\n" +
                                          "覚えなくていい場合はスキル以外の画面をタップ");

                    skill_btn = true;

                    //スキルがいっぱいの状態で覚えようとしている状態を保持
                    skill_full = true;
                }

                if(skill_notEquals == false || skill_full == true){
                    //習得してあるスキルをテキストに書き込む
                    binding.Skill1.setText(skill1);
                    binding.Skill2.setText(skill2);
                    binding.Skill3.setText(skill3);
                    binding.Skill4.setText(skill4);

                    //スキルボタンを押して大丈夫かの判定
                    if(skill_full == true){
                        binding.Skill1.setEnabled(true);
                        binding.Skill2.setEnabled(true);
                        binding.Skill3.setEnabled(true);
                        binding.Skill4.setEnabled(true);
                        skill_full = false;
                    }
                    else {
                        binding.Skill1.setEnabled(false);
                        binding.Skill2.setEnabled(false);
                        binding.Skill3.setEnabled(false);
                        binding.Skill4.setEnabled(false);
                    }

                    //スキルのないボタンを灰色にする
                    if(skill1.equals("")){
                        binding.Skill1.setBackgroundColor(Color.GRAY);
                    }
                    if(skill2.equals("")){
                        binding.Skill2.setBackgroundColor(Color.GRAY);
                    }
                    if(skill3.equals("")){
                        binding.Skill3.setBackgroundColor(Color.GRAY);
                    }
                    if(skill4.equals("")){
                        binding.Skill4.setBackgroundColor(Color.GRAY);
                    }

                    //ボタンのあるviewを表示状態に
                    binding.skills.setVisibility(View.VISIBLE);

                    //メッセージと結果を非表示に
                    binding.message.setVisibility(View.INVISIBLE);
                    binding.result.setVisibility(View.INVISIBLE);

                    //選択肢を非表示に
                    binding.Choices1.setVisibility(View.INVISIBLE);
                    binding.Choices2.setVisibility(View.INVISIBLE);

                    //スタートボタンを非表示に
                    binding.startbtn.setVisibility(View.INVISIBLE);

                    //アラート(text)を表示
                    binding.alert.setVisibility(View.VISIBLE);

                }
                else{
                    //ボタンのあるviewを非表示状態に
                    binding.skills.setVisibility(View.INVISIBLE);

                    //選択肢を表示
                    binding.Choices1.setVisibility(View.VISIBLE);
                    binding.Choices2.setVisibility(View.VISIBLE);

                    //メッセージと結果を表示
                    binding.message.setVisibility(View.VISIBLE);
                    binding.result.setVisibility(View.VISIBLE);

                    skill_notEquals = false;
                }
            }

            //画面に結果を表示//
            //メッセージ
            binding.message.setText(content);
            //ステータス
            binding.txtHP.setText(Integer.toString(hp));
            binding.txtATK.setText(Integer.toString(atk));
            binding.txtDEF.setText(Integer.toString(def));
            binding.txtDEX.setText(Integer.toString(dex));
            //結果表示に上昇ステータスを表示する
            binding.result.setText("HPが" + Integer.toString(E_hp) + "上がった" + "\n" +
                                    "ATKが" + Integer.toString(E_atk) + "上がった" + "\n" +
                                    "DEFが" + Integer.toString(E_def) + "上がった" + "\n" +
                                    "DEXが" + Integer.toString(E_dex) + "上がった");

        }catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    //ステージ判定
    protected void  choices_judg(String choice_num){
        //ステージの判定
        if(choice_num.equals("choices1")){
            if(stage_num == 1){
                //イベント
                EventJson(Event_Name[Event_choices1]);
            }else if(stage_num == 2){
                //イベント
                EventJson(Event_Name2[Event_choices1]);
            }else if(stage_num == 3){
                //イベント
                EventJson(Event_Name3[Event_choices1]);
            }else if(stage_num == 4){
                //イベント
                EventJson(Event_Name4[Event_choices1]);
            }
        }else if(choice_num.equals("choices2")){
            if(stage_num == 1){
                //イベント
                EventJson(Event_Name[Event_choices2]);
            }else if(stage_num == 2){
                //イベント
                EventJson(Event_Name2[Event_choices2]);
            }else if(stage_num == 3){
                //イベント
                EventJson(Event_Name3[Event_choices2]);
            }else if(stage_num == 4){
                //イベント
                EventJson(Event_Name4[Event_choices2]);
            }
        }
    }

    //ランダムな数字で選択肢をきめる
    protected void rad_num(int getint){
        //選択肢(ボタン)をランダムで生成
        Event_choices1 = rnd.nextInt(getint);
        Event_choices2 = rnd.nextInt(getint);
        //被った場合被らなくなるまで繰り返し
        while (Event_choices1 == Event_choices2){
            Event_choices1 = rnd.nextInt(getint);
            Event_choices2 = rnd.nextInt(getint);
        }
    }

    //選択肢を表示
    protected void random_Choices(){
        //ステージ1
        if(stage_num == 1){
            rad_num(10);
            //選択肢(ボタン)にイベントの名前を表示
            binding.Choices1.setText(Event_Name[Event_choices1]);
            binding.Choices2.setText(Event_Name[Event_choices2]);
        //ステージ2
        }else if(stage_num == 2){
            rad_num(11);
            //選択肢(ボタン)にイベントの名前を表示
            binding.Choices1.setText(Event_Name2[Event_choices1]);
            binding.Choices2.setText(Event_Name2[Event_choices2]);
        //ステージ3
        }else if(stage_num == 3){
            rad_num(10);
            //選択肢(ボタン)にイベントの名前を表示
            binding.Choices1.setText(Event_Name3[Event_choices1]);
            binding.Choices2.setText(Event_Name3[Event_choices2]);
        //ステージ4
        }else if(stage_num == 4){
            rad_num(10);
            //選択肢(ボタン)にイベントの名前を表示
            binding.Choices1.setText(Event_Name4[Event_choices1]);
            binding.Choices2.setText(Event_Name4[Event_choices2]);
        }

        //スキルの説明テキストを非表示に
        if(skill_btn == false){
            binding.alert.setVisibility(View.INVISIBLE);
        }

        //turncountが10を超えた場合の処理
        if(turncount >= 10 && skill_btn == false){
            //スタートボタンを表示する
            binding.startbtn.setVisibility(View.VISIBLE);
            //選択肢ボタンを非表示
            binding.Choices1.setVisibility(View.INVISIBLE);
            binding.Choices2.setVisibility(View.INVISIBLE);
        }
    }

    //スタートを押した際の処理
    protected void updata_db(){
        //ターンが規定以上ならステージを1増やす
        if(turncount >= 10){
            stage_num += 1;
        }

        // 入力されたタイトルとコンテンツをContentValuesに設定
        // ContentValuesは、項目名と値をセットで保存できるオブジェクト
        ContentValues cv = new ContentValues();
        cv.put(DBTables.CharacterTable.CHARA_SAVE_NAME, name);
        cv.put(DBTables.CharacterTable.CHARA_SAVE_HP, hp);
        cv.put(DBTables.CharacterTable.CHARA_SAVE_ATK, atk);
        cv.put(DBTables.CharacterTable.CHARA_SAVE_DEF, def);
        cv.put(DBTables.CharacterTable.CHARA_SAVE_DEX, dex);
        cv.put(DBTables.CharacterTable.CHARA_SAVE_SKILL1, skill1);
        cv.put(DBTables.CharacterTable.CHARA_SAVE_SKILL2, skill2);
        cv.put(DBTables.CharacterTable.CHARA_SAVE_SKILL3, skill3);
        cv.put(DBTables.CharacterTable.CHARA_SAVE_SKILL4, skill4);
        cv.put(DBTables.CharacterTable.CHARA_SAVE_TURN, turncount);

        //where文 今回はidを指定して
        String where = DBTables.CharacterTable.CHARA_SAVE_ID + " = 1";

        // 書き込みモードでデータベースをオープン
        try (SQLiteDatabase db = helper.getWritableDatabase()) {
            //アップデート
            db.update(DBTables.CharacterTable.TABLE_NAME, cv, where, null);
        }
    }

    protected void MenuButton(){
        Actor save = (Actor) this.getApplication();
        save.SetActivityActor(name,hp,atk,def,dex,skill1,skill2,skill3,skill4,turncount);
    }
}