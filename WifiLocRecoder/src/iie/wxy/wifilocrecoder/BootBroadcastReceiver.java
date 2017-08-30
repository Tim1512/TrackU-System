/**
 * 
 */
package iie.wxy.wifilocrecoder;

import iie.wxy.SystemUtils;
import android.R.bool;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * @author wxy
 *
 */
public class BootBroadcastReceiver extends BroadcastReceiver {
	private static final String ACTION_BOOT = "android.intent.action.BOOT_COMPLETED";
	private static final String ACTION_SHUTDOWN = "android.intent.action.ACTION_SHUTDOWN";
	
	@Override
	public void onReceive(Context context, Intent intent) {	 
		if (intent.getAction().equals(ACTION_BOOT)) { 
			boolean isRunning = SystemUtils.readConfig_IsRunning(context);
			Log.d("wxy", "BootBroadcastReceiver read isServiceRunning = "+isRunning);
			if (isRunning) {
				Intent startIntent = new Intent(context, WifiRecoder.class);
				context.startService(startIntent);
			}
		}
		
		if (intent.getAction().equals(ACTION_SHUTDOWN)) {
			boolean isServiceRunning = WifiRecoder.isServiceRunning(context, "iie.wxy.wifilocrecoder.WifiRecoder");
			Log.d("wxy", "BootBroadcastReceiver save isServiceRunning = "+isServiceRunning);
			SystemUtils.saveConfig_IsRunning(context, isServiceRunning);
			if (isServiceRunning) {
				Intent stopIntent = new Intent(context, WifiRecoder.class);
				context.stopService(stopIntent);
			}
		}
	}

}
