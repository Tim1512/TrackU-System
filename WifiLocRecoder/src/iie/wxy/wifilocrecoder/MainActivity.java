package iie.wxy.wifilocrecoder;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import iie.wxy.AccessPoint;
import iie.wxy.SystemUtils;

import android.support.v7.app.ActionBarActivity;
import android.R.integer;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

	WifiRecoder.Activitybinder myActivitybinder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onResume()
	 */
	@Override
	protected void onResume() {
		initUIEvent();
		Log.d("wxy", "here in MainActivity.onResume");
		super.onResume();
	}

	private void initUIEvent() {
		final Button startBtn = (Button) findViewById(R.id.start_service);
		final Button stopBtn = (Button) findViewById(R.id.stop_service);
		final EditText editorEditText = (EditText) findViewById(R.id.txt_Edittext);
		assert (startBtn != null);
		assert (stopBtn != null);
		assert (editorEditText != null);
		String nameString = SystemUtils.readConfig_UserName(this
				.getApplicationContext());
		if (!nameString.equals(getString(R.string.DEFAULT_DogName))) {
			editorEditText.setText(nameString);
		}

		// 如果检测到service在运行中，则设置按钮状态，不要重新创建。
		boolean isServiceRunning = WifiRecoder.isServiceRunning(
				getApplicationContext(), getString(R.string.PackageName));
		Log.d("wxy", "isServiceRunning = " + isServiceRunning);
		if (isServiceRunning) {
			editorEditText.setEnabled(false);
			startBtn.setEnabled(false);
			stopBtn.setEnabled(true);
		} else {
			editorEditText.setEnabled(true);
			startBtn.setEnabled(true);
			stopBtn.setEnabled(false);
		}

		startBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d("wxy", "on button start");
				String nameString = editorEditText.getText().toString();
				if (nameString.length() == 0) {
					Toast.makeText(MainActivity.this,
							getString(R.string.Warning_Empty), Toast.LENGTH_SHORT)
							.show();
					return;
				}
				if (!checkText(nameString)) {
					Toast.makeText(MainActivity.this,
							getString(R.string.Warning_OnlyLetters), Toast.LENGTH_SHORT)
							.show();
					return;
				}
				Intent startIntent = new Intent(MainActivity.this,
						WifiRecoder.class);
				startIntent.putExtra(getString(R.string.INTENT_Key_Name),
						nameString);
				startService(startIntent);
				editorEditText.setEnabled(false);
				startBtn.setEnabled(false);
				stopBtn.setEnabled(true);
			}
		});

		stopBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d("wxy", "on button stop");
				Intent stopIntent = new Intent(MainActivity.this,
						WifiRecoder.class);
				stopService(stopIntent);
				editorEditText.setEnabled(true);
				startBtn.setEnabled(true);
				stopBtn.setEnabled(false);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		Log.d("wxy", "MainActivity onDestory");
		super.onDestroy();
	}
	


	boolean isCoonWifiNetwork() {
		boolean flag = false;
		ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if (manager.getActiveNetworkInfo() != null) {
			if (manager.getActiveNetworkInfo().isAvailable()) {
				State wifi = manager.getNetworkInfo(
						ConnectivityManager.TYPE_WIFI).getState();
				if (wifi == State.CONNECTED || wifi == State.CONNECTING) {
					flag = true;
				}
			}
		}
		return flag;
	}
	
	private boolean checkText(String str){
		final String strRegex = "^[0-9a-zA-Z _-]+$";//[\\d\\s-]+  ^[A-Za-z0-9\\-]+$
		Matcher m=Pattern.compile(strRegex).matcher(str);
//		boolean f = m.find();
//		Log.d("wxy", "checkText = "+f);
		return m.find();
	}

}
