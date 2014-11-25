package tudoku.com.tudoku.service.auth;

import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;

import tudoku.com.tudoku.R;

public class TudokuAuthenticationServerConnector {

    public static Intent signIn(String username, String password, String authTokenType, Context context) {
        Intent result = new Intent();
        if (!username.equals("a")) {
            result.putExtra(AccountManager.KEY_BOOLEAN_RESULT, true);
            result.putExtra(AccountManager.KEY_ACCOUNT_NAME, username);
            result.putExtra(AccountManager.KEY_AUTHTOKEN, username + ":" + password);
            result.putExtra(AccountManager.KEY_PASSWORD, password);
        } else {
            result.putExtra(AccountManager.KEY_BOOLEAN_RESULT, false);
            result.putExtra(AccountManager.KEY_AUTH_FAILED_MESSAGE,
                    context.getString(R.string.error_login_wrong_credentials));
        }

        return result;
    }

}
