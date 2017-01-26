package com.subhrajyoti.wallify.db;


import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class ImageContract {

    public static final String CONTENT_AUTHORITY = "com.subhrajyoti.wallify.app";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_NOTE = "image";

    public static final class ImageEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_NOTE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_NOTE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_NOTE;

        public static final String TABLE_NAME = "images";

        public static final String IMAGE_ID = "image_id";

        public static final String IMAGE_BLOB = "blob";

        public static Uri buildUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }


    }


}
