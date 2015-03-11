package com.mediatek.factorymode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.f1game.tools.FileUtils;

public class OpticalActivity extends Activity implements OnClickListener,
		SensorEventListener {
	private static final String TAG = "OpticalActivity";
	static {
		try {
			Log.d(TAG, "Load engmodelJNI ");
			System.loadLibrary("engmodjni");
		} catch (UnsatisfiedLinkError ule) {
			Log.d(TAG, "Load engmodelJNI failed");
		}
	}
	private File fl;

	private native static int getCdata();

	private RelativeLayout mRlout = null;

	private TextView mLightLevelTv = null;
	private TextView mCdataText = null;

	private SensorManager mSensorManager = null;
	private Sensor mSensor = null;

	private boolean isteskok = false;
	private CountDownTimer mtimer = null;

	private float mLastLightLevel = 0;
	private boolean mIsFirstTime = true;
	private boolean timer_finish = true;
	private boolean isScrPause = false;
	ArrayList<String> data = new ArrayList<String>();
	int index = 0;
	private Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {
			int what = msg.what;
			Log.i(TAG, "msg.what = " + msg.what);
			switch (what) {
			case 1:
				String ads = "" + getCdata();
				//mCdataText
				//		.setText(getString(R.string.optical_environment_light_data)
				//				+ ads);
				index++;
				data.add("time: " + SystemClock.elapsedRealtime() + " -- data:"
						+ ads + "\n");
				if (index % 100 == 0) {
					index = 0;
					for (String tmp : data) {
						FileUtils.writestr(fl.getPath(), tmp);
					}
					data.clear();

				}
				if (isScrPause == false) {
					mHandler.sendEmptyMessageDelayed(1, 1L);
				} else {
					mHandler.removeMessages(1);
				}
				break;
			default:
				break;
			}

		};
	};

	public File getExternalFilesDirEx(String dir) {
		File fl = this.getExternalFilesDir(dir);
		if (!fl.exists() || fl.mkdirs())
			;
		return fl;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		setContentView(R.layout.optical);
		fl = new File(
				this.getExternalFilesDirEx(Environment.DIRECTORY_PICTURES),
				"als_ps.txt");

		// 存下文件内容，为下次读取使用
		FileUtils.deleteFile(fl);
		((TextView) findViewById(R.id.path)).setText(fl.getPath());
		mRlout = (RelativeLayout) findViewById(R.id.base_layout);

		mLightLevelTv = (TextView) findViewById(R.id.light_level);
		mCdataText = (TextView) findViewById(R.id.light_level_cdata);

		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

		if (mSensor == null) {
			mCdataText.setText("");
		}
		new Thread(new Runnable() {
			public void run() {
				while (true) {
					// try {
					// //Thread.sleep(1);
					// } catch (InterruptedException e) {
					// // TODO Auto-generated catch block
					// e.printStackTrace();
					// }
					String ads = "" + getCdata();
				//	mCdataText
					//		.setText(getString(R.string.optical_environment_light_data)
					//				+ ads);
					index++;
					data.add("time: " + SystemClock.elapsedRealtime()
							+ " -- data:" + ads + "\n");
					if (index % 100 == 0) {
						index = 0;
						for (String tmp : data) {
							FileUtils.writestr(fl.getPath(), tmp);
						}
						data.clear();

					}
				}
			}
		}).start();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		mSensorManager.unregisterListener(this);
		isScrPause = true;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		mSensorManager.registerListener(this, mSensor,
				SensorManager.SENSOR_DELAY_UI);
		isScrPause = false;
		// mHandler.sendEmptyMessageDelayed(1, 1L);

	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		float lightLevel = 0;
		boolean isChanged = false;
		lightLevel = event.values[0];

		if (mIsFirstTime) {
			mLastLightLevel = lightLevel;
			mIsFirstTime = false;
		}

		Log.d(TAG, "lightLevel is " + lightLevel + " last is "
				+ mLastLightLevel);

		float changedValue = mLastLightLevel - lightLevel;
		if (Math.abs(changedValue) > 0.000001) {
			mLastLightLevel = lightLevel;
			isChanged = true;
		}

		Float tmp = new Float(lightLevel);
		mLightLevelTv.setText(tmp.toString());

		int value = tmp.intValue();
		mRlout.setBackgroundColor(0XFF << 24 | value << 8);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if (mtimer != null) {
			mtimer.cancel();
		}
		super.onDestroy();
	}

	private void TimerFinished() {

	}
}
