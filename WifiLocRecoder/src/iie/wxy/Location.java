package iie.wxy;

import java.util.ArrayList;
import java.util.Collections;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Location {
	
	public long timestamp;
	public double latitude;
	public double longitude;
	public String date;
	public String time;
	public String strLatitude;
	public String strLongitude;
	public String strSpeed;
	public int strMode;
	public JSONArray apArray = null;
	
	public double tempDistance;
	public double tempSignal;

	public Location(long t, double lat, double lng){
		timestamp = t;
		latitude = lat;
		longitude = lng;
		
		String[] items = Utils.getDateString(timestamp).split(",");
		date = items[0];
		time = items[1];
		strLatitude = ""+latitude;
		strLongitude =""+longitude;
	}
	/**
	 * @param loc
	 */
	public Location(Location loc) {
		// TODO Auto-generated constructor stub
		timestamp = loc.timestamp;
		latitude = loc.latitude;
		longitude = loc.longitude;
		
		String[] items = Utils.getDateString(timestamp).split(",");
		date = items[0];
		time = items[1];
		strLatitude = ""+latitude;
		strLongitude =""+longitude;
	}

	//1970-03-25,09:12:00,39.951765,116.236016
	public Location(String line){
		String[] items = line.split(",");
		date = items[0];
		time = items[1];
		strLatitude = items[2];
		strLongitude = items[3];
		
		timestamp = Utils.getTimestamp(date, time);
		latitude = Double.parseDouble(strLatitude);
		longitude = Double.parseDouble(strLongitude);
	}
	
	public Location(String line, JSONArray array){
		String[] items = line.split(",");
		date = items[0];
		time = items[1];
		strLatitude = items[2];
		strLongitude = items[3];
		if (array.length() > 4) {
			try {
				ArrayList<Integer> list = new ArrayList<>();
				for (int i=0; i < array.length(); i++) {
					JSONObject object;
					object = array.getJSONObject(i);
					list.add(object.getInt("Level"));
				}
				Collections.sort(list);//list中信号由小到大排列
				apArray = new JSONArray();
				int flag = list.get((int) (list.size()*0.25));//
				for (int i=0; i < array.length(); i++) {
					JSONObject object = array.getJSONObject(i);
					int temp = object.getInt("Level");
					if (temp > flag) {
						apArray.put(object);
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else {
			apArray = array;
		}
		timestamp = Utils.getTimestamp(date, time);
		latitude = Double.parseDouble(strLatitude);
		longitude = Double.parseDouble(strLongitude);
	}

	public void updateLatlng(double lat, double lng){
		latitude = lat;
		longitude = lng;
		strLatitude = ""+latitude;
		strLongitude =""+longitude;
	}
	
	public String toString(){
		return date+","+time+","+strLatitude+","+strLongitude;
	}

}
