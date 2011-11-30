/**
 * 
 */
package com.glory.droid.applocker.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.glory.droid.applocker.PwdUI;
import com.glory.droid.applocker.UnlockActivity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * @author gy.zeng
 * 
 */
public class AppLockerService extends Service {

	private ExecutorService executorService;
	private Process monitorProcess;
	private Process cleanProcess;
	private ActivityManager activityManager = null;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		executorService = Executors.newSingleThreadExecutor();
		// AppMonitor monitor = new AppMonitor();
		// executorService.submit(monitor);
		activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		LockerThread thread = new LockerThread();
		executorService.submit(thread);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (executorService != null) {
			executorService.shutdown();
		}
		if (monitorProcess != null) {
			monitorProcess.destroy();
		}
		if (cleanProcess != null) {
			cleanProcess.destroy();
		}
	}

	class LockerThread implements Runnable {

		Intent pwdIntent = null;

		public LockerThread() {
			pwdIntent = new Intent(AppLockerService.this, PwdUI.class);
			pwdIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		}

		@Override
		public void run() {
			while (true) {
				Log.i("lock", "lockerThread run.");
				String packname = activityManager.getRunningTasks(1).get(0).topActivity.getPackageName();
				if ("com.privacysecure".equals(packname)) {
					startActivity(pwdIntent);
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	class AppMonitor implements Runnable {

		@Override
		public void run() {
			List<String> monitorCommandList = getMonitorCMD();
			List<String> cleanCommandList = getCleanCMD();
			try {
				String[] cleanCommand = cleanCommandList.toArray(new String[cleanCommandList.size()]);
				cleanProcess = Runtime.getRuntime().exec(cleanCommand);
				cleanProcess.waitFor();
				String[] monitorCommand = monitorCommandList.toArray(new String[monitorCommandList.size()]);
				monitorProcess = Runtime.getRuntime().exec(monitorCommand);
				InputStream inputStream = monitorProcess.getInputStream();
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				String line = null;
				while ((line = bufferedReader.readLine()) != null) {
					String myPackageName = AppLockerService.this.getPackageName();
					ActivityManager activityManager = (ActivityManager) AppLockerService.this.getSystemService(Context.ACTIVITY_SERVICE);
					String packageName = activityManager.getRunningTasks(1).get(0).topActivity.getPackageName();
					if (myPackageName.equals(packageName)) {
						continue;
					}
					Log.v("app", line);
					if (line.contains("com.dolphin.browser")) {
						Intent authIntent = new Intent(AppLockerService.this, UnlockActivity.class);
						authIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						AppLockerService.this.startActivity(authIntent);
					}
				}
			} catch (Exception e) {
				Log.e("app", "error", e);
			}
		}
	}

	private List<String> getCleanCMD() {
		List<String> commandList = new ArrayList<String>();
		commandList.add("logcat");
		commandList.add("-c");
		return commandList;
	}

	private List<String> getMonitorCMD() {
		List<String> commandList = new ArrayList<String>();
		commandList.add("logcat");
		commandList.add("ActivityManager:I");
		commandList.add("*:S");
		return commandList;
	}
}
