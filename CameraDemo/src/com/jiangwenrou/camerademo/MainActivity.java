package com.jiangwenrou.camerademo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;

public class MainActivity extends Activity implements OnClickListener {
	String TAG = "CameraActivity";
	private Camera mCamera;
	private CameraPreview mPreview;
	private Camera.Parameters mParams;
	ImageButton captureButton, button_cancel, button_ok, button_switch;
	FrameLayout preview;
	int id = 1;
	String path = null;
	Context ctx;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);
		ctx = getApplicationContext();
		// Create an instance of Camera
		if (checkCameraHardware(this))
			mCamera = getCameraInstance(id);
		else {
			return;
		}
		if (mCamera == null) {
			CustomWidget.cToastL(ctx, "打开摄像头失败，请在手机应用权限管理软件中打开权限。");
			return;
		}

		// Create our Preview view and set it as the content of our activity.
		mPreview = new CameraPreview(this, mCamera);
		preview = (FrameLayout) findViewById(R.id.camera_preview);
		preview.addView(mPreview);
		// Add a listener to the Capture button
		captureButton = (ImageButton) findViewById(R.id.button_capture);
		captureButton.setOnClickListener(this);
		button_switch = (ImageButton) findViewById(R.id.button_switch);
		button_switch.setOnClickListener(this);
		button_cancel = (ImageButton) findViewById(R.id.button_cancel);
		button_cancel.setOnClickListener(this);
		button_ok = (ImageButton) findViewById(R.id.button_ok);
		button_ok.setOnClickListener(this);
	}

	/** Check if this device has a camera */
	private boolean checkCameraHardware(Context context) {
		if (context.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA)) {
			// this device has a camera
			return true;
		} else {
			// no camera on this device
			return false;
		}
	}

	/** A safe way to get an instance of the Camera object. */
	@SuppressWarnings("deprecation")
	public Camera getCameraInstance(int id) {
		Camera c = null;
		int numCams = Camera.getNumberOfCameras();
		try {
			if (numCams > 1) {
				c = Camera.open(id);
			} else {
				c = Camera.open();
			}
			// get Camera parameters
			mParams = c.getParameters();
			Camera.Parameters params = c.getParameters();
			List<String> focusModes = params.getSupportedFocusModes();
			if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
				// Autofocus mode is supported
				mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
			}
			// set Camera parameters
			List<Size> pic = params.getSupportedPictureSizes();
			Size size = pic.get(0);
			for (int i = 0; i < pic.size(); i++) {
				if (pic.get(i).width > size.width)
					size = pic.get(i);
			}
			mParams.setPictureSize(size.width, size.height);
			List<Size> preViewSizes = params.getSupportedPreviewSizes();
			Size size2 = preViewSizes.get(0);
			for (int i = 0; i < preViewSizes.size(); i++) {
				if ((preViewSizes.get(i).width > size2.width)
						&& (preViewSizes.get(i).width <= DisplayUtil
								.getHeight(MainActivity.this)))
					size2 = preViewSizes.get(i);

			}
			mParams.setPreviewSize(size2.width, size2.height);
			mParams.setPictureFormat(PixelFormat.JPEG);
			if (id == 1) {
				mParams.setRotation(270);
			} else {
				mParams.setRotation(90);
			}
			c.setParameters(mParams);
			mParams = null;
			c.setDisplayOrientation(90);
		} catch (RuntimeException ex) {
			ex.printStackTrace();
		}
		// returns null if camera is unavailable
		return c;
	}

	private PictureCallback mPicture = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			File pictureFile = new File(
					Environment.getExternalStorageDirectory(),
					"camera_demo_temp.jpg");
			try {
				FileOutputStream fos = new FileOutputStream(pictureFile);
				fos.write(data);
				fos.close();
				path = pictureFile.getPath();
				button_cancel.setVisibility(View.VISIBLE);
				button_ok.setVisibility(View.VISIBLE);
				Log.i(TAG, "onPictureTaken - wrote bytes: " + data.length
						+ " to " + pictureFile.getAbsolutePath());
			} catch (FileNotFoundException e) {
				Log.d(TAG, "File not found: " + e.getMessage());
			} catch (IOException e) {
				Log.d(TAG, "Error accessing file: " + e.getMessage());
			}
		}

	};

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		if (mCamera == null) {
			if (checkCameraHardware(this))
				mCamera = getCameraInstance(id);
			else {
				return;
			}
			if (mCamera == null) {
				CustomWidget.cToastL(ctx, "打开摄像头失败，请在手机应用权限管理软件中打开权限。");
				return;
			}
			mCamera.startPreview();
			mPreview.setCamera(mCamera);
		}
		captureButton.setVisibility(View.VISIBLE);
		button_switch.setVisibility(View.VISIBLE);
		button_cancel.setVisibility(View.GONE);
		button_ok.setVisibility(View.GONE);
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (mCamera == null) {
			return;
		}
		releaseCamera();// release the camera immediately on pause event
	}

	private void releaseCamera() {
		if (mCamera != null) {
			mCamera.stopPreview();
			mCamera.release(); // release the camera for other applications
			mCamera = null;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.button_capture:
			mCamera.takePicture(null, null, mPicture);
			captureButton.setVisibility(View.GONE);
			button_switch.setVisibility(View.GONE);
			break;
		case R.id.button_switch:
			if (id == 1) {
				id = 0;
			} else {
				id = 1;
			}
			releaseCamera();
			preview.removeAllViews();
			mCamera = getCameraInstance(id);
			mCamera.startPreview();
			mPreview = new CameraPreview(this, mCamera);
			preview.addView(mPreview);
			break;
		case R.id.button_cancel:
			mCamera.startPreview();
			captureButton.setVisibility(View.VISIBLE);
			button_switch.setVisibility(View.VISIBLE);
			button_cancel.setVisibility(View.GONE);
			button_ok.setVisibility(View.GONE);
			break;
		case R.id.button_ok:
			// something for example upload
			break;
		default:
			break;
		}

	}
}