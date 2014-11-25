package tudoku.com.tudoku.app.attractions;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.io.File;
import java.util.Date;

import tudoku.com.tudoku.R;
import tudoku.com.tudoku.app.books.BookViewHolder;
import tudoku.com.tudoku.content.Columns;


public class AttractionsListAdapter extends SimpleCursorAdapter {
    private static final String LOG_TAG = "ATTR_LIST";

    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private int mLayout;

    private static final int CACHE_SIZE = 32;

    private LruCache<String, Bitmap> mCache;

    public AttractionsListAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        mContext = context;
        mLayout = layout;
        mLayoutInflater = LayoutInflater.from(context);
        mCache = new LruCache<String, Bitmap>(CACHE_SIZE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = mLayoutInflater.inflate(mLayout, parent, false);
        view.setTag(new BookViewHolder(view));

        return view;
    }



    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String attractionImageUrl = cursor.getString(cursor.getColumnIndex(Columns.BOOK_ATTRACTION_IMAGE_URL));
        String attractionName = cursor.getString(cursor.getColumnIndex(Columns.BOOK_ATTRACTION_NAME));
        String status = cursor.getString(cursor.getColumnIndex(Columns.BOOK_STATUS));
        long timeStart = cursor.getLong(cursor.getColumnIndex(Columns.BOOK_TIME_START));
        int attractionId = cursor.getInt(cursor.getColumnIndex(Columns.BOOK_ATTRACTION_ID));

        BookViewHolder viewHolder = (BookViewHolder) view.getTag();

        viewHolder.getAttractionImageView().setImageBitmap(fetchBitmap(attractionImageUrl));
        viewHolder.getAttractionNameView().setText(attractionName);
        viewHolder.getStatusView().setText(status);
        Date date = new Date(timeStart);
        viewHolder.getTimeStartView().setText(date.toString());
        viewHolder.setAttractionId(attractionId);
    }

    private Bitmap fetchBitmap(String path) {
        Bitmap thumbnail = getThumbnail(path);
        if (thumbnail != null) {
            return thumbnail;
        }

        File imageFile = new File(path);
        Bitmap bitmap;

        if( imageFile.exists() ) {
            bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
        } else {
            Log.d(LOG_TAG, "No image file found for path " + path);
            throw new IllegalArgumentException("No image file found for path " + path);
        }

        setThumbnail(path, bitmap);
        return bitmap;
    }

    private void setThumbnail(String path, Bitmap bitmap) {
        mCache.put(path, bitmap);
    }

    private Bitmap getThumbnail(String path) {
        return mCache.get(path);
    }


}
