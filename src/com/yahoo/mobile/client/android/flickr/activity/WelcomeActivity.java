package com.yahoo.mobile.client.android.flickr.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.yahoo.mobile.client.android.flickr.R;
import com.yahoo.mobile.client.android.flickr.fragment.WelcomePageFragment;
import com.yahoo.mobile.client.android.flickr.ui.widget.ParallaxBackgroundView;

public class WelcomeActivity extends FragmentActivity implements OnPageChangeListener {

	private ParallaxBackgroundView mBackgroundView;
	private ViewPager mViewPager;
	private WelcomePagerAdapter mAdapter;
	private ImageView mLogoView;
	private LinearLayout mPageIndicator;
	private Button mStartBtn;

	private float mLogoViewTranslateDistance;
	private float mStartBtnTranslateDistance;
	private long mLastTime;
	
	private int mCurrentPosition;
	private SparseArray<WelcomePageFragment> mFragmentCache;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		setContentView(R.layout.activity_welcome);
		mBackgroundView = (ParallaxBackgroundView) findViewById(R.id.activity_welcome_background_image);
		mViewPager = (ViewPager) findViewById(R.id.activity_welcome_viewpager);
		mViewPager.setOnPageChangeListener(this);
		mAdapter = new WelcomePagerAdapter(getSupportFragmentManager());
		mLogoView = (ImageView) findViewById(R.id.activity_welcome_flickr_logo);
		mPageIndicator = (LinearLayout) findViewById(R.id.activity_welcome_page_indicator);
		mStartBtn = (Button) findViewById(R.id.activity_welcome_get_started_button);
		mStartBtn.setOnClickListener(null);

		mFragmentCache = new SparseArray<WelcomePageFragment>(5);

		final View container = findViewById(R.id.activity_welcome_container);
		ViewTreeObserver observer = container.getViewTreeObserver();
		observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			@Override
			public void onGlobalLayout() {
				container.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				
				int paddingTop = mLogoView.getBottom();
				int paddingBottom = container.getBottom() - mPageIndicator.getTop();
				mViewPager.setPadding(0, paddingTop, 0, paddingBottom);

				DisplayMetrics outMetrics = new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
				float translateDelta = 35.0f * outMetrics.density;
				mLogoViewTranslateDistance = container.getHeight() / 2 - mLogoView.getBottom() - translateDelta;
				mStartBtnTranslateDistance = container.getHeight() / 2 - mStartBtn.getTop() + translateDelta;
				
				mCurrentPosition = 0;

				mViewPager.setAdapter(mAdapter);
				mViewPager.setCurrentItem(mCurrentPosition);
				mBackgroundView.setBackground(R.drawable.welcome_screen_parallax_bg);
			}
		});
	}
	
	@Override
	public void onPageSelected(int position) {
		mCurrentPosition = position;
		updatePageIndicator(position);
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		long now = SystemClock.uptimeMillis();
		mCurrentPosition = position;
		if (now - mLastTime <= 10) {
			return;
		}

		mLastTime = now;

		WelcomePageFragment f1 = mAdapter.getItem(position);
		f1.setLeftView(true);
		f1.onScroll(positionOffset);

		boolean startOrEnd = position == 0 || position == 4;
		if (positionOffset != 0.0 || !startOrEnd) {
			WelcomePageFragment f2 = mAdapter.getItem(position + 1);
			f2.setLeftView(false);
			f2.onScroll(positionOffset);
		}

		if (mCurrentPosition == 0) {
			updateView4TheFirstPage(positionOffset);
		} else if (mCurrentPosition >= 3) {
			// 如果已经是最后一页（4），直接作为1处理，让LOGO和按钮静止下来
			if (mCurrentPosition >= mAdapter.getCount() - 1) {								
				positionOffset = 1.0f;
			}
			updateView4TheLastPage(positionOffset);
		}
		
		int totalWidth = mViewPager.getWidth() * (mAdapter.getCount() - 1);
		int parallaxDistance = positionOffsetPixels + mCurrentPosition * mViewPager.getWidth();
		float parallax = 1.0f * parallaxDistance / totalWidth;
		mBackgroundView.setParallax(parallax);
	}

	@Override
	public void onPageScrollStateChanged(int state) {}

	private void updatePageIndicator(int position) {
		int count = mPageIndicator.getChildCount();
		for (int i = 0; i < count; i++) {
			View child = mPageIndicator.getChildAt(i);
			int bg = 0;
			if (i == position) {
				bg = R.drawable.viewpager_indicator_bullet_highlighted;
			} else {
				bg = R.drawable.viewpager_indicator_bullet_normal;
			}
			child.setBackgroundResource(bg);
		}
	}
	
	private void updateView4TheFirstPage(float positionOffset) {
		float translate = (float) Math.pow(1.0f - positionOffset, 1.5f);
		mLogoView.setTranslationY(mLogoViewTranslateDistance * translate);
		float scale = (float) Math.sqrt(1.0f - positionOffset) + 1.0f;
		mLogoView.setScaleX(scale);
		mLogoView.setScaleY(scale);
	}
	
	private void updateView4TheLastPage(float positionOffset) {
		float translate = (float) Math.pow(positionOffset, 1.5f);
		mLogoView.setTranslationY(mLogoViewTranslateDistance * translate);
		float scale = (float) Math.sqrt(positionOffset) + 1.0f;
		mLogoView.setScaleX(scale);
		mLogoView.setScaleY(scale);
		
		mStartBtn.setTranslationY(mStartBtnTranslateDistance * translate);
		
		// 加速消失，所以乘以系数3.5
		float alpha = Math.max(1.0f - 3.5f * positionOffset, 0.0f);
		mPageIndicator.setAlpha(alpha);
	}

	private final class WelcomePagerAdapter extends FragmentStatePagerAdapter {

		public WelcomePagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public WelcomePageFragment getItem(int position) {
			WelcomePageFragment fragment = mFragmentCache.get(position);
			if (fragment != null) {
				return fragment;
			}

			int animationId = 0;
			String msg = null;
			boolean interpolateFrames = false;

			switch (position) {
			case 0:
				animationId = 0;
				msg = null;
				interpolateFrames = true;
				break;
			case 1:
				animationId = R.array.welcome_groups_animation;
				msg = getString(R.string.welcome_page_2_message);
				interpolateFrames = true;
				break;
			case 2:
				animationId = R.array.welcome_lens_animation;
				msg = getString(R.string.welcome_page_3_message);
				interpolateFrames = false;
				break;
			case 3:
				animationId = R.array.welcome_albums_animation;
				msg = getString(R.string.welcome_page_4_message);
				interpolateFrames = true;
				break;
			case 4:
				animationId = 0;
				msg = null;
				interpolateFrames = true;
				break;
			default:
				break;
			}

			fragment = WelcomePageFragment.make(animationId, msg, interpolateFrames);
			mFragmentCache.put(position, fragment);

			return fragment;
		}

		@Override
		public int getCount() {
			return 5;
		}
	}
}