package com.yahoo.mobile.client.android.flickr.fragment;

import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yahoo.mobile.client.android.flickr.R;
import com.yahoo.mobile.client.android.ymagine.BitmapFactory;

public class WelcomePageFragment extends Fragment {
	
	private View mContentView;
	private ImageView mAnimationView;
	
	private boolean mLeftView;
	
	private Bitmap mBitmap;
	private BitmapFactory.Options mOpts;
	
	private TypedArray mAnimationImageArray;
	private int mAnimationImageCount;

	public static WelcomePageFragment make(int animationId, String msg, boolean interpolateFrames) {
		WelcomePageFragment fragment = new WelcomePageFragment();
		Bundle args = new Bundle();
		args.putInt("EXTRA_ANIMATION_ID", animationId);
		args.putString("EXTRA_MESSAGE_TEXT", msg);
		args.putBoolean("EXTRA_SHOULD_INTERPOLATE_FRAMES", interpolateFrames);
		fragment.setArguments(args);
		return fragment;
	}
	
	private int getAnimationId() {
		return getArguments().getInt("EXTRA_ANIMATION_ID");
	}

	private boolean shouldInterpolateFrames() {
		return getArguments().getBoolean("EXTRA_SHOULD_INTERPOLATE_FRAMES");
	}
	
	public final void onScroll(float positionOffset) {
		if (getActivity() == null || getAnimationId() <= 0) {
			return;
		}
		
		int index = -1;
		int midIndex = mAnimationImageCount / 2;
		
		if (mLeftView) {
			float offset = positionOffset;
			if (shouldInterpolateFrames()) {
				// 让左边的View变化稍慢
				offset = (float) Math.pow(positionOffset, 2.0 / 3.0);
			}
			index = (int) (offset * midIndex + midIndex);
		} else {
			float offset = positionOffset;
			if (shouldInterpolateFrames()) {
				// 让右边边的View变化稍快
				offset = (float) Math.pow(positionOffset, 3.0 / 2.0);
			}
			index = (int) (offset * midIndex);
		}
		
		if (index < 0 || index >= mAnimationImageCount) {
			return;
		}
		
		int id = mAnimationImageArray.getResourceId(index, 0);
		if (mOpts == null) {
			mOpts = new BitmapFactory.Options();
			mOpts.inMutable = true;
			mOpts.inScaled = false;
			mOpts.inSampleSize = 1;
		}
		mOpts.inBitmap = mBitmap;
		mBitmap = BitmapFactory.decodeResource(getResources(), id, mOpts);
		mAnimationView.setImageBitmap(mBitmap);
	}

	public final void setLeftView(boolean leftView) {
		mLeftView = leftView;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mContentView = inflater.inflate(R.layout.fragment_welcome_page, container, false);
		
		if (getAnimationId() > 0) {
			mAnimationView = (ImageView) mContentView.findViewById(R.id.activity_welcome_animation_image);
			mAnimationImageArray = getResources().obtainTypedArray(getAnimationId());
			mAnimationImageCount = mAnimationImageArray.length();
		}
		
		TextView msgView = (TextView) mContentView.findViewById(R.id.fragment_welcome_page_primary_text_view);
		String msg = getArguments().getString("EXTRA_MESSAGE_TEXT");
		msgView.setText(msg);
		
		return mContentView;
	}

	public void onDestroyView() {
		super.onDestroyView();
		if (mAnimationImageArray != null) {			
			mAnimationImageArray.recycle();
		}
	}
}