package com.subhrajyoti.wallify.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

@SuppressWarnings("ConstantConditions")
public class ImageProvider extends ContentProvider {

    private static final int NOTE_TABLE_ID = 100;
    private static final int NOTE_ROW_ID = 101;
    private static final UriMatcher uriMatcher = buildUriMatcher();
    private DbHelper dbHelper;

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ImageContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, ImageContract.PATH_NOTE, NOTE_TABLE_ID);
        matcher.addURI(authority, ImageContract.PATH_NOTE + "/#", NOTE_ROW_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] strings, String s, String[] strings1, String s1) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor;

        switch (uriMatcher.match(uri)) {

            case NOTE_TABLE_ID:
                cursor = db.query(ImageContract.ImageEntry.TABLE_NAME, strings, s, strings1, null, null, s1);
                break;

            case NOTE_ROW_ID:
                long _id = ContentUris.parseId(uri);
                cursor = db.query(ImageContract.ImageEntry.TABLE_NAME, strings, ImageContract.ImageEntry.IMAGE_ID + " = ?", new String[]{String.valueOf(_id)}, null, null,
                        s1);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        switch (uriMatcher.match(uri)) {

            case NOTE_ROW_ID:
                return ImageContract.ImageEntry.CONTENT_ITEM_TYPE;

            case NOTE_TABLE_ID:
                return ImageContract.ImageEntry.CONTENT_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {

        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        long _id;
        Uri returnUri;

        if (uriMatcher.match(uri) == NOTE_TABLE_ID) {
            _id = db.insert(ImageContract.ImageEntry.TABLE_NAME, null, contentValues);
            if (_id > 0) {
                returnUri = ImageContract.ImageEntry.buildUri(_id);
            } else {
                throw new UnsupportedOperationException("Unable to insert rows into: " + uri);
            }
        } else {
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String s, String[] strings) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows;

        if (uriMatcher.match(uri) == NOTE_TABLE_ID) {
            rows = db.delete(ImageContract.ImageEntry.TABLE_NAME, s, strings);
        } else {
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Because null could delete all rows:
        if (s == null || rows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rows;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String s, String[] strings) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows;
        if (uriMatcher.match(uri) == NOTE_TABLE_ID) {
            rows = db.update(ImageContract.ImageEntry.TABLE_NAME, contentValues, s, strings);
        } else {
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rows;
    }
}
