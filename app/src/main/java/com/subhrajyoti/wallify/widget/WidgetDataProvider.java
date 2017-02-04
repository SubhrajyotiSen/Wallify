package com.subhrajyoti.wallify.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.subhrajyoti.wallify.R;
import com.subhrajyoti.wallify.db.ImageContract;
import com.subhrajyoti.wallify.model.Image;

import java.io.File;
import java.util.ArrayList;

@SuppressLint("NewApi")
public class WidgetDataProvider implements RemoteViewsService.RemoteViewsFactory {

    private ArrayList<Image> mCollections;
    private Cursor cursor;

    Context mContext = null;

    public WidgetDataProvider(Context context, Intent intent) {
        mContext = context;
        mCollections = new ArrayList();
        Log.d("widget prov", "widget prov");
    }

    @Override
    public int getCount() {
        return mCollections.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews mView = new RemoteViews(mContext.getPackageName(),
                R.layout.widget_item);
        mView.setImageViewBitmap(R.id.widgetImage, BitmapFactory.decodeFile(mCollections.get(position).getPath()));
        Log.d("URI",(Uri.fromFile(new File(mCollections.get(position).getPath()))).toString() );

        return mView;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onCreate() {
        initData();
    }

    @Override
    public void onDataSetChanged() {
        initData();
    }

    private void initData() {
        mCollections.clear();
        cursor = mContext.getContentResolver().query(ImageContract.ImageEntry.CONTENT_URI,
                new String[]{ImageContract.ImageEntry.IMAGE_ID, ImageContract.ImageEntry.IMAGE_PATH},
                null,
                null,
                null);
        Log.d("Cursor", cursor.getCount()+"");
        assert cursor != null;
        while (cursor.moveToNext()){
            mCollections.add(new Image(cursor.getInt(cursor.getColumnIndex(ImageContract.ImageEntry.IMAGE_ID)),cursor.getString(cursor.getColumnIndex(ImageContract.ImageEntry.IMAGE_PATH))));
            Log.d("hello",cursor.getString(cursor.getColumnIndex(ImageContract.ImageEntry.IMAGE_PATH)));
        }
    }

    @Override
    public void onDestroy() {

    }

}