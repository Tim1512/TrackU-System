package iie.wxy.wifilocrecoder;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;

import android.R.array;
import android.R.integer;
import android.R.interpolator;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.os.Environment;
import android.util.Log;

public class Save {
	static final String SDCARD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
	static final String FILE_NAME = "LocationWifi.txt";
	static final String Wifi_FILE_NAME = "WifiConnection.txt";
	static final String Signal_FILE_NAME = "signal.signal";
	static final String DEAL_LOCATION_STRING = "deal_location.txt";
	static Save save;
	File file;
	public File wifiFile;
	public File signalFile;
	public File dealFile;
	public Save() {
		// TODO Auto-generated constructor stub
		file = new File(SDCARD_PATH,FILE_NAME);
		if (!file.exists()) {
			try {
				if(!file.createNewFile()){
					Log.d("wxy", "file already exit!");
				}			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}	
		wifiFile = new File(SDCARD_PATH,Wifi_FILE_NAME);
		if (!wifiFile.exists()) {
			try {
				if(!wifiFile.createNewFile()){
					Log.d("wxy", "file already exit!");
				}			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}	
		signalFile = new File(SDCARD_PATH,Signal_FILE_NAME);
		if (!signalFile.exists()) {
			try {
				if(!signalFile.createNewFile()){
					Log.d("wxy", "file already exit!");
				}			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
		
		dealFile = new File(SDCARD_PATH,DEAL_LOCATION_STRING);
		if (!dealFile.exists()) {
			try {
				if(!dealFile.createNewFile()){
					Log.d("wxy", "file already exit!");
				}			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}	
	}

	static Save getInstace(){
		if (save == null) {
			save = new Save();
		}
		return save;
	}
	boolean hasSDCard(){
		boolean sdCardExit = Environment.getExternalStorageState()
				.equalsIgnoreCase(android.os.Environment.MEDIA_MOUNTED);
		if (!sdCardExit) {
			Log.d("wxy", "sdcard does not exit!");
		}
		return sdCardExit;
	}
	public boolean writeFile(byte[] bytes){
		return writeFile(file, bytes);
	}
	boolean writeFile(File file, byte[] bytes){
//		Log.d("wxy", "write file to: "+file.getAbsolutePath());
		if(hasSDCard()){
			if(file.exists()){
				try {
					FileOutputStream fos = new FileOutputStream(file,true);
					fos.write(bytes);
					fos.close();
					return true;
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.d("wxy", "write file failed with FileNotFoundException!");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.d("wxy", "write file failed with IOException!");
				}
			}else {
				Log.d("wxy", "write file failed with file does not exists!");
			}
		}else {
			Log.d("wxy", "write file failed with hasSDCard!");
		}
		return false;
	}
	static byte[] readFile(File file){
		FileInputStream fis;
		byte[] b;
		try {
			fis = new FileInputStream(file);
			int len = fis.available();
			if(len <= 0) 
				return null;
			b = new byte[len];
			fis.read(b);
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return b;
	}
	
	void ParseText(PackageManager packageManager){

		InputStream instream;
		Map<String, Integer> hashMap = new HashMap<String, Integer>();
		int appCount = 0;
		try {
			instream = new FileInputStream(file);
			InputStreamReader inputreader = new InputStreamReader(instream);
			BufferedReader buffreader = new BufferedReader(inputreader);
			String line;
			
			while (( line = buffreader.readLine()) != null) {
				if (line.length()>0) {
					appCount++;
					String[] list = line.split(" ");
					for(int i=1;i<list.length;i++){
						String str = list[i];
						Integer count = hashMap.get(str);
						if (count == null) {
							hashMap.put(str, 1);
						}else{
							hashMap.put(str, count+1);
						}
					}
				}
			}
			instream.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(hashMap.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {   
		    public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {      
		        return (o2.getValue() - o1.getValue()); 
		        //return (o1.getKey()).toString().compareTo(o2.getKey());
		    }
		});

		File result  = new File(SDCARD_PATH,"result.txt");
		if (!result.exists()) {
			try {
				if(!result.createNewFile()){
					Log.d("wxy", "file already exit!");
				}			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
		writeFile(result,(new String("total application count = "+appCount+" permission count="+list.size()+"\n")).getBytes());
		for (int i = 0; i < list.size(); i++) {
			Map.Entry entry = list.get(i);
	        String key = (String)entry.getKey();    
	        Integer value = (Integer)entry.getValue();
	        writeFile(result,(new String(key+":"+((double)value/appCount*100)+"%"+"\n")).getBytes());
		}
		for (int i = 0; i < list.size(); i++) {
			Map.Entry entry = list.get(i);
	        String key = (String)entry.getKey();    
	        Integer value = (Integer)entry.getValue();
	        writeFile(result,(new String("<uses-permission android:name=\""+key+"\"></uses-permission>\n")).getBytes());
		}
		int count_null =0;
		int count_normal =0;
		int count_dangerous =0;
		int count_signature =0;
		int count_sors =0;
		for (int i = 0; i < list.size(); i++) {
			Map.Entry entry = list.get(i);
	        String key = (String)entry.getKey();    
	        Integer value = (Integer)entry.getValue();
	        
	        //通过usesPermissionName获取该权限的详细信息
	        PermissionInfo permissionInfo;
	        String groupString = null;
	        String labelString = null;
	        String levelString = null;int level=0;
			try {
				permissionInfo = packageManager.getPermissionInfo(key, 0);
		        //获得该权限属于哪个权限组,如:网络通信
		        PermissionGroupInfo permissionGroupInfo = packageManager.getPermissionGroupInfo(permissionInfo.group, 0);
		        groupString = permissionGroupInfo.loadLabel(packageManager).toString();
		        labelString = permissionInfo.loadLabel(packageManager).toString();	
		        level = permissionInfo.protectionLevel;
		        
		        
		        //levelString = protectionToString(permissionInfo.protectionLevel);
		        switch (level&PROTECTION_MASK_BASE) {
	            case PermissionInfo.PROTECTION_DANGEROUS:
	            	levelString = "dangerous";count_dangerous++;
	                break;
	            case PermissionInfo.PROTECTION_NORMAL:
	            	levelString = "normal";count_normal++;
	                break;
	            case PermissionInfo.PROTECTION_SIGNATURE:
	            	levelString = "signature";count_signature++;
	                break;
	            case PermissionInfo.PROTECTION_SIGNATURE_OR_SYSTEM:
	            	levelString = "signatureOrSystem";count_sors++;
	                break;
		        }
		        if ((level&PermissionInfo.PROTECTION_FLAG_SYSTEM) != 0) {
		        	levelString += "|system";
		        }
		        if ((level&PermissionInfo.PROTECTION_FLAG_DEVELOPMENT) != 0) {
		        	levelString += "|development";
		        }
			        
		        
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				count_null++;
				writeFile(result,(new String("error in NameNotFoundException :"+key+"\n\n")).getBytes());
				continue;
			}
			
	        
			
			writeFile(result,(new String(key+" "+groupString+" "+labelString+" "+levelString+"\n")).getBytes());

			
		}
		writeFile(result,(new String("dangerous:"+count_dangerous+" normal:"+count_normal+" signature:"+count_signature+" signatureOrSystem:"+count_sors+"\n")).getBytes());
		writeFile(result,(new String("totally "+count_null+"permission cannot found\n")).getBytes());
	}
	
	public static final int PROTECTION_MASK_BASE = 0xf;


}
