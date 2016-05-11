package iie.wxy;

import iie.wxy.Utils.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TrajectorySegment {
	public static final double	THRESHOLD_TYPE		= 0.51;// m/s
	public static int		THRESHOLD_DURATION	= 150;//second
	public static int		THRESHOLD_LENGTH	= 120;//m
	public static int		THRESHODL_INTERNAL 	= 2+THRESHOLD_DURATION/30;
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd,HH:mm:ss");	
	public int type;//-1待删除，0 静止， 1，运动
	public String startTime;
	public String endTime;
	public long startTimeStamp;
	public long endTimeStamp;
	public long duration;//only for static
	public double length;//only for dynamic
	public double ratio;
	public ArrayList<Location> points = new ArrayList<>();	//{latitude, longitude}
	
	public TrajectorySegment(String start, String end){
		String[] startItems = start.split(",");
		String[] endItems = end.split(",");
		if (startItems.length >= 4 && endItems.length >= 4) {

			try {
				startTime = startItems[0]+","+startItems[1];
				endTime =  endItems[0]+","+endItems[1];
						
				Date date1 = sdf.parse(startTime);
				startTimeStamp = date1.getTime();
				Date date2 = sdf.parse(endTime);
				endTimeStamp = date2.getTime();
				//2016-03-15,12:33:10,39.95181,116.235909
				Location startLocation = new Location(start);
				points.add(startLocation);
//				double startLat = Double.parseDouble(startItems[2]);
//				double startLng = Double.parseDouble(startItems[3]);
//				points.add(new double[]{startLat, startLng});
				Location endLocation = new Location(end);
				points.add(endLocation);
//				double endLat = Double.parseDouble(endItems[2]);
//				double endLng = Double.parseDouble(endItems[3]);
//				points.add(new double[]{endLat, endLng});
				length = Utils.Distance(startLocation.latitude, startLocation.longitude,
										endLocation.latitude, endLocation.longitude);
				duration = endTimeStamp - startTimeStamp;
				double speed = length*1000/duration;
				if((Math.abs(endTimeStamp - startTimeStamp) > THRESHOLD_DURATION*1000*3)){
					//发生了数据缺失
					if (length > THRESHOLD_LENGTH) {//期间发生了移动
						type = 1;
					}else {
						type = 0;
					}
					
				}else if (speed < TrajectorySegment.THRESHOLD_TYPE) {
					//static
					type = 0;
				}else {
					//dynamic
					type = 1;
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				type = -1;
			}
		}
	}
	public void changeTrajectoryType(){
		switch (type) {
		case 0://duration
			if (duration < THRESHOLD_DURATION*1000) {
				type = 1;
			}
			break;
		case 1://length
			if (length < THRESHOLD_LENGTH) {
				type = 0;
			}
			break;
		default:
			break;
		}
	}
	public int checkTarjectoryThreshold(int t){
		if(this.type != t){
			return 0;
		}
		duration = endTimeStamp - startTimeStamp;
		length = Utils.Distance(points.get(0).latitude, points.get(0).longitude,
				points.get(points.size()-1).latitude, points.get(points.size()-1).longitude);
		switch (type) {
		case 0://duration
			if (duration < THRESHOLD_DURATION*1000) {
				//type = 1;
				return 1;
			}
			break;
		case 1://length
			if (length < THRESHOLD_LENGTH) {
				//type = 0;
				return 1;
			}
			break;
		default:
			break;
		}
		return 0;
	}
	
	//-1 error。0 different type，1 merge successful
	public int mergeTarjectory(TrajectorySegment traj){
		if (type < 0 || traj.type < 0) {
			System.out.println("type < 0");
			return -1;
		}
		try {
			if (this.startTimeStamp < traj.startTimeStamp) {
				Date date2 = sdf.parse(traj.endTime);
				endTimeStamp = date2.getTime();
				this.endTime = traj.endTime;
				this.points.addAll(traj.points);
			}else if (this.startTimeStamp == traj.startTimeStamp) {
				return 1;
			}else {
				Date date2 = sdf.parse(traj.startTime);
				startTimeStamp = date2.getTime();
				this.startTime = traj.startTime;
				this.points.addAll(0, traj.points);
			}
			duration = endTimeStamp - startTimeStamp;
			length = Utils.Distance(points.get(0).latitude, points.get(0).longitude,
					points.get(points.size()-1).latitude, points.get(points.size()-1).longitude);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
		return 1;
	}
	
	public String getName() {
		return startTime+"->"+endTime;
	}
	
	public double[] getCoverageRect(){
		double[] rect = new double[4];
        Double maxlat=0.0;
        Double maxlng=0.0;
        Double minlat=Double.MAX_VALUE;
        Double minlng=Double.MAX_VALUE;
        for (Location d : points) {
			double lat = d.latitude;
			double lng = d.longitude;
    		if (lat < minlat) {
    			minlat = lat;
			}
    		if (lat > maxlat) {
				maxlat = lat;
			}
    		if (lng < minlng) {
    			minlng = lng;
			}
    		if (lng > maxlng) {
				maxlng = lng;
			}
		}
		rect[0] = maxlat;rect[1] = maxlng;
		rect[2] = minlat;rect[3] = minlng;
		return rect;
	}
	
	public String toString(){
		if (type < 0) {
			System.out.println("type < 0");
			return null;
		}
		String out = startTime+","+points.get(0).latitude+","+points.get(0).longitude+","+
					endTime+","+points.get(points.size()-1).latitude+","+points.get(points.size()-1).longitude+","+
					type+","+(duration/1000)+","+length+"\n";
		return out;
	}
	
	public boolean checkTemporalOrder(){
		return false;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
