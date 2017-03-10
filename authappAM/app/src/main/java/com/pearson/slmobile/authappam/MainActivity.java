package com.pearson.slmobile.authappam;

import java.util.ArrayList;
import java.util.List;

import ListAdapter.Item;
import ListAdapter.ListAdapter;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	private String TAG = this.getClass().getSimpleName();
	private AccountManager mAccountManager;
	@SuppressWarnings("rawtypes")
	private List list = null;
	private ListView listView;
	private ListAdapter listadaptor;
	public static final String DEMO_ACCOUNT_NAME = "Demo Account";
	public static final String DEMO_ACCOUNT_PASSWORD = "Demo123";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mAccountManager = AccountManager.get(this);

		((Button) findViewById(R.id.button1)).setOnClickListener(this);
		((Button) findViewById(R.id.button2)).setOnClickListener(this);

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

	private ArrayList<Item> getData() {
		ArrayList<Item> accountsList = new ArrayList<Item>();

		// Getting all registered Our Application Accounts;
		try {

			Account[] accounts = AccountManager.get(this).getAccountsByType(getString(R.string.auth_type));
			for (Account account : accounts) {
				Item item = new Item(account.type, account.name);
				accountsList.add(item);
			}
		} catch (Exception e) {
			Log.i(TAG, "Exception:" + e);
		}

		// For all registered accounts;
		/*
		 * try { Account[] accounts = AccountManager.get(this).getAccounts();
		 * for (Account account : accounts) { Item item = new Item(
		 * account.type, account.name); accountsList.add(item); } } catch
		 * (Exception e) { Log.i("Exception", "Exception:" + e); }
		 */
		return accountsList;
	}

	void createDemoAccount() {
		Account account = new Account(DEMO_ACCOUNT_NAME, getString(R.string.auth_type));
		boolean accountCreated = mAccountManager.addAccountExplicitly(account, DEMO_ACCOUNT_PASSWORD, null);
		if (accountCreated) {
			showMessage("Account Created");
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onClick(View v) {

		if (v.getId() == R.id.button1) {
			Intent createIntent = new Intent(MainActivity.this, AuthenticatorActivity.class);
			startActivity(createIntent);
		}
		if (v.getId() == R.id.button2) {
			list = getData();
			listView = (ListView) findViewById(R.id.listView1);
			listadaptor = new ListAdapter(MainActivity.this, R.layout.row_layout, list);
			listView.setAdapter(listadaptor);
		}
	}
}
