package iie.wxy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileUtils {

	static public interface FileScanCB {
		public void ScanCallBack(File file);
	}
	
	public FileUtils() {
		// TODO Auto-generated constructor stub
	}
	
	static public void readFile(String dirPath, String name){
		readFile(new File(dirPath, name));
	}
	
	static public void readFile(String fileName){
		readFile(new File(fileName));
	}
	
	static public byte[] readFile(File file){
		byte[] b;
		try(FileInputStream fis  = new FileInputStream(file);){
			int len = fis.available();
			if(len <= 0) 
				return null;
			b = new byte[len];
			fis.read(b);
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return b;
	}
	
	static int dirCount =0;
	static public void scanFileDirectory(String name, FileScanCB fileCB){
		scanFileDirectory(new File(name), fileCB);
	}
	
	static public void scanFileDirectory(File file, FileScanCB fileCB){
		if(file.isDirectory()){
			File[] list = file.listFiles();
			if(list == null){// maybe a link
				if(!file.isFile())
				{
					return ;					
				}
				else {
					return ;
				}

			}
			dirCount++;
			for (int i = 0; i < list.length; i++) {
					scanFileDirectory(list[i], fileCB);
				}				
			dirCount--;
		}
		else{
			fileCB.ScanCallBack(file);
		}
	}
	
	static public void printFile(String name, int dir, boolean isDir){
		StringBuffer buffer = new StringBuffer();
		if(isDir)
			buffer.append("+");
		else {
			buffer.append("|");
		}
		for (int i = 0; i < dir; i++) {
			buffer.append("    ");
		}
		buffer.append(name+"\n");
	}
	
	static public boolean writeFile(File file, byte[] bytes){
		if(file.exists()){
			try(FileOutputStream fos = new FileOutputStream(file);){
				fos.write(bytes);
				fos.close();
				return true;
			}catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	static public File createFile(String path, String name){
		File file;
		if(path.endsWith("/")){
			file = new File(path+name);
		}else{
			file = new File(path+"/"+name);
		}
		try {
			if(!file.createNewFile()){
			}
			return file;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	static public boolean createDirectory(String path){
		File file;
		
		file = new File(path);
		return true;
	}
	
	/**
	 * 
	* @ClassName: ReadLineCB 
	* @Description: 读取文件中的每一行的回调函数接口
	* @author  wxy
	* @date 2016-4-7 下午8:28:22 
	*
	 */
	public interface ReadLineCB{
		public void readLineCallBack(String line);
	}
	/**
	 * 
	* @title: ReadFileLine 
	* @description: 逐行读取文件中的内容，读到一行后调用回调函数。读取完后关闭文件
	* @param name
	* @param readCB void
	* @throws
	 */
	public static void readEachLine(String name, ReadLineCB readCB){
		String line;
//		FileInputStream fis = null;
		try(FileInputStream fis = new FileInputStream(new File(name));){
			BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
			while((line=reader.readLine()) != null){
				if (line.length() > 0) {
					readCB.readLineCallBack(line);
				}
			}
		}catch (IOException e) {
			e.printStackTrace();
 		}
	}
	
	/**
	 * 
	* @title: createFileOutputStream 
	* @description: 根据输入名创建一个输出流。记得使用完后关闭流。
	* @param name
	* @return FileOutputStream
	* @throws
	 */
	public static FileOutputStream createFileOutputStream(String name){
		return createFileOutputStream(name, false);
	}
	static FileOutputStream createFileOutputStream(String name, boolean isAppend){
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(new File(name),isAppend);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		return fos;
	}
}
