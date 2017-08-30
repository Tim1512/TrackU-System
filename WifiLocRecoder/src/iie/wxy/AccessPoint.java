package iie.wxy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.R.string;

public class AccessPoint {

	private String BSSID;
	private double avgSignalStrength;
	private double stdSignalStrength;
	private ArrayList<Integer> signaList;
	private double FPR;
	public  int		pheromone;
	private  int	RSSRange;
	private int maxSignal;
	private int minSignal;
	
	public AccessPoint(String bssid) {
		BSSID = bssid;
		signaList = new ArrayList<>();
		pheromone = 10;
		RSSRange = 0;
	}
	
	public AccessPoint(String bssid, int level) {
		BSSID = bssid;
		signaList = new ArrayList<>();
		addSamplingSignalStrength(level);
		pheromone = 10;
		RSSRange = 0;
	}
	
	public String getBSSID(){
		return BSSID;
	}
	public double getAvgSignalStrength(){
		computeStatistical();
		return avgSignalStrength;
	}
	public double getStdSignalStrength(){
		computeStatistical();
		return stdSignalStrength;
	}
	public int getRSSRange() {
		computeStatistical();
		return RSSRange;
	}
	public int getMaxSignal() {
		computeStatistical();
		return maxSignal;
	}
	public int getMinSignal() {
		computeStatistical();
		return minSignal;
	}
	public double getFDR(){
		computeStatistical();
		return FPR;
	}
	
	public String toString(){
		computeStatistical();
		return BSSID+":times="+signaList.size()+",avg="+
				avgSignalStrength+",std="+stdSignalStrength+",FPR="+FPR;
	}
	
	private void computeStatistical(){
		if (avgSignalStrength != -1 && stdSignalStrength != -1 && RSSRange != -1) {
			return;
		}
		int max=Integer.MIN_VALUE, min= 0;
		avgSignalStrength = 0;
		for (Integer signal : signaList) {
			avgSignalStrength+=signal;
			if (signal > max) {
				max = signal;
			}
			if (signal < min) {
				min = signal;
			}
		}
		maxSignal = max;
		minSignal = min;
		RSSRange = Math.abs(max - min);
		avgSignalStrength = avgSignalStrength/signaList.size();
		stdSignalStrength = 0;
		for(Integer value:signaList){
			stdSignalStrength+=(value-avgSignalStrength)*(value-avgSignalStrength); 
		}
		stdSignalStrength = (stdSignalStrength/(signaList.size()));//Math.sqrt		
		FPR = signaList.size()*(100+avgSignalStrength)/(stdSignalStrength+1);//+1
	}
	
	public void addSamplingSignalStrength(int ss){
		signaList.add(ss);
		stdSignalStrength	= -1;
		avgSignalStrength	= -1;
		RSSRange			= -1;
	}

	
	
//	按照每行一个json的格式读取，json中存放若干个ap的采样值
//	public static Map<String, AccessPoint> readSignalFile(String file){
//		String line;
//		Map<String, AccessPoint> hashMap = new HashMap<String, AccessPoint>(); 
//		try {
//			FileInputStream fis = new FileInputStream(new File(file));
//			BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
//			while((line=reader.readLine()) != null){
//				if (line.length() > 0) {
//					JSONArray array = new JSONArray(line);
//					for (int i = 0; i < array.length(); i++) {
//					   JSONObject obj = array.getJSONObject(i);
//					   String bssidString = obj.getString("BSSID");
//					   int	level = obj.getInt("Level");
//					   AccessPoint ap = null;
//					   if ((ap=hashMap.get(bssidString)) != null) {
//						   ap.addSamplingSignalStrength(level);
//					   }else {
//						   hashMap.put(bssidString, new AccessPoint(bssidString, level));
//					   }
//					}
//				}
//			}
//			
//			fis.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return null;
//		}
//		return hashMap;
//	}
}

