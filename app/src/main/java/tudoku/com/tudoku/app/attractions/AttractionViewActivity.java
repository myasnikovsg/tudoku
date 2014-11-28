package tudoku.com.tudoku.app.attractions;

import android.content.ContentUris;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import tudoku.com.tudoku.R;
import tudoku.com.tudoku.content.Columns;
import tudoku.com.tudoku.content.TudokuContentProvider;
import tudoku.com.tudoku.util.Extra;

public class AttractionViewActivity extends FragmentActivity {

    private ContentObserver mContentObserver;
    private ViewPager mViewPager;
    private AttractionPagerAdapter mPagerAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // get attraction id to pass in further
        int attractionId = getIntent().getIntExtra(Extra.ATTRACTION_ID, -1);
        // the only thing we need from args is to be final and to contain ATTRACTION_ID and TAB
        final Bundle args = new Bundle();
        args.putInt(Extra.ATTRACTION_ID, attractionId);
        // query table for this attraction
        Uri queryUri = ContentUris.withAppendedId(TudokuContentProvider.ATTRACTIONS_CONTENT_URI,
                attractionId);
        final Cursor cursor = getContentResolver().query(queryUri, new String[] {Columns.ATTRACTION_NAME_SHORT,
                Columns.ATTRACTION_IMAGE_URL}, null, null, null);
        if (cursor.getCount() == 0) {
            // why the hell should we need an empty cursor
            cursor.close();
            // nothing found, good lord. Gonna make user wait
            setContentView(R.layout.waiting_for_data_screen);
            // this observer's onChange will trigger normal creation of activity
            mContentObserver = new ContentObserver(new Handler()) {
                @Override
                public void onChange(boolean selfChange) {
                    // firstly, we don't need observer anymore
                    getContentResolver().unregisterContentObserver(mContentObserver);
                    // the only change we can get here is insert, so we can proceed
                    onDataArrived(args);
                }

                @Override
                public boolean deliverSelfNotifications() {
                    return true;
                }
            };

            getContentResolver().registerContentObserver(queryUri, false, mContentObserver);
        } else {
            // where is something in the table for us - we can proceed.
            onDataArrived(args);
        }

    }

    protected void onDataArrived(Bundle args) {
        // get attraction id to pass in further
        int attractionId = getIntent().getIntExtra(Extra.ATTRACTION_ID, -1);
        // query table for this attraction
        Uri queryUri = ContentUris.withAppendedId(TudokuContentProvider.ATTRACTIONS_CONTENT_URI,
                attractionId);
        Cursor cursor = getContentResolver().query(queryUri, new String[] {Columns.ATTRACTION_NAME_SHORT,
                Columns.ATTRACTION_IMAGE_URL}, null, null, null);
        cursor.moveToFirst();

        setContentView(R.layout.attraction_screen);
        TextView activityShortNameView = (TextView) findViewById(R.id.attraction_short_name);
        ImageView activityImageView = (ImageView) findViewById(R.id.attraction_image);

        activityShortNameView.setText(cursor.getString(cursor.getColumnIndex(Columns.ATTRACTION_NAME_SHORT)));

        mViewPager = (ViewPager) findViewById(R.id.attraction_pager);
        mPagerAdapter = new AttractionPagerAdapter(getSupportFragmentManager(), this, args);

        mViewPager.setAdapter(mPagerAdapter);

        // TODO register content observer for resolver
    }
}
