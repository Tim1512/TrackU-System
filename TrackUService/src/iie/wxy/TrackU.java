/**
 * 
 */package iie.wxy;

import iie.wxy.Utils.KMLWriter;
import iie.wxy.Utils.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

/** 
 * @ClassName: TrackU 
 * @Description: TODO
 * @author  wxy
 *  
 */
public class TrackU {
	static final String FILE_FILTERPOINT= "FilterPoint.txt";
	static final String FILE_SMOOTH		= "SmoothData.txt";
	static final String FIEL_LOCATION_OUT="LocationOut";
	static int			THRESHOLD_UPPER_LIMIT = 50;
	
//	Please replace the meta-data file name as follow string:
	static final String FILE_INPUT = "LocationWifi.txt";
	/**
	 * @title: main 
	 * @description: TODO
	 * @param args void
	 * @throws
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		GaussFilter(FILE_INPUT, FILE_SMOOTH);
		ActivityDection(FILE_SMOOTH,FIEL_LOCATION_OUT,2);
		File file = new File(FILE_SMOOTH);if (file.exists()) {file.delete();}
	}
	static void GaussFilter(String input, String output){
		FilterPoint(input, FILE_FILTERPOINT);
		String line = null;
		String[] items = null;
		FileInputStream fis= null;
		FileOutputStream fos = null;
		File file=new File(FILE_FILTERPOINT); 
		ArrayList<Location> dataSet=new ArrayList<Location>();
		try {
			fis = new FileInputStream(file);
			fos = new FileOutputStream(new File(output));
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd,HH:mm:ss");
			BufferedReader buffreader = new BufferedReader(new InputStreamReader(fis));
			
			buffreader.mark((int)file.length()+1);
			while ((line=buffreader.readLine()) != null){
				if (line.length() > 0) {//static 
					items = line.split(",");
					if (items.length >= 4) {
						double lat = Double.parseDouble(items[2]);
						double lng = Double.parseDouble(items[3]);
						Date date1 = sdf.parse(items[0]+","+items[1]);
						dataSet.add(new Location(date1.getTime(), lat, lng));
					}
				}
			}
			dataSet = GaussKernelSmoother.GaussSmoother(dataSet);
			buffreader.reset();
			int lineCount = 0;
			while ((line=buffreader.readLine()) != null){
				//2016-03-07,00:14:00,22.551605,114.234724
				if (line.length() > 0) {//static 
					items = line.split(",");
					if (items.length >= 2) {
						String newLat = ""+dataSet.get(lineCount).latitude;
						String newLng = ""+dataSet.get(lineCount).longitude;
						String outString = items[0]+","+items[1]+","+newLat+","+newLng+"\n";
						fos.write(outString.getBytes());
						lineCount++;
					}
				}
			}
			fis.close();
			fos.close();
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			file = new File(FILE_FILTERPOINT);if (file.exists()) {file.delete();}
		}

	}
	//50m/s,如果两个记录点之间超过这个速度，则认为是奇点，然后扔掉
	static void FilterPoint(String strFile, String strResult){
		
		File file_in = new File(strFile);
		File file_out = new File(strResult);
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			fis = new FileInputStream(file_in);
			fos = new FileOutputStream(file_out);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		InputStreamReader inputReader = new InputStreamReader(fis);
		BufferedReader buffreader = new BufferedReader(inputReader);
		String line1;
		String line2;
		String[] items1;
		String[] items2;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd,HH:mm:ss");
		try {
			while (( line1 = buffreader.readLine()) != null) {
				if (line1.length()>0) {
					if (line1.contains("E")) {
						continue;
					}
					break;
				}
			}			
			fos.write(new String(line1+"\n").getBytes());
			while (( line2 = buffreader.readLine()) != null) {
				if (line2.length()>0) {
					items1 = line1.split(",");
					items2 = line2.split(",");
					if (items1.length>=4 && items2.length>=4) {
						Date date1 = sdf.parse(items1[0]+","+items1[1]);
						Date date2 = sdf.parse(items2[0]+","+items2[1]);
						long temp = date2.getTime()- date1.getTime();//milliseconds 
						double distance = Utils.Distance(items1[2], items1[3], items2[2], items2[3]);//m
						double speed;
						if (temp == 0) {
							continue;
						}
						speed = distance*1000/temp;
						if(speed < THRESHOLD_UPPER_LIMIT){
							fos.write(new String(line2+","+speed+"\n").getBytes());
						}else{
							continue ;
						}
					}
					line1 = line2;
				}
			}
			fis.close();
			fos.close();
		}catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static void ActivityDection(String input, String output, int trajType){
		String line;
		String line2;
		File file_inFile = new File(input);
		String[] items;
		FileInputStream fis= null;
		FileOutputStream fos = null;
		ArrayList<String> list = new ArrayList<>();
		ArrayList<TrajectorySegment> trajList = new ArrayList<>();
		try {
			fis = new FileInputStream(file_inFile);
			BufferedReader buffreader = new BufferedReader(new InputStreamReader(fis));
			while ((line = buffreader.readLine()) != null) {
				if (line.length() > 0) {//static 
					items = line.split(",");
					if (items.length >= 4) {
						break;
					}
				}
			}
			while ((line2=buffreader.readLine()) != null){
				if (line2.length() > 0) {//static 
					items = line2.split(",");
					if (items.length >= 4) {
						trajList.add(new TrajectorySegment(line, line2));
						line = line2;
					}
				}
			}
			fis.close();
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//融合线段
		int changedFlag = 1;
		while(changedFlag == 1 ){
			changedFlag = 0;
			TrajectorySegment baseSegment = null;
			TrajectorySegment iterSegment = null;
			int ret = -1;
			Iterator<TrajectorySegment> iterIterator = null;
			
//			合并相邻同状态的段
			if (MergeSegment(trajList) == 1) {
				changedFlag = 1;
			}
//			检查所有静止线段的阈值是否符合的线段合并
			iterIterator = trajList.iterator();
			while(iterIterator.hasNext()){
				iterSegment = iterIterator.next();
				ret = iterSegment.checkTarjectoryThreshold(0);
				if (ret == 1) {
					iterSegment.changeTrajectoryType();
				}
			}			
//			合并相邻同状态的段
			if (MergeSegment(trajList) == 1) {
				changedFlag = 1;
			}
//			检查所有运动线段的阈值是否符合的线段合并
			iterIterator = trajList.iterator();
			while(iterIterator.hasNext()){
				iterSegment = iterIterator.next();
				ret = iterSegment.checkTarjectoryThreshold(1);
				if (ret == 1) {
					iterSegment.changeTrajectoryType();
				}
			}			
		}
		MergeDriftSegment(trajList);
		smoothTripEnds(trajList);
		String dirString ="";
		if (output.lastIndexOf("\\") != -1) {
			dirString =output.substring(0, output.lastIndexOf("\\"));
		}
		KMLWriter.KmlSerializer(trajList, output+".kml", trajType);

//		used to c
//		double countDistance = 0;
//		for (TrajectorySegment trajectorySegment : trajList) {
//			ArrayList<Location> points = trajectorySegment.points;
//			for (int i =1; i<points.size(); ++i) {
//				countDistance += (Utils.Distance(points.get(i).latitude, points.get(i).longitude, 
//									points.get(i-1).latitude, points.get(i-1).longitude));
//			}
//		}
//		int trajCount = 0;
		//print list
//		for (TrajectorySegment trajectorySegment : trajList) {
//			if (trajectorySegment.type == 1) {//运动
//				//trajCount++;
//				if (trajectorySegment.length < TrajectorySegment.THRESHOLD_LENGTH) {
//					trajCount++;
//				}
//			}else if (trajectorySegment.type < 0) {
//				System.out.println("error");
//			}else if (trajectorySegment.type == 0) {
//				if(trajectorySegment.duration < TrajectorySegment.THRESHOLD_DURATION){
//					trajCount++;
//				}
//			}
//		}
//		//SliptActivity(input, output, trajList);
//		System.out.println(""+TrajectorySegment.THRESHOLD_DURATION+","+TrajectorySegment.THRESHOLD_LENGTH+
//								","+trajList.size()+","+ trajCount+","+(trajList.size()-trajCount));
//		System.out.println(trajList);
		return ;
	}
	//merge segment with the same type
	static int MergeSegment(ArrayList<TrajectorySegment> trajList){
		TrajectorySegment baseSegment = null;
		TrajectorySegment iterSegment = null;
		Iterator<TrajectorySegment> iterIterator = null;
		int changedFlag = 0;

		iterIterator = trajList.iterator();
		while(iterIterator.hasNext()){
			baseSegment = iterIterator.next();
			if (baseSegment.type < 0) {
				iterIterator.remove();
			}else {
				break;
			}
		}
		while(iterIterator.hasNext()){
			iterSegment = iterIterator.next();
			if (iterSegment.type == baseSegment.type) {
				changedFlag = 1;
				baseSegment.mergeTarjectory(iterSegment);
				iterIterator.remove();
			}else{
				baseSegment = iterSegment;
			}
		}
		return changedFlag;
	}
	static int MergeDriftSegment(ArrayList<TrajectorySegment> trajList){
		TrajectorySegment traj = null;
		TrajectorySegment traj1= null;
		TrajectorySegment traj2= null;
		boolean changedFlag = true;
		while (changedFlag) {
			changedFlag = false;
			for(int i=0; i<trajList.size()-2; i++){
				traj = trajList.get(i);
				traj1= trajList.get(i+1);
				traj2= trajList.get(i+2);
				if (traj.type==0 && traj1.type==1 && traj2.type==0) {
					if (traj1.duration > TrajectorySegment.THRESHOLD_DURATION*1000) {
						continue;
					}
					double ratio = Utils.checkRectOverlap(traj.getCoverageRect(), traj2.getCoverageRect());
					if (ratio>0.5) {
						changedFlag = true;
						//merge
//						traj.MergeTarjectory(traj1);
						traj.mergeTarjectory(traj2);
						//delete
						trajList.remove(i+1);
						trajList.remove(i+1);
						break;
					}
				}
			}
			
		}
		return 0;
	}
	static int smoothTripEnds(ArrayList<TrajectorySegment> trajList){
		int length = TrajectorySegment.THRESHODL_INTERNAL;
		if (length < 2) {
			length = 3;
		}
		for (int i = 0; i < trajList.size(); i++) {
			if (trajList.get(i).type == 1) {
				ArrayList<Location> points = trajList.get(i).points;
				for(int x=2; x<length+1; ++x){
					for (int j=0; j<points.size()-x; ++j) {
						double offset = Utils.Distance(points.get(j), points.get(j+x));
						double trip=0;
						for (int k = j; k < j+x; k++) {
							double xx = Utils.Distance(points.get(k), points.get(k+1));
							trip+= (xx);
						}
						double ratio = trip/(1+offset);
						if (ratio > 2.0) {
//							System.out.println("ratio="+ratio);
							ArrayList<Location> dataSet = new ArrayList<>();
							int start=0, end=0;
							if (i>0) {
								dataSet.addAll(trajList.get(i-1).points);
								start = trajList.get(i-1).points.size();
							}
							dataSet.addAll(trajList.get(i).points);
							if (i < (trajList.size()-1)) {
								dataSet.addAll(trajList.get(i+1).points);
							}
							end = start+trajList.get(i).points.size();
							//smooth....
							 trajList.get(i).points = GaussKernelSmoother.GaussSmoother(dataSet,start,end,j+1,x-1,ratio);
						}
					}					
				}
			}
		}
		return 0;
	}
//	static void SliptActivity(String input, String output, ArrayList<TrajectorySegment> trajList){
//		File file_inFile = new File(input);
//		FileInputStream fis= null;
//		try {
//			fis = new FileInputStream(file_inFile);
//			BufferedReader buffreader = new BufferedReader(new InputStreamReader(fis));
//
//			buffreader.mark((int)file_inFile.length()+1);
//			for (TrajectorySegment traj : trajList) {
//				buffreader.reset();
//				if (traj.type==1 &&traj.points.size()>4) {
//					String foutString = output+traj.startTime.replaceAll(":", "-");
//					WriteSegment2File(buffreader, foutString+".raw", traj);
//					ComputeSpeed(foutString+".raw", foutString+".speed");
//					ComputeAcc(foutString+".speed", foutString+".result");
//					File file = null;
//					file = new File(foutString+".raw");if (file.exists()) {file.delete();}
//					file = new File(foutString+".speed");if (file.exists()) {file.delete();}
//				}
//			}
//			fis.close();
//		} catch (NumberFormatException | IOException | ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
}
