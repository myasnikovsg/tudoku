package tudoku.com.tudoku.app.attractions;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import tudoku.com.tudoku.R;
import tudoku.com.tudoku.app.AttractionViewActivity;
import tudoku.com.tudoku.app.AuthAwareActivity;
import tudoku.com.tudoku.app.books.BookViewHolder;
import tudoku.com.tudoku.content.Columns;
import tudoku.com.tudoku.content.TudokuContentProvider;
import tudoku.com.tudoku.service.TudokuService;
import tudoku.com.tudoku.util.Action;
import tudoku.com.tudoku.util.Extra;


public class AttractionsListActivity extends AuthAwareActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String[] PROJECTION = {Columns.BOOK_ID,
            Columns.BOOK_ATTRACTION_ID,
            Columns.BOOK_ATTRACTION_NAME,
            Columns.BOOK_ATTRACTION_IMAGE_URL,
            Columns.BOOK_STATUS,
            Columns.BOOK_TIME_START};
    private static final String SELECTION = null;

    AttractionsListAdapter mListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.books_list);

        Intent serviceIntent = new Intent();
        serviceIntent.setAction(Action.UPDATE_BOOKS);
        serviceIntent.setClass(AttractionsListActivity.this, TudokuService.class);
        sendServiceRequest(serviceIntent);

        ListView listView = (ListView) findViewById(R.id.books_list_view);
        mListAdapter = new AttractionsListAdapter(this,
                R.layout.books_list_item, null, PROJECTION, null, 0);
        getContentResolver().delete(TudokuContentProvider.ATTRACTIONS_CONTENT_URI, null, null);
        listView.setAdapter(mListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int attractionId = ((BookViewHolder) view.getTag()).getAttractionId();

                Intent serviceIntent = new Intent();
                serviceIntent.setAction(Action.UPDATE_ATTRACTION);
                serviceIntent.setClass(AttractionsListActivity.this, TudokuService.class);
                serviceIntent.putExtra(Extra.ATTRACTION_ID, attractionId);
                sendServiceRequest(serviceIntent);

                Intent activityIntent = new Intent(AttractionsListActivity.this, AttractionViewActivity.class);
                activityIntent.putExtra(Extra.ATTRACTION_ID, attractionId);
                startActivity(activityIntent);
            }
        });

        getLoaderManager().initLoader(0, null, this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, TudokuContentProvider.BOOKS_CONTENT_URI,
                PROJECTION, SELECTION, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mListAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mListAdapter.swapCursor(null);
    }
}
