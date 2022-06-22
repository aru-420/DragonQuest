package com.example.dragonquest;

public class Actor {
    private String name;    //キャラ名
    private int hp;         //ヒットポイント
    private int atk;        //攻撃力
    private int def;        //守備力
    private int dex;        //素早さ
    private String skill1;  //スキル1
    private String skill2;  //スキル2
    private String skill3;  //スキル3
    private String skill4;  //スキル4

    //ステータスを受け取り保存する
    public Actor(String set_name, int set_hp, int set_atk, int set_def, int set_dex,
                 String set_skill1, String set_skill2, String set_skill3, String set_skill4){
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
    private void setSkill1(String set_skill1){
        skill1 = set_skill1;
    }
    private void setSkill2(String set_skill2){
        skill2 = set_skill2;
    }
    private void setSkill3(String set_skill3){
        skill3 = set_skill3;
    }
    private void setSkill4(String set_skill4){
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
    public String getSkill1(){
        return skill1;
    }
    public String getSkill2(){
        return skill2;
    }
    public String getSkill3(){
        return skill3;
    }
    public String getSkill4(){
        return skill4;
    }
}
