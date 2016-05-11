package iie.wxy.Utils;

import iie.wxy.Location;
import iie.wxy.TrajectorySegment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;

import javax.jws.soap.SOAPBinding.Style;

import org.json.JSONArray;
import org.json.JSONObject;

public class KMLWriter {

	public static String styleID_Wifi_polygon_center = "styleID_Wifi_polygon_center";
	
	public static String styleID_Wifi_line_point = "styleID_Wifi_line_point";//时间点
	public static String styleID_Wifi_line = "styleID_Wifi_line";//时间段
	public static String styleID_Wifi_polygon = "styleID_Wifi_polygon";//时间段
	
	public static String styleID_GPS_line_point = "styleID_GPS_line_point";//时间点
	public static String styleID_GPS_line = "styleID_GPS_line";//时间段
	public static String styleID_GPS_polygon = "styleID_GPS_polygon";//时间段
//	<Placemark>
//		<name>Point25</name>
//		<styleUrl>#icon-503-DB4436-nodesc</styleUrl>
//		<Point>
//			<coordinates>116.234343,39.953012,0.0</coordinates>
//		</Point>
//	</Placemark>
	static void appendPoint(String name, String style, Location coordinate, FileOutputStream fos) throws IOException{
		StringBuffer sbBuffer = new StringBuffer();
		sbBuffer.append("		<Placemark>\n");
		if (name != null) {
			sbBuffer.append("			<name>"+name+"</name>\n");
		}
		if (style != null) {
			sbBuffer.append("			<styleUrl>#"+style+"</styleUrl>\n");
		}
		sbBuffer.append("			<Point>\n");
		sbBuffer.append("				<coordinates>");
		if (coordinate != null) {
			sbBuffer.append(""+coordinate.longitude+","+coordinate.latitude+"");
		}
		sbBuffer.append(",0.0</coordinates>\n			</Point>\n");
		sbBuffer.append("		</Placemark>\n");
		fos.write(new String(sbBuffer).getBytes());
	}
	
//	<Placemark>
//		<name>Polygon1</name>
//		<styleUrl>#poly-F9F7A6-6-104-nodesc</styleUrl>
//		<Polygon>
//			<outerBoundaryIs>
//				<LinearRing>
//					<tessellate>1</tessellate>
//					<coordinates>116.23647699999998,39.954147,0.0 116.234307,39.953657,0.0 116.234829,39.951245,0.0 116.237105,39.950849,0.0 116.23647699999998,39.954147,0.0</coordinates>
//				</LinearRing>
//			</outerBoundaryIs>
//		</Polygon>
//	</Placemark>
	static void appendPloygon(String name, String style, ArrayList<double[]> dataSet, FileOutputStream fos) throws IOException{
		StringBuffer sbBuffer = new StringBuffer();
		sbBuffer.append("		<Placemark>\n");
		if (name != null) {
			sbBuffer.append("			<name>"+name+"</name>\n");
		}
		if (style != null) {
			sbBuffer.append("			<styleUrl>#"+style+"</styleUrl>\n");
		}
		sbBuffer.append("			<Polygon>\n");
		sbBuffer.append("				<outerBoundaryIs>\n");
		sbBuffer.append("					<LinearRing>\n");
		sbBuffer.append("						<tessellate>1</tessellate>\n");	
		sbBuffer.append("						<coordinates>");
		for(double[] coor:dataSet){
			sbBuffer.append(""+coor[1]+","+coor[0]+",0.0 ");
		}
		sbBuffer.append("</coordinates>\n");
		sbBuffer.append("					</LinearRing>\n");
		sbBuffer.append("				</outerBoundaryIs>\n");
		sbBuffer.append("			</Polygon>\n");
		sbBuffer.append("		</Placemark>\n");
		fos.write(new String(sbBuffer).getBytes());
	}
	
//	<Placemark>
//		<name>Line1</name>
//		<styleUrl>#line-F4EB37-11-nodesc</styleUrl>
//		<LineString>
//			<tessellate>1</tessellate>
//			<coordinates>116.234307,39.953657,0.0 116.234248,39.953088,0.0 116.234385,39.951407,0.0 116.234357,39.951047,0.0</coordinates>
//		</LineString>
//	</Placemark>
	static void appendLine(String name, String style, ArrayList<Location> dataSet, ArrayList<String> nameSet, FileOutputStream fos, int type) throws IOException{
		StringBuffer sbBuffer = new StringBuffer();
		sbBuffer.append("		<Placemark>\n");
		if (name != null) {
			sbBuffer.append("			<name>"+name+"</name>\n");
		}
		if (style != null) {
			sbBuffer.append("			<styleUrl>#"+style+"</styleUrl>\n");
		}
		sbBuffer.append("			<LineString>\n");
		sbBuffer.append("				<tessellate>1</tessellate>\n");
		sbBuffer.append("				<coordinates>");
		for(Location data:dataSet){
			sbBuffer.append(""+data.longitude+","+data.latitude+",0.0 ");
		}
		sbBuffer.append("</coordinates>\n");
		sbBuffer.append("			</LineString>\n");
		sbBuffer.append("		</Placemark>\n");
		fos.write(new String(sbBuffer).getBytes());
		String pointStyleString=styleID_GPS_line_point;
		if (type == 1) {
			pointStyleString = styleID_Wifi_line_point;
		}
//		for (int i = 0; i < dataSet.size(); i++) {
//			if (nameSet == null || nameSet.size() != dataSet.size()) {
//				AppendPoint(null, pointStyleString, dataSet.get(i), fos);
//			}else {
//				AppendPoint(nameSet.get(i), pointStyleString, dataSet.get(i), fos);
//			}
//			
//		}
	}
	
