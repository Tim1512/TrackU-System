package iie.wxy.wifilocrecoder;

import iie.wxy.AccessPointPool;
import iie.wxy.FileUtils;
import iie.wxy.FileUtils.FileScanCB;
import iie.wxy.Location;
import iie.wxy.SystemUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.Test;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;

import android.R.integer;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Instrumentation;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * @author wxy
 *
 */
public class WifiRecoder extends Service {
	public static final String ACTION = "iie.wxy.wifilocrecoder.WifiRecoder";
	private final int NOTIFICATION_ID = 9527;
	private LocationService locationService = null;
	private Activitybinder mBinder = new Activitybinder();
	private AccessPointPool pool = new AccessPointPool();
	private WatchDog dog;
	private WorkThread workThread = new WorkThread();
	private WifiManager wifiManager = null;// 获得系统wifi服务
//	private CachedScanResults cachedResults = null;
	

	@Override
	public void onCreate() {
		super.onCreate();
		wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		dog = new WatchDog(this.getApplicationContext());
		registerLocalReceivers();
		createNotification();
		Log.d("wxy", "WiFi Recoder onCreate");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("wxy", "WiFi Recoder onStartCommand(" + startId + ")");
		if (intent == null) {
			initWatchDog(getString(R.string.DEFAULT_DogName));
		} else {
			initWatchDog(intent.getStringExtra(getString(R.string.INTENT_Key_Name)));
		}

		if (locationService != null && locationService.isStarted()) {
			Log.d("wxy", "WiFi Recoder Service has started!");
		} else {
//			先开启位置处理线程
			workThread.start();
//			开启定位服务
			locationService = new LocationService(getApplicationContext());
			locationService.registerListener(mListener);
			locationService.setLocationOption(locationService
					.getDefaultLocationClientOption());
			locationService.start();
			Log.d("wxy", "WiFi Recoder Service just starts");
		}
		printCurrentThread();
		return START_STICKY;
	}
	
	private void initWatchDog(String nameString) {
		String name = SystemUtils.readConfig_UserName(this
				.getApplicationContext());
		if (nameString != null) {
			// 更新名字
			if (!name.equals(nameString)) {
				SystemUtils.saveConfig_UserName(this.getApplicationContext(),
						nameString);
				name = nameString;
			}
		}
		dog.setName(name);
		dog.setID(SystemUtils.getAndroidID(this.getApplicationContext()));
	}
	
