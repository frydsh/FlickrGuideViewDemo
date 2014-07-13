package com.yahoo.mobile.client.android.flickr.ui.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.yahoo.mobile.client.android.ymagine.BitmapFactory;

public class ParallaxBackgroundView extends View {
	
	private Bitmap mBackground;
	private float mParallax;
	private Rect mSrcRect;
	private Rect mDstRect;
	private Paint mPaint;

	public ParallaxBackgroundView(Context context) {
		super(context);
		init();
	}

	public ParallaxBackgroundView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setFilterBitmap(true);
	}

	public final void setParallax(float parallax) {
		if (parallax == mParallax) return;
		if (parallax > 1.0f) {
			parallax = 1.0f;
		} else if (parallax < 0.0f) {
			parallax = 0.0f;
		}
		mParallax = parallax;
		invalidate();
	}

	public final void setBackground(int imgResId) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inMaxHeight = getMeasuredHeight();
		mBackground = BitmapFactory.decodeResource(getResources(), imgResId, opts);
		if (mBackground != null) {
			mSrcRect = new Rect(0, 0, mBackground.getWidth(), mBackground.getHeight());
			mDstRect = new Rect(0, 0, 0, getMeasuredHeight());
		}
		invalidate();
	}

	public void onDraw(Canvas canvas) {
		if (mBackground != null) {
			float r = 1.0f * mDstRect.height() / mSrcRect.height();
		    int width = (int) (mSrcRect.width() * r);
		    int left = - (int) ((width - getMeasuredWidth()) * mParallax);
		    mDstRect.left = left;
		    mDstRect.right = (left + width);
		    canvas.drawBitmap(mBackground, mSrcRect, mDstRect, mPaint);
		} else {
			super.onDraw(canvas);
		}
	}
}