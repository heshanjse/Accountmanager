package com.pearson.slmobile.authappam;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;

import static com.pearson.slmobile.authappam.AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS;


public class AuthenticatorActivity extends AccountAuthenticatorActivity implements OnClickListener{

	public final static String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE";
	public final static String ARG_AUTH_TYPE = "AUTH_TYPE";
	public final static String ARG_ACCOUNT_NAME = "ACCOUNT_NAME";
	public final static String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";

	public static final String KEY_ERROR_MESSAGE = "ERR_MSG";

	public final static String PARAM_USER_PASS = "USER_PASS";

	private final String TAG = this.getClass().getSimpleName();

	private AlertDialog mAlertDialog;
	private boolean mInvalidate;


	private AccountManager mAccountManager;
	private String mAuthTokenType;
	String authtoken = "123456789"; // this
	String password = "12345";


	String accountName;
	public static final String DEMO_ACCOUNT_NAME = "Demo Account";
	public static final String DEMO_ACCOUNT_PASSWORD = "Demo123";

	public Account findAccount(String accountName) {
		for (Account account : mAccountManager.getAccounts())
			if (TextUtils.equals(account.name, accountName) && TextUtils.equals(account.type, getString(R.string.auth_type))) {
				System.out.println("FOUND");
				return account;
			}
		return null;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_login);

		Log.d(TAG, "onCreate");

		mAccountManager = AccountManager.get(getBaseContext());

		// If this is a first time adding, then this will be null
		accountName = getIntent().getStringExtra(ARG_ACCOUNT_NAME);
		mAuthTokenType = getIntent().getStringExtra(ARG_AUTH_TYPE);

		if (mAuthTokenType == null)
			mAuthTokenType = getString(R.string.auth_type);

		findAccount(accountName);

		System.out.println(mAuthTokenType + ", accountName : " + accountName);

		((Button)findViewById(R.id.submit)).setOnClickListener(this);

		findViewById(R.id.acbutton).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showAccountPicker(AUTHTOKEN_TYPE_FULL_ACCESS, false);
				//final Account availableAccounts[] = mAccountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);
				//getExistingAccountAuthToken(availableAccounts[1], AUTHTOKEN_TYPE_FULL_ACCESS);
			}
		});
	}


//	getAuthToken


	private void showAccountPicker(final String authTokenType, final boolean invalidate) {
		mInvalidate = invalidate;

		final Account availableAccounts[] = mAccountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);

		AccountManagerFuture accFut = AccountManager.get(getBaseContext()).getAuthToken(availableAccounts[0], "com.pearson.slmobile.authappam", null, this, null, null);

		try {
			authtoken = ((Bundle)accFut.getResult()).get("authtoken").toString();
		} catch (OperationCanceledException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (AuthenticatorException e) {
			e.printStackTrace();
		}




		if (availableAccounts.length == 0) {
			Toast.makeText(this, "No accounts", Toast.LENGTH_SHORT).show();
		} else {
			String name[] = new String[availableAccounts.length];
			for (int i = 0; i < availableAccounts.length; i++) {
				name[i] = String.valueOf(availableAccounts[i].name);
			}

			// Account picker
		//	mAlertDialog = new AlertDialog.Builder(this).setTitle("Pick Account").setAdapter(new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, name), new DialogInterface.OnClickListener() {
				mAlertDialog = new AlertDialog.Builder(this)
						.setTitle("Pick Account")
						.setMessage(authtoken)
						.show();
//						.setAdapter(new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, name), new DialogInterface.OnClickListener() {
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					if(invalidate)
//						invalidateAuthToken(availableAccounts[which], authTokenType);
//					else
//						getExistingAccountAuthToken(availableAccounts[which], authTokenType);
//				}
		//	}
//		).create();
//			mAlertDialog.show();
		}
	}

	private void getExistingAccountAuthToken(Account account, String authTokenType) {
		final AccountManagerFuture<Bundle> future = mAccountManager.getAuthToken(account, authTokenType, null, this, null, null);

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Bundle bnd = future.getResult();

					final String authtoken = bnd.getString(AccountManager.KEY_AUTHTOKEN);
					showMessage((authtoken != null) ? "SUCCESS!\ntoken: " + authtoken : "FAIL");
					Log.d("udinic", "GetToken Bundle is " + bnd);
				} catch (Exception e) {
					e.printStackTrace();
					showMessage(e.getMessage());
				}
			}
		}).start();
	}

	private void invalidateAuthToken(final Account account, String authTokenType) {
		final AccountManagerFuture<Bundle> future = mAccountManager.getAuthToken(account, authTokenType, null, this, null,null);

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Bundle bnd = future.getResult();

					final String authtoken = bnd.getString(AccountManager.KEY_AUTHTOKEN);
					mAccountManager.invalidateAuthToken(account.type, authtoken);
					showMessage(account.name + " invalidated");
				} catch (Exception e) {
					e.printStackTrace();
					showMessage(e.getMessage());
				}
			}
		}).start();
	}










	void userSignIn() {

		// You should probably call your server with user credentials and get
		// the authentication token here.
		// For demo, I have hard-coded it.
		authtoken = "123456789";

		accountName = ((EditText) findViewById(R.id.accountName)).getText().toString().trim();
		password = ((EditText) findViewById(R.id.accountPassword)).getText().toString().trim();

		if (accountName.length() > 0) {
			Bundle data = new Bundle();
			data.putString(AccountManager.KEY_ACCOUNT_NAME, accountName);
			data.putString(AccountManager.KEY_ACCOUNT_TYPE, mAuthTokenType);
			data.putString(AccountManager.KEY_AUTHTOKEN, authtoken);
			data.putString(PARAM_USER_PASS, password);

			// Some extra data about the user
			Bundle userData = new Bundle();
			userData.putString("UserID", "25");
			data.putBundle(AccountManager.KEY_USERDATA, userData);

			//Make it an intent to be passed back to the Android Authenticator
			final Intent res = new Intent();
			res.putExtras(data);

			//Create the new account with Account Name and TYPE
			final Account account = new Account(accountName, mAuthTokenType);

			//Add the account to the Android System
			if (mAccountManager.addAccountExplicitly(account, password, userData)) {
				// worked
				Log.d(TAG, "Account added");
				mAccountManager.setAuthToken(account, mAuthTokenType, authtoken);
				setAccountAuthenticatorResult(data);
				setResult(RESULT_OK, res);
				finish();
			} else {
				// guess not
				Log.d(TAG, "Account NOT added");
			}

		}
	}

	@Override
	public void onClick(View v) {
		userSignIn();		
	}

	private void showMessage(final String msg) {
		if (TextUtils.isEmpty(msg))
			return;

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
			}
		});
	}



	void createDemoAccount() {
		Account account = new Account(DEMO_ACCOUNT_NAME, getString(R.string.auth_type));
		boolean accountCreated = mAccountManager.addAccountExplicitly(account, DEMO_ACCOUNT_PASSWORD, null);
		if (accountCreated) {
			showMessage("Account Created");
		}
	}
}
