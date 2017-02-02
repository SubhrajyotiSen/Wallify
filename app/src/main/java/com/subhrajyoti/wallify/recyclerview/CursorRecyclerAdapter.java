package com.subhrajyoti.wallify.recyclerview;

import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.support.v7.widget.RecyclerView;

import com.subhrajyoti.wallify.db.ImageContract;

public abstract class CursorRecyclerAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    private static final String LOG_TAG = CursorRecyclerAdapter.class.getSimpleName();
    private Cursor mCursor;
    private boolean dataIsValid;
    private int rowIdColumn;
    private DataSetObserver mDataSetObserver;

    public CursorRecyclerAdapter(Context context, Cursor cursor) {
        mCursor = cursor;
        dataIsValid = cursor != null;
        rowIdColumn = dataIsValid ? mCursor.getColumnIndex(ImageContract.ImageEntry.IMAGE_ID) : -1;
        mDataSetObserver = new NotifyingDataSetObserver();
        if (dataIsValid) {
            mCursor.registerDataSetObserver(mDataSetObserver);
        }
    }

    public Cursor getCursor() {
        return mCursor;
    }

    @Override
    public int getItemCount() {
        if (dataIsValid && mCursor != null) {
            return mCursor.getCount();
        }
        return 0;
    }

    @Override
    public long getItemId(int position) {
        if (dataIsValid && mCursor != null && mCursor.moveToPosition(position)) {
            return mCursor.getLong(rowIdColumn);
        }
        return 0;
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(true);
    }

    public abstract void onBindViewHolder(VH viewHolder, Cursor cursor, int position);

    @Override
    public void onBindViewHolder(VH viewHolder, int position) {
        if (!dataIsValid) {
            throw new IllegalStateException("This should only be called when Cursor is valid");
        }
        try {
            if (!mCursor.moveToPosition(position)) {
                throw new IllegalStateException("Could not move Cursor to position: " + position);
            }
        }
        catch (Exception e){
            e.getLocalizedMessage();
        }

        onBindViewHolder(viewHolder, mCursor, position);
    }

    public Cursor swapCursor(Cursor newCursor) {
        if (newCursor == mCursor) {
            return null;
        }
        final Cursor oldCursor = mCursor;
        if (oldCursor != null && mDataSetObserver != null) {
            oldCursor.unregisterDataSetObserver(mDataSetObserver);
        }
        mCursor = newCursor;
        if (mCursor != null) {
            if (mDataSetObserver != null) {
                mCursor.registerDataSetObserver(mDataSetObserver);
            }
            rowIdColumn = newCursor.getColumnIndexOrThrow(ImageContract.ImageEntry.IMAGE_ID);
            dataIsValid = true;
            notifyDataSetChanged();
        } else {
            rowIdColumn = -1;
            dataIsValid = false;
            notifyDataSetChanged();
        }
        return oldCursor;
    }

    private class NotifyingDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            super.onChanged();
            dataIsValid = true;
            notifyDataSetChanged();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
            dataIsValid = false;
            notifyDataSetChanged();
        }
    }
}