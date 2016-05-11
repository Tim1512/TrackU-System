package iie.wxy.wifilocrecoder;


import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import android.content.Context;
import android.util.Log;

/**
 * 
 * @author baidu
 *
 */
public class LocationService {
	private LocationClient client = null;
	private LocationClientOption mOption,DIYoption;
	private Object  objLock = new Object();

	/***
	 * 
	 * @param locationContext
	 */
	public LocationService(Context locationContext){
		synchronized (objLock) {
			if(client == null){
				client = new LocationClient(locationContext);
				client.setLocOption(getDefaultLocationClientOption());
			}
		}
	}
	
	/***
	 * 
	 * @param listener
	 * @return
	 */
	
	public boolean registerListener(BDLocationListener listener){
		boolean isSuccess = false;
		if(listener != null){
			client.registerLocationListener(listener);
			isSuccess = true;
		}
		return  isSuccess;
	}
	
	public void unregisterListener(BDLocationListener listener){
		if(listener != null){
			client.unRegisterLocationListener(listener);
		}
	}
	
	/***
	 * 
	 * @param option
	 * @return isSuccessSetOption
	 */
	public boolean setLocationOption(LocationClientOption option){
		boolean isSuccess = false;
		if(option != null){
			if(client.isStarted())
				client.stop();
			DIYoption = option;
			client.setLocOption(option);
		}
		return isSuccess;
	}
	
	public LocationClientOption getOption(){
		return DIYoption;
	}
	/***
	 * 
	 * @return DefaultLocationClientOption
	 */
	public LocationClientOption getDefaultLocationClientOption(){
		if(mOption == null){
			mOption = new LocationClientOption();
			mOption.setLocationMode(LocationMode.Hight_Accuracy);
			mOption.setCoorType("wgs84");
			mOption.setOpenGps(false);
			mOption.setPriority(LocationClientOption.NetWorkFirst);//GPS”≈œ»
			mOption.disableCache(true);
			mOption.setScanSpan(0);
		    mOption.setLocationNotify(false);
		    mOption.setIgnoreKillProcess(true);
		    mOption.SetIgnoreCacheException(true);
		}
		return mOption;
	}
	
	public void start(){
		synchronized (objLock) {
			if(client != null && !client.isStarted()){
				client.start();
			}
		}
	}
	public void stop(){
		synchronized (objLock) {
			if(client != null && client.isStarted()){
				client.stop();
			}
		}
	}
	public int requestLocation(){
		synchronized (objLock) {
			if(client != null && client.isStarted()){
				return client.requestLocation();
			}
		}
		return -1;
	}
	
	public boolean isStarted(){
		synchronized (objLock) 	{
			if (client != null) {
				return client.isStarted();
			}else {
				return false;
			}
		}
	}
	
}
