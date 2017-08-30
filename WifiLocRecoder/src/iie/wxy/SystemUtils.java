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
	 * 获取系统的标识码
	 * 
	 * @param context
	 *            输入上下文环境
	 * @return 返回androidID
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
		SharedPreferences.Editor edit = sharedPreferences.edit(); // 创建SharedPreferences.Editor对象
		edit.putString(context.getString(R.string.CONFIG_UserName), name);
		edit.commit(); // 提交存储数据
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
	 * 在配置文件中保存服务是否运行的状态
	 * 
	 * @param context
	 *            上下文环境
	 * @param isRunning
	 *            当前运行状态
	 */
	public static void saveConfig_IsRunning(Context context, boolean isRunning) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				context.getString(R.string.CONFIG_FileName),
				Context.MODE_PRIVATE);
		SharedPreferences.Editor edit = sharedPreferences.edit(); // 创建SharedPreferences.Editor对象
		edit.putBoolean(context.getString(R.string.CONFIG_IsRunning), isRunning);// 存储数据
		edit.commit(); // 提交存储数据
	}

	/**
	 * 在配置文件中读取服务是否运行的状态
	 * 
	 * @param context
	 *            上下文环境
	 * @return 运行状态
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
