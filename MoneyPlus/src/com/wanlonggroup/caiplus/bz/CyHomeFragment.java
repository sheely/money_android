package com.wanlonggroup.caiplus.bz;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.damon.ds.widget.DSActionBar;
import com.wanlonggroup.caiplus.R;
import com.wanlonggroup.caiplus.adapter.BasicDSAdapter;
import com.wanlonggroup.caiplus.app.BasePtrListFragment;

public class CyHomeFragment extends BasePtrListFragment implements View.OnClickListener {

	CyListAdapter adapter;

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		actionBar().setDisplayHomeAsUpEnabled(false);
		setTitle("财友");
		invalidateActionBar();
	}

	@Override
	protected boolean hasActionBar() {
		return true;
	}

	@Override
	public void onCreateActionBar(DSActionBar actionBar) {
		actionBar.addAction(R.drawable.ic_search, "ic_search", this);
	}

	@Override
	public void onClick(View v) {
		startActivity("cp://querycy");
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	};


	class CyListAdapter extends BasicDSAdapter {

		@Override
		public View getCPItemView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cy_list_item_view, parent,
					false);
			}
			return convertView;
		}

		@Override
		public void loadNextData(int startIndex) {
		}

	}

}
