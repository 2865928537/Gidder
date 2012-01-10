package net.antoniy.gidder.ui.activity;

import net.antoniy.gidder.R;
import net.antoniy.gidder.ui.adapter.SlideAdapter;
import net.antoniy.gidder.ui.fragment.FragmentType;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Window;

import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TitlePageIndicator;

public class SlideActivity extends FragmentActivity {
	private final static String TAG = SlideActivity.class.getSimpleName();
	
//	private final static FragmentType[] CONTENT = new FragmentType[] { FragmentType.USERS, FragmentType.REPOSITORIES };
	private final static FragmentType[] CONTENT = FragmentType.values();
	
	private SlideAdapter mAdapter;
	private ViewPager mPager;
	private PageIndicator mIndicator;
//	private FragmentType currentFragment = FragmentType.USERS;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.slide);
		
		Log.i(TAG, "Creating slide activity...");
		
		mAdapter = new SlideAdapter(getSupportFragmentManager(), CONTENT);
		
		mPager = (ViewPager)findViewById(R.id.slidePager);
		mPager.setAdapter(mAdapter);
//		mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
//			@Override
//			public void onPageSelected(int position) {
////				currentFragment = SlideActivity.CONTENT[position];
//				Log.i(TAG, "Current fragment: " + SlideActivity.CONTENT[position].getTitle());
//			}
//		});
		
		mIndicator = (TitlePageIndicator)findViewById(R.id.slideIndicator);
//		mIndicator = (TabPageIndicator)findViewById(R.id.slideIndicator);
		mIndicator.setViewPager(mPager);
		mIndicator.setCurrentItem(mAdapter.getCount() / 2);
	}
	
	public FragmentType getCurrentFragment() {
		return CONTENT[mPager.getCurrentItem() % CONTENT.length];
	}
}
