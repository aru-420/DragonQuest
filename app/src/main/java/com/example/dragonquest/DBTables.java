package com.example.dragonquest;

import android.provider.BaseColumns;

public class DBTables {


    //誤ってインスタンス化しないようprivate宣言
    private DBTables(){}

    //テーブル内容を定義
    public static class SkillTable implements BaseColumns{
        // BaseColumns インターフェースを実装することで、内部クラスは_IDを継承できる
        public static final String TABLE_NAME           = "skill_table";
        public static final String COLUMN_NAME_SKILL_ID    = "skill_id";
        public static final String COLUMN_NAME_SKILL_NAME = "skill_name";
        public static final String COLUMN_NAME_SKILL_EFFECT   = "skill_effect";
    }

    //テーブル内容を定義
    public static class CharacterTable implements BaseColumns{
        // BaseColumns インターフェースを実装することで、内部クラスは_IDを継承できる
        public static final String TABLE_NAME           = "chara_save_table";
        public static final String CHARA_SAVE_ID    = "chara_save_id";
        public static final String CHARA_SAVE_NAME = "chara_save_name";
        public static final String CHARA_SAVE_HP   = "chara_save_hp";
        public static final String CHARA_SAVE_ATK   = "chara_save_atk";
        public static final String CHARA_SAVE_DEF   = "chara_save_def";
        public static final String CHARA_SAVE_DEX   = "chara_save_dex";
        public static final String CHARA_SAVE_SKILL1   = "chara_save_skill1";
        public static final String CHARA_SAVE_SKILL2   = "chara_save_skill2";
        public static final String CHARA_SAVE_SKILL3   = "chara_save_skill3";
        public static final String CHARA_SAVE_SKILL4   = "chara_save_skill4";
        public static final String CHARA_SAVE_TURN   = "chara_save_turn";
    }
}
