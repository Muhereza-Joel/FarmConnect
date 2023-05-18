package com.moels.farmconnect.utility_classes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "login_db";

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, 1);

    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE user(" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT, " +
                "password TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS user");
    }

    public Boolean insert(String username, String password){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username);
        contentValues.put("password", password);
        long result = sqLiteDatabase.insert("user", null, contentValues);

        if (result == -1) return false;
        else return true;
    }

    public Boolean checkUsername(String username){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String sql = "SELECT * FROM user WHERE username=?";
        Cursor cursor = sqLiteDatabase.rawQuery(sql, new String[]{username});

        if (cursor.getCount() > 0) return false;
        else return true;

    }

    public Boolean checkLogin(String username, String password){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String sql = "SELECT * FROM user WHERE username = ? AND password = ?";
        Cursor cursor = sqLiteDatabase.rawQuery(sql, new String[]{username, password});

        if (cursor.getCount() > 0) return true;
        else return false;
    }

}
