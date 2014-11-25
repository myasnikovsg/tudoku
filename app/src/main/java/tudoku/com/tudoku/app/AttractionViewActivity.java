package tudoku.com.tudoku.app;

import android.app.TabActivity;
import android.content.ContentUris;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import java.util.HashMap;

import tudoku.com.tudoku.R;
import tudoku.com.tudoku.app.attractions.AttractionDescriptionFragment;
import tudoku.com.tudoku.app.attractions.AttractionMapFragment;
import tudoku.com.tudoku.content.Columns;
import tudoku.com.tudoku.content.TudokuContentProvider;
import tudoku.com.tudoku.util.Extra;

public class AttractionViewActivity extends FragmentActivity implements TabHost.OnTabChangeListener {

    private TabHost mTabHost;
    private HashMap mapTabInfo = new HashMap();
    private TabInfo mLastTab = null;
    private ContentObserver mContentObserver;

    private class TabInfo {
        private String tag;
        private Class clss;
        private Bundle args;
        private Fragment fragment;
        TabInfo(String tag, Class clazz, Bundle args) {
            this.tag = tag;
            this.clss = clazz;
            this.args = args;
        }

    }

    class TabFactory implements TabHost.TabContentFactory {

        private final Context mContext;

        /**
         * @param context
         */
        public TabFactory(Context context) {
            mContext = context;
        }

        /** (non-Javadoc)
         * @see android.widget.TabHost.TabContentFactory#createTabContent(java.lang.String)
         */
        public View createTabContent(String tag) {
            View v = new View(mContext);
            v.setMinimumWidth(0);
            v.setMinimumHeight(0);
            return v;
        }

    }
    /** (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // get attraction id to pass in further
        int attractionId = getIntent().getIntExtra(Extra.ATTRACTION_ID, -1);
        // the only thing we need from args is to be final and to contain ATTRACTION_ID and TAB
        final Bundle args = new Bundle();
        if (savedInstanceState != null && savedInstanceState.containsKey("tab")) {
            args.putString("tab", savedInstanceState.getString("tab"));
        }
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
        TextView activityShortNameView = (TextView) findViewById(R.id.activity_short_name);
        ImageView activityImageView = (ImageView) findViewById(R.id.activity_image);

        activityShortNameView.setText(cursor.getString(cursor.getColumnIndex(Columns.ATTRACTION_NAME_SHORT)));

        initialiseTabHost(args);
        if (args != null) {
            mTabHost.setCurrentTabByTag(args.getString("tab"));
        }
        // TODO register content observer for resolver
    }

    /** (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onSaveInstanceState(android.os.Bundle)
     */
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("tab", mTabHost.getCurrentTabTag()); //save the tab selected
        super.onSaveInstanceState(outState);
    }

    /**
     * Step 2: Setup TabHost
     */
    private void initialiseTabHost(Bundle args) {
        mTabHost = (TabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup();
        TabInfo tabInfo = null;
        AttractionViewActivity.addTab(this, this.mTabHost, this.mTabHost.newTabSpec("Tab1").setIndicator("Tab 1"), ( tabInfo = new TabInfo("Tab1", AttractionDescriptionFragment.class, args)));
        this.mapTabInfo.put(tabInfo.tag, tabInfo);
        AttractionViewActivity.addTab(this, this.mTabHost, this.mTabHost.newTabSpec("Tab2").setIndicator("Tab 2"), ( tabInfo = new TabInfo("Tab2", AttractionMapFragment.class, args)));
        this.mapTabInfo.put(tabInfo.tag, tabInfo);
        // Default to first tab
        this.onTabChanged("Tab1");
        //
        mTabHost.setOnTabChangedListener(this);
    }

    private static void addTab(AttractionViewActivity activity, TabHost tabHost, TabHost.TabSpec tabSpec, TabInfo tabInfo) {
        // Attach a Tab view factory to the spec
        tabSpec.setContent(activity.new TabFactory(activity));
        String tag = tabSpec.getTag();

        // Check to see if we already have a fragment for this tab, probably
        // from a previously saved state.  If so, deactivate it, because our
        // initial state is that a tab isn't shown.
        tabInfo.fragment = activity.getSupportFragmentManager().findFragmentByTag(tag);
        if (tabInfo.fragment != null && !tabInfo.fragment.isDetached()) {
            FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
            ft.detach(tabInfo.fragment);
            ft.commit();
            activity.getSupportFragmentManager().executePendingTransactions();
        }

        tabHost.addTab(tabSpec);
    }

    /** (non-Javadoc)
     * @see android.widget.TabHost.OnTabChangeListener#onTabChanged(java.lang.String)
     */
    public void onTabChanged(String tag) {
        TabInfo newTab = (TabInfo) this.mapTabInfo.get(tag);
        if (mLastTab != newTab) {
            FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
            if (mLastTab != null) {
                if (mLastTab.fragment != null) {
                    ft.detach(mLastTab.fragment);
                }
            }
            if (newTab != null) {
                if (newTab.fragment == null) {
                    newTab.fragment = Fragment.instantiate(this,
                            newTab.clss.getName(), newTab.args);
                    ft.add(R.id.realtabcontent, newTab.fragment, newTab.tag);
                } else {
                    ft.attach(newTab.fragment);
                }
            }

            mLastTab = newTab;
            ft.commit();
            this.getSupportFragmentManager().executePendingTransactions();
        }
    }

}