	static public void KmlFileBegin(FileOutputStream fos) throws IOException{
		String outString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
					"<kml xmlns=\"http://earth.google.com/kml/2.1\" >\n"+
					"	<Document>\n";
		fos.write(outString.getBytes());
	}
	
	static public void KmlFileEnd(FileOutputStream fos) throws IOException{
		//append style and end of the file
		StringBuffer sbBuffer = new StringBuffer();
		sbBuffer.append("		<Style id='"+styleID_Wifi_line+"'>\n");
		sbBuffer.append("			<LineStyle>\n");
		sbBuffer.append("				<color>ff3644DB</color>\n");
		sbBuffer.append("				<width>6</width>\n");
		sbBuffer.append("			</LineStyle>\n");
		sbBuffer.append("			<BalloonStyle>\n");
		sbBuffer.append("				<text><![CDATA[<h3>$[name]</h3>]]></text>\n");
		sbBuffer.append("			</BalloonStyle>\n");
		sbBuffer.append("		</Style>\n");
		sbBuffer.append("		<Style id='"+styleID_Wifi_line_point+"'>\n");
		sbBuffer.append("			<IconStyle>\n");
		sbBuffer.append("				<color>ff3644DB</color>\n");
		sbBuffer.append("				<scale>1.1</scale>\n");
		sbBuffer.append("				<Icon><href>http://www.gstatic.com/mapspro/images/stock/1059-biz-outdoor.png</href></Icon>\n");
		sbBuffer.append("			</IconStyle>\n");
		sbBuffer.append("			<LabelStyle>\n");
		sbBuffer.append("				<scale>2.1</scale>\n");
		sbBuffer.append("			</LabelStyle>\n");
		sbBuffer.append("			<BalloonStyle>\n");
		sbBuffer.append("				<text><![CDATA[<h3>$[name]</h3>]]></text>\n");
		sbBuffer.append("			</BalloonStyle>\n");
		sbBuffer.append("		</Style>\n");
		sbBuffer.append("		<Style id='"+styleID_Wifi_polygon+"'>\n");
		sbBuffer.append("			<LineStyle>\n");
		sbBuffer.append("				<color>ff969CEE</color>\n");
		sbBuffer.append("				<width>6</width>\n");
		sbBuffer.append("			</LineStyle>\n");
		sbBuffer.append("			<PolyStyle>\n");
		sbBuffer.append("				<color>68969CEE</color>\n");
		sbBuffer.append("				<fill>1</fill>\n");
		sbBuffer.append("				<outline>1</outline>\n");
		sbBuffer.append("			</PolyStyle>\n");	
		sbBuffer.append("			<BalloonStyle>\n");
		sbBuffer.append("				<text><![CDATA[<h3>$[name]</h3>]]></text>\n");
		sbBuffer.append("			</BalloonStyle>\n");
		sbBuffer.append("		</Style>\n");
		
		sbBuffer.append("		<Style id='"+styleID_GPS_line+"'>\n");
		sbBuffer.append("			<LineStyle>\n");
		sbBuffer.append("				<color>ffF08641</color>\n");
		sbBuffer.append("				<width>6</width>\n");
		sbBuffer.append("			</LineStyle>\n");
		sbBuffer.append("			<BalloonStyle>\n");
		sbBuffer.append("				<text><![CDATA[<h3>$[name]</h3>]]></text>\n");
		sbBuffer.append("			</BalloonStyle>\n");
		sbBuffer.append("		</Style>\n");
		sbBuffer.append("		<Style id='"+styleID_GPS_line_point+"'>\n");
		sbBuffer.append("			<IconStyle>\n");
		sbBuffer.append("				<color>ff3644DB</color>\n");
		sbBuffer.append("				<scale>1.1</scale>\n");
		sbBuffer.append("				<Icon><href>http://www.gstatic.com/mapspro/images/stock/1469-trans-walking.png</href></Icon>\n");
		sbBuffer.append("			</IconStyle>\n");
		sbBuffer.append("			<LabelStyle>\n");
		sbBuffer.append("				<scale>2.1</scale>\n");
		sbBuffer.append("			</LabelStyle>\n");
		sbBuffer.append("			<BalloonStyle>\n");
		sbBuffer.append("				<text><![CDATA[<h3>$[name]</h3>]]></text>\n");
		sbBuffer.append("			</BalloonStyle>\n");
		sbBuffer.append("		</Style>\n");
		sbBuffer.append("		<Style id='"+styleID_GPS_polygon+"'>\n");
		sbBuffer.append("			<LineStyle>\n");
		sbBuffer.append("				<color>ffFFC39F</color>\n");
		sbBuffer.append("				<width>6</width>\n");
		sbBuffer.append("			</LineStyle>\n");
		sbBuffer.append("			<PolyStyle>\n");
		sbBuffer.append("				<color>68FFC39F</color>\n");
		sbBuffer.append("				<fill>1</fill>\n");
		sbBuffer.append("				<outline>1</outline>\n");
		sbBuffer.append("			</PolyStyle>\n");	
		sbBuffer.append("			<BalloonStyle>\n");
		sbBuffer.append("				<text><![CDATA[<h3>$[name]</h3>]]></text>\n");
		sbBuffer.append("			</BalloonStyle>\n");
		sbBuffer.append("		</Style>\n");
		sbBuffer.append("	</Document>\n"+"</kml>");
		fos.write(new String(sbBuffer).getBytes());
	}
	static public void LocationKmlSerializer(ArrayList<Location> locList, String output, int trajType, String apName){
		FileOutputStream fos = FileUtils.createFileOutputStream(output);
		if (fos == null) {
			return ;
		}
		BufferedReader buffreader;
		try {
			KmlFileBegin(fos);
			String styleString;
			styleString=styleID_GPS_line;
			if (trajType == 1) {
				styleString=styleID_Wifi_line;
			}
			for (int i = 0; i < locList.size(); i++) {
				int level = 1;
				JSONArray array = locList.get(i).apArray;
				for(int j=0; j< array.length(); j++){
					JSONObject object = array.getJSONObject(j);
					if (object.getString("BSSID").compareTo(apName) == 0) {
						level = object.getInt("Level");
						break;
					}
				}
				appendPoint(""+level, styleString, locList.get(i), fos);
			}
			KmlFileEnd(fos);
			fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	static public void KmlSerializer(ArrayList<TrajectorySegment> trajList, String output, int trajType){
		FileOutputStream fos = FileUtils.createFileOutputStream(output);
		if (fos == null) {
			return ;
		}
		BufferedReader buffreader;
		try {
			KmlFileBegin(fos);
			String styleString;
			for (TrajectorySegment trajectorySegment : trajList) {
				switch (trajectorySegment.type) {
				case 0://静止区域，绘制polygon
					styleString=styleID_GPS_polygon;
					if (trajType == 1) {
						styleString=styleID_Wifi_polygon;
					}
					
					appendPloygon(trajectorySegment.getName(), styleString, 
								Utils.computeGeoBondingbox(trajectorySegment.points), fos);
					break;
				case 1://移动路径，绘制
					styleString=styleID_GPS_line;
					if (trajType == 1) {
						styleString=styleID_Wifi_line;
					}
					appendLine(trajectorySegment.getName(), styleString, 
								trajectorySegment.points, null, fos, trajType);
					break;
				default:
					break;
				}
			}
			KmlFileEnd(fos);
			fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
//	static public void KmlSerializer(String input, String output, int style){
//		FileOutputStream fos = CreateFos(output);
//		FileInputStream fis = null;
//        if (fos == null) {
//			return ;
//		}
//        String[] items;
//        String line;
//        BufferedReader buffreader;
//       int count = 0;
//		try {
//			KmlFileBegin(fos);
//			fis = new FileInputStream(new File(input));
//			buffreader = new BufferedReader(new InputStreamReader(fis));
//			while ((line=buffreader.readLine()) != null){
//				if (line.length() > 0) {
//					items = line.split(",");
//					if(items.length > 2){
//						count++;
//						if (Integer.parseInt(items[0]) == 0) {
//							//0,2016-03-07,00:17:14,2016-03-07,00:33:32,14
//							int countLine = Integer.parseInt(items[5]);
//							String[] items2;
//							ArrayList<Location> dataSet = new ArrayList<>();
//							ArrayList<String> dataName = new ArrayList<String>();
//							for (int i = 0; i < countLine; i++) {
//								if((line=buffreader.readLine()) != null){
//									if (line.length() > 0){
//										dataSet.add(new Location(line));
//										
//										
//										items2 = line.split(",");
//										double lat = Double.parseDouble(items2[0]);
//										double lng = Double.parseDouble(items2[1]);
//										dataSet.add(new double[]{lat, lng});
//										dataName.add(items2[2]);
//									}
//								}
//							}
//							String lineStyleString = styleID_GPS_line;
//							if (style == 1) {
//								lineStyleString = styleID_Wifi_line;
//							}
//							appendLine("line"+count, lineStyleString, dataSet, dataName, fos, style);
//						}else if (Integer.parseInt(items[0]) == 1){
//							//maxLat+","+maxlng+","+minLat+","+minlng
//							if (items.length < 11) {
//								System.out.println("shit "+line);
//							}
//							double maxLat = Double.parseDouble(items[5]);
//							double maxlng = Double.parseDouble(items[6]);
//							double minLat = Double.parseDouble(items[7]);
//							double minlng = Double.parseDouble(items[8]);
//							double centerLat = Double.parseDouble(items[9]);
//							double centerLng = Double.parseDouble(items[10]);
//							ArrayList<double[]> dataSet = new ArrayList<double[]>();
//							dataSet.add(new double[]{maxLat,maxlng});
//							dataSet.add(new double[]{minLat,maxlng});
//							dataSet.add(new double[]{minLat,minlng});
//							dataSet.add(new double[]{maxLat,minlng});
//							String styleString=styleID_GPS_polygon;
//							if (style == 1) {
//								styleString=styleID_Wifi_polygon;
//							}else if (style == 2) {
//								styleString=styleID_GPS_polygon;
//							}
//							appendPloygon("area"+count, styleString, dataSet, fos);
//						}
//					}
//				}
//			}
//			KmlFileEnd(fos);
//			fos.close();
//			fis.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		System.out.println(count);
//	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		KMLWriter.KmlSerializer("schedule.txt", "test.kml", 1);
//		System.out.println("KMLWriter excute end\n");
	}
//	static FileOutputStream CreateFos(String name){
//		File file = new File(name);
//		if (file.exists()) {
//			file.delete();
//		}
//		FileOutputStream fos;
//		try {
//			fos = new FileOutputStream(file,true);
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return null;
//		}
//		return fos;
//	}
}
