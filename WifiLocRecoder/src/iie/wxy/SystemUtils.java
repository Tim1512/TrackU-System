/**
 * 
 */
package iie.wxy;

import iie.wxy.wifilocrecoder.R;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author wxy
 * 
 */
public class SystemUtils {

	/**
	 * ��ȡϵͳ�ı�ʶ��
	 * 
	 * @param context
	 *            ���������Ļ���
	 * @return ����androidID
	 */
	public static String getAndroidID(Context context) {
		return ""
				+ android.provider.Settings.Secure.getString(
						context.getContentResolver(),
						android.provider.Settings.Secure.ANDROID_ID);

	}

	public static void saveConfig_UserName(Context context, String name) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				context.getString(R.string.CONFIG_FileName),
				Context.MODE_PRIVATE);
		SharedPreferences.Editor edit = sharedPreferences.edit(); // ����SharedPreferences.Editor����
		edit.putString(context.getString(R.string.CONFIG_UserName), name);
		edit.commit(); // �ύ�洢����
	}

	public static String readConfig_UserName(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				context.getString(R.string.CONFIG_FileName),
				Context.MODE_PRIVATE);
		return sharedPreferences.getString(
				context.getString(R.string.CONFIG_UserName),
				context.getString(R.string.DEFAULT_DogName));
	}

	/**
	 * �������ļ��б�������Ƿ����е�״̬
	 * 
	 * @param context
	 *            �����Ļ���
	 * @param isRunning
	 *            ��ǰ����״̬
	 */
	public static void saveConfig_IsRunning(Context context, boolean isRunning) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				context.getString(R.string.CONFIG_FileName),
				Context.MODE_PRIVATE);
		SharedPreferences.Editor edit = sharedPreferences.edit(); // ����SharedPreferences.Editor����
		edit.putBoolean(context.getString(R.string.CONFIG_IsRunning), isRunning);// �洢����
		edit.commit(); // �ύ�洢����
	}

	/**
	 * �������ļ��ж�ȡ�����Ƿ����е�״̬
	 * 
	 * @param context
	 *            �����Ļ���
	 * @return ����״̬
	 */
	public static boolean readConfig_IsRunning(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				context.getString(R.string.CONFIG_FileName),
				Context.MODE_PRIVATE);
		return sharedPreferences.getBoolean(
				context.getString(R.string.CONFIG_IsRunning), false);
	}

	/**
	 * 
	 */
	public SystemUtils() {
		// TODO Auto-generated constructor stub
	}

}
