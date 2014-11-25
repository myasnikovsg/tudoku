package tudoku.com.tudoku.app;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import tudoku.com.tudoku.R;
import tudoku.com.tudoku.service.TudokuService;
import tudoku.com.tudoku.service.auth.TudokuAuthenticationServerConnector;
import tudoku.com.tudoku.util.Action;
import tudoku.com.tudoku.util.Extra;
import tudoku.com.tudoku.util.validation.FieldValidator;
import tudoku.com.tudoku.util.validation.ValidationResult;
import tudoku.com.tudoku.util.validation.ValidationType;

public class AuthenticatorActivity extends AccountAuthenticatorActivity implements View.OnClickListener{

    public static final String ACCOUNT_TYPE = "com.tudoku.account";
    public static final String AUTH_TOKEN_TYPE = "com.tudoku.auth";
    // bad user
    public static final String NO_USER_STRING = "@no_user";

    private FieldValidator mFieldValidator;

    private EditText mUsernameField;
    private EditText mPasswordField;

    private AccountManager mAccountManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);

        mUsernameField = (EditText) findViewById(R.id.username_field);
        String username = getIntent().getStringExtra(AccountManager.KEY_ACCOUNT_NAME);

        mPasswordField = (EditText) findViewById(R.id.password_field);
        mPasswordField.setTypeface(Typeface.DEFAULT);
        mPasswordField.setTransformationMethod(new PasswordTransformationMethod());

        mFieldValidator = new FieldValidator(this);
        mFieldValidator.addForValidation(mUsernameField, ValidationType.USERNAME);
        mFieldValidator.addForValidation(mPasswordField, ValidationType.PASSWORD);

        if (getIntent().getBooleanExtra(Extra.IS_CREATING_NEW_ACCOUNT, false)) {
            findViewById(R.id.login_facebook_button).setVisibility(View.GONE);
            findViewById(R.id.login_twitter_button).setVisibility(View.GONE);
        } else {
            if (username != null) {
                mUsernameField.setText(username);
            }
        }

        Button loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(this);

        mAccountManager = AccountManager.get(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button:
                InputMethodManager imm = (InputMethodManager)getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                View focusedView = this.getCurrentFocus();
                if (focusedView != null) {
                    imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
                }

                ValidationResult validationResult = mFieldValidator.validate();
                if (validationResult.equals(ValidationResult.RESULT_OK)) {
                    submit();
                } else {
                    Toast.makeText(getApplicationContext(), validationResult.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    validationResult.getReason().requestFocus();
                }
        }
    }

    public void submit() {
        final String username = mUsernameField.getText().toString();
        final String password = mPasswordField.getText().toString();
        new AsyncTask<Void, Void, Intent>() {
            @Override
            protected Intent doInBackground(Void... params) {
                return TudokuAuthenticationServerConnector.signIn(username, password,
                        null, AuthenticatorActivity.this);
            }
            @Override
            protected void onPostExecute(Intent intent) {
                if (intent.getBooleanExtra(AccountManager.KEY_BOOLEAN_RESULT, false)) {
                    intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, ACCOUNT_TYPE);
                    finishLogin(intent);
                } else {
                    Toast.makeText(getApplicationContext(), intent.getExtras().getString(AccountManager.KEY_AUTH_FAILED_MESSAGE),
                            Toast.LENGTH_SHORT).show();
                    mPasswordField.setText("");
                    mPasswordField.requestFocus();
                }
            }
        }.execute();
    }

    private void finishLogin(Intent intent) {
        String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        String accountPassword = intent.getStringExtra(AccountManager.KEY_PASSWORD);
        final Account account = new Account(accountName,
                intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));
        if (getIntent().getBooleanExtra(Extra.IS_CREATING_NEW_ACCOUNT, false)) {
            String authToken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
            // Creating the account on the device and setting the auth token we got
            // (Not setting the auth token will cause another call to the server to authenticate the user)
            mAccountManager.addAccountExplicitly(account, accountPassword, null);
            mAccountManager.setAuthToken(account, AUTH_TOKEN_TYPE, authToken);
        } else {
            mAccountManager.setPassword(account, accountPassword);
        }

        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
    }

}
