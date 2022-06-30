package com.example.dragonquest;

public class Skill {
    private int skill_num;
    private String skill_name;
    private Double skill_effect;
    private String skill_subject;
    private String skill_gif;
    private String skill_context;

    //値をセット
    public Skill(int num, String name, Double effect, String subject,String gif , String context){
        skill_num = num;
        skill_name = name;
        skill_effect = effect;
        skill_subject = subject;
        skill_gif = gif;
        skill_context =context;
    }
    public void setSkill_num(int num){
        skill_num = num;
    }
    public void setSkill_name(String name){
        skill_name = name;
    }
    public void setSkill_effect(Double effect){
        skill_effect = effect;
    }
    public void setSkill_subject(String subject){
        skill_subject = subject;
    }
    public void setSkill_gif(String gif){
        skill_gif = gif;
    }

    //値を呼び出し
    public int getSkill_num(){
        return skill_num;
    }
    public String getSkill_name(){
        return skill_name;
    }
    public Double getSkill_effect(){
        return skill_effect;
    }
    public String getSkill_subject(){
        return skill_subject;
    }
    public String getSkill_gif(){return skill_gif;}
    public String getSkill_context(){return skill_context;}

}
