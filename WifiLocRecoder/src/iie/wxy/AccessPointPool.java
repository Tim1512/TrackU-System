package iie.wxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AccessPointPool {

	Map<String, AccessPoint> APList;
	Location lastLocation = null;
	public double[] center;
	
	public double[] tempRealPostion = new double[2];
	
	Queue<Location> locationQ = new LinkedList<Location>();
	public AccessPointPool(){
		
		APList = new HashMap<String, AccessPoint>();
	}

	public void addAP(AccessPoint ap){
		APList.put(ap.getBSSID(), ap);
	}

	/**
	* 
	* @title: update 
	* @description: 更新AP池中ap的状态，如果一个ap长时间没有被扫描到，则从池中删除. 如果没有记录某个ap，记录
	* @param array 格式如：[{"BSSID":"26:4b:f5:25:e8:bf","Level":-46}]
	* @return void
	* @throws
	*/
	public void update(Location loc){
		JSONArray array = loc.apArray;
		if (array == null) {
			return ;
		}
		try {
			for (int i = 0; i < array.length(); i++) {
				   JSONObject obj;

				   obj = array.getJSONObject(i);
				   String bssidString = obj.getString("BSSID");
				   int	level = obj.getInt("Level");
				   AccessPoint ap = null;
				   if ((ap=APList.get(bssidString)) != null) {
					   ap.addSamplingSignalStrength(level);
				   }
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}
	/**
	 * 
	* @title: eliminateAccessPoint 
	* @description: 淘汰时间久未检测到的ap
	* @throws
	 */
	public void eliminateAccessPoint(Location loc) {
		JSONArray array = loc.apArray;
		if (array == null) {
			return ;
		}
		try {
			for (int i = 0; i < array.length(); i++) {
				   JSONObject obj;
				   obj = array.getJSONObject(i);
				   String bssidString = obj.getString("BSSID");
				   int	level = obj.getInt("Level");
				   AccessPoint ap = null;
				   if ((ap=APList.get(bssidString)) != null) {
					   ap.pheromone += (100+level);//根据信号强度列出
				   }else {
					   APList.put(bssidString, new AccessPoint(bssidString, level));
				   }
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		eliminate old ap
		Iterator iter = APList.entrySet().iterator();
		while(iter.hasNext()){
			Map.Entry entry = (Map.Entry) iter.next();
			Object key = entry.getKey();
			AccessPoint ap = (AccessPoint) entry.getValue();
			ap.pheromone -= 10;
			if (ap.pheromone < 0) {
				iter.remove();
			}
		}
	}
	
	public double[] getQueueCentre() {
		double[] ret = new double[2];ret[0]=0;ret[1]=0;
		for (Location loc : locationQ) {
			ret[0]+=loc.latitude;
			ret[1]+=loc.longitude;
		}
		ret[0] = ret[0]/locationQ.size();
		ret[1] = ret[1]/locationQ.size();
		return ret;
	}
	
	public void appendLocationQ(Location loc) {
		if (locationQ.size() > 90) {
			locationQ.poll();
		}
		Location location = new Location(loc);
		locationQ.offer(location);
	}
	
	public double getFadingFactor(Location loc) {
		if (lastLocation == null || loc.apArray.length() == 0) {
			return 1;
		}
		int apCount = 0;
		double drift = 0;
		double fluctuation = 0;
		double rssRange = 0;
		Double countWieight = 0.0;

		try {
			for (int i = 0; i < loc.apArray.length(); i++) {
				fluctuation = 0;
				rssRange = 0;
				JSONObject jsonObject;
				jsonObject = loc.apArray.getJSONObject(i);
				AccessPoint ap = APList.get(jsonObject.getString("BSSID"));
				if (ap == null) {
					continue;
				}
				Double avgLevel = ap.getAvgSignalStrength();
				if (avgLevel != null) {
					apCount++;
					countWieight += ap.getFDR();
					fluctuation = Math.abs((double)jsonObject.getInt("Level") - avgLevel);//
					if (fluctuation > 0) {
						rssRange = (int) (ap.getMaxSignal() - ap.getAvgSignalStrength());
					}else {
						rssRange = (int) (ap.getAvgSignalStrength() - ap.getMinSignal());
					}
					drift += ((Math.abs(fluctuation)/(1+rssRange))*ap.getFDR());
				}
				
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (countWieight == 0) {
			return 0;
		}else {
			double apFactor = (double)apCount/loc.apArray.length();//countAPFactor+=apFactor;
			drift = apFactor*(drift/countWieight);
			return drift;
		}
	}
	/**
	 * 
	* @title: reduceDrift 
	* @description: 减少点的漂移
	* @param loc
	* @return Location 返回经过修正的位置
	* @throws
	 */
	public Location reduceDrift(Location loc) {
		update(loc);
		
		if (lastLocation != null) {
			double parameterP = 1-getFadingFactor(loc);//countDrift+=parameterP;countc++;
			double lat = 0,lng = 0;
			double[] tempCenter = getQueueCentre();
			lat= loc.latitude - parameterP*(loc.latitude - tempCenter[0]);
			lng = loc.longitude - parameterP*(loc.longitude - tempCenter[1]);
			double tempD = Utils.Distance(lat, lng, tempCenter[0], tempCenter[1]);
			loc.updateLatlng(lat, lng);				
		}
		eliminateAccessPoint(loc);
		appendLocationQ(loc);
		lastLocation = loc;
		return loc;
	}
	
	
//	public static double countc = 0;
//	public static double countDrift = 0;
//	static double countAPFactor = 0;
//	static ArrayList<String> locs = new ArrayList<>();
//	static ArrayList<String> signals = new ArrayList<>();	
	

//	/**
//	 * @param args
//	 */
//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//		FileUtils.readEachLine("LocationWifi.txt", new FileUtils.ReadLineCB() {
//			
//			@Override
//			public void readLineCallBack(String line) {
//				// TODO Auto-generated method stub
//				locs.add(line);
//			}
//		});
//		FileUtils.readEachLine("signal.signal", new FileUtils.ReadLineCB() {
//			
//			@Override
//			public void readLineCallBack(String line) {
//				// TODO Auto-generated method stub
//				signals.add(line);
//			}
//		});
//		if (locs.size() != signals.size()) {
//			System.out.println("locs.size() != signals.size()");
//		}
////		checkAP();
//		compute(new double[]{39.95174, 116.23602});
////		test(new double[]{39.95103, 116.23596});
//		
////		estimatePosition();
//	}
	public static class Position implements Comparable{
		public double avg;
		public double[] location;
		public double a;
		public double n;
		public String BSSID;
		public Position(double a, double[] loc) {
			// TODO Auto-generated constructor stub
			avg = a;
			location = loc;
		}
		
		public Position(String bssid, double[] loc, double aa, double nn){
			this.BSSID = bssid;
			this.a = aa;
			this.n = nn;
			this.location = loc;
		}
		
		/**
		 * @param pa
		 */
		public Position(Position pa) {
			// TODO Auto-generated constructor stub
			avg = pa.avg;
			location = pa.location;
			this.BSSID = pa.BSSID;
			this.a = pa.a;
			this.n = pa.n;
		}

		/* (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(Object o) {
			// TODO Auto-generated method stub
			Position other = (Position)o;
			if (avg > other.avg) {
				return -1;
			}
			if (avg == other.avg) {
				return 0;
			}
			if (avg < other.avg) {
				return 1;
			}
			return 0;
		}
	}
	
//	public static double[] estimateLocation(JSONArray array,
//			ArrayList<TaskBarrierThread> apList){
//		double[] ret = new double[2];
//		Map<String, Integer> rssiMap = new HashMap<String, Integer>();
//		ArrayList<Double> distanceList = new ArrayList<>();
////		将array转换为map数据结构，供下面使用
//		for (int i=0; i< array.length(); ++i) {
//			JSONObject object = array.getJSONObject(i);
//			if (rssiMap.get(object.getString("BSSID")) == null) {
//				rssiMap.put(object.getString("BSSID"), object.getInt("Level"));
//			}
//		}
////		根据每个ap和array中观测的位置计算距离d, d = exp(( a - rssi)/10/n);
//		for (TaskBarrierThread task : apList) {
//			Integer rssi = rssiMap.get(task.bssidString);
//			if (rssi == null) {
//				continue;
//			}
////			pa,pb.pc,pd 到ap的距离
//			double d = Math.pow(10,(task.parameterA - rssi)/10/task.parameterN);
//			task.estimateDistance = d;
//			distanceList.add(d);
//		}
////		double da = distanceList.get(0);
////		double db = distanceList.get(1);
////		double dc = distanceList.get(2);
//		
////		暴力遍历
//		int minDistance = Integer.MAX_VALUE;
//		for(double lat=39.9505;lat<39.9515;lat+=0.00001){
//			for(double lng=116.2355;lng<116.2365;lng+=0.00001){
//				int dis = 0;
//				for (TaskBarrierThread task : apList) {
//					dis+= Math.abs(Utils.Distance(lat, lng, task.result[0], task.result[1])-task.estimateDistance);
//				}
//				if (dis < minDistance) {
//					minDistance = dis;
//					ret[0] = lat;
//					ret[1] = lng;
//				}
//			}
//		}
//		
////		根据三边质心定位法推算位置。
////		首先假设三个圆中至少有两个圆相交
//		
////		如果全不相交，计算三个ap的质心
//		
//		return ret;
//	}
//	/**
//	 *	西北角：39.95122,116.235857 WN c
//		西南角：39.950962,116.235879 WS d
//		东南角：39.950899,116.236034 ES b
//		东北角：39.951195,116.236131 EN a
//		中心：    39.951105,116.235979 cc
//		
//		39.95080-39.95130, 116.23575-116.23625
//	 */
//	static double[] la = new double[]{39.951195,116.236131};
//	static double[] lb = new double[]{39.950899,116.236034};
//	static double[] lc = new double[]{39.95122,116.235857};
//	static double[] ld = new double[]{39.950962,116.235879};
//	static double[] lcc= new double[]{39.951105,116.235979};
//	public static void estimatePosition(){
//		ArrayList<AccessPointPool> pools = new ArrayList<>();
//		pools.add(getPool("AP/EN-LocationWifi.txt", "AP/EN-signal.signal", la));
//		pools.add(getPool("AP/ES-LocationWifi.txt", "AP/ES-signal.signal", lb));
//		pools.add(getPool("AP/WN-LocationWifi.txt", "AP/WN-signal.signal", lc));
//		pools.add(getPool("AP/WS-LocationWifi.txt", "AP/WS-signal.signal", ld));
//		pools.add(getPool("AP/Center-LocationWifi.txt", "AP/Center-signal.signal", lcc));
//		
////		pools.add(getPool("AP1/A_LocationWifi.txt", "AP1/A_signal.signal", new double[]{39.951292,116.235733}));
////		pools.add(getPool("AP1/C_LocationWifi.txt", "AP1/C_signal.signal", new double[]{39.950911,116.235853}));
////		pools.add(getPool("AP1/E_LocationWifi.txt", "AP1/E_signal.signal", new double[]{39.950856,116.236088}));
////		pools.add(getPool("AP1/G_LocationWifi.txt", "AP1/G_signal.signal", new double[]{39.951269,116.236147}));
//		
//		
////		pools.add(getPool("AP1/B_LocationWifi.txt", "AP1/B_signal.signal", new double[]{39.951098,116.235821}));
////		pools.add(getPool("AP1/I_LocationWifi.txt", "AP1/I_signal.signal", new double[]{39.951064,116.236002}));
//		final ArrayList<TaskBarrierThread> arrayList = estimateAP(pools);
//		count_len=0;
////		写算法估计每个采样信号的位置
//		FileUtils.readEachLine("AP/Center-signal.signal", new FileUtils.ReadLineCB() {
//			
//			@Override
//			public void readLineCallBack(String line) {
//				// TODO Auto-generated method stub
//				JSONArray array = new JSONArray(line);
//				double[] loc = estimateLocation(array, arrayList);
//				System.out.println(""+loc[0]+","+loc[1]);
//				count_len+=Utils.Distance(loc[0], loc[1], 39.951105,116.235979);
//				countTimes++;
//			}
//		});
//		System.out.println("after count_len = "+count_len/countTimes);
//		count_len=0;countTimes=0;
//		FileUtils.readEachLine("AP/Center-LocationWifi.txt", new FileUtils.ReadLineCB() {
//			
//			@Override
//			public void readLineCallBack(String line) {
//				// TODO Auto-generated method stub
//				String[] items = line.split(",");
//				if (items.length >= 4) {
//					double lat = Double.parseDouble(items[2]);
//					double lng = Double.parseDouble(items[3]);
//					count_len+=Utils.Distance(lat,lng, 39.951105,116.235979);
//					countTimes++;
//				}
//			}
//		});
//		System.out.println("orginal count_len = "+count_len/countTimes);
//		System.out.println("main thread finished!!"); 
//	}
//	
//	public static ArrayList<TaskBarrierThread> estimateAP(ArrayList<AccessPointPool> pools){
////		统计ap，找出同时覆盖四个点的ap，该ap信号在不同点梯度较大
//		String bssidString;
//		final long start = System.currentTimeMillis();
//		ArrayList<TaskBarrierThread> result = new ArrayList<TaskBarrierThread>(); 
//		ArrayList<String> apBSSID = new ArrayList<>();
//		Iterator iter = pools.get(0).APList.entrySet().iterator();
//		while(iter.hasNext()){
//			Map.Entry entry = (Map.Entry) iter.next();
//			Object key = entry.getKey();
//			AccessPoint ap = (AccessPoint) entry.getValue();
//			bssidString = ap.getBSSID();
//			boolean flag = false;
//			double countAvg = 0;
//			for (AccessPointPool pool : pools) {
//				AccessPoint apoint = pool.APList.get(bssidString);
//				if (apoint == null) {
//					flag = true;
//					break;
//				}
//				countAvg += apoint.getAvgSignalStrength();
//				if (apoint.getAvgSignalStrength() < -70) {
//					flag = true;
//					break;
//				}
//				System.out.println(bssidString+":"+pool.tempRealPostion[0]+","+pool.tempRealPostion[1]+", avg"+apoint.getAvgSignalStrength());
//			}
//			if (flag) {
//				continue;
//			}
//			apBSSID.add(bssidString);//39.951105,116.235979
////			if (apBSSID.size() < 3) {
////				apBSSID.add(bssidString);
////			}
//		}
//		
//		CyclicBarrier barrier = new CyclicBarrier(apBSSID.size()+1,new Runnable(){  
//            @Override  
//            public void run() {  
//                long end = System.currentTimeMillis();  
//                System.out.println("total time : "+(end-start)+"ms");  
//            }  
//        });
//		for (String string : apBSSID) {
//			ArrayList<Position> list = new ArrayList<Position>();
//			for (AccessPointPool pool : pools) {
//				AccessPoint apoint = pool.APList.get(string);
//				list.add(new Position(apoint.getAvgSignalStrength(), pool.tempRealPostion));
//			}
//			Collections.sort(list);
//			TaskBarrierThread thread = new TaskBarrierThread(barrier,string, list);//改为list输入
//			result.add(thread);
//			thread.start();
//		}
//		try {
//			barrier.await();
//		} catch (InterruptedException | BrokenBarrierException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}  
//		return result;
//	}
//	
//	static double count_len = 0;
//	static double countTimes = 0;
//	public static AccessPointPool getPool(String recoder, String signal, double[] pos){
//		AccessPointPool pool = new AccessPointPool();
//		FileUtils.readEachLine(recoder, new FileUtils.ReadLineCB() {
//			
//			@Override
//			public void readLineCallBack(String line) {
//				// TODO Auto-generated method stub
//				locs.add(line);
//			}
//		});
//		FileUtils.readEachLine(signal, new FileUtils.ReadLineCB() {
//			
//			@Override
//			public void readLineCallBack(String line) {
//				// TODO Auto-generated method stub
//				signals.add(line);
//			}
//		});
//		if (locs.size() != signals.size()) {
//			System.out.println("locs.size() != signals.size()");
//		}
////		记录所有location
//		for (int i = 0; i < locs.size(); i++) {
//			String string = locs.get(i);
//			if (string.contains("E")) {
//				continue;
//			}
//			Location location = new Location(locs.get(i), new JSONArray(signals.get(i)));
//			pool.eliminateAccessPoint(location);
//		}
//		pool.tempRealPostion = pos;
//		return pool;
//	}
//	
//	//检查结果集合中一个ap对应的点集
//	public static void checkAP(){
//		//训练pool
//		AccessPointPool pool = new AccessPointPool();
//		ArrayList<Location> list = new ArrayList<>();
//		
////		记录所有location
//		for (int i = 0; i < locs.size(); i++) {
//			String string = locs.get(i);
//			if (string.contains("E")) {
//				continue;
//			}
//			Location location = new Location(locs.get(i), new JSONArray(signals.get(i)));
//			pool.eliminateAccessPoint(location);
//			list.add(location);
//		}
//		//对每一个ap，列出出现在信号大于均值的点
//		Iterator iter = pool.APList.entrySet().iterator();
//		while(iter.hasNext()){
//			Map.Entry entry = (Map.Entry) iter.next();
//			Object key = entry.getKey();
//			AccessPoint ap = (AccessPoint) entry.getValue();
//			String nameString =ap.getBSSID().replaceAll(":", "_");
//			ArrayList<Location> targetList = new ArrayList<>();
//			for (Location location : list) {
//				JSONArray array = location.apArray;
//				for(int i=0; i< array.length(); i++){
//					JSONObject object = array.getJSONObject(i);
//					if (object.getString("BSSID").compareTo(ap.getBSSID()) == 0) {
//						int level = object.getInt("Level");
//						targetList.add(location);
////						if (level > ap.getAvgSignalStrength()) {
////							targetList.add(location);
////						}
//					}
//				}
//			}
//			if (targetList.size() > 0) {
//				KMLWriter.LocationKmlSerializer(targetList, nameString+".kml", 1, ap.getBSSID());
//			}
//		}
//		iter = pool.APList.entrySet().iterator();
//		while(iter.hasNext()){
//			Map.Entry entry = (Map.Entry) iter.next();
//			Object key = entry.getKey();
//			AccessPoint ap = (AccessPoint) entry.getValue();
//			System.out.println(ap.getBSSID()+"="+ap.toString());
//		}
//		System.out.println("pool.APList.size = "+pool.APList.size());
//	}
//	
//	public static void test(double[] center){
//		if (center == null) {
//			return ;
//		}
//		AccessPointPool pool = new AccessPointPool();
//		FileOutputStream fos = FileUtils.createFileOutputStream("test.txt");
//		ArrayList<Location> list = new ArrayList<>();
////		记录所有location
//		for (int i = 0; i < locs.size(); i++) {
//			String string = locs.get(i);
//			if (string.contains("E")) {
//				continue;
//			}
//			Location location = new Location(locs.get(i), new JSONArray(signals.get(i)));
//			pool.eliminateAccessPoint(location);
//			list.add(location);
//		}
////		找出离中心店最近的loc
//		double distance = 0;
//		double minDistance = Double.MAX_VALUE;
//		Location minLocation = null;
//		double maxDistance = Double.MIN_VALUE;
//		Location maxLocation = null;
//		for (Location location : list) {
//			distance = Utils.Distance(center[0], center[1], location.latitude, location.longitude);
//			if (distance < minDistance) {
//				minDistance = distance;
//				minLocation = location;
//			}
//			if (distance > maxDistance) {
//				maxDistance = distance;
//				maxLocation = location;
//			}
//			location.tempDistance = distance;
//		}
////		找出信号强度与pool中波动最小的点。
//		double signal = 0;
//		double minSignal = Double.MAX_VALUE;
//		Location minSigLocation = null;
//		double maxSignal = 0;
//		Location maxSigLocation = null;
//		Double countWieight = 0.0;
//		try {
//			for (Location location : list) {
//				countWieight = 0.0;
//				signal = 0;
//				for (Object obj : location.apArray) {
//					JSONObject jsonObject = (JSONObject) obj;
//					AccessPoint ap = pool.APList.get(jsonObject.getString("BSSID"));
//					if (ap == null) {
//						continue;
//					}
//					double xx = Math.abs((jsonObject.getInt("Level") - ap.getAvgSignalStrength()));
//					signal += xx;
////					signal += (Math.abs((jsonObject.getInt("Level") - ap.getAvgSignalStrength()))*ap.getFDR());
//					countWieight+=ap.getFDR();
//				}
////				signal = signal/countWieight;
//				if (signal < minSignal) {
//					minSignal = signal;
//					minSigLocation = location;
//				}
//				if (signal > maxSignal) {
//					maxSignal = signal;
//					maxSigLocation = location;
//				}
//				location.tempSignal = signal;
//				System.out.println(location.toString());
//				fos.write(new String(location.toString()+"\n").getBytes());
//			}
//			fos.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		System.out.println("minDistance="+minDistance+",\nminLocation=" +minLocation.toString()+
//				",\nminSignal=" +minSignal+
//				",\nminSigLocation=" +minSigLocation.toString()+
//				"");
//		double d1 = Utils.Distance(center[0], center[1], minSigLocation.latitude, minSigLocation.longitude);
//		double d2 = Utils.Distance(center[0], center[1], minLocation.latitude, minLocation.longitude);
//		double d = Math.abs(d2-d1);
//		System.out.println("distance between minLocation and minSigLocation = "+d);
//		
//		System.out.println("maxDistance="+maxDistance+",\nmaxLocation=" +maxLocation.toString()+
//				",\nmaxSignal=" +maxSignal+
//				",\nmaxSigLocation=" +maxSigLocation.toString()+
//				"");
//		d1 = Utils.Distance(center[0], center[1], maxSigLocation.latitude, maxSigLocation.longitude);
//		d2 = Utils.Distance(center[0], center[1], maxLocation.latitude, maxLocation.longitude);
//		d = Math.abs(d2-d1);
//		System.out.println("distance between maxLocation and maxSigLocation = "+d);
//		Iterator iter = pool.APList.entrySet().iterator();
//		while(iter.hasNext()){
//			Map.Entry entry = (Map.Entry) iter.next();
//			Object key = entry.getKey();
//			AccessPoint ap = (AccessPoint) entry.getValue();
//			System.out.println(ap.getBSSID()+"="+ap.toString());
//		}
//		System.out.println("pool.APList.size = "+pool.APList.size());
//	}
//	
//	public static void compute(double[] center){
//		AccessPointPool pool = new AccessPointPool();
//		FileOutputStream fos = FileUtils.createFileOutputStream("out.txt");
//		ArrayList<Location> list = new ArrayList<>();
//		double beforeCount = 0;
//		double afterCount = 0;
//		try {
//			for (int i = 0; i < locs.size(); i++) {
//				String string = locs.get(i);
//				if (string.contains("E")) {
//					continue;
//				}
//				Location loc = new Location(locs.get(i), new JSONArray(signals.get(i)));
//				beforeCount += Utils.Distance(loc.latitude, loc.longitude, center[0], center[1]);
//				loc= pool.reduceDrift(loc);
//				afterCount += Utils.Distance(loc.latitude, loc.longitude, center[0], center[1]);
//				list.add(loc);
//				fos.write(new String(loc.toString()+"\n").getBytes());
//			}
//			fos.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	
}
