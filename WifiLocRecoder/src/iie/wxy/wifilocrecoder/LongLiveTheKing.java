/**
 * 
 */
package iie.wxy.wifilocrecoder;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.app.Fragment;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

/**
 * @author wxy
 *
 */
public class LongLiveTheKing extends Activity {

	private static final String TAG = "wxy";
	public static WeakReference<LongLiveTheKing> instance;
	/* (non-Javadoc)
	 * @see android.app.Activity#dispatchTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		Log.d(TAG, "LongLiveTheKing.dispatchTouchEvent()");
//		finishSelf();
//		return super.dispatchTouchEvent(ev);
		return false;
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.screen_filter);
        instance = new WeakReference<>(this);
        Window window = getWindow();
        window.setGravity(Gravity.TOP | Gravity.LEFT);
        WindowManager.LayoutParams attributes = window.getAttributes();
//        attributes.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | 
//        		WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
//        attributes.flags =WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
//                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
//              | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
//              | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
//              | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
//              | WindowManager.LayoutParams.FLAG_FULLSCREEN
//              | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        attributes.x = 0;
        attributes.y = 0;
        attributes.height = 1;
        attributes.width = 1;
        window.setAttributes(attributes);
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        setVisible(false);
        setFinishOnTouchOutside(true);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		if (instance != null && instance.get() == this) {
            instance = null;
        }
		super.onDestroy();
	}




	/* (non-Javadoc)
	 * @see android.app.Activity#onTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.d(TAG, "LongLiveTheKing.onTouchEvent()");
//		finishSelf();
//		return super.onTouchEvent(event);
		return false;
	}
	
	public void finishSelf() {
		Log.d(TAG, "LongLiveTheKing.finishSelf():"+this.hasWindowFocus()+", isTaskRoot:"+this.isTaskRoot());
//		View view = this.getRootView(this);
//		if (view != null && view.isFocusable() && view.isFocusableInTouchMode() ) {
//			boolean ret = view.requestFocus();
//			Log.d(TAG, "LongLiveTheKing.finishSelf():requestFocus"+ret);
//		}
		finish();
    }

	/* (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		Log.d(TAG, "LongLiveTheKing.onBackPressed()");		
		super.onBackPressed();
	}
	
	private static View getRootView(Activity context)  
    {  
        return ((ViewGroup)context.findViewById(android.R.id.content)).getChildAt(0);  
    }  
}
