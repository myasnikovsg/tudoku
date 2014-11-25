package tudoku.com.tudoku.service;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import tudoku.com.tudoku.app.AuthAwareActivity;
import tudoku.com.tudoku.app.AuthenticatorActivity;
import tudoku.com.tudoku.content.AttractionDataType;
import tudoku.com.tudoku.content.BookDataType;
import tudoku.com.tudoku.content.Columns;
import tudoku.com.tudoku.content.TudokuContentProvider;
import tudoku.com.tudoku.rest.server.TudokuServerConnector;
import tudoku.com.tudoku.util.Action;
import tudoku.com.tudoku.util.Extra;

public class TudokuService extends Service {

    private static final String LOG_TAG = "SERVICE";

    private static final String SERVICE_NAME = "com.tudoku.Service";

    private AccountManager mAccountManager;
    private Account mAccount;

    public TudokuService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mAccountManager = AccountManager.get(this);
        SharedPreferences preferences = getSharedPreferences(AuthAwareActivity.AUTH_PREFS, MODE_PRIVATE);
        String accountName = preferences.getString(AccountManager.KEY_ACCOUNT_NAME, null);
        String accountType = preferences.getString(AccountManager.KEY_ACCOUNT_TYPE, null);
        if (accountName != null && accountType != null) {
            Log.d(LOG_TAG, "loaded account with name " + accountName + " and type " + accountType);
            mAccount = new Account(accountName, accountType);
        }
//        Log.d(LOG_TAG, "registering ")
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if (intent == null) {
            return Service.START_STICKY;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (intent.getAction().equals(Action.UPDATE_BOOKS)) {
                    onUpdateBooks();
                } else if (intent.getAction().equals(Action.SET_ACCOUNT)) {
                    onSetAccount(intent);
                } else if (intent.getAction().equals(Action.UPDATE_ATTRACTION)) {
                    onUpdateAttractions(intent);
                }

            }
        }).start();

        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void onSetAccount(final Intent intent) {
        String accountType = intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE);
        String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        if (accountType == null || accountName == null) {
            Log.d(LOG_TAG, "bad intent for setAccount");
            return;
        }

        // saving to preferences
        SharedPreferences preferences = getSharedPreferences(AuthAwareActivity.AUTH_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(AccountManager.KEY_ACCOUNT_NAME, accountName);
        editor.putString(AccountManager.KEY_ACCOUNT_TYPE, accountType);
        editor.apply();

        Log.d(LOG_TAG, "Setting account with type " + accountType + " and name " + accountName);
        mAccount = new Account(accountName, accountType);
    }

    private String getToken() {
        try {
            String authToken = null;
            if (mAccount != null) {
                authToken = mAccountManager.blockingGetAuthToken(mAccount, AuthenticatorActivity.AUTH_TOKEN_TYPE, false);
            } else {
                Log.d(LOG_TAG, "account is null");
            }

            if (authToken != null) {
                return authToken;
            } else {
                Intent broadcast = new Intent(Action.RE_AUTHENTICATE);
                if (mAccount != null) {
                    broadcast.putExtra(AccountManager.KEY_ACCOUNT_NAME, mAccount.name);
                    broadcast.putExtra(AccountManager.KEY_ACCOUNT_TYPE, mAccount.type);
                }
                sendBroadcast(broadcast);
            }
        } catch (OperationCanceledException | IOException | AuthenticatorException e) {
            Log.d(LOG_TAG, "Error condition while getting token", e);
        }
        return null;
    }

    private void onUpdateAttractions(Intent intent) {
        String token = getToken();
        if (token == null) {
            Log.d(LOG_TAG, "updateAttractions failed: null token");
            return;
        }

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
        }

        TudokuServerConnector serverConnector = new TudokuServerConnector(token);
        AttractionDataType attraction = serverConnector.getAttraction(
                intent.getIntExtra(Extra.ATTRACTION_ID, -1));

        Log.d(LOG_TAG, "trying to update attraction with id " + attraction.getId());
        Uri updateUri = ContentUris.withAppendedId(TudokuContentProvider.ATTRACTIONS_CONTENT_URI,
                attraction.getId());
        int rowsUpdated = getContentResolver().update(updateUri, attraction.asContentValues(), null, null);
        if (rowsUpdated == 0) {
            Log.d(LOG_TAG, "update failed, inserting attraction with id " + attraction.getId());
            getContentResolver().insert(TudokuContentProvider.ATTRACTIONS_CONTENT_URI, attraction.asContentValues());
        }
    }

    private void onUpdateBooks() {
        String token = getToken();
        if (token == null) {
            Log.d(LOG_TAG, "updateBooks failed: null token");
            return;
        }

        TudokuServerConnector serverConnector = new TudokuServerConnector(token);
        List<BookDataType> books = serverConnector.getBooks();
        for (BookDataType book : books) {
            Log.d(LOG_TAG, "trying to update book with id " + book.getId());
            Uri updateUri = ContentUris.withAppendedId(TudokuContentProvider.BOOKS_CONTENT_URI, book.getId());
            int rowsUpdated = getContentResolver().update(updateUri, book.asContentValues(), null, null);
            if (rowsUpdated == 0) {
                Log.d(LOG_TAG, "update failed, inserting book with id " + book.getId());
                getContentResolver().insert(TudokuContentProvider.BOOKS_CONTENT_URI, book.asContentValues());
            }
        }

//        String selection = Columns.ATTRACTIONS_DATE + " > " + System.currentTimeMillis();
//
//        Cursor cursor = getContentResolver().query(TudokuContentProvider.ATTRACTIONS_CONTENT_URI, new String[]{Columns.ATTRACTION_REF_ID}, selection, null, Columns.ATTRACTION_REF_ID + " ASC");
//        int columnIndex = cursor.getColumnIndex(Columns.ATTRACTION_REF_ID);
//
//        while (cursor.moveToNext()) {
//            long referenceId = cursor.getLong(columnIndex);
//            serverAttractionIds.remove(referenceId);
//        }
//
//        cursor.close();
//
//        List<AttractionDataType> attractionsFromServer = serverConnector.getAttractions(serverAttractionIds);
//        for (AttractionDataType attraction : attractionsFromServer) {
//            getContentResolver().insert(TudokuContentProvider.ATTRACTIONS_CONTENT_URI, attraction.asContentValues());
//        }
    }
}