	private void registerLocalReceivers(){
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_TIME_TICK);
		registerReceiver(receiver, filter);
	}

	@Override
	public void onDestroy() {
		// 先注销时钟周期
		unregisterReceiver(receiver);
		// 在注销百度服务
		locationService.stop();
		locationService.unregisterListener(mListener);
		locationService = null;
		Log.d("wxy", "WiFi Recoder onDestory");
		stopForeground(true);
		workThread.interrupt();
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	private final BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Intent.ACTION_TIME_TICK)) {
				occupyTopScreen(false);
				
				int ret = locationService.requestLocation();
				Log.d("wxy", "get a broadcastReceiver !" + " request result = "
						+ ret);
			}
		}
		
	};

	private BDLocationListener mListener = new BDLocationListener() {
		@Override
		public void onReceiveLocation(BDLocation location) {
			printCurrentThread();
			workThread.appendLocation(location);
			workThread.mWorkHandler.sendEmptyMessage(1);
		}
	};
	
	private class WorkThread extends Thread{
		private Object lockObject = new Object();
		private ArrayList<BDLocation> list = new ArrayList<>();
		public Handler mWorkHandler;
		public WorkThread(){
		}
		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			Looper.prepare();
			mWorkHandler = new Handler(){
				public void handleMessage(Message msg)
				{
					printCurrentThread();
					BDLocation location = null;
					synchronized (lockObject) {
						location = list.get(list.size()-1);
						list.clear();
					}
					if (location != null) {
						recordLocation(location);
					}
				}
            };
            Looper.loop();
		}
		
		public void appendLocation(BDLocation location){
			synchronized (lockObject) {
				list.add(location);
			}
		}
	}
	private void occupyTopScreen(boolean isForce){
		if (isSDKlower23() && !isForce) {
			return;
		}
		Context context = getApplicationContext();
		try {
            Intent intent = new Intent(context, LongLiveTheKing.class);//singleInstance
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);// 
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e("wxy", "e:", e);
        }
	}
	
	private void releaseTopScreen(boolean isForce){
		if (isSDKlower23() && !isForce) {
			return;
		}
		LongLiveTheKing myKing = LongLiveTheKing.instance != null ? LongLiveTheKing.instance.get() : null;
        if (myKing != null) {
        	try {
                Instrumentation inst= new Instrumentation();
                inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
            } catch(Exception e) {
                e.printStackTrace();
            }
        	myKing.finishSelf();
        }
        
	}

	private void recordLocation(BDLocation location){
		Log.d("wxy", "in BDLocationListener");
		
		printCurrentThread();
		SimpleDateFormat sDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd,HH:mm:ss");
		String time = sDateFormat.format(new java.util.Date());
		if (null != location
				&& location.getLocType() == BDLocation.TypeNetWorkLocation) {
			StringBuffer buffer = new StringBuffer();
			buffer.append(time + "," + location.getLatitude() + ","
					+ location.getLongitude());
			JSONArray arrays = recordScanResult();
			Location loc = new Location(new String(buffer), arrays);
			loc = pool.reduceDrift(loc);
			StringBuilder builder = new StringBuilder();
			builder.append(buffer.toString());
			if (pool.gAPCounts < 1) {
				builder.append(","+-1);
				builder.append(","+-1);
				builder.append(","+0);
				builder.append(","+0);
			}else{
				builder.append(","+pool.gFator);
				builder.append(","+pool.gGamma);
				builder.append(","+pool.gWeight);
				builder.append(","+pool.gAPCounts);
			}
			
			builder.append(","+getConnectedWiFiAddress(getApplicationContext()));
			String record = builder.toString();
			Save.getInstace().writeFile(
					new String(record + "\n").getBytes());
			dog.feed(record);
		} else {
			Log.d("wxy", "LocType: "+location.getLocType()+", data:" + "+location.getLatitude()" + ","
					+ location.getLongitude());
		}
	}

	public boolean compareJSONArray(JSONArray array1, JSONArray array2) {
		if (array1 == null || array2 == null) {
			return false;
		}
		Map<String, Integer> map1 = new HashMap<>();
		Map<String, Integer> map2 = new HashMap<>();
		try {
			for (int i = 0; i < array1.length(); i++) {
				JSONObject object = array1.getJSONObject(i);
				map1.put(object.getString("BSSID"), object.getInt("Level"));
			}
			for (int i = 0; i < array2.length(); i++) {
				JSONObject object = array2.getJSONObject(i);
				map2.put(object.getString("BSSID"), object.getInt("Level"));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Iterator iter = map1.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			String key = (String) entry.getKey();
			Integer value = (int) entry.getValue();
			Integer target = map2.get(key);
			if (target == null) {
				return false;
			}
			if (target != value) {
				return false;
			}
		}
		return true;
	}
	
	private boolean isForegroundAPP(){
		String packageName = "iie.wxy.wifilocrecoder";
		return packageName.equals(getForegroundApp());
	}
	
	private String getForegroundApp() {
        ActivityManager am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
        	return tasks.get(0).topActivity.getPackageName();
		}
        return "null";
    }

	private List<ScanResult> getScanResultInSomeWay(){
		int count = 0;
		List<ScanResult> wifiList = (List<ScanResult>) wifiManager.getScanResults();
		while((count < 10 && wifiList.size() == 0)){
			//bad news, have to do something.
			count ++;
//			获取wifi记录前后占据屏幕顶
			occupyTopScreen(true);
	        wifiList = (List<ScanResult>) wifiManager.getScanResults();
	        releaseTopScreen(true);
		}
		return wifiList;
	}
	
	public JSONArray recordScanResult() {
		List<ScanResult> wifiList =  Snake.getList();//getScanResultInSomeWay();//;Snake.getList();////cachedResults.get();
		Log.d("wxy", "get wifiList len: "+wifiList.size());
		//如果cachedWiFiList为空，又或者为null，则在屏幕顶端创建activity来获得wifi list
		if (wifiList == null || wifiList.size() == 0) {// 
			wifiList = getScanResultInSomeWay();
		}
		return buildScanArray(wifiList);
	}
	
	private JSONArray buildScanArray(List<ScanResult> wifiList){
		JSONArray arrays = new JSONArray();
		JSONObject single_wifi;
		if (wifiList == null) {
			return arrays;
		}
		for (ScanResult scanResult : wifiList) {
			single_wifi = new JSONObject();
			try {
				single_wifi.put("BSSID", scanResult.BSSID);
				single_wifi.put("Level", scanResult.level);// +"_"+scanResult.frequency
			} catch (JSONException e) {
				e.printStackTrace();
			}
			arrays.put(single_wifi);
		}
		StringBuffer buffer = new StringBuffer();
		SimpleDateFormat sDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd,HH:mm:ss");
		String time = sDateFormat.format(new java.util.Date());
		buffer.append(time + ","+arrays.toString()+ "\n");
		Save.getInstace().writeSignalFile(buffer.toString());
		return arrays;
	}

	public class Activitybinder extends Binder {
		// 返回service当前状态
		boolean getStatus() {
			return locationService.isStarted();
		}
	}

	private String getConnectedWiFiAddress(Context context){
		if (context == null) {
            throw new NullPointerException("getConnectedWiFiAddress context is null");
        }
        WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifiMgr.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
            ConnectivityManager connManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifiInfo = connManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (wifiInfo.isConnected()) {
				return wifiMgr.getConnectionInfo().getBSSID();
			}
        }
		return "UnConnected";
	}
	
	boolean isCoonWifiNetwork() {
		boolean flag = false;
		ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if (manager.getActiveNetworkInfo() != null) {
			if (manager.getActiveNetworkInfo().isAvailable()) {
				State wifi = manager.getNetworkInfo(
						ConnectivityManager.TYPE_WIFI).getState();
				if (wifi == State.CONNECTED || wifi == State.CONNECTING) {
					flag = true;
				}
			}
		}
		return flag;
	}

	/**
	 * 从系统中获取WiFi扫描的状态，并将结果记录到文件中。
	 */
	public void getWifiInfo() {
		WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);// 获得系统wifi服务
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		if (wifiInfo == null) {
			return;
		}
		String mac = wifiInfo.getMacAddress();
		if (mac == null) {
			return;
		}
		String labelString = wifiInfo.getSSID();
		if (labelString == null) {
			return;
		}
		StringBuffer buffer = new StringBuffer();
		SimpleDateFormat sDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd,HH:mm:ss");
		String time = sDateFormat.format(new java.util.Date());
		buffer.append(time + "," + mac + "," + labelString);
		buffer.append("\r\n");

