package com.yahoo.mobile.client.android.flickr.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class CustomFontTextView extends TextView {
	
	public CustomFontTextView(Context context) {
		this(context, null);
	}

	public CustomFontTextView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CustomFontTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		String font = FontUtil.getTypefaceName(context, attrs, defStyle);
		if (font == null) return;
		Typeface typeface = FontUtil.getTypeface(getResources(), font);
		if (typeface == null) return;
		setTypeface(typeface);
	}
}