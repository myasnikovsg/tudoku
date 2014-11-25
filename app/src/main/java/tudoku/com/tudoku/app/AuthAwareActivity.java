package tudoku.com.tudoku.app;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;

import tudoku.com.tudoku.service.TudokuService;
import tudoku.com.tudoku.util.Action;

public class AuthAwareActivity extends Activity {
    public static final String AUTH_PREFS = "tudoku_auth_prefs";

    private static final String LOG_TAG = "AUTH_AC";

    private static IntentFilter sReAuthIntentFilter = new IntentFilter(Action.RE_AUTHENTICATE);

    AccountManager mAccountManager;
    private BroadcastReceiver mBroadcastReceiver;

    private Intent mLastIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAccountManager = AccountManager.get(this);

        mBroadcastReceiver = new AuthBroadcastReceiver();
        registerReceiver(mBroadcastReceiver, sReAuthIntentFilter);
    }

    protected void sendServiceRequest(Intent intent) {
        mLastIntent = intent;
        startService(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private class AuthBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, Intent intent) {
            String accountType = intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE);
            if (accountType == null || accountType.equals(AuthenticatorActivity.ACCOUNT_TYPE)) {
                String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                if (accountType == null) {
                    accountType = AuthenticatorActivity.ACCOUNT_TYPE;
                }
                if (accountName == null) {
                    accountName = AuthenticatorActivity.NO_USER_STRING;
                }
                Account account = new Account(accountName, accountType);
                final AccountManagerFuture<Bundle> accountManagerFuture = mAccountManager.getAuthToken(account, AuthenticatorActivity.AUTH_TOKEN_TYPE, null, (Activity) context, null, null);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Bundle authResult = accountManagerFuture.getResult();

                            Intent serviceIntent = new Intent(Action.SET_ACCOUNT);
                            serviceIntent.setClass(context, TudokuService.class);
                            serviceIntent.putExtra(AccountManager.KEY_ACCOUNT_NAME,
                                    authResult.getString(AccountManager.KEY_ACCOUNT_NAME));
                            serviceIntent.putExtra(AccountManager.KEY_ACCOUNT_TYPE,
                                    authResult.getString(AccountManager.KEY_ACCOUNT_TYPE));
                            startService(serviceIntent);

                            if (mLastIntent != null) {
                                startService(mLastIntent);
                                Log.d(LOG_TAG, "Resending last intent");
                            }

                        } catch (OperationCanceledException | IOException | AuthenticatorException e) {
                            Log.d(LOG_TAG, "Error during re-authentication");
                        }
                    }
                }).start();
            } else {
                // handle the rest sign in here
            }
        }
    }

}
