package com.example.dragonquest;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.dragonquest.DBTables.CharacterTable;

public class DatabaseHelper extends SQLiteOpenHelper {

    // データベースのバージョン
    // テーブルの内容などを変更したら、この数字を変更する
    static final private int VERSION = 12;

    // データベース名
    static final private String DBNAME = "sample.db";

    // コンストラクタは必ず必要
    public DatabaseHelper(Context context) {
        super(context, DBNAME, null, VERSION);
    }

    // データベース作成時にテーブルを作成
    @Override
    public void onCreate(SQLiteDatabase db) {

//        db.execSQL(
//                "CREATE TABLE "+ SkillTable.TABLE_NAME + " (" +
//                        SkillTable.COLUMN_NAME_SKILL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
//                        SkillTable.COLUMN_NAME_SKILL_NAME + " TEXT NOT NULL," +
//                        SkillTable.COLUMN_NAME_SKILL_EFFECT +
//                        " TEXT NOT NULL) "
//        );

        // テーブルを作成
        db.execSQL(
                "CREATE TABLE " + CharacterTable.TABLE_NAME + "(" +
                        CharacterTable.CHARA_SAVE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        CharacterTable.CHARA_SAVE_NAME + " TEXT ," +
                        CharacterTable.CHARA_SAVE_HP + " INTEGER ," +
                        CharacterTable.CHARA_SAVE_ATK + " INTEGER ," +
                        CharacterTable.CHARA_SAVE_DEF + " INTEGER ," +
                        CharacterTable.CHARA_SAVE_DEX + " INTEGER ," +
                        CharacterTable.CHARA_SAVE_SKILL1 + " INTEGER ," +
                        CharacterTable.CHARA_SAVE_SKILL2 + " INTEGER ," +
                        CharacterTable.CHARA_SAVE_SKILL3 + " INTEGER ," +
                        CharacterTable.CHARA_SAVE_SKILL4 + " INTEGER ," +
                        CharacterTable.CHARA_SAVE_TURN + " INTEGER" +
                        " )"
        );

        //追加するカラムに内容をセット
        ContentValues cv = new ContentValues();
        cv.put(CharacterTable.CHARA_SAVE_ID, 1);
        cv.put(CharacterTable.CHARA_SAVE_NAME, "名前");
        cv.put(CharacterTable.CHARA_SAVE_HP, 0);
        cv.put(CharacterTable.CHARA_SAVE_ATK, 0);
        cv.put(CharacterTable.CHARA_SAVE_DEX, 0);
        cv.put(CharacterTable.CHARA_SAVE_SKILL1, "スキル");
        cv.put(CharacterTable.CHARA_SAVE_SKILL2, "");
        cv.put(CharacterTable.CHARA_SAVE_SKILL3, "");
        cv.put(CharacterTable.CHARA_SAVE_SKILL4, "");
        cv.put(CharacterTable.CHARA_SAVE_TURN, 0);

        ContentValues cv2 = new ContentValues();
        cv2.put(CharacterTable.CHARA_SAVE_ID, 2);
        cv2.put(CharacterTable.CHARA_SAVE_NAME, "名前");
        cv2.put(CharacterTable.CHARA_SAVE_HP, 0);
        cv2.put(CharacterTable.CHARA_SAVE_ATK, 0);
        cv2.put(CharacterTable.CHARA_SAVE_DEX, 0);
        cv2.put(CharacterTable.CHARA_SAVE_SKILL1, "スキル");
        cv2.put(CharacterTable.CHARA_SAVE_SKILL2, "");
        cv2.put(CharacterTable.CHARA_SAVE_SKILL3, "");
        cv2.put(CharacterTable.CHARA_SAVE_SKILL4, "");
        cv2.put(CharacterTable.CHARA_SAVE_TURN, 0);

        //インサート
        db.insert(CharacterTable.TABLE_NAME, null, cv);
        db.insert(CharacterTable.TABLE_NAME, null, cv2);
    }

    //バージョン変更時に削除
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + CharacterTable.TABLE_NAME);
//        db.execSQL("DROP TABLE IF EXISTS " + "chara_table");
        onCreate(db);
    }
}
