package com.xdamon.app;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;

import com.xdamon.library.R;

public class FragmentLoaderActivity extends DSActivity {

	public static final String EXTRA_FRAGMENT = "_FragmentLoaderActivity_fragment_class_name";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_loader_activity);

		String fragmentClassName = getStringParam(EXTRA_FRAGMENT);
		if (!TextUtils.isEmpty(fragmentClassName)) {
			Bundle bundle = new Bundle();
			bundle.putAll(getIntent().getExtras());

			Uri uri = getIntent().getData();
			if (uri != null) {
				try {
					String params = uri.getQuery();
					if (!TextUtils.isEmpty(params)) {
						String[] pas = params.split("&");
						for (String str : pas) {
							String[] tmp = str.split("=");
							if (tmp.length > 1) {
								bundle.putString(tmp[0], tmp[1]);
							}
						}
					}
				} catch (Exception e) {

				}
			}

			Fragment fragment = Fragment.instantiate(this, fragmentClassName, bundle);
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
			transaction.replace(R.id.fragment_container, fragment, EXTRA_FRAGMENT);
			transaction.commitAllowingStateLoss();
			getSupportFragmentManager().executePendingTransactions();
		}

	}
}
