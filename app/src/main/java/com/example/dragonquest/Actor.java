package com.example.dragonquest;

import android.app.Application;

public class Actor extends Application {
    private String name;    //キャラ名
    private int hp;         //ヒットポイント
    private int atk;        //攻撃力
    private int def;        //守備力
    private int dex;        //素早さ
    private Skill skill1;  //スキル1
    private Skill skill2;  //スキル2
    private Skill skill3;  //スキル3
    private Skill skill4;  //スキル4
    private int save_turn; //ターン数
    private Actor save; //メニューでセーブする値を保存

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public Actor(){
        name = "";
        hp = 0;
        atk = 0;
        def = 0;
        dex = 0;
        skill1 = new Skill("");
        skill2 = new Skill("");
        skill3 = new Skill("");
        skill4 = new Skill("");
    }
    //ステータスを受け取り保存する
    public Actor(String set_name, int set_hp, int set_atk, int set_def, int set_dex,
                 Skill set_skill1, Skill set_skill2, Skill set_skill3, Skill set_skill4){
        name = set_name;
        hp = set_hp;
        atk = set_atk;
        def = set_def;
        dex = set_dex;
        skill1 = set_skill1;
        skill2 = set_skill2;
        skill3 = set_skill3;
        skill4 = set_skill4;
    }

    //DB保存用ステータスを受け取り保存する
    public Actor(String set_name, int set_hp, int set_atk, int set_def, int set_dex,
                 Skill set_skill1, Skill set_skill2, Skill set_skill3, Skill set_skill4,int turn){
        name = set_name;
        hp = set_hp;
        atk = set_atk;
        def = set_def;
        dex = set_dex;
        skill1 = set_skill1;
        skill2 = set_skill2;
        skill3 = set_skill3;
        skill4 = set_skill4;
        save_turn = turn;
    }

    //各種ステータスを個別に更新
    public void setName(String set_name){
        name = set_name;
    }
    public void setHp(int set_hp){
        hp = set_hp;
    }
    public void setAtk(int set_atk){
        atk =set_atk;
    }
    public void setDef(int set_def){
        def = set_def;
    }
    public void setDex(int set_dex){
        dex =set_dex;
    }
    private void setSkill1(Skill set_skill1){
        skill1 = set_skill1;
    }
    private void setSkill2(Skill set_skill2){
        skill2 = set_skill2;
    }
    private void setSkill3(Skill set_skill3){
        skill3 = set_skill3;
    }
    private void setSkill4(Skill set_skill4){
        skill4 = set_skill4;
    }


    //各種ステータスを呼び出す
    public String getName(){
        return name;
    }
    public int getHp(){
        return hp;
    }
    public int getAtk(){
        return atk;
    }
    public int getDef(){
        return def;
    }
    public int getDex(){
        return dex;
    }
    public Skill getSkill1(){
        return skill1;
    }
    public Skill getSkill2(){
        return skill2;
    }
    public Skill getSkill3(){
        return skill3;
    }
    public Skill getSkill4(){
        return skill4;
    }

    //スキル名から正しいスキルを返す
    public Skill returnSkill(String name){
        if (skill1.getSkill_name().equals(name)){
            return skill1;
        }else if (skill2.getSkill_name().equals(name)){
            return skill2;
        }else if (skill3.getSkill_name().equals(name)){
            return skill3;
        }else{
            return skill4;
        }
    }

    //変数を受け取って保存する処理
    public void SetActivityActor(String set_name, int set_hp, int set_atk, int set_def, int set_dex,
                                 String set_skill1, String set_skill2, String set_skill3, String set_skill4,int turn){

        skill1 = new Skill(set_skill1);
        skill2 = new Skill(set_skill2);
        skill3 = new Skill(set_skill3);
        skill4 = new Skill(set_skill4);
        save = new Actor(set_name, set_hp, set_atk, set_def, set_dex,
        skill1, skill2, skill3, skill4, turn);
    }

    //変数を返却する処理
    public Actor GetActivityActor(){
        return save;
    }
}
