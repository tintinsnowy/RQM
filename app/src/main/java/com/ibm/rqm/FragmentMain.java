package com.ibm.rqm;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.ibm.rqm.fragments.ReportFragment;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class FragmentMain extends Fragment {

	private boolean mSearchCheck;
	private static final String TEXT_FRAGMENT = "TEXT_FRAGMENT";

	private View mRootView;

	private ViewPager mPager;

	//页面列表
	private ArrayList<ReportFragment> fragmentList;
	//标题列表
	private ArrayList<String> titleList;

	//通过pagerTabStrip可以设置标题的属性
	private PagerTabStrip pagerTabStrip;
	private PagerTitleStrip pagerTitleStrip;

	public FragmentMain newInstance(String text){
		FragmentMain mFragment = new FragmentMain();
		Bundle mBundle = new Bundle();
		mBundle.putString(TEXT_FRAGMENT, text);
		mFragment.setArguments(mBundle);
		return mFragment;
	}


	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mPager = (ViewPager)mRootView.findViewById(R.id.pager);

		pagerTabStrip = (PagerTabStrip)mRootView.findViewById(R.id.tabstrip);
		pagerTabStrip.setTabIndicatorColor(getResources().getColor(R.color.nliveo_white));
		pagerTabStrip.setBackgroundColor(getResources().getColor(R.color.nliveo_blue_colorPrimary));

	}

	@Override
	public void onResume() {
		super.onResume();
		setUpViewPager();
	}

	private void setUpViewPager(){
		fragmentList = new ArrayList<ReportFragment>();
		titleList = new ArrayList<String>();
		//添加Fragment 和 title
		titleList.add(getString(R.string.title_execution_trend_report));
		titleList.add(getString(R.string.title_requirements_coverage));
		titleList.add(getString(R.string.title_execution_status_by_owner));
		titleList.add(getString(R.string.title_execution_status_by_testcase));


		fragmentList.add(ReportFragment.newInstance(getString(R.string.execution_trend_report), 0));
		fragmentList.add(ReportFragment.newInstance(getString(R.string.requirements_coverage), 1));
		fragmentList.add(ReportFragment.newInstance(getString(R.string.execution_status_by_owner), 2));
		fragmentList.add(ReportFragment.newInstance(getString(R.string.execution_status_by_testcase), 3));

		mPager.setAdapter(new MyAdapter(getChildFragmentManager()));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		mRootView = inflater.inflate(R.layout.fragment_main, container, false);
		mRootView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		return mRootView;
	}


	// TODO 刷新操作需要添加
	public void reflesh()
	{
		for (ReportFragment fragment : fragmentList) {
			fragment.reflesh();
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();

		try {
			Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
			childFragmentManager.setAccessible(true);
			childFragmentManager.set(this, null);

		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}


    public class MyAdapter extends FragmentPagerAdapter {
        public MyAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int arg0){
            return fragmentList.get(arg0);
        }

        @Override
        public int getCount(){
            return fragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position){
            return titleList.get(position);
        }
    }
}
