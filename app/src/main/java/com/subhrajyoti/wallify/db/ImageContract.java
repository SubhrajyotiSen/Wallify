package com.subhrajyoti.wallify.db;


import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class ImageContract {

    static final String CONTENT_AUTHORITY = "com.subhrajyoti.wallify.app";
    static final String PATH_NOTE = "image";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final class ImageEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_NOTE).build();
        public static final String IMAGE_ID = "_id";
        public static final String IMAGE_PATH = "path";
        static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_NOTE;
        static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_NOTE;
        static final String TABLE_NAME = "images";

        static Uri buildUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }


    }


}
