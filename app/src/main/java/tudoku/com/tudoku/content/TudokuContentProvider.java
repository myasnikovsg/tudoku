package tudoku.com.tudoku.content;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;

public class TudokuContentProvider extends ContentProvider {
    private static final String LOG_TAG = "CON_PRO";

    private static final String DB_NAME = "tudokuDB";
    private static final int DB_VERSION = 3;

    private static final String BOOKS_TABLE = "books";
    private static final String ATTRACTIONS_TABLE = "attractions";

    static final String BOOKS_TABLE_CREATE = "create table " + BOOKS_TABLE +
            "(" +
            Columns.BOOK_ID + " integer primary key, " +
            Columns.BOOK_ATTRACTION_ID + " integer, " +
            Columns.BOOK_ATTRACTION_NAME + " text, " +
            Columns.BOOK_ATTRACTION_IMAGE_URL + " text, " +
            Columns.BOOK_STATUS + " text, " +
            Columns.BOOK_TIME_START + " integer" +
            ");";

    static final String ATTRACTIONS_TABLE_CREATE = "create table " + ATTRACTIONS_TABLE +
            "(" +
            Columns.ATTRACTION_ID + " integer primary key, " +
            Columns.ATTRACTION_NAME_SHORT + " text, " +
            Columns.ATTRACTION_NAME_LONG + " text, " +
            Columns.ATTRACTION_DESCRIPTION_SHORT + " text, " +
            Columns.ATTRACTION_DESCRIPTION_LONG + " text, " +
            Columns.ATTRACTION_IMAGE_URL + " text, " +
            Columns.ATTRACTION_LATITUDE + " integer, " +
            Columns.ATTRACTION_LONGITUDE + " integer" +
            ");";

    public static final String AUTHORITY = "tudoku.com.tudoku.providers.TudokuContentProvider";

