package com.subhrajyoti.wallify.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "images.db";
    private static final String COMMA = ",";
    private static final String SPACE = " ";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String CREATE_TABLE_NOTE = "CREATE TABLE " + ImageContract.ImageEntry.TABLE_NAME + "("
                + ImageContract.ImageEntry.IMAGE_ID + SPACE + "INTEGER PRIMARY KEY" + COMMA
                + ImageContract.ImageEntry.IMAGE_BLOG + SPACE + "BLOB NOT NULL"
                + ")";

        sqLiteDatabase.execSQL(CREATE_TABLE_NOTE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ImageContract.ImageEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);

    }
}
