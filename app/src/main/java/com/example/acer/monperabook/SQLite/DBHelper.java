package com.example.acer.monperabook.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Map;
import java.util.Set;

/**
 * Created by Azhary Arliansyah on 12/11/2017.
 */

public class DBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "monpera_book";
    public static final int DB_VERSION = 8;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE artifact_favorites(");
        sql.append("kode_artifak VARCHAR PRIMARY KEY, ");
        sql.append("nama VARCHAR, ");
        sql.append("deskripsi TEXT, ");
        sql.append("foto TEXT);");
        db.execSQL(sql.toString());

        sql = new StringBuilder();
        sql.append("CREATE TABLE artifact(");
        sql.append("kode_artifak VARCHAR PRIMARY KEY, ");
        sql.append("nama VARCHAR, ");
        sql.append("deskripsi TEXT, ");
        sql.append("foto TEXT, ");
        sql.append("rate FLOAT);");
        db.execSQL(sql.toString());

        sql = new StringBuilder();
        sql.append("CREATE TABLE question(");
        sql.append("id_pertanyaan INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, ");
        sql.append("kode_artifak VARCHAR, ");
        sql.append("pertanyaan TEXT);");
        db.execSQL(sql.toString());

        sql = new StringBuilder();
        sql.append("CREATE TABLE note(");
        sql.append("id_note INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, ");
        sql.append("kode_artifak VARCHAR, ");
        sql.append("note TEXT);");
        db.execSQL(sql.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS artifact_favorites");
        db.execSQL("DROP TABLE IF EXISTS artifact");
        db.execSQL("DROP TABLE IF EXISTS question");
        db.execSQL("DROP TABLE IF EXISTS note");
        onCreate(db);
    }

    public boolean insert(String table, Map<String, String> keyValuePair) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        Set<String> keys = keyValuePair.keySet();
        for (String key : keys) {
            contentValues.put(key, keyValuePair.get(key));
        }
        db.insert(table, null, contentValues);
        db.close();
        return true;
    }

    public boolean update(String table, Map<String, String> keyValuePair, String conditions) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        Set<String> keys = keyValuePair.keySet();
        for (String key : keys) {
            contentValues.put(key, keyValuePair.get(key));
        }
        db.update(table, contentValues, conditions, null);
        db.close();
        return true;
    }

    /** start select method overloading */
    public Cursor select(String table, String conditions) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + table + " WHERE " + conditions;
        Cursor c = db.rawQuery(sql, null);
        return c;
    }

    public Cursor select(String table) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + table;
        Cursor c = db.rawQuery(sql, null);
        return c;
    }
    /** end select method overloading */

    public boolean delete(String table, String conditions) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(table, conditions, null) > 0;
    }
}
