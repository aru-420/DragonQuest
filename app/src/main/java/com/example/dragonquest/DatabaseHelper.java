package com.example.dragonquest;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.dragonquest.DBTables.CharacterTable;
import com.example.dragonquest.DBTables.SkillTable;

public class DatabaseHelper extends SQLiteOpenHelper {

    // データベースのバージョン
    // テーブルの内容などを変更したら、この数字を変更する
    static final private int VERSION = 6;

    // コンストラクタは必ず必要
    public DatabaseHelper(Context context) {
        super(context, DBNAME, null, VERSION);
    }

    // データベース名
    static final private String DBNAME = "sample.db";

    // データベース作成時にテーブルを作成
    @Override
    public void onCreate(SQLiteDatabase db) {
        // テーブルを作成
//        db.execSQL(
//                "CREATE TABLE "+ SkillTable.TABLE_NAME + " (" +
//                        SkillTable.COLUMN_NAME_SKILL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
//                        SkillTable.COLUMN_NAME_SKILL_NAME + " TEXT NOT NULL," +
//                        SkillTable.COLUMN_NAME_SKILL_EFFECT +
//                        " TEXT NOT NULL) "
//        );

        db.execSQL(
                "CREATE TABLE " + CharacterTable.TABLE_NAME + "(" +
                        CharacterTable.CHARA_SAVE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        CharacterTable.CHARA_SAVE_NAME + " TEXT NOT NULL," +
                        CharacterTable.CHARA_SAVE_HP + " INTEGER NOT NULL," +
                        CharacterTable.CHARA_SAVE_ATK + " INTEGER NOT NULL," +
                        CharacterTable.CHARA_SAVE_DEF + " INTEGER NOT NULL," +
                        CharacterTable.CHARA_SAVE_DEX + " INTEGER NOT NULL," +
                        CharacterTable.CHARA_SAVE_SKILL1 + " INTEGER NOT NULL," +
                        CharacterTable.CHARA_SAVE_SKILL2 + " INTEGER ," +
                        CharacterTable.CHARA_SAVE_SKILL3 + " INTEGER ," +
                        CharacterTable.CHARA_SAVE_SKILL4 + " INTEGER ," +
                        CharacterTable.CHARA_SAVE_TURN + " INTEGER" +
                        " )"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + SkillTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + "chara_table");
        onCreate(db);
    }
}
