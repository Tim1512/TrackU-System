package iie.wxy.Utils;

import iie.wxy.Location;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Utils {
	static public double log(double value, double base) {
		return Math.log(value) / Math.log(base);
	}
	public static boolean compareJSONArray(JSONArray array1, JSONArray array2){
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
//	double ratio = checkRectOverlap(new double[]{4.5,4.5,3.5,3.5}, new double[]{4,4,3,3});
//	maxLat+","+maxlng+","+minLat+","+minlng
	public static double checkRectOverlap(double[] rect1, double[] rect2){
		double lat1 = rect1[0]-rect1[2];//Distance(rect1[1], rect1[0], rect1[1], rect1[2]);
		double lng1 = rect1[1]-rect1[3];//Distance(rect1[1], rect1[0], rect1[3], rect1[0]);
		double lat2 = rect2[0]-rect2[2];//Distance(rect2[1], rect2[0], rect2[1], rect2[2]);
		double lng2 = rect2[1]-rect2[3];//Distance(rect2[1], rect2[0], rect2[3], rect2[0]);
		double ratio=0;
		
		double startlat=Math.min(rect1[2], rect2[2]);
		double endlat = Math.max(rect1[0], rect2[0]);
		double lat = lat1 + lat2 - (endlat - startlat);
		double startlng = Math.min(rect1[3], rect2[3]);
		double endlng = Math.max(rect1[1], rect2[1]);
		double lng = lng1+ lng2 - (endlng - startlng);
		
		if (lat<=0 || lng<=0) {
			return 0;
		}else{
			double area1 = Distance(lat1,lng1, 0,  lng1) * Distance( lat1,lng1, lat1, 0);//lat1 * lng1;lat1 * lng1;//
			double area2 =Distance( lat2,lng2, 0, lng2) * Distance( lat2,lng2, lat2, 0);// lat2 * lng2;//
			double area  = Distance( lat,lng, 0, lng) * Distance( lat,lng, lat, 0);//lat * lng;//
			double ratio1= area/area1;
			double ratio2= area/area2;
			return (ratio1>ratio2? ratio1:ratio2);
			//ratio = area/(area1+area2-area);
		}
	}
	
	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd,HH:mm:ss");
	public static long getTimestamp(String date, String time){
		return getTimestamp(date+","+time);
	}
	public static long getTimestamp(String	str){
		long ret = 0;
		try {
			ret = sdf.parse(str).getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	public static String getDateString(long timeStamp){
		return sdf.format(new Date(timeStamp));
	}
	
	public static ArrayList<double[]> computeGeoBondingbox(ArrayList<Location> dataSet){
		ArrayList<double[]> boxArrayList = new ArrayList<>();
		double maxLat=0.0;
		double maxLng=0.0;
		double minLat=Double.MAX_VALUE;
		double minLng=Double.MAX_VALUE;
		double lat = 0.0;
        Double lng = 0.0;
        for (Location  data : dataSet) {
        	lat = data.latitude;
        	lng = data.longitude;
        	if (lat == 4.9E-324) {
				int xxx;
				xxx = 0;
			}
    		if (lat < minLat) {//不考虑跨越0°赤道线的问题~简单
    			minLat = lat;
    		}
    		if (lat > maxLat) {
    			maxLat = lat;
    		}
    		if (lng < minLng) {
    			minLng = lng;
    		}
    		if (lng > maxLng) {
    			maxLng = lng;
    		}
		}
        boxArrayList.add(new double[]{maxLat,maxLng});
        boxArrayList.add(new double[]{minLat,maxLng});
        boxArrayList.add(new double[]{minLat,minLng});
        boxArrayList.add(new double[]{maxLat,minLng});
        System.out.println(""+(Utils.Distance(maxLat,maxLng, maxLat,minLng)));
        System.out.println(""+(Utils.Distance(minLat,maxLng, minLat,minLng)));
		return boxArrayList;
	}
	
	/** 
	 * 计算地球上任意两点(经纬度)距离 
	 *  
	 * @param lat1 
	 *            第一点纬度 
	 * @param long1 
	 *            第一点经度 
	 * @param lat2 
	 *            第二点纬度 
	 * @param long2 
	 *            第二点经度 
	 * @return 返回距离 单位：米 
	 */ 
	public static double Distance(Location loc1, Location loc2){
		if (loc1 == null || loc2 == null) {
			return -1;
		}
		return Distance(loc1.latitude, loc1.longitude, loc2.latitude, loc2.longitude);
	}
	public static double Distance(String lat1,String long1 , String lat2, String long2){
		if(long1 == null || lat1 == null || long2 == null || lat2 ==null){
			return -1;
		}
		return Distance(Double.parseDouble(lat1), Double.parseDouble(long1), 
						Double.parseDouble(lat2), Double.parseDouble(long2));
	}
	public static double Distance(double lat1,double long1, double lat2,  double long2) {  
	    double a, b, R;  
	    R = 6378137; // 地球半径  
	    lat1 = lat1 * Math.PI / 180.0;  
	    lat2 = lat2 * Math.PI / 180.0;  
	    a = lat1 - lat2;  
	    b = (long1 - long2) * Math.PI / 180.0;  
	    double d;  
	    double sa2, sb2;  
	    sa2 = Math.sin(a / 2.0);  
	    sb2 = Math.sin(b / 2.0);  
	    d = 2   * R     * Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(lat1)  
	                    * Math.cos(lat2) * sb2 * sb2));  
	    return d;  
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
