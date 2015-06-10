package com.jiangwenrou.camerademo;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class CustomWidget {

	public static void cToast(Context context, String show) {
		Toast toast = Toast.makeText(context, show, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	public static void cToastL(Context context, String show) {
		Toast toast = Toast.makeText(context, show, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}
}