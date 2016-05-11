package iie.wxy;


import java.util.ArrayList;

public class GaussKernelSmoother {

	private static final long e = 10*1000;
	public static ArrayList<Location> GaussSmoother(ArrayList<Location> dataSet, int start, int end, int index, int length, double ratio){
		if (ratio == 0) {
			ratio = 1;
		}
		ArrayList<Location> result = new ArrayList<Location>();
		for (int i = start; i < end; i++) {
			double wt = 0;
			double swt = 0;
			double sumlat = 0;
			double sumlng = 0;
			if (i>=start+index && i <start+index+length) {
				int tempStart = start+index -TrajectorySegment.THRESHODL_INTERNAL*3;
				if (tempStart < 0) {
					tempStart=0;
				}
				int tempEnd = start+index+length+TrajectorySegment.THRESHODL_INTERNAL*3;
				if (tempEnd>dataSet.size()) {
					tempEnd = dataSet.size();
				}
				for (int j = tempStart; j < tempEnd; j++) {
					wt = (dataSet.get(i).timestamp - dataSet.get(j).timestamp)*(dataSet.get(i).timestamp - dataSet.get(j).timestamp)/2/(e*ratio)/(e*ratio);
					wt = Math.exp(-wt);
					swt += wt;
					sumlat += wt*dataSet.get(j).latitude;
					sumlng += wt*dataSet.get(j).longitude;
				}
				sumlat = sumlat/swt;
				sumlng = sumlng/swt;
				result.add(new Location(dataSet.get(i).timestamp, sumlat, sumlng));
			}else {
				result.add(dataSet.get(i));
			}
			sumlat = 0;
			sumlng = 0;
			swt = 0;
		}
		return result;
	}
	//dataSet:<timestamp, value>
	public static ArrayList<Location> GaussSmoother(ArrayList<Location> dataSet){
		ArrayList<Location> result = new ArrayList<Location>();
		for (int i = 0; i < dataSet.size(); i++) {
			double wt = 0;
			double swt = 0;
			double sumlat = 0;
			double sumlng = 0;
			for (int j = 0; j < dataSet.size(); j++) {
				wt = (dataSet.get(i).timestamp - dataSet.get(j).timestamp)*(dataSet.get(i).timestamp - dataSet.get(j).timestamp)/2/e/e;
				wt = Math.exp(-wt);
//				wt = -Math.pow((dataSet.get(i).timestamp - dataSet.get(j).timestamp), 2)/2*e*e;
				swt += wt;
				sumlat += wt*dataSet.get(j).latitude;
				sumlng += wt*dataSet.get(j).longitude;
			}
			sumlat = sumlat/swt;
			sumlng = sumlng/swt;
			result.add(new Location(dataSet.get(i).timestamp, sumlat, sumlng));
			sumlat = 0;
			sumlng = 0;
			swt = 0;
		}
		return result;
	}
}