//		 Save.getInstace().writeFile(Save.getInstace().wifiFile,
//		 (new String(buffer)).getBytes());
	}

	public static boolean isServiceRunning(Context mContext, String className) {
		ActivityManager manager = (ActivityManager) mContext
				.getSystemService(ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if (className.equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	private void createNotification() {
		Intent notificationIntent = new Intent(this, MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, 0);
		Notification notification;
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
			notification = new NotificationCompat.Builder(this)
					.setSmallIcon(R.drawable.ic_tracku)
					.setTicker(getString(R.string.NOTIFICATION_Ticker))
					.setContentTitle(
							getString(R.string.NOTIFICATION_ContentTitle))
					.setContentText(
							getString(R.string.NOTIFICATION_ContentText))
					.setContentIntent(pendingIntent)
					.setPriority(Notification.PRIORITY_MAX).build();
		} else {
			notification = new Notification.Builder(this)
					.setSmallIcon(R.drawable.ic_tracku)
					.setTicker(getString(R.string.NOTIFICATION_Ticker))
					.setContentTitle(
							getString(R.string.NOTIFICATION_ContentTitle))
					.setContentText(
							getString(R.string.NOTIFICATION_ContentText))
					.setContentIntent(pendingIntent)
					.setPriority(Notification.PRIORITY_MAX).build();
		}
		notification.flags = Notification.FLAG_NO_CLEAR;
		startForeground(NOTIFICATION_ID, notification);
	}

	private void printCurrentThread() {
//		 Log.d("wxy","Process： "+android.os.Process.myPid()+" Thread: "+android.os.Process.myTid()+" name "+Thread.currentThread().getName());
	}
	
	/**
	 * 判断framework版本是否小于23，如果小于23则不启动longlivetheking
	 * 目前先测试通过，收集数据时直接返回true，不启动透明activity
	 * @return
	 */
	private boolean isSDKlower23(){
		if(Build.VERSION.SDK_INT < 23){  
			Log.d("wxy", "SDK < 23");
			return true;  
		}
		return false;
	}
	 
	 /**
	 * @author wxy
	 *	缓存扫描到的结果，由于main线程会设置cache，工作线程会获取线程
		这是个生产者消费者模型
	 *	为了防止空指针，将读写操作互斥。
	 *	暂时未使用
	 */
//	class CachedScanResults{
//		private Object cachedLock = new Object();
//		private List<ScanResult> cachedResults = null;
//		public CachedScanResults(){
//			cachedResults = new ArrayList<>();
////			cachedResults = (List<ScanResult>) wifiManager.getScanResults();
//		}
//		
//		private void set(List<ScanResult> results){
//			
//			cachedResults = results;
////			synchronized (cachedLock) {
////				cachedResults = results;
////			}
//			Log.d("wxy", "CachedScanResults set: cached size = "+cachedResults.size());
//		}
//		
//		private List<ScanResult> get(){
//			Log.d("wxy", "CachedScanResults get: cached size = "+cachedResults.size());
//			List<ScanResult> results = null;
//			results = cachedResults;
////			synchronized (cachedLock) {
////				results = cachedResults;
////			}
//			return results;
//		}
//	 }
}
