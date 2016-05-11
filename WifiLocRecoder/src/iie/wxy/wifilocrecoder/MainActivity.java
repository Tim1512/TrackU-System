package iie.wxy.wifilocrecoder;

import android.support.v7.app.ActionBarActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

	WifiRecoder.Activitybinder myActivitybinder;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		final Button startBtn = (Button) findViewById(R.id.start_service);
		final Button stopBtn = (Button) findViewById(R.id.stop_service);
		
		startBtn.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.d("wxy", "on button start");
				Intent startIntent = new Intent(MainActivity.this, WifiRecoder.class);
				startService(startIntent);
				startBtn.setEnabled(false);
				stopBtn.setEnabled(true);
			}
		});
		stopBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.d("wxy", "on button stop");
				Intent stopIntent = new Intent(MainActivity.this, WifiRecoder.class);
				stopService(stopIntent);
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
	
	boolean isCoonWifiNetwork(){
		boolean flag = false;
		ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);  
        if (manager.getActiveNetworkInfo() != null) { 
        	if (manager.getActiveNetworkInfo().isAvailable()) {
        		State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();  
                if(wifi == State.CONNECTED || wifi == State.CONNECTING){  
                	flag = true;
                } 
			}
        }  
        return flag;
	}
}