    public static final String BOOKS_PATH = "books";
    public static final Uri BOOKS_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + BOOKS_PATH);
    private static final String BOOKS_CONTENT_TYPE = "vnd.android.cursor.dir/vnd."
            + AUTHORITY + "." + BOOKS_PATH;
    static final String BOOKS_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd."
            + AUTHORITY + "." + BOOKS_PATH;

    public static final String ATTRACTIONS_PATH = "attractions";
    public static final Uri ATTRACTIONS_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + ATTRACTIONS_PATH);
    private static final String ATTRACTIONS_CONTENT_TYPE = "vnd.android.cursor.dir/vnd."
            + AUTHORITY + "." + ATTRACTIONS_PATH;
    static final String ATTRACTIONS_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd."
            + AUTHORITY + "." + ATTRACTIONS_PATH;

    static final int URI_BOOKS = 1;
    static final int URI_BOOK_ID = 2;
    static final int URI_ATTRACTIONS = 3;
    static final int URI_ATTRACTION_ID = 4;

    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, BOOKS_PATH, URI_BOOKS);
        uriMatcher.addURI(AUTHORITY, BOOKS_PATH + "/#", URI_BOOK_ID);
        uriMatcher.addURI(AUTHORITY, ATTRACTIONS_PATH, URI_ATTRACTIONS);
        uriMatcher.addURI(AUTHORITY, ATTRACTIONS_PATH + "/#", URI_ATTRACTION_ID);
    }

    DatabaseHelper databaseHelper;
    SQLiteDatabase database;

    @Override
    public boolean onCreate() {
        Log.d(LOG_TAG, "onCreate");
        databaseHelper = new DatabaseHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.d(LOG_TAG, "query: " + uri.toString());

        String table;

        switch (uriMatcher.match(uri)) {
            case URI_BOOKS:
                Log.d(LOG_TAG, "URI matched to books");
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = Columns.BOOK_TIME_START + " ASC";
                }
                table = BOOKS_TABLE;
                break;
            case URI_BOOK_ID:
                String id = uri.getLastPathSegment();
                Log.d(LOG_TAG, "URI matched to book with ref id " + id);
                if (TextUtils.isEmpty(selection)) {
                    selection = Columns.BOOK_ID + " = " + id;
                } else {
                    selection = selection + " AND " + Columns.BOOK_ID + " = " + id;
                }
                table = BOOKS_TABLE;
                break;
            case URI_ATTRACTIONS:
                Log.d(LOG_TAG, "URI matched to attractions");
                table = ATTRACTIONS_TABLE;
                break;
            case URI_ATTRACTION_ID:
                id = uri.getLastPathSegment();
                Log.d(LOG_TAG, "URI matched to attraction with ref id " + id);
                if (TextUtils.isEmpty(selection)) {
                    selection = Columns.ATTRACTION_ID + " = " + id;
                } else {
                    selection = selection + " AND " + Columns.ATTRACTION_ID + " = " + id;
                }
                table = ATTRACTIONS_TABLE;
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }

        database = databaseHelper.getWritableDatabase();

        Cursor cursor = database.query(table, projection, selection,
                selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        Log.d(LOG_TAG, "getType: " + uri.toString());

        switch (uriMatcher.match(uri)) {
            case URI_BOOKS:
                return BOOKS_CONTENT_TYPE;
            case URI_BOOK_ID:
                return BOOKS_CONTENT_ITEM_TYPE;
            case URI_ATTRACTIONS:
                return ATTRACTIONS_CONTENT_TYPE;
            case URI_ATTRACTION_ID:
                return ATTRACTIONS_CONTENT_ITEM_TYPE;
        }

        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.d(LOG_TAG, "insert: " + uri.toString());

        String table;

        switch (uriMatcher.match(uri)) {
            case URI_BOOKS:
                Log.d(LOG_TAG, "URI matched to books");
                table = BOOKS_TABLE;
                break;
            case URI_ATTRACTIONS:
                Log.d(LOG_TAG, "URI matched to attractions");
                table = ATTRACTIONS_TABLE;
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }

        database = databaseHelper.getWritableDatabase();
        database.insert(table, null, values);

        // TODO please please please fix this
        long itemId = values.getAsLong(Columns.BOOK_ID);

        Uri resultUri = ContentUris.withAppendedId(uri, itemId);
        getContext().getContentResolver().notifyChange(resultUri, null);

        return resultUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.d(LOG_TAG, "delete: " + uri.toString());

        String table;

        switch (uriMatcher.match(uri)) {
            case URI_BOOKS:
                Log.d(LOG_TAG, "URI matched to all books");
                table = BOOKS_TABLE;
                break;
            case URI_BOOK_ID:
                String id = uri.getLastPathSegment();
                Log.d(LOG_TAG, "URI matched to book with id " + id);

                if (TextUtils.isEmpty(selection)) {
                    selection = Columns.BOOK_ID + " = " + id;
                } else {
                    selection = selection + " AND " + Columns.BOOK_ID + " = " + id;
                }
                table = BOOKS_TABLE;
                break;
            case URI_ATTRACTIONS:
                Log.d(LOG_TAG, "URI matched to all attractions");
                table = ATTRACTIONS_TABLE;
                break;
            case URI_ATTRACTION_ID:
                id = uri.getLastPathSegment();
                Log.d(LOG_TAG, "URI matched to attraction with id " + id);

                if (TextUtils.isEmpty(selection)) {
                    selection = Columns.ATTRACTION_ID + " = " + id;
                } else {
                    selection = selection + " AND " + Columns.ATTRACTION_ID + " = " + id;
                }
                table = ATTRACTIONS_TABLE;
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }

        database = databaseHelper.getWritableDatabase();
        int deletedRowsCount = database.delete(table, selection, selectionArgs);
        if (deletedRowsCount != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return deletedRowsCount;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.d(LOG_TAG, "update: " + uri.toString());

        String table;

        switch (uriMatcher.match(uri)) {
            case URI_BOOKS:
                Log.d(LOG_TAG, "URI matched to all books");
                table = BOOKS_TABLE;
                break;
            case URI_BOOK_ID:
                String id = uri.getLastPathSegment();
                Log.d(LOG_TAG, "URI matched to book with id " + id);

                if (TextUtils.isEmpty(selection)) {
                    selection = Columns.BOOK_ID + " = " + id;
                } else {
                    selection = selection + " AND " + Columns.BOOK_ID + " = " + id;
                }
                table = BOOKS_TABLE;
                break;
            case URI_ATTRACTIONS:
                Log.d(LOG_TAG, "URI matched to all attractions");
                table = ATTRACTIONS_TABLE;
                break;
            case URI_ATTRACTION_ID:
                id = uri.getLastPathSegment();
                Log.d(LOG_TAG, "URI matched to attraction with id " + id);

                if (TextUtils.isEmpty(selection)) {
                    selection = Columns.ATTRACTION_ID + " = " + id;
                } else {
                    selection = selection + " AND " + Columns.ATTRACTION_ID + " = " + id;
                }
                table = ATTRACTIONS_TABLE;
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }

        database = databaseHelper.getWritableDatabase();

        int upgradedRowsCount = database.update(table, values, selection, selectionArgs);
        if (upgradedRowsCount != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return upgradedRowsCount;
    }

    protected static final class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(ATTRACTIONS_TABLE_CREATE);
            db.execSQL(BOOKS_TABLE_CREATE);

            File imagePath = Environment.getExternalStorageDirectory();
            File catsPath = new File(imagePath, "cats");

            ContentValues emperorCatBookValues = new ContentValues();
            emperorCatBookValues.put(Columns.BOOK_ID, 12);
            emperorCatBookValues.put(Columns.BOOK_ATTRACTION_ID, 55);
            emperorCatBookValues.put(Columns.BOOK_ATTRACTION_NAME, "Emperor of catkind");
            File emperorCatPath = new File(catsPath, "emperor_of_catkind.jpg");
            emperorCatBookValues.put(Columns.BOOK_ATTRACTION_IMAGE_URL, emperorCatPath.getAbsolutePath());
            emperorCatBookValues.put(Columns.BOOK_STATUS, "Reigning");
            emperorCatBookValues.put(Columns.BOOK_TIME_START, System.currentTimeMillis() + 1000 * 60 * 60 * 24);

            db.insert(BOOKS_TABLE, null, emperorCatBookValues);




//            ContentValues seriousCatValues = new ContentValues();
//            seriousCatValues.put(Columns.ATTRACTION_REF_ID, 10002);
//            seriousCatValues.put(Columns.ATTRACTION_NAME_SHORT, "Serious cat");
//            seriousCatValues.put(Columns.ATTRACTION_DESCRIPTION_SHORT, "Serious cat means serious business");
//            seriousCatValues.put(Columns.ATTRACTIONS_DATE, "Date: you ain't been paying attention!");
//            File seriousCatPath = new File(catsPath, "serious_cat.jpg");
//            seriousCatValues.put(Columns.ATTRACTION_IMAGE_URL, seriousCatPath.getAbsolutePath());

//            db.insert(ATTRACTIONS_TABLE, null, emperorCatValues);
//            db.insert(ATTRACTIONS_TABLE, null, happyCatValues);
////            db.insert(ATTRACTIONS_TABLE, null, seriousCatValues);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + ATTRACTIONS_TABLE);

            onCreate(db);
        }
    }
}