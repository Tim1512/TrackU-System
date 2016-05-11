package iie.wxy.wifilocrecoder;

import iie.wxy.AccessPointPool;
import iie.wxy.Location;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class WifiRecoder extends Service {
	public static final String ACTION = "iie.wxy.wifilocrecoder.WifiRecoder";
	private LocationService locationService;
	private Activitybinder mBinder = new Activitybinder();
	TaskThread mTaskThread = new TaskThread();
	AccessPointPool pool = new AccessPointPool();
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		locationService = new LocationService(getApplicationContext());
		locationService.registerListener(mListener);
		locationService.setLocationOption(locationService.getDefaultLocationClientOption());
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		locationService.start();
		
		mTaskThread.start();
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		locationService.stop();
		locationService.unregisterListener(mListener);
		
	}
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return mBinder;
	}

	private final BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_TIME_TICK)) {
            	int ret = locationService.requestLocation();
//            	Log.d("wxy", "get a broadcastReceiver !"+" request result = "+ret);
            	getWifiInfo();
            }
		}
    };
	private BDLocationListener mListener = new BDLocationListener(){
		@Override
		public void onReceiveLocation(BDLocation location) {
			// TODO Auto-generated method stub
			Log.d("wxy", "in BDLocationListener");
			if (null != location && location.getLocType() != BDLocation.TypeServerError) {
				StringBuffer buffer = new StringBuffer();
				SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd,HH:mm:ss");
				String time = sDateFormat.format(new java.util.Date());
				buffer.append(time+","+location.getLatitude()+","+location.getLongitude());
				//buffer.append("\n");
				//Save.getInstace().writeFile((new String(buffer)).getBytes());
				double lat = location.getLatitude();
				double lng = location.getLongitude();
				JSONArray arrays = recordScanResult();
				Location loc = new Location(new String(buffer), arrays);
				loc= pool.reduceDrift(loc);
				Save.getInstace().writeFile(new String(loc.toString()+"\n").getBytes());
			}
		}
	};
	public boolean compareJSONArray(JSONArray array1, JSONArray array2){
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
	public JSONArray recordScanResult(){
		WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);        //获得系统wifi服务  
		List<ScanResult> wifiList = (List<ScanResult>) wifiManager.getScanResults(); 
		JSONArray arrays = new JSONArray();  
		JSONObject  single_wifi;
		for (ScanResult scanResult : wifiList) {
			single_wifi = new  JSONObject ();
			try {
				single_wifi.put("BSSID", scanResult.BSSID);
				single_wifi.put("Level", scanResult.level);//+"_"+scanResult.frequency
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			arrays.put(single_wifi);
		}
		Save.getInstace().writeFile(Save.getInstace().signalFile,  new String(arrays.toString()+"\n").getBytes());
		return arrays;
	}
  public class Activitybinder extends Binder{
//	  返回service当前状态
	  boolean getStatus(){
		  return locationService.isStarted();
	  }
  }
	boolean isCoonWifiNetwork(){
		boolean flag = false;
		ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);  
        if (manager.getActiveNetworkInfo() != null) { 
        	if (manager.getActiveNetworkInfo().isAvailable()) {
        		State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();  
                if(wifi == State.CONNECTED || wifi == State.CONNECTING){  
                	flag = true;
                } 
			}
        }  
        return flag;
	}
	public void getWifiInfo() {
		WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);//获得系统wifi服务  
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		if (wifiInfo == null) {			
			return;
		}
		String mac = wifiInfo.getMacAddress();
		if (mac == null) {
			return;
		}
		String labelString = wifiInfo.getSSID();
		if(labelString == null){
			return ;
		}
		StringBuffer buffer = new StringBuffer();
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd,HH:mm:ss");
		String time = sDateFormat.format(new java.util.Date());
		buffer.append(time+","+mac+","+labelString);
		buffer.append("\r\n");
		
		Save.getInstace().writeFile( Save.getInstace().wifiFile, (new String(buffer)).getBytes());
	}
	
    public class TaskThread extends Thread {
        @Override
        public void run() {
            System.out.print("here step into run");
            while (true){
            	locationService.requestLocation();
                try {
                    Thread.sleep(60*1000,0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
           }
        }
    };
    public static boolean isServiceRunning(Context mContext,String className) {

        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager)
        mContext.getSystemService(Context.ACTIVITY_SERVICE); 
        List<ActivityManager.RunningServiceInfo> serviceList 
                   = activityManager.getRunningServices(30);

        if (!(serviceList.size()>0)) {
            return false;
        }

        for (int i=0; i<serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className) == true) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }
}
