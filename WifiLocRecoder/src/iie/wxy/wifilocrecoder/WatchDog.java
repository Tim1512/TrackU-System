/**
 * 
 */
package iie.wxy.wifilocrecoder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import iie.wxy.Utils;
import android.R.integer;
import android.content.Context;
import android.util.Log;

/**
 * @author wxy
 * 
 */
public class WatchDog {
	private Context mContext;
	private String nameString = "";
	private String idString = "";
	private int counter = 0;
	private static int internal = 1;
	private static String BASEURL = "";
	private static String MSGKEY = "";
	/**
	 * 喂狗,网络通信在新建线程中完成
	 */
	public WatchDog(Context context) {
		mContext = context;
		BASEURL = context.getString(R.string.Network_BASEURL);
		MSGKEY = context.getString(R.string.Network_MsgKey);
		counter = 0;
		Log.d("wxy", "url: "+BASEURL+", key: "+MSGKEY);
	}

	/**
	 * 在选定的时间向服务器发送消息。 “id, name, time”
	 */
	public void feed(String record) {
		counter++;
		if(counter%internal == 0){
//			Log.d("wxy", "feed: "+getMessage());
			
//			WorkThread worker = new WorkThread();
//			worker.start();
			
			String msg = getMessage(record);
			String ret = doPost(BASEURL, msg);
			String[] strs = ret.replaceAll("\r|\n", "").split("=");
	        if (strs.length > 1 && strs[0].equals("internal")) {
	        	internal = Integer.parseInt(strs[1]);
//	        	Log.d("wxy", "watchdog internal = "+internal);
			}
			counter = 0;
		}
	}

	private String getMessage(String record) {
		StringBuilder builder = new StringBuilder();
		builder.append(idString + "," + nameString);
		builder.append(","+ Utils.getTimeStamp());
		record = record.replaceAll(",", "|");
		builder.append(", msg="+ record);
		return builder.toString();
	}

	public void setName(String name) {
		nameString = name;
//		Log.d("wxy", toString());
	}

	public void setID(String id) {
		idString = id;
//		Log.d("wxy", toString());
	}

	public String toString() {
		
		return "id: " + idString + ", name: " + nameString;
	}
	
	public String doPost(String url, String msg){
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(MSGKEY, msg));
        return NetworkUtils.doPost(url, params);
    }
	
//	private class WorkThread extends Thread{
//
//		@Override
//		public void run() {
//			String msg = getMessage();
//			doPost(BASEURL, msg);
////			Log.d("wxy", "msg in thread: "+msg);
//		}
//		
//	}
}
