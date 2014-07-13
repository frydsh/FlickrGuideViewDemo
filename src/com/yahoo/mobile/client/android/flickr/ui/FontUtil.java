package com.yahoo.mobile.client.android.flickr.ui;

import java.util.HashMap;

import com.yahoo.mobile.client.android.flickr.R;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class FontUtil {

	private static HashMap<String, Typeface> mTypefaces = new HashMap<String, Typeface>();
	
	public static Typeface getTypeface(Resources res, String name) {
		if (name == null || res == null) {
			return null;
		}
		
		Typeface typeface = mTypefaces.get(name);
		if (typeface != null) {
			return typeface;
		}
	
		AssetManager am = res.getAssets();
		typeface = Typeface.createFromAsset(am, name);
		mTypefaces.put(name, typeface);
		
		return typeface;
	}
	
	public static String getTypefaceName(Context context, AttributeSet set, int defStyle) {
		if (context == null || set == null) {
			return null;
		}
		
		Theme theme = context.getTheme();
		if (theme == null) {
			return null;
		}
		
		int[] attrs = { R.attr.font };
		TypedArray typedArray = theme.obtainStyledAttributes(set, attrs, defStyle, 0);
		if (typedArray != null && typedArray.length() > 0) {			
			return typedArray.getString(0);
		}
		
		return null;
	}
}
