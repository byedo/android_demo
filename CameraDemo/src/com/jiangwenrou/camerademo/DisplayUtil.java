package com.jiangwenrou.camerademo;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;

public class DisplayUtil {
	private static int width = 0;
	private static int height = 0;

	@SuppressWarnings("deprecation")
	public static int getWidth(Activity activity) {
		if (width != 0) {
			return width;
		} else {
			Display display = activity.getWindow().getWindowManager().getDefaultDisplay();
			width = display.getWidth();
			height = display.getHeight();
			DisplayMetrics metrics = new DisplayMetrics();
			display.getMetrics(metrics);
			if (Build.VERSION.SDK_INT >= 17) {
				try {
					android.graphics.Point realSize = new android.graphics.Point();
					Display.class.getMethod("getRealSize", android.graphics.Point.class).invoke(display, realSize);
					height = realSize.y;
				} catch (Exception ignored) {
				}
			}
			return width;
		}
	}

	@SuppressWarnings("deprecation")
	public static int getHeight(Activity activity) {
		if (height != 0) {
			return height;
		} else {
			Display display = activity.getWindow().getWindowManager().getDefaultDisplay();
			width = display.getWidth();
			height = display.getHeight();
			DisplayMetrics metrics = new DisplayMetrics();
			display.getMetrics(metrics);
			if (Build.VERSION.SDK_INT >= 17) {
				try {
					android.graphics.Point realSize = new android.graphics.Point();
					Display.class.getMethod("getRealSize", android.graphics.Point.class).invoke(display, realSize);
					height = realSize.y;
				} catch (Exception ignored) {
				}
			}
			return height;
		}
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

}
