package iie.wxy.wifilocrecoder;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import android.R.integer;
import android.net.wifi.ScanResult;
import android.provider.CalendarContract.Instances;
import android.util.Log;

/**
 * @author wxy ���÷���ԭ���sdk�г�ȡwifi list��Ȼ��ʹ��
 */
public class Snake {

	public static List<ScanResult> getList(){
		return getList3();
//		List<ScanResult> ret1 = getList1();
//		List<ScanResult> ret2 = getList2();
//		List<ScanResult> ret3 = getList3();
//		List<ScanResult> ret4 = getList4();
//		List<ScanResult> ret2gk = getList2gk();
//		String string = "ret1.size"+ret1.size()+"" +
//				", ret2.size"+ret2.size()+
//				", ret4.size"+ret4.size()+"\n";
//		Save.getInstace().writeLine(ret2.toString());
//		Log.d("wxy", "list2 result = "+ret2.hashCode());
//		
//		if (ret2 != null) {
//			Log.d("wxy", "ret3 result = "+ret3.hashCode());
//			return ret3;
//		}else{
//			Log.d("wxy", "ret3 == null");
//			return null;
//		}
	}
		
	public static List<ScanResult> getList1() {
		try {
			Class localClass1 = Class.forName("com.baidu.location.h.e");
			Field localField = localClass1.getDeclaredField("ki");
			if (localField == null)
				return null;
			localField.setAccessible(true);
			Object object = localField.get(localClass1);
			if (object != null) {
				// ���ú���
				Method m1 = localClass1.getDeclaredMethod("dd");
				com.baidu.location.h.f obj_f = (com.baidu.location.h.f)m1.invoke(object);
				if (obj_f != null) {
					Class clzz_f = Class.forName("com.baidu.location.h.f");
					Field field_kq = clzz_f.getDeclaredField("kq");
					if (field_kq == null)
						return null;
					List<ScanResult> list = (List<ScanResult>) field_kq.get(obj_f);
					return list;
				}
			}
		} catch (ClassNotFoundException | NoSuchFieldException
				| IllegalAccessException | IllegalArgumentException
				| NoSuchMethodException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static List<ScanResult> getList2() {
		try {
			Class clzz_m = Class.forName("com.baidu.location.e.m");
//			�������fw
			Field field_fw = clzz_m.getDeclaredField("fW");
			if (field_fw == null)
				return null;
			field_fw.setAccessible(true);
			//��õ���ģʽ��m����m���ͼ̳���b����
			com.baidu.location.e.m obj_m = (com.baidu.location.e.m)field_fw.get(clzz_m);
			if (obj_m != null) {
//				��m�����л�ȡfR����
				Field field_type_f = clzz_m.getDeclaredField("f8");
				if (field_type_f == null)
					return null;
				field_type_f.setAccessible(true);
				com.baidu.location.h.f obj_f = (com.baidu.location.h.f)field_type_f.get(obj_m);
				if (obj_f != null) {
					Class clzz_f = Class.forName("com.baidu.location.h.f");
					Field field_kq = clzz_f.getDeclaredField("kq");
					if (field_kq == null)
						return null;
					List<ScanResult> list = (List<ScanResult>) field_kq.get(obj_f);
					return list;
				}
			}
		} catch (ClassNotFoundException | NoSuchFieldException
				| IllegalAccessException | IllegalArgumentException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static List<ScanResult> getList2gk() {
		try {
			Class clzz_m = Class.forName("com.baidu.location.e.m");
//			�������fw
			Field field_fw = clzz_m.getDeclaredField("fW");
			if (field_fw == null)
				return null;
			field_fw.setAccessible(true);
			//��õ���ģʽ��m����m���ͼ̳���b����
			com.baidu.location.e.m obj_m = (com.baidu.location.e.m)field_fw.get(clzz_m);
			if (obj_m != null) {
//				��m�����л�ȡfR����
				Field field_type_f = clzz_m.getDeclaredField("gk");
				if (field_type_f == null)
				{
					Log.d("wxy", "can not get attribute gk");
					return null;
				}
				field_type_f.setAccessible(true);
				com.baidu.location.h.f obj_f = (com.baidu.location.h.f)field_type_f.get(obj_m);
				if (obj_f != null) {
					Class clzz_f = Class.forName("com.baidu.location.h.f");
					Field field_kq = clzz_f.getDeclaredField("kq");
					if (field_kq == null)
						return null;
					List<ScanResult> list = (List<ScanResult>) field_kq.get(obj_f);
					return list;
				}
			}
		} catch (ClassNotFoundException | NoSuchFieldException
				| IllegalAccessException | IllegalArgumentException e) {
			e.printStackTrace();
			Log.e("wxy", "e:"+e.toString());
		}
		return null;
	}
	
	public static List<ScanResult> getList3() {
		try {
			Class clzz_m = Class.forName("com.baidu.location.e.m");
//			�������fw
			Field localField = clzz_m.getDeclaredField("fW");
			if (localField == null)
				return null;
			localField.setAccessible(true);
			//��õ���ģʽ��m����m���ͼ̳���b����
			com.baidu.location.e.b obj_m = (com.baidu.location.e.m)localField.get(clzz_m);
			if (obj_m != null) {
//				��m�����л�ȡfR����
				Class clzz_b = Class.forName("com.baidu.location.e.m").getSuperclass();
				Field field_fR = clzz_b.getDeclaredField("fR");
				field_fR.setAccessible(true);
				if (localField == null)
					return null;
				com.baidu.location.h.f obj_f = (com.baidu.location.h.f)field_fR.get(obj_m);
				if (obj_f != null) {
					Class clzz_f = Class.forName("com.baidu.location.h.f");
					Field field_kq = clzz_f.getDeclaredField("kq");
					if (field_kq == null)
						return null;
					List<ScanResult> list = (List<ScanResult>) field_kq.get(obj_f);
					return list;
				}
			}
		} catch (ClassNotFoundException | NoSuchFieldException
				| IllegalAccessException | IllegalArgumentException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static List<ScanResult> getList4() {
		try {
			Class clzz_m = Class.forName("com.baidu.location.e.j");
//			�������fw
			Field field_fw = clzz_m.getDeclaredField("hH");
			if (field_fw == null)
				return null;
			field_fw.setAccessible(true);
			//��õ���ģʽ��m����m���ͼ̳���b����
			Object obj_m = (Object)field_fw.get(clzz_m);
			if (obj_m != null) {
//				��m�����л�ȡfR����
				Field field_type_f = clzz_m.getDeclaredField("hF");
				if (field_type_f == null)
					return null;
				field_type_f.setAccessible(true);
				com.baidu.location.h.f obj_f = (com.baidu.location.h.f)field_type_f.get(obj_m);
				if (obj_f != null) {
					Class clzz_f = Class.forName("com.baidu.location.h.f");
					Field field_kq = clzz_f.getDeclaredField("kq");
					if (field_kq == null)
						return null;
					List<ScanResult> list = (List<ScanResult>) field_kq.get(obj_f);
					return list;
				}
			}
		} catch (ClassNotFoundException | NoSuchFieldException
				| IllegalAccessException | IllegalArgumentException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static List<ScanResult> getList5() {
		try {
			Log.d("wxy", ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			Class clzz_m = Class.forName("com.baidu.location.e.m");
			//ͨ������ģʽ���m����
			Method mthod_ba = clzz_m.getDeclaredMethod("ba");
			if (mthod_ba == null) {
				return null;
			}
			//��õ���ģʽ��m����m���ͼ̳���b����
			com.baidu.location.e.m obj_m = (com.baidu.location.e.m)mthod_ba.invoke(null);
			if (obj_m != null) {
//				��m�����л�ȡfR����
				Field field_type_f = clzz_m.getDeclaredField("gj");
				if (field_type_f == null)
					return null;
				field_type_f.setAccessible(true);
				String str = (String)field_type_f.get(obj_m);
				if (str == null) {
					Log.d("wxy", "gj = null");
				}else{
					Log.d("wxy", "gj = "+str);
				}
						
				field_type_f = clzz_m.getDeclaredField("f0");
				if (field_type_f == null)
					return null;
				field_type_f.setAccessible(true);
				str = (String)field_type_f.get(obj_m);
				if (str == null) {
					Log.d("wxy", "f0 = null");
				}else{
					Log.d("wxy", "f0 = "+str);
				}
				
				field_type_f = clzz_m.getDeclaredField("f2");
				if (field_type_f == null)
					return null;
				field_type_f.setAccessible(true);
				List list = (List)field_type_f.get(obj_m);
				if (list == null) {
					Log.d("wxy", "f2 = null");
				}else{
					Log.d("wxy", "f2 = "+str);
				}
				
				field_type_f = clzz_m.getDeclaredField("fT");
				Object obj_f = (Object)field_type_f.get(obj_m);
				if (obj_f != null) {
					Class userCla = (Class) obj_f.getClass().getSuperclass();
					Log.d("wxy", "obj_f.getSuperclass = "+userCla.toString());
					Field[] fs = userCla.getDeclaredFields(); 
					for (Field field : fs) {
						field.setAccessible(true); //����Щ�����ǿ��Է��ʵ�  
				        Object val = field.get(obj_f);//�õ������Ե�ֵ 
				        if (val == null) {
				        	Log.d("wxy", "name: "+field.getName()+", val == null");
						}else{
							Log.d("wxy", "name: "+field.getName()+", type = "+field.getType().toString()+", val = "+val.toString());
							if (field.getName().equals("c6")) {
								
					        	StringEntity entity = (StringEntity) field.get(obj_f);
					        	Log.d("wxy", "	entity type = "+entity.getContentType());
					        	Log.d("wxy", "	entity content = "+EntityUtils.toString(entity));
							}
						}
					}
//					Class clzz_f = Class.forName("com.baidu.location.h.f");
//					Field field_kq = clzz_f.getDeclaredField("kq");
//					if (field_kq == null)
//						return null;
//					List<ScanResult> list = (List<ScanResult>) field_kq.get(obj_f);
//					return list;
				}else{
					Log.d("wxy", "fT = null");
				}
			}
		} catch (ClassNotFoundException | NoSuchFieldException
				| IllegalAccessException | IllegalArgumentException | NoSuchMethodException | InvocationTargetException | IllegalStateException | IOException e) {
			e.printStackTrace();
		}
		Log.d("wxy", "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
		return null;
	}
}
