package iie.wxy;
import iie.wxy.Utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class KMean {

    private int k;// 分成多少簇  
    private int m;// 迭代次数  
    private int dataSetLength;// 数据集元素个数，即数据集的长度  
    private ArrayList<double[]> dataSet;// 数据集链表  
    private ArrayList<double[]> center;// 中心链表  
    private ArrayList<ArrayList<double[]>> cluster; // 簇  
    private ArrayList<Double> jc;// 误差平方和，k越接近dataSetLength，误差越小  
    private Random random;  
  
    public ArrayList<double[]> getCenter(){
    	return center;
    }
    
    public void setDataSet(ArrayList<double[]> dataSet) {  
        this.dataSet = dataSet;  
    }  

    public ArrayList<ArrayList<double[]>> getCluster() {  
        return cluster;  
    }  
  
    public KMean(int k) {  
        if (k <= 0) {  
            k = 1;  
        }  
        this.k = k;  
    }  
  
    private void init() {  
        m = 0;  
        random = new Random();  
        if (dataSet == null || dataSet.size() == 0) {  
            initDataSet();  
        }  
        dataSetLength = dataSet.size();  
        if (k > dataSetLength) {  
            k = dataSetLength;  
        }  
        center = initCenters();  
        cluster = initCluster();  
        jc = new ArrayList<Double>();  
    }  
  
    private void initDataSet() {  
        dataSet = new ArrayList<double[]>();  
        double[][] dataSetArray = new double[][] { { 8, 2 }, { 3, 4 }, { 2, 5 },  
                { 4, 2 }, { 7, 3 }, { 6, 2 }, { 4, 7 }, { 6, 3 }, { 5, 3 },  
                { 6, 3 }, { 6, 9 }, { 1, 6 }, { 3, 9 }, { 4, 1 }, { 8, 6 } };  
  
        for (int i = 0; i < dataSetArray.length; i++) {  
            dataSet.add(dataSetArray[i]);  
        }  
    }  
  
    private ArrayList<double[]> initCenters() {  
        ArrayList<double[]> center = new ArrayList<double[]>();  
        int[] randoms = new int[k];  
        boolean flag;  
        int temp = random.nextInt(dataSetLength);  
        randoms[0] = temp;  
        for (int i = 1; i < k; i++) {  
            flag = true;  
            while (flag) {  
                temp = random.nextInt(dataSetLength);  
                int j = 0;  
                while (j < i) {  
                    if (temp == randoms[j]) {  
                        break;  
                    }  
                    j++;  
                }  
                if (j == i) {  
                    flag = false;  
                }  
            }  
            randoms[i] = temp;  
        }  
        for (int i = 0; i < k; i++) {  
            center.add(dataSet.get(randoms[i]));// 生成初始化中心链表  
        }  
        return center;  
    }  
    
    private ArrayList<ArrayList<double[]>> initCluster() {  
        ArrayList<ArrayList<double[]>> cluster = new ArrayList<ArrayList<double[]>>();  
        for (int i = 0; i < k; i++) {  
            cluster.add(new ArrayList<double[]>());  
        }  
  
        return cluster;  
    }  
  
    private double distance(double[] element, double[] center) {  

        return Utils.Distance(element[0], element[1], center[0], center[1]);  
    }  
  
    private int minDistance(double[] distance) {  
        double minDistance = distance[0];  
        int minLocation = 0;  
        for (int i = 1; i < distance.length; i++) {  
            if (distance[i] < minDistance) {  
                minDistance = distance[i];  
                minLocation = i;  
            } else if (distance[i] == minDistance) // 如果相等，随机返回一个位置  
            {  
                if (random.nextInt(10) < 5) {  
                    minLocation = i;  
                }  
            }  
        }  
  
        return minLocation;  
    }  
  
    private void clusterSet() {  
        double[] distance = new double[k];  
        for (int i = 0; i < dataSetLength; i++) {  
            for (int j = 0; j < k; j++) {  
                distance[j] = distance(dataSet.get(i), center.get(j));  
                // System.out.println("test2:"+"dataSet["+i+"],center["+j+"],distance="+distance[j]);  
  
            }  
            int minLocation = minDistance(distance);  
            cluster.get(minLocation).add(dataSet.get(i));// 核心，将当前元素放到最小距离中心相关的簇中  
  
        }  
    }  
  
    private double errorSquare(double[] element, double[] center) {  
        double x = element[0] - center[0];  
        double y = element[1] - center[1];  
  
        double errSquare = x * x + y * y;  
  
        return errSquare;  
    }  
  
    private void countRule() {  
        double jcF = 0;  
        for (int i = 0; i < cluster.size(); i++) {  
            for (int j = 0; j < cluster.get(i).size(); j++) {  
                jcF += errorSquare(cluster.get(i).get(j), center.get(i));  
  
            }  
        }  
        jc.add(jcF);  
    }  
  
    private void setNewCenter() {  
        for (int i = 0; i < k; i++) {  
            int n = cluster.get(i).size();  
            if (n != 0) {  
                double[] newCenter = { 0, 0 };  
                for (int j = 0; j < n; j++) {  
                    newCenter[0] += cluster.get(i).get(j)[0];  
                    newCenter[1] += cluster.get(i).get(j)[1];  
                }  
                newCenter[0] = newCenter[0] / n;  
                newCenter[1] = newCenter[1] / n;  
                center.set(i, newCenter);  
            }  
        }  
    }  
  
    public void printDataArray(ArrayList<double[]> dataArray, int index, 
            String dataArrayName) {  
    	System.out.println("center is ["+center.get(index)[0]+","+center.get(index)[1]+"]");
//        for (int i = 0; i < dataArray.size(); i++) {  
//            System.out.println("print:" + dataArrayName + "[" + i + "]={"  
//                    + dataArray.get(i)[0] + "," + dataArray.get(i)[1] + "}");  
//        }  
        System.out.println("===================================");  
    }  
  
    private void kmeans() {  
        init();  
        // 循环分组，直到误差不变为止  
        while (true) {  
            clusterSet();  
            countRule();  
            // 误差不变了，分组完成  
            if (m != 0) {  
                if (jc.get(m) - jc.get(m - 1) == 0) {  
                    break;  
                }  
            }  
  
            setNewCenter();  
            // printDataArray(center,"newCenter");  
            m++;  
            cluster.clear();  
            cluster = initCluster();  
        }  
    }  
  
    public void execute() {  
        long startTime = System.currentTimeMillis();  
        System.out.println("kmeans begins");  
        kmeans();  
        long endTime = System.currentTimeMillis();  
        System.out.println("kmeans running time=" + (endTime - startTime)  
                + "ms");  
        System.out.println("kmeans ends");  
        System.out.println();  
    }  
    
    public void findClusterPoint(int k){
    	Map<double[], Integer> hashMap = new HashMap<double[], Integer>(); 
    	for(double[] point:dataSet){
    		Integer tempInteger = hashMap.get(point);
    		if (tempInteger == null) {
				hashMap.put(point, 1);
			}else {
				hashMap.put(point, tempInteger+1);
			}
    	}
    	System.out.println("hash map ="+hashMap.size());
    	double[] maxPoint;int maxCount=0;
    	for(double[] inter:hashMap.keySet()){
    		Integer countInteger = hashMap.get(inter);
    		if (countInteger > maxCount) {
				maxCount = countInteger;
				maxPoint = inter;
			}
    	}
    	
     }
}
