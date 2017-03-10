package ListAdapter;

import java.util.List;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.pearson.slmobile.authappam.R;

public class ListAdapter extends ArrayAdapter<Item> {
	private List<Item> appsList = null;
	private Context context;

	public ListAdapter(Context context, int textViewResourceId, List<Item> appsList) {
		super(context, textViewResourceId, appsList);
		this.context = context;
		this.appsList = appsList;
	}

	@Override
	public int getCount() {
		return ((null != appsList) ? appsList.size() : 0);
	}

	@Override
	public Item getItem(int position) {
		return ((null != appsList) ? appsList.get(position) : null);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (null == view) {
			LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = layoutInflater.inflate(R.layout.row_layout, parent, false);
		}

		final Item data = appsList.get(position);
		if (null != data) {

			TextView appName = (TextView) view.findViewById(R.id.key);
			TextView packageName = (TextView) view.findViewById(R.id.value);

			Button b = (Button) view.findViewById(R.id.button_remove);
			b.setTag(position);
			b.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(final View v) {
					AccountManager mAccountManager;
					mAccountManager = AccountManager.get(context);
					Account account = new Account(data.getValue(), context.getString(R.string.auth_type));
					mAccountManager.removeAccount(account, new AccountManagerCallback<Boolean>() {
						@Override
						public void run(AccountManagerFuture<Boolean> arg0) {
							if (arg0.isDone())
								Toast.makeText(context, "Removed " + data.getValue(), Toast.LENGTH_SHORT).show();
							appsList.remove(Integer.parseInt(String.valueOf(v.getTag())));
							notifyDataSetChanged();
						}
					}, null);
				}
			});

			appName.setText(data.getKey());
			packageName.setText(data.getValue());
		}
		return view;
	}
}
