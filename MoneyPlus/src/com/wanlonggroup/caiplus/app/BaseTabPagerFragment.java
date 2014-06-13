package com.wanlonggroup.caiplus.app;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;

import com.damon.ds.widget.TabIndicator;
import com.wanlonggroup.caiplus.R;

public class BaseTabPagerFragment extends BaseFragment implements OnTabChangeListener {
	protected TabHost mTabHost;
	protected ViewPager mViewPager;
	protected TabsAdapter mTabsAdapter;
	private OnTabChangeListener onTabChangeListener;
	private OnPageChangeListener onPageChangeListener;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		return onSetView(inflater, container);
	}
	
	public View onSetView(LayoutInflater inflater, ViewGroup container){
    	return inflater.inflate(R.layout.tabs_pager_fragment,container,false);
    }

	public ViewPager viewPager() {
		return mViewPager;
	}

	public TabHost tabHost() {
		return mTabHost;
	}

	public TabsAdapter tabsAdapter() {
		return mTabsAdapter;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mTabHost = (TabHost) view.findViewById(android.R.id.tabhost);
		mTabHost.setup();
		mViewPager = (ViewPager) view.findViewById(R.id.pager);
		mViewPager.setOffscreenPageLimit(3);
		mTabsAdapter = new TabsAdapter(this, mTabHost, mViewPager);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (savedInstanceState != null) {
			mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("tab", mTabHost.getCurrentTabTag());
	}

	public void addTab(String title, Class<?> clss, Bundle args) {
		addTab(title, 0, 0, clss, args);
	}

	public void addTab(String title, int indicatorView, Class<?> clss, Bundle args) {
		addTab(title, 0, indicatorView, clss, args);
	}

	public void addTab(String title, int icon, int indicatorView, Class<?> clss, Bundle args) {
		if (title == null) {
			throw new IllegalArgumentException("title cann't be null!");
		}

		mTabsAdapter.addTab(
				mTabHost.newTabSpec(title).setIndicator(
						new TabIndicator(getActivity(), title, icon, indicatorView).createIndicatorView(mTabHost)),
				clss, args);
	}

	public void setTabsAdapter(TabsAdapter adapter) {
		mTabsAdapter = adapter;
	}

	public void setOnPageChangeListener(OnPageChangeListener onPageChangeListener) {
		this.onPageChangeListener = onPageChangeListener;
	}

	/**
	 * This is a helper class that implements the management of tabs and all
	 * details of connecting a ViewPager with associated TabHost. It relies on a
	 * trick. Normally a tab host has a simple API for supplying a View or
	 * Intent that each tab will show. This is not sufficient for switching
	 * between pages. So instead we make the content part of the tab host 0dp
	 * high (it is not shown) and the TabsAdapter supplies its own dummy view to
	 * show as the tab content. It listens to changes in tabs, and takes care of
	 * switch to the correct paged in the ViewPager whenever the selected tab
	 * changes.
	 */
	public static class TabsAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener {
		private final FragmentActivity mContext;
		private final TabHost mTabHost;
		private final BaseTabPagerFragment mFragment;
		private final ViewPager mViewPager;
		private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

		static final class TabInfo {
			private final String tag;
			private final Class<?> clss;
			private final Fragment fragment;
			private final Bundle args;

			TabInfo(String _tag, Class<?> _class, Bundle _args) {
				tag = _tag;
				clss = _class;
				args = _args;
				fragment = null;
			}

			TabInfo(String _tag, Fragment _fragment, Bundle _args) {
				tag = _tag;
				fragment = _fragment;
				args = _args;
				clss = null;
			}
		}

		static class DummyTabFactory implements TabHost.TabContentFactory {
			private final Context mContext;

			public DummyTabFactory(Context context) {
				mContext = context;
			}

			@Override
			public View createTabContent(String tag) {
				View v = new View(mContext);
				v.setMinimumWidth(0);
				v.setMinimumHeight(0);
				return v;
			}
		}

		public TabsAdapter(BaseTabPagerFragment fragment, TabHost tabHost, ViewPager pager) {
			super(fragment.getActivity().getSupportFragmentManager());
			mFragment = fragment;
			mContext = fragment.getActivity();
			mTabHost = tabHost;
			mViewPager = pager;
			mTabHost.setOnTabChangedListener(fragment);
			mViewPager.setAdapter(this);
			mViewPager.setOnPageChangeListener(this);
		}

		public void addTab(TabHost.TabSpec tabSpec, Class<?> clss, Bundle args) {
			tabSpec.setContent(new DummyTabFactory(mContext));
			String tag = tabSpec.getTag();

			TabInfo info = new TabInfo(tag, clss, args);
			mTabs.add(info);
			mTabHost.addTab(tabSpec);
			notifyDataSetChanged();
		}

		public void addTab(TabHost.TabSpec tabSpec, Fragment fragment, Bundle args) {
			tabSpec.setContent(new DummyTabFactory(mContext));
			String tag = tabSpec.getTag();

			TabInfo info = new TabInfo(tag, fragment, args);
			mTabs.add(info);
			mTabHost.addTab(tabSpec);
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return mTabs.size();
		}

		@Override
		public Fragment getItem(int position) {
			TabInfo info = mTabs.get(position);
			if (info.fragment != null) {
				return info.fragment;
			}
			return Fragment.instantiate(mContext, info.clss.getName(), info.args);
		}

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			if (mFragment.onPageChangeListener != null) {
				mFragment.onPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
			}
		}

		@Override
		public void onPageSelected(int position) {
			// Unfortunately when TabHost changes the current tab, it kindly
			// also takes care of putting focus on it when not in touch mode.
			// The jerk.
			// This hack tries to prevent this from pulling focus out of our
			// ViewPager.
			TabWidget widget = mTabHost.getTabWidget();
			int oldFocusability = widget.getDescendantFocusability();
			widget.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
			mTabHost.setCurrentTab(position);
			widget.setDescendantFocusability(oldFocusability);
			if (mFragment.onPageChangeListener != null) {
				mFragment.onPageChangeListener.onPageSelected(position);
			}
		}

		@Override
		public void onPageScrollStateChanged(int state) {
			if (mFragment.onPageChangeListener != null) {
				mFragment.onPageChangeListener.onPageScrollStateChanged(state);
			}
		}

		public void clear() {
			FragmentTransaction transaction = mContext.getSupportFragmentManager().beginTransaction();
			for (TabInfo info : mTabs) {
				if (info.fragment != null) {
					transaction.remove(info.fragment);
				}
			}
			transaction.commit();
		}
	}

	@Override
	public void onTabChanged(String tabId) {
		int position = mTabHost.getCurrentTab();
		mTabsAdapter.notifyDataSetChanged();
		mViewPager.setCurrentItem(position);
		if (onTabChangeListener != null) {
			onTabChangeListener.onTabChanged(tabId);
		}
	}

	public void setOnTabChangeListener(OnTabChangeListener onTabChangeListener) {
		this.onTabChangeListener = onTabChangeListener;
	}

	public void clearAllFragment() {
		mTabsAdapter.clear();
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}
	
	protected void setTabWidgetBackground(int drawableId) {
		if (drawableId > 0) {
			mTabHost.getTabWidget().setBackgroundResource(drawableId);
		}
	}
}
